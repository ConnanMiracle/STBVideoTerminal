/* Copyright (C) 2010-2011, Mamadou Diop.
 *  Copyright (C) 2011, Doubango Telecom.
 *
 * Contact: Mamadou Diop <diopmamadou(at)doubango(dot)org>
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
import org.imshello.ngn.NgnEngine;
import org.imshello.ngn.media.NgnMediaType;
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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
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

public class ScreenMultiChat extends BaseScreen {
	private static String TAG = ScreenMultiChat.class.getCanonicalName();

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
	private Button mBtSend;

	private Emotions emotionClass;
	private Map<String, String> emotionsMap;
	private ImageButton emotion;
	private int[] emotion_array;
	private GridView emotionView;
	private static EmotionUtils emotionUtils;
	
	private TextView user;
	private ImageView call;
	private ImageView back;

	private static String username[];
	//private static NgnMsrpSession currentSession;
	private static NgnMsrpSession[] MultiSessions;
	private static int memberNum;

	public ScreenMultiChat() {
		super(SCREEN_TYPE.CHAT_T, TAG);
		mMediaType = NgnMediaType.None;
		mHistorytService = getEngine().getHistoryService();
		mSipService = getEngine().getSipService();
		mContactService1 = getEngine().getContactService();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_multichat);

		mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		screenChatContext=ScreenMultiChat.this;
		emotionUtils = new EmotionUtils();
		emotionClass = new Emotions();
		emotion_array = emotionClass.emotion;
		emotion = (ImageButton) findViewById(R.id.btn_emotion);
		emotion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				displayEmotionSelectView();
			}
		});
		emotionView = (GridView) findViewById(R.id.emotion_grid_view);
		emotionView.setAdapter(new GridAdapter(ScreenMultiChat.this));
		// 选择表情
		emotionView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				emotionsMap = emotionClass.getEmotionsList_image();
				addEmotionImageToEditText(position);
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
		String chatName=null;
		for(int i=0;i<username.length;i++){
/*			if(tmp!=null){
				NgnContact contact = mContactService1.getContactByPhoneNumber(tmp);
				if (contact == null) {
					if (tmp.contains("sip")) {
						String a1[] = tmp.split(":");
						String a2[] = a1[1].split("@");
						user.setText(chatName+a2[0]);
					} else {
						user.setText(chatName+tmp);
					}
				} else {
					user.setText(chatName+tmp);
				}
			}*/
			String tmp=username[i];
			if(i<memberNum)
				if(i==0)
					chatName=tmp;
				else
					chatName=chatName+tmp;
			else 
				break;
		}
		user.setText(chatName);
			
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
							ScreenMultiChat.this).setTitle("提示").setMessage("请先登录！")
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
						for(NgnMsrpSession tmp:MultiSessions){
							sendMessage(tmp);
						}
					}
				} else {// 未登录时提示登录 by wangy 20140620
					Dialog alertDialog = new AlertDialog.Builder(
							ScreenMultiChat.this).setTitle("提示").setMessage("请先登录！")
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
				mBtSend.setEnabled(!NgnStringUtils.isNullOrEmpty(mEtCompose
						.getText().toString()));
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
				if(ScreenMultiChat.this.getCurrentFocus()!=null){
					//隐藏软键盘
					/*((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(ScreenChat.this.getCurrentFocus()
							.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);*/
					emotionView.setVisibility(View.GONE);
				}
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
			int i=0;
			for(NgnMsrpSession tmp:MultiSessions){
				if(tmp==null){
					MultiSessions[i]=tmp;
					break;
				}
				i++;
			}
		}
	}

	private boolean sendMessage(NgnMsrpSession currentMsrpSession) {
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
			if (currentMsrpSession != null) {
				ret = currentMsrpSession.SendMessage(content);
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

	/**
	 * Static method called from other activity to perform a multichat.
	 * This method retrieve the participants phone numbers which are parsed into several SIP URLs.
	 * These SIP URL will be used in method initialize(NgnMediaType type) to set up several MSRP session.
	 * These session will be used to deliver chat messages.
	 * @param participants: members involved in the multichat.
	 * @param bIsPagerMode
	 * @param numOfParticipants: the number of participants involved in the chat
	 */
	public static void startChat(String participants[], boolean bIsPagerMode, int numOfParticipants) {
		final Engine engine = (Engine) NgnEngine.getInstance();
		MultiSessions=new NgnMsrpSession[10];
		username=participants;
		memberNum=numOfParticipants;
		for (String tmp:participants) {
			//remote = tmp;
			if (!NgnStringUtils.isNullOrEmpty(tmp)
					&& tmp.startsWith("sip:")) {

				tmp = NgnUriUtils.getUserName(tmp);
			}
			if (NgnStringUtils.isNullOrEmpty((sRemoteParty = tmp))) {
				Log.e(TAG, "Null Uri");
				return;
			}
			if (engine.getScreenService().show(ScreenMultiChat.class)) {
				final IBaseScreen screen = engine.getScreenService().getScreen(
						TAG);
				if (screen instanceof ScreenMultiChat) {
					((ScreenMultiChat) screen)
							.initialize(bIsPagerMode ? NgnMediaType.SMS
									: NgnMediaType.Chat);
				}
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
		private final ScreenMultiChat mBaseScreen;

		ScreenChatAdapter(ScreenMultiChat baseSceen) {
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
			new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
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
				tvContent.setText(content == null ? NgnStringUtils.emptyValue()
						: spannableChatContent);
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
				SpannableString spannableChatContent=emotionUtils.getEmotionSpannable(screenChatContext, content);
				tvContent.setText(content == null ? NgnStringUtils.emptyValue()
						: spannableChatContent);
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
			if (ScreenMultiChat.this.getCurrentFocus() != null) {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(ScreenMultiChat.this
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
		emotionUtils.addEmotionSpan(ScreenMultiChat.this, content, mEtCompose);

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
				imageView = new ImageView(ScreenMultiChat.this);
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				imageView.setLayoutParams(new GridView.LayoutParams(DisplayUtils.dip2px(ScreenMultiChat.this, 20), 
						DisplayUtils.dip2px(ScreenMultiChat.this, 20)));
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setBackgroundResource(emotion_array[position]);
			return imageView;

		}

	}
}
