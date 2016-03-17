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
	 * 获取手机屏幕宽和高 
	 * 宽 = xy[0] 
	 * 高 = xy[1] 
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
	 * 对时间进行处理,获取YY-MM-dd HH:mm格式的时间字符串 
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
	 * 对时间进行处理,获取任意格式的时间字符串 
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
	 * 回到主界面 
	 */
	public static void toHome(Context context){
		Intent i= new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		context.startActivity(i);
	}
	
	/** 
	 * 创建httpClient，建立http连接
	 * @return 
	 */
	public static HttpClient createHttpClient(int timeOut){
		// 设置超时
		BasicHttpParams httpParams = new BasicHttpParams();  
		HttpConnectionParams.setConnectionTimeout(httpParams, timeOut);  
		HttpConnectionParams.setSoTimeout(httpParams, timeOut); 
		// 创建一个默认的HttpClient
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		return httpClient;
	}
	
	/** 
	 * 创建httpClient，建立https连接
	 * @return 
	 */
	public static HttpClient createHttpsClient(int timeOut){
		// 设置超时
 	   BasicHttpParams httpParams = new BasicHttpParams();  
 	   HttpConnectionParams.setConnectionTimeout(httpParams, timeOut);  
 	   HttpConnectionParams.setSoTimeout(httpParams, timeOut); 
 	   // 创建一个默认的HttpClient
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
	 * 与web服务器进行交互（httpPost）
	 * @return 
	 */
	public static String httpClientPost(boolean isHttps,String url,List<NameValuePair> params,int timeOut){
		String str = "";
		try {
			HttpClient httpClient = null;
			// 设置超时
			if(isHttps){
				httpClient = createHttpsClient(timeOut);
			} else {
				httpClient = createHttpClient(timeOut);
			}					
			HttpPost request = new HttpPost(url);
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 发送post请求，并将响应内容转换成字符串
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
	 * 与web服务器进行交互（httpGet）
	 * @return 
	 */
	public static String httpClientGet(boolean isHttps,String url,int timeOut){
		String str = "";
		try {
			// 设置超时
			HttpClient httpClient = null;
			// 设置超时
			if(isHttps){
				httpClient = createHttpsClient(timeOut);
			} else {
				httpClient = createHttpClient(timeOut);
			}		
			// 创建一个GET请求
    	    HttpGet request = new HttpGet(url);
    	    // 发送GET请求，并将响应内容转换成字符串
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
	 * 处理json特殊字符
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
     * 将图片截取为圆角图片
     * @param bitmap 原图片
     * @param ratio 截取比例，如果是8，则圆角半径是宽高的1/8，如果是2，则是圆形图片
     * @return 圆角矩形图片
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
