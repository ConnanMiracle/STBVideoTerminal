package org.imshello.droid.Utils;
import java.util.ArrayList;

import org.imshello.droid.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CallRecordAdapterNew extends BaseAdapter{

	private LayoutInflater mInflater;
	private ArrayList<CallRecord> callRecords;
	private Context context;
	
	public CallRecordAdapterNew(Context context,ArrayList<CallRecord> callRecords) {
		this.context = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.callRecords = callRecords;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub		
		return callRecords.size();		
	}
	
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(callRecords == null){
			return null;
		}
		CallRecordHolder callRecordHolder = null;
		if (convertView == null) {
			callRecordHolder = new CallRecordHolder();
			convertView = mInflater.inflate(R.layout.phone_call_record_list_item, null);
			callRecordHolder.userIcon = (ImageView) convertView.findViewById(R.id.user_icon);
			callRecordHolder.name = (TextView) convertView.findViewById(R.id.contact_name);
			callRecordHolder.statusIcon = (ImageView) convertView.findViewById(R.id.status_icon);
			callRecordHolder.time = (TextView) convertView.findViewById(R.id.time);		
			callRecordHolder.numType = (TextView) convertView.findViewById(R.id.num_type);	
			callRecordHolder.phoneNum = (TextView) convertView.findViewById(R.id.contact_phone_num);		
			convertView.setTag(callRecordHolder);
		} else {
			callRecordHolder = (CallRecordHolder)convertView.getTag();
		}
		
		CallRecord callRecord = callRecords.get(position);
		//设置用户头像
		if(position%2 == 0){
			callRecordHolder.userIcon.setImageResource(R.drawable.occupying_one);	
		} else {
			callRecordHolder.userIcon.setImageResource(R.drawable.occupying_two);	
		}

		//选择通话记录状态
		if(callRecord.callStatus == CallRecord.RECEIVED_CALL){
			callRecordHolder.statusIcon.setImageResource(R.drawable.callin);
		} else if(callRecord.callStatus == CallRecord.MISSED_CALL){
			callRecordHolder.statusIcon.setImageResource(R.drawable.missed);
		} else {
			callRecordHolder.statusIcon.setImageResource(R.drawable.callout);
		}
		//选中号码类型
		if(callRecord.numType == CallRecord.MOBILE_PHONE){
			callRecordHolder.numType.setText(context.getResources().getText(R.string.mobile_phone));
		} else {
			callRecordHolder.numType.setText(context.getResources().getText(R.string.telephone));
		}
		String callTime = CommonFunction.getTime(callRecord.time, "HH:mm");
		callRecordHolder.time.setText(callTime);
		callRecordHolder.name.setText(callRecord.name);
		callRecordHolder.phoneNum.setText(callRecord.num);
		return convertView;
	}
	
	public void setcallRecordsData(ArrayList<CallRecord> callRecords){
		this.callRecords = callRecords;
	}
	
	public void clear(){
		if(callRecords != null && !callRecords.isEmpty()){
			callRecords.clear();
		}
	}
	
	private class CallRecordHolder{
		private ImageView userIcon;
		private TextView name;
		private ImageView statusIcon;
		private TextView time;
		private TextView numType;
		private TextView phoneNum;
				
	}
}