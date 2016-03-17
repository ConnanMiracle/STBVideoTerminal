/*
 * This Screen
 * 
 * 
 * */
package org.imshello.droid.Screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.imshello.droid.Engine;
import org.imshello.droid.R;
import org.imshello.droid.Sortcontacts.CharacterParser;
import org.imshello.droid.Sortcontacts.PinyinComparator;
import org.imshello.droid.Sortcontacts.SortAdapter;
import org.imshello.droid.Sortcontacts.SortModel;
import org.imshello.ngn.model.NgnContact;
import org.imshello.ngn.utils.NgnObservableList;
import org.imshello.ngn.utils.NgnStringUtils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class ScreenChooseChatMember extends BaseScreen {
	private static String TAG = ScreenChooseChatMember.class.getCanonicalName();
	private Button confirmBtn;
	private ListView list;
	private ImageView back;
	private TextView chosenBuddiesDisplay;
	private String chosenBuddiesTemp;
	private String[] buddies;//all buddies
	private String[] participants;// members of the multi-session
	private int numOfParticipants;
	private SortModel mSelectedContact;
	private final NgnObservableList<NgnContact> mContacts=Engine.getInstance().getContactService()
				.getObservableContacts();
	private CharacterParser characterParser;//汉字转拼音类的实例
	private List<SortModel> SourceDateList;
	private PinyinComparator pinyinComparator;//按拼音排列ListView内容类的实例
	private SortAdapter adapter;
	
	public ScreenChooseChatMember() {
		super(SCREEN_TYPE.ME_T, TAG);
		// TODO 自动生成的构造函数存根
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_multisession_member);
		confirmBtn=(Button)this.findViewById(R.id.confrim_btn);
		list=(ListView)this.findViewById(R.id.choose_buddy_listView);
		chosenBuddiesDisplay=(TextView)this.findViewById(R.id.chosen_buudies_textView_display);
		back = (ImageView)findViewById(R.id.back);
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		initialize();
	}
	private void initialize() {
		numOfParticipants=0;
		// TODO initialize the String arrays and bind the listView with an adapter and set the listener for button and listView
		participants=new String [10];
		//list.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,buddies));
		SourceDateList = get();

		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		list.setAdapter(adapter);
		list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

		
		confirmBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				//At least one member is selected, start chat, else make a Toast.
				if (participants[0]!=null) {
					// TODO To start the multichat with the selected buddies array when buddies are selected
					//Intent i = new Intent();
					//i.putExtra("participants", participants);
					//startActivity(i);
					ScreenMultiChat.startChat(participants, true,numOfParticipants);
					//mScreenService.show(ScreenMultiChat.class);
					//ScreenChooseChatMember.this.finish();
				}else {
					Toast.makeText(getApplicationContext(), "没有选择聊天成员", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		list.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int position, long id) {
				// TODO add selected buddies to a array

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO 自动生成的方法存根
				
			}
			
		});
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mSelectedContact = (SortModel) parent
						.getItemAtPosition(position);
				//find out the first blank item of "participants" array, and add the selected item to it.
				boolean hasChosen=false;
				for(String tmp: participants){
					//if the participants array already contains the selected item, break.
					if(tmp==mSelectedContact.getName()){
						Toast.makeText(getApplicationContext(), "已经选择了该好友", Toast.LENGTH_SHORT).show();
						hasChosen=true;
						break;
					}
				}
				int count=0;
				for(String tmp: participants){					
					//if not chosen, add it to the display text.
					 if(tmp==null){
							if (!hasChosen) {
								numOfParticipants++;
								tmp = mSelectedContact.getName();
								participants[count]=tmp;
								if(count==0)
									chosenBuddiesTemp=tmp;
								else
									chosenBuddiesTemp = chosenBuddiesTemp +"  ;"+ tmp;
								chosenBuddiesDisplay.setText(chosenBuddiesTemp);
								break;
							}
					}
					count++; 
				}

			}
			
		});
        back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mScreenService.show(ScreenMain.class);
			}
		});
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
