package ld.socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LogonActivity extends Activity {

	private EditText edtUserName;
	private EditText edtIp;
	private EditText edtPort;

	private Button btnLogon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.logon);

		this.initViews();
	}

	private void initViews() {
		this.edtUserName = (EditText) this.findViewById(R.id.edtUserName);
		this.edtIp = (EditText) this.findViewById(R.id.edtIp);
		this.edtPort = (EditText) this.findViewById(R.id.edtPort);

		this.btnLogon = (Button) this.findViewById(R.id.btnLogon);
		this.btnLogon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// showAlert(edtUserName.getText().toString());
				if (edtUserName.getText().toString().equals("")) {
					showDialog("请输入用户名");
					return;
				}

				if (edtIp.getText().toString().equals("")) {
					showDialog("请输入IP地址");
					return;
				}

				if (edtPort.getText().toString().equals("")) {
					showDialog("请输入端口号");
					return;
				}

				int port = Integer.parseInt(edtPort.getText().toString());
				ChartInfo chartInfo = ChartInfo.getInstance();
				chartInfo.setIp(edtIp.getText().toString());
				chartInfo.setPort(port);
				SocketClient proxy = SocketClient.getInstance();

				if (proxy == null) {
					showDialog("未接入互联网");
					setWireless();
					return;
				}

				proxy.putHandler("Logon", new Handler() {
					@Override
					public void handleMessage(Message msg) {

						SocketClient proxy = SocketClient.getInstance();
						proxy.removeHandler("Logon");
						Log.d("initSocket", "handleMessage");
						if (msg == null || msg.obj == null) {
							return;
						}

						JSONObject json = (JSONObject) msg.obj;
						try {
							String userName = json.getString("UserName");
							Log.d("initSocket", "userName:" + userName);
							ChartInfo.getInstance().setUserName(userName);
							Intent itt = new Intent();
							itt
									.setClass(LogonActivity.this,
											MainActivity.class);
							LogonActivity.this.startActivity(itt);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				proxy.logon(edtUserName.getText().toString());
			}
		});
	}

	private void setWireless() {
		Intent mIntent = new Intent("/");
		ComponentName comp = new ComponentName("com.android.settings",
				"com.android.settings.WirelessSettings");
		mIntent.setComponent(comp);
		mIntent.setAction("android.intent.action.VIEW");
		startActivityForResult(mIntent, 0);
	}

	private void showDialog(String mess) {
		new AlertDialog.Builder(this).setTitle("信息").setMessage(mess)
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			AlertDialog alertDialog = new AlertDialog.Builder(
					LogonActivity.this).setTitle("退出程序").setMessage("是否退出程序")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									LogonActivity.this.finish();
								}

							}).setNegativeButton("取消",

					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					}).create(); // 创建对话框

			alertDialog.show(); // 显示对话框

			return false;

		}

		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SocketClient proxy = SocketClient.getInstance();
		if (proxy != null) {
			proxy.close();
		}
	}

}
