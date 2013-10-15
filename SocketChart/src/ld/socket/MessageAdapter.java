package ld.socket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessageAdapter extends BaseAdapter {

	private List<MessageRecord> messageList = new ArrayList<MessageRecord>();

	private LayoutInflater inflater;

	public MessageAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	public void add(MessageRecord record) {
		record.setDate(new Date());
		messageList.add(record);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return messageList.size();
	}

	@Override
	public Object getItem(int location) {
		// TODO Auto-generated method stub
		return messageList.get(location);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return messageList.indexOf(messageList.get(position));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		try {
			MessageRecord record = messageList.get(position);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.listview_item, null);
				convertView.setTag(record);
			}

			Log.d("MessageAdapter", "msg:" + record.getMessage());

			TextView itmMessage = (TextView) convertView
					.findViewById(R.id.itmMessage);
			itmMessage.setText(record.getMessage());
			
			TextView itmUserName = (TextView) convertView
			.findViewById(R.id.itmUserName);
			itmUserName.setText(record.getUserName());

			TextView itmTime = (TextView) convertView
					.findViewById(R.id.itmTime);
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			itmTime.setText(format.format(record.getDate()));

			return convertView;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

}
