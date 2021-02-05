import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingWorker;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class Management_2 extends JFrame {

	public static Thread t;
	private JPanel contentPane;
	private JPanel panel;
	private JPanel panel_1;
	private JCheckBox chckbxNewCheckBox_1;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JLabel lblNewLabel_Instance_IP;
	private JLabel lblNewLabel_Instance_Static;
	private JLabel lblNewLabel_Type;
	private JLabel lblNewLabel_4;
	private JLabel lblNewLabel_5;
	private JLabel lblNewLabel_CPU;
	private JButton btnNext;
	private JButton btnPrevious;
	private JLabel lblNewLabel_Instance_Type;
	public SocketSet SelectedInstance = Main.getSocketList().get(Management_1.Instance_Number);

	public void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Management_2 frame = new Management_2();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public int getSelectedInstanceNumber() {
		return SelectedInstance.getSocketNum();
	}

	/**
	 * Create the frame.
	 */
	public Management_2() {
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage("D:\\\uD559\uAD50\\2020-2\\\uCEA1\uC2A4\uD1A4\\UI\\Provisioning\\image\\iGroom.jpg"));
		setBounds(100, 100, 424, 333);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(5, 46, 195, 196);
		contentPane.add(panel);
		panel.setLayout(null);

		lblNewLabel_1 = new JLabel("\uC778\uC2A4\uD134\uC2A4 IP");
		lblNewLabel_1.setFont(new Font("굴림", Font.BOLD, 12));
		lblNewLabel_1.setBounds(12, 10, 89, 15);
		panel.add(lblNewLabel_1);

		lblNewLabel_2 = new JLabel("\uC778\uC2A4\uD134\uC2A4 \uC0C1\uD0DC");
		lblNewLabel_2.setFont(new Font("굴림", Font.BOLD, 12));
		lblNewLabel_2.setBounds(12, 78, 89, 15);
		panel.add(lblNewLabel_2);

		lblNewLabel_3 = new JLabel("\uC778\uC2A4\uD134\uC2A4 \uC720\uD615");
		lblNewLabel_3.setFont(new Font("굴림", Font.BOLD, 12));
		lblNewLabel_3.setBounds(12, 143, 89, 15);
		panel.add(lblNewLabel_3);

		lblNewLabel_Instance_IP = new JLabel(SelectedInstance.getIp()); // 인스턴스 아이피 주소값
		lblNewLabel_Instance_IP.setBounds(12, 35, 137, 15);
		panel.add(lblNewLabel_Instance_IP);

		lblNewLabel_Instance_Static = new JLabel(SelectedInstance.getStatus()); // 인스턴스 상태
		lblNewLabel_Instance_Static.setBounds(12, 103, 89, 15);
		panel.add(lblNewLabel_Instance_Static);

		if(SelectedInstance.getProvider() == "AWS")
			lblNewLabel_Instance_Type = new JLabel(Main.getAWSInstance()); // 인스턴스 유형
		else if(SelectedInstance.getProvider() == "Azure")
			lblNewLabel_Instance_Type = new JLabel(Main.getAzureInstance()); // 인스턴스 유형
		else
			lblNewLabel_Instance_Type = new JLabel(Main.getGCPInstance()); // 인스턴스 유형
		lblNewLabel_Instance_Type.setBounds(12, 168, 150, 15);
		panel.add(lblNewLabel_Instance_Type);

		panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBounds(207, 46, 195, 196);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		lblNewLabel_4 = new JLabel("Cpu \uC0AC\uC6A9\uB7C9");
		lblNewLabel_4.setFont(new Font("굴림", Font.BOLD, 12));
		lblNewLabel_4.setBounds(12, 10, 86, 15);
		panel_1.add(lblNewLabel_4);

		lblNewLabel_5 = new JLabel("\uD504\uB85C\uBC14\uC774\uB354");
		lblNewLabel_5.setFont(new Font("굴림", Font.BOLD, 12));
		lblNewLabel_5.setBounds(12, 78, 100, 15);
		panel_1.add(lblNewLabel_5);

		lblNewLabel_CPU = new JLabel(SelectedInstance.getCpuHistory().getLast().toString() + " %"); // Cpu 사용량
		lblNewLabel_CPU.setBounds(12, 35, 100, 15);
		panel_1.add(lblNewLabel_CPU);

		JLabel lblNewLabel_Provider = new JLabel(SelectedInstance.getProvider()); // 프로바이더
		lblNewLabel_Provider.setBounds(12, 103, 89, 15);
		panel_1.add(lblNewLabel_Provider);

		chckbxNewCheckBox_1 = new JCheckBox("\uBAA8\uB2C8\uD130\uB9C1");
		chckbxNewCheckBox_1.setFont(new Font("굴림", Font.PLAIN, 13));
		chckbxNewCheckBox_1.setBounds(310, 17, 79, 23);
		contentPane.add(chckbxNewCheckBox_1);

		btnNext = new JButton("Next->");
		btnNext.setBounds(292, 261, 91, 23);
		contentPane.add(btnNext);

		btnPrevious = new JButton("<-Previous");
		btnPrevious.setHorizontalAlignment(SwingConstants.LEFT);
		btnPrevious.setBounds(181, 261, 99, 23);
		contentPane.add(btnPrevious);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("iGroom");
		setVisible(true);

		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {// UI_2으로 이동
				// TODO Auto-generated method stub

				if (chckbxNewCheckBox_1.isSelected() == true) {
					//dispose();

					t = new Thread(new Runnable() {
						Management_3 swingWorkerRealTime;
						@Override
						public void run() {
							swingWorkerRealTime = new Management_3(SelectedInstance.getCpuHistory());
							swingWorkerRealTime.go();
						}
					});
					t.start();
				}
			}
			
		});

		btnPrevious.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {// UI_2으로 이동
				// TODO Auto-generated method stub
				dispose();
				Management_1 m1 = new Management_1();
			}
		});
	}
}
