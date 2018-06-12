package projectclass;

public class Task {
	public int task_id;
	public int car_id;
	
	/*0 等待完成（0 未发送/1 已发送） 2 完成中 3 取消中 4 已取消 5 已完成*/
	public int task_status; 
	public boolean task_cycle;
	public int pos_id;
	public int wait_t;
}
