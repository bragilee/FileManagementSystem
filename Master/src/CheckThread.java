import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class CheckThread implements Runnable {
	
	
	private DataInputStream in;
	private DataOutputStream out;
	private ServerUti su;
	
	
	public CheckThread(DataInputStream in,DataOutputStream out,ServerUti s){
		this.in=in;
		this.out=out;
		this.su=s;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {	
			String a= su.replyJson("Workload",su.getCPULoad());
			out.writeUTF(a);
			out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
//	public String replyJson(String type,String value){
//		JSONObject obj=new JSONObject();
//		obj.put("Type", type);
//		obj.put("Value", value);
//		return obj.toJSONString();
//	}
		
	}
	
	
	
	






