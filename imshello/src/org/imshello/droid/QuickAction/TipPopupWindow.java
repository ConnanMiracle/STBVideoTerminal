package org.imshello.droid.QuickAction;

import org.imshello.droid.R;
import org.imshello.droid.Screens.BasePopupScreen;
import org.imshello.droid.Screens.BaseScreen.SCREEN_TYPE;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;


public class TipPopupWindow extends BasePopupScreen{

	private View mMenuView;
	private TextView image;
	private static String TAG = TipPopupWindow.class.getCanonicalName();
	public TipPopupWindow(final Activity context) {
		super(SCREEN_TYPE.SPLASH_T, TAG);
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.screen_tip, null);
		image = (TextView)mMenuView.findViewById(R.id.tip);
		
		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		//设置按钮监听
		//设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		//设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(w/2+100);
		//设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(350);
		//设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		//设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.Animations_PopDownMenu_Left);
		//实例化一个ColorDrawable颜色为半透明
		//Drawable dw = new
		//设置SelectPicPopupWindow弹出窗体的背景
		
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
	private Object getResources() {
		// TODO Auto-generated method stub
		return null;
	}

}
