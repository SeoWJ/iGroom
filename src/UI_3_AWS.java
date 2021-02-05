import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JScrollBar;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

public class UI_3_AWS extends JFrame {

	private JPanel contentPane;
	static String AWS_instance; //아마존 인스턴스 타입 전역변수 -> 후에 Management_1 테이블 '유형'란에 들어갑니다
	/**
	 * 
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI_3_AWS frame = new UI_3_AWS();
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
	public UI_3_AWS() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("./image/iGroom1.jpg"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblProvider = new JLabel("\uC11C\uBC84 \uC0AC\uC591\uC744 \uC120\uD0DD\uD574 \uC8FC\uC138\uC694.");
		lblProvider.setFont(new Font("굴림", Font.BOLD, 18));
		lblProvider.setBounds(138, 30, 259, 24);
		contentPane.add(lblProvider);
		
		JComboBox InstanceType = new JComboBox();
		InstanceType.setModel(new DefaultComboBoxModel(new String[] {"t2.micro", "t4.micro"}));
		InstanceType.setBounds(139, 77, 91, 23);
		contentPane.add(InstanceType);
		
		JComboBox Image = new JComboBox();
		Image.setModel(new DefaultComboBoxModel(new String[] {"ami-40d28157", "ami-30d28157"}));
		Image.setBounds(139, 110, 91, 23);
		contentPane.add(Image);
		
		JComboBox Memory = new JComboBox();
		Memory.setModel(new DefaultComboBoxModel(new String[] {"1G", "2G", "3G", "4G"}));
		Memory.setBounds(139, 143, 91, 23);
		contentPane.add(Memory);
		
		JComboBox Storage = new JComboBox();
		Storage.setModel(new DefaultComboBoxModel(new String[] {"1G", "2G", "3G", "4G"}));
		Storage.setBounds(139, 176, 91, 23);
		contentPane.add(Storage);
		
		JLabel lblNewLabel = new JLabel("Instance Type");
		lblNewLabel.setBounds(40, 80, 85, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblImage = new JLabel("Image");
		lblImage.setBounds(40, 112, 85, 15);
		contentPane.add(lblImage);
		
		JLabel lblMemory = new JLabel("Memory");
		lblMemory.setBounds(40, 148, 85, 15);
		contentPane.add(lblMemory);
		
		JLabel lblStorage = new JLabel("Storage");
		lblStorage.setBounds(40, 182, 85, 15);
		contentPane.add(lblStorage);
		
		JButton btnPrevious = new JButton("<-Previous");
		btnPrevious.setHorizontalAlignment(SwingConstants.LEFT);
		btnPrevious.setBounds(222, 230, 99, 23);
		contentPane.add(btnPrevious);
		
		JButton btnNext = new JButton("Next->");
		btnNext.setBounds(333, 231, 91, 23);
		contentPane.add(btnNext);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setIcon(new ImageIcon("./image/AWS.png"));
		lblNewLabel_1.setBounds(12, 10, 105, 48);
		contentPane.add(lblNewLabel_1);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Provisioning ");
		setVisible(true);
		
		btnPrevious.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {//UI_2으로 이동
				// TODO Auto-generated method stub
				dispose();
				UI_2 f2= new UI_2();
			}
		});;

		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Main.setAWSInstance((String) InstanceType.getSelectedItem());

				if(UI_2.Provider_case==1) {//amazon+azure
					dispose();
					UI_3_Azure f3= new UI_3_Azure();
				}
				
				else if(UI_2.Provider_case==2) {//amazon+gcp				
					dispose();
					UI_3_GCP f3= new UI_3_GCP();
				}

				else if(UI_2.Provider_case==3) {//amazon+azure+gcp
					dispose();
					UI_3_Azure f3= new UI_3_Azure();
				}					

			}
		});;
	}
}
