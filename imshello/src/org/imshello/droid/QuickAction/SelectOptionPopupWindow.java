package org.imshello.droid.QuickAction;



import org.imshello.droid.R;
import org.imshello.droid.Screens.BasePopupScreen;
import org.imshello.droid.Screens.BaseScreen.SCREEN_TYPE;
import org.imshello.droid.Screens.ScreenSet;
import org.imshello.droid.controller.ControllerService;
import org.imshello.ngn.NgnEngine;
import org.imshello.ngn.services.INgnConfigurationService;
import org.imshello.ngn.services.INgnSipService;
import org.imshello.ngn.utils.NgnConfigurationEntry;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class SelectOptionPopupWindow extends BasePopupScreen {

	private Button  btn_cancel;
	private View mMenuView;
	private  Button setting;
	private Button saosao;
	SaosaoPopupWindow menuWindow3;  //��ά�뵯����
	private TextView name;
	private final INgnConfigurationService mConfigurationService;
	private final INgnSipService mSipService;
	private static String TAG = SelectOptionPopupWindow.class.getCanonicalName();
	public SelectOptionPopupWindow(final Activity context,OnClickListener itemsOnClick) {
		super(SCREEN_TYPE.SPLASH_T, TAG);
		mSipService = getEngine().getSipService();
		mConfigurationService = getEngine().getConfigurationService();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.option_select_menu, null);
		
		name = (TextView)mMenuView.findViewById(R.id.name);
		if(mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPU, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPU)!=null)
		{
		String s = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPU, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPU);
		String a1[] = s.split(":");
		String a2[] = a1[1].split("@");
		name.setText(a2[0]);
		}
		if(mSipService.isRegistered())
		{
			String t = name.getText().toString();
			name.setText(t+"("+"�ѵ�¼"+")");
		}
		else
		{
			String t = name.getText().toString();
			name.setText(t+"("+"δ��¼"+")");
		}
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		btn_cancel = (Button) mMenuView.findViewById(R.id.btn_exit);
		//ȡ����ť
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//���ٵ�����
				NgnEngine.getInstance().stop();//added by wangy 20140711
				ControllerService instance = ControllerService.getInstance();
				if(instance!=null){
					instance.shutdown();
					Log.i(TAG, "controller service is shut down!");
				}
				System.exit(0);;
			}
		});
		
		setting = (Button)mMenuView.findViewById(R.id.setting);
		setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mScreenService.show(ScreenSet.class);
				dismiss();
			}
		});
		
		saosao = (Button)mMenuView.findViewById(R.id.saosao);
		saosao.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				saoImageOnClick(context);
				
			}
		});
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
	
	 //Ϊ������ά��ͼƬ��Ӽ��� 
	 public void saoImageOnClick(final Activity context)
	 {
		 menuWindow3 = new SaosaoPopupWindow(context);
		 menuWindow3.showAtLocation(context.findViewById(R.id.key), Gravity.CENTER, 0, 0); //����layout��PopupWindow����ʾ��λ��
	 }
	
	 
}
