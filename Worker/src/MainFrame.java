import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;


public class MainFrame extends JFrame{

	private static JTextField jar;
	private static JTextField input;
	private static JTextField output;
	private static JTextField memory;
	private  static ArrayList<Job> jobList=new ArrayList<Job>();
	public static ArrayList<Worker> workerList;
	private static JTextField waittime;
	private JTextField worker_ip;
	private JTextField worker_port;
	private JTextField worker_name;

	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new MainFrame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public MainFrame() {
		initialize();
		System.setProperty("javax.net.ssl.trustStore", "/Users/bragilee/Resources/master.cer");
		System.setProperty("javax.net.ssl.trustStorePassword","512288");
	}

	public void initialize(){
		JFrame frame1 = new JFrame();
		frame1.setSize(700,800);
		frame1.getContentPane().setLayout(null);
		frame1.setTitle("Master");
		frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame1.setVisible(true);
		frame1.setLocationRelativeTo( null );

		JLabel lblWorkload = new JLabel("Memory Usage");
		lblWorkload.setBounds(376, 157, 92, 16);
		frame1.getContentPane().add(lblWorkload);

		JLabel lblJarFile = new JLabel("Jar File");
		lblJarFile.setBounds(25, 92, 61, 16);
		frame1.getContentPane().add(lblJarFile);

		JLabel lblInputFile = new JLabel("Input File");
		lblInputFile.setBounds(25, 157, 61, 16);
		frame1.getContentPane().add(lblInputFile);

		JLabel lblLongestWaitingTime = new JLabel("Longest Wait Time");
		lblLongestWaitingTime.setBounds(537, 157, 117, 16);
		frame1.getContentPane().add(lblLongestWaitingTime);

		input = new JTextField();
		input.setColumns(10);
		input.setBounds(25, 175, 192, 30);
		frame1.getContentPane().add(input);

		jar = new JTextField();
		jar.setBounds(25, 115, 192, 30);
		frame1.getContentPane().add(jar);
		jar.setColumns(10);

		JLabel lblOutputFile = new JLabel("Output File");
		lblOutputFile.setBounds(376, 92, 117, 16);
		frame1.getContentPane().add(lblOutputFile);

		output = new JTextField();
		output.setColumns(10);
		output.setBounds(373, 120, 296, 30);
		frame1.getContentPane().add(output);

		memory = new JTextField();
		memory.setText("256");
		memory.setColumns(10);
		memory.setBounds(373, 175, 106, 30);
		frame1.getContentPane().add(memory);

		waittime = new JTextField();
		waittime.setText("60");
		waittime.setColumns(10);
		waittime.setBounds(537, 175, 117, 30);
		frame1.getContentPane().add(waittime);

		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientUti cu=new ClientUti();
				for (Job j:jobList){
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								new JobFrame(j,cu,workerList);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
				
				frame1.dispose();
			}
		});
		btnSubmit.setBounds(210, 650, 222, 45);
		frame1.getContentPane().add(btnSubmit);

		JButton btnNewButton = new JButton("...");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser();  
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);  
				jfc.showDialog(new JLabel(), "Select Jar File");  
				File jar_file=jfc.getSelectedFile();
				String jar_file_path=jar_file.getAbsolutePath();
				jar.setText(jar_file_path);
			}
		});
		btnNewButton.setBounds(217, 117, 110, 28);
		frame1.getContentPane().add(btnNewButton);

		JButton button = new JButton("...");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser();  
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);  
				jfc.showDialog(new JLabel(), "Select Input File");  
				File input_file=jfc.getSelectedFile();
				String input_file_path=input_file.getAbsolutePath();
				input.setText(input_file_path);
			}
		});
		button.setBounds(217, 176, 110, 30);
		frame1.getContentPane().add(button);

		String [] job_title={"Jar File","Input File","Output File","Maximum Memory","Longest Wait Time"};
		String[][] job_data = new String[0][5];
		DefaultTableModel jobModel = new DefaultTableModel(job_data,job_title);

		JTable job_table=new JTable(jobModel);

		JButton btnNewButton_2 = new JButton("Add From File");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (Worker w:workerList){
					System.out.println(w.name);
				}
			}
		});
		btnNewButton_2.setBounds(373, 217, 296, 39);
		frame1.getContentPane().add(btnNewButton_2);

		JScrollPane scrollPane_1 = new JScrollPane(job_table);
		scrollPane_1.setBounds(25, 278, 642, 103);
		frame1.getContentPane().add(scrollPane_1);

		JButton btnNewButton_1 = new JButton("Add Job");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String jar_file=jar.getText();
				String input_file=input.getText();
				String output_file=output.getText();
				String max_memory=memory.getText();
				String wait_time=waittime.getText();
				if ((!jar_file.equals(""))&&(!input_file.equals(""))&&(!output_file.equals(""))){
					Job j=new Job(jar_file,input_file,output_file,max_memory,wait_time);
					jobList.add(j);
				
				String[] job={jar_file.trim(),input_file.trim(),output_file.trim(),max_memory.trim(),wait_time.trim()};
				DefaultTableModel tableModel = (DefaultTableModel) job_table.getModel();
				tableModel.addRow(job);
				jar.setText("");
				input.setText("");
				output.setText("");
				memory.setText("256");
				waittime.setText("60");}
			}
		});
		btnNewButton_1.setBounds(25, 217, 302, 39);
		frame1.getContentPane().add(btnNewButton_1);

		JLabel lblMb = new JLabel("MB");
		lblMb.setBounds(478, 182, 61, 16);
		frame1.getContentPane().add(lblMb);

		JLabel lblSeconds = new JLabel("s");
		lblSeconds.setBounds(653, 179, 21, 23);
		frame1.getContentPane().add(lblSeconds);

		JLabel lblJobSubmission = new JLabel("Job Submission");
		lblJobSubmission.setFont(new Font("Arial", Font.BOLD, 17));
		lblJobSubmission.setBounds(265, 29, 146, 39);
		frame1.getContentPane().add(lblJobSubmission);

		JLabel lblWorkerList = new JLabel("Worker List");
		lblWorkerList.setFont(new Font("Arial", Font.BOLD, 17));
		lblWorkerList.setBounds(286, 408, 146, 39);
		frame1.getContentPane().add(lblWorkerList);

		String [] worker_header={"Worker Name","IP Address","Port"};
		WorkerList wl=new WorkerList();
		wl.parseJson();
		workerList=wl.getWorkerList();
		int num_worker=workerList.size();
		String[][] worker_data=new String[num_worker][3];
		for (int i=0;i<num_worker;i++){
			Worker w=workerList.get(i);
			worker_data[i][0]=w.name;
			worker_data[i][1]=w.ip;
			worker_data[i][2]=w.port+"";
		}
		
		DefaultTableModel workerModel = new DefaultTableModel(worker_data,worker_header);

		JTable worker_table=new JTable(workerModel);

		JScrollPane scrollPane = new JScrollPane(worker_table);
		scrollPane.setBounds(25, 446, 642, 103);
		frame1.getContentPane().add(scrollPane);

		worker_ip = new JTextField();
		worker_ip.setBounds(192, 589, 135, 39);
		frame1.getContentPane().add(worker_ip);
		worker_ip.setColumns(10);

		JLabel ip = new JLabel("IP");
		ip.setBounds(192, 561, 61, 16);
		frame1.getContentPane().add(ip);

		worker_port = new JTextField();
		worker_port.setColumns(10);
		worker_port.setBounds(358, 589, 135, 39);
		frame1.getContentPane().add(worker_port);

		JLabel port = new JLabel("Port");
		port.setBounds(358, 561, 61, 16);
		frame1.getContentPane().add(port);

		JButton btnNewButton_3 = new JButton("Add Worker");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String w_name=worker_name.getText();
				String w_ip=worker_ip.getText();
				String w_port=worker_port.getText();
				String [] new_worker={w_name,w_ip,w_port};
				int port=Integer.parseInt(w_port);
				if ((!w_name.trim().equals(""))&&(!w_ip.trim().equals(""))&&(!w_port.trim().equals(""))){
					Worker w=new Worker(port,w_ip,w_name);
					workerList.add(w);
					
					DefaultTableModel workerModel = (DefaultTableModel) worker_table.getModel();
					workerModel.addRow(new_worker);
					
					worker_name.setText("");
					worker_ip.setText("");
					worker_port.setText("");
					
				}		
			}
		});
		btnNewButton_3.setBounds(534, 590, 135, 39);
		frame1.getContentPane().add(btnNewButton_3);
		
		worker_name = new JTextField();
		worker_name.setColumns(10);
		worker_name.setBounds(22, 589, 135, 39);
		frame1.getContentPane().add(worker_name);
		
		JLabel lblWorkerName = new JLabel("Worker Name");
		lblWorkerName.setBounds(25, 561, 84, 16);
		frame1.getContentPane().add(lblWorkerName);

	}
}