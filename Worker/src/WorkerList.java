
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;




public class WorkerList {
	
	private  ArrayList<Worker> workerList=new ArrayList<Worker>();
	private static final JSONParser parser = new JSONParser();
	
	public void addWorker(Worker w){
		getWorkerList().add(w);
	}
	
	public String readFromFile(String file_path){
		String laststr="";
		BufferedReader reader=null;
		try {
			FileInputStream fileInputStream=new FileInputStream(file_path);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while((tempString = reader.readLine()) != null){
				laststr += tempString;
				}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
				if(reader!= null)
					try {
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		}
		return laststr;
	}
	
	public void parseJson(){
		String jsonContext=readFromFile("/Users/bragilee/Resources/workerlist.json");
		JSONObject workerlist;
		try {
			workerlist = (JSONObject) parser.parse(jsonContext);
			int size = workerlist.size();
			for(int i=0;i<size;i++){
				JSONObject jo = (JSONObject) workerlist.get(i+"");
				String ip=(String) jo.get("IP");
				int port=Integer.parseInt(jo.get("port").toString());
				String name=(String) jo.get("name");
				Worker worker=new Worker(port,ip,name);
				workerList.add(worker);
				}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public ArrayList<Worker> getWorkerList() {
		return workerList;
	}

	public void setWorkerList(ArrayList<Worker> wl) {
		workerList = wl;
	}
}
