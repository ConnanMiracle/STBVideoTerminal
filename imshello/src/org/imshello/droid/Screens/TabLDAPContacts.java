package org.imshello.droid.Screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.imshello.droid.Engine;
import org.imshello.droid.R;
import org.imshello.droid.QuickAction.ActionItem;
import org.imshello.droid.QuickAction.QuickAction;
import org.imshello.droid.Sortcontacts.CharacterParser;
import org.imshello.droid.Sortcontacts.ClearEditText;
import org.imshello.droid.Sortcontacts.PinyinComparator;
import org.imshello.droid.Sortcontacts.SideBar;
import org.imshello.droid.Sortcontacts.SortAdapter;
import org.imshello.droid.Sortcontacts.SortModel;
import org.imshello.droid.Sortcontacts.SideBar.OnTouchingLetterChangedListener;
import org.imshello.droid.Utils.DataBaseControl;
import org.imshello.droid.Utils.LDAPControl;
import org.imshello.droid.Utils.Ldap_contacts;
import org.imshello.ngn.media.NgnMediaType;
import org.imshello.ngn.model.NgnContact;
import org.imshello.ngn.model.NgnHistorySMSEvent;
import org.imshello.ngn.model.NgnHistoryEvent.StatusType;
import org.imshello.ngn.services.INgnContactService;
import org.imshello.ngn.services.INgnHistoryService;
import org.imshello.ngn.services.INgnSipService;
import org.imshello.ngn.utils.NgnObservableList;
import org.imshello.ngn.utils.NgnStringUtils;

import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResultEntry;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class TabLDAPContacts extends BaseScreen {
	
	private static String TAG = TabLDAPContacts.class.getCanonicalName();
	
	private EditText mEtNumber; //号码输入框
	private ImageButton dialerShowButton,
				callRecordButton,missedCallRecordButton,dialerHideButton;
	private LinearLayout dialerLayout;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private LinearLayout dialerExpandLayout;

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
	private  final INgnHistoryService mHistoryService;
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
  
	//ldap相关
	private LDAPControl control = null;
	private SearchResult ldap_result;
	Handler handler;
	private ProgressBar pbar;
	//判断是否联网
		 ConnectivityManager connectivityManager;
		NetworkInfo networkInfo;
		private TextView tip;
	//数据库操作相关
		 List<Ldap_contacts> contactsList;
	
	
	public TabLDAPContacts() {
		super(SCREEN_TYPE.SORT_CONTACTS, TAG);
		// TODO Auto-generated constructor stub
		
		//mContacts.addObserver((Observer) MainActivity.this);
		
		mContactService = getEngine().getContactService();
		mSipService = getEngine().getSipService();
		mHistoryService = getEngine().getHistoryService();
		mContacts = Engine.getInstance().getContactService().getObservableContacts();
		mAItemVoiceCall = new ActionItem();
		mAItemVoiceCall.setTitle("语音");
		mAItemVoiceCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mSelectedContact != null){
					ScreenSplash.count = 1;
					ScreenAV.makeCall(mSelectedContact.getPhonenumber(), NgnMediaType.Audio);
					//added by song 将通话记录添加到信息记录中 2014.7.8
					final NgnHistorySMSEvent e = new NgnHistorySMSEvent(mSelectedContact.getPhonenumber(), StatusType.Failed);
					e.setContent("语音通话");
					e.setStartTime(new Date().getTime());
					mHistoryService.addEvent(e);
					if(mLasQuickAction != null){
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
				if(mSelectedContact != null){
					ScreenAV.makeCall(mSelectedContact.getPhonenumber(), NgnMediaType.AudioVideo);
					if(mLasQuickAction != null){
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
				if(mLasQuickAction != null){
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
				if(mLasQuickAction != null){
					mLasQuickAction.dismiss();
				}
			}
		});
		
		mAItemShare = new ActionItem();
		mAItemShare.setTitle("Share");
		mAItemShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mSelectedContact != null){
					Intent intent = new Intent();
                    intent.setType("*/*")
                    	.addCategory(Intent.CATEGORY_OPENABLE)
                    	.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select content"), SELECT_CONTENT);   
					if(mLasQuickAction != null){
						mLasQuickAction.dismiss();
					}
				}
			}
		});
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ldap_contacts_main);
		pbar = (ProgressBar)findViewById(R.id.bar);
		tip = (TextView)findViewById(R.id.tip);
		connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		 networkInfo = connectivityManager.getActiveNetworkInfo();
		 
		
		 //检测是否联网
		 if(networkInfo == null || !networkInfo.isAvailable())
			{
			 tip.setVisibility(View.VISIBLE);
			// Net_Listen();
			}
		 else
		 {
		search();   
		 }
			
		 //handler处理子进程发来的消息
		 handler=new Handler()
	       {
	          @Override
			public void handleMessage(Message msg)
	             {
	              if(msg.what==0)
	               {
	  				tip.setVisibility(View.GONE);
	            	pbar.setVisibility(View.GONE);
	                initViews();
	               }
	              else if(msg.what==1)
	              {
	            	  search();
	            	  
	              }
	            }
	       };
	       
	}
	
	public void Net_Listen()
	{
		Runnable runnable1=new Runnable()
	     {
			@Override
			public void run() 
	          {
					while(networkInfo == null || !networkInfo.isAvailable())
					{
					connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
					networkInfo = connectivityManager.getActiveNetworkInfo();
				    }
	            	
					 handler.sendMessage(handler.obtainMessage(1));
	          }
	    };
	    new Thread(runnable1).start();
	}
	
	
	
	/**
//	 * 获取联系人并添加到listview中
//	 */
	public void search(){	
		
		//子线程获取数据
		Runnable runnable=new Runnable()
	     {
	         @Override
			public void run() 
	          {
	             if(control == null){
				    control = new LDAPControl();
					}		
	            pbar.setVisibility(View.VISIBLE);
				ldap_result  = control.search1();			
			    handler.sendMessage(handler.obtainMessage(0));
	          }
	     };
	         new Thread(runnable).start();
	   //handler消息 数据获取后通知listview加载数据
	      
	         
	}
	
	
	//初始化联系人显示
	private void initViews() {
		//实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		
		pinyinComparator = new PinyinComparator();
		
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		
		//设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					sortListView.setSelection(position);
				}
				
			}
		});
		
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSelectedContact = (SortModel)parent.getItemAtPosition(position);
				
				if(mSipService.isRegistered()){			
					if(mSelectedContact != null){
						mLasQuickAction = new QuickAction(view);
						if(!NgnStringUtils.isNullOrEmpty(mSelectedContact.getPhonenumber())){
							mLasQuickAction.addActionItem(mAItemVoiceCall);
							//mLasQuickAction.addActionItem(mAItemVideoCall);
							//mLasQuickAction.addActionItem(mAItemChat);
							//mLasQuickAction.addActionItem(mAItemSMS);
							//mLasQuickAction.addActionItem(mAItemShare);
						}							
						mLasQuickAction.setAnimStyle(QuickAction.ANIM_AUTO);
						mLasQuickAction.show();
					}
				}
				else {//未登录时提示登录 by wangy 20140620
					Toast.makeText(getApplicationContext(), "请先登录！", Toast.LENGTH_SHORT).show();
				}
			}
		});
	
		SourceDateList = get(ldap_result);
		
		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);
		
		
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		
		//根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
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
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(String [] date){
		List<SortModel> mSortList = new ArrayList<SortModel>();
		
		for(int i=0; i<date.length; i++){
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);
			//汉字转换成拼音
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase());
			}else{
				sortModel.setSortLetters("#");
			}
			
			mSortList.add(sortModel);
		}
		return mSortList;
		
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		}else{
			filterDateList.clear();
			for(SortModel sortModel : SourceDateList){
				String name = sortModel.getName();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(sortModel);
				}
			}
		}
		
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}
	
	
	private List<SortModel> get(SearchResult result)
	{
		List<SortModel> mSortList = new ArrayList<SortModel>();
	  if(result!=null)
	  {
			final ResultCode resultCode = result.getResultCode();
		      if (resultCode == ResultCode.SUCCESS) {
		    	  final int Count = result.getEntryCount();	    	  
		          for(int i = 0;i<Count;i++){	        	   
		        	   SearchResultEntry entry = result.getSearchEntries().get(i);
		        	   String name = entry.getAttributeValue("cn");
		        	   Log.w("cn", name);
		        	   String ou = entry.getAttributeValue("ou");
		        	   String mobileNum = entry.getAttributeValue("mobile");
		        	   SortModel sortModel = new SortModel();
		        	   
		        	   sortModel.setName(name);
		        	   sortModel.setPhonenumber(mobileNum);
		        		//汉字转换成拼音
						String pinyin = characterParser.getSelling(name);
						String sortString = pinyin.substring(0, 1).toUpperCase();
						
						// 正则表达式，判断首字母是否是英文字母
						if(sortString.matches("[A-Z]")){
							sortModel.setSortLetters(sortString.toUpperCase());
						}else{
							sortModel.setSortLetters("#");
							
							
							Ldap_contacts contacts= new Ldap_contacts(name,mobileNum);
							saveContactsDataToDB(contacts);
							
						}
						mSortList.add(sortModel);
		          }
		      }
	  }
	  else
	  {
		  
	  }
				
		return mSortList;
	}

	/**
	 * 将联系人存入数据库
	 * @param contacts
	 */
	private void saveContactsDataToDB(Ldap_contacts contacts){
		DataBaseControl db = new DataBaseControl(this);
		db.saveContactsToDatabase(contacts);
		db.close();
	}
	
	//从数据库中取出数据
	private ArrayList<Ldap_contacts> getContactsData(){
		ArrayList<Ldap_contacts> persons = null; 
		DataBaseControl db = new DataBaseControl(this);
		persons = db.getContactsList();
		db.close();
		return persons;
	}
	
	private List<SortModel> getDB()
	{
		List<SortModel> mSortList = new ArrayList<SortModel>();
		List<Ldap_contacts>  list = getContactsData(); 
		int Count =  contactsList .size();
			  
		          for(int i = 0;i<Count;i++){	        	   
		        	   Ldap_contacts entry = contactsList.get(i);
		        	   String name = entry.name;
		        	   Log.w("cn", name);
		        	  
		        	   String mobileNum = entry.mobileNum;
		        	   SortModel sortModel = new SortModel();
		        	   
		        	   sortModel.setName(name);
		        	   sortModel.setPhonenumber(mobileNum);
		        		//汉字转换成拼音
						String pinyin = characterParser.getSelling(name);
						String sortString = pinyin.substring(0, 1).toUpperCase();
						
						// 正则表达式，判断首字母是否是英文字母
						if(sortString.matches("[A-Z]")){
							sortModel.setSortLetters(sortString.toUpperCase());
						}else{
							sortModel.setSortLetters("#");			
							
						}
						mSortList.add(sortModel);
		          }

				
		return mSortList;
	}

	
}
