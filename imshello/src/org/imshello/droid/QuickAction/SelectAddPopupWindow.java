package org.imshello.droid.QuickAction;




import org.imshello.droid.R;
import org.imshello.droid.Screens.BasePopupScreen;
import org.imshello.droid.Screens.ScreenChooseChatMember;
import org.imshello.droid.Screens.BaseScreen.SCREEN_TYPE;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class SelectAddPopupWindow extends BasePopupScreen {

	private Button  btn_add_friends;
	private View mMenuView;
	private  Button btn_new_multiSession;
	private static String TAG = SelectAddPopupWindow.class.getCanonicalName();

	public SelectAddPopupWindow(final Activity context,OnClickListener itemsOnClick) {
		super(SCREEN_TYPE.SPLASH_T, TAG);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.add_select_menu, null);
		
		btn_add_friends=(Button)mMenuView.findViewById(R.id.btn_add_friends);
		btn_new_multiSession=(Button)mMenuView.findViewById(R.id.btn_new_mutilSession);
		
		btn_new_multiSession.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO �Զ����ɵķ������
				mScreenService.show(ScreenChooseChatMember.class);
				dismiss();
			}});
		
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		//���ð�ť����
		//����SelectPicPopupWindow��View
		this.setContentView(mMenuView);
		//����SelectPicPopupWindow��������Ŀ�
		this.setWidth(w/2+50);
		//����SelectPicPopupWindow��������ĸ�
		this.setHeight(LayoutParams.WRAP_CONTENT);
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
				
				int height = mMenuView.findViewById(R.id.pop_layout_add).getTop();
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
