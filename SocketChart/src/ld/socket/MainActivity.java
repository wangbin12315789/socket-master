package ld.socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {

	private EditText edtMessage;

	private Button btnSend;

	private ListView ltvMessage;

	private MessageAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// 隐藏键盘
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		Log.d("initSocket", "MessageAdapter");
		this.adapter = new MessageAdapter(this);
		Log.d("initSocket", "adapter is ok");

		this.findThisViews();
		this.initHandler();

		this.serOnClick();

		Log.d("initSocket", "onCreate");
	}

	private void findThisViews() {
		this.edtMessage = (EditText) this.findViewById(R.id.edtMessage);
		this.btnSend = (Button) this.findViewById(R.id.btnSend);

		this.ltvMessage = (ListView) this.findViewById(R.id.ltvMessage);
		// this.ltvMessage.setEnabled(false);
		this.ltvMessage.setAdapter(this.adapter);
	}

	private void initHandler() {

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.obj == null) {
					Log.d("initSocket", "handleMessage is null");
					return;
				}

				Log.d("initSocket", "handleMessage");
				try {
					JSONObject json = (JSONObject) msg.obj;
					String userName = json.getString("UserName");

					StringBuilder sb = new StringBuilder();
					int length = json.getJSONArray("Message").length();
					for (int i = 0; i < length; i++) {
						String item = json.getJSONArray("Message").getString(i);
						if (item.equals("")) {
							continue;
						}
						if (length > i + 1) {
							Log.d("initSocket", "length:" + length);
							Log.d("initSocket", "i:" + i);
							Log.d("initSocket", "item:" + item);
							item += "\n";
						}
						sb.append(item);
					}

					MessageRecord record = new MessageRecord();
					record.setUserName(userName);
					record.setMessage(sb.toString());

					MainActivity.this.adapter.add(record);
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		SocketClient proxy = SocketClient.getInstance();
		proxy.putHandler("Send", handler);
	}

	private void serOnClick() {
		this.btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnSend.setEnabled(false);
				String txt = edtMessage.getText().toString();
				if (txt.equals("")) {
					btnSend.setEnabled(true);
					return;
				}
				SocketClient proxy = SocketClient.getInstance();
				proxy.sendMessage(txt);
				edtMessage.setText("");
				btnSend.setEnabled(true);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			AlertDialog alertDialog = new AlertDialog.Builder(
					MainActivity.this).setTitle("询问").setMessage("是否注销登录？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									MainActivity.this.finish();
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
}