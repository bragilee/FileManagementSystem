
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Formatter;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class ServerUti {
	
	public static final JSONParser parser = new JSONParser();
	
	public String createJobfolder(String folderName){

		File folder = new File(folderName);
		if (!folder.exists()) { 
			folder.mkdir(); 
		} 
		String folderPath=folder.getAbsolutePath();
		return folderPath;
	}

	public synchronized int processJarFile(String jarFile, String inputFile,String outputFile,long memory_f){
		String xmx="-Xmx"+memory_f+"m";
		ProcessBuilder pb = new ProcessBuilder("java",xmx,"-jar", jarFile,inputFile,outputFile);  

		File file=new File(jarFile);
		File parentDir=file.getParentFile();
		pb.directory(parentDir);

		File logfolder = new File(parentDir,"logfolder");
		logfolder.mkdir();
		File logfile=new File(logfolder,"log");
		pb.redirectErrorStream(true);
		pb.redirectOutput(logfile);
		//		int flag=0;
		Process p;

		try {
			p = pb.start();
			int flag=p.waitFor();
			//			p.destroy();
//			System.exit(0);
			return (flag==0? 1:2);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return 3;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return 4;
		}
	}

	public void deleteJobfolder(String folderpath){
		deleteFolderFiles(folderpath);
		File folder=new File(folderpath);
		folder.delete();
	}

	private  void deleteFolderFiles(String folderpath){
		File filefolder=new File(folderpath);
		File[] files=filefolder.listFiles();
		for(File f:files){
			f.delete();
		}
	}

	public synchronized void returnFile(DataInputStream in,DataOutputStream out,String file_path) throws IOException{
		File sendFile = new File(file_path);
		String fileName = replyJson("fileName",sendFile.getName());
		out.writeUTF(fileName);
		out.flush();
		System.err.println("Sending file name and size: " + fileName);
		String bodyEncode = getFileString(file_path);
		String[] bodies = bodyEncode.split(System.getProperty("line.separator"));
		out.writeUTF(replyJson("fileBodySize",Integer.toString(bodies.length)));
		out.flush();
		System.err.println("File body size: " + bodies.length);
		for (int i = 0; i < bodies.length; i++) {
			String fileBody = replyJson("fileBody",bodies[i]);
			out.writeUTF(fileBody);
			out.flush();
		}			
		System.err.println("Returning file is Finished");
	}

	public  String getCPULoad() throws IOException{

		InputStream is = null;  
		InputStreamReader isr = null;  
		BufferedReader brStat = null;  
		StringTokenizer tokenStat = null;  

		Process process = Runtime.getRuntime().exec("top -b -n 1");  
		is = process.getInputStream();                    
		isr = new InputStreamReader(is);  
		brStat = new BufferedReader(isr);  
		brStat.readLine();
		brStat.readLine();
		tokenStat = new StringTokenizer(brStat.readLine());
		for(int i = 0 ; i<7;i++){
			tokenStat.nextToken();
		}
		float idle= Float.parseFloat(tokenStat.nextToken());
		float workload=1-idle/100;
		
		return format(workload);

	}


    private String format(double value) {  
        return new Formatter().format("%.4f", value).toString();  
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

		String fileBodySizeReceived = in.readUTF();
		JSONObject fileBodySizeJson = (JSONObject) parser.parse(fileBodySizeReceived);
		String fileBodySizeString = (String) fileBodySizeJson.get("Value");
		int fileBodySize = Integer.parseInt(fileBodySizeString); 
		System.err.println("File body length: " + fileBodySize);
		FileOutputStream fos = new FileOutputStream(receiveFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		for (int i = 0; i < fileBodySize; i++) {
			String fileEncodedString = in.readUTF();
			JSONObject fileBodyJson = (JSONObject) parser.parse(fileEncodedString);
			String fileBody = (String) fileBodyJson.get("Value");
			byte[] fileByte = generateFile(fileBody);
			bos.write(fileByte,0,fileByte.length);
			bos.flush();
		}
		System.err.println("Receiving file Finished");

		return receiveFile.getAbsolutePath();

	}

	private byte[] generateFile(String fileEncoder){
		Base64Coder decoder = new Base64Coder();
		byte[] b = decoder.decodeLines(fileEncoder);
		return b;
	}
	
	public synchronized String replyJson(String type,String value){
		JSONObject obj=new JSONObject();
		obj.put("Type", type);
		obj.put("Value", value);
		return obj.toJSONString();
	}
	
	private String getFileString(String filePath) throws FileNotFoundException, IOException{
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
	
}
