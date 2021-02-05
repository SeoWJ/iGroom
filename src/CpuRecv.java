import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.JOptionPane;

public class CpuRecv extends Thread {
	BufferedReader br;
	String ip;
	
	public CpuRecv(String ip, BufferedReader br) {
		this.ip = ip;
		this.br = br;
	}
	
	@Override
	public void run() {
		while(true) {
			if(br != null) {
				try {
					String line = br.readLine();
					System.out.println(ip + " : " + line);
				} catch (IOException e) {
					break;
				}
			}
		}
	}
}
