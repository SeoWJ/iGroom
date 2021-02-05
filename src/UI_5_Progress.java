import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.ImageIcon;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;

public class UI_5_Progress extends JFrame {

	private JPanel contentPane;
	private static boolean ApplySuccess = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI_5_Progress frame = new UI_5_Progress();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public UI_5_Progress() {
		setTitle("Provisioning ");
		setIconImage(Toolkit.getDefaultToolkit().getImage("./image/iGroom1.jpg"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Provisioning..");
		lblNewLabel.setFont(new Font("굴림", Font.BOLD, 21));
		lblNewLabel.setBounds(154, 105, 161, 35);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("(May takes 10-30 mins)");
		lblNewLabel_1.setFont(new Font("굴림", Font.PLAIN, 13));
		lblNewLabel_1.setBounds(154, 124, 150, 45);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setIcon(new ImageIcon("./image/loading.gif"));
		lblNewLabel_2.setBounds(110, 10, 282, 242);
		contentPane.add(lblNewLabel_2);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Provisioning ");
		setVisible(true);

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				String enable_AWS = "-var=\"enable_AWS=" + Main.getEnableAWS() + "\"";
				String enable_Azure = "-var=\"enable_Azure=" + Main.getEnableAzure() + "\"";
				String enable_GCP = "-var=\"enable_GCP=" + Main.getEnableGCP() + "\"";
				String min_instance = "-var=\"min_instance=" + Main.getMinInstance() + "\"";
				String max_instance = "-var=\"max_instance=" + Main.getMaxInstance() + "\"";
				String AWS_instance = "-var=\"AWS_instance=" + Main.getAWSInstance() + "\"";
				String Azure_instance = "-var=\"Azure_instance=" + Main.getAzureInstance() + "\"";
				String GCP_instance = "-var=\"GCP_instance=" + Main.getGCPInstance() + "\"";

				String[] init = new String[] { "terraform.exe", "init" };
				String[] apply = new String[] { 
						"terraform.exe", "apply", 
						enable_AWS, enable_Azure, enable_GCP,
						min_instance, max_instance,
						AWS_instance, Azure_instance, GCP_instance,
						"-auto-approve" 
				};
				String DBAddress = null;

				try {
					ProcessBuilder processInit = new ProcessBuilder(init);
					processInit.directory(new File("./tf/s3")); // .을 앞에 붙이면 상대경로
					Process process = processInit.start();
					String str;
					BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
					while ((str = stdOut.readLine()) != null) {
						System.out.println(str);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					String[] s3_apply = new String[] { "terraform.exe", "apply", "-auto-approve" };
					ProcessBuilder processApply = new ProcessBuilder(s3_apply);
					processApply.directory(new File("./tf/s3")); // .을 앞에 붙이면 상대경로
					Process process = processApply.start();
					String str;
					BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
					while ((str = stdOut.readLine()) != null) {
						System.out.println(str);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					ProcessBuilder processInit = new ProcessBuilder(init);
					processInit.directory(new File("./tf")); // .을 앞에 붙이면 상대경로
					Process process = processInit.start();
					String str;
					BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
					while ((str = stdOut.readLine()) != null) {
						System.out.println(str);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					boolean Applying = true;
					while (Applying) {
						ProcessBuilder processApply = new ProcessBuilder(apply);
						processApply.directory(new File("./tf")); // .을 앞에 붙이면 상대경로
						Process process = processApply.start();
						String str;
						BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
						while ((str = stdOut.readLine()) != null) {
							if (str.contains("Apply complete!"))
								Applying = false;
							if (str.contains("Database-address = ")) {
								DBAddress = str;
								DBAddress = DBAddress.substring(19, DBAddress.length());
								Main.setDatabaseAddress(DBAddress);

								BufferedWriter OutputFile;
								OutputFile = new BufferedWriter(new FileWriter("sys_data.bin"));

								OutputFile.write("" + Main.getEnableAWS() + "\n");
								OutputFile.write("" + Main.getEnableAzure() + "\n");
								OutputFile.write("" + Main.getEnableGCP() + "\n");

								OutputFile.write(Main.getAWSInstance() + "\n");
								OutputFile.write(Main.getAzureInstance() + "\n");
								OutputFile.write(Main.getGCPInstance() + "\n");

								OutputFile.write("" + Main.getMinInstance() + "\n");
								OutputFile.write("" + Main.getMaxInstance() + "\n");

								OutputFile.write(Main.getDatabaseAddress() + "\n");

								OutputFile.close();
							}
							System.out.println(str);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				//////////////////// JDBC Connection////////////////////////////
				// DB어드레스는 line 84에서 선언된 String DBAddress에 line 136을 통해 실제값이 들어가 있음.
				
				String url = "jdbc:mysql://" + DBAddress + "/mysql"; // mysql접속을 위한 url
				
				List<String> aws_connection_ips = new ArrayList<String>(); // 접속 ip 저장을 위한 arraylist
				List<String> azure_connection_ips = new ArrayList<String>();
				List<String> gcp_connection_ips = new ArrayList<String>();
				
				boolean DBConnecting = true;
				while (DBConnecting) {
					try {
						Main.setDatabaseConnection(url);

						// Statement는 정적 SQL문을 실행하고 결과를 반환받기 위한 객체. Statement하나당 한개의 ResultSet 객체만을 열 수
						// 있음.
						java.sql.Statement st = null;
						ResultSet result = null;
						st = Main.getDatabaseConnection().createStatement();
						result = st.executeQuery("select user_host from general_log where user_host like '[azure]%'"); // executeQuery
																														// :
																														// 쿼리를
																														// 실행하고
																														// 결과를
																														// ResultSet
																														// 객체로
																														// 반환.
						// 결과를 하나씩 출력.
						String aws_connection_ip[] = {};
						int azure_count = 0; // 프로바이더 접속 ip 확인시 카운트 증가
						boolean check = true;
						while (result.next()) {
							if (azure_count >= Main.getMinInstance()) { // 접속 ip가 2개 이상 확인되면 break
								break;
							}
							String str = result.getNString(1);
							azure_connection_ips.add(str.substring(12, str.length() - 1));
							azure_count++;
						}
						result = st.executeQuery("select user_host from general_log where user_host like '[gcp]%'"); // executeQuery
																														// :
																														// 쿼리를
																														// 실행하고
																														// 결과를
																														// ResultSet
																														// 객체로
																														// 반환.
						int gcp_count = 0;
						while (result.next()) {
							if (gcp_count >= Main.getMinInstance()) {
								break;
							}
							String str = result.getNString(1);
							gcp_connection_ips.add(str.substring(10, str.length() - 1));
							gcp_count++;
						}
						result = st.executeQuery("select user_host from general_log where user_host like '[aws]%'"); // executeQuery
																														// :
																														// 쿼리를
																														// 실행하고
																														// 결과를
																														// ResultSet
																														// 객체로
																														// 반환.
						int aws_count = 0;
						while (result.next()) {
							if (aws_count >= Main.getMinInstance()) {
								break;
							}
							String str = result.getNString(1);
							for (int i = 5; i < str.length(); i++) {
								if (str.charAt(i) == '[') {
									aws_connection_ips.add(str.substring(i + 1, str.length() - 1));
									aws_count++;
								}
							}
						}
						if (aws_count >= Main.getMinInstance() && gcp_count >= Main.getMinInstance() && azure_count >= Main.getMinInstance()) {
							System.out.println("<aws ip list>");
							for (int i = 0; i < aws_connection_ips.size(); i++) {
								System.out.println(aws_connection_ips.get(i));
							}
							System.out.println("<azure ip list>");
							for (int i = 0; i < azure_connection_ips.size(); i++) {
								System.out.println(azure_connection_ips.get(i));
							}
							System.out.println("<gcp ip list>");
							for (int i = 0; i < gcp_connection_ips.size(); i++) {
								System.out.println(gcp_connection_ips.get(i));
							}
							System.out.println("Number of instances connected from aws :" + aws_count);
							System.out.println("Number of instances connected from azure :" + azure_count);
							System.out.println("Number of instances connected from gcp :" + gcp_count);
							System.out.println("Over than 2 connections checked from each providers!");
							DBConnecting = false;
						}
						
						aws_connection_ips.clear();
						azure_connection_ips.clear();
						gcp_connection_ips.clear();
					} catch (SQLException e) {
						System.out.println("SQLException: " + e.getMessage());
						System.out.println("SQLState: " + e.getSQLState());
					}
					
					Thread.sleep(5000);
				}
				////////////////////////////////////////////////
				return null;
			}

			protected void done() {
				JOptionPane.showMessageDialog(null, "프로비저닝이 완료되었습니다.");
				dispose();
				Management_1 m1 = new Management_1();
			}
		};

		worker.execute();
	}
}