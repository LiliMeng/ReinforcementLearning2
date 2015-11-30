package ReinforcementLearning;

public class RobotState
{
	public static final int numHeading = 4;
	public static final int numEnemyDistance = 8;
	public static final int numEnemyBearing = 4;
	public static final int numHitWall = 2;
	public static final int numHitByBullet = 2;
	public static final int numStates;
	public static final int mapping[][];//policy mapping 
	
	static
	{
		mapping = new int[numHeading][numEnemyDistance];//[numEnemyBearing][numHitWall][numHitByBullet];
		int count = 0;
		for(int i = 0; i< numHeading; i++)
		{
			for(int j =0; j<numEnemyDistance; j++)
			{
				//for(int k=0; k<numEnemyBearing; k++)
				//{
					//for(int m=0; m<numHitWall; m++)
					//{
						//for(int n=0; n<numHitByBullet; n++)
						//{
							mapping[i][j] = count ++;
						//}
					//}
				//}
			}
		}
		numStates = count;
	}
	
	public static int getHeading(double heading)
	{
		double angle = 360 / numHeading;
		double newHeading = heading + angle/2;
		if(newHeading >360)
			newHeading -=360.0;
		return (int) (newHeading/angle);
	}
	
	public static int getEnemyDistance(double value)
	{
		double oneDistanceLevel=1000/numEnemyDistance;
		int distance = (int) (value /oneDistanceLevel);
		if(distance > numEnemyDistance -1)
			distance = numEnemyDistance -1;
		return distance;
	}
	
	public static int getEnemyBearing(double bearing)
	{
		double circleAngle = Math.PI * 2;
		if(bearing < 0)
		{
			bearing = circleAngle + bearing;
		}
		double angle = circleAngle / numEnemyBearing;
		double newBearing = bearing + angle/2;
		if(newBearing > circleAngle)
		{
			newBearing -= circleAngle;
		}
		return (int)(newBearing/angle);
		
	}
}
