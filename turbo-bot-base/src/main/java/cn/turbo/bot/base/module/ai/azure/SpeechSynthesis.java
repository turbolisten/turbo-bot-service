package cn.turbo.bot.base.module.ai.azure;

import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

/**
 * azure 语音
 * 测试一下
 *
 * @author huke
 * @date 2025/5/10 16:32
 */
public class SpeechSynthesis {

    private static final String speechKey = "xxxx";
    private static final String speechRegion = "eastasia";

    @SneakyThrows
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // azure
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
        speechConfig.setSpeechSynthesisLanguage("zh-CN");
        speechConfig.setSpeechSynthesisVoiceName("zh-CN-XiaomoNeural");
        String text = "我家的后面有一个很大的园，相传叫作百草园。";

        // 输出到文件
        speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Audio16Khz64KBitRateMonoMp3);
        SpeechSynthesizer synthesizer = new SpeechSynthesizer(speechConfig);
        SpeechSynthesisResult result = synthesizer.SpeakText(text);

        // 获取原始数据
        byte[] rawSilkData = result.getAudioData();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(rawSilkData);

        // 关键修改3：保存为.aud扩展名
        String finalPath = "D:\\test.mp3";
        try (FileOutputStream fos = new FileOutputStream(finalPath)) {
            fos.write(bos.toByteArray());
        }
    }
}
