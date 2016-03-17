/* Copyright (C) 2010-2014, IMSHello R&D Group
*  Contact:  <rd(at)imshello(dot)org>	
*  This file is part of 'IMSHello for Android' Project 
*
*/
package org.imshello.ngn.utils;

import org.imshello.ngn.sip.NgnPresenceStatus;
import org.imshello.utils.USBCamera;
import org.imshello.baseWRAP.tdav_codec_id_t;
import org.imshello.baseWRAP.tmedia_bandwidth_level_t;
import org.imshello.baseWRAP.tmedia_pref_video_size_t;
import org.imshello.baseWRAP.tmedia_profile_t;
import org.imshello.baseWRAP.tmedia_qos_strength_t;
import org.imshello.baseWRAP.tmedia_qos_stype_t;
import org.imshello.baseWRAP.tmedia_srtp_mode_t;
import org.imshello.baseWRAP.tmedia_srtp_type_t;


public class NgnConfigurationEntry {
	private static final String TAG = NgnConfigurationEntry.class.getCanonicalName();
	
	public final static String  SHARED_PREF_NAME = TAG;
	public static final String PCSCF_DISCOVERY_DNS_SRV = "DNS NAPTR+SRV";
	
	// General
	public static final String GENERAL_AUTOSTART = "GENERAL_AUTOSTART." + TAG;
	public static final String GENERAL_AUTOSTART_VIDEO = "GENERAL_AUTOSTART_VIDEO." + TAG;
	public static final String GENERAL_SHOW_WELCOME_SCREEN = "GENERAL_SHOW_WELCOME_SCREEN." + TAG;
	public static final String GENERAL_FULL_SCREEN_VIDEO = "GENERAL_FULL_SCREEN_VIDEO." + TAG;
	public static final String GENERAL_USE_FFC = "GENERAL_USE_FFC." + TAG;
	public static final String GENERAL_INTERCEPT_OUTGOING_CALLS = "GENERAL_INTERCEPT_OUTGOING_CALLS." + TAG;
	public static final String GENERAL_AUDIO_PLAY_LEVEL = "GENERAL_AUDIO_PLAY_LEVEL." + TAG;
	public static final String GENERAL_ENUM_DOMAIN = "GENERAL_ENUM_DOMAIN." + TAG;
	public static final String GENERAL_AEC = "GENERAL_AEC."+ TAG ;
	public static final String GENERAL_VAD = "GENERAL_VAD."+ TAG ;	
	public static final String GENERAL_NR = "GENERAL_NR."+ TAG ;	
	public static final String GENERAL_ECHO_TAIL = "GENERAL_ECHO_TAIL." + TAG ;
	public static final String GENERAL_USE_ECHO_TAIL_ADAPTIVE = "GENERAL_USE_ECHO_TAIL_ADAPTIVE." + TAG ;
	public static final String GENERAL_SEND_DEVICE_INFO = "GENERAL_SEND_DEVICE_INFO" + TAG;
	
	// Identity
	public static final String IDENTITY_DISPLAY_NAME = "IDENTITY_DISPLAY_NAME." + TAG;
	public static final String IDENTITY_IMPU = "IDENTITY_IMPU." + TAG;
	public static final String IDENTITY_IMPI = "IDENTITY_IMPI." + TAG;
	public static final String IDENTITY_PASSWORD = "IDENTITY_PASSWORD." + TAG;
	
	// Network
	public static final String NETWORK_REGISTRATION_TIMEOUT = "NETWORK_REGISTRATION_TIMEOUT." + TAG;
	public static final String NETWORK_REALM = "NETWORK_REALM" + TAG;
	public static final String NETWORK_USE_WIFI = "NETWORK_USE_WIFI." + TAG;
	public static final String NETWORK_USE_3G = "NETWORK_USE_3G." + TAG;
	public static final String NETWORK_USE_ETHERNET = "NETWORK_USE_ETHERNET." + TAG;
	public static final String NETWORK_USE_EARLY_IMS = "NETWORK_USE_EARLY_IMS." + TAG;
	public static final String NETWORK_IP_VERSION = "NETWORK_IP_VERSION." + TAG;
	public static final String NETWORK_PCSCF_DISCOVERY = "NETWORK_PCSCF_DISCOVERY." + TAG;
	public static final String NETWORK_PCSCF_HOST = "NETWORK_PCSCF_HOST" + TAG;
	public static final String NETWORK_PCSCF_PORT = "NETWORK_PCSCF_PORT" + TAG ;
	public static final String NETWORK_USE_SIGCOMP = "NETWORK_USE_SIGCOMP." + TAG;
	public static final String NETWORK_TRANSPORT = "NETWORK_TRANSPORT." + TAG;
	
	// NAT Traversal
	public static final String NATT_HACK_AOR = "NATT_HACK_AOR." + TAG;
	public static final String NATT_HACK_AOR_TIMEOUT = "NATT_HACK_AOR_TIMEOUT." + TAG;
	public static final String NATT_USE_STUN = "NATT_USE_STUN." + TAG;
	public static final String NATT_USE_ICE = "NATT_USE_ICE." + TAG;
	public static final String NATT_STUN_DISCO = "NATT_STUN_DISCO." + TAG;
	public static final String NATT_STUN_SERVER = "NATT_STUN_SERVER." + TAG;
	public static final String NATT_STUN_PORT = "NATT_STUN_PORT." + TAG;
	
	// QoS
	public static final String QOS_PRECOND_BANDWIDTH_LEVEL = "QOS_PRECOND_BANDWIDTH_LEVEL." + TAG;
	public static final String QOS_PRECOND_STRENGTH = "QOS_PRECOND_STRENGTH." + TAG;
    public static final String QOS_PRECOND_TYPE = "QOS_PRECOND_TYPE." + TAG;
    public static final String QOS_REFRESHER = "QOS_REFRESHER." + TAG;
    public static final String QOS_SIP_CALLS_TIMEOUT = "QOS_SIP_CALLS_TIMEOUT." + TAG;
    public static final String QOS_SIP_SESSIONS_TIMEOUT = "QOS_SIP_SESSIONS_TIMEOUT" + TAG;
    public static final String QOS_USE_SESSION_TIMERS = "QOS_USE_SESSION_TIMERS." + TAG;
    public static final String QOS_PREF_VIDEO_SIZE = "QOS_PREF_VIDEO_SIZE." + TAG;
    public static final String QOS_USE_ZERO_VIDEO_ARTIFACTS = "QOS_USE_ZERO_VIDEO_ARTIFACTS." + TAG;

	
	// Media
	public static final String MEDIA_CODECS = "MEDIA_CODECS." + TAG;
	public static final String MEDIA_AUDIO_RESAMPLER_QUALITY = "MEDIA_AUDIO_RESAMPLER_QUALITY." + TAG;
	public static final String MEDIA_AUDIO_CONSUMER_GAIN = "MEDIA_AUDIO_CONSUMER_GAIN." + TAG;
	public static final String MEDIA_AUDIO_PRODUCER_GAIN = "MEDIA_AUDIO_PRODUCER_GAIN." + TAG;
	public static final String MEDIA_AUDIO_CONSUMER_ATTENUATION = "MEDIA_AUDIO_CONSUMER_ATTENUATION." + TAG;
	public static final String MEDIA_AUDIO_PRODUCER_ATTENUATION = "MEDIA_AUDIO_PRODUCER_ATTENUATION." + TAG;
	public static final String MEDIA_PROFILE = "MEDIA_PROFILE." + TAG;
	
	// Security
	public static final String SECURITY_SRTP_MODE = "SECURITY_SRTP_MODE." + TAG;
	public static final String SECURITY_SRTP_TYPE = "SECURITY_SRTP_TYPE." + TAG;
	public static final String SECURITY_IMSAKA_AMF = "SECURITY_IMSAKA_AMF." + TAG;
	public static final String SECURITY_IMSAKA_OPID = "SECURITY_IMSAKA_OPID." + TAG;
	public static final String SECURITY_TLS_PRIVKEY_FILE_PATH = "SECURITY_TLS_PRIVKEY_FILE_PATH." + TAG;
	public static final String SECURITY_TLS_PUBKEY_FILE_PATH = "SECURITY_TLS_PUBKEY_FILE_PATH." + TAG;
	public static final String SECURITY_TLS_CA_FILE_PATH = "SECURITY_TLS_CA_FILE_PATH." + TAG;
	public static final String SECURITY_TLS_VERIFY_CERTS = "SECURITY_TLS_VERIFY_CERTS." + TAG;
	
	// XCAP
	public static final String XCAP_PASSWORD = "XCAP_PASSWORD." + TAG;
	public static final String XCAP_USERNAME = "XCAP_USERNAME." + TAG;
	public static final String XCAP_ENABLED = "XCAP_ENABLED." + TAG;
	public static final String XCAP_XCAP_ROOT = "XCAP_XCAP_ROOT." + TAG;
	
	// RCS (Rich Communication Suite)
	public static final String RCS_AVATAR_PATH = "RCS_AVATAR_PATH." + TAG;
	public static final String RCS_USE_BINARY_SMS = "RCS_USE_BINARY_SMS." + TAG;
	public static final String RCS_CONF_FACT = "RCS_CONF_FACT." + TAG;
	public static final String RCS_FREE_TEXT = "RCS_FREE_TEXT." + TAG;
	public static final String RCS_HACK_SMS = "RCS_HACK_SMS." + TAG;
	public static final String RCS_USE_MSRP_FAILURE = "RCS_USE_MSRP_FAILURE." + TAG;
	public static final String RCS_USE_MSRP_SUCCESS = "RCS_USE_MSRP_SUCCESS." + TAG;
	public static final String RCS_USE_MWI = "RCS_USE_MWI." + TAG;
	public static final String RCS_USE_OMAFDR = "RCS_USE_OMAFDR." + TAG;
	public static final String RCS_USE_PARTIAL_PUB = "RCS_USE_PARTIAL_PUB." + TAG;
	public static final String RCS_USE_PRESENCE = "RCS_USE_PRESENCE." + TAG;
	public static final String RCS_USE_RLS = "RCS_USE_RLS." + TAG;
	public static final String RCS_SMSC = "RCS_SMSC." + TAG;
	public static final String RCS_STATUS  = "RCS_STATUS." + TAG;
	
	//AnswerPone by wangy 20140717
	public static final String ANS_TIME="ANS_TIME" + TAG;
	public static final String ANS_STATUS="ANS_STATUS" + TAG;
	
	public static final String USB_CAMERA_STATUS="USB_CAMERA_STATUS."+ TAG;
	public static final String CAPTURE_FMT="CAPTURE_FMT."+ TAG;//"CAMERA_CAPTURE_YUYV";
	public static final String STREAM_FMT="STREAM_FMT"+ TAG;//"STREAM_FMT_YUV420P";
	public static final String LOCAL_VIEW="LOCAL_VIEW."+ TAG;//"CALLBACK_REQUEST_BITMAP";
	public static final String FRAME_WIDTH="FRAME_WIDTH"+ TAG;
	public static final String FRAME_HEIGHT="FRAME_HEIGHT"+ TAG;
	public static final String USB_CAMERA_DEVICE_NAME="USB_CAMERA_DEVICE_NAME"+ TAG;
	
	public static final String QOS_VIDEO_FPS="QOS_VIDEO_FPS"+TAG;
	
	//phone controller by wangy 20160119
	public static final String CONTROLLER_SWITCH="CONTROLLER_SWITCH."+ TAG;
	public static final String CONTROLLER_SCAN_PORT_SEND="CONTROLLER_SCAN_PORT_SEND."+ TAG;
	public static final String CONTROLLER_SCAN_PORT_RECV="CONTROLLER_SCAN_PORT_RECV."+ TAG;
	public static final String CONTROLLER_MESSENGER_PORT_LOCAL_RECV="CONTROLLER_MESSENGER_PORT_LOCAL_RECV."+ TAG;
	public static final String CONTROLLER_MESSENGER_PORT_REMOTE_RECV="CONTROLLER_MESSENGER_PORT_REMOTE_RECV."+ TAG;
	
	//BufferQueue 20160130 by wangy
	public static final String HI_DECODER_BUFFERQUEUE_SIZE="HI_DECODER_BUFFERQUEUE_SIZE."+TAG;
	public static final String HI_DECODER_BUFFERSIZE="HI_DECODER_BUFFERSIZE."+TAG;
	public static final String USB_CAMERA_BUFFERQUEUE_SIZE="USB_CAMERA_BUFFERQUEUE_SIZE."+TAG;
	public static final String USB_CAMERA_BUFFER_SIZE="USB_CAMERA_BUFFER_SIZE."+TAG;
	
	//
	//	Default values
	//
	
	// General
	public static final boolean DEFAULT_GENERAL_SHOW_WELCOME_SCREEN = true;
	public static final boolean DEFAULT_GENERAL_FULL_SCREEN_VIDEO = true;
	public static final boolean DEFAULT_GENERAL_INTERCEPT_OUTGOING_CALLS = true;
	public static final boolean DEFAULT_GENERAL_USE_FFC = true;
	public static final boolean DEFAULT_GENERAL_AUTOSTART = true;
	public static final boolean DEFAULT_GENERAL_AUTOSTART_VIDEO = true;
	public static final float DEFAULT_GENERAL_AUDIO_PLAY_LEVEL = 1.0f;
	public static final String DEFAULT_GENERAL_ENUM_DOMAIN = "e164.org";
	public static final boolean DEFAULT_GENERAL_AEC = true;
	public static final boolean DEFAULT_GENERAL_USE_ECHO_TAIL_ADAPTIVE = true;
	public static final boolean DEFAULT_GENERAL_VAD = false; // speex-dsp don't support VAD for fixed-point implementation
	public static final boolean DEFAULT_GENERAL_NR = true;
	public static final int DEFAULT_GENERAL_ECHO_TAIL = 200;
	public static final boolean DEFAULT_GENERAL_SEND_DEVICE_INFO = false;
	
	//	Identity
	public static final String DEFAULT_IDENTITY_DISPLAY_NAME = "John";
	public static final String DEFAULT_IDENTITY_IMPU = "sip:101@imshello.com";
	public static final String DEFAULT_IDENTITY_IMPI = "101";
	public static final String DEFAULT_IDENTITY_PASSWORD = null;
	
	// Network
	public static final int DEFAULT_NETWORK_REGISTRATION_TIMEOUT = 1700;
	public static final String DEFAULT_NETWORK_REALM = "ims.dlttc.cn";
	public static final boolean DEFAULT_NETWORK_USE_WIFI = true;
	public static final boolean DEFAULT_NETWORK_USE_3G = true;
	public static final boolean DEFAULT_NETWORK_USE_ETHERNET = true;
	public static final String DEFAULT_NETWORK_PCSCF_DISCOVERY = "None";
	public static final String DEFAULT_NETWORK_PCSCF_HOST = "10.160.1.1";
	public static final int DEFAULT_NETWORK_PCSCF_PORT = 5060;
	public static final boolean DEFAULT_NETWORK_USE_SIGCOMP = false;
	public static final String DEFAULT_NETWORK_TRANSPORT = "udp";
	public static final String DEFAULT_NETWORK_IP_VERSION = "ipv4";
	public static final boolean DEFAULT_NETWORK_USE_EARLY_IMS = false;
	
	
	// NAT Traversal
	public static final int DEFAULT_NATT_HACK_AOR_TIMEOUT = 2000;
	public static final boolean DEFAULT_NATT_HACK_AOR = false;
	public static final boolean DEFAULT_NATT_USE_STUN = false;
	public static final boolean DEFAULT_NATT_USE_ICE = false;
	public static final boolean DEFAULT_NATT_STUN_DISCO = true;
	public static final String DEFAULT_NATT_STUN_SERVER = "numb.viagenie.ca";
	public static final int DEFAULT_NATT_STUN_PORT = 3478;
	
	// QoS
    public static final int DEFAULT_QOS_PRECOND_BANDWIDTH_LEVEL = tmedia_bandwidth_level_t.tmedia_bl_unrestricted.swigValue(); // should be String but do not change for backward compatibility
    public static final String DEFAULT_QOS_PRECOND_STRENGTH = tmedia_qos_strength_t.tmedia_qos_strength_none.toString();
    public static final String DEFAULT_QOS_PRECOND_TYPE = tmedia_qos_stype_t.tmedia_qos_stype_none.toString();
    public static final String DEFAULT_QOS_REFRESHER = "none";
    public static final int DEFAULT_QOS_SIP_SESSIONS_TIMEOUT = 600000;
    public static final int DEFAULT_QOS_SIP_CALLS_TIMEOUT = 3600;
    public static final boolean DEFAULT_QOS_USE_SESSION_TIMERS = false;
    public static final boolean DEFAULT_QOS_USE_ZERO_VIDEO_ARTIFACTS = false;
    public static final String DEFAULT_QOS_PREF_VIDEO_SIZE = tmedia_pref_video_size_t.tmedia_pref_video_size_cif.toString();
	
	// Media
    public static final String DEFAULT_MEDIA_PROFILE = tmedia_profile_t.tmedia_profile_default.toString();
	public static final int DEFAULT_MEDIA_CODECS = 
		//tdav_codec_id_t.tdav_codec_id_g729ab.swigValue()|
		//tdav_codec_id_t.tdav_codec_id_pcma.swigValue() |
		//tdav_codec_id_t.tdav_codec_id_pcmu.swigValue() |
		//tdav_codec_id_t.tdav_codec_id_amr_nb_be.swigValue()|
		///tdav_codec_id_t.tdav_codec_id_amr_nb_oa.swigValue()|
		//tdav_codec_id_t.tdav_codec_id_amr_wb_be.swigValue()|
		//tdav_codec_id_t.tdav_codec_id_amr_wb_oa.swigValue()|
		//tdav_codec_id_t.tdav_codec_id_bv16.swigValue()|
		//tdav_codec_id_t.tdav_codec_id_bv32.swigValue()|
		//tdav_codec_id_t.tdav_codec_id_g722.swigValue()|
		//tdav_codec_id_t.tdav_codec_id_gsm.swigValue()|
		//tdav_codec_id_t.tdav_codec_id_ilbc.swigValue()|
		//tdav_codec_id_t.tdav_codec_id_speex_nb.swigValue()|
		//tdav_codec_id_t.tdav_codec_id_speex_uwb.swigValue()|
		//tdav_codec_id_t.tdav_codec_id_speex_wb.swigValue()|
		tdav_codec_id_t.tdav_codec_id_opus.swigValue()|
		tdav_codec_id_t.tdav_codec_id_h264_bp.swigValue() 
		//tdav_codec_id_t.tdav_codec_id_vp8.swigValue() |
		//tdav_codec_id_t.tdav_codec_id_h263p.swigValue() |
		//tdav_codec_id_t.tdav_codec_id_h263.swigValue()
		;
	public static final int DEFAULT_MEDIA_AUDIO_RESAMPLER_QUALITY = 0;
	public static final int DEFAULT_MEDIA_AUDIO_CONSUMER_GAIN = 0; // disabled
	public static final int DEFAULT_MEDIA_AUDIO_PRODUCER_GAIN = 0; // disabled
	public static final float DEFAULT_MEDIA_AUDIO_CONSUMER_ATTENUATION = 1f; // disabled
	public static final float DEFAULT_MEDIA_AUDIO_PRODUCER_ATTENUATION = 1f; // disabled
	
	// Security
	public static final String DEFAULT_SECURITY_IMSAKA_AMF = "0x0000";
	public static final String DEFAULT_SECURITY_IMSAKA_OPID = "0x00000000000000000000000000000000";
	public static final String DEFAULT_SECURITY_SRTP_MODE = tmedia_srtp_mode_t.tmedia_srtp_mode_none.toString();
	public static final String DEFAULT_SECURITY_SRTP_TYPE = tmedia_srtp_type_t.tmedia_srtp_type_sdes.toString();
	public static final String DEFAULT_SECURITY_TLS_PRIVKEY_FILE_PATH = null;
	public static final String DEFAULT_SECURITY_TLS_PUBKEY_FILE_PATH = null;
	public static final String DEFAULT_SECURITY_TLS_CA_FILE_PATH = null;
	public static final boolean DEFAULT_SECURITY_TLS_VERIFY_CERTS = false;
	
	// XCAP
	public static final boolean DEFAULT_XCAP_ENABLED = false;
	public static final String DEFAULT_XCAP_ROOT = "http://imshello.org:8080/services";
	public static final String DEFAULT_XCAP_USERNAME = "sip:johndoe@imshello.org";
	public static final String DEFAULT_XCAP_PASSWORD = null;
	
	// RCS (Rich Communication Suite)
	public static final String DEFAULT_RCS_AVATAR_PATH = "";
	public static final boolean DEFAULT_RCS_USE_BINARY_SM = false; 
	public static final String DEFAULT_RCS_CONF_FACT = "sip:Conference-Factory@imshello.org";
	public static final String DEFAULT_RCS_FREE_TEXT = "Hello world";
	public static final boolean DEFAULT_RCS_HACK_SMS = false;
	public static final boolean DEFAULT_RCS_USE_MSRP_FAILURE = true;
	public static final boolean DEFAULT_RCS_USE_MSRP_SUCCESS = false;
	public static final boolean DEFAULT_RCS_USE_BINARY_SMS = false;
	public static final boolean DEFAULT_RCS_USE_MWI = false;
	public static final boolean DEFAULT_RCS_USE_OMAFDR = false;
	public static final boolean DEFAULT_RCS_USE_PARTIAL_PUB = false;
	public static final boolean DEFAULT_RCS_USE_PRESENCE = false;
	public static final boolean DEFAULT_RCS_USE_RLS = false;
	public static final String DEFAULT_RCS_SMSC = "sip:+331000000000@imshello.org";
	public static final NgnPresenceStatus DEFAULT_RCS_STATUS = NgnPresenceStatus.Online;
	
	//AnswerPhone by wangy 20140417
	public static final String 	DEFAULT_ANS_TIME="5";
	public static final String DEFAULT_ANS_STATUS="OFF";
	
	//USB Camera 
	public static final int DEFAULT_USB_CAMERA_STATUS=1;//ON
	public static final int DEFAULT_CAPTURE_FMT=1;//"CAMERA_CAPTURE_YUYV";
	public static final int DEFAULT_STREAM_FMT=1;//"STREAM_FMT_YUV420P";
	public static final int DEFAULT_LOCAL_VIEW=1;//"CALLBACK_REQUEST_BITMAP";
	public static final int DEFAULT_FRAME_WIDTH=640;
	public static final int DEFAULT_FRAME_HEIGHT=480;
	public static final String DEFAULT_USB_CAMERA_DEVICE_NAME="video0";
	
	public static final int DEFAULT_QOS_VIDEO_FPS=15;
	
	//phone controller by wangy 20160119
	public static final boolean DEFAULT_CONTROLLER_SWITCH=false;
	public static final String DEFAULT_CONTROLLER_SCAN_PORT_SEND="10001";
	public static final String DEFAULT_CONTROLLER_SCAN_PORT_RECV="10002";
	public static final String DEFAULT_CONTROLLER_MESSENGER_PORT_LOCAL_RECV="10003";
	public static final String DEFAULT_CONTROLLER_MESSENGER_PORT_REMOTE_RECV="8000";
	
	//databufferqueue 20160130 by wangy
	public static final int DEFAULT_HI_DECODER_BUFFERQUEUE_SIZE=16;
	public static final int DEFAULT_HI_DECODER_BUFFERSIZE=400000;
	public static final int DEFAULT_USB_CAMERA_BUFFERQUEUE_SIZE=16;
	public static final int DEFAULT_USB_CAMERA_BUFFER_SIZE=400000;
	
}
