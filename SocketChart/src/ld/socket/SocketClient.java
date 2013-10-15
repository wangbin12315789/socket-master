package ld.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SocketClient {

	private static Socket client;

	private static SocketClient instance = null;

	public static SocketClient getInstance() {
		if (instance == null) {

			synchronized (ChartInfo.class) {
				if (instance == null) {
					try {
						ChartInfo chartInfo = ChartInfo.getInstance();
						client = new Socket(chartInfo.getIp(), chartInfo
								.getPort());
						instance = new SocketClient();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				}
			}
		}
		return instance;
	}

	private SocketClient() {
		this.initMap();
		this.startThread();
	}

	private void initMap() {
		this.handlerMap = new HashMap<String, Handler>();
	}

	public void close() {
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		instance = null;
	}
	
	private void startThread() {
		Thread thread = new Thread() {
			@Override
			public void run() {

				while (true) {
					if (client == null || !client.isConnected()) {
						continue;
					}

					BufferedReader reader;
					try {
						reader = new BufferedReader(new InputStreamReader(
								client.getInputStream()));
						String line = reader.readLine();
						Log.d("initSocket", "line:" + line);
						if (line.equals("")) {
							continue;
						}

						JSONObject json = new JSONObject(line);
						String method = json.getString("Method");
						Log.d("initSocket", "method:" + method);
						if (method.equals("")
								|| !handlerMap.containsKey(method)) {
							Log.d("initSocket", "handlerMap not method");
							continue;
						}

						Handler handler = handlerMap.get(method);
						if (handler == null) {
							Log.d("initSocket", "handler is null");
							continue;
						}
						Log.d("initSocket", "handler:" + method);
						Object obj = json.getJSONObject("Result");
						Log.d("initSocket", "Result:" + obj);
						Message msg = new Message();
						msg.obj = obj;
						handler.sendMessage(msg);

					} catch (IOException e) {

					} catch (JSONException e) {

					}
				}
			}
		};
		thread.start();
	}

	private Map<String, Handler> handlerMap;

	public void putHandler(String methodnName, Handler handler) {
		this.removeHandler(methodnName);
		this.handlerMap.put(methodnName, handler);
	}

	public void removeHandler(String methodnName) {
		if (this.handlerMap.containsKey(methodnName)) {
			this.handlerMap.remove(methodnName);
		}
	}

	public void logon(String userName) {
		Log.d("initSocket", "logon");
		try {
			OutputStreamWriter osw = new OutputStreamWriter(client
					.getOutputStream());

			BufferedWriter writer = new BufferedWriter(osw);

			JSONObject param = new JSONObject();
			param.put("UserName", userName.replace("\n", " "));

			JSONObject json = this.getJSONData("Logon", param);

			writer.write(json.toString());
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		Log.d("initSocket", "Send");
		try {
			OutputStreamWriter osw = new OutputStreamWriter(client
					.getOutputStream());

			BufferedWriter writer = new BufferedWriter(osw);

			JSONArray array = new JSONArray();
			for (String item : message.split("\n")) {
				array.put(item);
			}

			JSONObject param = new JSONObject();
			param.put("Message", array);

			param.put("UserName", ChartInfo.getInstance().getUserName());

			JSONObject json = this.getJSONData("Send", param);

			writer.write(json.toString());
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private JSONObject getJSONData(String methodName, JSONObject param) {
		JSONObject json = new JSONObject();
		try {
			json.put("Method", methodName);
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			json.put("DateTime", format.format(new Date()));
			json.put("Param", param);
			return json;
		} catch (JSONException e) {
			return null;
		}
	}
}
