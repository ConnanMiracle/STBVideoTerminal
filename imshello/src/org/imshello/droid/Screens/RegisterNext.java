package org.imshello.droid.Screens;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.imshello.droid.R;
public class RegisterNext extends BaseScreen{

	
	private TextView nextuser;
	private TextView nextpass;
	private Button back;
	private static String TAG = RegisterNext.class.getCanonicalName();
	public RegisterNext() {
		super(SCREEN_TYPE.REGIST_T, TAG);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registernext);
		
		nextuser = (TextView)findViewById(R.id.next_user);
		nextpass = (TextView)findViewById(R.id.next_pass);
		back = (Button)findViewById(R.id.registernext);
		
		if(Register.user!=""&&Register.pass!="")
		{
			nextuser.setText(Register.user);
			nextpass.setText(Register.pass);
		}
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mScreenService.show(Login.class);
			}
		});
		
	}
	
}
