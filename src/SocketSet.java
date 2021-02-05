import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import javax.swing.SwingWorker;

public class SocketSet extends SwingWorker<Void, Void> {
	private Socket socket;
	private String ip;
	private BufferedReader br;
	private PrintWriter pw;
	private String provider;
	private int socketnum;
	private LinkedList<Double> CpuHistory = new LinkedList<Double>();
	private String status;

	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		status = "½ÇÇà Áß";
		for(int i=0; i<50; i++)
			CpuHistory.add(0.0);
		
		while (true) {
			if (br != null) {
				try {
					String line = br.readLine();
					System.out.println(ip + " : " + line);
					if (line == null) {
						Main.downConnectedProvider(provider);
						Main.getDeadIp().add(ip);
						status = "Á¾·áµÊ";
						Management_1.contents[socketnum][2] = status;
						Management_1.contents[socketnum][3] = "-";
						break;
					}
					
					Management_1.contents[socketnum][0] = ip;
					Management_1.contents[socketnum][1] = provider;
					Management_1.contents[socketnum][2] = status;
					Management_1.contents[socketnum][3] = line + " %";
					
					CpuHistory.add(Double.parseDouble(line));
					if(CpuHistory.size() > 50)
						CpuHistory.removeFirst();
				} catch (IOException e) {
					//Main.getSocketList().remove(socketnum);
					Main.downConnectedProvider(provider);
					Main.getDeadIp().add(ip);
					status = "Á¾·áµÊ";
					Management_1.contents[socketnum][2] = status;
					Management_1.contents[socketnum][3] = "-";
					break;
				}
			}
			else {
				Main.downConnectedProvider(provider);
				Main.getDeadIp().add(ip);
				Management_1.contents[socketnum][2] = "Á¾·áµÊ";
				Management_1.contents[socketnum][3] = "-";
				break;
			}
		}
		return null;
	}

	public SocketSet() {
		this.socket = null;
		this.ip = null;
		this.br = null;
		this.pw = null;
	}

	public SocketSet(String address, int portNum, int socketNum, String provider) throws Exception {
		try {
			setSocket(address, portNum);
			setBufferedReader();
			setPrintWriter();
			setIp(address);
			setSocketNum(socketNum);
			setProvider(provider);

			Main.upConnectedProvider(provider);
		} catch (Exception e) {
			throw e;
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public BufferedReader getBufferedReader() {
		return br;
	}

	public PrintWriter getPrintWriter() {
		return pw;
	}

	public String getIp() {
		return ip;
	}

	public int getSocketNum() {
		return socketnum;
	}
	
	public String getProvider() {
		return provider;
	}

	public void setSocketNum(int socketNum) {
		this.socketnum = socketNum;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setSocket(String address, int portNum) throws Exception {
		socket = new Socket();
		SocketAddress socketAddress = new InetSocketAddress(address, portNum);
		socket.connect(socketAddress, 1000);
	}

	public void setBufferedReader() throws Exception {
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void setPrintWriter() throws Exception {
		pw = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
	}
	
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public LinkedList<Double> getCpuHistory(){
		return CpuHistory;
	}

	public String getStatus() {
		return status;
	}
}
