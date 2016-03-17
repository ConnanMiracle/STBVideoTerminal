package org.imshello.droid.Utils;

public class Contacts {

	//属性建议使用public定义，在android中可适当提升性能
	public String name;
	public String text;
	public String iconUrl; //头像路径
	public int groupId; //所在组的id
	public int childId; //所在子组的id
	public int groudType; //所在组的类型
	
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
