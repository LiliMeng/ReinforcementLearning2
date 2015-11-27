package ReinforcementLearning;

import ReinforcementLearning.RobotAction;
import ReinforcementLearning.LUQTable;
import java.util.Random;

public class QLearning
{
	public static final double LearningRate = 0.1;
	public static final double DiscountRate = 0.99;
	public double ExplorationRate = 0.01;
	private int lastState;
	private int lastAction;
	private LUQTable Qtable;

	public double setExploitationRate(double value)
	{
		ExplorationRate=value;
		return ExplorationRate;
	}
	public QLearning(LUQTable table)
	{
		this.Qtable = table;
	}

	public void learn(int state, int action, double reward)
	{
		double oldQValue = Qtable.getQValue(lastState, lastAction);
		double newQValue = oldQValue + LearningRate * (reward+ DiscountRate * Qtable.maxQValue(state)-oldQValue);
	    
		//update the Q value in the look up table
		Qtable.setQValue(lastState, lastAction, newQValue);
		
		//update state and action
		lastState = state;
		lastAction = action;
	}
	
	
	public int selectAction(int state){

		double thres = Math.random();
		
		int actionIndex = 0;
		
		if (thres<ExplorationRate)
		{//randomly select one action from action(0,1,2,3)
			Random ran = new Random();
			actionIndex = ran.nextInt(((RobotAction.numRobotActions-1 - 0) + 1));
		}
		else
		{//e-greedy
			actionIndex=Qtable.bestAction(state);
		}
		return actionIndex;
	}
   
}
