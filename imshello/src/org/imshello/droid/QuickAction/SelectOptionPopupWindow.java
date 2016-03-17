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
	SaosaoPopupWindow menuWindow3;  //二维码弹出框
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
			name.setText(t+"("+"已登录"+")");
		}
		else
		{
			String t = name.getText().toString();
			name.setText(t+"("+"未登录"+")");
		}
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		btn_cancel = (Button) mMenuView.findViewById(R.id.btn_exit);
		//取消按钮
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//销毁弹出框
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
		//设置按钮监听
		//设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		//设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(w/2+50);
		//设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		//设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		//设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.mystyle);
		//实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		//设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		//mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
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
	
	 //为弹出二维码图片添加监听 
	 public void saoImageOnClick(final Activity context)
	 {
		 menuWindow3 = new SaosaoPopupWindow(context);
		 menuWindow3.showAtLocation(context.findViewById(R.id.key), Gravity.CENTER, 0, 0); //设置layout在PopupWindow中显示的位置
	 }
	
	 
}
