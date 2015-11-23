import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

	import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Worker {
		
		public static void main(String[] args){
			System.setProperty("javax.net.ssl.keyStore", "/home/ubuntu/master.cer");
			System.setProperty("javax.net.ssl.keyStorePassword","512288");


			int port=8000;
			ServerSocketFactory factory=SSLServerSocketFactory.getDefault();
			ServerUti su=new ServerUti();
			try(ServerSocket server =factory.createServerSocket(port)){
				while(true){
					Socket socket=server.accept();
					DataInputStream in = new DataInputStream(socket.getInputStream());
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					String request=in.readUTF();
//					System.out.println(request);
					JSONObject request_json=(JSONObject) ServerUti.parser.parse(request);
					String request_type=(String) request_json.get("Type");
					System.out.println(request_type);
					if(request_type.equals("check")){
						Thread check=new Thread(new CheckThread(in,out,su));
						check.setDaemon(true);
						check.start();
					}
					if(request_type.equals("transmit")){
												
						Thread transmit=new Thread(new TransmitThread(in,out,su));
						transmit.setDaemon(true);
						transmit.start();
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		}
	}

