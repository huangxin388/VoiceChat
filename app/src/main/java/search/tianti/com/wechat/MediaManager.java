package search.tianti.com.wechat;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 此类用于管理音频的播放
 */
public class MediaManager {
    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;

    public static void playSound(String filePath, MediaPlayer.OnCompletionListener listener) {
        if(mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.setOnCompletionListener(listener);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pause() {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public static void resume() {
        if(mMediaPlayer != null && isPause) {
            isPause = false;
            mMediaPlayer.start();
        }
    }

    public static void release() {
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
