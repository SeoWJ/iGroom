import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JCheckBox;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Toolkit;


public class UI_2 extends JFrame {

	public static int Provider_case; //â �̵� �����ϰ� �ϱ� ���� ���� ���̽��� ������ ��������ϴ�.
									//���ι��̴� üũ �� next ��ư ������ ������ ���� �Էµǵ��� �����ϰ� (1,2,3...)
									//�Ŀ� �� ���� �������� ���ι��̴� ������� ����â�� �ߵ��� ��������ϴ�.
									//���� �� ���̽� ������ �������� Management_1�� �ν��Ͻ����� ��µ˴ϴ�.
									
									//���̽��� �Ʒ����� next�� �׼Ǹ����� �κ� ���ø� �� �� �ְ�����
									//Provider_case=1; -> amazon+azure�� üũ�� ���
									//Provider_case=2; -> amazon+GCP�� üũ�� ���	
									//Provider_case=3; -> amazon+azure+GCP�� üũ�� ���
									//�̷��� �� ���� ���������ϴ�
	
	public class AddAws extends JPanel {public void paintComponet(Graphics g) {
		Dimension d = getSize();
		ImageIcon image = new ImageIcon("./image/cloud.jpg");
		g.drawImage(image.getImage(),1,1,d.width,d.height,null);
	}
}
	private JPanel contentPane;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI_2 frame = new UI_2();
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
	public UI_2() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("./image/iGroom1.jpg"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		 JLabel lblProvider = new JLabel("\u203BProvider\uB97C \uC120\uD0DD\uD574 \uC8FC\uC138\uC694.");
	      lblProvider.setBounds(10, 10, 311, 24);
	      lblProvider.setFont(new Font("�޸�����ü", Font.PLAIN, 18));
	      contentPane.add(lblProvider);
	      
	      JCheckBox chckbxNewCheckBox_Amazon = new JCheckBox("Amazon - AWS");
	      chckbxNewCheckBox_Amazon.setFont(new Font("�޸�����ü", Font.BOLD, 12));
	      chckbxNewCheckBox_Amazon.setBounds(22, 166, 119, 23);
	      contentPane.add(chckbxNewCheckBox_Amazon);
	      
	      JCheckBox chckbxNewCheckBox_Azure = new JCheckBox("Microsoft - Azure");
	      chckbxNewCheckBox_Azure.setFont(new Font("�޸�����ü", Font.BOLD, 12));
	      chckbxNewCheckBox_Azure.setBounds(175, 166,127, 23);
	      contentPane.add(chckbxNewCheckBox_Azure);
	      
	      JCheckBox chckbxNewCheckBox_GCP = new JCheckBox("Google - GCP");
	      chckbxNewCheckBox_GCP.setFont(new Font("�޸�����ü", Font.BOLD, 12));
	      chckbxNewCheckBox_GCP.setBounds(322, 166,110, 23);
	      contentPane.add(chckbxNewCheckBox_GCP);
		
		JButton btnNext = new JButton("Next->");
		btnNext.setBounds(333, 230, 91, 23);
		contentPane.add(btnNext);
		
		JButton btnPrevious = new JButton("<-Previous");
		btnPrevious.setHorizontalAlignment(SwingConstants.LEFT);
		btnPrevious.setBounds(222, 230, 99, 23);
		contentPane.add(btnPrevious);
		
		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setIcon(new ImageIcon("./image/provider_all.png"));
		lblNewLabel.setBounds(10, 78, 436, 100);
		contentPane.add(lblNewLabel);


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Provisioning ");
		setVisible(true);
		
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int amazon = 0;
				int selected = 0;
				
				if(chckbxNewCheckBox_Amazon.isSelected()) {
					amazon++;
					selected++;
				}
				if(chckbxNewCheckBox_Azure.isSelected()) selected++;
				if(chckbxNewCheckBox_GCP.isSelected()) selected++;
				
				if(selected == 0)
					JOptionPane.showMessageDialog(null, "���ι��̴��� �������ֽʽÿ�.");
				else if(selected == 1)
					JOptionPane.showMessageDialog(null, "2�� �̻��� ���ι��̴��� �������ּ���."); 
				else {
					if(amazon == 0)
						JOptionPane.showMessageDialog(null, "�Ƹ����� �ݵ�� �������ּ���.");
					else if(chckbxNewCheckBox_Amazon.isSelected()==true && chckbxNewCheckBox_Azure.isSelected()==true && 
							chckbxNewCheckBox_GCP.isSelected()==false) {
						Provider_case=1; //amazon+azure�� üũ�� ���
						dispose();
						Main.enableProvider(1,1,0);
						UI_3_AWS f3= new UI_3_AWS();
					}
					
					else if(chckbxNewCheckBox_Amazon.isSelected()==true && chckbxNewCheckBox_Azure.isSelected()==false && 
							chckbxNewCheckBox_GCP.isSelected()==true) {
						Provider_case=2; //amazon+gcp�� üũ�� ���
						dispose();
						Main.enableProvider(1,0,1);
						UI_3_AWS f3= new UI_3_AWS();
					}
					
					else if(chckbxNewCheckBox_Amazon.isSelected()==true && chckbxNewCheckBox_Azure.isSelected()==true && 
							chckbxNewCheckBox_GCP.isSelected()==true) {
						Provider_case=3; //amazon+azure+gcp�� üũ�� ���
						dispose();
						Main.enableProvider(1,1,1);
						UI_3_AWS f3= new UI_3_AWS();
					}		
				}
			}
		});;
		
		btnPrevious.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {//UI_2���� �̵�
				// TODO Auto-generated method stub
				dispose();
				UI_1 f= new UI_1();
			}
		});;
		
	}
}
