package ReinforcementLearning;

import java.awt.geom.*; 

public class Enemy
{
	  String name; 
	  public double bearing; 
	  public double head; 
	  public long ctime; 
	  public double speed; 
	  public double x, y; 
	  public double distance; 
	  public double changehead; 
	  public double energy; 
	 
	  public Point2D.Double guessPosition(long when) 
	  { 
	    double newY, newX; 
	    newX = x;
	    newY = y;
	    return new Point2D.Double(newX, newY); 
	  } 
	 
	  public double guessX(long when) 
	  { 
	    long diff = when - ctime; 
	    System.out.println(diff); 
	    return x;
	  } 
	  public double guessY(long when) 
	  { 
	    return y;
	  } 
}
