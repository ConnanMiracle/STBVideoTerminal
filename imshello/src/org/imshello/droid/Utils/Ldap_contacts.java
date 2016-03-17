package org.imshello.droid.Utils;

import java.io.File;

public class Ldap_contacts {

private static final long serialVersionUID = 1L;
	
	//属性建议使用public定义，在android中可适当提升性能
	public String name;	
	public String ou; //所在部门
	public int groudType; //所在组的类型
	public File iconFile; //头像文件
	public String mobileNum;
	public String mainNum;
	public String extensionNum;
	public String email;
	public String address;
	public String employeeType;
	public String employeeNum;
	
	public Ldap_contacts(){
		super();
	}
	
	public Ldap_contacts(String name) {
		super();
		this.name = name;
	}
	
	public Ldap_contacts(String name,  String mobileNum) {
		super();
		this.name = name;
		this.ou = null;
		this.mobileNum = mobileNum;
		this.mainNum = null;
		this.extensionNum = null;
		this.email = null;
		this.address = null;
		this.employeeType = null;
		this.employeeNum = null;
	}	
		
}

