package org.imshello.droid.Screens;

import org.imshello.droid.R;
import org.imshello.ngn.services.INgnConfigurationService;
import org.imshello.ngn.utils.NgnConfigurationEntry;
import org.imshello.ngn.utils.NgnStringUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SettingsUSBCamera extends BaseScreen{
	private final static String TAG = SettingsQoS.class.getCanonicalName();
	private final INgnConfigurationService mConfigurationService;
	
	//private EditText Ans_Timer;
	private RadioButton switch_on,switch_off;
	private RadioButton pixel_h264,pixel_yuyv,pixel_yuv420,pixel_nv21,pixel_rgb565;
	private RadioButton stream_raw,stream_yuv420p,stream_argb;
	private RadioButton view_bitmap,view_pixel;
	private RadioGroup RG_switch,RG_pixel,RG_stream,RG_view;
	private EditText frame_width,frame_height;
	private EditText device_name;
	private ImageView back;
	
	private int cameraStatus=1;
	private int pixelFormat=2;
	private int streamFormat=1;
	private int viewFormat=1;
	
	public SettingsUSBCamera() {
		// TODO Auto-generated constructor stub
		super(SCREEN_TYPE.ANSWERPHONE_T, TAG);
		// Services
		mConfigurationService = getEngine().getConfigurationService();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_set_usb_camera);
		
		//Ans_Timer = (EditText)findViewById(R.id.Ans_Timer_Edit);
        switch_on = (RadioButton)findViewById(R.id.usb_camera_switch_RadioButton_on);
        switch_off = (RadioButton)findViewById(R.id.usb_camera_switch_RadioButton_off);
        
        pixel_h264 = (RadioButton)findViewById(R.id.usb_camera_pixel_fmt_RadioButton_h264);
        pixel_yuyv = (RadioButton)findViewById(R.id.usb_camera_pixel_fmt_RadioButton_yuyv);
        pixel_yuv420 = (RadioButton)findViewById(R.id.usb_camera_pixel_fmt_RadioButton_yuv420);
        pixel_nv21 = (RadioButton)findViewById(R.id.usb_camera_pixel_fmt_RadioButton_nv21);
        pixel_rgb565 = (RadioButton)findViewById(R.id.usb_camera_pixel_fmt_RadioButton_rgb565);
        
        stream_raw = (RadioButton)findViewById(R.id.usb_camera_stream_fmt_RadioButton_raw);
        stream_yuv420p = (RadioButton)findViewById(R.id.usb_camera_stream_fmt_RadioButton_yuv420p);
        stream_argb = (RadioButton)findViewById(R.id.usb_camera_stream_fmt_RadioButton_argb);
        
        view_bitmap = (RadioButton)findViewById(R.id.usb_camera_view_RadioButton_bitmap);
        view_pixel = (RadioButton)findViewById(R.id.usb_camera_view_RadioButton_pixel);
        
        RG_switch=(RadioGroup)findViewById(R.id.usb_camera_switch_RadioGroup);
        RG_pixel=(RadioGroup)findViewById(R.id.usb_camera_pixel_fmt_RadioGroup);
        RG_stream=(RadioGroup)findViewById(R.id.usb_camera_stream_fmt_RadioGroup);
        RG_view=(RadioGroup)findViewById(R.id.usb_camera_view_RadioGroup);
        
        frame_width=(EditText)findViewById(R.id.usb_camera_frame_width);
        frame_height=(EditText)findViewById(R.id.usb_camera_frame_height);
        
        device_name=(EditText)findViewById(R.id.usb_camera_device_name);
      
        switch_off.setChecked(mConfigurationService.getInt(NgnConfigurationEntry.USB_CAMERA_STATUS,
        		NgnConfigurationEntry.DEFAULT_USB_CAMERA_STATUS)==0);
        switch_on.setChecked(!switch_off.isChecked());
        
        pixel_yuyv.setChecked(mConfigurationService.getInt(NgnConfigurationEntry.CAPTURE_FMT,
        		NgnConfigurationEntry.DEFAULT_CAPTURE_FMT)==2);
        pixel_h264.setChecked(mConfigurationService.getInt(NgnConfigurationEntry.CAPTURE_FMT,
        		NgnConfigurationEntry.DEFAULT_CAPTURE_FMT)==1);
        pixel_yuv420.setChecked(mConfigurationService.getInt(NgnConfigurationEntry.CAPTURE_FMT,
        		NgnConfigurationEntry.DEFAULT_CAPTURE_FMT)==3);
        pixel_nv21.setChecked(mConfigurationService.getInt(NgnConfigurationEntry.CAPTURE_FMT,
        		NgnConfigurationEntry.DEFAULT_CAPTURE_FMT)==4);
        pixel_rgb565.setChecked(mConfigurationService.getInt(NgnConfigurationEntry.CAPTURE_FMT,
        		NgnConfigurationEntry.DEFAULT_CAPTURE_FMT)==5);
        
        stream_yuv420p.setChecked(mConfigurationService.getInt(NgnConfigurationEntry.STREAM_FMT,
        		NgnConfigurationEntry.DEFAULT_STREAM_FMT)==1);
        stream_raw.setChecked(mConfigurationService.getInt(NgnConfigurationEntry.STREAM_FMT,
        		NgnConfigurationEntry.DEFAULT_STREAM_FMT)==0);
        stream_argb.setChecked(mConfigurationService.getInt(NgnConfigurationEntry.STREAM_FMT,
        		NgnConfigurationEntry.DEFAULT_STREAM_FMT)==2);
        
        view_bitmap.setChecked(mConfigurationService.getInt(NgnConfigurationEntry.LOCAL_VIEW,
        		NgnConfigurationEntry.DEFAULT_LOCAL_VIEW)==1);
        view_pixel.setChecked(!view_bitmap.isChecked());
        
        frame_width.setText(String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.FRAME_WIDTH,
        		NgnConfigurationEntry.DEFAULT_FRAME_WIDTH)));
        frame_height.setText(String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.FRAME_HEIGHT,
        		NgnConfigurationEntry.DEFAULT_FRAME_HEIGHT)));
        
        device_name.setText(String.valueOf(mConfigurationService.getString(NgnConfigurationEntry.USB_CAMERA_DEVICE_NAME,
        		NgnConfigurationEntry.DEFAULT_USB_CAMERA_DEVICE_NAME)));

        
        /*Ans_on.setChecked(mConfigurationService.getString(NgnConfigurationEntry.ANS_STATUS,
        		NgnConfigurationEntry.DEFAULT_ANS_STATUS).equalsIgnoreCase("ON"));
        Ans_off.setChecked(!Ans_on.isChecked());*/
        
        // add listeners (for the configuration)
        super.addConfigurationListener(switch_on);
        super.addConfigurationListener(switch_off);
        
        super.addConfigurationListener(pixel_h264);
        super.addConfigurationListener(pixel_yuyv);
        super.addConfigurationListener(pixel_yuv420);
        super.addConfigurationListener(pixel_nv21);
        super.addConfigurationListener(pixel_rgb565);
        
        super.addConfigurationListener(stream_yuv420p);
        super.addConfigurationListener(stream_raw);
        
        super.addConfigurationListener(view_bitmap);
        super.addConfigurationListener(view_pixel);
        
        super.addConfigurationListener(frame_width);
        super.addConfigurationListener(frame_height);
        
        super.addConfigurationListener(device_name);
        //RG_switch=(RadioGroup)findViewById(R.id.usb_camera_switch_RadioGroup);

        RG_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	            
        	@Override
        	public void onCheckedChanged(RadioGroup arg0, int arg1) {
	        	// TODO Auto-generated method stub
	        	//获取变更后的选中项的ID
	        	int radioButtonId = arg0.getCheckedRadioButtonId();  
	        	if(radioButtonId==switch_on.getId())
	        		cameraStatus=1;
	        	else if(radioButtonId==switch_off.getId())
	        		cameraStatus=0;
        	}
        });
        RG_pixel.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
        	@Override
        	public void onCheckedChanged(RadioGroup arg0, int arg1) {
	        	// TODO Auto-generated method stub
	        	//获取变更后的选中项的ID
	        	int radioButtonId = arg0.getCheckedRadioButtonId(); 
	        	switch(radioButtonId){
		        	case R.id.usb_camera_pixel_fmt_RadioButton_h264:
		        		pixelFormat=1;
		        		break;
		        	case R.id.usb_camera_pixel_fmt_RadioButton_yuyv:
		        		pixelFormat=2;
		        		break;
		        	case R.id.usb_camera_pixel_fmt_RadioButton_yuv420:
		        		pixelFormat=3;
		        		break;
		        	case R.id.usb_camera_pixel_fmt_RadioButton_nv21:
		        		pixelFormat=4;
		        		break;	        		
		        	case R.id.usb_camera_pixel_fmt_RadioButton_rgb565:
		        		pixelFormat=5;
		        		break;
		        	default: 
		        		break;
	        	}
        	}
        });
        RG_stream.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
        	@Override
        	public void onCheckedChanged(RadioGroup arg0, int arg1) {
	        	// TODO Auto-generated method stub
	        	//获取变更后的选中项的ID
	        	int radioButtonId = arg0.getCheckedRadioButtonId();   
	        	switch(radioButtonId){
		        	case R.id.usb_camera_stream_fmt_RadioButton_raw:
		        		streamFormat=0;
		        		break;
		        	case R.id.usb_camera_stream_fmt_RadioButton_yuv420p:
		        		streamFormat=1;
		        		break;
		        	case R.id.usb_camera_stream_fmt_RadioButton_argb:
		        		streamFormat=2;
		        		break;
		        	default: 
		        		break;
	        	}
        	}
        });
        RG_view.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
        	@Override
        	public void onCheckedChanged(RadioGroup arg0, int arg1) {
	        	// TODO Auto-generated method stub
	        	//获取变更后的选中项的ID
	        	int radioButtonId = arg0.getCheckedRadioButtonId(); 
	        	switch(radioButtonId){
		        	case R.id.usb_camera_view_RadioButton_bitmap:
		        		viewFormat=1;
		        		break;
		        	case R.id.usb_camera_view_RadioButton_pixel:
		        		viewFormat=0;
		        		break;
		        	default: 
		        		break;
        	}
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
			mConfigurationService.putInt(NgnConfigurationEntry.USB_CAMERA_STATUS, 
					cameraStatus);
			mConfigurationService.putInt(NgnConfigurationEntry.CAPTURE_FMT, 
					pixelFormat);
			mConfigurationService.putInt(NgnConfigurationEntry.STREAM_FMT, 
					streamFormat);
			mConfigurationService.putInt(NgnConfigurationEntry.LOCAL_VIEW, 
					viewFormat);
			mConfigurationService.putInt(NgnConfigurationEntry.FRAME_WIDTH, 
					NgnStringUtils.parseInt(frame_width.getText().toString().trim(), NgnConfigurationEntry.DEFAULT_FRAME_WIDTH));
			mConfigurationService.putInt(NgnConfigurationEntry.FRAME_HEIGHT, 
					NgnStringUtils.parseInt(frame_height.getText().toString().trim(), NgnConfigurationEntry.DEFAULT_FRAME_HEIGHT));
			mConfigurationService.putString(NgnConfigurationEntry.USB_CAMERA_DEVICE_NAME, device_name.getText().toString().trim());
			
			// Compute
			if(!mConfigurationService.commit()){
				Log.e(TAG, "Failed to commit() configuration");
			}
			Log.e("USBCamera Config", "capture fmt: "+mConfigurationService.getInt(NgnConfigurationEntry.CAPTURE_FMT,NgnConfigurationEntry.DEFAULT_CAPTURE_FMT));
			super.mComputeConfiguration = false;
			Log.d("USBCamera","USBCamera Setting changed.");
		}
		super.onPause();
	}
}