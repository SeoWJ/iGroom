import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Toolkit;

public class UI_4 extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	
	public static int min_instance; //최소 인스턴스 개수 -> Management_1에서 인스턴스란에 이 값으로 각 프로바이더의 인스턴스들이 생성됨
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI_4 frame = new UI_4();
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
	public UI_4() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("D:\\\uD559\uAD50\\2020-2\\\uCEA1\uC2A4\uD1A4\\UI\\Provisioning\\image\\cloud.jpg"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblProvisioning_1 = new JLabel("\uC790\uB3D9 \uC2A4\uCF00\uC77C\uB9C1 \uADF8\uB8F9 \uC0AC\uC774\uC988\uB97C \uC120\uD0DD\uD574\uC8FC\uC138\uC694");
		lblProvisioning_1.setFont(new Font("굴림", Font.BOLD, 13));
		lblProvisioning_1.setBounds(95, 33, 274, 24);
		contentPane.add(lblProvisioning_1);
		
		JLabel lblProvisioning_1_1 = new JLabel("\u203B\uC0AC\uC6A9\uC790 \uC124\uC815\uC5D0 \uB530\uB77C \uC790\uB3D9\uC73C\uB85C \uC778\uC2A4\uD134\uC2A4\uB97C \uD655\uC7A5\uD558\uAC70\uB098 \uCD95\uC18C\uD569\uB2C8\uB2E4.\r\n");
		lblProvisioning_1_1.setFont(new Font("굴림", Font.PLAIN, 11));
		lblProvisioning_1_1.setBounds(59, 54, 412, 24);
		contentPane.add(lblProvisioning_1_1);
		
		textField = new JTextField();
//		textField.setText(min_instance);
		textField.setBounds(110, 88, 54, 21);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBounds(200, 88, 54, 21);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblScaleBetween = new JLabel("Scale between");
		lblScaleBetween.setFont(new Font("굴림", Font.PLAIN, 13));
		lblScaleBetween.setBounds(12, 88, 110, 24);
		contentPane.add(lblScaleBetween);
		
		JLabel lblAnd = new JLabel("and\r\n");
		lblAnd.setFont(new Font("굴림", Font.PLAIN, 13));
		lblAnd.setBounds(169, 88, 30, 24);
		contentPane.add(lblAnd);
		
		JLabel lblInstances = new JLabel("Instances.");
		lblInstances.setFont(new Font("굴림", Font.PLAIN, 13));
		lblInstances.setBounds(266, 88, 100, 24);
		contentPane.add(lblInstances);
		
		JLabel lblTheseWillBe = new JLabel("These will be the minimum and maximum size of your group.");
		lblTheseWillBe.setFont(new Font("굴림", Font.PLAIN, 13));
		lblTheseWillBe.setBounds(12, 114, 357, 24);
		contentPane.add(lblTheseWillBe);
		
		JLabel lblTheseWillBe_1 = new JLabel("\u203B \uD2B8\uB798\uD53D \uC591\uC5D0 \uB530\uB77C \uC778\uC2A4\uD134\uC2A4\uB97C \uCD94\uAC00 \uD558\uAC70\uB098, \uBE44\uC815\uC0C1\uC801\uC778 \uC778\uC2A4\uD134\uC2A4\uB97C \uAD50\uCCB4\uD569\uB2C8\uB2E4.");
		lblTheseWillBe_1.setFont(new Font("굴림", Font.PLAIN, 11));
		lblTheseWillBe_1.setBounds(12, 161, 424, 24);
		contentPane.add(lblTheseWillBe_1);
		
		JLabel lblTheseWillBe_2 = new JLabel("\u203B \uAC01 \uD504\uB85C\uBC14\uC774\uB354\uB2F9 minimum size\uB85C \uCD08\uAE30 \uC0DD\uC131\uB429\uB2C8\uB2E4.");
		lblTheseWillBe_2.setFont(new Font("굴림", Font.PLAIN, 11));
		lblTheseWillBe_2.setBounds(12, 182, 357, 24);
		contentPane.add(lblTheseWillBe_2);
										
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Provisioning ");
		setVisible(true);
		
		
		JButton btnPrevious = new JButton("<-Previous");
		btnPrevious.setHorizontalAlignment(SwingConstants.LEFT);
		btnPrevious.setBounds(222, 230, 99, 23);
		contentPane.add(btnPrevious);
		
		JButton btnNext = new JButton("Next->");
		btnNext.setBounds(333, 231, 91, 23);
		contentPane.add(btnNext);

	
	btnPrevious.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			dispose();
			UI_3_AWS f2= new UI_3_AWS();
		}
	});;
	
	
	btnNext.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(textField.getText().isEmpty()==true || textField_1.getText().isEmpty()==true) {//아무 값도 입력하지 않았을 경우 띄워지는 알림창
				JOptionPane.showMessageDialog(null, "자동 스케일링 그룹 사이즈를 선택해주세요.");	
			}
			else if(isNumeric(textField.getText())==false || isNumeric(textField_1.getText())==false ) {//숫자가 아닌 값을 입력한 경우 띄워지는 알림창
				JOptionPane.showMessageDialog(null, "숫자를 입력하세요.");	

			}
			else if( Integer.parseInt(textField.getText()) >Integer.parseInt(textField_1.getText())) {//min에 입력한 값이 max에 입력한 값보다 큰 경우 띄워지는 알림창
				JOptionPane.showMessageDialog(null, "최소값과 최대값을 알맞게 입력하세요.");	

			}
			else {
			Main.setMinInstance(Integer.parseInt(textField.getText()));
			Main.setMaxInstance(Integer.parseInt(textField_1.getText()));
			dispose();
			UI_5_Progress m1 = new UI_5_Progress();
			}
		}
	});;
	}
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public static boolean isNumeric(String s) {	//숫자 판별해주는 함수
		  try {
		      Double.parseDouble(s);
		      return true;
		  } catch(NumberFormatException e) {
		      return false;
		  }
		}

}

