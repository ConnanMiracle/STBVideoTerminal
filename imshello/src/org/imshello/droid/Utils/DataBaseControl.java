/*
 * added by yufei
 */
package org.imshello.droid.Utils;

import java.io.File;
import java.util.ArrayList;



import android.content.ContentValues;
import android.content.Context;   
import android.database.Cursor;   
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;   
import android.util.Log;

public class DataBaseControl {   


	private DataBaseOpenHelper dbOpenHelper;   
	private SQLiteDatabase db;
	private static final String CONTACTS_TABLE = "contacts";
	private static final String CONTACTS_ID = "id";
	private static final String CONTACTS_NAME = "name";	
	private static final String CONTACTS_OU = "ou";
	private static final String CONTACTS_URL = "iconPath";
	private static final String CONTACTS_MOBILE_NUM = "mobileNum";
	private static final String CONTACTS_MAIN_NUM = "mainNum";
	private static final String CONTACTS_EXTENSION_NUM = "extensionNum";
	private static final String CONTACTS_EMAIL = "email";
	private static final String CONTACTS_ADDRESS = "address";
	private static final String CONTACTS_EMPLOYEE_TYPE = "employeeType";
	private static final String CONTACTS_EMPLOYEE_NUM = "employeeNum";
	
	
	private static final String CALL_RECORD_TABLE = "callRecord";
	private static final String CALL_RECORD_NAME = "name";
	private static final String CALL_RECORD_NUM = "num";
	private static final String CALL_RECORD_NUMTYPE = "numType";
	private static final String CALL_RECORD_URL = "iconUrl";
	private static final String CALL_RECORD_STATUS = "callStatus";
	private static final String CALL_RECORD_TIME = "time";
	
	public DataBaseControl(Context context) {   
		dbOpenHelper = new DataBaseOpenHelper(context);   
		db = open();
	}

	/**
	 * �����ݿ�
	 * @return
	 * @throws SQLException
	 */
	public SQLiteDatabase open() throws SQLException{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();		       
        return db;
    }
    
    /**
     * �ر����ݿ�
     */
    public void close(){
    	dbOpenHelper.close();
    }
    
    
    /**
     * �����ݿ��в�������
     * @param contacts
     * @return
     */
    public long insertContacts(Ldap_contacts contacts) {
        
        ContentValues initialValues = new ContentValues();
        initialValues.put(CONTACTS_NAME, contacts.name);        
        initialValues.put(CONTACTS_OU, contacts.ou);
        initialValues.put(CONTACTS_MOBILE_NUM, contacts.mobileNum);
        initialValues.put(CONTACTS_MAIN_NUM, contacts.mainNum);
        initialValues.put(CONTACTS_EXTENSION_NUM, contacts.extensionNum);
        initialValues.put(CONTACTS_EMAIL, contacts.email);
        initialValues.put(CONTACTS_ADDRESS, contacts.address);
        initialValues.put(CONTACTS_EMPLOYEE_TYPE, contacts.employeeType);
        initialValues.put(CONTACTS_EMPLOYEE_NUM, contacts.employeeNum);
        if(contacts.iconFile != null){
        	initialValues.put(CONTACTS_URL, contacts.iconFile.getAbsolutePath());
        }      
        return db.insert(CONTACTS_TABLE, null, initialValues);
    }
	
    //public long insertCallRecord(CallRecord callRecord) {
    //    
    //    ContentValues initialValues = new ContentValues();
     //   initialValues.put(CALL_RECORD_NAME, callRecord.name);
    //    initialValues.put(CALL_RECORD_NUM, callRecord.num);
    //    initialValues.put(CALL_RECORD_NUMTYPE, callRecord.numType);
    //    initialValues.put(CALL_RECORD_URL, callRecord.iconUrl);
    //    initialValues.put(CALL_RECORD_STATUS, callRecord.callStatus);       
    //    initialValues.put(CALL_RECORD_TIME, callRecord.time);       
    //    return db.insert(CALL_RECORD_TABLE, null, initialValues);
    //}
    
	/**
	 * ����ϵ�����ݴ��뵽���ݿ�
	 * @param contacts
	 */
	public void saveContactsToDatabase(Ldap_contacts contacts){
		insertContacts(contacts);
	}
	
	/**
	 * ��ͨ����¼���뵽���ݿ�
	 * @param contacts
	 */
	//public void saveCallRecordToDatabase(CallRecord callRecord){
	//	insertCallRecord(callRecord);
	//}
	
	/**
	 * ͨ��idɾ����ϵ�� 
	 * @param id
	 * @return
	 */
    public boolean deleteContacts(long id) {    	
        return db.delete(CONTACTS_TABLE, CONTACTS_ID + "=" + id, null) > 0;
    }

    /**
     * ɾ��������ϵ��
     * @return
     */
    public boolean deleteAllContacts() {    	
        return db.delete(CONTACTS_TABLE, null, null) > 0;
    }
    
    /**
     * ��ѯ��ϵ��
     * @param groupType
     * @param groupId
     * @param childId
     * @return
     */
    public Cursor selectContacts(){
    	String selection = null;
    	String[] selectionArgs = new String[]{};
		Cursor cur = db.query(CONTACTS_TABLE, null, 
				null, null, null, null, null);
		return cur;
    }
    
    /**
     * ���ݺ����ѯ��ϵ��
     * @param phoneNum
     * @return
     */
    public Cursor selectContacts(String phoneNum){
    	String selection = CONTACTS_MOBILE_NUM + " = ?";
    	String[] selectionArgs = new String[]{phoneNum};
		Cursor cur = db.query(CONTACTS_TABLE, new String[]{CONTACTS_ID,CONTACTS_NAME,CONTACTS_OU,CONTACTS_URL}, 
				selection, selectionArgs, null, null, null);
		return cur;
    }
    
    /**
     * ��ȡ��ϵ���б�
     * @param groupType
     * @param groupId
     * @param childId
     * @return
     */
    public ArrayList<Ldap_contacts> getContactsList(){
    	ArrayList<Ldap_contacts> contactsList = new ArrayList<Ldap_contacts>();
    	Cursor cur = selectContacts();
    	if(cur != null){
    		while(cur.moveToNext()){
    			File iconFile = null;
    			Ldap_contacts contacts = new Ldap_contacts(cur.getString(1), cur.getString(3));
    			Log.w("ou", cur.getString(2)+"|");
    			Log.w("iconPath", cur.getString(3)+"|");
    			String iconPath = cur.getString(3);
    			if(iconPath != null){
    				iconFile = new File(iconPath);    			
        			contacts.iconFile = iconFile;
    			}    			
    			contactsList.add(contacts);
			}
			cur.close();
    	}
    	return contactsList;
    }
    
    /**
     * ��ȡ��ϵ����Ϣ
     * @param phoneNum
     * @return
     */
    public Ldap_contacts getContacts(String phoneNum){
    	Ldap_contacts contacts = null;
    	Cursor cur = selectContacts(phoneNum);
    	if(cur != null){
    		if(cur.moveToNext()){
    			File iconFile = null;
    			contacts = new Ldap_contacts(cur.getString(1));
    			String iconPath = cur.getString(3);
    			if(iconPath != null){
    				iconFile = new File(iconPath);    			
        			contacts.iconFile = iconFile;
    			}    			
    		}  
    		cur.close();
    	}
    	return contacts;
    }
    
    /**
     * ��ѯͨ����¼
     * @param callStatus
     * @return
     */
    //public Cursor selectCallRecord(int callStatus){
    //	String selection = null;
    //	String[] selectionArgs = null;
    //	if(callStatus != CallRecord.ALL_CALL){
    //		selection = CALL_RECORD_STATUS + " = ?";
    //		selectionArgs = new String[]{callStatus+""};
    //	}    	
	//	Cursor cur = db.query(CALL_RECORD_TABLE, null, 
	//			selection, selectionArgs, null, null, null);
	//	return cur;
    //}
    
    /**
     * ��ȡͨ����¼�б�
     * @param callStatus
     * @return
     */
    //public ArrayList<CallRecord> getCallRecord(int callStatus){
    //	ArrayList<CallRecord> callRecords = new ArrayList<CallRecord>();
    //	Cursor cur = selectCallRecord(callStatus);
    //	if(cur != null){
    //		while(cur.moveToNext()){
    //			CallRecord callRecord = new CallRecord(cur.getString(1), cur.getString(2),
    //					cur.getString(3), cur.getInt(4), cur.getInt(5), cur.getLong(6));
    //			callRecords.add(callRecord);
	//		}
	//		cur.close();
    //	}
    //	return callRecords;
    //}
    
    /**
     * ͨ��ƴ��������ϵ��
     * @param text
     * @return
     */
    public ArrayList<Contacts> getContactsByPinYin(String text){   	
    	ArrayList<Contacts> contactsList = new ArrayList<Contacts>();
    	/*if(text != null && !text.equals("")) {
    		for (int i = 0; i < text.length(); i++) {
				char ch = text.
			}
    	}*/
    	return contactsList;
    }
} 
