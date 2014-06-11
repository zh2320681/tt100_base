package cn.shrek.base.download.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.shrek.base.download.bo.DLTask;
import cn.shrek.base.util.BaseUtil;
import cn.tt100.base.R;

public class DefalutDownLoadedAdapter extends BaseAdapter {

	public List<DLTask> allTasks;
	private Context mContext;
	private LayoutInflater mLayoutInflater;

	public DefalutDownLoadedAdapter(Context mContext, List<DLTask> mList) {
		this.mContext = mContext;
		this.allTasks = mList;
		this.mLayoutInflater = LayoutInflater.from(mContext);
	}

	public List<DLTask> getAllTasks() {
		return this.allTasks;
	}

	public int getCount() {
		return this.allTasks.size();
	}

	public DLTask getItem(int position) {
		return allTasks.get(position);
	}

	public long getItemId(int id) {
		return id;
	}

	public View getView(int position, View convertView, ViewGroup parent){
	    final DLTask mDLTask = this.allTasks.get(position);
	    
	    DownloadShow mDownloadShow;
	    if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.downloaded_adapter, null);
			mDownloadShow = new DownloadShow();
			mDownloadShow.icon = (ImageView)convertView.findViewById(R.id.downloaded_adapter_icon);
			mDownloadShow.name = (TextView)convertView.findViewById(R.id.downloaded_adapter_name);
			mDownloadShow.size = (TextView)convertView.findViewById(R.id.downloaded_adapter_size);
			mDownloadShow.btn = (Button)convertView.findViewById(R.id.downloaded_adapter_btn);
			mDownloadShow.btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					BaseUtil.openFile(mDLTask.getSavePath(), mContext);
				}
			});
			convertView.setTag(mDownloadShow);
		}else{
			mDownloadShow = (DownloadShow)convertView.getTag();
		}
		mDownloadShow.icon.setImageResource(BaseUtil.getIcon(mDLTask.getSavePath()));
		mDownloadShow.name.setText(mDLTask.fileName);
		mDownloadShow.size.setText(BaseUtil.getFileSize(mDLTask.totalSize));
	
		return convertView;
	  }

	public void setAllTasks(List<DLTask> paramList) {
		this.allTasks = paramList;
	}

	class DownloadShow {
		public Button btn;
		public ImageView icon;
		public TextView name, size;

		DownloadShow() {
		}
	}

}
