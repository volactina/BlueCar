package javapkg;

import java.util.*;

public class SimField {
	int obstaclenum;
	ArrayList<SimObstacle> simobstacles=new ArrayList<SimObstacle>();
	int firenum;
	ArrayList<SimFire> simfires=new ArrayList<SimFire>();
	public SimField() {
		obstaclenum=0;
		SimObstacle obs1=new SimObstacle(100,100,2);
		simobstacles.add(obs1);
		SimObstacle obs2=new SimObstacle(50,50,3);
		simobstacles.add(obs2);
		firenum=0;
		SimFire fire1=new SimFire(86,87);
		simfires.add(fire1);
		SimFire fire2=new SimFire(140,-50);
		simfires.add(fire2);
	}
}
