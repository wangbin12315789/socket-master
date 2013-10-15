package ld.socket;

public class ChartInfo {

	private String userName;

	private String ip;

	private int port;

	private ChartInfo() {
	}

	public String getUserName() {
		return userName;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private static ChartInfo instance = null;

	public static ChartInfo getInstance() {
		if (instance == null) {

			synchronized (ChartInfo.class) {
				if (instance == null) {
					instance = new ChartInfo();
				}
			}
		}
		return instance;
	}
}
