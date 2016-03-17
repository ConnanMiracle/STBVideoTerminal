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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class SettingsController extends BaseScreen {
	private final static String TAG = SettingsController.class
			.getCanonicalName();

	private EditText mEtScanPortSend;
	private EditText mEtScanPortRecv;
	private EditText mEtMessengerPortRemoteRecv;
	private EditText mEtMessengerPortLocalRecv;
	private RadioButton mRadioButtonOn;
	private RadioButton mRadioButtonOff;

	private final INgnConfigurationService mConfigurationService;

	public SettingsController() {
		super(SCREEN_TYPE.QOS_T, TAG);

		// Services
		mConfigurationService = getEngine().getConfigurationService();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_controller);

		// get controls

		mEtScanPortSend = (EditText) findViewById(R.id.controller_setting_edittext_port_scan_send);
		mEtScanPortRecv = (EditText) findViewById(R.id.controller_setting_edittext_port_scan_recv);
		mEtMessengerPortRemoteRecv= (EditText) findViewById(R.id.controller_setting_edittext_port_messenger_remote_recv);
		mEtMessengerPortLocalRecv= (EditText) findViewById(R.id.controller_setting_edittext_port_messenger_local_recv);
		
		mRadioButtonOff = (RadioButton) findViewById(R.id.controller_setting_radio_btn_off);
		mRadioButtonOn = (RadioButton) findViewById(R.id.controller_setting_radio_btn_on);

		mRadioButtonOn.setChecked(mConfigurationService.getBoolean(
				NgnConfigurationEntry.CONTROLLER_SWITCH,
				NgnConfigurationEntry.DEFAULT_CONTROLLER_SWITCH));
		mRadioButtonOff.setChecked(!mRadioButtonOn.isChecked());

		mEtScanPortSend.setText(mConfigurationService.getString(
				NgnConfigurationEntry.CONTROLLER_SCAN_PORT_SEND,
				NgnConfigurationEntry.DEFAULT_CONTROLLER_SCAN_PORT_SEND));
		mEtScanPortRecv.setText(mConfigurationService.getString(
				NgnConfigurationEntry.CONTROLLER_SCAN_PORT_RECV,
				NgnConfigurationEntry.DEFAULT_CONTROLLER_SCAN_PORT_RECV));
		mEtMessengerPortRemoteRecv.setText(mConfigurationService.getString(
				NgnConfigurationEntry.CONTROLLER_MESSENGER_PORT_REMOTE_RECV,
				NgnConfigurationEntry.DEFAULT_CONTROLLER_MESSENGER_PORT_REMOTE_RECV));
		mEtMessengerPortLocalRecv.setText(mConfigurationService.getString(
				NgnConfigurationEntry.CONTROLLER_MESSENGER_PORT_LOCAL_RECV,
				NgnConfigurationEntry.DEFAULT_CONTROLLER_MESSENGER_PORT_LOCAL_RECV));
		
		// add listeners (for the configuration)

		addConfigurationListener(mEtScanPortSend);
		addConfigurationListener(mEtScanPortRecv);
		addConfigurationListener(mEtMessengerPortRemoteRecv);
		addConfigurationListener(mEtMessengerPortLocalRecv);
		addConfigurationListener(mRadioButtonOn);
		addConfigurationListener(mRadioButtonOff);

	}

	@Override
	protected void onPause() {
		if (super.mComputeConfiguration) {

			mConfigurationService.putBoolean(
					NgnConfigurationEntry.CONTROLLER_SWITCH,
					mRadioButtonOn.isChecked());

			mConfigurationService.putString(
					NgnConfigurationEntry.CONTROLLER_SCAN_PORT_SEND, mEtScanPortSend
							.getText().toString());

			mConfigurationService.putString(
					NgnConfigurationEntry.CONTROLLER_SCAN_PORT_RECV, mEtScanPortRecv
							.getText().toString());
			
			mConfigurationService.putString(
					NgnConfigurationEntry.CONTROLLER_MESSENGER_PORT_REMOTE_RECV, mEtMessengerPortRemoteRecv
							.getText().toString());
			
			mConfigurationService.putString(
					NgnConfigurationEntry.CONTROLLER_MESSENGER_PORT_LOCAL_RECV, mEtMessengerPortLocalRecv
							.getText().toString());

			// Compute
			if (!mConfigurationService.commit()) {
				Log.e(TAG, "Failed to commit() configuration");
			}

			super.mComputeConfiguration = false;
		}
		/*Toast.makeText(
				this,
				mConfigurationService.getBoolean(
						NgnConfigurationEntry.CONTROLLER_SWITCH, false)
						+ " "
						+ mConfigurationService
								.getString(
										NgnConfigurationEntry.CONTROLLER_PORT_SEND,
										NgnConfigurationEntry.DEFAULT_CONTROLLER_PORT_SEND)
						+ " "
						+ mConfigurationService
								.getString(
										NgnConfigurationEntry.CONTROLLER_PORT_RECV,
										NgnConfigurationEntry.DEFAULT_CONTROLLER_PORT_RECV),
				Toast.LENGTH_SHORT).show();*/
		super.onPause();
	}

	public void onClick(View v) {
		mScreenService.back();
		//ScreenMain.setCurrentTab(3);
	}

	@Override
	public boolean hasBack() {
		return true;
	}

	@Override
	public boolean back() {
		super.back();
		//ScreenMain.setCurrentTab(3);
		return true;
	}
}
