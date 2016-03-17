package org.imshello.droid.Screens;

import org.imshello.droid.R;
import org.imshello.droid.R.id;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BuddyDetail extends BaseScreen {
	TextView name,sex;
	View number,category,sip,photo;
	Button audioCall,videoCall,chat;
	String strName;
	String strPhone;
	public BuddyDetail(SCREEN_TYPE type, String id) {
		super(type, id);
		// TODO Auto-generated constructor stub
	}
	
	public BuddyDetail(){
		this(SCREEN_TYPE.BUDDY_DETAIL_T,"buudy_detail_id");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_buddy);
		initViews();
		strName=this.getIntent().getStringExtra("name");
		strPhone=this.getIntent().getStringExtra("number");
		name.setText(strName);
		sex.setText("");
		((TextView)number.findViewById(R.id.title)).setText("电话");
		((TextView)number.findViewById(R.id.content)).setText(strPhone);
		((TextView)category.findViewById(R.id.title)).setText("标签");
		((TextView)category.findViewById(R.id.content)).setText("通讯录联系人");
		((TextView)sip.findViewById(R.id.title)).setText("SIP");
		((TextView)sip.findViewById(R.id.content)).setText("无");
		((TextView)photo.findViewById(R.id.title)).setText("个人相册");
		((TextView)photo.findViewById(R.id.content)).setText("无");
		audioCall.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Dialog alertDialog = new AlertDialog.Builder(
							BuddyDetail.this).setTitle("提示").setMessage("非SIP用户，将发起普通电话，是否继续?")
							.setNegativeButton("否", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}})
								.setPositiveButton("是", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							    Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+strPhone));
							    
							    BuddyDetail.this.startActivity(intent);
							}})
							.create();
					alertDialog.show();
				}				
		});
		videoCall.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Dialog alertDialog = new AlertDialog.Builder(
						BuddyDetail.this).setTitle("提示").setMessage("非SIP用户，无法视频通话！").setNegativeButton("确定", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}})
						
						.create();
				alertDialog.show();
			}
			
		});
		chat.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Dialog alertDialog = new AlertDialog.Builder(
						BuddyDetail.this).setTitle("提示").setMessage("非SIP用户，无法进行聊天！").setNegativeButton("确定", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}})
						.create();
				alertDialog.show();
			}
			
		});
	}
	
	private void initViews(){
		name=(TextView) this.findViewById(id.contacts_linear_layout).findViewById(R.id.contacts_inner_layout).findViewById(R.id.name);
		sex=(TextView) this.findViewById(id.contacts_linear_layout).findViewById(R.id.contacts_inner_layout).findViewById(R.id.sex);
		number= this.findViewById(id.contacts_linear_layout2).findViewById(R.id.item_number);
		category= this.findViewById(id.contacts_linear_layout2).findViewById(R.id.item_category);
		sip= this.findViewById(id.contacts_linear_layout2).findViewById(R.id.item_sip);
		photo= this.findViewById(id.contacts_linear_layout2).findViewById(R.id.item_photo);
		audioCall= (Button) this.findViewById(id.contacts_linear_layout3).findViewById(R.id.buddy_detail_audio_call);
		videoCall= (Button) this.findViewById(id.contacts_linear_layout3).findViewById(R.id.buddy_detail_video_call);
		chat= (Button) this.findViewById(id.contacts_linear_layout3).findViewById(R.id.buddy_detail_message);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
		}
		return true;
	}
}
