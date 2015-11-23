
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;


public class JobThread implements Runnable{
	ArrayList<Worker> wl;	
	Socket socket;
	String jar_path;
	String input_path;
	String output_path;
	String maxmemory;
	String waittime;
	JobFrame jf=null;
	ClientUti cu;
	DefaultTableModel workerModel;
	Worker best_worker;
	boolean time_flag=true;


	public JobThread(ArrayList<Worker> wl,Job j,JobFrame newJobFrame,ClientUti cu,DefaultTableModel wm){
		this.wl=wl;
		this.jf=newJobFrame;
		jar_path=j.jar_path;
		input_path=j.input_path;
		output_path=j.output_path;
		maxmemory=j.max_memory;
		waittime=j.wait_time;
		this.cu=cu;
		workerModel=wm;

	}

	@Override
	public void run() {

		SocketFactory factory=SSLSocketFactory.getDefault();
//		ClientUti cu=new ClientUti();

		for (Worker w:wl){
			System.out.println(w.ip);
			Socket socket = null;
			try {
				socket = factory.createSocket(w.ip,w.port);
				w.socket=socket;
				cu.checkWorker(w,socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				w.status="DOWN";
				w.down_reason=e.getMessage();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				String exceptionMessage = e.getMessage();
				System.err.println(exceptionMessage);
				//				jf.worker_status.append("Communication Parse exception:  "+ exceptionMessage + "\n");
				e.printStackTrace();
			}

			//			String worker_info=w.name+" "+w.getIP()+" "+w.getPort()+" "+w.getWorkLoad()+" "+w.getStatus();
			//			jf.worker_status.append(worker_info+"\n");
			String down_workload=w.workload+"";
			if (w.workload==2){
				down_workload="-";
			}
			String[] worker_i={w.name,w.ip,w.port+"",down_workload,w.status,w.down_reason};
			workerModel.addRow(worker_i);
		}

		best_worker=new Worker();
		double minload=1;
		for (Worker w:wl){
			if (w.status.equals("RUNNING")){
				if(w.workload<minload){
					minload=w.workload;
					best_worker=w;
				}
			}
		}

		if (best_worker.name.equals("No Worker")){
			jf.job_status.append("Job failed because there is no worker avalibale now");
		}else{
			jf.job_status.append("Best worker is "+best_worker.name + "\n");
			//			
			try {

				Worker timer_worker=best_worker;
				int time=Integer.parseInt(waittime);
				int wait_time=time*1000;

				new Timer().schedule(new TimerTask(){   
					public void run() {  
						time_flag=false;
						System.out.println("boom...");  
						String network_status="";
						String failure_reason="";

						try {
							Socket best_socket_monitor = factory.createSocket(timer_worker.ip,timer_worker.port);
							DataOutputStream out=new DataOutputStream(best_socket_monitor.getOutputStream());
							out.writeUTF(cu.requestJson("monitor", ""));
							System.out.print("aa");
							network_status="Connected";
						} catch (IOException e) {
							// TODO Auto-generated catch block
							network_status="Disconnected";
							failure_reason=e.getMessage();
						}
						System.out.println(jf.job_s);
						if (jf.job_s.equals("running")){
							if(network_status.equals("Connected")){
								jf.job_status.append("Job Failure: Time Out");
							}else{
								if (failure_reason.equals("Connection refused")){
									jf.job_status.append("Job Failure: Time Out caused by VM down."+"\n");
								}else{
									jf.job_status.append("Job Failure: Time Out caused by Network Disconnection."+"\n");
								}

							}

						}
					}
				},wait_time);

				Socket best_socket = factory.createSocket(best_worker.ip,best_worker.port);
				DataInputStream in = new DataInputStream(best_socket.getInputStream());
				DataOutputStream out = new DataOutputStream(best_socket.getOutputStream());
				out.writeUTF(cu.requestJson("transmit", ""));
				out.flush();

				/*
				 * Transmit Jar File and get Response.
				 */
				cu.transFile(jar_path, in, out);
				String jar_reply=in.readUTF();
				JSONObject jar_reply_json=(JSONObject) ClientUti.parser.parse(jar_reply);
				String jar_status=(String) jar_reply_json.get("Value");

				/*
				 *Response is "successful", then transmit "Input File" and get response.
				 */

				if((jar_status.equals("successful"))&&(time_flag==true)){
					jf.job_status.append("Jar file is transmitted successfully.\n");

					cu.transFile(input_path, in, out);

					String input_reply=in.readUTF();
					JSONObject input_reply_json=(JSONObject) ClientUti.parser.parse(input_reply);
					String input_status=(String) input_reply_json.get("Value");

					/*
					 * Input response is "successful", then send "Maxinum Meomory".
					 */

					if((input_status.equals("successful"))&&(time_flag==true)){
						jf.job_status.append("Input file is transmitted successfully\n");

						File output_file=new File(output_path);
						out.writeUTF(cu.requestJson("Output", output_file.getName()));
						out.flush();

						out.writeUTF(cu.requestJson("maxMemoory",maxmemory));
						out.flush();

						/*
						 * Get response-"Start Processing"
						 */
						String start_process=in.readUTF();
						JSONObject start_process_json=(JSONObject) ClientUti.parser.parse(start_process);
						String job_status_start=(String) start_process_json.get("Value");
						if ((job_status_start.equals("start processing"))&&(time_flag==true)){
							jf.job_status.append("Job is processing\n");

							String prcess_reply=in.readUTF();
							JSONObject process_json=(JSONObject) ClientUti.parser.parse(prcess_reply);
							String process_status=(String) process_json.get("Type");
							String process_results=(String) process_json.get("Value");
							if ((process_status.equals("successful"))&&(time_flag==true)){
								jf.job_status.append("Job is processed successfully"+"\n");

								String log_folder_name=System.currentTimeMillis()+"";
								File log_folder=new File(log_folder_name);
								log_folder.mkdir();

								/*
								 * Get  parent folder of "output file".
								 */

								String output_folder=null;
								if (output_file.getParent()==null){
									output_folder=log_folder.getAbsolutePath();
								}else{
									output_folder=output_file.getParent();
								}

								cu.receiveFile(in, out, output_folder);
								File output=new File(output_folder,output_file.getName());
								jf.output_file.setText(output.getAbsolutePath());

								File log=new File(log_folder.getAbsoluteFile(),"log");
								cu.receiveFile(in, out, log_folder.getAbsolutePath());
								jf.log_file.setText(log.getAbsolutePath());				
							}
							else {
								if (time_flag==true){
								jf.job_status.append("Job Failure:"+process_results);
								
								String log_folder_name=System.currentTimeMillis()+"";
								File log_folder=new File(log_folder_name);
								log_folder.mkdir();

								File log=new File(log_folder.getAbsoluteFile(),"log");
								cu.receiveFile(in, out, log_folder.getAbsolutePath());
								jf.log_file.setText(log.getAbsolutePath());}					
							}
						}
						else {
							if(time_flag==true){
							jf.job_status.append("Job processing is not started.\n");}
						}

					}
					else {
						if (time_flag==true){
						jf.job_status.append("Input file is transmitted successfully.\n");}
					}
				}

				else {
					if(time_flag==true){
					jf.job_status.append("Jar file is transmitted successfully.\n");}
				}

			}catch (FileNotFoundException e) {
				// TODO: handle exception
				String exceptionMessage = e.getMessage();
				System.out.println(exceptionMessage);
				if (time_flag==true){
					jf.job_status.append("File Exception:  " + exceptionMessage + "\n");
				}
			}
			catch (IOException e) {
				if (time_flag==true){
								jf.job_status.append("Job Failure : VM is down  " +"\n");}
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				String exceptionMessage = e.getMessage();
				if(time_flag==true){
				jf.job_status.append("Communication Parse Exception:  " + exceptionMessage +"\n");}
			} finally{
				jf.job_s="finished";
			}
		}

	}
}

