package ReinforcementLearning;

import ReinforcementLearning.RobotAction;
import ReinforcementLearning.LUQTable;
import java.util.Random;
 
public class QLearning
{
	public static final double LearningRate = 0.1;
	public static final double DiscountRate = 0.9;
    public double explorationRate = 0.9;
	private int lastState;
	private int lastAction;
	private LUQTable Qtable;

	public QLearning(LUQTable table)
	{
		this.Qtable = table;
	}

	public void learnQ(int state, int action, double reward)
	{  
		   double oldQValue = Qtable.getQValue(lastState, lastAction);
		   double newQValue = oldQValue + LearningRate * (reward+ DiscountRate * Qtable.maxQValue(state)-oldQValue);
	    
		   //update the Q value in the look up table
		   Qtable.setQValue(lastState, lastAction, newQValue);
	  
		
		//update state and action
		lastState = state;
		lastAction = action;
	}
	
	public void learnSARSA(int state, int action, double reward)
	{
		double oldQValue = Qtable.getQValue(lastState, lastAction);
		
		int newAction=this.selectAction(state);
		
		double newQValue = oldQValue + LearningRate * (reward+ DiscountRate * Qtable.getQValue(state, newAction)-oldQValue);
	    
		//update the Q value in the look up table
		Qtable.setQValue(lastState, lastAction, newQValue);
		
		//update state and action
		lastState = state;
		lastAction = newAction;
	}
	
	public int selectAction(int state)
	{

		double thres = Math.random();
		
		int actionIndex = 0;
		
		if (thres<explorationRate)
		{//randomly select one action from action(0,1,2)
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
