package cn.turbo.bot.base.common;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.RandomUtil;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxEmojiEnum;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 常量
 *
 * @author huke
 * @date 2025/2/17 19:27
 */
public class CommonConst {

    // TODO 配置 按需可选
    public static final String DEV_EMAIL = "turbohub@163.com";

    /**
     * 错误提示语句
     */
    public static final List<String> ERR_TIPS = Lists.newArrayList("糟糕~量子隧道堵车啦！土拨鼠正在用爪子疏通代码……", "警告！检测到脑洞过载，土拨鼠已挖穿地核！",
                                                                   "您的请求卡在量子叠加态了——既存在又不存在！", "警告！土拨鼠的脑瓜子开始量子沸腾了！",
                                                                   "别慌！只是程序猿在啃代码时噎住了……", "吱~土拨鼠拍拍灰尘：这次挖洞翻车啦！",
                                                                   "发生薛定谔式故障：不点确定就永远不知道错哪儿了！",
                                                                   "系统进入土拨鼠语模式：吱吱=救命，咕=想吃，啪嗒=死机",
                                                                   "外星人劫持了主脑！土拨鼠正用门牙反黑……进度0.0001%",
                                                                   "警告！土拨鼠挖穿地心导致岩浆灌入CPU，灭火中…",
                                                                   "糟糕~系统进入量子鬼打墙模式，本鼠正在奋力捉鬼中…",
                                                                   "量子土拨鼠已启动「咕咕咕」防御：故障是真的，但本鼠假装努力过了");

    /**
     * 随机返回一个错误提示语句
     *
     * @return
     */
    public static String getErrTips() {
        String tip = RandomUtil.randomEle(ERR_TIPS);
        String format = "{}{}\n{}量子修复中, 请稍后重试~";
        return StrFormatter.format(format, WxEmojiEnum.WARN_LIGHT.getValue(), tip, WxEmojiEnum.UFO.getValue());
    }

    public static void main(String[] args) {
        System.out.println(getErrTips());
    }
}
