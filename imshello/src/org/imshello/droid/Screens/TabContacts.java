package org.imshello.droid.Screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.imshello.droid.Engine;
import org.imshello.droid.R;
import org.imshello.droid.QuickAction.ActionItem;
import org.imshello.droid.QuickAction.QuickAction;
import org.imshello.droid.R.string;
//import org.imshello.droid.Screens.ScreenPhone;
import org.imshello.droid.Sortcontacts.CharacterParser;
import org.imshello.droid.Sortcontacts.ClearEditText;
import org.imshello.droid.Sortcontacts.PinyinComparator;
import org.imshello.droid.Sortcontacts.SideBar;
import org.imshello.droid.Sortcontacts.SortAdapter;
import org.imshello.droid.Sortcontacts.SortModel;
import org.imshello.droid.Sortcontacts.SideBar.OnTouchingLetterChangedListener;
import org.imshello.droid.Utils.DialerUtils;
import org.imshello.droid.Utils.PhoneDialerUtils;
import org.imshello.ngn.media.NgnMediaType;
import org.imshello.ngn.model.NgnContact;
import org.imshello.ngn.model.NgnHistorySMSEvent;
import org.imshello.ngn.model.NgnHistoryEvent.StatusType;
import org.imshello.ngn.services.INgnContactService;
import org.imshello.ngn.services.INgnHistoryService;
import org.imshello.ngn.services.INgnSipService;
import org.imshello.ngn.utils.NgnObservableList;
import org.imshello.ngn.utils.NgnStringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TabContacts extends BaseScreen {

	private static String TAG = TabContacts.class.getCanonicalName();

	private EditText mEtNumber; // 号码输入框
	private ImageButton dialerShowButton, callRecordButton,
			missedCallRecordButton, dialerHideButton;
	private LinearLayout dialerLayout, dialerExpandLayout;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	private final NgnObservableList<NgnContact> mContacts;
	private final INgnContactService mContactService;
	private final INgnSipService mSipService;
	private final ActionItem mAItemVoiceCall;
	private final ActionItem mAItemVideoCall;
	private final ActionItem mAItemChat;
	private final ActionItem mAItemSMS;
	private final ActionItem mAItemShare;
	private SortModel mSelectedContact;
	private QuickAction mLasQuickAction;
	private final INgnHistoryService mHistoryService;
	private static final int SELECT_CONTENT = 1;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	public TabContacts() {
		super(SCREEN_TYPE.SORT_CONTACTS, TAG);
		// TODO Auto-generated constructor stub

		// mContacts.addObserver((Observer) MainActivity.this);

		mContactService = getEngine().getContactService();
		mSipService = getEngine().getSipService();
		mHistoryService = getEngine().getHistoryService();
		mContacts = Engine.getInstance().getContactService()
				.getObservableContacts();
		mAItemVoiceCall = new ActionItem();
		mAItemVoiceCall.setTitle("语音");
		mAItemVoiceCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectedContact != null) {
					ScreenSplash.count = 1;
					ScreenAV.makeCall(mSelectedContact.getPhonenumber(),
							NgnMediaType.Audio);
					// added by song 将通话记录添加到信息记录中 2014.7.8
					final NgnHistorySMSEvent e = new NgnHistorySMSEvent(
							mSelectedContact.getPhonenumber(),
							StatusType.Failed);
					e.setContent("语音通话");
					e.setStartTime(new Date().getTime());
					mHistoryService.addEvent(e);
					if (mLasQuickAction != null) {
						mLasQuickAction.dismiss();
					}
				}
			}
		});

		mAItemVideoCall = new ActionItem();
		mAItemVideoCall.setTitle("Video");
		mAItemVideoCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectedContact != null) {
					ScreenAV.makeCall(mSelectedContact.getPhonenumber(),
							NgnMediaType.AudioVideo);
					if (mLasQuickAction != null) {
						mLasQuickAction.dismiss();
					}
				}
			}
		});

		mAItemChat = new ActionItem();
		mAItemChat.setTitle("聊天");
		mAItemChat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ScreenChat.startChat(mSelectedContact.getPhonenumber(), false);
				if (mLasQuickAction != null) {
					mLasQuickAction.dismiss();
				}
			}
		});

		mAItemSMS = new ActionItem();
		mAItemSMS.setTitle("信息");
		mAItemSMS.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ScreenSplash.count = 1;
				ScreenChat.startChat(mSelectedContact.getPhonenumber(), true);
				if (mLasQuickAction != null) {
					mLasQuickAction.dismiss();
				}
			}
		});

		mAItemShare = new ActionItem();
		mAItemShare.setTitle("Share");
		mAItemShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectedContact != null) {
					Intent intent = new Intent();
					intent.setType("*/*").addCategory(Intent.CATEGORY_OPENABLE)
							.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(
							Intent.createChooser(intent, "Select content"),
							SELECT_CONTENT);
					if (mLasQuickAction != null) {
						mLasQuickAction.dismiss();
					}
				}
			}
		});

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_contacts);
		initViews();
		init();
	}

	// 初始化拨号盘信息
	private void init() {
		mEtNumber = (EditText) findViewById(R.id.edit_number);
		dialerShowButton = (ImageButton) findViewById(R.id.dialer_show_button);
		dialerLayout = (LinearLayout) findViewById(R.id.dialer_layout);
		dialerExpandLayout = (LinearLayout) findViewById(R.id.dialer_expand_layout);
		// dialerHideButton =
		// (ImageButton)findViewById(R.id.dialer_hide_button);

		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_0, "0",
				"+", PhoneDialerUtils.TAG_0, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_1, "1",
				"", PhoneDialerUtils.TAG_1, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_2, "2",
				"ABC", PhoneDialerUtils.TAG_2, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_3, "3",
				"DEF", PhoneDialerUtils.TAG_3, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_4, "4",
				"GHI", PhoneDialerUtils.TAG_4, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_5, "5",
				"JKL", PhoneDialerUtils.TAG_5, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_6, "6",
				"MNO", PhoneDialerUtils.TAG_6, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_7, "7",
				"PQRS", PhoneDialerUtils.TAG_7, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_8, "8",
				"TUV", PhoneDialerUtils.TAG_8, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_9, "9",
				"WXYZ", PhoneDialerUtils.TAG_9, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_star,
				"*", "", PhoneDialerUtils.TAG_STAR, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_sharp,
				"#", "", PhoneDialerUtils.TAG_SHARP, mOnDialerClick);

		PhoneDialerUtils.setDialerImageButton(this, R.id.dialer_hide_button,
				R.drawable.keyboard_down, PhoneDialerUtils.TAG_HIDE,
				mOnDialerClick);
		PhoneDialerUtils.setDialerImageButton(this, R.id.dialer_call_audio_button,
				R.drawable.voice_call_48, PhoneDialerUtils.TAG_AUDIO_CALL, mOnDialerClick);
		
		PhoneDialerUtils.setDialerImageButton(this, R.id.dialer_call_video_button,
				R.drawable.visio_call_48, PhoneDialerUtils.TAG_VIDEO_CALL, mOnDialerClick);
		PhoneDialerUtils.setDialerImageButton(this, R.id.dialer_delete,
				R.drawable.delete, PhoneDialerUtils.TAG_DELETE, mOnDialerClick);
		// DialerUtils.setDialerImageButton(dialerHideButton,
		// DialerUtils.TAG_HIDE, mOnDialerClick);
		// DialerUtils.setDialerImageButton(deleteTextButton,
		// DialerUtils.TAG_DELETE, mOnDialerClick);
		PhoneDialerUtils.setDialerImageButton(dialerShowButton,
				PhoneDialerUtils.TAG_SHOW, mOnDialerClick);
		// DialerUtils.setDialerImageButton(callRecordButton,
		// DialerUtils.TAG_CALLRECORD, mOnDialerClick);
		// DialerUtils.setDialerImageButton(missedCallRecordButton,
		// DialerUtils.TAG_MISSEDCALLRECORD, mOnDialerClick);

		// mEtNumber.setInputType(InputType.TYPE_NULL);
		mEtNumber.setFocusable(false);
		mEtNumber.setFocusableInTouchMode(false);
		dialerLayout.setVisibility(View.GONE);
		dialerExpandLayout.setVisibility(View.VISIBLE);

	}

	// 为拨号盘按钮添加监听处理
	private final View.OnClickListener mOnDialerClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int tag = Integer.parseInt(v.getTag().toString());
			final String number = mEtNumber.getText().toString();
			if (tag == DialerUtils.TAG_CHAT) {
				//
			} else if (tag == PhoneDialerUtils.TAG_DELETE) {
				// 删除拨号盘中输入的字符
				final int selStart = mEtNumber.getSelectionStart();
				if (selStart > 0) {
					final StringBuffer sb = new StringBuffer(number);
					sb.delete(selStart - 1, selStart);
					mEtNumber.setText(sb.toString());
					mEtNumber.setSelection(selStart - 1);
				}
			} else if (tag == PhoneDialerUtils.TAG_HIDE) {
				// 控制拨号盘隐藏
				dialerLayout.setVisibility(View.GONE);
				dialerExpandLayout.setVisibility(View.VISIBLE);
			} else if (tag == PhoneDialerUtils.TAG_SHOW) {
				// 控制拨号盘显示
				dialerLayout.setVisibility(View.VISIBLE);
				dialerExpandLayout.setVisibility(View.GONE);
			} else if (tag == PhoneDialerUtils.TAG_CALLRECORD) {
				// 显示全部通话记录
				// callRecordButton.setBackgroundResource(R.drawable.all)
				// changeCurrentShowCallRecord(CallRecord.ALL_CALL);
			} else if (tag == PhoneDialerUtils.TAG_MISSEDCALLRECORD) {
				// 只显示未接来电记录
				// changeCurrentShowCallRecord(CallRecord.MISSED_CALL);
			} else if (tag == PhoneDialerUtils.TAG_PHONE_CALL) {
				//
			}else if(tag == PhoneDialerUtils.TAG_AUDIO_CALL){
				if (mSipService.isRegistered()) {
					if (number.startsWith("*")) {
						if (mSipService.isRegistered()
								&& !NgnStringUtils.isNullOrEmpty(number)) {
							ScreenAV.makeCall(number, NgnMediaType.Audio);
							// added by song 将通话记录添加到信息记录中 2014.7.8

							final NgnHistorySMSEvent e = new NgnHistorySMSEvent(
									number, StatusType.Failed);
							e.setContent("语音通话");
							e.setStartTime(new Date().getTime());
							mHistoryService.addEvent(e);
							mEtNumber.setText(NgnStringUtils.emptyValue());
						}

					} else {
						if (mSipService.isRegistered()
								&& !NgnStringUtils.isNullOrEmpty(number)) {
							ScreenAV.makeCall(number, NgnMediaType.Audio);
							// added by song 将通话记录添加到信息记录中 2014.7.8
							final NgnHistorySMSEvent e = new NgnHistorySMSEvent(
									number, StatusType.Failed);
							e.setContent("语音通话");
							e.setStartTime(new Date().getTime());
							mHistoryService.addEvent(e);
							mEtNumber.setText(NgnStringUtils.emptyValue());
						}

					}

				} else {
					Toast.makeText(TabContacts.this, string.toast1,
							Toast.LENGTH_SHORT).show();
					;
				}
				
			} else if(tag == PhoneDialerUtils.TAG_VIDEO_CALL){
				if (mSipService.isRegistered()) {
					if (number.startsWith("*")) {
						if (mSipService.isRegistered()
								&& !NgnStringUtils.isNullOrEmpty(number)) {
							ScreenAV.makeCall(number, NgnMediaType.AudioVideo);
							// added by song 将通话记录添加到信息记录中 2014.7.8

							final NgnHistorySMSEvent e = new NgnHistorySMSEvent(
									number, StatusType.Failed);
							e.setContent("视频通话");
							e.setStartTime(new Date().getTime());
							mHistoryService.addEvent(e);
							mEtNumber.setText(NgnStringUtils.emptyValue());
						}

					} else {
						if (mSipService.isRegistered()
								&& !NgnStringUtils.isNullOrEmpty(number)) {
							ScreenAV.makeCall(number, NgnMediaType.AudioVideo);
							// added by song 将通话记录添加到信息记录中 2014.7.8
							final NgnHistorySMSEvent e = new NgnHistorySMSEvent(
									number, StatusType.Failed);
							e.setContent("视频通话");
							e.setStartTime(new Date().getTime());
							mHistoryService.addEvent(e);
							mEtNumber.setText(NgnStringUtils.emptyValue());
						}

					}

				} else {
					Toast.makeText(TabContacts.this, string.toast1,
							Toast.LENGTH_SHORT).show();
					;
				}
			}
			//else if (tag == PhoneDialerUtils.TAG_NET_CALL) {		} 
			else {
				final String textToAppend = tag == DialerUtils.TAG_STAR ? "*"
						: (tag == DialerUtils.TAG_SHARP ? "#" : Integer
								.toString(tag));
				appendText(textToAppend);
			}
		}
	};

	/**
	 * 为号码输入框追加内容
	 * 
	 * @param textToAppend
	 */
	private void appendText(String textToAppend) {
		final int selStart = mEtNumber.getSelectionStart();
		final StringBuffer sb = new StringBuffer(mEtNumber.getText().toString());
		sb.insert(selStart, textToAppend);
		mEtNumber.setText(sb.toString());
		mEtNumber.setSelection(selStart + 1);
	}

	// 初始化联系人显示
	private void initViews() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSelectedContact = (SortModel) parent
						.getItemAtPosition(position);
				Intent i=new Intent(TabContacts.this,BuddyDetail.class);
				i.putExtra("name", mSelectedContact.getName());
				i.putExtra("number", mSelectedContact.getPhonenumber());
				startActivity(i);
				/*if (mSipService.isRegistered()) {
					if (mSelectedContact != null) {
						mLasQuickAction = new QuickAction(view);
						if (!NgnStringUtils.isNullOrEmpty(mSelectedContact
								.getPhonenumber())) {
							mLasQuickAction.addActionItem(mAItemVoiceCall);
							// mLasQuickAction.addActionItem(mAItemVideoCall);
							// mLasQuickAction.addActionItem(mAItemChat);
							mLasQuickAction.addActionItem(mAItemSMS);
							// mLasQuickAction.addActionItem(mAItemShare);
						}
						mLasQuickAction.setAnimStyle(QuickAction.ANIM_AUTO);
						mLasQuickAction.show();
					}
				} else {// 未登录时提示登录 by wangy 20140620
					Toast.makeText(getApplicationContext(), "请先登录！",
							Toast.LENGTH_SHORT).show();
				}*/
			}
		});

		SourceDateList = get();

		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);

		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(String[] date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.length; i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	private List<SortModel> get() {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		synchronized (mContacts) {
			List<NgnContact> contacts = mContacts.getList();
			int i = 0;
			while (contacts.size() < 2 && i < 1000) {
				contacts = mContacts.getList();
				i++;
			}

			String displayName;
			String number;

			for (NgnContact contact : contacts) {
				displayName = contact.getDisplayName();
				number = contact.getPrimaryNumber();
				if (NgnStringUtils.isNullOrEmpty(displayName)) {
					continue;
				}
				SortModel sortModel = new SortModel();
				sortModel.setName(displayName);
				sortModel.setPhonenumber(number);
				// 汉字转换成拼音
				String pinyin = characterParser.getSelling(displayName);
				String sortString = pinyin.substring(0, 1).toUpperCase();

				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}

				mSortList.add(sortModel);

			}
		}
		return mSortList;
	}
}
