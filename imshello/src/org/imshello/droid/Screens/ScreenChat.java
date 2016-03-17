/* Copyright (C) 2010-2011, Mamadou Diop.
 *  Copyright (C) 2011, imshello Telecom.
 *
 * Contact: Mamadou Diop <diopmamadou(at)imshello(dot)org>
 *	
 * This file is part of imsdroid Project (http://code.google.com/p/imsdroid)
 *
 * imsdroid is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 *	
 * imsdroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.imshello.droid.Screens;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.imshello.droid.Engine;
import org.imshello.droid.R;
import org.imshello.droid.Utils.DateTimeUtils;
import org.imshello.droid.Utils.DisplayUtils;
import org.imshello.droid.Utils.EmotionUtils;
import org.imshello.droid.Utils.Emotions;
import org.imshello.droid.Utils.FileChooserDialog;
import org.imshello.droid.Utils.MyChatService;
import org.imshello.ngn.NgnEngine;
import org.imshello.ngn.media.NgnMediaType;
import org.imshello.ngn.model.NgnContact;
import org.imshello.ngn.model.NgnHistoryEvent;
import org.imshello.ngn.model.NgnHistorySMSEvent;
import org.imshello.ngn.model.NgnHistoryEvent.StatusType;
import org.imshello.ngn.services.INgnContactService;
import org.imshello.ngn.services.INgnHistoryService;
import org.imshello.ngn.services.INgnSipService;
import org.imshello.ngn.sip.NgnMessagingSession;
import org.imshello.ngn.sip.NgnMsrpSession;
import org.imshello.ngn.utils.NgnPredicate;
import org.imshello.ngn.utils.NgnStringUtils;
import org.imshello.ngn.utils.NgnUriUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ScreenChat extends BaseScreen {
	private static String TAG = ScreenChat.class.getCanonicalName();
	private static final int REQUEST_FILE = 1;
	private static final int REQUEST_IMAGE = 2;
	
	private final INgnHistoryService mHistorytService;
	private final INgnSipService mSipService;
	private final INgnContactService mContactService1;
	private InputMethodManager mInputMethodManager;

	private static String sRemoteParty;
	
	private static Context screenChatContext;
	private NgnMsrpSession mSession;
	private NgnMediaType mMediaType;
	private ScreenChatAdapter mAdapter;
	private EditText mEtCompose;
	private ListView mLvHistoy;
	private Button mBtVoiceCall;
	private Button mBtVisioCall;
	private Button mBtShare;
	private Button mBtSend;
	private Button mBtnVoice;
	private ImageButton mFileTrans;
	private ImageButton mImageTrans;
	private ImageView mBtnSwitch;
	private static MyChatService mChatService;
	private MyHandler mHandler;
	private static String mFilePath;

	private Emotions emotionClass;
	private Map<String, String> emotionsMap;
	private ImageButton emotion;
	private int[] emotion_array;
	private GridView emotionView;
	private static EmotionUtils emotionUtils;
	
	private ImageButton mBtnAddFeature;
	private View AddFeatureView;
	
	private TextView user;
	private ImageView call;
	private ImageView back;

	private static String username;
	private static String remote;

	public ScreenChat() {
		super(SCREEN_TYPE.CHAT_T, TAG);
		mMediaType = NgnMediaType.None;
		mHistorytService = getEngine().getHistoryService();
		mSipService = getEngine().getSipService();
		mContactService1 = getEngine().getContactService();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_chat);
		mHandler=new MyHandler();
		mChatService=new MyChatService(this,mHandler);
		mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		screenChatContext=ScreenChat.this;
		emotionUtils = new EmotionUtils();
		emotionClass = new Emotions();
		emotion_array = emotionClass.emotion;
		emotion = (ImageButton) findViewById(R.id.btn_emotion);
		emotion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				AddFeatureView.setVisibility(View.GONE);
				displayEmotionSelectView();
			}
		});
		emotionView = (GridView) findViewById(R.id.emotion_grid_view);
		emotionView.setAdapter(new GridAdapter(ScreenChat.this));
		// 选择表情
		emotionView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				emotionsMap = emotionClass.getEmotionsList_image();
				addEmotionImageToEditText(position);
			}
		});
		
		mBtnSwitch=(ImageView)this.findViewById(R.id.btn_audio_speak_switch);
		mBtnSwitch.setOnClickListener(new OnClickListener(){
			int count=0;
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				if(count%2==0){
					mEtCompose.setVisibility(View.GONE);
					mBtnVoice.setVisibility(View.VISIBLE);
					mBtnSwitch.setImageResource(R.drawable.keyboard);
				}else{
					mEtCompose.setVisibility(View.VISIBLE);
					mBtnVoice.setVisibility(View.GONE);
					mBtnSwitch.setImageResource(R.drawable.speak);
				}
				count++;
				
			}
			
		});

		mEtCompose = (EditText) findViewById(R.id.screen_chat_editText_compose);
		// mBtVoiceCall = (Button)
		// findViewById(R.id.screen_chat_button_voice_call);
		// mBtVisioCall = (Button)
		// findViewById(R.id.screen_chat_button_visio_call);
		// mBtShare = (Button)
		// findViewById(R.id.screen_chat_button_share_content);
		mBtSend = (Button) findViewById(R.id.screen_chat_button_send);
		mLvHistoy = (ListView) findViewById(R.id.screen_chat_listView);
		call = (ImageView) findViewById(R.id.call);
		back = (ImageView) findViewById(R.id.back);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mScreenService.show(ScreenMain.class);
			}
		});
		// 显示对话者的姓名
		user = (TextView) findViewById(R.id.name);

		NgnContact contact = mContactService1.getContactByPhoneNumber(remote);
		if (contact == null) {
			if (remote.contains("sip")) {
				String a1[] = remote.split(":");
				String a2[] = a1[1].split("@");
				user.setText(a2[0]);
			} else {
				user.setText(remote);
			}
		} else {
			username = contact.getDisplayName();
			user.setText(username);
		}

		mAdapter = new ScreenChatAdapter(this);
		mLvHistoy.setAdapter(mAdapter);
		mLvHistoy.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		mLvHistoy.setStackFromBottom(true);

		call.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mSipService.isRegistered()) {
					if (!NgnStringUtils.isNullOrEmpty(sRemoteParty)) {
						ScreenAV.makeCall(sRemoteParty, NgnMediaType.Audio);
						// added by song 将通话记录添加到信息记录中 2014.7.8
						final NgnHistorySMSEvent e = new NgnHistorySMSEvent(
								sRemoteParty, StatusType.Failed);
						e.setContent("语音通话");
						e.setStartTime(new Date().getTime());
						mHistorytService.addEvent(e);
					}
				} else {// 未登录时提示登录 by wangy 20140620
					Dialog alertDialog = new AlertDialog.Builder(
							ScreenChat.this).setTitle("提示").setMessage("请先登录！")
							.create();
					alertDialog.show();
				}
			}

		});
		/*
		 * mBtVoiceCall.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * if(mSipService.isRegistered()){
		 * if(!NgnStringUtils.isNullOrEmpty(sRemoteParty)){
		 * ScreenAV.makeCall(sRemoteParty, NgnMediaType.Audio); } } else
		 * {//未登录时提示登录 by wangy 20140620 Dialog alertDialog=new
		 * AlertDialog.Builder(ScreenChat.this) .setTitle("提示")
		 * .setMessage("请先登录！") .create(); alertDialog.show(); } } });
		 */
		/*
		 * mBtVisioCall.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * if(mSipService.isRegistered()){
		 * if(!NgnStringUtils.isNullOrEmpty(sRemoteParty)){
		 * ScreenAV.makeCall(sRemoteParty, NgnMediaType.AudioVideo); } } else
		 * {//未登录时提示登录 by wangy 20140620 Dialog alertDialog=new
		 * AlertDialog.Builder(ScreenChat.this) .setTitle("提示")
		 * .setMessage("请先登录！") .create(); alertDialog.show(); } } });
		 */
		/*
		 * mBtShare.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * if(mSipService.isRegistered()){
		 * if(!NgnStringUtils.isNullOrEmpty(sRemoteParty)){ // TODO } } else
		 * {//未登录时提示登录 by wangy 20140620 Dialog alertDialog=new
		 * AlertDialog.Builder(ScreenChat.this) .setTitle("提示")
		 * .setMessage("请先登录！") .create(); alertDialog.show(); } } });
		 */

		mBtSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSipService.isRegistered()) {
					if (!NgnStringUtils.isNullOrEmpty(mEtCompose.getText()
							.toString())) {
						sendMessage();
					}
				} else {// 未登录时提示登录 by wangy 20140620
					Dialog alertDialog = new AlertDialog.Builder(
							ScreenChat.this).setTitle("提示").setMessage("请先登录！")
							.create();
					alertDialog.show();
				}

				if (mInputMethodManager != null) {
					mInputMethodManager.hideSoftInputFromWindow(
							mEtCompose.getWindowToken(), 0);
				}
			}
		});

		mEtCompose.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				boolean isNull=NgnStringUtils.isNullOrEmpty(mEtCompose
						.getText().toString());
				mBtSend.setEnabled(!isNull);
				if(!isNull){
					mBtSend.setVisibility(View.VISIBLE);
					mBtnAddFeature.setVisibility(View.GONE);
				}
				else {
					mBtSend.setVisibility(View.GONE);
					mBtnAddFeature.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// BugFix: http://code.google.com/p/android/issues/detail?id=7189
		mEtCompose.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					if (!v.hasFocus()) {
						v.requestFocus();
					}
					break;
				}
				return false;
			}
		});
		mEtCompose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(ScreenChat.this.getCurrentFocus()!=null){
					//隐藏软键盘
					/*((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(ScreenChat.this.getCurrentFocus()
							.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);*/
					emotionView.setVisibility(View.GONE);
					AddFeatureView.setVisibility(View.GONE);
				}
			}
		});
		AddFeatureView=findViewById(R.id.plus_feature);
		mBtnAddFeature=(ImageButton) findViewById(R.id.btn_add_new);
		mBtnAddFeature.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				emotionView.setVisibility(View.GONE);
				if(AddFeatureView.getVisibility()==View.GONE){
					if(emotionView.getVisibility()!=View.VISIBLE)
						AddFeatureView.setVisibility(View.VISIBLE);
				}
				else
					AddFeatureView.setVisibility(View.GONE);
			}
			
		});
		mBtnVoice=(Button)findViewById(R.id.btn_audio_speak);
		mBtnVoice.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// TODO upon pressed, record. upon released, upload and send message.
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mChatService.startVoice();
					break;
				case MotionEvent.ACTION_UP:
					mChatService.stopVoice();
					AddFeatureView.setVisibility(View.GONE);
					break;
				default:
					break;
				}
				return false;
			}
		});
		mFileTrans=(ImageButton)findViewById(R.id.btn_file_transfer);
		mFileTrans.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO generate a dialog to promote users to choose file.
				AddFeatureView.setVisibility(View.GONE);
				Intent intent = new Intent();
				intent.putExtra("explorer_title",
						getString(R.string.dialog_read_from_dir));
				intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath())), "*/*");
				intent.setClass(ScreenChat.this, FileChooserDialog.class);
				startActivityForResult(intent, REQUEST_FILE);
			}
			
		});
		mImageTrans=(ImageButton)findViewById(R.id.btn_image_transfer);
		mImageTrans.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO generate a dialog to promote users to choose file.
				AddFeatureView.setVisibility(View.GONE);
				Intent intent = new Intent();
				intent.putExtra("explorer_title",
						getString(R.string.dialog_read_from_dir));
				intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath())), "*/*");
				intent.setClass(ScreenChat.this, FileChooserDialog.class);
				startActivityForResult(intent, REQUEST_IMAGE);
			}
			
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mMediaType != NgnMediaType.None) {
			initialize(mMediaType);
		}
		mAdapter.refresh();
	}

	@Override
	protected void onPause() {
		if (mInputMethodManager != null) {
			mInputMethodManager.hideSoftInputFromWindow(
					mEtCompose.getWindowToken(), 0);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mSession != null) {
			mSession.decRef();
			mSession = null;
		}
	}

	@Override
	public boolean hasBack() {
		return true;
	}

	/*
	 * 去除返回原版IMSDroid的情况，by wangy 20140620
	 * 
	 * @Override public boolean back(){ boolean ret =
	 * mScreenService.show(ScreenTabMessages.class); if(ret){
	 * mScreenService.destroy(getId()); } return ret; }
	 */

	private void initialize(NgnMediaType mediaType) {
		final boolean bIsNewScreen = mMediaType == NgnMediaType.None;
		mMediaType = mediaType;
		if (mMediaType == NgnMediaType.Chat) {
			final String validUri = NgnUriUtils.makeValidSipUri(sRemoteParty);
			if (!NgnStringUtils.isNullOrEmpty(validUri)) {
				mSession = NgnMsrpSession
						.getSession(new NgnPredicate<NgnMsrpSession>() {
							@Override
							public boolean apply(NgnMsrpSession session) {
								if (session != null
										&& session.getMediaType() == NgnMediaType.Chat) {
									return NgnStringUtils.equals(
											session.getRemotePartyUri(),
											validUri, false);
								}
								return false;
							}
						});
				if (mSession == null) {
					if ((mSession = NgnMsrpSession.createOutgoingSession(
							mSipService.getSipStack(), NgnMediaType.Chat,
							validUri)) == null) {
						Log.e(TAG, "Failed to create MSRP session");
						finish();
						return;
					}
				}
				if (bIsNewScreen && mSession != null) {
					mSession.incRef();
				}
			} else {
				Log.e(TAG, "makeValidSipUri(" + sRemoteParty + ") has failed");
				finish();
				return;
			}
		}
	}
	private boolean sendVoiceMessage(String content){
		mFilePath="";
		boolean ret = false;

		final NgnHistorySMSEvent e = new NgnHistorySMSEvent(sRemoteParty,
				StatusType.Outgoing);
		mFilePath=content;
		e.setContent(content);

		if (!mSipService.isRegistered()) {
			Log.e(TAG, "Not registered");
			return false;
		}
		if (mMediaType == NgnMediaType.Chat) {
			if (mSession != null) {
				ret = mSession.SendMessage(content);
			} else {
				Log.e(TAG, "MSRP session is null");
				return false;
			}
		} else {
			final String remotePartyUri = NgnUriUtils
					.makeValidSipUri(sRemoteParty);
			final NgnMessagingSession imSession = NgnMessagingSession
					.createOutgoingSession(mSipService.getSipStack(),
							remotePartyUri);
			if (!(ret = imSession.sendTextMessage(content))) {
				e.setStatus(StatusType.Failed);
			}
			NgnMessagingSession.releaseSession(imSession);
		}

		mHistorytService.addEvent(e);
		mEtCompose.setText(NgnStringUtils.emptyValue());
		return ret;
	}

	private boolean sendMessage() {
		mFilePath="";
		boolean ret = false;
		final String content = mEtCompose.getText().toString();

		final NgnHistorySMSEvent e = new NgnHistorySMSEvent(sRemoteParty,
				StatusType.Outgoing);
		e.setContent(content);

		if (!mSipService.isRegistered()) {
			Log.e(TAG, "Not registered");
			return false;
		}
		if (mMediaType == NgnMediaType.Chat) {
			if (mSession != null) {
				ret = mSession.SendMessage(content);
			} else {
				Log.e(TAG, "MSRP session is null");
				return false;
			}
		} else {
			final String remotePartyUri = NgnUriUtils
					.makeValidSipUri(sRemoteParty);
			final NgnMessagingSession imSession = NgnMessagingSession
					.createOutgoingSession(mSipService.getSipStack(),
							remotePartyUri);
			if (!(ret = imSession.sendTextMessage(mEtCompose.getText()
					.toString()))) {
				e.setStatus(StatusType.Failed);
			}
			NgnMessagingSession.releaseSession(imSession);
		}

		mHistorytService.addEvent(e);
		mEtCompose.setText(NgnStringUtils.emptyValue());
		return ret;
	}

	public static void startChat(String remoteParty, boolean bIsPagerMode) {
		final Engine engine = (Engine) NgnEngine.getInstance();
		remote = remoteParty;
		if (!NgnStringUtils.isNullOrEmpty(remoteParty)
				&& remoteParty.startsWith("sip:")) {

			remoteParty = NgnUriUtils.getUserName(remoteParty);
		}

		if (NgnStringUtils.isNullOrEmpty((sRemoteParty = remoteParty))) {
			Log.e(TAG, "Null Uri");
			return;
		}

		if (engine.getScreenService().show(ScreenChat.class)) {
			final IBaseScreen screen = engine.getScreenService().getScreen(TAG);
			if (screen instanceof ScreenChat) {
				((ScreenChat) screen)
						.initialize(bIsPagerMode ? NgnMediaType.SMS
								: NgnMediaType.Chat);
			}
		}
	}

	//
	// HistoryEventSMSFilter
	//
	static class HistoryEventChatFilter implements
			NgnPredicate<NgnHistoryEvent> {
		@Override
		public boolean apply(NgnHistoryEvent event) {
			if (event != null && (event.getMediaType() == NgnMediaType.SMS)) {
				return NgnStringUtils.equals(sRemoteParty,
						event.getRemoteParty(), false);
			}
			return false;
		}
	}

	//
	// DateComparator
	//
	static class DateComparator implements Comparator<NgnHistoryEvent> {
		@Override
		public int compare(NgnHistoryEvent e1, NgnHistoryEvent e2) {
			return (int) (e1.getStartTime() - e2.getStartTime());
		}
	}

	/**
	 * ScreenChatAdapter
	 */
	static class ScreenChatAdapter extends BaseAdapter implements Observer {
		private List<NgnHistoryEvent> mEvents;
		private final LayoutInflater mInflater;
		private final Handler mHandler;
		private final ScreenChat mBaseScreen;

		ScreenChatAdapter(ScreenChat baseSceen) {
			mBaseScreen = baseSceen;
			mHandler = new Handler();
			mInflater = LayoutInflater.from(mBaseScreen);
			mEvents = mBaseScreen.mHistorytService.getObservableEvents()
					.filter(new HistoryEventChatFilter());
			Collections.sort(mEvents, new DateComparator());
			mBaseScreen.mHistorytService.getObservableEvents()
					.addObserver(this);
		}

		@Override
		protected void finalize() throws Throwable {
			mBaseScreen.mHistorytService.getObservableEvents().deleteObserver(
					this);
			super.finalize();
		}

		public void refresh() {
			mEvents = mBaseScreen.mHistorytService.getObservableEvents()
					.filter(new HistoryEventChatFilter());
			Collections.sort(mEvents, new DateComparator());
			if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
				notifyDataSetChanged();
			} else {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						notifyDataSetChanged();
					}
				});
			}
		}

		@Override
		public int getCount() {
			return mEvents.size();
		}

		@Override
		public Object getItem(int position) {
			return mEvents.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;

			final NgnHistoryEvent event = (NgnHistoryEvent) getItem(position);
			if (event == null) {
				return null;
			}
			if (view == null) {
				switch (event.getMediaType()) {
				case Audio:
				case AudioVideo:
				case FileTransfer:
				default:
					Log.e(TAG, "Invalid media type");
					return null;
				case SMS:

					break;
				}
			}

			final NgnHistorySMSEvent SMSEvent = (NgnHistorySMSEvent) event;
			final String content = SMSEvent.getContent();
			final boolean bIncoming = SMSEvent.getStatus() == StatusType.Incoming;
			// added by song 2014.7.8
			SimpleDateFormat sdformat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");// 24小时制
			if (bIncoming) {									
				view = mInflater.inflate(R.layout.chatting_item_msg_text_left,
						null);
				TextView tvSendTime = (TextView) view
						.findViewById(R.id.tv_sendtime);
				// TextView tvUserName = (TextView)
				// convertView.findViewById(R.id.tv_username);
				TextView tvContent = (TextView) view
						.findViewById(R.id.tv_chatcontent);

				tvSendTime
						.setText(DateTimeUtils.getFriendlyDateString(new Date(
								SMSEvent.getStartTime())));
				SpannableString spannableChatContent=emotionUtils.getEmotionSpannable(screenChatContext, content);
				if(content.contains("/**THIS IS A INFO MESSAGE**/")){
					if(content.contains("AUDIO")){
						final String filePath=content.substring(content.indexOf("AUDIO_PATH:")+11,content.indexOf("AUDIO_LENGTH:"));
						final String fileLength=content.substring(content.indexOf("AUDIO_LENGTH:")+13);
						Log.e("TAG", "filepath"+filePath);
						//tvContent.setBackgroundResource(R.drawable.phone_speaker_48);
						tvContent.setText("语音 "+fileLength);
						tvContent.setOnClickListener(new OnClickListener(){
	
							@Override
							public void onClick(View arg0) {
								// TODO 自动生成的方法存根
								mChatService.doDownload(arg0,filePath,MyChatService.FILE_TYPE_AUDIO);
							}
							
						});
					}else if(content.contains("FILE")){
						final String filePath=content.substring(content.indexOf("FILE_PATH:")+10);
						//tvContent.setBackgroundResource(R.drawable.phone_speaker_48);
						tvContent.setText("文件 ");
						tvContent.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View arg0) {
								// TODO 自动生成的方法存根
								mChatService.doDownload(arg0,filePath,MyChatService.FILE_TYPE_FILE);
							}
							
						});
					}else if(content.contains("IMAGE")){						
						view = mInflater.inflate(R.layout.chatting_item_image_left,null);
						final String filePath=content.substring(content.indexOf("IMAGE_PATH:")+11);
						//tvContent.setBackgroundResource(R.drawable.phone_speaker_48);
						tvSendTime=(TextView) view.findViewById(R.id.tv_sendtime);
						tvSendTime.setText(DateTimeUtils.getFriendlyDateString(new Date(SMSEvent.getStartTime())));
						final ImageView imageView=(ImageView) view.findViewById(R.id.chat_image);
						
						imageView.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View arg0) {
								// TODO 自动生成的方法存根
								mChatService.doDownload(imageView,filePath,MyChatService.FILE_TYPE_IMAGE);
							}
							
						});
					}
					//SMSEvent.setContent("语音");
					return view;
				}else{
					tvContent.setText(content == null ? NgnStringUtils.emptyValue()
						: spannableChatContent);					
				}
			} else {
				view = mInflater.inflate(R.layout.chatting_item_msg_text_right,
						null);
				TextView tvSendTime = (TextView) view
						.findViewById(R.id.tv_sendtime);
				// TextView tvUserName = (TextView)
				// convertView.findViewById(R.id.tv_username);
				TextView tvContent = (TextView) view
						.findViewById(R.id.tv_chatcontent);
				tvSendTime
						.setText(DateTimeUtils.getFriendlyDateString(new Date(
								SMSEvent.getStartTime())));
				if(content.contains("/**THIS IS A INFO MESSAGE**/")){
					if(content.contains("AUDIO")){
						final String filePath=content.substring(content.indexOf("AUDIO_PATH:")+11,content.indexOf("AUDIO_LENGTH:"));
						final String fileLength=content.substring(content.indexOf("AUDIO_LENGTH:")+13);
						//tvContent.setBackgroundResource(R.drawable.phone_speaker_48);
						tvContent.setText("语音 "+fileLength);
						tvContent.setOnClickListener(new OnClickListener(){
	
							@Override
							public void onClick(View arg0) {
								// TODO 自动生成的方法存根
								mChatService.doDownload(arg0,filePath,MyChatService.FILE_TYPE_AUDIO);
							}
							
						});
					}else if(content.contains("FILE")){
						final String filePath=content.substring(content.indexOf("FILE_PATH:")+10);
						//tvContent.setBackgroundResource(R.drawable.phone_speaker_48);
						tvContent.setText("文件 ");
						tvContent.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View arg0) {
								// TODO 自动生成的方法存根
								mChatService.doDownload(arg0,filePath,MyChatService.FILE_TYPE_FILE);
							}
							
						});
					}else if(content.contains("IMAGE")){
						view = mInflater.inflate(R.layout.chatting_item_image_right,null);
						final String filePath=content.substring(content.indexOf("IMAGE_PATH:")+11);
						//tvContent.setBackgroundResource(R.drawable.phone_speaker_48);
						tvSendTime=(TextView) view.findViewById(R.id.tv_sendtime);
						tvSendTime.setText(DateTimeUtils.getFriendlyDateString(new Date(SMSEvent.getStartTime())));
						final ImageView imageView=(ImageView) view.findViewById(R.id.chat_image);
						imageView.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View arg0) {
								// TODO 自动生成的方法存根
								mChatService.doDownload(arg0,filePath,MyChatService.FILE_TYPE_IMAGE);
							}
							
						});
					}
					//SMSEvent.setContent("语音");
					return view;
				}else{
					SpannableString spannableChatContent=emotionUtils.getEmotionSpannable(screenChatContext, content);
					tvContent.setText(content == null ? NgnStringUtils.emptyValue()
							: spannableChatContent);
				}
			}

			return view;
		}

		@Override
		public void update(Observable observable, Object data) {
			refresh();
		}
	}

	/**
	 * 添加表情
	 */
	private void displayEmotionSelectView() {
		/*
		 * if(!isWrite){
		 * btnSendOrChange.setBackgroundResource(R.drawable.voice_weixin_own);
		 * btnVoice.setVisibility(View.GONE);
		 * contentEditText.setVisibility(View.VISIBLE); isWrite = true; }
		 */
		if (emotionView.getVisibility() != View.VISIBLE) {
			// 隐藏软键盘
			if (ScreenChat.this.getCurrentFocus() != null) {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(ScreenChat.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			}
			/*
			 * //隐藏expand列表----"+"号控件，可以添加图片等聊天信息，待实现
			 * if(expandView.getVisibility() == View.VISIBLE)
			 * expandView.setVisibility(View.GONE);
			 */
			// 显示表情
			emotionView.setVisibility(View.VISIBLE);
			/*
			 * if(chatList != null && chatList.size()>1){ new Handler().post(new
			 * Runnable() {
			 * 
			 * @Override public void run() {
			 * chatListView.setSelection(chatList.size() - 1); } }); }
			 */
		} else {
			// 隐藏表情
			emotionView.setVisibility(View.GONE);
		}
	}

	/**
	 * 添加表情
	 * 
	 * @param position
	 */
	public void addEmotionImageToEditText(int position) {

		Drawable drawable = getResources().getDrawable(emotion_array[position]);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		String text = emotionsMap.get(emotion_array[position] + "");
		int cursor = mEtCompose.getSelectionStart();
		String textContent = mEtCompose.getText().toString();
		String head = textContent.substring(0, cursor);
		String tail = textContent.substring(cursor, textContent.length());
		String content = head + "[" + text + "]" + tail;
		mEtCompose.setText(content);
		Editable editable = mEtCompose.getText();
		Selection.setSelection(editable, cursor + (text.length() + 2));
		emotionUtils.addEmotionSpan(ScreenChat.this, content, mEtCompose);

	}

	/**
	 * 表情适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class GridAdapter extends BaseAdapter {

		public GridAdapter(Context context) {

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return emotion_array.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(ScreenChat.this);
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				imageView.setLayoutParams(new GridView.LayoutParams(DisplayUtils.dip2px(ScreenChat.this, 20), 
						DisplayUtils.dip2px(ScreenChat.this, 20)));
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setBackgroundResource(emotion_array[position]);
			return imageView;

		}

	}
	
	public class MyHandler extends Handler{
		public static final int FILE_UPLOAD_PATH=0;
		public static final int FILE_DOWNLOAD_PATH=1;
		@Override
		public void handleMessage(Message msg){
			if(msg.what==MyHandler.FILE_UPLOAD_PATH){
				//Toast.makeText(screenChatContext, "uploadfile executed, stored at "+msg.obj.toString(), Toast.LENGTH_SHORT).show();
				if (msg.arg1==1) {
					String fileInfoMsg = "/**THIS IS A INFO MESSAGE**/"
							+ "AUDIO_PATH:" + msg.obj.toString()
							+ "AUDIO_LENGTH:" + msg.arg2+"秒";
					if (mSipService.isRegistered()) {
						sendVoiceMessage(fileInfoMsg);
					} else {// 未登录时提示登录 by wangy 20140620
						Dialog alertDialog = new AlertDialog.Builder(
								ScreenChat.this).setTitle("提示")
								.setMessage("请先登录！").create();
						alertDialog.show();
					}
				}else if(msg.arg1==2){
					String fileInfoMsg = "/**THIS IS A INFO MESSAGE**/"
							+ "FILE_PATH:" + msg.obj.toString();
					if (mSipService.isRegistered()) {
						sendVoiceMessage(fileInfoMsg);
					} else {// 未登录时提示登录 by wangy 20140620
						Dialog alertDialog = new AlertDialog.Builder(
								ScreenChat.this).setTitle("提示")
								.setMessage("请先登录！").create();
						alertDialog.show();
					}
				}else if(msg.arg1==3){
										String fileInfoMsg = "/**THIS IS A INFO MESSAGE**/"
							+ "IMAGE_PATH:" + msg.obj.toString();
					if (mSipService.isRegistered()) {
						sendVoiceMessage(fileInfoMsg);
					} else {// 未登录时提示登录 by wangy 20140620
						Dialog alertDialog = new AlertDialog.Builder(
								ScreenChat.this).setTitle("提示")
								.setMessage("请先登录！").create();
						alertDialog.show();
					}
				}
			}
			else if(msg.what==MyHandler.FILE_DOWNLOAD_PATH){
				//Toast.makeText(screenChatContext, "downloadfile executed, stored at "+msg.obj.toString(), Toast.LENGTH_SHORT).show();
				if(msg.arg1==1){
					File f=(File) msg.obj;
					mChatService.playAudioMsg(f);
				}else if(msg.arg1==2){
					//Toast.makeText(screenChatContext, "downloadfile executed", Toast.LENGTH_SHORT).show();
				}else if(msg.arg1==3){
				/*	HashMap<String,Object> map=(HashMap<String, Object>) msg.obj;
					String filePath=map.get("File").toString();
					Bitmap image=(Bitmap) map.get("Image");*/
				}
			}		
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		String path;
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_FILE) {
				Uri uri = intent.getData();
				String filePath="";
				try {
					filePath=URLDecoder.decode(uri.getEncodedPath(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("file...", filePath);
				mChatService.doUpload(filePath, MyChatService.FILE_TYPE_FILE);
			}
			else if (requestCode == REQUEST_IMAGE) {
				Uri uri = intent.getData();
				String filePath="";
				try {
					filePath=URLDecoder.decode(uri.getEncodedPath(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("file...", filePath);
				mChatService.doUpload(filePath, MyChatService.FILE_TYPE_IMAGE);
			}
		}
	}
}
