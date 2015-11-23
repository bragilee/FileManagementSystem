

public class Job {
	
	public String jar_path;
	public String input_path;
	public String output_path;
	public String max_memory="";
	public String wait_time="";
	public float work_load=Float.POSITIVE_INFINITY;
	
	public String status="RUNNING";
	
	public Job(String jar, String input, String output, String me, String wait){
		jar_path=jar;
		input_path=input;
		output_path=output;
		max_memory=me;
		wait_time=wait;
	}
}
