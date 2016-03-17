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

// 还需封装录音的功能，并把该文件路径传过来。注意返回的路径问题，photo_destination:' 后面是否有/
public class MyChatService {
	private long startTime=0;
	private long endTime=0;
	public static final int FILE_TYPE_AUDIO = 1;
	public static final int FILE_TYPE_FILE = 2;
	public static final int FILE_TYPE_IMAGE = 3;
	Context context;
	String url = "http://202.118.18.8/";
	String randomName;
	/** log标记 */
	private static final String LOG_TAG = "AudioRecordTest";
	/** 语音文件保存路径 */
	private String mFilePath = null;
	/** 按住说话按钮 */
	private Button mBtnVoice;
	/** 用于语音播放 */
	private MediaPlayer mPlayer = null;
	/** 用于完成录音 */
	private MediaRecorder mRecorder = null;
	/** 显示语音列表 */
	private ListView mVoidListView;

	/** 录音存储路径 */
	private static final String p = Environment.getExternalStorageDirectory()
			.getPath() + "/";
	private MyHandler mHandler;

	public MyChatService(Context ctx,MyHandler handler) {
		this.context = ctx;
		this.mHandler = handler;
		mPlayer=new MediaPlayer();
	}

	/** 开始录音 */
	public void startVoice() {
		startTime=System.currentTimeMillis();
		// 设置录音保存路径
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
		Toast.makeText(context, "开始录音", 0).show();
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

	/** 停止录音 */
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
		 * 此处的urlConnection对象实际上是根据URL的 请求协议(此处是http)生成的URLConnection类
		 * 的子类HttpURLConnection,故此处最好将其转化 为HttpURLConnection类型的对象,以便用到
		 * HttpURLConnection更多的API.如下:
		 */
		if (!(conn instanceof HttpURLConnection))
			throw new IOException("Not an Http connection!");
		try {
			HttpURLConnection httpUrlConnection = (HttpURLConnection) conn;
			/*
			 * 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在 http正文内，因此需要设为true,
			 * 默认情况下是false
			 */
			httpUrlConnection.setDoOutput(true);
			/* 设置是否从httpUrlConnection读入，默认情况下是true */
			httpUrlConnection.setDoInput(true);
			/* Post 请求不能使用缓存 */
			httpUrlConnection.setUseCaches(false);
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charset", "GB2312");
			/* 设定请求的方法为"POST"，默认是GET */
			httpUrlConnection.setRequestMethod("POST");
			// httpUrlConnection.addRequestProperty("FileName", file.getName());
			httpUrlConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			// httpUrlConnection.setRequestProperty("content-type", "text/xml");
			/*
			 * 设定传送的内容类型是可序列化的java对象
			 * ,如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
			 */
			// httpUrlConnection.setRequestProperty("Content-type",
			// "application/x-java-serialized-object");

			/* 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成 */
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
			// 写入到输出流中
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
			// 上传成功返回200
			if (res == 200) {
				in = conn.getInputStream();
				int ch;
				StringBuilder sb2 = new StringBuilder();
				// 保存数据
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
				//使用Intent
				Intent intent = new Intent(Intent.ACTION_VIEW);
				//Uri mUri = Uri.parse("file://" + picFile.getPath());Android3.0以后最好不要通过该方法，存在一些小Bug
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
						Log.e(LOG_TAG, "播放失败");
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
			Log.e(LOG_TAG, "播放失败"+e.toString());
		}
	}
	
	public static Bitmap getNetWorkBitmap(String urlString) {
		URL imgUrl = null;
		Bitmap bitmap = null;
		try {
			imgUrl = new URL(urlString);
			// 使用HttpURLConnection打开连接
			HttpURLConnection urlConn = (HttpURLConnection) imgUrl
					.openConnection();
			urlConn.setDoInput(true);
			urlConn.connect();
			// 将得到的数据转化成InputStream
			InputStream is = urlConn.getInputStream();
			// 将InputStream转换成Bitmap
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
