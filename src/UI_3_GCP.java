import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.DefaultComboBoxModel;

public class UI_3_GCP extends JFrame {

	private JPanel contentPane;
	static String GCP_instance; //GCP 인스턴스 타입 전역변수 -> 후에 Management_1 테이블 '유형'란에 들어갑니다

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI_3_GCP frame = new UI_3_GCP();
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
	public UI_3_GCP() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("./image/iGroom1.jpg"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setBounds(12, 6, 0, 0);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblProvider = new JLabel("\uC11C\uBC84 \uC0AC\uC591\uC744 \uC120\uD0DD\uD574 \uC8FC\uC138\uC694.");
		lblProvider.setFont(new Font("굴림", Font.BOLD, 18));
		lblProvider.setBounds(139, 31, 259, 24);
		contentPane.add(lblProvider);
		
		JComboBox InstanceType = new JComboBox();
		InstanceType.setModel(new DefaultComboBoxModel(new String[] {"e2-micro", "GCP2"}));
		InstanceType.setBounds(139, 77, 91, 23);
		contentPane.add(InstanceType);
		
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
		
		JComboBox Storage = new JComboBox();
		Storage.setModel(new DefaultComboBoxModel(new String[] {"1G", "2G", "3G", "4G"}));
		Storage.setBounds(139, 176, 91, 23);
		contentPane.add(Storage);
		
		JComboBox Memory = new JComboBox();
		Memory.setModel(new DefaultComboBoxModel(new String[] {"1G", "2G", "3G", "4G"}));
		Memory.setBounds(139, 143, 91, 23);
		contentPane.add(Memory);
		
		JComboBox Image = new JComboBox();
		Image.setModel(new DefaultComboBoxModel(new String[] {"GCP-ami-40d28157", "GCP-ami-30d28157"}));
		Image.setBounds(139, 110, 91, 23);
		contentPane.add(Image);
		
		JButton btnPrevious = new JButton("<-Previous");
		btnPrevious.setHorizontalAlignment(SwingConstants.LEFT);
		btnPrevious.setBounds(222, 230, 99, 23);
		contentPane.add(btnPrevious);
		
		JButton btnNext = new JButton("Next->");
		btnNext.setBounds(333, 231, 91, 23);
		contentPane.add(btnNext);
		
		JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setIcon(new ImageIcon("./image/GCP.png"));
		lblNewLabel_2.setBounds(40, -3, 131, 95);
		contentPane.add(lblNewLabel_2);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Provisioning ");
		setVisible(true);
		
		
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {//UI_2으로 이동
				// TODO Auto-generated method stub
				Main.setGCPInstance((String) InstanceType.getSelectedItem());
				dispose();
				UI_4 f3= new UI_4();
			}
		});;
		
	}

}
