package search.tianti.com.wechat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView list;
    private ListAdapter mAdapter;
    private List<RecordBean> mData;
    private View vafer;
    private RecordButton button;
    private RecordButton.AudioRecodFinishListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();
        initView();
        initEvent();
    }

    /**
     * 申请运行时权限
     */
    private void requestPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},0X001);
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        button = findViewById(R.id.record_button);
        mData = new ArrayList<>();
        list = findViewById(R.id.list);
        mAdapter = new ListAdapter(this,mData);
        list.setAdapter(mAdapter);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mListener = new RecordButton.AudioRecodFinishListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                RecordBean bean = new RecordBean(seconds,filePath);
                mData.add(bean);
                mAdapter.notifyDataSetChanged();
                list.setSelection(mData.size()-1);
            }
        };

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //播放动画
                if(vafer != null) {
                    vafer.setBackgroundResource(R.drawable.adj);
                    vafer = null;
                }
                vafer = view.findViewById(R.id.vafer);
                vafer.setBackgroundResource(R.drawable.voice_anim);
                AnimationDrawable animationDrawable = (AnimationDrawable) vafer.getBackground();
                animationDrawable.start();
                //播放音频
                  MediaManager.playSound(mData.get(position).getPath(),new MediaPlayer.OnCompletionListener(){

                      @Override
                      public void onCompletion(MediaPlayer mp) {
                          vafer.setBackgroundResource(R.drawable.adj);
                      }
                  });
            }
        });

        button.setmListener(mListener);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0x001) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }
}
