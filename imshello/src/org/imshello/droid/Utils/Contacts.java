package org.imshello.droid.Utils;

public class Contacts {

	//���Խ���ʹ��public���壬��android�п��ʵ���������
	public String name;
	public String text;
	public String iconUrl; //ͷ��·��
	public int groupId; //�������id
	public int childId; //���������id
	public int groudType; //�����������
	
	public Contacts(String name, String text, String iconUrl) {
		super();
		this.name = name;
		this.text = text;
		this.iconUrl = iconUrl;
	}
	
	public Contacts(String name, String text, String iconUrl, int groupId,
			int childId,int groudType) {
		super();
		this.name = name;
		this.text = text;
		this.iconUrl = iconUrl;
		this.groupId = groupId;
		this.childId = childId;
		this.groudType = groudType;
	}
	
}
