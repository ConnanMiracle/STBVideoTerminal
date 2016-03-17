package org.imshello.droid.Utils;

import android.os.AsyncTask;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchScope;

public class LDAPControl {
	// 瑜版挸澧犻柊宥囩枂娣団剝浼�  
	private static String ldapHost = "114.215.176.27";   
	private static int ldapPort = 389;   
	private static String ldapBindDN = "cn=admin,dc=sipsys,dc=com"; 
	private static String ldapBaseDN = "dc=sipsys,dc=com";
	private static String ldapPassword = "adminpwd";   
	private static LDAPConnection connection = null;  	
	private volatile boolean isSearching = false;
	
   /**
    * 閹兼粎鍌ㄧ�灞惧灇閸氬海娈戦崶鐐剁殶閹恒儱褰� 
    */
    public interface SearchCallback {
	    public void onSearchFinish(SearchResult result);			
    }
    
	/**
	 * 閺嬪嫰锟介弬瑙勭《
	 */
	public LDAPControl(){
		
	}	
	
	/**
	 * 瀵倹顒為懢宄板絿閼辨梻閮存禍鐑樻殶閹癸拷
	 * @param callback
	 */
	public synchronized void search(final SearchCallback callback){	
		if(isSearching){
			return;
		}
		new AsyncTask<Object, Object, Object>() {
			@Override
			protected SearchResult doInBackground(Object... objects) {
				isSearching = true;
				SearchResult result = null;
				try {			
					connection = getConnection();
					if(connection.isConnected()){
						String searchText = "cn=*";
						String filterString = searchText;
						Filter filter = Filter.create(filterString);
					    final SearchRequest request = new SearchRequest(ldapBaseDN,
					           SearchScope.SUB, filter);
					    result = connection.search(request);				        
					}					     
				} catch(LDAPSearchException lse){
					lse.printStackTrace();
				} catch (LDAPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
			      if (connection != null){
			    	  connection.close();
			      }
			    }
				return result;
			}
			@Override
			protected void onPostExecute(Object obj) {
				isSearching = false;
				callback.onSearchFinish((SearchResult) obj);
			}				
		}.execute(); 
				
	}
			
	/**
	 * 閼惧嘲褰嘗DAP鏉╃偞甯�
	 * @return
	 * @throws LDAPException
	 */
	public synchronized LDAPConnection getConnection()
	         throws LDAPException {		
	    final LDAPConnection conn = new LDAPConnection(ldapHost, ldapPort);
	    try {
	        conn.bind(ldapBindDN, ldapPassword);
	    } catch (LDAPException le) {
	        conn.close();
	        throw le;
	    }
	    return conn;
	 }
	
	public SearchResult search1(){	
				SearchResult result = null;
				try {			
					connection = getConnection();
					if(connection.isConnected()){
						String searchText = "cn=*";
						String filterString = searchText;
						Filter filter = Filter.create(filterString);
					    final SearchRequest request = new SearchRequest(ldapBaseDN,
					           SearchScope.SUB, filter);
					    result = connection.search(request);				        
					}					     
				} catch(LDAPSearchException lse){
					lse.printStackTrace();
				} catch (LDAPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
			      if (connection != null){
			    	  connection.close();
			      }
			    }
				return result;
			}
	
				
	
}
