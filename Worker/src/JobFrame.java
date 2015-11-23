import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;


public class JobFrame extends JFrame {
	private  JTextField jar;
	private  JTextField input;
	private  JTextField output;
	private  JTextField workload;
	private  JTextField watitime;
	public  JTextField output_file;
	public  JTextField log_file;
	public  JTextArea job_status;
	public String job_s="running";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public JobFrame(Job j,ClientUti cu, ArrayList<Worker> wl) {

		setSize(600,700);
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		
		
		JLabel lblJobInformation = new JLabel("Job Information");
		lblJobInformation.setFont(new Font("Arial Black", Font.BOLD, 14));
		lblJobInformation.setBounds(26, 12, 146, 31);
		this.getContentPane().add(lblJobInformation);

		JLabel lblJar = new JLabel("Jar");
		lblJar.setBounds(26, 57, 26, 15);
		this.getContentPane().add(lblJar);

		jar = new JTextField();
		jar.setEditable(false);
		jar.setBounds(112, 55, 247, 31);
		this.getContentPane().add(jar);
		jar.setColumns(10);
		jar.setText(j.jar_path);

		JLabel lblInput = new JLabel("Input");
		lblInput.setBounds(26, 102, 40, 22);
		this.getContentPane().add(lblInput);

		input = new JTextField();
		input.setEditable(false);
		input.setColumns(10);
		input.setBounds(112, 98, 247, 31);
		this.getContentPane().add(input);
		input.setText(j.input_path);

		JLabel lblOutput = new JLabel("Output");
		lblOutput.setBounds(26, 145, 50, 22);
		this.getContentPane().add(lblOutput);

		output = new JTextField();
		output.setEditable(false);
		output.setColumns(10);
		output.setBounds(112, 141, 247, 31);
		this.getContentPane().add(output);
		output.setText(j.output_path);

		JLabel lblWorkload = new JLabel("Maxminum Memory");
		lblWorkload.setBounds(371, 59, 127, 23);
		this.getContentPane().add(lblWorkload);

		JLabel lblWaittime = new JLabel("WaitTime");
		lblWaittime.setBounds(397, 149, 64, 15);
		this.getContentPane().add(lblWaittime);

		JLabel lblWorkerStatus = new JLabel("Worker Status");
		lblWorkerStatus.setFont(new Font("Arial Black", Font.BOLD, 14));
		lblWorkerStatus.setBounds(26, 192, 115, 31);
		this.getContentPane().add(lblWorkerStatus);

		JLabel lblJob = new JLabel("Job Status");
		lblJob.setFont(new Font("Arial Black", Font.BOLD, 14));
		lblJob.setBounds(26, 351, 115, 31);
		this.getContentPane().add(lblJob);

		JLabel lblOutputFile = new JLabel("Output File");
		lblOutputFile.setFont(new Font("Arial Black", Font.BOLD, 14));
		lblOutputFile.setBounds(26, 526, 115, 31);
		this.getContentPane().add(lblOutputFile);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(26, 394, 555, 120);
		getContentPane().add(scrollPane_1);

		job_status = new JTextArea();
		scrollPane_1.setViewportView(job_status);
		job_status.setEditable(false);
		job_status.setRows(4);

		JLabel lblLogFile = new JLabel("Log File");
		lblLogFile.setFont(new Font("Arial Black", Font.BOLD, 14));
		lblLogFile.setBounds(26, 587, 115, 31);
		this.getContentPane().add(lblLogFile);



		workload = new JTextField();
		workload.setEditable(false);
		workload.setBounds(497, 54, 84, 33);
		this.getContentPane().add(workload);
		workload.setColumns(10);
		workload.setText(j.max_memory+"");

		watitime = new JTextField();
		watitime.setEditable(false);
		watitime.setColumns(10);
		watitime.setBounds(497, 141, 84, 31);
		this.getContentPane().add(watitime);
		watitime.setText(j.wait_time+"");
		
		String[] worker_title={"Worker Name","IP","Port","Workload","Status","Down Reason"};
		String[][] worker_data=new String[0][6];
		
		DefaultTableModel workerModel = new DefaultTableModel(worker_data,worker_title);
		
		JTable worker_table=new JTable(workerModel);
//		JScrollPane scrollPane9 = new JScrollPane(worker_table);
//		scrollPane_1.setBounds(25, 278, 400, 103);
//		this.getContentPane().add(scrollPane9);
		
		JScrollPane scrollPane = new JScrollPane(worker_table);
		scrollPane.setBounds(26, 235, 555, 104);
		getContentPane().add(scrollPane);

		output_file = new JTextField();
		output_file.setEditable(false);
		output_file.setColumns(10);
		output_file.setBounds(26, 558, 555, 30);
		this.getContentPane().add(output_file);

		log_file = new JTextField();
		log_file.setEditable(false);
		log_file.setColumns(10);
		log_file.setBounds(26, 614, 555, 31);
		this.getContentPane().add(log_file);
		
//		System.out.println(wl.size());
		
		Thread job_t=new Thread(new JobThread(wl, j,this,cu,workerModel));
		job_t.setDaemon(true);
		job_t.start();				
	}
}
