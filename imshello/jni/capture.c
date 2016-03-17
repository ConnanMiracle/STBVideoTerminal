/*
 *  V4L2 video capture example
 */
 
#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <string.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include <fcntl.h>              /* low-level i/o */
#include <unistd.h>
#include <errno.h>
#include <malloc.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/mman.h>
#include <sys/ioctl.h>

#include <asm/types.h>          /* for videodev2.h */

#include <linux/videodev2.h>
#include <linux/usbdevice_fs.h>

#define  LOG_TAG    "USB_V"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define CLEAR(x) memset (&(x), 0, sizeof (x))

#ifndef V4L2_PIX_FMT_H264
#define V4L2_PIX_FMT_H264     v4l2_fourcc('H', '2', '6', '4') /* H264 with start codes */
#endif

#define  CAMERA_YUV_CAPTURE  1
#define  C920_H264_CAPTURE   2


struct buffer {
    void *   start;
    size_t   length;
};
struct buffer *     buffers  = NULL;
static unsigned int n_buffers= 0;


char* dev_name="/dev/video0";
unsigned char* frame_buffer;
int   size_frame_buffer;

static int fmt_pix_width; 
static int fmt_pix_height;
static int fmt_frame_rate;
static int fmt_pix_format;

static int  fd= -1; //device socket
static int nStopFlag=0;


static int errnoexit(const char *s)
{
	LOGE("%s error %d, %s", s, errno, strerror (errno));
	return ERROR_LOCAL;
}

static int xioctl(int fh, int request, void *arg)
{
	int r;

	do {
		r = ioctl(fh, request, arg);
	} while (-1 == r && EINTR == errno);

	return r;
}

static int initmmap(void)
{
	struct v4l2_requestbuffers req;

	CLEAR (req);

	req.count               = 4;
	req.type                = V4L2_BUF_TYPE_VIDEO_CAPTURE;
	req.memory              = V4L2_MEMORY_MMAP;

	if (-1 == xioctl (fd, VIDIOC_REQBUFS, &req)) {
		if (EINVAL == errno) {
			LOGE("%s does not support memory mapping", dev_name);
			return ERROR_LOCAL;
		} else {
			return errnoexit ("VIDIOC_REQBUFS");
		}
	}

	if (req.count < 2) {
		LOGE("Insufficient buffer memory on %s", dev_name);
		return ERROR_LOCAL;
 	}

	buffers = calloc (req.count, sizeof (*buffers));

	if (!buffers) {
		LOGE("Out of memory");
		return ERROR_LOCAL;
	}

	for (n_buffers = 0; n_buffers < req.count; ++n_buffers) {
		struct v4l2_buffer buf;

		 CLEAR (buf);

		buf.type        = V4L2_BUF_TYPE_VIDEO_CAPTURE;
		buf.memory      = V4L2_MEMORY_MMAP;
		buf.index       = n_buffers;

		if (-1 == xioctl (fd, VIDIOC_QUERYBUF, &buf))
			return errnoexit ("VIDIOC_QUERYBUF");

		buffers[n_buffers].length = buf.length;
		buffers[n_buffers].start =
		mmap (NULL ,
			buf.length,
			PROT_READ | PROT_WRITE,
			MAP_SHARED,
			fd, buf.m.offset);

		if (MAP_FAILED == buffers[n_buffers].start)
			return errnoexit ("mmap");
	}

	return SUCCESS_LOCAL;
}

static int open_device(void)
{
  
	struct stat st;

	if (-1 == stat (dev_name, &st)) {
		LOGE("Cannot identify '%s': %d, %s", dev_name, errno, strerror (errno));
		return ERROR_LOCAL;
	}

	if (!S_ISCHR (st.st_mode)) {
		LOGE("%s is no device", dev_name);
		return ERROR_LOCAL;
	}

	fd = open (dev_name, O_RDWR | O_NONBLOCK, 0);

	if (-1 == fd) {
		LOGE("Cannot open '%s': %d, %s", dev_name, errno, strerror (errno));
		return ERROR_LOCAL;
	}
	return SUCCESS_LOCAL;
}
	
static int init_device(void) 
{
	struct v4l2_capability cap;
	struct v4l2_cropcap cropcap;
	struct v4l2_crop crop;
	struct v4l2_format fmt;
	unsigned int min;

	if (-1 == xioctl (fd, VIDIOC_QUERYCAP, &cap)) {
		if (EINVAL == errno) {
			LOGE("%s is no V4L2 device", dev_name);
			return ERROR_LOCAL;
		} else {
			return errnoexit ("VIDIOC_QUERYCAP");
		}
	}

	if (!(cap.capabilities & V4L2_CAP_VIDEO_CAPTURE)) {
		LOGE("%s is no video capture device", dev_name);
		return ERROR_LOCAL;
	}

	if (!(cap.capabilities & V4L2_CAP_STREAMING)) {
		LOGE("%s does not support streaming i/o", dev_name);
		return ERROR_LOCAL;
	}
	
	CLEAR (cropcap);

	cropcap.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

	if (0 == xioctl (fd, VIDIOC_CROPCAP, &cropcap)) {
		crop.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
		crop.c = cropcap.defrect; 

		if (-1 == xioctl (fd, VIDIOC_S_CROP, &crop)) {
			switch (errno) {
				case EINVAL:
					break;
				default:
					break;
			}
		}
	} else {
	}

	CLEAR (fmt);

	fmt.type                = V4L2_BUF_TYPE_VIDEO_CAPTURE;

	fmt.fmt.pix.width       = fmt_pix_width; 
	fmt.fmt.pix.height      = fmt_pix_height;

	if(fmt_pix_format==C920_H264_CAPTURE)
	{
	    fmt.fmt.pix.pixelformat = V4L2_PIX_FMT_H264;
	}
	else
	{
	    fmt.fmt.pix.pixelformat = V4L2_PIX_FMT_YUYV;
	}
	
	fmt.fmt.pix.field = V4L2_FIELD_ANY;
	//fmt.fmt.pix.field       = V4L2_FIELD_INTERLACED;

	if (-1 == xioctl (fd, VIDIOC_S_FMT, &fmt))
		return errnoexit ("VIDIOC_S_FMT");

	min = fmt.fmt.pix.width * 2;
	if (fmt.fmt.pix.bytesperline < min)
		fmt.fmt.pix.bytesperline = min;
	min = fmt.fmt.pix.bytesperline * fmt.fmt.pix.height;
	if (fmt.fmt.pix.sizeimage < min)
		fmt.fmt.pix.sizeimage = min;

	return initmmap();
}

static int start_stream(void)
{
	unsigned int i;
	enum v4l2_buf_type type;

	for (i = 0; i < n_buffers; ++i) {
		struct v4l2_buffer buf;

		CLEAR (buf);

		buf.type        = V4L2_BUF_TYPE_VIDEO_CAPTURE;
		buf.memory      = V4L2_MEMORY_MMAP;
		buf.index       = i;

		if (-1 == xioctl (fd, VIDIOC_QBUF, &buf))
			return errnoexit ("VIDIOC_QBUF");
	}

	type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

	if (-1 == xioctl (fd, VIDIOC_STREAMON, &type))
		return errnoexit ("VIDIOC_STREAMON");

	return SUCCESS_LOCAL;
}


static int stop_stream(void)
{
	enum v4l2_buf_type type;

	type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

	if (-1 == xioctl (fd, VIDIOC_STREAMOFF, &type))
		return errnoexit ("VIDIOC_STREAMOFF");

	return SUCCESS_LOCAL;
}


static int uninit_device(void)
{
	unsigned int i;

	for (i = 0; i < n_buffers; ++i)
		if (-1 == munmap (buffers[i].start, buffers[i].length))
			return errnoexit ("munmap");

	free (buffers);

	return SUCCESS_LOCAL;
}

static int close_device(void)
{
	if (-1 == close (fd)){
		fd = -1;
		return errnoexit ("close");
	}

	fd = -1;
	return SUCCESS_LOCAL;
}


JNIEXPORT jint JNICALL Java_org_imshello_droid_Utils_USBCamera_SetDevName
  (JNIEnv *pEnv, jobject pObj, jstring sDevName)
{
    dev_name=(char *)(*pEnv)->GetStringUTFChars(pEnv, sDevName, NULL);
    return 0;
}

/*
JNIEXPORT jint JNICALL Java_org_imshello_USBCamera_SetFrameBuffer
  (JNIEnv *pEnv, jobject pObj, jbyteArray bFrameBuf)
{
    frame_buffer=(unsigned char*)(*pEnv)->GetByteArrayElements(pEnv,bFrameBuf,0);  //GetDirectBufferAddress
    size_frame_buffer=0;
    return 0;
}
*/


JNIEXPORT jint JNICALL Java_org_imshello_droid_Utils_USBCamera_SetCaptureParameters
  (JNIEnv *pEnv, jobject pObj, jint pix_width, jint pix_height, jint frame_rate, jint pix_format)
{
    fmt_pix_width=pix_width;
    fmt_pix_height=pix_height; 
    fmt_frame_rate=frame_rate;
    fmt_pix_format=pix_format;   
    return 0;
}

JNIEXPORT jint JNICALL Java_org_imshello_droid_Utils_USBCamera_StartCapture(JNIEnv *pEnv, jobject pObj, jbyteArray bFrameBuf)
{
	fd_set fds;
	struct timeval tv;
	int r;
	struct v4l2_buffer buf;
	
	frame_buffer=(unsigned char*)(*pEnv)->GetByteArrayElements(pEnv,bFrameBuf,0);  //GetDirectBufferAddress
    size_frame_buffer=0;
	
	jclass cls=(*pEnv)->GetObjectClass(pEnv,pObj);
	jmethodID OnNewVideoFrameCallback = (*pEnv)->GetMethodID(pEnv, cls, "OnNewVideoFrameCallback", "(I)I");
	
	open_device();
	init_device(); 
	start_stream();
	nStopFlag=0;
	
	while(1)
	{
		if(nStopFlag==1) // When call "StopCapture(), it was set =1"
	        break;
		
		for (;; ) 
		{
			FD_ZERO(&fds);
			FD_SET(fd, &fds);

			/* Timeout. */
			tv.tv_sec = 2;
			tv.tv_usec = 0;

			r = select(fd + 1, &fds, NULL, NULL, &tv);

			if (-1 == r) {
				if (EINTR == errno)
					continue;
				errno_exit("select");
			}

			if (0 == r) {
				fprintf(stderr, "select timeout\n");
				exit(EXIT_FAILURE);
			}	
			
			//if (read_frame())
			//	break;
		    CLEAR(buf);

		    buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
		    buf.memory = V4L2_MEMORY_MMAP;

		    if (-1 == xioctl(fd, VIDIOC_DQBUF, &buf))
			{
			    switch (errno) 
				{
			        case EAGAIN: 
            	        break;
        			case EIO:
		        	/* Could ignore EIO, see spec. */
	        		/* fall through */
			        default:
				        errno_exit("VIDIOC_DQBUF");
			    }
				continue; /* EAGAIN - continue select loop. */
		    }
		    assert(buf.index < n_buffers);

			//buf.index].start, buf.bytesused
			memcpy(frame_buffer, buffers[buf.index].start, buf.bytesused);
			size_frame_buffer=buf.bytesused;
			
		    // unsigned char* frame_buffer;  int size_frame_buffer;
	        (*pEnv)->CallIntMethod(pEnv, pObj, OnNewVideoFrameCallback, size_frame_buffer);	
		    
		    if (-1 == xioctl(fd, VIDIOC_QBUF, &buf))
			    errno_exit("VIDIOC_QBUF");
		    break;
			
		} //for(;;)	
	} //While(1)
	
	stop_stream();
	uninit_device();
	close_device();
	
	(*pEnv)->ReleaseByteArrayElements(pEnv,bFrameBuf,frame_buffer,0);

    return 0;

}

JNIEXPORT jint JNICALL Java_org_imshello_droid_Utils_USBCamera_StopCapture(JNIEnv *pEnv, jobject pObj)
{
    nStopFlag=1;
    return 0;
}


