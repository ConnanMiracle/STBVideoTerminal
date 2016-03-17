package org.imshello.droid.QuickAction;

import org.imshello.droid.R;
import org.imshello.droid.Screens.BasePopupScreen;
import org.imshello.droid.Screens.BaseScreen.SCREEN_TYPE;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;


public class SaosaoPopupWindow extends BasePopupScreen{

	private View mMenuView;
	private ImageView image;
	private static String TAG = SaosaoPopupWindow.class.getCanonicalName();
	public SaosaoPopupWindow(final Activity context) {
		super(SCREEN_TYPE.SPLASH_T, TAG);
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.screen_saosao, null);
		image = (ImageView)mMenuView.findViewById(R.id.picture);
		
		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		this.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		//int h = context.getWindowManager().getDefaultDisplay().getHeight()/5;
		//int w = context.getWindowManager().getDefaultDisplay().getWidth()/5;
		//���ð�ť����
		//����SelectPicPopupWindow��View
		this.setContentView(mMenuView);
		//����SelectPicPopupWindow��������Ŀ�
		//this.setWidth(w);
		//����SelectPicPopupWindow��������ĸ�
		//this.setHeight(h);
		//����SelectPicPopupWindow��������ɵ��
		this.setFocusable(true);
		//����SelectPicPopupWindow�������嶯��Ч��
		this.setAnimationStyle(R.style.mystyle);
		//ʵ����һ��ColorDrawable��ɫΪ��͸��
		ColorDrawable dw = new ColorDrawable(0000000000);
		//����SelectPicPopupWindow��������ı���
		this.setBackgroundDrawable(dw);
		//mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ������������ٵ�����
		mMenuView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				int height = mMenuView.findViewById(R.id.pop_layout_option).getTop();
				int y=(int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}				
				return true;
			}
		});

	}

}
