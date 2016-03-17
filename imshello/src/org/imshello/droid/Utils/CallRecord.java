package org.imshello.droid.Utils;

public class CallRecord {
	
	//属性建议使用public定义，在android中可适当提升性能
		public String name; //联系人姓名
		public String num; //联系人电话
		public int numType; //电话类型
		public String iconUrl; //头像路径
		public int callStatus; //通话状态
		public long time; //通话时间
		
		//电话类型
		public static final int TELEPHONE = 0;
		public static final int MOBILE_PHONE = 1;
		
		//通话状态
		public static final int ALL_CALL = -1;
		public static final int DIALLED_NOS = 0;
		public static final int RECEIVED_CALL = 1;
		public static final int MISSED_CALL = 2;
		
		public CallRecord(String num, int callStatus, long time) {
			super();
			this.num = num;
			this.callStatus = callStatus;
			this.time = time;
		}
		
		public CallRecord(String name, String iconUrl, String num, int numType, int callStatus, long time) {
			super();
			this.name = name;
			this.iconUrl = iconUrl;
			this.num = num;
			this.numType = numType;
			this.callStatus = callStatus;
			this.time = time;
		}	
	}
