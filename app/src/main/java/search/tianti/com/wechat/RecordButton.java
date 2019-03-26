package search.tianti.com.wechat;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class RecordButton extends AppCompatButton implements AudioManager.AudioPrepareListener{

    //按钮捕捉到的手势状态
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    private int mCurrentState = STATE_NORMAL;
    //超出按钮多远之后为取消录音范围
    private static final int CANCEL_Y_DIATANCE = 50;
    //不同状态时为handler发送不同的信息
    private static final int MSG_PREPARED = 0x110;
    private static final int MSG_VOICE_CHANGE = 0x111;
    private static final int MSG_DISMISSED = 0x112;
    //记录录音时间
    private float mTime = 0;
    //是否触发longClick
    private  boolean isReady = false;
    //是否正在录音
    private boolean isRecording = false;
    //提示框管理类
    private DialogManager mDialogManager;
    //音频管理类
    private AudioManager mAudioManager;
    private static final String TAG = "RecordButton";

    private AudioRecodFinishListener mListener;

    public interface AudioRecodFinishListener {//录制结束回调接口
        public void onFinish(float seconds,String filePath);
    }

    public void setmListener(AudioRecodFinishListener mListener) {
        this.mListener = mListener;
    }

    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                while (isRecording) {
                    Thread.sleep(100);
//                    Log.d(TAG, "run: 我在子线程中");
//                    Log.d(TAG, "run: mTime = " + mTime);
                    mTime += 0.1f;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PREPARED:
                    isRecording = true;
                    mDialogManager.showRecordDialog();
                    mDialogManager.recording();
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGE:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DISMISSED:
                    mDialogManager.dismissDialog();
                    break;
            }
        }
    };


    public RecordButton(Context context) {
        this(context,null);
    }

    public RecordButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDialogManager = new DialogManager(context);
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wechat";
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioPrepareListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isReady = true;
                mAudioManager.prepareAudio();
                return true;
            }
        });
    }


    @Override
    public void prepared() {
        mHandler.sendEmptyMessage(MSG_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指按下时，显示最原始的dialog
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isRecording) {
                    //根据X，Y坐标判断是否取消录音
                    if(wangToCancel(x,y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                if(!isReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                //没有prepare完毕，UP就触发了或者录音时间小于0.6s
                if(!isRecording || mTime < 0.6f) {
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DISMISSED,1300);
                } else if(mCurrentState == STATE_RECORDING) {
                    mDialogManager.dismissDialog();
                    mAudioManager.release();
                    if(mListener != null) {
                        mListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                    }
                } else if(mCurrentState == STATE_WANT_TO_CANCEL) {
                    mDialogManager.dismissDialog();
                    mAudioManager.cancel();
                }
                reset();
                break;
        }
        return super.onTouchEvent( event );
    }

    /**
     * 重置状态
     */
    private void reset() {
        isRecording = false;
        changeState(STATE_NORMAL);
        isReady = false;
        mTime = 0;
    }
    //根据手指距按钮的距离判断用户是否想取消录制
    private boolean wangToCancel(int x, int y) {
        if(x < 0 || x > getWidth()) {
            return true;
        }

        if(y < -CANCEL_Y_DIATANCE || y > getHeight() + CANCEL_Y_DIATANCE) {
            return true;
        }
        return false;
    }
    /**
     *  改变按钮及提示框状态
     */
    private void changeState(int state) {
        if(mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_record_normal);
                    setText(R.string.btn_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.btn_recording);
                    if(isRecording) {
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.btn_want_to_cancel);
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }

}
