/*
 * added by yufei
 */
package org.imshello.droid.Utils;

import android.content.Context;   
import android.database.sqlite.SQLiteDatabase;   
import android.database.sqlite.SQLiteDatabase.CursorFactory;   
import android.database.sqlite.SQLiteOpenHelper;   

public class DataBaseOpenHelper extends SQLiteOpenHelper {   
    
	// 类没有实例化,是不能用作父类构造器的参数,必须声明为静态   
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
        // 第一个参数是应用的上下文   
        // 第二个参数是应用的数据库名字   
        // 第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类   
        // 第四个参数是数据库版本，必须是大于0的int（即非负数）   
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
    
    // onUpgrade()方法在数据库版本每次发生变化时都会把用户手机上的数据库表删除，然后再重新创建。   
    // 一般在实际项目中是不能这样做的，正确的做法是在更新数据库表结构时，还要考虑用户存放于数据库中的数据不会丢失,从版本几更新到版本几。   
    @Override  
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {   
        db.execSQL("DROP TABLE IF EXISTS contacts");
        db.execSQL("DROP TABLE IF EXISTS callRecord");
        onCreate(db);   
    }   
}  

