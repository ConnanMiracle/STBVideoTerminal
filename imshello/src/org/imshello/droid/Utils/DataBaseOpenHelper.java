/*
 * added by yufei
 */
package org.imshello.droid.Utils;

import android.content.Context;   
import android.database.sqlite.SQLiteDatabase;   
import android.database.sqlite.SQLiteDatabase.CursorFactory;   
import android.database.sqlite.SQLiteOpenHelper;   

public class DataBaseOpenHelper extends SQLiteOpenHelper {   
    
	// ��û��ʵ����,�ǲ����������๹�����Ĳ���,��������Ϊ��̬   
    private static String dbname = "tianbo";   
    private static int version = 6;   
    private static final String CREATE_CONTACTS = "CREATE TABLE IF NOT EXISTS contacts("+
    	"id integer primary key autoincrement,"+"name varchar(32),ou varchar(64),iconPath varchar(64),"+
    	"mobileNum varchar(16),mainNum varchar(16),extensionNum varchar(16),email varchar(16),address varchar(32)," +
    	"employeeType varchar(16),employeeNum varchar(16)"+")";              
    //private static final String CREATE_CALLRECORD = "CREATE TABLE IF NOT EXISTS callRecord("+
	//	"id integer primary key autoincrement,"+"name varchar(32),num varchar(32),numType integer,iconUrl varchar(64)," +
	//			"callStatus integer,time long)"; 
    
    public DataBaseOpenHelper(Context context) {   
        // ��һ��������Ӧ�õ�������   
        // �ڶ���������Ӧ�õ����ݿ�����   
        // ����������CursorFactoryָ����ִ�в�ѯʱ���һ���α�ʵ���Ĺ�����,����Ϊnull,����ʹ��ϵͳĬ�ϵĹ�����   
        // ���ĸ����������ݿ�汾�������Ǵ���0��int�����Ǹ�����   
        super(context, dbname, null, version);   
        // TODO Auto-generated constructor stub   
    }   
  
    public DataBaseOpenHelper(Context context, String name,   
            CursorFactory factory, int version) {   
        super(context, name, factory, version);   
        // TODO Auto-generated constructor stub   
    }   
  
    @Override  
    public void onCreate(SQLiteDatabase db) {     	
        db.execSQL(CREATE_CONTACTS);
    //    db.execSQL(CREATE_CALLRECORD);        
    } 
    
    // onUpgrade()���������ݿ�汾ÿ�η����仯ʱ������û��ֻ��ϵ����ݿ��ɾ����Ȼ�������´�����   
    // һ����ʵ����Ŀ���ǲ����������ģ���ȷ���������ڸ������ݿ��ṹʱ����Ҫ�����û���������ݿ��е����ݲ��ᶪʧ,�Ӱ汾�����µ��汾����   
    @Override  
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {   
        db.execSQL("DROP TABLE IF EXISTS contacts");
        db.execSQL("DROP TABLE IF EXISTS callRecord");
        onCreate(db);   
    }   
}  

