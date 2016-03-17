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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.imshello.droid.R;
import org.imshello.droid.Utils.DateTimeUtils;
import org.imshello.droid.Utils.EmotionUtils;
import org.imshello.ngn.model.NgnContact;
import org.imshello.ngn.model.NgnHistoryEvent;
import org.imshello.ngn.model.NgnHistorySMSEvent;
import org.imshello.ngn.model.NgnHistoryAVCallEvent.HistoryEventAVFilter;
import org.imshello.ngn.model.NgnHistorySMSEvent.HistoryEventSMSFilter;
import org.imshello.ngn.model.NgnHistorySMSEvent.HistoryEventSMSIntelligentFilter;
import org.imshello.ngn.services.INgnContactService;
import org.imshello.ngn.services.INgnHistoryService;
import org.imshello.ngn.utils.NgnStringUtils;
import org.imshello.ngn.utils.NgnUriUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class TabMessages extends BaseScreen {
	private static String TAG = TabMessages.class.getCanonicalName();

	private static final int MENU_CLEAR_MESSSAGES = 1;

	private final INgnHistoryService mHistoryService;
	private final INgnContactService mContactService;
	
	private static EmotionUtils emotionUtils;
	private static Context screenTabMessagesContext;
	
	private NgnHistoryEvent history;
	private NgnHistorySMSEvent msm;
	private ListView mListView;
	private ScreenTabMessagesAdapter mAdapter;
	private ArrayList<String> name;

	public TabMessages() {
		super(SCREEN_TYPE.TAB_MESSAGES_T, TAG);

		mHistoryService = getEngine().getHistoryService();
		mContactService = getEngine().getContactService();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_messages);
		Log.d(TAG, "onCreate()");
		
		screenTabMessagesContext=this;
		emotionUtils = new EmotionUtils();
		
		mAdapter = new ScreenTabMessagesAdapter(this);
		mListView = (ListView) findViewById(R.id.screen_tab_messages_listView);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(mOnItemListViewClickListener);
		mListView.setOnItemLongClickListener(mOnItemListViewLongClickListener);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");
	}

	@Override
	public boolean hasBack() {
		return true;
	}

	@Override
	public boolean back() {
		return mScreenService.show(ScreenMain.class);
	}

	@Override
	public boolean hasMenu() {
		return true;
	}

	@Override
	public boolean createOptionsMenu(Menu menu) {
		menu.add(0, MENU_CLEAR_MESSSAGES, 0, "Clear entries");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_CLEAR_MESSSAGES:
			mHistoryService.deleteEvents(new HistoryEventSMSFilter());
			break;
		}
		return true;
	}

	private final OnItemClickListener mOnItemListViewClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			final NgnHistoryEvent event = (NgnHistoryEvent) parent
					.getItemAtPosition(position);
			if (event != null) {
				ScreenSplash.count = 0;// 20140620聊天界面返回时返回到信息列表界面
				ScreenChat.startChat(event.getRemoteParty(), true);
			}
		}
	};

	private final OnItemLongClickListener mOnItemListViewLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			final NgnHistoryEvent event1 = (NgnHistoryEvent) parent
					.getItemAtPosition(position);

			Toast.makeText(getApplicationContext(), event1.getRemoteParty(),
					Toast.LENGTH_SHORT).show();
			return true;
		}
	};

	private void Add() {
		new AlertDialog.Builder(TabMessages.this).setTitle("提示")
				.setMessage("删除对话")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

						// mHistoryService.deleteEvent(event1);

					}
				}).show();
	}

	/**
	 * ScreenTabMessagesAdapter
	 */
	static class ScreenTabMessagesAdapter extends BaseAdapter implements
			Observer {
		private List<NgnHistoryEvent> mEvents;
		private final LayoutInflater mInflater;
		private final Handler mHandler;
		private final TabMessages mBaseScreen;
		private final MyHistoryEventSMSIntelligentFilter mFilter;
		private final HistoryEventAVFilter mFilter1;

		ScreenTabMessagesAdapter(TabMessages baseSceen) {
			mBaseScreen = baseSceen;
			mHandler = new Handler();
			mInflater = LayoutInflater.from(mBaseScreen);
			mFilter = new MyHistoryEventSMSIntelligentFilter();
			mFilter1 = new HistoryEventAVFilter();
			mEvents = mBaseScreen.mHistoryService.getObservableEvents().filter(
					mFilter);
			// mEvents =
			// mBaseScreen.mHistoryService.getObservableEvents().getList();
			mBaseScreen.mHistoryService.getObservableEvents().addObserver(this);
		}

		@Override
		protected void finalize() throws Throwable {
			mBaseScreen.mHistoryService.getObservableEvents().deleteObserver(
					this);
			super.finalize();
		}

		void refresh() {
			mFilter.reset();
			mEvents = mBaseScreen.mHistoryService.getObservableEvents().filter(
					mFilter);
			// mEvents =
			// mBaseScreen.mHistoryService.getObservableEvents().getList();
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

				case SMS:
					view = mInflater.inflate(R.layout.tab_messages_item,
							null);
					String remoteParty1 = event.getRemoteParty();
					NgnContact contact = mBaseScreen.mContactService
							.getContactByPhoneNumber(remoteParty1);
					if (contact == null) {
						remoteParty1 = NgnUriUtils.getDisplayName(remoteParty1);
					} else {
						remoteParty1 = contact.getDisplayName();
					}

					final TextView tvRemote1 = (TextView) view
							.findViewById(R.id.screen_tab_messages_item_textView_remote);
					final TextView tvDate1 = (TextView) view
							.findViewById(R.id.screen_tab_messages_item_textView_date);
					final TextView tvContent = (TextView) view
							.findViewById(R.id.screen_tab_messages_item_textView_content);
					final TextView tvUnSeen = (TextView) view
							.findViewById(R.id.screen_tab_messages_item_textView_unseen);

					final NgnHistorySMSEvent SMSEvent = (NgnHistorySMSEvent) event;
					tvRemote1.setText(remoteParty1);
					tvDate1.setText(DateTimeUtils
							.getFriendlyDateString(new Date(event
									.getStartTime())));
					final String SMSContent = SMSEvent.getContent();
					SpannableString spannableChatContent=emotionUtils.getEmotionSpannable(screenTabMessagesContext, SMSContent);
					if(SMSContent.contains("/**THIS IS A INFO MESSAGE**/")&&SMSContent.contains("AUDIO"))
						tvContent.setText("语音");
					else if(SMSContent.contains("/**THIS IS A INFO MESSAGE**/")&&SMSContent.contains("FILE")){
						tvContent.setText("文件");
					}else if(SMSContent.contains("/**THIS IS A INFO MESSAGE**/")&&SMSContent.contains("IMAGE")){
						tvContent.setText("图片");
					}
					else{
						tvContent
								.setText(NgnStringUtils.isNullOrEmpty(SMSContent) ? NgnStringUtils
										.emptyValue() : spannableChatContent);
					}
					tvUnSeen.setText(Integer.toString(mFilter.getUnSeen()));
					break;

				case FileTransfer:
				default:
					Log.e(TAG, "Invalid media type");
					return null;

				}
			}
			return view;
		}

		@Override
		public void update(Observable observable, Object data) {
			Log.d(TAG, "update()");
			refresh();
		}
	}

	//
	// MyHistoryEventSMSIntelligentFilter
	//
	static class MyHistoryEventSMSIntelligentFilter extends
			HistoryEventSMSIntelligentFilter {
		private int mUnSeen;

		int getUnSeen() {
			return mUnSeen;
		}

		@Override
		protected void reset() {
			super.reset();
			mUnSeen = 0;
		}

		@Override
		public boolean apply(NgnHistoryEvent event) {
			if (super.apply(event)) {
				mUnSeen += event.isSeen() ? 0 : 1;
				return true;
			}
			return false;
		}
	}
}
