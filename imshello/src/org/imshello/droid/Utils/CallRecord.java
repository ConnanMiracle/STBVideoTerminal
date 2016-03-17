package org.imshello.droid.Utils;

public class CallRecord {
	
	//���Խ���ʹ��public���壬��android�п��ʵ���������
		public String name; //��ϵ������
		public String num; //��ϵ�˵绰
		public int numType; //�绰����
		public String iconUrl; //ͷ��·��
		public int callStatus; //ͨ��״̬
		public long time; //ͨ��ʱ��
		
		//�绰����
		public static final int TELEPHONE = 0;
		public static final int MOBILE_PHONE = 1;
		
		//ͨ��״̬
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
