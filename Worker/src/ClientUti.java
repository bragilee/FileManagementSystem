
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ClientUti {

	public static final JSONParser parser = new JSONParser();
	private BufferedOutputStream bos;

	public synchronized  void transFile(String fileAddress, DataInputStream in,DataOutputStream out) throws FileNotFoundException, IOException, ParseException{
		File sendFile = new File(fileAddress);
		String fileName = requestJson("fileName",sendFile.getName());
		out.writeUTF(fileName);
		out.flush();
		System.err.println("Sending file name and size: " + fileName);
		String bodyEncode = getFileString(fileAddress);
		String[] bodies = bodyEncode.split(System.getProperty("line.separator"));
		out.writeUTF(requestJson("fileBodySize",Integer.toString(bodies.length)));
		out.flush();
		System.err.println("File body size: " + bodies.length);
		for (int i = 0; i < bodies.length; i++) {
			String fileBody = requestJson("fileBody",bodies[i]);
			out.writeUTF(fileBody);
			out.flush();
		}			
		System.err.println("Finished");
	}

	
	public void checkWorker(Worker w,Socket socket) throws IOException, ParseException{
		DataInputStream in = null;
		DataOutputStream  out = null;
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());

		out.writeUTF(requestJson("check",""));
		out.flush();
		String response=in.readUTF();
		JSONObject response_json = (JSONObject) parser.parse(response);
		String reply=(String)response_json.get("Type");
		if (reply.equals("Workload")){
			String workload_s=(String)response_json.get("Value");
			Double workload_d=Double.parseDouble(workload_s);
			w.status="RUNNING";
			w.workload=workload_d;
		}
	}



	public  String requestJson(String request,String value) {
		JSONObject obj=new JSONObject();
		obj.put("Type", request);
		obj.put("Value",value);
		return obj.toJSONString();
	}
	
	private String getFileString(String filePath) throws FileNotFoundException,IOException{
		InputStream in = null;
		byte[] fileData = null;

		in = new FileInputStream(filePath);        
		fileData = new byte[in.available()];
		in.read(fileData);
		in.close();

		Base64Coder encoder = new Base64Coder();  
		String encodedFileString = encoder.encodeLines(fileData);
		return encodedFileString;
	}
	
	public synchronized String receiveFile(DataInputStream in,DataOutputStream out,String folder_path) throws IOException, ParseException{
		String fileNameString;
		File receiveFile=null;
		fileNameString = in.readUTF();
		JSONObject fileNameJson = (JSONObject) parser.parse(fileNameString);
		String fileName = (String)fileNameJson.get("Value");
		System.err.println("Receiving file name and size: " + fileName);
		File folder=new File(folder_path);
		receiveFile=new File(folder,fileName);

		try {
			//create a new file if it doesn't exist already
			receiveFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fileBodySizeReceived = in.readUTF();
		JSONObject fileBodySizeJson = (JSONObject) parser.parse(fileBodySizeReceived);
		String fileBodySizeString = (String) fileBodySizeJson.get("Value");
		int fileBodySize = Integer.parseInt(fileBodySizeString); 
		System.err.println("File body length: " + fileBodySize);
		FileOutputStream fos = new FileOutputStream(receiveFile);
		bos = new BufferedOutputStream(fos);
		for (int i = 0; i < fileBodySize; i++) {
			String fileEncodedString = in.readUTF();
			JSONObject fileBodyJson = (JSONObject) parser.parse(fileEncodedString);
			String fileBody = (String) fileBodyJson.get("Value");
			byte[] fileByte = generateFile(fileBody);
			bos.write(fileByte,0,fileByte.length);
			bos.flush();
		}
		System.err.println("Finished");

		return receiveFile.getAbsolutePath();

	}
	
	public byte[] generateFile(String fileEncoder){
		Base64Coder decoder = new Base64Coder();
		byte[] b = decoder.decodeLines(fileEncoder);
		return b;
	}

}
