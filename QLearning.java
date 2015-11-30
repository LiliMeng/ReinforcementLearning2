package ReinforcementLearning;

import ReinforcementLearning.RobotAction;
import ReinforcementLearning.LUQTable;
import java.util.Random;

public class QLearning
{
	public static final double LearningRate = 0.1;
	public static final double DiscountRate = 0.9;
	//public double ExplorationRate = 0.8;
	private int lastState;
	private int lastAction;
	private LUQTable Qtable;

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
}
