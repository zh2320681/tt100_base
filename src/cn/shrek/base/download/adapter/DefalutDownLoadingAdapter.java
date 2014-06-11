package cn.shrek.base.download.adapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.shrek.base.download.DLConstant;
import cn.shrek.base.download.Downloader;
import cn.shrek.base.download.bo.DLTask;
import cn.shrek.base.download.bo.DLThreadTask;
import cn.shrek.base.download.db.DLDatabaseHelper;
import cn.tt100.base.R;

public class DefalutDownLoadingAdapter extends BaseAdapter {

	private Object lockObj;
	private Context mContext;
	private DLDatabaseHelper mHelper;
	private LayoutInflater mLayoutInflater;
	private Map<DLTask, Date> taskTimeRecs;

	public DefalutDownLoadingAdapter(Context context,
			DLDatabaseHelper paramDLDatabaseHelper) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mHelper = paramDLDatabaseHelper;

		taskTimeRecs = new HashMap<DLTask, Date>();
		lockObj = new Object();
	}

	public int getCount() {
		return Downloader.allTasks.size();
	}

	public Map.Entry<DLTask, Set<DLThreadTask>> getItem(int paramInt) {
		int index = 0;
		for (Map.Entry<DLTask, Set<DLThreadTask>> entry : Downloader.allTasks
				.entrySet()) {
			if (paramInt == index) {
				return entry;
			}
			index++;
		}
		return null;
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Map.Entry<DLTask, Set<DLThreadTask>> entry = getItem(position);

		final DLTask mDLTask = entry.getKey();
		final Set<DLThreadTask> dtTasks = entry.getValue();

		DownloadShow mDownloadShow = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.downloading_adapter,
					null);
			mDownloadShow = new DownloadShow();
			mDownloadShow.states = (ImageView) convertView
					.findViewById(R.id.downloading_adapter_states);
			mDownloadShow.name = (TextView) convertView
					.findViewById(R.id.downloading_adapter_name);
			mDownloadShow.info = (TextView) convertView
					.findViewById(R.id.downloading_adapter_info);
			mDownloadShow.speed = (TextView) convertView
					.findViewById(R.id.downloading_adapter_speed);
			mDownloadShow.mProgress = (ProgressBar) convertView
					.findViewById(R.id.downloading_adapter_pre);
			mDownloadShow.btn = (Button) convertView
					.findViewById(R.id.downloading_adapter_btn);
			mDownloadShow.prolayout = convertView
					.findViewById(R.id.downloading_adapter_preLayout);

			convertView.setTag(mDownloadShow);
		} else {
			mDownloadShow = (DownloadShow)convertView.getTag();
		}
		mDownloadShow.btn.setOnClickListener(new OnClickListener() {

			@Override
			public synchronized void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(mContext, "暂未开放该功能!",
				// Toast.LENGTH_LONG).show();
				// return;
				v.setEnabled(false);
				switch (mDLTask.states.get()) {
				case DLConstant.TASK_WAIT:

					break;
				case DLConstant.TASK_RUN:
					// 暂停任务
					
					break;
				case DLConstant.TASK_ERROR:
					// 多线程异常检测
					
					break;
				case DLConstant.TASK_PAUSE:
					
					break;
				}
				v.setEnabled(true);
			}
		});

		String name = mDLTask.fileName;
		if (name == null || name.equals("")) {
			name = mDLTask.downLoadUrl.substring(
					mDLTask.downLoadUrl.lastIndexOf('/') + 1);
		}
		mDownloadShow.name.setText(name);
		mDownloadShow.info.setText(mDLTask.errorMessage);
		mDownloadShow.mProgress.setMax((int) mDLTask.totalSize);
		
		//获取下载总数
		int sumSize = 0;
		for(DLThreadTask task : dtTasks){
			sumSize += task.hasDownloadLength;
		}
		mDownloadShow.mProgress.setProgress(sumSize);

		switch (mDLTask.states.get()) {
		case DLConstant.TASK_WAIT:
			mDownloadShow.states.setImageResource(R.drawable.downloaing_wait);
			mDownloadShow.btn.setText("等待");
			// mDownloadShow.mProgress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.barcolor));
			mDownloadShow.speed.setVisibility(View.GONE);
			mDownloadShow.prolayout.setVisibility(View.VISIBLE);
			mDownloadShow.info.setText("等待下载中..");
			break;
		case DLConstant.TASK_RUN:
			mDownloadShow.states.setImageResource(R.drawable.downloaing_run);
			mDownloadShow.btn.setText("暂停");
			// mDownloadShow.mProgress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.barcolor));
			mDownloadShow.speed.setVisibility(View.VISIBLE);
			mDownloadShow.prolayout.setVisibility(View.VISIBLE);
			break;
		case DLConstant.TASK_ERROR:
			mDownloadShow.states.setImageResource(R.drawable.downloaing_error);
			mDownloadShow.btn.setText("再试");
			mDownloadShow.speed.setVisibility(View.GONE);
			mDownloadShow.info.setText(mDLTask.errorMessage);
			mDownloadShow.prolayout.setVisibility(View.GONE);
			break;
		case DLConstant.TASK_PAUSE:
			mDownloadShow.states.setImageResource(R.drawable.downloaing_pause);
			mDownloadShow.btn.setText("开始");
			// mDownloadShow.mProgress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.barcolor_pause));
			mDownloadShow.speed.setVisibility(View.VISIBLE);
			mDownloadShow.prolayout.setVisibility(View.VISIBLE);
			mDownloadShow.speed.setText("暂停下载");
			break;
		}

		return convertView;
	}

	class DownloadShow {
		public Button btn;
		public TextView info, name, speed;
		public ProgressBar mProgress;
		public View prolayout;
		public ImageView states;

		DownloadShow() {

		}
	}

}
