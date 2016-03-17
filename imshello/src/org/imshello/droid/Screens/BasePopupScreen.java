package org.imshello.droid.Screens;

import org.imshello.droid.Engine;
import org.imshello.droid.Screens.BaseScreen.SCREEN_TYPE;
import org.imshello.droid.Services.IScreenService;
import org.imshello.ngn.utils.NgnStringUtils;

import android.app.ProgressDialog;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;

public class BasePopupScreen extends PopupWindow implements IBaseScreen{


	protected String mId;
	protected final SCREEN_TYPE mType;
	protected boolean mComputeConfiguration;
	protected ProgressDialog mProgressDialog;
	protected Handler mHanler;
	
	protected final IScreenService mScreenService;

	protected BasePopupScreen(SCREEN_TYPE type, String id) {
		super();
		mType = type;
		mId = id;
		mScreenService = ((Engine)Engine.getInstance()).getScreenService();
	}

	protected Engine getEngine(){
		return (Engine)Engine.getInstance();
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
		return false;
	}

	@Override
	public boolean hasBack() {
		return false;
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

	@Override
	public boolean createOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}


