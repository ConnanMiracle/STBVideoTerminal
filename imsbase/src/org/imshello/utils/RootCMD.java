package org.imshello.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class RootCMD

{
	// ִ��linux�����������
	public static String execRootCmd(String paramString) {
		String resultString = "";
		try {
			Process localProcess = Runtime.getRuntime().exec("su ");// ����Root�����androidϵͳ����su����
			OutputStream localOutputStream = localProcess.getOutputStream();
			DataOutputStream localDataOutputStream = new DataOutputStream(
					localOutputStream);
			InputStream localInputStream = localProcess.getInputStream();
			DataInputStream localDataInputStream = new DataInputStream(
					localInputStream);		
			String str1 = String.valueOf(paramString);
			String str2 = str1 + "\n";
			localDataOutputStream.writeBytes(str2);
			localDataOutputStream.flush();
			String str3 = localDataInputStream.readLine();
			resultString = str3;
			localDataOutputStream.writeBytes("exit\n");
			localDataOutputStream.flush();
			localProcess.waitFor();
			return resultString;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return resultString;
	}

	// ִ��linux�������ע������
	public static int execRootCmdSilent(String paramString) {
		try {
			Process localProcess = Runtime.getRuntime().exec("su");
			Object localObject = localProcess.getOutputStream();
			DataOutputStream localDataOutputStream = new DataOutputStream(
					(OutputStream) localObject);
			String str = String.valueOf(paramString);
			localObject = str + "\n";
			localDataOutputStream.writeBytes((String) localObject);
			localDataOutputStream.flush();
			localDataOutputStream.writeBytes("exit\n");
			localDataOutputStream.flush();
			localProcess.waitFor();
			localObject = localProcess.exitValue();
			return Integer.parseInt(localObject.toString());
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return -1;
	}

	// �жϻ���Android�Ƿ��Ѿ�root�����Ƿ��ȡrootȨ��
	public static boolean haveRoot() {

		int i = execRootCmdSilent("echo test"); // ͨ��ִ�в������������
		if (i != -1)
			return true;
		return false;
	}

}
