package search.tianti.com.wechat;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogManager {
    private Dialog mDialog;
    private ImageView ivIcon;
    private ImageView ivVoice;
    private TextView tvLabel;
    private Context mContext;
    private static final String TAG = "DialogManager";

    public DialogManager(Context mContext) {
        this.mContext = mContext;
        Log.d(TAG, "DialogManager: dialog构造");
    }

    public void showRecordDialog() {
        Log.d(TAG, "showRecordDialog: 展示dialog");
        mDialog = new Dialog(mContext,R.style.style_dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_dialog,null);
        mDialog.setContentView(view);

        ivIcon = view.findViewById(R.id.iv_dialog_icon);
        ivVoice = view.findViewById(R.id.iv_dialog_voice);
        tvLabel = view.findViewById(R.id.tv_dialog_label);

        mDialog.show();
    }

    public void recording() {
        Log.d(TAG, "recording: 正在录音");
        if(mDialog != null && mDialog.isShowing()) {
            ivIcon.setVisibility(View.VISIBLE);
            ivVoice.setVisibility(View.VISIBLE);
            tvLabel.setVisibility(View.VISIBLE);
            ivIcon.setImageResource(R.drawable.recorder);
            tvLabel.setText("手指上滑，取消发送");
        }
    }

    public void tooShort() {
        Log.d(TAG, "tooShort: ");
        if(mDialog != null && mDialog.isShowing()) {
            ivIcon.setVisibility(View.VISIBLE);
            ivVoice.setVisibility(View.GONE);
            Log.d(TAG, "tooShort: 调用代码，使音量隐藏");
            tvLabel.setVisibility(View.VISIBLE);
            ivIcon.setImageResource(R.drawable.voice_to_short);
            tvLabel.setText("录音时间过短");
        }
    }

    public void wantToCancel() {
        Log.d(TAG, "wantToCancel: ");
        if(mDialog != null && mDialog.isShowing()) {
            ivIcon.setVisibility(View.VISIBLE);
            ivVoice.setVisibility(View.GONE);
            Log.d(TAG, "wantToCancel: 调用代码使音量隐藏");
            tvLabel.setVisibility(View.VISIBLE);
            ivIcon.setImageResource(R.drawable.cancel);
            tvLabel.setText("松开手指，取消发送");
        }
    }

    public void dismissDialog() {
        if(mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void updateVoiceLevel(int level) {
        if(mDialog != null && mDialog.isShowing()) {
            int resId = mContext.getResources().getIdentifier("v"+level,"drawable",mContext.getPackageName());
            ivVoice.setImageResource(resId);
        }
    }

}
