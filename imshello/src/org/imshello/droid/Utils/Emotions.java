package org.imshello.droid.Utils;

import java.util.HashMap;
import java.util.Map;

import org.imshello.droid.R;

public class Emotions {
	public int[] emotion = new int[] {
			R.drawable.d_zhajipijiu,R.drawable.d_madaochenggong,R.drawable.d_duixiang,
			R.drawable.d_chitangyuan, R.drawable.o_fahongbao,R.drawable.f_shenma,
			R.drawable.w_fuyun, R.drawable.f_v5,R.drawable.d_feizao, R.drawable.o_weiguan,			
			R.drawable.d_hehe, R.drawable.d_xixi, R.drawable.d_haha,
			R.drawable.d_aini, R.drawable.d_yun, R.drawable.d_lei,
			R.drawable.d_chanzui, R.drawable.d_zhuakuang, R.drawable.d_heng,
			R.drawable.d_keai, R.drawable.d_nu, R.drawable.d_han,
			R.drawable.d_haixiu, R.drawable.d_shuijiao, R.drawable.d_qian,
			R.drawable.d_touxiao, R.drawable.d_ku, R.drawable.d_shuai,
			R.drawable.d_chijing, R.drawable.d_bizui, R.drawable.d_bishi,
			R.drawable.d_wabishi, R.drawable.d_huaxin, R.drawable.d_guzhang,
			R.drawable.d_beishang, R.drawable.d_sikao, R.drawable.d_shengbing,
			R.drawable.d_qinqin, R.drawable.d_numa, R.drawable.d_taikaixin,
			R.drawable.d_landelini, R.drawable.d_youhengheng, R.drawable.d_zuohengheng,
			R.drawable.d_xu, R.drawable.d_weiqu, R.drawable.d_tu,
			R.drawable.d_kelian, R.drawable.d_dahaqi, R.drawable.d_zuoguilian,
			R.drawable.d_shiwang,
			R.drawable.d_ding, R.drawable.d_yiwen, R.drawable.d_shudaizi,
			R.drawable.d_kun, R.drawable.d_ganmao, R.drawable.d_baibai,
			R.drawable.d_heixian, R.drawable.d_yinxian, R.drawable.d_fennu,
			R.drawable.d_nanhaier, R.drawable.d_nvhaier, R.drawable.d_aoteman,
			R.drawable.d_zhutou, R.drawable.h_woshou, R.drawable.h_ye,
			R.drawable.h_good, R.drawable.h_ruo, R.drawable.h_buyao,
			R.drawable.h_ok, R.drawable.h_zan, R.drawable.h_lai,
			R.drawable.h_haha, R.drawable.h_quantou, R.drawable.h_zuicha,
			R.drawable.w_yueliang, R.drawable.w_taiyang, R.drawable.w_weifeng,
			R.drawable.w_shachenbao, R.drawable.w_xiayu, R.drawable.w_xue,
			R.drawable.w_xueren, R.drawable.w_luoye, R.drawable.w_xianhua,
			R.drawable.l_xin, R.drawable.l_shangxin, R.drawable.l_aixinchuandi,
			R.drawable.f_hufen, R.drawable.f_meng, R.drawable.f_jiong,
			R.drawable.f_zhi, R.drawable.f_shuai, R.drawable.f_xi,
			R.drawable.o_weibo, R.drawable.o_wennuanmaozi, R.drawable.o_shoutao,
			R.drawable.o_lvsidai, R.drawable.o_hongsidai, R.drawable.o_dangao,
			R.drawable.o_kafei, R.drawable.o_xigua, R.drawable.o_bingun,
			R.drawable.o_ganbei, R.drawable.o_lazhu, R.drawable.o_qiche,
			R.drawable.o_feiji, R.drawable.o_zixingche, R.drawable.o_liwu,
			R.drawable.o_zhaoxiangji, R.drawable.o_shouji, R.drawable.o_fengshan,
			R.drawable.o_huatong, R.drawable.o_zhong, R.drawable.o_zuqiu,
			R.drawable.o_dianying, R.drawable.o_yinyue, R.drawable.o_shixi,
			R.drawable.d_lvxing, R.drawable.d_tuzi, R.drawable.d_xiongmao,
			R.drawable.f_geili};	

	public Map<String, Integer> getEmotionsList_text() {

		int i = 0;	
		Map<String, Integer> emotions_map1 = new HashMap<String, Integer>();
		emotions_map1.put("炸鸡和啤酒", emotion[i++]);
		emotions_map1.put("马到成功", emotion[i++]);
		emotions_map1.put("马上有对象", emotion[i++]);
		emotions_map1.put("吃汤圆", emotion[i++]);
		emotions_map1.put("让红包飞", emotion[i++]);
		emotions_map1.put("神马", emotion[i++]);
		emotions_map1.put("浮云", emotion[i++]);
		emotions_map1.put("威武", emotion[i++]);
		emotions_map1.put("肥皂", emotion[i++]);
		emotions_map1.put("围观", emotion[i++]);
		emotions_map1.put("呵呵", emotion[i++]);
		emotions_map1.put("嘻嘻", emotion[i++]);
		emotions_map1.put("哈哈", emotion[i++]);
		emotions_map1.put("爱你", emotion[i++]);
		emotions_map1.put("晕", emotion[i++]);
		emotions_map1.put("泪", emotion[i++]);
		emotions_map1.put("馋嘴", emotion[i++]);
		emotions_map1.put("抓狂", emotion[i++]);
		emotions_map1.put("哼", emotion[i++]);
		emotions_map1.put("可爱", emotion[i++]);
		emotions_map1.put("怒", emotion[i++]);
		emotions_map1.put("汗", emotion[i++]);
		emotions_map1.put("害羞", emotion[i++]);
		emotions_map1.put("睡觉", emotion[i++]);
		emotions_map1.put("钱", emotion[i++]);
		emotions_map1.put("偷笑", emotion[i++]);
		emotions_map1.put("酷", emotion[i++]);
		emotions_map1.put("衰", emotion[i++]);
		emotions_map1.put("吃惊", emotion[i++]);
		emotions_map1.put("闭嘴", emotion[i++]);
		emotions_map1.put("鄙视", emotion[i++]);
		emotions_map1.put("挖鼻屎", emotion[i++]);
		emotions_map1.put("花心", emotion[i++]);
		emotions_map1.put("鼓掌", emotion[i++]);
		emotions_map1.put("悲伤", emotion[i++]);
		emotions_map1.put("思考", emotion[i++]);
		emotions_map1.put("生病", emotion[i++]);
		emotions_map1.put("亲亲", emotion[i++]);
		emotions_map1.put("怒骂", emotion[i++]);
		emotions_map1.put("太开心", emotion[i++]);
		emotions_map1.put("懒得理你", emotion[i++]);
		emotions_map1.put("右哼哼", emotion[i++]);
		emotions_map1.put("左哼哼", emotion[i++]);
		emotions_map1.put("嘘", emotion[i++]);
		emotions_map1.put("委屈", emotion[i++]);
		emotions_map1.put("吐", emotion[i++]);
		emotions_map1.put("可怜", emotion[i++]);
		emotions_map1.put("打哈欠", emotion[i++]);
		emotions_map1.put("ali做鬼脸", emotion[i++]);
		emotions_map1.put("失望", emotion[i++]);
		emotions_map1.put("顶", emotion[i++]);
		emotions_map1.put("疑问", emotion[i++]);
		emotions_map1.put("书呆子", emotion[i++]);
		emotions_map1.put("困", emotion[i++]);
		emotions_map1.put("感冒", emotion[i++]);
		emotions_map1.put("拜拜", emotion[i++]);
		emotions_map1.put("黑线", emotion[i++]);
		emotions_map1.put("阴险", emotion[i++]);
		emotions_map1.put("愤怒", emotion[i++]);
		emotions_map1.put("男孩", emotion[i++]);
		emotions_map1.put("女孩", emotion[i++]);
		emotions_map1.put("奥特曼", emotion[i++]);
		emotions_map1.put("猪头", emotion[i++]);
		emotions_map1.put("握手", emotion[i++]);
		emotions_map1.put("耶", emotion[i++]);
		emotions_map1.put("good", emotion[i++]);
		emotions_map1.put("弱", emotion[i++]);		
		emotions_map1.put("不要", emotion[i++]);
		emotions_map1.put("ok", emotion[i++]);
		emotions_map1.put("赞", emotion[i++]);
		emotions_map1.put("来", emotion[i++]);
		emotions_map1.put("BOBO爱你", emotion[i++]);
		emotions_map1.put("拳头", emotion[i++]);
		emotions_map1.put("最差", emotion[i++]);
		emotions_map1.put("月亮", emotion[i++]);
		emotions_map1.put("太阳", emotion[i++]);		
		emotions_map1.put("微风", emotion[i++]);
		emotions_map1.put("沙尘暴", emotion[i++]);
		emotions_map1.put("下雨", emotion[i++]);
		emotions_map1.put("雪", emotion[i++]);
		emotions_map1.put("雪人", emotion[i++]);
		emotions_map1.put("落叶", emotion[i++]);
		emotions_map1.put("鲜花", emotion[i++]);
		emotions_map1.put("心", emotion[i++]);
		emotions_map1.put("伤心", emotion[i++]);
		emotions_map1.put("爱心传递", emotion[i++]);
		emotions_map1.put("互粉", emotion[i++]);
		emotions_map1.put("萌", emotion[i++]);
		emotions_map1.put("囧", emotion[i++]);
		emotions_map1.put("织", emotion[i++]);
		emotions_map1.put("帅", emotion[i++]);
		emotions_map1.put("喜", emotion[i++]);
		emotions_map1.put("围脖", emotion[i++]);
		emotions_map1.put("温暖帽子", emotion[i++]);
		emotions_map1.put("手套", emotion[i++]);
		emotions_map1.put("绿丝带", emotion[i++]);
		emotions_map1.put("红丝带", emotion[i++]);
		emotions_map1.put("蛋糕", emotion[i++]);
		emotions_map1.put("咖啡", emotion[i++]);
		emotions_map1.put("西瓜", emotion[i++]);
		emotions_map1.put("冰棍", emotion[i++]);
		emotions_map1.put("干杯", emotion[i++]);
		emotions_map1.put("蜡烛", emotion[i++]);
		emotions_map1.put("汽车", emotion[i++]);
		emotions_map1.put("飞机", emotion[i++]);
		emotions_map1.put("自行车", emotion[i++]);
		emotions_map1.put("礼物", emotion[i++]);
		emotions_map1.put("照相机", emotion[i++]);
		emotions_map1.put("手机", emotion[i++]);
		emotions_map1.put("风扇", emotion[i++]);
		emotions_map1.put("话筒", emotion[i++]);
		emotions_map1.put("钟", emotion[i++]);
		emotions_map1.put("足球", emotion[i++]);
		emotions_map1.put("电影", emotion[i++]);
		emotions_map1.put("音乐", emotion[i++]);
		emotions_map1.put("实习", emotion[i++]);
		emotions_map1.put("旅行", emotion[i++]);
		emotions_map1.put("兔子", emotion[i++]);
		emotions_map1.put("熊猫", emotion[i++]);
		emotions_map1.put("给力", emotion[i++]);
		return emotions_map1;

	}

	public Map<String, String> getEmotionsList_image() {
		int i = 0;
		Map<String, String> emotions_map2 = new HashMap<String, String>();
		emotions_map2.put(emotion[i++] + "", "炸鸡和啤酒");
		emotions_map2.put(emotion[i++] + "", "马到成功");
		emotions_map2.put(emotion[i++] + "", "马上有对象");
		emotions_map2.put(emotion[i++] + "", "吃汤圆");
		emotions_map2.put(emotion[i++] + "", "让红包飞");
		emotions_map2.put(emotion[i++] + "", "神马");
		emotions_map2.put(emotion[i++] + "", "浮云");
		emotions_map2.put(emotion[i++] + "", "威武");
		emotions_map2.put(emotion[i++] + "", "肥皂");
		emotions_map2.put(emotion[i++] + "", "围观");
		emotions_map2.put(emotion[i++] + "", "呵呵");
		emotions_map2.put(emotion[i++] + "", "嘻嘻");
		emotions_map2.put(emotion[i++] + "", "哈哈");
		emotions_map2.put(emotion[i++] + "", "爱你");
		emotions_map2.put(emotion[i++] + "", "晕");
		emotions_map2.put(emotion[i++] + "", "泪");
		emotions_map2.put(emotion[i++] + "", "馋嘴");
		emotions_map2.put(emotion[i++] + "", "抓狂");
		emotions_map2.put(emotion[i++] + "", "哼");
		emotions_map2.put(emotion[i++] + "", "可爱");
		emotions_map2.put(emotion[i++] + "", "怒");
		emotions_map2.put(emotion[i++] + "", "汗");
		emotions_map2.put(emotion[i++] + "", "害羞");
		emotions_map2.put(emotion[i++] + "", "睡觉");
		emotions_map2.put(emotion[i++] + "", "钱");
		emotions_map2.put(emotion[i++] + "", "偷笑");
		emotions_map2.put(emotion[i++] + "", "酷");
		emotions_map2.put(emotion[i++] + "", "衰");
		emotions_map2.put(emotion[i++] + "", "吃惊");
		emotions_map2.put(emotion[i++] + "", "闭嘴");
		emotions_map2.put(emotion[i++] + "", "鄙视");
		emotions_map2.put(emotion[i++] + "", "挖鼻屎");
		emotions_map2.put(emotion[i++] + "", "花心");
		emotions_map2.put(emotion[i++] + "", "鼓掌");
		emotions_map2.put(emotion[i++] + "", "悲伤");
		emotions_map2.put(emotion[i++] + "", "思考");
		emotions_map2.put(emotion[i++] + "", "生病");
		emotions_map2.put(emotion[i++] + "", "亲亲");
		emotions_map2.put(emotion[i++] + "", "怒骂");
		emotions_map2.put(emotion[i++] + "", "太开心");
		emotions_map2.put(emotion[i++] + "", "懒得理你");
		emotions_map2.put(emotion[i++] + "", "右哼哼");
		emotions_map2.put(emotion[i++] + "", "左哼哼");
		emotions_map2.put(emotion[i++] + "", "嘘");

		emotions_map2.put(emotion[i++] + "", "委屈");
		emotions_map2.put(emotion[i++] + "", "吐");
		emotions_map2.put(emotion[i++] + "", "可怜");
		emotions_map2.put(emotion[i++] + "", "打哈欠");
		emotions_map2.put(emotion[i++] + "", "ali做鬼脸");
		emotions_map2.put(emotion[i++] + "", "失望");
		emotions_map2.put(emotion[i++] + "", "顶");
		emotions_map2.put(emotion[i++] + "", "疑问");
		emotions_map2.put(emotion[i++] + "", "书呆子");
		emotions_map2.put(emotion[i++] + "", "困");
		emotions_map2.put(emotion[i++] + "", "感冒");
		emotions_map2.put(emotion[i++] + "", "拜拜");
		emotions_map2.put(emotion[i++] + "", "黑线");
		emotions_map2.put(emotion[i++] + "", "阴险");
		emotions_map2.put(emotion[i++] + "", "愤怒");
		emotions_map2.put(emotion[i++] + "", "男孩");
		emotions_map2.put(emotion[i++] + "", "女孩");
		emotions_map2.put(emotion[i++] + "", "奥特曼");
		emotions_map2.put(emotion[i++] + "", "猪头");
		emotions_map2.put(emotion[i++] + "", "握手");
		emotions_map2.put(emotion[i++] + "", "耶");
		emotions_map2.put(emotion[i++] + "", "good");
		emotions_map2.put(emotion[i++] + "", "弱");		
		emotions_map2.put(emotion[i++] + "", "不要");
		emotions_map2.put(emotion[i++] + "", "ok");
		emotions_map2.put(emotion[i++] + "", "赞");
		emotions_map2.put(emotion[i++] + "", "来");
		emotions_map2.put(emotion[i++] + "", "BOBO爱你");
		emotions_map2.put(emotion[i++] + "", "拳头");
		emotions_map2.put(emotion[i++] + "", "最差");
		emotions_map2.put(emotion[i++] + "", "月亮");
		emotions_map2.put(emotion[i++] + "", "太阳");
		emotions_map2.put(emotion[i++] + "", "微风");
		emotions_map2.put(emotion[i++] + "", "沙尘暴");
		emotions_map2.put(emotion[i++] + "", "下雨");
		emotions_map2.put(emotion[i++] + "", "雪");
		emotions_map2.put(emotion[i++] + "", "雪人");
		emotions_map2.put(emotion[i++] + "", "落叶");
		emotions_map2.put(emotion[i++] + "", "鲜花");
		emotions_map2.put(emotion[i++] + "", "心");
		emotions_map2.put(emotion[i++] + "", "伤心");
		emotions_map2.put(emotion[i++] + "", "爱心传递");
		emotions_map2.put(emotion[i++] + "", "互粉");
		emotions_map2.put(emotion[i++] + "", "萌");
		emotions_map2.put(emotion[i++] + "", "囧");
		emotions_map2.put(emotion[i++] + "", "织");
		emotions_map2.put(emotion[i++] + "", "帅");
		emotions_map2.put(emotion[i++] + "", "喜");
		emotions_map2.put(emotion[i++] + "", "围脖");
		emotions_map2.put(emotion[i++] + "", "温暖帽子");
		emotions_map2.put(emotion[i++] + "", "手套");
		emotions_map2.put(emotion[i++] + "", "绿丝带");
		emotions_map2.put(emotion[i++] + "", "红丝带");
		emotions_map2.put(emotion[i++] + "", "蛋糕");
		emotions_map2.put(emotion[i++] + "", "咖啡");
		emotions_map2.put(emotion[i++] + "", "西瓜");
		emotions_map2.put(emotion[i++] + "", "冰棍");
		emotions_map2.put(emotion[i++] + "", "干杯");
		emotions_map2.put(emotion[i++] + "", "蜡烛");
		emotions_map2.put(emotion[i++] + "", "汽车");
		emotions_map2.put(emotion[i++] + "", "飞机");
		emotions_map2.put(emotion[i++] + "", "自行车");
		emotions_map2.put(emotion[i++] + "", "礼物");
		emotions_map2.put(emotion[i++] + "", "照相机");
		emotions_map2.put(emotion[i++] + "", "手机");
		emotions_map2.put(emotion[i++] + "", "风扇");
		emotions_map2.put(emotion[i++] + "", "话筒");
		emotions_map2.put(emotion[i++] + "", "钟");
		emotions_map2.put(emotion[i++] + "", "足球");
		emotions_map2.put(emotion[i++] + "", "电影");
		emotions_map2.put(emotion[i++] + "", "音乐");
		emotions_map2.put(emotion[i++] + "", "实习");
		emotions_map2.put(emotion[i++] + "", "旅行");
		emotions_map2.put(emotion[i++] + "", "兔子");
		emotions_map2.put(emotion[i++] + "", "熊猫");
		emotions_map2.put(emotion[i++] + "", "给力");
		return emotions_map2;
	}
}
