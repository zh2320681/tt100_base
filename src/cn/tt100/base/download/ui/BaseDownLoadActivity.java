package cn.tt100.base.download.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.tt100.base.R;
import cn.tt100.base.download.DLConstant;
import cn.tt100.base.download.Downloader;
import cn.tt100.base.download.adapter.DefalutDownLoadedAdapter;
import cn.tt100.base.download.adapter.DefalutDownLoadingAdapter;
import cn.tt100.base.download.bo.DLTask;
import cn.tt100.base.download.bo.DLThreadTask;
import cn.tt100.base.download.db.DLDatabaseHelper;

public class BaseDownLoadActivity extends Activity {

	private static final int MSG_SERVICE_START = 17;
	private Button backBtn, clearBtn;

	private AlertDialog dialog;
	private ListView downloaded_content;
	private ImageView downloaded_expand, downloaded_no, downloading_expand;
	private RelativeLayout downloaded_layout;

	private TextView downloaded_text;
	private ListView downloading_content;

	private RelativeLayout downloading_layout;
	private ImageView downloading_no;
	private TextView downloading_text;
	private Button flashBtn;
	private DefalutDownLoadedAdapter loadedAdapter;
	private DefalutDownLoadingAdapter loadingAdapter;
	private DLDatabaseHelper mOperator;

	
	public BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (DLConstant.BROADCAST_TASK.equals(action)){
		      String str = intent.getStringExtra(DLConstant.DL_TASK_MSG);
		      DLTask mDLTask = (DLTask)intent.getSerializableExtra(DLConstant.DL_TASK_OBJ);
		      
		      mDLTask.errorMessage = str;
		      if (mDLTask.states.get() == DLConstant.TASK_SUCESS){
		        loadedAdapter.setAllTasks(mOperator.getDLOverTasks());
		        loadedAdapter.notifyDataSetChanged();
		      }
		      loadingAdapter.notifyDataSetChanged();
		    }
		}
	};

	private OnClickListener myClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == clearBtn){
				
			}else if(v == flashBtn){
				//刷新
				flashBtn.setEnabled(false);
				
				Toast.makeText(BaseDownLoadActivity.this, "刷新完成!", Toast.LENGTH_SHORT).show();
				flashBtn.setEnabled(true);
			}else if(v == backBtn){
				finish();
			}
		}
	};



	private void defalutInit() {
		downloading_text = (TextView)findViewById(R.id.downloding_text);
		downloaded_text = (TextView)findViewById(R.id.downloded_text);
		downloading_no = (ImageView)findViewById(R.id.downloding_no_info);
		downloaded_no = (ImageView)findViewById(R.id.downloded_no_info);
		downloading_expand = (ImageView)findViewById(R.id.downloding_expand);
		downloaded_expand = (ImageView)findViewById(R.id.downloded_expand);
		downloading_layout = (RelativeLayout)findViewById(R.id.downloding_layout);
		downloaded_layout = (RelativeLayout)findViewById(R.id.downloded_layout);
		downloading_content = (ListView)findViewById(R.id.downloding_content);
		downloaded_content = (ListView)findViewById(R.id.downloded_content);
		clearBtn = (Button)findViewById(R.id.downloding_clear);
		flashBtn = (Button)findViewById(R.id.downloding_flash);
		backBtn = (Button)findViewById(R.id.downloding_back);
		
		clearBtn.setOnClickListener(myClick);
		flashBtn.setOnClickListener(myClick);
		backBtn.setOnClickListener(myClick);
		defalutInitDownLoadData();
	}

	private void defalutInitDownLoadData(){
		List<DLTask> dlOverTasks = mOperator.getDLOverTasks();
		if (Downloader.allTasks == null){
			Downloader.allTasks = Collections.synchronizedMap(new HashMap<DLTask, Set<DLThreadTask>>());
		}
		loadingAdapter = new DefalutDownLoadingAdapter(this, mOperator);

		downloading_content.setAdapter(loadingAdapter);
		reflashLoadingAdapter();
		loadedAdapter = new DefalutDownLoadedAdapter(this, dlOverTasks);
		downloaded_content.setAdapter(loadedAdapter);
		reflashLoadedAdapter();
		
		downloaded_content.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
			    Intent intent = new Intent(BaseDownLoadActivity.this, BaseDownLoadInfo.class);
			    String urlStr = loadingAdapter.getItem(arg2).getKey().downLoadUrl;
			    intent.putExtra("task", urlStr);
			    startActivity(intent);
			}
		});
		
		downloading_content.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BaseDownLoadActivity.this, BaseDownLoadInfo.class);
			    String urlStr = loadedAdapter.getItem(arg2).downLoadUrl;
			    intent.putExtra("task", urlStr);
			    startActivity(intent);
			}
		});
  }

	private void onDefaultCreate(Bundle paramBundle) {
		setContentView(R.layout.download_default);
		this.mOperator = new DLDatabaseHelper(this);;
		defalutInit();
		registerDownloadReceiver();
	}

	/**
	 * 刷新 已下载的列表
	 */
	private void reflashLoadedAdapter() {
		if (loadedAdapter.getAllTasks().size() > 0) {
			downloaded_no.setVisibility(View.GONE);
			downloaded_content.setVisibility(View.VISIBLE);
		} else {
			downloaded_content.setVisibility(View.GONE);
			downloaded_no.setVisibility(View.VISIBLE);
		}
		downloaded_text.setText("已下载("+loadedAdapter.getAllTasks().size()+")");
	}

	/**
	 * 刷新正在下载的 列表
	 */
	private void reflashLoadingAdapter() {
		if (Downloader.allTasks.size() > 0) {
			downloading_no.setVisibility(View.GONE);
			downloading_content.setVisibility(View.VISIBLE);
		} else {
			downloading_content.setVisibility(View.GONE);
			downloading_no.setVisibility(View.VISIBLE);
		}
		downloading_text.setText("正在下载(" + Downloader.allTasks.size() + ")");
	}

	private void registerDownloadReceiver() {
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(DLConstant.BROADCAST_TASK);
		registerReceiver(mReceiver, mFilter);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		onDefaultCreate(savedInstanceState);
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
}