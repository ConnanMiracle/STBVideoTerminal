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

public class SettingsIdentity  extends BaseScreen {
	private final static String TAG = SettingsIdentity.class.getCanonicalName();
	private final INgnConfigurationService mConfigurationService;
	
	private EditText mEtDisplayName;
	private EditText mEtIMPU;
	private EditText mEtIMPI;
	private EditText mEtPassword;
	private EditText mEtRealm;
	private CheckBox mCbEarlyIMS;
	private ImageView back;
	private EditText mEtProxyHost;
	private EditText mEtProxyPort;
	private Spinner mSpTransport;
	private Spinner mSpProxyDiscovery;
	private final static String[] sSpinnerTransportItems = new String[] {NgnConfigurationEntry.DEFAULT_NETWORK_TRANSPORT.toUpperCase(), "TCP", "TLS"/*, "SCTP"*/};
	private final static String[] sSpinnerProxydiscoveryItems = new String[] {NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_DISCOVERY, NgnConfigurationEntry.PCSCF_DISCOVERY_DNS_SRV/*, "DHCPv4/v6", "Both"*/};
	
	
	public SettingsIdentity() {
		super(SCREEN_TYPE.IDENTITY_T, TAG);
		
		mConfigurationService = getEngine().getConfigurationService();
	}

	private TextWatcher watcher = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
			//自动填充相关字段
			/*if(s.length()>0&&s.toString().contains("@")){
				String name_proxy="";
				String nameString="";
				String proxyString="";
				String []arr=s.toString().split(":");
				if (arr.length>1)
				{
					name_proxy=arr[1];
				}else{
					name_proxy=arr[0];
				}
				
				String []arr2=name_proxy.split("@");
				if (arr2.length==2)
				{
					nameString=arr2[0];
					proxyString=arr2[1];
					mEtIMPI.setText(nameString.trim());//填充"验证标识"
					mEtRealm.setText(proxyString.trim());//填充"鉴权域"
					mEtProxyHost.setText(proxyString);//填充"cscf ip"
				}
				
			}*/
			
			mEtIMPI.setText(s);
			
		}
    	
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_identity);
        
        mEtDisplayName = (EditText)findViewById(R.id.screen_identity_editText_displayname);
        mEtIMPU = (EditText)findViewById(R.id.screen_identity_editText_impu);
        mEtIMPI = (EditText)findViewById(R.id.screen_identity_editText_impi);
        mEtPassword = (EditText)findViewById(R.id.screen_identity_editText_password);
        mEtRealm = (EditText)findViewById(R.id.screen_identity_editText_realm);
        mCbEarlyIMS = (CheckBox)findViewById(R.id.screen_identity_checkBox_earlyIMS);
        
         //added
        mEtProxyHost = (EditText)findViewById(R.id.screen_network_editText_pcscf_host);
        mEtProxyPort = (EditText)findViewById(R.id.screen_network_editText_pcscf_port);
        mSpTransport = (Spinner)findViewById(R.id.screen_network_spinner_transport);
        mSpProxyDiscovery = (Spinner)findViewById(R.id.screen_network_spinner_pcscf_discovery);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sSpinnerTransportItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpTransport.setAdapter(adapter);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sSpinnerProxydiscoveryItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpProxyDiscovery.setAdapter(adapter);
        mEtProxyHost.setText(mConfigurationService.getString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_HOST));
        mEtProxyPort.setText(Integer.toString(mConfigurationService.getInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT)));
        mSpTransport.setSelection(super.getSpinnerIndex(
				mConfigurationService.getString(NgnConfigurationEntry.NETWORK_TRANSPORT, sSpinnerTransportItems[0]),
				sSpinnerTransportItems));
        mSpProxyDiscovery.setSelection(super.getSpinnerIndex(
				mConfigurationService.getString(NgnConfigurationEntry.NETWORK_PCSCF_DISCOVERY, sSpinnerProxydiscoveryItems[0]),
				sSpinnerProxydiscoveryItems));
        
        String []arr=mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPU, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPU).toString().split(":");
        String []arr1 = arr[1].split("@");
        mEtDisplayName.setText(mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, NgnConfigurationEntry.DEFAULT_IDENTITY_DISPLAY_NAME));
        mEtIMPU.setText(arr1[0]);
        mEtIMPI.setText(arr1[0]);
        mEtPassword.setText(mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_PASSWORD, NgnStringUtils.emptyValue()));
        mEtRealm.setText(mConfigurationService.getString(NgnConfigurationEntry.NETWORK_REALM, NgnConfigurationEntry.DEFAULT_NETWORK_REALM));
        mCbEarlyIMS.setChecked(mConfigurationService.getBoolean(NgnConfigurationEntry.NETWORK_USE_EARLY_IMS, NgnConfigurationEntry.DEFAULT_NETWORK_USE_EARLY_IMS));
        //返回按钮
        back = (ImageView)findViewById(R.id.back);
          back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mScreenService.show(ScreenSet.class);
			}
		});
        
        super.addConfigurationListener(mEtProxyHost);
        super.addConfigurationListener(mEtProxyPort);
        super.addConfigurationListener(mSpTransport);
        super.addConfigurationListener(mSpProxyDiscovery);
        mEtIMPU.addTextChangedListener(watcher);
        super.addConfigurationListener(mEtDisplayName);
        super.addConfigurationListener(mEtIMPU);
        super.addConfigurationListener(mEtIMPI);
        super.addConfigurationListener(mEtPassword);
        super.addConfigurationListener(mEtRealm);
        super.addConfigurationListener(mCbEarlyIMS);
	}	
 
	@Override
	protected void onPause() {
		if(super.mComputeConfiguration){
			mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, 
					mEtDisplayName.getText().toString().trim());
			String impu = "sip:"+mEtIMPU.getText().toString().trim()+"@"+mEtRealm.getText().toString().trim();
			mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, 
					impu);
			//mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, 
					//mEtIMPU.getText().toString().trim());
			mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, 
					mEtIMPI.getText().toString().trim());
			mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, 
					mEtPassword.getText().toString().trim());
			mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, 
					mEtRealm.getText().toString().trim());
			mConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_EARLY_IMS, 
					mCbEarlyIMS.isChecked());
			
			//added
			mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, 
					mEtProxyHost.getText().toString().trim());
			mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, 
					NgnStringUtils.parseInt(mEtProxyPort.getText().toString().trim(), NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT) );
			mConfigurationService.putString(NgnConfigurationEntry.NETWORK_TRANSPORT, 
					SettingsIdentity.sSpinnerTransportItems[mSpTransport.getSelectedItemPosition()]);
			mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_DISCOVERY, 
					SettingsIdentity.sSpinnerProxydiscoveryItems[mSpProxyDiscovery.getSelectedItemPosition()]);
			
			// Compute
			if(!mConfigurationService.commit()){
				Log.e(TAG, "Failed to Commit() configuration");
			}
			
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
