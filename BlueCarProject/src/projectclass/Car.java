package projectclass;

import java.util.*;

public class Car {
	public int car_id;
	public boolean status;
	public int pos_id;
	public float x,y,dir;
	public String start_time;
	public ArrayList<Integer> send_tot;
	public ArrayList<Integer> receive_tot;
	public static final int h=25,w=15;
	public boolean wait_update=false;
	public int update_status;
	public int left_motor,right_motor;
	public float rotate_per_sec,move_per_sec;
	public boolean auto_report_obstacle,auto_report_sensor;
	public int maxreportpersec_obstacle,maxreportpersec_sensor;
}
