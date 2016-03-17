/* Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, imshello Telecom.
*
* Contact: Mamadou Diop <diopmamadou(at)imshello(dot)org>
*	
* This file is part of imsdroid Project (http://code.google.com/p/imsdroid)
*
* imsdroid is free software: you can redistribute it and/or modify it under the terms of 
* the GNU General Public License as published by the Free Software Foundation, either version 3 
* of the License, or (at your option) any later version.
*	
* imsdroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU General Public License for more details.
*	
* You should have received a copy of the GNU General Public License along 
* with this program; if not, write to the Free Software Foundation, Inc., 
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.imshello.droid.Screens;

import org.imshello.droid.R;
import org.imshello.ngn.services.INgnConfigurationService;
import org.imshello.ngn.utils.NgnConfigurationEntry;
import org.imshello.ngn.utils.NgnStringUtils;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsBufferQueue  extends BaseScreen {
	private final static String TAG = SettingsBufferQueue.class.getCanonicalName();
	private final INgnConfigurationService mConfigurationService;
	
	private EditText mUSBQueueSize;
	private EditText mUSBBufferSize;
	private EditText mDecoderQueueSize;
	private EditText mDecoderBufferSize;
	private ImageView back;


	
	
	public SettingsBufferQueue() {
		super(SCREEN_TYPE.IDENTITY_T, TAG);
		
		mConfigurationService = getEngine().getConfigurationService();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_bufferqueue);
        mUSBQueueSize = (EditText)findViewById(R.id.screen_bufferqueue_editText_usb_queue_size);
        mUSBBufferSize = (EditText)findViewById(R.id.screen_bufferqueue_editText_usb_buffer_size);
        mDecoderQueueSize = (EditText)findViewById(R.id.screen_bufferqueue_editText_decoder_queue_size);
        mDecoderBufferSize = (EditText)findViewById(R.id.screen_bufferqueue_editText_decoder_buffer_size);
        
        mUSBQueueSize.setText(String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.USB_CAMERA_BUFFERQUEUE_SIZE, NgnConfigurationEntry.DEFAULT_USB_CAMERA_BUFFERQUEUE_SIZE)));
        mUSBBufferSize.setText(String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.USB_CAMERA_BUFFER_SIZE, NgnConfigurationEntry.DEFAULT_USB_CAMERA_BUFFER_SIZE)));
        mDecoderQueueSize.setText(String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.HI_DECODER_BUFFERQUEUE_SIZE, NgnConfigurationEntry.DEFAULT_HI_DECODER_BUFFERQUEUE_SIZE)));
        mDecoderBufferSize.setText(String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.HI_DECODER_BUFFERSIZE, NgnConfigurationEntry.DEFAULT_HI_DECODER_BUFFERSIZE)));
     
        

        super.addConfigurationListener(mUSBQueueSize);
        super.addConfigurationListener(mUSBBufferSize);
        super.addConfigurationListener(mDecoderQueueSize);
        super.addConfigurationListener(mDecoderBufferSize);

	}	
 
	@Override
	protected void onPause() {
		if(super.mComputeConfiguration){
			mConfigurationService.putInt(NgnConfigurationEntry.USB_CAMERA_BUFFERQUEUE_SIZE, 
					Integer.parseInt(mUSBQueueSize.getText().toString()));
			mConfigurationService.putInt(NgnConfigurationEntry.USB_CAMERA_BUFFER_SIZE, 
					Integer.parseInt(mUSBBufferSize.getText().toString()));
			mConfigurationService.putInt(NgnConfigurationEntry.HI_DECODER_BUFFERQUEUE_SIZE, 
					Integer.parseInt(mDecoderQueueSize.getText().toString()));
			mConfigurationService.putInt(NgnConfigurationEntry.HI_DECODER_BUFFERSIZE, 
					Integer.parseInt(mDecoderBufferSize.getText().toString()));
			
			// Compute
			if(!mConfigurationService.commit()){
				Log.e(TAG, "Failed to Commit() configuration");
			}
			
			Toast.makeText(this,mUSBQueueSize.getText().toString()+" "+
					mUSBBufferSize.getText().toString()+" "+
					mDecoderQueueSize.getText().toString()+" "+
					mDecoderBufferSize.getText().toString(), Toast.LENGTH_SHORT).show();
			super.mComputeConfiguration = false;
		}
		super.onPause();
	}
	@Override
	public boolean back()
	{
		 mScreenService.show(ScreenSet.class);
		boolean ret = mScreenService.show(ScreenSet.class);
		if(ret){
			mScreenService.destroy(getId());
		}
		this.finish();
		return ret;
	}
}
