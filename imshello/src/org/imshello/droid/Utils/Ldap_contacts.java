package org.imshello.droid.Utils;

import java.io.File;

public class Ldap_contacts {

private static final long serialVersionUID = 1L;
	
	//���Խ���ʹ��public���壬��android�п��ʵ���������
	public String name;	
	public String ou; //���ڲ���
	public int groudType; //�����������
	public File iconFile; //ͷ���ļ�
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

