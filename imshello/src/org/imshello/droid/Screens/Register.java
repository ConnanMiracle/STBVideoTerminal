package org.imshello.droid.Screens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.imshello.droid.R;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Register extends BaseScreen{

	
	public static String user = "";
	public static String pass = "";;
	
	private EditText user_edit;
	private EditText pass_edit;
	private ProgressBar pbar;
	private Button register;
	private Handler handler;
	
	String abc = "-";
	
	private static String TAG = Register.class.getCanonicalName();
	public Register() {
		super(SCREEN_TYPE.REGIST_T, TAG);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		
		user_edit = (EditText)findViewById(R.id.mobileNo);
		pass_edit = (EditText)findViewById(R.id.pass);
		register = (Button)findViewById(R.id.register);
		pbar = (ProgressBar)findViewById(R.id.bar);
		register.setOnClickListener(register_onClick);
		
		
		 //handler处理子进程发来的消息
		 handler=new Handler()
	       {
	          @Override
			public void handleMessage(Message msg)
	             {
	              if(msg.what==0)
	               {
	            	//pbar.setVisibility(View.GONE);
	            	Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
	            	mScreenService.show(RegisterNext.class);
	               }
	              else if(msg.what==1)
	              {
	            	  pbar.setVisibility(View.GONE);
	            	  user_edit.setText("");
	            	  pass_edit.setText("");
	            	  user = "";
	      			  pass = "";
	            	  Toast.makeText(getApplicationContext(), "注册失败"+"-此用户名已被注册", Toast.LENGTH_SHORT).show();
	              }
	            }
	       };
		
	}
	
	public OnClickListener register_onClick = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			user = user_edit.getText().toString().trim();
			pass = pass_edit.getText().toString().trim();
			
			if(user.length()<=1&&pass.length()<=1)
			{
				Toast.makeText(getApplicationContext(), "您输入账号密码过短", Toast.LENGTH_SHORT).show();
			}
			else
			{
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				  imm.hideSoftInputFromWindow(Register.this .getCurrentFocus().getWindowToken(),  
	                    InputMethodManager.HIDE_NOT_ALWAYS); 
				 // pbar.setVisibility(View.VISIBLE);	
				  doRegister();
			}
		}
	};
	
	private void doRegister()
	{
		
		
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String requestUri="http://114.215.176.27/empManage/empManage/test.php";
					//String requestUri="http://weixinteacher.gotoip4.com/test.php";
				String result="";
				
				HttpPost post = new HttpPost(requestUri);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				  // 添加要传递的参数

				  NameValuePair pair1 = new BasicNameValuePair("user_name", user);
				  params.add(pair1);
				  NameValuePair pair2 = new BasicNameValuePair("user_pass", pass);
				  params.add(pair2);
				 
		         try {
		        	 HttpEntity httpentity = new UrlEncodedFormEntity(params, "gb2312");
					   post.setEntity(httpentity);
					   HttpClient client = new DefaultHttpClient();
					HttpResponse response = client.execute(post);
					HttpEntity entity = response.getEntity();
	
					if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					{
						result += EntityUtils.toString(entity);
						abc += result;
					}
					
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		         
		         if(result.contains("ok"))
		         {
		        	 handler.sendMessage(handler.obtainMessage(0));
		         }
		         else
		         {
		        	 handler.sendMessage(handler.obtainMessage(1));
		         }
		        
			}
		};
		
		 new Thread(run).start(); 
	}
	
	
	
	
	
	 @Override
	 public boolean back()
	 {
		 mScreenService.show(Login.class);
	    return true;
	 }
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub     
	          //按下键盘上返回按钮
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{ 	
			System.exit(0);
		    return true;
	    }
		else
		{
  
		return super.onKeyDown(keyCode, event);
	    }
	}*/
}
