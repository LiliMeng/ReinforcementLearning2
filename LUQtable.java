package ReinforcementLearning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;


import ReinforcementLearning.RobotAction;
import ReinforcementLearning.RobotState;

import robocode.*; 

import robocode.AdvancedRobot; 

public class LUQTable {
	
	private double[][] qTable;
	
	public LUQTable()
	{
		qTable = new double[RobotState.numStates][RobotAction.numRobotActions];
		initializeQtable();
	}
	
	public void initializeQtable()
	{
		for(int i=0; i<RobotState.numStates; i++)
		{
			for(int j=0; j<RobotAction.numRobotActions; j++)
			{
				qTable[i][j] = 0;
			}
		}
	}
	
	public double maxQValue(int state)
	{
		double maxQvalue = Double.NEGATIVE_INFINITY;
		
		for(int i=0; i<qTable[state].length;i++)
		{	
			if(qTable[state][i] > maxQvalue)
			{
				maxQvalue = qTable[state][i];
			}
		}
		return maxQvalue;
	}
	
	public int bestAction(int state)
	{
		double maxQvalue = Double.NEGATIVE_INFINITY;
		
		int bestAct = 0;
		for(int i=0; i<qTable[state].length; i++)
		{
			double qValue = qTable[state][i];
			if(qValue > maxQvalue)
			{
				maxQvalue = qValue;
				bestAct = i;
			}
		}
		return bestAct;
	}
 
	public double getQValue(int state, int action)
	{
		return qTable[state][action];
	}
	
	public void setQValue(int state, int action, double value)
	{
		qTable[state][action] = value;
	}
	
	public double totalValue()
	{
		double sum =0.0;
		for(int i=0; i<RobotState.numStates; i++)
		{
			for(int j=0; j<RobotAction.numRobotActions; j++)
			{
				sum = sum + qTable[i][j];
				
			}
		}
		return sum;
	}
	
	 public void loadData(File file)   
	  {   
	    BufferedReader r = null;   
	    try   
	    {   
	      r = new BufferedReader(new FileReader(file));   
	      for (int i = 0; i < RobotState.numStates; i++)   
	        for (int j = 0; j < RobotAction.numRobotActions; j++)   
	          qTable[i][j] = Double.parseDouble(r.readLine());   
	    }   
	    catch (IOException e)   
	    {   
	      System.out.println("IOException trying to open reader: " + e);   
	      initializeQtable();   
	    }   
	    catch (NumberFormatException e)   
	    {   
	      initializeQtable();   
	    }   
	    finally   
	    {   
	      try   
	      {   
	        if (r != null)   
	          r.close();   
	      }   
	      catch (IOException e)   
	      {   
	        System.out.println("IOException trying to close reader: " + e);   
	      }   
	    }   
	  }   
	
	 public void saveData(File file)   
	  {   
	    PrintStream w = null;   
	    try   
	    {   
	      w = new PrintStream(new RobocodeFileOutputStream(file));   
	      for (int i = 0; i < RobotState.numStates; i++)   
	        for (int j = 0; j < RobotAction.numRobotActions; j++)   
	          w.println(new Double(qTable[i][j]));   
	   
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
