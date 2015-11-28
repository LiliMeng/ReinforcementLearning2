package ReinforcementLearning;

public class RobotAction
{
	/*public static final int Ahead = 0;
	public static final int Back = 1;
	public static final int TurnLeftAhead = 2;
	public static final int TurnRightAhead = 3;
	//public static final int Fire = 4;
	*/
	public static final int AntiGravityMove=0;
	public static final int GravityMove=1;
	public static final int AimAndFire= 2;
	
	public static final int numRobotActions = 3;
	
	public static final double RobotMoveDistance = 300.0;
	public static final double RobotTurnDegree = 45.0;
}
