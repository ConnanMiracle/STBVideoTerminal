package org.imshello.droid.Screens;

import org.imshello.droid.CustomDialog;
import org.imshello.droid.Engine;
import org.imshello.droid.R;
import org.imshello.droid.Screens.BaseScreen.SCREEN_TYPE;
import org.imshello.droid.Services.IScreenService;
import org.imshello.ngn.utils.NgnStringUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.MediaColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class BaseTabScreen extends TabActivity implements IBaseScreen {
	private static final String TAG = BaseTabScreen.class.getCanonicalName();
	
	
	protected String mId;
	protected final SCREEN_TYPE mType;
	protected boolean mComputeConfiguration;
	protected ProgressDialog mProgressDialog;
	protected Handler mHanler;
	
	protected final IScreenService mScreenService;

	protected BaseTabScreen(SCREEN_TYPE type, String id) {
		super();
		mType = type;
		mId = id;
		mScreenService = ((Engine)Engine.getInstance()).getScreenService();
	}

	protected Engine getEngine(){
		return (Engine)Engine.getInstance();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHanler = new Handler();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (!processKeyDown(keyCode, event)) {
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}

	@Override
	public String getId() {
		return mId;
	}

	@Override
	public SCREEN_TYPE getType() {
		return mType;
	}

	@Override
	public boolean hasMenu() {
		return true;
	}

	@Override
	public boolean hasBack() {
		return true;
	}

	@Override
	public boolean back() {
		return mScreenService.back();
	}


	protected void addConfigurationListener(RadioButton radioButton) {
		radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mComputeConfiguration = true;
			}
		});
	}

	protected void addConfigurationListener(EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mComputeConfiguration = true;
			}
		});
	}

	protected void addConfigurationListener(CheckBox checkBox) {
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mComputeConfiguration = true;
			}
		});
	}

	protected void addConfigurationListener(Spinner spinner) {
		// setOnItemClickListener not supported by Spinners
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				mComputeConfiguration = true;
			}
		});
	}

	protected int getSpinnerIndex(String value, String[] values) {
		for (int i = 0; i < values.length; i++) {
			if (NgnStringUtils.equals(value, values[i], true)) {
				return i;
			}
		}
		return 0;
	}
	
	protected int getSpinnerIndex(int value, int[] values) {
		for (int i = 0; i < values.length; i++) {
			if (value == values[i]) {
				return i;
			}
		}
		return 0;
	}

	protected void showInProgress(String text, boolean bIndeterminate,
			boolean bCancelable) {
		synchronized (this) {
			if (mProgressDialog == null) {
				mProgressDialog = new ProgressDialog(this);
				mProgressDialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						mProgressDialog = null;
					}
				});
				mProgressDialog.setMessage(text);
				mProgressDialog.setIndeterminate(bIndeterminate);
				mProgressDialog.setCancelable(bCancelable);
				mProgressDialog.show();
			}
		}
	}

	protected void cancelInProgress() {
		synchronized (this) {
			if (mProgressDialog != null) {
				mProgressDialog.cancel();
				mProgressDialog = null;
			}
		}
	}

	protected void cancelInProgressOnUiThread() {
		mHanler.post(new Runnable() {
			@Override
			public void run() {
				cancelInProgress();
			}
		});
	}

	protected void showInProgressOnUiThread(final String text,
			final boolean bIndeterminate, final boolean bCancelable) {
		mHanler.post(new Runnable() {
			@Override
			public void run() {
				showInProgress(text, bIndeterminate, bCancelable);
			}
		});
	}

	protected void showMsgBox(String title, String message) {
		CustomDialog.create(this, R.drawable.icon, title, message, "OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}, null, null);
	}

	protected void showMsgBoxOnUiThread(final String title, final String message) {
		mHanler.post(new Runnable() {
			@Override
			public void run() {
				showMsgBox(title, message);
			}
		});
	}

    protected String getPath(Uri uri) {
    	try{
	        String[] projection = { MediaColumns.DATA };
	        Cursor cursor = managedQuery(uri, projection, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	        cursor.moveToFirst();
	        final String path = cursor.getString(column_index);
	        cursor.close();
	        return path;
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
    
	public static boolean processKeyDown(int keyCode, KeyEvent event) {
		final IScreenService screenService = ((Engine)Engine.getInstance()).getScreenService();
		final IBaseScreen currentScreen = screenService.getCurrentScreen();
		if (currentScreen != null) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0
					&& currentScreen.getType() != SCREEN_TYPE.HOME_T) {
				if (currentScreen.hasBack()) {
					if (!currentScreen.back()) {
						return false;
					}
				} else {
					screenService.back();
				}
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP){
				if(currentScreen.getType() == SCREEN_TYPE.AV_T){
					Log.d(TAG, "intercepting volume changed event");
					if(((ScreenAV)currentScreen).onVolumeChanged((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN))){
						return true;
					}
				}
			}
			else if (keyCode == KeyEvent.KEYCODE_MENU
					&& event.getRepeatCount() == 0) {
				if (currentScreen instanceof Activity
						&& currentScreen.hasMenu()) {
					return false;
					// return ((Activity)currentScreen).onKeyDown(keyCode,
					// event);
				}
				/*
				 * if(!currentScreen.hasMenu()){
				 * screenService.show(ScreenHome.class); return true; } else
				 * if(currentScreen instanceof Activity){ return
				 * ((Activity)currentScreen).onKeyDown(keyCode, event); }
				 */
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean createOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}
	
}

