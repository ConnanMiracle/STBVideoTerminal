package org.imshello.droid.Utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class GetUpdateInfo {
	private static String verStringJSON;
	public  static  String serverPathJOSN="http://202.118.18.8/imshello/verUpdate/version.json";
	public  static  String downPathString="http://202.118.18.8/imshello/verUpdate/IMSHello.apk";
	public static String appName="IMSHello.apk";
	public static String getVerStringJSON() {
		return verStringJSON;
	}
	public static void setVerStringJSON(String verStringJSON) {
		try {
			GetUpdateInfo.verStringJSON = verStringJSON;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String getUpdataVerJSON(){
		try {
			StringBuilder newVerJSON = new StringBuilder();
			HttpClient client = new DefaultHttpClient();	
			HttpParams httpParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);
			HttpResponse response = client.execute(new HttpGet(serverPathJOSN));
			HttpEntity entity = response.getEntity();
			if(entity != null){
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(entity.getContent(),"UTF-8"),8192);
				String line = null;
				while((line = reader.readLine()) != null){
					newVerJSON.append(line+"\n");
					reader.close();
					break;
				}
				return newVerJSON.toString();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "";
	}
}
