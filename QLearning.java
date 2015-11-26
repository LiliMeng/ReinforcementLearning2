package ReinforcementLearning;

import ReinforcementLearning.RobotAction;
import ReinforcementLearning.LUQTable;
import java.util.Random;

public class QLearning
{
	public static final double LearningRate = 0.1;
	public static final double DiscountRate = 0.9;
	public static final double ExploitationRate = 1;
	private int lastState;
	private int lastAction;
	private boolean first = true;
	private LUQTable Qtable;
	public double QValueDiff;

	public QLearning(LUQTable table)
	{
		this.Qtable = table;
	}

	public void learn(int state, int action, double reward)
	{
		System.out.println("reward: " + reward);
		if(first)
		{
			first = false;
		}
		else
		{
			double oldQValue = Qtable.getQValue(lastState, lastAction);
			double newQValue = oldQValue + LearningRate * (reward+ DiscountRate * Qtable.maxQValue(state)-oldQValue);
			
			Qtable.setQValue(lastState, lastAction, newQValue);
		}
		
		lastState = state;
		lastAction = action;
	}
	
	/*
	public int selectAction(int state){

		double thres = Math.random();
		
		int actionIndex = 0;
		
		if (thres<ExploitationRate)
		{//exploratory
			Random ran = new Random();
			actionIndex = ran.nextInt(((RobotAction.numRobotActions-1 - 0) + 1));
		}
		else
		{//greedy
			
			actionIndex=Qtable.bestAction(state);
		}
		return actionIndex;
	}*/
    
	public int selectAction(int state, long time)
	{
		double qValue;
		double sum = 0.0;
		double[] value = new double[RobotAction.numRobotActions];
	
		for(int i =0; i<value.length; i++)
		{
			qValue = Qtable.getQValue(state, i);
			value[i] = Math.exp(ExploitationRate *qValue);
			sum +=value[i];
			System.out.println("Q-value: " + qValue);
		}
		
		if(sum!=0)
		{
			for(int i = 0; i<value.length; i++)
			{
				value[i] /=sum;
				System.out.println("P(a|s): " + value[i]);
			}
		}
		else
		{
			return Qtable.bestAction(state);
		}
       
		int action = 0;
		double cumProb = 0.0;
		double randomNum = Math.random();
		System.out.println("Random Number: " + randomNum);
		while(randomNum > cumProb && action <value.length)
		{
			cumProb +=value[action];
			action ++;
		}
		
		return action -1;
	}
}
