package org.imshello.droid.Utils;

import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EmotionUtils {
	public char strarray[];
	public Emotions emotion;
	public Map<String, Integer> emotions_map;
	//private ResHandler handler;
	private class MyURLSpan extends ClickableSpan{  
		MyURLSpan(String url) { 

		} 

		@Override 
		public void onClick(View widget) {  
			// TODO Auto-generated method stub

		}     
	}
	
	
	
	public void addAllSpan(Context context, String str, TextViewFixTouchConsume textView,boolean isClickAble,int weibo_type) {
		try {
			SpannableString ss = new SpannableString(str);
			strarray = str.toCharArray();
			int l = 0;
			if(str.length()>10){
				l = str.length() - 10;
				for (int i = 0; i < l; i++) {  

					if (strarray[i] == 'h' && strarray[i + 1] == 't'  
							&& strarray[i + 2] == 't' && strarray[i + 3] == 'p'  
							&& strarray[i + 4] == ':' && strarray[i + 5] == '/'  
							&& strarray[i + 6] == '/') {  
						StringBuffer sb = new StringBuffer("http://");  
						for (int j = i + 7; true; j++) {  
							if (j < strarray.length && strarray[j] != ' ' && strarray[j] != ';' && strarray[j] != '；' && strarray[j] != ',' 
									&& strarray[j] != '，' && strarray[j] != '。' && strarray[j] != '[' && strarray[j] != ']' && strarray[j] != '【' && strarray[j] != '】' && strarray[j] != '@'
									&& strarray[j] != '(' && strarray[j] != '（' && strarray[j] != '）' && strarray[j] != ')' && strarray[j] != '#' && strarray[i] != '、')  
								sb.append(strarray[j]);  
							else {
								//MyURLSpan1 myURLSpan = new MyURLSpan1(sb.toString(),context);   
								ss.setSpan(null, i, j,  
										Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
								i = j;  
								break;  
							}  
						}  
					}  

				}
			}

			// 处理@
			l = str.length();
			StringBuffer sb = null;
			boolean start = false;
			int startIndex = 0;
			int countAt = 0;
			for (int i = 0; i < l; i++) {
				if (strarray[i] == '@') {
					countAt++;
					if(countAt > 1 && strarray[i-1] != '\0' && strarray[i-1] != ':' && strarray[i-1] != '：' && strarray[i-1] != ' ' && strarray[i-1] != ',' && strarray[i-1] != '，' 
							&& strarray[i-1] != '。' && strarray[i-1] != '/' && strarray[i-1] != '[' && strarray[i-1] != ']' && strarray[i-1] != '【' 
							&& strarray[i-1] != '】' && strarray[i-1] != '(' && strarray[i-1] != ')' && strarray[i-1] != '（' && strarray[i-1] != '）' || strarray[i] == '、'){
						if(sb != null && sb.length()>1){
							ss.setSpan(null, startIndex,
									i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						sb = null;
						start = false;
					}					
					start = true;
					sb = new StringBuffer();
					startIndex = i;
				} else {
					if (start) {
						if (strarray[i] == ':' || strarray[i] == '：' || strarray[i] == ' ' || strarray[i] == ',' || strarray[i] == '，' || strarray[i] == '、' 
								|| strarray[i] == '。' || strarray[i] == '/' || strarray[i] == '[' || strarray[i] == ']'|| strarray[i] == '【' 
								|| strarray[i] == '】' || strarray[i] == '(' || strarray[i] == ')' || strarray[i] == '（' || strarray[i] == '）') {
							if(sb.length()>1){
								ss.setSpan(null, startIndex,
										i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							}
							sb = null;
							start = false;

						} else if(i == l-1){
							sb.append(strarray[i]);
							if(sb.length()>1){		
								ss.setSpan(null, startIndex,
										i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							}
							sb = null;
							start = false;

						} else {						
							sb.append(strarray[i]);
						}
					}
				}

			}
			// 处理 话题
			start = false;
			startIndex = 0;
			for (int i = 0; i < l; i++) {
				if (strarray[i] == '#') {
					if (!start) {
						start = true;
						sb = new StringBuffer("weibo://weibo.view/");
						startIndex = i;
					} else {
						sb.append('#');
						ss.setSpan(new MyURLSpan(sb.toString()), startIndex,
								i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						sb = null;
						start = false;
					}
				} else {
					if (start) {
						sb.append(strarray[i]);
					}
				}
			}

			// 处理显示表情
			String content = str;
			int i = 0;
			int starts = 0;
			int end = 0;
			while (i < content.length()) {								
				starts = content.indexOf("[", i);
				if (starts == -1) {
					break;
				}
				end = content.indexOf("]", starts);
				if (end == -1) {
					break;
				}
				if(end <= starts){
					if (starts >= i) {
						i = starts;
					}
					i++;
					break;
				}
				String phrase = content.substring(starts + 1, end);

				emotion = new Emotions();
				emotions_map = emotion.getEmotionsList_text();
				if (emotions_map.get(phrase) != null) {

					try {
						Drawable drawable = context.getResources()
								.getDrawable(emotions_map.get(phrase));
						if (drawable != null) {
							drawable.setBounds(0, DisplayUtils.dip2px(context, 5f),
									DisplayUtils.dip2px(context, 18f),
									DisplayUtils.dip2px(context, 23f));
							ImageSpan span = new ImageSpan(drawable,
									DynamicDrawableSpan.ALIGN_BASELINE);
							ss.setSpan(span, starts, end + 1,
									Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}

				}

				if (end >= i) {
					i = end;
				}
				i++;

			}
			textView.setText(ss); // 设定TextView话题和url和好友 连接
			if(isClickAble){
				textView.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());	
			}			
			strarray = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void addAllSpan(Context context, String str, TextView textView,boolean isClickAble,int weibo_type) {
		try {
			SpannableString ss = new SpannableString(str);
			strarray = str.toCharArray();
			int l = 0;
			if(str.length()>10){
				l = str.length() - 10;
				for (int i = 0; i < l; i++) {  

					if (strarray[i] == 'h' && strarray[i + 1] == 't'  
							&& strarray[i + 2] == 't' && strarray[i + 3] == 'p'  
							&& strarray[i + 4] == ':' && strarray[i + 5] == '/'  
							&& strarray[i + 6] == '/') {  
						StringBuffer sb = new StringBuffer("http://");  
						for (int j = i + 7; true; j++) {  
							if (j < strarray.length && strarray[j] != ' ')  
								sb.append(strarray[j]);  
							else {
								//MyURLSpan1 myURLSpan = new MyURLSpan1(sb.toString(),context);   
								ss.setSpan(null, i, j,  
										Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
								i = j;  
								break;  
							}  
						}  
					}  

				}
			}

			// 处理@
			l = str.length();
			StringBuffer sb = null;
			boolean start = false;
			int startIndex = 0;
			for (int i = 0; i < l; i++) {
				if (strarray[i] == '@') {
					start = true;
					sb = new StringBuffer();
					startIndex = i;
				} else {
					if (start) {
						if (strarray[i] == ':' || strarray[i] == '：' || strarray[i] == ' ' || strarray[i] == ',' || strarray[i] == '，' 
								|| strarray[i] == '。' || strarray[i] == '/' || strarray[i] == '[' || strarray[i] == ']'|| strarray[i] == '【' 
								|| strarray[i] == '】' || strarray[i] == '(' || strarray[i] == ')' || strarray[i] == '（' || strarray[i] == '）') {
							if(sb.length()>1){
								ss.setSpan(null, startIndex,
										i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							}
							sb = null;
							start = false;

						} else if(i == l-1){
							sb.append(strarray[i]);
							if(sb.length()>1){							
								ss.setSpan(null, startIndex,
										i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							}
							sb = null;
							start = false;

						} else {						
							sb.append(strarray[i]);
						}
					}
				}

			}
			// 处理 话题
			start = false;
			startIndex = 0;
			for (int i = 0; i < l; i++) {
				if (strarray[i] == '#') {
					if (!start) {
						start = true;
						sb = new StringBuffer("weibo://weibo.view/");
						startIndex = i;
					} else {
						sb.append('#');
						ss.setSpan(new MyURLSpan(sb.toString()), startIndex,
								i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						sb = null;
						start = false;
					}
				} else {
					if (start) {
						sb.append(strarray[i]);
					}
				}
			}

			// 处理显示表情
			String content = str;
			int i = 0;
			int starts = 0;
			int end = 0;
			while (i < content.length()) {								
				starts = content.indexOf("[", i);
				if (starts == -1) {
					break;
				}
				end = content.indexOf("]", starts);
				if (end == -1) {
					break;
				}
				if(end <= starts){
					if (starts >= i) {
						i = starts;
					}
					i++;
					break;
				}
				String phrase = content.substring(starts + 1, end);

				emotion = new Emotions();
				emotions_map = emotion.getEmotionsList_text();
				if (emotions_map.get(phrase) != null) {

					try {
						Drawable drawable = context.getResources()
								.getDrawable(emotions_map.get(phrase));
						if (drawable != null) {
							drawable.setBounds(0, DisplayUtils.dip2px(context, 5f),
									DisplayUtils.dip2px(context, 18f),
									DisplayUtils.dip2px(context, 23f));
							ImageSpan span = new ImageSpan(drawable,
									DynamicDrawableSpan.ALIGN_BASELINE);
							ss.setSpan(span, starts, end + 1,
									Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}

				}

				if (end >= i) {
					i = end;
				}
				i++;

			}
			textView.setText(ss); // 设定TextView话题和url和好友 连接
			if(isClickAble){
				textView.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());	
				//textView.setMovementMethod(LinkMovementMethod.getInstance());
			}			
			strarray = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 单独处理显示表情
	public void addEmotionSpan(Context context, String str, EditText textView) {
		int cursor = textView.getSelectionStart();
		textView.setText(getEmotionSpannable(context, str));
		// 设定TextView话题和url和好友 连接
		Editable editable = textView.getText();
		Selection.setSelection(editable, cursor);
		strarray = null;
	}

	// 单独处理显示表情
	public void addEmotionSpanForTextView(Context context, String str, TextView textView) {		
		textView.setText(getEmotionSpannable(context, str));
		strarray = null;
	}

	public SpannableString getEmotionSpannable(Context context, String str){
		SpannableString ss = new SpannableString(str);
		String content = str;
		int i = 0;
		int starts = 0;
		int end = 0;
		while (i < content.length()) {
			starts = content.indexOf("[", i);
			if (starts == -1) {
				break;
			}
			end = content.indexOf("]", starts);
			if (end == -1) {
				break;
			}

			if(end <= starts){
				if (starts >= i) {
					i = starts;
				}
				i++;
				break;
			}

			String phrase = content.substring(starts + 1, end);

			emotion = new Emotions();
			emotions_map = emotion.getEmotionsList_text();
			if (emotions_map.get(phrase) != null) {

				try {
					Drawable drawable = context.getResources().getDrawable(
							emotions_map.get(phrase));
					if (drawable != null) {
						// drawable.setBounds(0, 0,
						// drawable.getIntrinsicWidth(),
						// drawable.getIntrinsicHeight());
						drawable.setBounds(0, DisplayUtils.dip2px(context, 5f),
								DisplayUtils.dip2px(context, 20f),
								DisplayUtils.dip2px(context, 25f));
						ImageSpan span = new ImageSpan(drawable,
								DynamicDrawableSpan.ALIGN_BASELINE);
						ss.setSpan(span, starts, end + 1,
								Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}

			}

			if (end >= i) {
				i = end;
			}
			i++;

		}
		return ss;
	}



}
