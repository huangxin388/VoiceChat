package search.tianti.com.wechat;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AudioManager {
    private Context mContext;
    private static AudioManager instance;
    private MediaRecorder mRecoder;
    private String mDir;//音频保存目录
    private String mCurrentFilePath;//保存音频的临时路径
    private static final String TAG = "AudioManager";

    private AudioPrepareListener mListener;
    private boolean isPrepared = false;

    private AudioManager(String dir) {
        this.mDir = dir;
    }

    public static AudioManager getInstance(String dir) {
        if(instance == null) {
            synchronized (AudioManager.class) {
                if(instance == null) {
                    instance = new AudioManager(dir);
                }
            }
        }
        return instance;
    }

    public interface AudioPrepareListener {
        public void prepared();
    }

    public void setOnAudioPrepareListener(AudioPrepareListener listener) {
        this.mListener = listener;
    }

    public void prepareAudio() {
        isPrepared = false;
        File dir = new File(mDir);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = generateFileName();
        File file = new File(dir,fileName);
        mCurrentFilePath = file.getAbsolutePath();
        mRecoder = new MediaRecorder();
        //设置输出文件路径
        mRecoder.setOutputFile(mCurrentFilePath);
        //设置输出源为麦克风
        mRecoder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置音频格式
        mRecoder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        //设置编码格式
        mRecoder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecoder.prepare();
            mRecoder.start();
            isPrepared = true;
            if(mListener != null) {
                mListener.prepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateFileName() {
        String name = UUID.randomUUID().toString() + ".amr";
        return name;
    }


    /**
     * 获取音量级别
     * @param maxLevel
     * @return
     */
    public int getVoiceLevel(int maxLevel) {
        if(isPrepared) {
            int level = 1;
            try {
                if(mRecoder != null) {
                    //MediaRecorder能获取到的音量范围是1-32768
                    level = (int)(maxLevel * mRecoder.getMaxAmplitude() / 32768.0) + 1;
                } else {
                    Log.d(TAG, "getVoiceLevel: 获取音量的方法是在子线程中，因此可能MediaPlayer已经被release了，还在获取音量信息");
                }

            } catch (IllegalStateException e) {
                Log.d(TAG, "getVoiceLevel: 出现问题了，我跳过还不行吗");
                e.printStackTrace();
            }
            return level;
        }
        return 1;
    }

    /**
     * 发送录音，释放资源
     */
    public void release() {

        if(mRecoder != null) {
            try {
                mRecoder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                mRecoder = null;
                mRecoder = new MediaRecorder();
            }
            mRecoder.release();
            mRecoder = null;
        }
    }

    /**
     * 取消录音释放资源
     */
    public void cancel() {
        release();
        if(mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
        }
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }
}
