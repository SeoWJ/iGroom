import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

public class UI_1 extends JFrame {

   private JPanel contentPane;

   /**
    * Launch the application.
    */
   public static void main(String[] args) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            try {
               UI_1 frame = new UI_1();
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
   public UI_1() {
      setIconImage(Toolkit.getDefaultToolkit().getImage("./image/iGroom1.jpg"));
      JCheckBox[] c = new JCheckBox[3]; 
      
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 387, 318);
      contentPane = new JPanel();
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);
      
      JLabel lblNewLabel = new JLabel("\u203B\uC0AC\uC6A9\uD558\uACE0\uC790 \uD558\uB294 \uC11C\uBE44\uC2A4\uB97C \uC120\uD0DD\uD574\uC8FC\uC138\uC694.");
      lblNewLabel.setBounds(23, 212, 264, 24);
      lblNewLabel.setFont(new Font("휴먼편지체", Font.BOLD, 13));
      contentPane.add(lblNewLabel);
      
      JCheckBox chckbxNewCheckBox = new JCheckBox("Provisioning");
      chckbxNewCheckBox.setFont(new Font("휴먼편지체", Font.PLAIN, 12));
      chckbxNewCheckBox.setBounds(23, 240, 109, 23);
      contentPane.add(chckbxNewCheckBox);
      
      JCheckBox chckbxManagement = new JCheckBox("Management");
      chckbxManagement.setFont(new Font("휴먼편지체", Font.PLAIN, 12));
      chckbxManagement.setBounds(136, 240, 107, 23);
      contentPane.add(chckbxManagement);
      
      ButtonGroup Group =  new ButtonGroup();
      Group.add(chckbxNewCheckBox);
      Group.add(chckbxManagement);
      
      JButton btnNewButton = new JButton("Next->");
      btnNewButton.setFont(UIManager.getFont("Button.font"));
      btnNewButton.setBounds(277, 248, 91, 23);
      contentPane.add(btnNewButton);
      
      JLabel lblNewLabel_1 = new JLabel("");
      lblNewLabel_1.setIcon(new ImageIcon("./image/iGroom2.png"));
      lblNewLabel_1.setBounds(0, 0, 380, 202);
      contentPane.add(lblNewLabel_1);
      
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setTitle("iGroom");
      setVisible(true);
      
      
      btnNewButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if(chckbxNewCheckBox.isSelected()==true) { //Provisioning란이 체크 되었을 경우
               dispose(); //기존 창 닫아주는 기능
               UI_2 f2= new UI_2(); //UI_2 창 새로 생성
               
            }
            else if(chckbxManagement.isSelected()==true) { //Management란이 체크 되었을 경우
            	BufferedReader inputFile;
            	
            	try {
					inputFile = new BufferedReader(new FileReader("sys_data.bin"));
					String str;
					int eAWS, eAzure, eGCP;
					
					str = inputFile.readLine();
					eAWS = Integer.parseInt(str);
					str = inputFile.readLine();
					eAzure = Integer.parseInt(str);
					str = inputFile.readLine();
					eGCP = Integer.parseInt(str);
					
					Main.enableProvider(eAWS, eAzure, eGCP);
					
					Main.setAWSInstance(inputFile.readLine());
					Main.setAzureInstance(inputFile.readLine());
					Main.setGCPInstance(inputFile.readLine());
					
					Main.setMinInstance(Integer.parseInt(inputFile.readLine()));
					Main.setMaxInstance(Integer.parseInt(inputFile.readLine()));
					
					Main.setDatabaseAddress(inputFile.readLine());
					
					inputFile.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	
               dispose();
               Management_1 m1 = new Management_1();
            }
         }
      });;
      
      
      
   }
}
