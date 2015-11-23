
import java.net.Socket;

public class Worker {
	public int port;
	public String ip;
	public String status;
	public double workload;
	public String name;
	public Socket socket=null;
	public String down_reason="";
	
	public Worker(int port, String ip,String name){
		this.port=port;
		this.ip=ip;
		workload=2;
		status="";
		this.name=name;
		
		
	}
	
	public Worker(){
		name="No Worker";
	}
	
	public void setStatus(String s){
		status=s;
	}
	
	public void setWorkLoad(double workload_f){
		workload=workload_f;
	}
	
	public double getWorkLoad(){
		return workload;
	}
	
	public String getStatus(){
		return status;
	}
	
	public String getIP(){
		return ip;
	}
	
	public int getPort(){
		return port;
	}
	
	public void setIP(String ip){
		 this.ip=ip;
	}
	
	public void setPort(int port){
		this.port=port;
	}
	
}