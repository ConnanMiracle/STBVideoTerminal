package org.imshello.droid.Screens;

import org.imshello.droid.R;
import org.imshello.ngn.services.INgnConfigurationService;
import org.imshello.ngn.utils.NgnConfigurationEntry;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ScreenSetAnswerPhone extends BaseScreen{
	private final static String TAG = SettingsQoS.class.getCanonicalName();
	private final INgnConfigurationService mConfigurationService;
	
	private EditText Ans_Timer;
	private RadioButton Ans_on;
	private RadioButton Ans_off;
	private RadioGroup Ans_switch;
	private ImageView back;
	
	public ScreenSetAnswerPhone() {
		// TODO Auto-generated constructor stub
		super(SCREEN_TYPE.ANSWERPHONE_T, TAG);
		// Services
		mConfigurationService = getEngine().getConfigurationService();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_set_answerphone);
		
		Ans_Timer = (EditText)findViewById(R.id.Ans_Timer_Edit);
        Ans_on = (RadioButton)findViewById(R.id.Ans_RadioButton_on);
        Ans_off = (RadioButton)findViewById(R.id.Ans_RadioButton_off);
        Ans_switch=(RadioGroup)findViewById(R.id.Ans_RadioGroup);
        Ans_Timer.setText(mConfigurationService.getString(NgnConfigurationEntry.ANS_TIME, NgnConfigurationEntry.DEFAULT_ANS_TIME));
      
     
        Ans_on.setChecked(mConfigurationService.getString(NgnConfigurationEntry.ANS_STATUS,
        		NgnConfigurationEntry.DEFAULT_ANS_STATUS).equalsIgnoreCase("ON"));
        Ans_off.setChecked(!Ans_on.isChecked());
        
        // add listeners (for the configuration)
        super.addConfigurationListener(Ans_Timer);

        super.addConfigurationListener(Ans_on);
        super.addConfigurationListener(Ans_off);
        Ans_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	            
        	@Override
        	public void onCheckedChanged(RadioGroup arg0, int arg1) {
	        	// TODO Auto-generated method stub
	        	//获取变更后的选中项的ID
	        	int radioButtonId = arg0.getCheckedRadioButtonId();        	        	
        	}
        });
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mScreenService.show(ScreenSet.class);
			}
		});
	}
	
	@Override
	protected void onPause() {
		if(super.mComputeConfiguration){
			
			mConfigurationService.putString(NgnConfigurationEntry.ANS_TIME, 
					Ans_Timer.getText().toString().trim());
			mConfigurationService.putString(NgnConfigurationEntry.ANS_STATUS, 
					Ans_on.isChecked() ? "ON" : "OFF");
			
			// Compute
			if(!mConfigurationService.commit()){
				Log.e(TAG, "Failed to commit() configuration");
			}
			
			super.mComputeConfiguration = false;
			Log.d("AnswerPhone","AnswerPhone Setting changed.");
		}
		super.onPause();
	}
}