import java.util.Vector;
import java.sql.*;
import java.util.*;

public class Main {
	private static int EnableAWS;
	private static int EnableAzure;
	private static int EnableGCP;
	
	private static String InstanceAWS;
	private static String InstanceAzure;
	private static String InstanceGCP;
	
	private static int min_instance;
	private static int max_instance;
	
	private static String DatabaseAddress;
	private static Connection DatabaseConnection = null;
	private static int ConnectedAWS = 0;
	private static int ConnectedAzure = 0;
	private static int ConnectedGCP = 0;
	
	private static Vector<SocketSet> SocketList = new Vector<SocketSet>();
	private static List<String> AliveIp = new ArrayList<String>();
	private static List<String> DeadIp = new ArrayList<String>();
	
	public static void main(String[] args) {//로그인화면으로 실행
		// TODO Auto-generated method stub
		UI_1 f = new UI_1();
	}
	
	public static void enableProvider(int aws, int azure, int gcp) {
		EnableAWS = aws;
		EnableAzure = azure;
		EnableGCP = gcp;
	}
	
	public static int getEnableAWS() { return EnableAWS; }
	public static int getEnableAzure() { return EnableAzure; }
	public static int getEnableGCP() { return EnableGCP; }
	
	public static void setAWSInstance(String awsInstance) {	InstanceAWS = awsInstance; }
	public static void setAzureInstance(String azureInstance) { InstanceAzure = azureInstance; }
	public static void setGCPInstance(String gcpInstance) { InstanceGCP = gcpInstance; }

	public static String getAWSInstance() { return InstanceAWS; }
	public static String getAzureInstance() { return InstanceAzure; }
	public static String getGCPInstance() { return InstanceGCP; }
	
	public static void setMinInstance(int min) { min_instance = min; }
	public static void setMaxInstance(int max) { max_instance = max; }
	
	public static int getMinInstance() { return min_instance; }
	public static int getMaxInstance() { return max_instance; }
	
	public static void setDatabaseAddress(String IP) { DatabaseAddress = IP; }
	
	public static String getDatabaseAddress() { return DatabaseAddress; }
	
	public static Vector<SocketSet> getSocketList() { return SocketList; }

	public static Connection getDatabaseConnection() {
		return DatabaseConnection;
	}

	public static void setDatabaseConnection(String url) {
		try {
			DatabaseConnection = DriverManager.getConnection(url, "client", "1234");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getConnectedAWS() {
		return ConnectedAWS;
	}
	
	public static int getConnectedAzure() {
		return ConnectedAzure;
	}
	
	public static int getConnectedGCP() {
		return ConnectedGCP;
	}
	
	public static void upConnectedProvider(String provider) {
		if(provider == "AWS")
			ConnectedAWS++;
		if(provider == "Azure")
			ConnectedAzure++;
		if(provider == "GCP")
			ConnectedGCP++;
	}
	
	public static void downConnectedProvider(String provider) {
		if(provider == "AWS")
			ConnectedAWS--;
		if(provider == "Azure")
			ConnectedAzure--;
		if(provider == "GCP")
			ConnectedGCP--;
	}

	public static List<String> getDeadIp() {
		return DeadIp;
	}

	public static List<String> getAliveIp() {
		return AliveIp;
	}
}
