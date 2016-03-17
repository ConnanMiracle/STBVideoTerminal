package org.imshello.droid.Utils;

import java.util.HashMap;

import org.imshello.droid.R;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
public class PhoneDialerUtils 
{
public static final int TAG_0 = 0;
public static final int TAG_1 = 1;
public static final int TAG_2 = 2;
public static final int TAG_3 = 3;
public static final int TAG_4 = 4;
public static final int TAG_5 = 5;
public static final int TAG_6 = 6;
public static final int TAG_7 = 7;
public static final int TAG_8 = 8;
public static final int TAG_9 = 9;
public static final int TAG_STAR = 10;
public static final int TAG_SHARP = 11;
public static final int TAG_CHAT = 12;
public static final int TAG_AUDIO_CALL = 13;
public static final int TAG_VIDEO_CALL = 14;
public static final int TAG_DELETE = 15;
public static final int TAG_HIDE = 16;
public static final int TAG_SHOW = 17;
public static final int TAG_CALLRECORD = 18;
public static final int TAG_MISSEDCALLRECORD = 19;
public static final int TAG_PHONE_CALL = 20;//居然有人把这些常量的值设成一样，简直坑死了！！
public static final int TAG_NET_CALL = 21;
public static HashMap<Integer,String> dialerData = new HashMap<Integer,String>();	

public static void setDialerTextButton(View view, String num, String text, int tag, View.OnClickListener listener){
	view.setTag(tag);
	dialerData.put(tag, text);
	view.setOnClickListener(listener);
	((TextView)view.findViewById(R.id.dialer_text_num)).setText(num);
	((TextView)view.findViewById(R.id.dialer_text)).setText(text);
}

public static void setDialerTextButton(Activity parent, int viewId, String num, String text, int tag, View.OnClickListener listener){
	setDialerTextButton(parent.findViewById(viewId), num, text, tag, listener);
}

public static void setDialerTextButton(View parent, int viewId, String num, String text, int tag, View.OnClickListener listener){
	setDialerTextButton(parent.findViewById(viewId), num, text, tag, listener);
}

/*public static void setDialerImageButton(Activity parent, int viewId, int imageId, int tag, View.OnClickListener listener){
	View view = parent.findViewById(viewId);
	view.setTag(tag);
	view.setOnClickListener(listener);
	((ImageView)view.findViewById(R.id.dialer_button_image)).setImageResource(imageId);
}*/
public static void setDialerImageButton(Activity parent, int viewId, int imageId, int tag, View.OnClickListener listener){
	View view = parent.findViewById(viewId);
	view.setTag(tag);
	view.setOnClickListener(listener);
	((ImageView)view.findViewById(R.id.dialer_button_image)).setImageResource(imageId);
//	if(tag == TAG_PHONE_CALL){
	//	((TextView)view.findViewById(R.id.dialer_button_text)).setText("手机呼叫");
	//} else if(tag == TAG_NET_CALL){
	//	((TextView)view.findViewById(R.id.dialer_button_text)).setText("网络呼叫");
	//}
	
}

public static void setDialerImageButton(View view, int tag, View.OnClickListener listener){
	view.setTag(tag);
	view.setOnClickListener(listener);
}

}
