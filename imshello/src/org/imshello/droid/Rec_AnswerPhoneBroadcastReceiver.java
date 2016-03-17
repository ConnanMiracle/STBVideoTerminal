package org.imshello.droid;

import org.imshello.droid.Screens.ScreenAV;
import org.imshello.ngn.sip.NgnAVSession;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class Rec_AnswerPhoneBroadcastReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if("org.imshello.ANSWERPHONE_ON_REC".equals(intent.getAction())){
			boolean test=NgnAVSession.hasSession(ScreenAV.mySession_id);
			if(test){
				NgnAVSession.getSession(ScreenAV.mySession_id).hangUpCall();
			}
		}
	}
}
	