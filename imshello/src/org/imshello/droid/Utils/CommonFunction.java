package org.imshello.droid.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;

public class CommonFunction {
	
	/** 
	 * ��ȡ�ֻ���Ļ��͸� 
	 * �� = xy[0] 
	 * �� = xy[1] 
	 * @return 
	 */  
	public static int[] getPhone(Context context) {  
		 int[] xy = new int[2];  
		 DisplayMetrics dm = new DisplayMetrics();  
		 ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);  
		 xy[0] = dm.widthPixels;  
		 xy[1] = dm.heightPixels;  
		 return xy;  
	} 
	
	/** 
	 * ��ʱ����д���,��ȡYY-MM-dd HH:mm��ʽ��ʱ���ַ��� 
	 * @param millisecond since January 1, 1970 00:00:00 UTC
	 * @return 
	 */  
	public static String getTime(long millisecond){
		Date date = new Date(millisecond);
		String time = "";
		if(date != null){
			SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			df.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			time = df.format(date);
		}						
		return time;
	}
	
	/** 
	 * ��ʱ����д���,��ȡ�����ʽ��ʱ���ַ��� 
	 * @param millisecond since January 1, 1970 00:00:00 UTC
	 * @return 
	 */  
	public static String getTime(long millisecond,String format){
		Date date = new Date(millisecond);
		String time = "";
		if(date != null){
			SimpleDateFormat df = new SimpleDateFormat(format);
			df.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			time = df.format(date);
		}						
		return time;
	}
			
	/** 
	 * �ص������� 
	 */
	public static void toHome(Context context){
		Intent i= new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		context.startActivity(i);
	}
	
	/** 
	 * ����httpClient������http����
	 * @return 
	 */
	public static HttpClient createHttpClient(int timeOut){
		// ���ó�ʱ
		BasicHttpParams httpParams = new BasicHttpParams();  
		HttpConnectionParams.setConnectionTimeout(httpParams, timeOut);  
		HttpConnectionParams.setSoTimeout(httpParams, timeOut); 
		// ����һ��Ĭ�ϵ�HttpClient
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		return httpClient;
	}
	
	/** 
	 * ����httpClient������https����
	 * @return 
	 */
	public static HttpClient createHttpsClient(int timeOut){
		// ���ó�ʱ
 	   BasicHttpParams httpParams = new BasicHttpParams();  
 	   HttpConnectionParams.setConnectionTimeout(httpParams, timeOut);  
 	   HttpConnectionParams.setSoTimeout(httpParams, timeOut); 
 	   // ����һ��Ĭ�ϵ�HttpClient
 	   SchemeRegistry schemeRegistry = new SchemeRegistry();  
 	   schemeRegistry.register(new Scheme("https",  
 	                       new EasySSLSocketFactory(), 443));  
 	   schemeRegistry.register(new Scheme("https",  
                new EasySSLSocketFactory(), 8443));  
 	   ClientConnectionManager connManager = new ThreadSafeClientConnManager(httpParams, schemeRegistry); 
 	   HttpClient httpClient = new DefaultHttpClient(connManager,httpParams);
 	   return httpClient;
		
	}
	
	/** 
	 * ��web���������н�����httpPost��
	 * @return 
	 */
	public static String httpClientPost(boolean isHttps,String url,List<NameValuePair> params,int timeOut){
		String str = "";
		try {
			HttpClient httpClient = null;
			// ���ó�ʱ
			if(isHttps){
				httpClient = createHttpsClient(timeOut);
			} else {
				httpClient = createHttpClient(timeOut);
			}					
			HttpPost request = new HttpPost(url);
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// ����post���󣬲�����Ӧ����ת�����ַ���
			HttpResponse response = httpClient.execute(request);	    	   
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {   
				str = EntityUtils.toString(response.getEntity());   
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	
	/** 
	 * ��web���������н�����httpGet��
	 * @return 
	 */
	public static String httpClientGet(boolean isHttps,String url,int timeOut){
		String str = "";
		try {
			// ���ó�ʱ
			HttpClient httpClient = null;
			// ���ó�ʱ
			if(isHttps){
				httpClient = createHttpsClient(timeOut);
			} else {
				httpClient = createHttpClient(timeOut);
			}		
			// ����һ��GET����
    	    HttpGet request = new HttpGet(url);
    	    // ����GET���󣬲�����Ӧ����ת�����ַ���
    	    HttpResponse response = httpClient.execute(request);		    	   
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {   
				str = EntityUtils.toString(response.getEntity());   
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	
	/** 
	 * ����json�����ַ�
	 * @return 
	 */
	public static String string2Json(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
					case '\\':
					sb.append("\\\\");
					break;
					case '/':
					sb.append("\\/");
					break;
					case '\b':
					sb.append("\\b");
					break;
					case '\f':
					sb.append("\\f");
					break;
					case '\n':
					sb.append("\\n");
					break;
					case '\r':
					sb.append("\\r");
					break;
					case '\t':
					sb.append("\\t");
					break;
					default:
					sb.append(c);
			}
		}
		return sb.toString();
	} 

	 /**
     * ��ͼƬ��ȡΪԲ��ͼƬ
     * @param bitmap ԭͼƬ
     * @param ratio ��ȡ�����������8����Բ�ǰ뾶�ǿ�ߵ�1/8�������2������Բ��ͼƬ
     * @return Բ�Ǿ���ͼƬ
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float ratio) {
            
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                            bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawRoundRect(rectF, bitmap.getWidth()/ratio, 
                            bitmap.getHeight()/ratio, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
    }
    
}
