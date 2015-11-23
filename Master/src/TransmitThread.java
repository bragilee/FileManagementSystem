import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.css.CSSCharsetRule;


public class TransmitThread implements Runnable {

	DataInputStream in;
	DataOutputStream out;
	ServerUti su;

	public TransmitThread(DataInputStream in,DataOutputStream out,ServerUti su){
		this.in=in;
		this.out=out;
		this.su=su;
	}

	@Override
	public void run() {
//		ServerUti sul=new ServerUti();
		// TODO Auto-generated method stub
		String job_folder=System.currentTimeMillis()+"";
		String job_folder_path=su.createJobfolder(job_folder);
		File log_folder = null;
		String jar_path = null;
		try {
			jar_path = su.receiveFile(in, out, job_folder_path);

			out.writeUTF(su.replyJson("Jar", "successful"));
			out.flush();

			String input_path = null;

			input_path = su.receiveFile(in, out, job_folder_path);


			out.writeUTF(su.replyJson("Input", "successful"));
			out.flush();

			String outputfile;

			outputfile = in.readUTF();
			JSONObject output_json=(JSONObject) ServerUti.parser.parse(outputfile);
			String output_name=(String) output_json.get("Value");

			String maxmemory = in.readUTF();
			JSONObject memory_json=(JSONObject) ServerUti.parser.parse(maxmemory);
			String memory_s=(String) memory_json.get("Value");
			Long memory_f=Long.parseLong(memory_s);
			System.out.println(memory_f);

			out.writeUTF(su.replyJson("Status", "start processing"));
			out.flush();
			int result=su.processJarFile(jar_path, input_path,output_name,memory_f);
			System.err.println(result);
			String process_result="";
			String message="";
			switch (result) {
			case 1:
				process_result="successful";
				break;
			case 2:
				process_result="failed";
				message="Job Exception";
				break;
			case 3:
				process_result="failed";
				message="Process IO Exception";
				break;
			case 4:
				process_result="failed";
				message="Porcess Interrupt Exception";
				break;
			}
			
			if(process_result.equals("successful")){
				out.writeUTF(su.replyJson(process_result,message));
				out.flush();

				File output_return=new File(job_folder,output_name);
				log_folder=new File(job_folder,"logfolder");
				File log_return=new File(log_folder,"log");

				su.returnFile(in, out, output_return.getAbsolutePath());
				su.returnFile(in,out,log_return.getAbsolutePath());
			}else{
				out.writeUTF(su.replyJson("failed",message));
				out.flush();
				
				File output_return=new File(job_folder,output_name);
				log_folder=new File(job_folder,"logfolder");
				File log_return=new File(log_folder,"log");

				su.returnFile(in,out,log_return.getAbsolutePath());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			String exceptionMessage = e.getMessage();
			System.out.println(exceptionMessage);

//			e.printStackTrace();
		} catch (ParseException e) {

		}

	}


}
