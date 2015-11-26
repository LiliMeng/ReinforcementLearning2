package ReinforcementLearning;

import java.awt.*; 
import java.awt.geom.*; 
import java.io.*;

import ReinforcementLearning.RobotAction;
import ReinforcementLearning.QLearning;
import ReinforcementLearning.LUQTable;
import ReinforcementLearning.RobotState;
import ReinforcementLearning.Enemy;

import robocode.*; 
 
import robocode.AdvancedRobot; 
import robocode.util.Utils;

public class mmRobot extends AdvancedRobot 
{
	private double winningRound=0;
	private double losingRound=0;
	public static final double PI = Math.PI;
	private Enemy enemy = new Enemy();;
	private LUQTable qtable = new LUQTable();
	private QLearning learner = new QLearning(qtable);  
	
    private double reward = 0.0; 
	private double firePower; 
	private int moveDirection = 1; 
	private int isHitWall = 0; 
	private int isHitByBullet = 0; 
	boolean inWall=false;
	boolean movingForward;
	
	private static final double rewardForWin = 10;
	private static final double rewardForDeath = -10; 
	
	private static final double rewardForHitEnemy = -5; 
	
	private static final double rewardForBulletHit = 20;
	private static final double rewardForBulletMissed = -5;
	private static final double rewardForHitByBullet = -10; 
 
	private static final double rewardForHitWall = -5; 
	private static final double rewardForCloseWall = -5;
	
    private double accumuReward;
	 
	public void run() 
	{ 
	    setColors(Color.green, Color.white, Color.red); 
	    setAdjustGunForRobotTurn(true); 
	    setAdjustRadarForGunTurn(true); 
	    turnRadarRightRadians(2 * PI); 
	    
	 // Check if the robot is closer than 50px from the wall.
	 	if (getX() <= 50 || getY() <= 50 || getBattleFieldWidth() - getX() <= 50 || getBattleFieldHeight() - getY() <= 50)
	 	{
	 		inWall = true;
	 	} 
	 	else 
	 	{
			learner = new QLearning(qtable); 
	 		inWall = false;
	 	}
	  
	    while (true) 
	    { 
	    	if (getX() > 50 && getY() > 50 && getBattleFieldWidth() - getX() > 50 && getBattleFieldHeight() - getY() > 50 && inWall == true) 
	    	{
				inWall = false;
			}
			if (getX() <= 50 || getY() <= 50 || getBattleFieldWidth() - getX() <= 50 || getBattleFieldHeight() - getY() <= 50 ) 
			{
				reward=rewardForCloseWall;
				if ( inWall == false)
				{
					reverseDirection();
					inWall = true;
					learner = new QLearning(qtable); 
				}
			}
			
	      //doMovement();
	      firePower = 400/enemy.distance; 
	      if (firePower > 3) 
	        firePower = 3; 
	      radarMovement();
	      gunMovement(); 
	      if (getGunHeat() == 0) { 
	        setFire(firePower); 
	      } 
	      performLearning();
	      execute();      
	    } 
	  } 
	 
	  void doMovement() 
	  { 
	    if (getTime()%20 == 0) 
	    { 
	      moveDirection *= -1;		//reverse direction 
	      setAhead(moveDirection*300);	//move in that Direction
	    } 
	    setTurnRightRadians(enemy.bearing + (PI/2)- ((PI/6) * moveDirection));
	  } 
	 
	  private void performLearning() {
    
	    int state = getState(); 
	    int action = learner.selectAction(state,getTime()); 
	    out.println("RobotAction selected: " + action); 
	    learner.learn(state, action, reward); 
	    accumuReward += reward;
	    out.println("AccumulateReward: " + accumuReward); 
	    reward = 0.0; 
	    isHitWall = 0; 
	    isHitByBullet = 0; 
	 
	    switch (action) 
	    { 
	      case RobotAction.Ahead: 
	        setAhead(RobotAction.RobotMoveDistance); 
	        break; 
	      case RobotAction.Back: 
	        setBack(RobotAction.RobotMoveDistance); 
	        break; 
	      case RobotAction.TurnLeft: 
	        setTurnLeft(RobotAction.RobotTurnDegree); 
	        break; 
	      case RobotAction.TurnRight: 
	    	 setTurnRight(RobotAction.RobotTurnDegree);
	        break; 
	    } 
	    
	  } 
	
	  
	  private int getState() 
	  { 
		//int x = RobotState.getXlevel(getX());
		//int y = RobotState.getYlevel(getY());
	    int heading = RobotState.getHeading(getHeading()); 
	    int enemyDistance = RobotState.getEnemyDistance(enemy.distance); 
	    int enemyBearing = RobotState.getEnemyBearing(enemy.bearing); 
	    out.println("State(" + heading + ", " + enemyDistance + ", " + enemyBearing + ", " + isHitWall + ", " + isHitByBullet + ")"); 
	    int state = RobotState.mapping[heading][enemyDistance][enemyBearing][isHitWall][isHitByBullet]; 
	    return state;
	  } 
	 
	  private void radarMovement() 
	  { 
	    double radarOffset; 
	    if (getTime() - enemy.ctime > 4) { //if we haven't seen anybody for a bit.... 
	      radarOffset = 4*PI;				//rotate the radar to find a enemy 
	    } else { 
	 
	      radarOffset = getRadarHeadingRadians() - (Math.PI/2 - Math.atan2(enemy.y - getY(),enemy.x - getX())); 
	      radarOffset = NormaliseBearing(radarOffset); 
	      if (radarOffset < 0) 
	        radarOffset -= PI/10; 
	      else 
	        radarOffset += PI/10; 
	    } 
	    setTurnRadarLeftRadians(radarOffset); 
	  } 
	  
	  private void gunMovement() 
	  { 
	    long time; 
	    long nextTime; 
	    Point2D.Double p; 
	    p = new Point2D.Double(enemy.x, enemy.y); 
	    for (int i = 0; i < 30; i++) 
	    { 
	      nextTime = (int)Math.round((getRange(getX(),getY(),p.x,p.y)/(20-(3*firePower)))); 
	      time = getTime() + nextTime - 10; 
	      p = enemy.guessPosition(time); 
	    } 
	    double gunOffset = getGunHeadingRadians() - (Math.PI/2 - Math.atan2(p.y - getY(),p.x -  getX())); 
	    setTurnGunLeftRadians(NormaliseBearing(gunOffset)); 
	  } 
	 
	  double NormaliseBearing(double ang) { 
	    if (ang > PI) 
	      ang -= 2*PI; 
	    if (ang< -PI) 
	      ang += 2*PI; 
	    return ang; 
	  } 
	 
	  double NormaliseHeading(double ang) { 
	    if (ang > 2*PI) 
	      ang -= 2*PI; 
	    if (ang < 0) 
	      ang += 2*PI; 
	    return ang;  
	  } 
	 
	  public double getRange( double x1,double y1, double x2,double y2 ) 
	  { 
	    return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
	  } 
	 
	  public double absbearing( double x1,double y1, double x2,double y2 ) 
	  { 
	    double xo = x2-x1; 
	    double yo = y2-y1; 
	    double h = getRange( x1,y1, x2,y2 ); 
	    if( xo > 0 && yo > 0 ) 
	    { 
	      return Math.asin( xo / h ); 
	    } 
	    if( xo > 0 && yo < 0 ) 
	    { 
	      return Math.PI - Math.asin( xo / h ); 
	    } 
	    if( xo < 0 && yo< 0 ) 
	    { 
	      return Math.PI + Math.asin( -xo / h ); 
	    } 
	    if( xo < 0 && yo > 0 ) 
	    { 
	      return 2.0*Math.PI - Math.asin( -xo / h ); 
	    } 
	    return 0; 
	  } 
	 
	  /**
		 * reverseDirection:  Switch from ahead to back & vice versa
		 */
		public void reverseDirection() {
			if (movingForward)
			{
				setBack(4000);
				movingForward = false;
			} else 
			{
				setAhead(4000);
				movingForward = true;
			}
		}
	 
	  public void onBulletHit(BulletHitEvent e) 
	  { 
	    if (enemy.name == e.getName()) 
	    {
	      reward =rewardForBulletHit; 
	    } 
	  } 
	  
	  public void onBulletMissed(BulletMissedEvent e) 
	  { 
	    reward =rewardForBulletMissed; 
	  } 
	 
	  public void onHitByBullet(HitByBulletEvent e) 
	  { 
	    if (enemy.name == e.getName()) 
	    { 
	      reward = rewardForHitByBullet; 
	    } 
	    isHitByBullet = 1; 
	  } 
	 
	  public void onHitRobot(HitRobotEvent e) 
	  { 
		
	    if (enemy.name == e.getName()) 
	    { 
	      reward = rewardForHitEnemy; 
	    } 
	  } 
	 
	  public void onHitWall(HitWallEvent e) 
	  { 
	    reward = rewardForHitWall; 
	    isHitWall = 1; 
	  } 
	 
	  public void onScannedRobot(ScannedRobotEvent e) 
	  { 
	    if ((e.getDistance() < enemy.distance)||(enemy.name == e.getName())) 
	    { 
	      //the next line gets the absolute bearing to the point where the bot is 
	      double absbearing_rad = (getHeadingRadians()+e.getBearingRadians())%(2*PI); 
	      //this section sets all the information about our enemy 
	      enemy.name = e.getName(); 
	      enemy.x = getX()+Math.sin(absbearing_rad)*e.getDistance(); //works out the x coordinate of where the enemy is 
	      enemy.y = getY()+Math.cos(absbearing_rad)*e.getDistance(); //works out the y coordinate of where the enemy is 
	      enemy.bearing = e.getBearingRadians(); 
	      enemy.head = e.getHeadingRadians(); 
	      enemy.ctime = getTime();				//game time at which this scan was produced 
	      enemy.speed = e.getVelocity(); 
	      enemy.distance = e.getDistance(); 
	      enemy.energy = e.getEnergy(); 
	    } 
	  } 
	 
	  public void onRobotDeath(RobotDeathEvent e) 
	  { 
	 
	    if (e.getName() == enemy.name) 
	      enemy.distance = 10000; 
	  }   

	  public void onWin(WinEvent event) 
	  { 
		 reward = reward+rewardForWin;
		 winningRound++; 
		 
		// int state=RobotState.mapping[2][0][0][0];
		 //int action=3;
		 PrintStream w = null; 
		    try 
		    { 
		      w = new PrintStream(new RobocodeFileOutputStream("/home/lili/workspace/EECE592/ReinforcementLearning/src/ReinforcementLearning/survival.xlsx", true)); 
		      //w.println(qtable.getQValue(state, action)); 
		      w.println(accumuReward);
		      if (w.checkError()) 
		        System.out.println("Could not save the data!"); 
		      w.close(); 
		    } 
		    catch (IOException e) 
		    { 
		      System.out.println("IOException trying to write: " + e); 
		    } 
		    finally 
		    { 
		      try 
		      { 
		        if (w != null) 
		          w.close(); 
		      } 
		      catch (Exception e) 
		      { 
		        System.out.println("Exception trying to close witer: " + e); 
		      } 
		    } 
		  }  

	  public void onDeath(DeathEvent event) 
	  { 
	     losingRound++; 
	     reward = reward+rewardForDeath;
	     
	    // int state=RobotState.mapping[2][0][0][0][0];
		 //int action=3;
	     
	     PrintStream w = null; 
		    try 
		    { 
		      w = new PrintStream(new RobocodeFileOutputStream("/home/lili/workspace/EECE592/ReinforcementLearning/src/ReinforcementLearning/survival.xlsx", true)); 
		     // w.println(qtable.getQValue(state, action)); 
		      w.println(accumuReward);
		      if (w.checkError()) 
		        System.out.println("Could not save the data!"); 
		      w.close(); 
		    } 
		    catch (IOException e) 
		    { 
		      System.out.println("IOException trying to write: " + e); 
		    } 
		    finally 
		    { 
		      try 
		      { 
		        if (w != null) 
		          w.close(); 
		      } 
		      catch (Exception e) 
		      { 
		        System.out.println("Exception trying to close witer: " + e); 
		      } 
		    } 
	  } 
}
