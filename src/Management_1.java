import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

//import sun.security.ec.point.Point;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import java.awt.Toolkit;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;

public class Management_1 extends JFrame {

	static String Provider_Name;
	static String Instance_Ip;
	static String Instance_State;
	static String Instance_Type;
	static int Instance_Number;

	public static String NetworkName;
	public static String Instance_ID;
	private JPanel contentPane;
	private final JCheckBox chckbxNewCheckBox = new JCheckBox("");
	
	public static String contents[][] = new String[100][4];

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Management_1 frame = new Management_1();
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
	public Management_1() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("./image/iGroom1.jpg"));
		setBounds(100, 100, 555, 300);
	      contentPane = new JPanel();
	      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	      setContentPane(contentPane);
	      contentPane.setLayout(null);
		
		String header[] = { "Instance IP", "Provider", "상태", "CPU 사용량" };
		
		String[] IP = new String[100];
		String[] Provider = new String[100];
		String[] Status = new String[100];
		String[] CPU = new String[100];
		
		for(int i=0; i<100; i++) {
			contents[i][0] = IP[i];
			contents[i][1] = Provider[i];
			contents[i][2] = Status[i];
			contents[i][3] = CPU[i];
		}
		
		for(int i=0; i<Main.getSocketList().size(); i++) {
			contents[i][0] = Main.getSocketList().get(i).getIp();
			contents[i][1] = Main.getSocketList().get(i).getProvider();
			contents[i][2] = Main.getSocketList().get(i).getStatus();
			contents[i][3] = "-";
		}

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				Vector<List<String>> Ip = new Vector<List<String>>();
				
				List<String> aws_connection_ips = new ArrayList<String>(); // 접속 ip 저장을 위한 arraylist
				List<String> azure_connection_ips = new ArrayList<String>();
				List<String> gcp_connection_ips = new ArrayList<String>();
				
				String[] ProviderList = { "AWS", "Azure", "GCP" };
				
				boolean init = true;
				
				while (true) {
					try {
						aws_connection_ips.clear();
						azure_connection_ips.clear();
						gcp_connection_ips.clear();
						Ip.clear();
						
						if (Main.getDatabaseConnection() == null)
							Main.setDatabaseConnection("jdbc:mysql://" + Main.getDatabaseAddress() + "/mysql");

						// Statement는 정적 SQL문을 실행하고 결과를 반환받기 위한 객체. Statement하나당 한개의 ResultSet 객체만을 열 수
						// 있음.
						java.sql.Statement st = null;
						ResultSet result = null;
						st = Main.getDatabaseConnection().createStatement();
						result = st.executeQuery("select user_host from general_log where user_host like '[azure]%'"); // executeQuery

						boolean check = true;
						while (result.next()) {
							String str = result.getNString(1);
							azure_connection_ips.add(str.substring(12, str.length() - 1));
						}
						result = st.executeQuery("select user_host from general_log where user_host like '[gcp]%'"); // executeQuery

						while (result.next()) {
							String str = result.getNString(1);
							gcp_connection_ips.add(str.substring(10, str.length() - 1));
						}
						result = st.executeQuery("select user_host from general_log where user_host like '[aws]%'"); // executeQuery

						while (result.next()) {
							String str = result.getNString(1);
							for (int i = 5; i < str.length(); i++) {
								if (str.charAt(i) == '[') {
									aws_connection_ips.add(str.substring(i + 1, str.length() - 1));
								}
							}
						}

						Ip.add(aws_connection_ips);
						Ip.add(azure_connection_ips);
						Ip.add(gcp_connection_ips);
					} catch (SQLException e) {
						System.out.println("SQLException: " + e.getMessage());
						System.out.println("SQLState: " + e.getSQLState());
					}

					

					for (int i = 0; i < Main.getSocketList().size(); i++)
						Main.getSocketList().get(i).setSocketNum(i);

					for (int i = 0; i < Ip.size(); i++) {
						for (int j = 0; j < Ip.get(i).size(); j++) {
							System.out.println("SQL>> " + Ip.get(i).get(j));
							if(Main.getDeadIp().contains(Ip.get(i).get(j)))
								continue;
							if(Main.getAliveIp().contains(Ip.get(i).get(j)))
								continue;
							
							try {
								SocketSet s = new SocketSet(Ip.get(i).get(j), 8082, Main.getSocketList().size(), ProviderList[i]);
								Main.getSocketList().add(s);
								Main.getAliveIp().add(Ip.get(i).get(j));
								s.execute();
							} catch (Exception e) {
								if (init) {
									Main.getAliveIp().remove(Ip.get(i).get(j));
									Main.getDeadIp().add(Ip.get(i).get(j));
								}
								e.printStackTrace();
							}
						}
					}
					
					System.out.println("+++++++ Dead Ip +++++++");
					for(int i=0; i<Main.getDeadIp().size(); i++)
						System.out.println(Main.getDeadIp().get(i));
					
					System.out.println("+++++++ Alive Ip +++++++");
					for(int i=0; i<Main.getAliveIp().size(); i++)
						System.out.println(Main.getAliveIp().get(i));
					
					System.out.println("+++++++++++++++++++++");

					for (int i = 0; i < Main.getSocketList().size(); i++) {
						Main.getSocketList().get(i).getPrintWriter().println("continue");
						Main.getSocketList().get(i).getPrintWriter().flush();
					}
					
					init = false;
					
					Thread.sleep(5000);
				}
			}

		};
		worker.execute();

		DefaultTableModel model = new DefaultTableModel(contents, header);
//		JTable table = new JTable(model);
//		JScrollPane scrollPane = new JScrollPane(table);
//		scrollPane.setBounds(0, 82, 540, 138);
//		contentPane.add(scrollPane);

		 JTable table = new JTable(model);   
//	      table.setFont(new Font("굴림", Font.PLAIN, 13));
	      JScrollPane scrollPane = new JScrollPane(table);
	      scrollPane.setViewportBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
	      scrollPane.setBounds(0, 49, 540, 171);
	      contentPane.add(scrollPane);
		
		JButton btnPrevious = new JButton("<-Previous");
		btnPrevious.setHorizontalAlignment(SwingConstants.LEFT);
		btnPrevious.setBounds(319, 230, 99, 23);
		contentPane.add(btnPrevious);

		JButton btnNext = new JButton("Next->");
		btnNext.setBounds(430, 230, 91, 23);
		contentPane.add(btnNext);

		JLabel lblNewLabel = new JLabel("\uC0DD\uC131\uB41C \uC778\uC2A4\uD134\uC2A4");
		lblNewLabel.setBounds(1, 68, 99, 15);
		contentPane.add(lblNewLabel);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Management");
		setVisible(true);

		Timer timer = new Timer();
		TimerTask timer_task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				DefaultTableModel dtm = (DefaultTableModel) table.getModel();
				dtm.setNumRows(0);

				dtm = new DefaultTableModel(contents, header);
				table.setModel(dtm);
			}

		};

		timer.schedule(timer_task, 1000, 5000);

		btnPrevious.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {// UI_2으로 이동
				// TODO Auto-generated method stub
				dispose();
				UI_1 f = new UI_1();
				timer.cancel();
			}
		});

		table.addMouseListener((MouseListener) new MouseAdapter() { // 테이블 더블클릭시 이벤트
			@Override
			public void mouseClicked(MouseEvent e) {

				int row = table.getSelectedRow(); // 클릭된 열의 값
				System.out.println(row);

				Instance_Ip = (String) table.getModel().getValueAt(table.getSelectedRow(), 0);
				Provider_Name = (String) table.getModel().getValueAt(table.getSelectedRow(), 1);
				Instance_State = (String) table.getModel().getValueAt(table.getSelectedRow(), 2);
				Instance_Type = (String) table.getModel().getValueAt(table.getSelectedRow(), 3);
				Instance_Number = row;
				// Management_2 의 정보란에 들어갈 변수들입니다.
				// 클릭된 열에 해당하는 각 행들의 값을 String으로 파싱해 차례로 변수에 넣어줍니다.

				if (row <= Main.getSocketList().size() - 1) {
					dispose();
					Management_2 f1 = new Management_2();
				}
			}
		});
		;
	}

}
