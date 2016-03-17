package org.imshello.droid.Screens;

import org.imshello.droid.R;

import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ChangePassword extends BaseScreen{
	
	private EditText pass;
	private EditText passrepeat;
	private Button change;
	private String pass_text;
	private String passrepeat_text;
	private ImageView back;
	private static String TAG = ChangePassword.class.getCanonicalName();
	
	public ChangePassword() {
		super(SCREEN_TYPE.CHANGE_T,TAG);
		// TODO Auto-generated constructor stub
	}


  @Override
  public void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.change_password);
	
	pass = (EditText)findViewById(R.id.pass);
	passrepeat = (EditText)findViewById(R.id.passrepeat);
	change = (Button)findViewById(R.id.change);
	
	//返回按钮
	back = (ImageView)findViewById(R.id.back);
    back.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			mScreenService.show(ScreenSet.class);
		}
	});
  
}
  
  
  private OnClickListener change_onClick = new OnClickListener() {
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		pass_text = pass.getText().toString().trim();
		passrepeat_text = passrepeat.getText().toString().trim();
		
		if(pass_text.equals(passrepeat_text))
		{
			
		}
		else
		{
			Toast.makeText(getApplicationContext(), "两次密码不同", Toast.LENGTH_SHORT).show();
		}
	}
};
  
  
  
}
