package org.imshello.droid.Utils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import org.imshello.droid.Screens.ScreenChat.MyHandler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

// �����װ¼���Ĺ��ܣ����Ѹ��ļ�·����������ע�ⷵ�ص�·�����⣬photo_destination:' �����Ƿ���/
public class MyChatService {
	private long startTime=0;
	private long endTime=0;
	public static final int FILE_TYPE_AUDIO = 1;
	public static final int FILE_TYPE_FILE = 2;
	public static final int FILE_TYPE_IMAGE = 3;
	Context context;
	String url = "http://202.118.18.8/";
	String randomName;
	/** log��� */
	private static final String LOG_TAG = "AudioRecordTest";
	/** �����ļ�����·�� */
	private String mFilePath = null;
	/** ��ס˵����ť */
	private Button mBtnVoice;
	/** ������������ */
	private MediaPlayer mPlayer = null;
	/** �������¼�� */
	private MediaRecorder mRecorder = null;
	/** ��ʾ�����б� */
	private ListView mVoidListView;

	/** ¼���洢·�� */
	private static final String p = Environment.getExternalStorageDirectory()
			.getPath() + "/";
	private MyHandler mHandler;

	public MyChatService(Context ctx,MyHandler handler) {
		this.context = ctx;
		this.mHandler = handler;
		mPlayer=new MediaPlayer();
	}

	/** ��ʼ¼�� */
	public void startVoice() {
		startTime=System.currentTimeMillis();
		// ����¼������·��
		randomName = UUID.randomUUID().toString();
		mFilePath = p + randomName + ".amr";
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			Log.i(LOG_TAG, "SD Card is not mounted,It is  " + state + ".");
		}
		File directory = new File(mFilePath).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			Log.i(LOG_TAG, "Path to file could not be created");
		}
		Toast.makeText(context, "��ʼ¼��", 0).show();
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mRecorder.setOutputFile(mFilePath);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
		mRecorder.start();
	}

	/** ֹͣ¼�� */
	public void stopVoice() {
		endTime=System.currentTimeMillis();
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		doUpload(mFilePath, FILE_TYPE_AUDIO);
	}

	public void doUpload(String mFilePath, int mFileType) {
		new UploadFileTask(mFileType).execute(
				"http://202.118.18.8/upload_test/upload_file.php", mFilePath);
	};

	public void doDownload(View v,String mFilePath, int mFileType) {
		new DownloadFileTask(v,mFileType).execute(url + mFilePath, mFilePath);
	};

	private InputStream openHttpConnection(String urlString) throws IOException {
		InputStream in = null;
		int response = -1;
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		if (!(conn instanceof HttpURLConnection))
			throw new IOException("Not an Http connection!");
		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
/*			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
			}*/
			in = httpConn.getInputStream();
		} catch (Exception e) {
			Log.e("inputStream openHttpConnection", e.toString());
			throw new IOException("Error connecting : get");
		}
		return in;
	}

	private String openHttpConnection(String urlString, File file)
			throws IOException {
		OutputStream out = null;
		URL url = new URL(urlString);
		String filePath = "";

		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "---------------------------7df186291601dc";

		// String boundary = "******";
		URLConnection conn = url.openConnection();
		/*
		 * �˴���urlConnection����ʵ�����Ǹ���URL�� ����Э��(�˴���http)���ɵ�URLConnection��
		 * ������HttpURLConnection,�ʴ˴���ý���ת�� ΪHttpURLConnection���͵Ķ���,�Ա��õ�
		 * HttpURLConnection�����API.����:
		 */
		if (!(conn instanceof HttpURLConnection))
			throw new IOException("Not an Http connection!");
		try {
			HttpURLConnection httpUrlConnection = (HttpURLConnection) conn;
			/*
			 * �����Ƿ���httpUrlConnection�������Ϊ�����post���󣬲���Ҫ���� http�����ڣ������Ҫ��Ϊtrue,
			 * Ĭ���������false
			 */
			httpUrlConnection.setDoOutput(true);
			/* �����Ƿ��httpUrlConnection���룬Ĭ���������true */
			httpUrlConnection.setDoInput(true);
			/* Post ������ʹ�û��� */
			httpUrlConnection.setUseCaches(false);
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charset", "GB2312");
			/* �趨����ķ���Ϊ"POST"��Ĭ����GET */
			httpUrlConnection.setRequestMethod("POST");
			// httpUrlConnection.addRequestProperty("FileName", file.getName());
			httpUrlConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			// httpUrlConnection.setRequestProperty("content-type", "text/xml");
			/*
			 * �趨���͵����������ǿ����л���java����
			 * ,����������,�ڴ������л�����ʱ,��WEB����Ĭ�ϵĲ�����������ʱ������java.io.EOFException)
			 */
			// httpUrlConnection.setRequestProperty("Content-type",
			// "application/x-java-serialized-object");

			/* ���ӣ���������2����url.openConnection()���˵����ñ���Ҫ��connect֮ǰ��� */
			httpUrlConnection.connect();
			out = httpUrlConnection.getOutputStream();

			StringBuilder sb1 = new StringBuilder();
			sb1.append(twoHyphens);
			sb1.append(boundary);
			sb1.append(end);
			sb1.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
					+ file.getName() + "\"" + end);
			sb1.append("Content-Type: application/octet-stream;chartset="
					+ "GB2312" + end);
			sb1.append(end);
			// д�뵽�������
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeBytes(twoHyphens + boundary + end);
			Log.e("MIMIE", sb1.toString());
			dos.write(sb1.toString().getBytes());

			byte[] b = new byte[1024];
			int len = 0;
			try {
				FileInputStream in = new FileInputStream(file);
				while ((len = in.read(b)) != -1) {
					dos.write(b, 0, len);
					Log.e("outputStream openHttpConnection 1",
							String.format("%s", b));
				}
				in.close();
				dos.writeBytes(end);
			} catch (IOException e1) {
				Log.e("outputStream openHttpConnection 2", e1.toString());
			}

			dos.writeBytes(end + twoHyphens + boundary + twoHyphens + end);
			dos.flush();
			int res = httpUrlConnection.getResponseCode();
			InputStream in = null;
			// �ϴ��ɹ�����200
			if (res == 200) {
				in = conn.getInputStream();
				int ch;
				StringBuilder sb2 = new StringBuilder();
				// ��������
				while ((ch = in.read()) != -1) {
					sb2.append((char) ch);
				}
				Log.e("TAG", sb2.toString());
				filePath = sb2.substring(
						sb2.indexOf("photo_destination:'") + 19,
						sb2.indexOf("',"));
				Log.e("TAG", filePath);
			}
		} catch (Exception e) {
			Log.e("outputStream openHttpConnection 3", e.toString());
			throw new IOException("Error connecting :post");
		}
		return filePath;
	}

	private File performDownloadAndStore(String url, File file) {
		InputStream in = null;
		byte[] b = new byte[1024];
		FileOutputStream out=null;
		try {
			out = new FileOutputStream(file);
			in = openHttpConnection(url);
			if(in==null){
				Log.e(LOG_TAG, "input stream is null!");
				return file;
			}
			while (in.read(b)!=-1) {
				out.write(b);
			}
			out.flush();
			//in.close();
		
		} catch (IOException e1) {
			Log.e("Download perform", e1.toString());
			return null;
		}finally{
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return file;
	}

	/**
	 * This class defines an asyncTask to execute download action. Called when
	 * audio item is clicked. To start a download, do
	 * {@code new DownloadTextTask().execute(url,file_path);}
	 * 
	 * @return file download
	 * @author wangy
	 * 
	 */
	public class DownloadFileTask extends AsyncTask<String, Void, File> {
		int fileType = -1;
		View view;
		Bitmap image=null;

		public DownloadFileTask(View v,int type) {
			this.fileType = type;
			this.view=v;
		}

		@Override
		protected File doInBackground(String... params) {
			File f;
			int indexOfPoint=-1,tmp=0;
			do{	
				tmp=params[1].indexOf(".",indexOfPoint+1);
				if(tmp!=-1)
					indexOfPoint=tmp;
				else
					break;
			}while(true);			
			String fileNameExtension=params[1].substring(indexOfPoint+1); 
			if(fileType==FILE_TYPE_AUDIO){
				f = new File(Environment.getExternalStorageDirectory().getPath()+"/IMSHELLO_REC"+"."+fileNameExtension);
				return performDownloadAndStore(params[0], f);
			}
			else if(fileType==FILE_TYPE_FILE){
				f = new File(Environment.getExternalStorageDirectory().getPath()+"/IMSHELLO_FILE"+"."+fileNameExtension);
				return performDownloadAndStore(params[0], f);
			}
			else{
				f = new File(Environment.getExternalStorageDirectory().getPath()+"/IMSHELLO_IMAGE"+"."+fileNameExtension);
				image=getNetWorkBitmap(params[0]);
				return changeBitMapToFile(image,f);
			}
			//Log.e("file_path", f.getAbsolutePath());
			//return performDownloadAndStore(params[0], f);
		}

		@Override
		protected void onPostExecute(File result) {
			if (fileType==FILE_TYPE_IMAGE) {
				Drawable d;
				ImageView v = (ImageView) view;
				v.setImageBitmap(image);
				//ʹ��Intent
				Intent intent = new Intent(Intent.ACTION_VIEW);
				//Uri mUri = Uri.parse("file://" + picFile.getPath());Android3.0�Ժ���ò�Ҫͨ���÷���������һЩСBug
				intent.setDataAndType(Uri.fromFile(result), "image/*");
				context.startActivity(intent);

			}
			/*HashMap<String,Object> map=new HashMap<String,Object>();
			map.put("File", result);
			map.put("Image", image);*/
			mHandler.obtainMessage(MyHandler.FILE_DOWNLOAD_PATH, fileType, -1,
					result).sendToTarget();
		}
	}

	/**
	 * This class defines an asyncTaxk to execute upload action. To start an
	 * upload, do {@code new UploadFileTask().execute(sever_url,file_path);}
	 * 
	 * @return File uploaded.
	 * @author wangy
	 * 
	 */
	public class UploadFileTask extends AsyncTask<String, Void, String> {
		int fileType = -1;

		public UploadFileTask(int type) {
			this.fileType = type;
		}

		@Override
		protected String doInBackground(String... params) {
			File f = new File(params[1]);
			String filePath = "";/* path of the file uploaded on the server */
			try {
				filePath = openHttpConnection(params[0], f);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("upload task", e.toString());
			}
			return filePath;
		}

		@Override
		protected void onPostExecute(String result) {
			int audioTime=(int) ((endTime-startTime)/1000);
			mHandler.obtainMessage(MyHandler.FILE_UPLOAD_PATH, fileType, audioTime,
					result).sendToTarget();
		}

	}
/*
	private class MyHandler extends Handler {
		public static final int FILE_UPLOAD_PATH = 0;
		public static final int FILE_DOWNLOAD_PATH = 1;

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MyHandler.FILE_UPLOAD_PATH) {
				Toast.makeText(context,
						"uploadfile executed, stored at " + msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				String fileInfoMsg = "/THIS IS A INFO MESSAGE/"
						+ "FILE_PATH:" + msg.obj.toString();
				// mSession.SendMessage(fileInfoMsg);
			} else if (msg.what == MyHandler.FILE_DOWNLOAD_PATH) {
				Toast.makeText(
						context,
						"downloadfile executed, stored at "
								+ msg.obj.toString(), Toast.LENGTH_SHORT)
						.show();
				if (msg.arg1 == 1) {
					File f = (File) msg.obj;
					try {
						mPlayer.reset();
						mPlayer.setDataSource(f.getAbsolutePath());
						mPlayer.prepare();
						mPlayer.start();
					} catch (IOException e) {
						Log.e(LOG_TAG, "����ʧ��");
					}
				} else if (msg.arg1 == 2) {
					Toast.makeText(context, "downloadfile executed",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
*/
	public void playAudioMsg(File f) {
		if (f == null)
			return;
		try {
			mPlayer.reset();
			mPlayer.setDataSource(f.getAbsolutePath());
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "����ʧ��"+e.toString());
		}
	}
	
	public static Bitmap getNetWorkBitmap(String urlString) {
		URL imgUrl = null;
		Bitmap bitmap = null;
		try {
			imgUrl = new URL(urlString);
			// ʹ��HttpURLConnection������
			HttpURLConnection urlConn = (HttpURLConnection) imgUrl
					.openConnection();
			urlConn.setDoInput(true);
			urlConn.connect();
			// ���õ�������ת����InputStream
			InputStream is = urlConn.getInputStream();
			// ��InputStreamת����Bitmap
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("[getNetWorkBitmap->]MalformedURLException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[getNetWorkBitmap->]IOException");
			e.printStackTrace();
		}
		return bitmap;
	}
	
	private File changeBitMapToFile(Bitmap b,File f){
		//File f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/","TESTBITMAP.jpg");
		 BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(f));			
			b.compress(Bitmap.CompressFormat.JPEG, 80, bos);  
	        bos.flush();  
	        bos.close();  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("test", e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return f;
	}

}
