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

public class mmRobot extends AdvancedRobot 
{
	private double winningRound;
	private double losingRound;

	public static final double PI = Math.PI;
	private Enemy enemy;
	private static LUQTable qtable = null;
	private static QLearning learner = null; 
	private double firePower; 
	private int direction = 1; 
	private int isHitWall = 0; 
	private int isHitByBullet = 0; 
	 
	double accumuReward=0.0;
	double currentReward=0.0;
	
	private static final double rewardForWin = 10;
	private static final double rewardForDeath = -10; 
	
	private static final double rewardForHitRobot = 0; 
	
	private static final double rewardForBulletHit = 5;
	private static final double rewardForHitByBullet = -5; 
 
	private static final double rewardForHitWall = -3; 
	
	FileWriter fw;
	
	public void run() 
	{
		
		qtable = new LUQTable(); 
		loadData();
		learner = new QLearning(qtable); 
		
		enemy = new Enemy(); 
	    enemy.distance = 100000; 
	 
	    setColors(Color.green, Color.white, Color.red); 
	    setAdjustGunForRobotTurn(true); 
	    setAdjustRadarForGunTurn(true); 
	    turnRadarRightRadians(2 * PI); 
	    int countRound=0;
	    
	    while (true) 
	    { 
	      countRound++;
	      
	      if(countRound>20000000)
	      {
	    	  out.println("Before explorationRate"+learner.ExplorationRate);
	    	  learner.setExploitationRate(0);
	    	  out.println("After explorationRate"+learner.ExplorationRate);
	      }
	      
	      radarMovement(); 
	      aimAndFire();
	      performLearning(); 
	      execute(); 
	    } 
	  } 
	
	  private void performLearning() 
	  { 
	    int state = getState(); 
	    int action = learner.selectAction(state); 
	    out.println("RobotAction selected: " + action); 
	    learner.learn(state, action, currentReward); 
	    accumuReward+=currentReward;
	    currentReward = 0.0; 
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
	      case RobotAction.TurnLeftAhead: 
	        setTurnLeft(RobotAction.RobotTurnDegree); 
	        setAhead(RobotAction.RobotMoveDistance); 
	        break; 
	      case RobotAction.TurnRightAhead: 
	        setTurnRight(RobotAction.RobotTurnDegree);
	        setAhead(RobotAction.RobotMoveDistance); 
	        break; 
	     // case RobotAction.Fire:
	    //	this.aimAndFire();
	    //	break;
	    } 
	  } 
	 
	  private int getState() 
	  { 
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
	    if (getTime() - enemy.ctime > 4) { 
	      radarOffset = 4*PI;				
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
	 
	  private void aimAndFire() 
	  { 
		 firePower = 400/enemy.distance; 
	     if (firePower > 3) 
	        firePower = 3; 
		  
	    long time; 
	    long nextTime; 
	    Point2D.Double p; 
	    p = new Point2D.Double(enemy.x, enemy.y); 
	    for (int i = 0; i < 20; i++) 
	    { 
	      nextTime = (int)Math.round((getRange(getX(),getY(),p.x,p.y)/(20-(3*firePower)))); 
	      time = getTime() + nextTime - 10; 
	      p = enemy.guessPosition(time); 
	    } 
	    double gunOffset = getGunHeadingRadians() - (Math.PI/2 - Math.atan2(p.y - getY(),p.x -  getX())); 
	    setTurnGunLeftRadians(NormaliseBearing(gunOffset)); 
	    
	    if (getGunHeat() == 0) 
	      {
	        setFire(firePower); 
	      } 
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
	 
	  public void onBulletHit(BulletHitEvent e) 
	  { 
	    if (enemy.name == e.getName()) 
	    { 
	      currentReward=rewardForBulletHit;
	    } 
	  } 

	  public void onHitByBullet(HitByBulletEvent e) 
	  { 
	    if (enemy.name == e.getName()) 
	    { 
	      currentReward=rewardForHitByBullet;
	    } 
	    isHitByBullet = 1; 
	  } 
	 
	  public void onHitRobot(HitRobotEvent e) 
	  { 
	    if (enemy.name == e.getName()) 
	    { 
	      currentReward= rewardForHitRobot; 
	    } 
	  } 
	 
	  public void onHitWall(HitWallEvent e) 
	  { 
		currentReward=rewardForHitWall;
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
	      double h = NormaliseBearing(e.getHeadingRadians() - enemy.head); 
	      h = h/(getTime() - enemy.ctime); 
	      enemy.changehead = h; 
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
	  
	  /*
		public void saveQTable()
		{
			try 
			{
				if(fw==null)
				{ 
					fw = new FileWriter(new File("/home/lili/workspace/EECE592/ReinforcementLearning/src/ReinforcementLearning/myQtable.txt"));
				}
		
				for(int i=0; i<RobotState.numStates; i++)
				{
					for(int j=0; j<RobotAction.numRobotActions; j++)
					{
						fw.write("state:  "+i+"  action:   "+j+"  Qvalue   "+Double.toString(qtable.getQValue(i,j)));
						fw.write("\r\n");
					}
				}
				fw.close();
			 }
			catch (IOException ex) 
			{
				
	            ex.printStackTrace();

	        }
	    }*/
		
	 
	  public void onRobotDeath(RobotDeathEvent e) 
	  {
	 
	    if (e.getName() == enemy.name) 
	      enemy.distance = 10000; 
	  }   

	  public void onWin(WinEvent event) 
	  { 
		 winningRound++; 
		 currentReward=rewardForWin;
		 //saveQTable();
		saveData();
		 
		 //int state=RobotState.mapping[0][0][0][0][0];
		 
		 //int action =2;
		// learner.learn(state, action, currentReward);

		 PrintStream w = null; 
		    try 
		    { 
		      w = new PrintStream(new RobocodeFileOutputStream("/home/lili/workspace/EECE592/ReinforcementLearning/src/ReinforcementLearning/survival.xlsx", true)); 
		      w.println(accumuReward+" "+getRoundNum()+"\t"+winningRound+" "+1); 
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
		// int state=RobotState.mapping[0][0][0][0][0];
		 //int action =2;
	     losingRound++;
	     currentReward=rewardForDeath;
	     //saveQTable();
	     saveData();
	    // learner.learn(state, action, currentReward);
	     PrintStream w = null; 
		    try 
		    { 
		      w = new PrintStream(new RobocodeFileOutputStream("/home/lili/workspace/EECE592/ReinforcementLearning/src/ReinforcementLearning/survival.xlsx", true)); 
		      w.println(accumuReward+" "+getRoundNum()+"\t"+losingRound+" "+0); 
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
	  
	  public void loadData()   
	  {   
	    try   
	    {   
	      qtable.loadData(getDataFile("LUTable.dat"));   
	    }   
	    catch (Exception e)   
	    {   
	    }   
	  }   
	   
	  public void saveData()   
	  {   
	    try   
	    {   
	      qtable.saveData(getDataFile("LUTable.dat"));   
	    }   
	    catch (Exception e)   
	    {   
	      out.println("Exception trying to write: " + e);   
	    }   
	  }   
	  
	  
}
