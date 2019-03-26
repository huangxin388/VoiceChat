package search.tianti.com.wechat;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends BaseAdapter {

    private Context mContext;
    private List<RecordBean> mData;
    private int maxWidth;
    private int minWidth;

    public ListAdapter(Context mContext, List<RecordBean> mData) {
        this.mContext = mContext;
        this.mData = mData;
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        maxWidth = (int)(metrics.widthPixels * 0.7f);
        minWidth = (int)(metrics.widthPixels * 0.15f);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.record_item,parent,false);
            holder = new ViewHolder();
            holder.length = convertView.findViewById(R.id.fl_container);
            holder.time = convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.time.setText(Math.round(mData.get(position).getTime()) + "\"");
        ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
        lp.width = (int) (minWidth + mData.get(position).getTime()/60f*maxWidth);
//        holder.length.setLayoutParams(lp);
        return convertView;
    }

    class ViewHolder {
        TextView time;
        FrameLayout length;
    }
}
