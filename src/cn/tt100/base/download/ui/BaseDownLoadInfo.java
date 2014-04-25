package cn.tt100.base.download.ui;

import java.util.Set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.tt100.base.R;
import cn.tt100.base.download.DLConstant;
import cn.tt100.base.download.Downloader;
import cn.tt100.base.download.bo.DLTask;
import cn.tt100.base.download.bo.DLThreadTask;
import cn.tt100.base.download.db.DLDatabaseHelper;
import cn.tt100.base.util.BaseUtil;

public class BaseDownLoadInfo extends Activity {
	private DLTask mDLTask;
	private Button nameBtn, addressBtn;
	private TextView nameView, sizeView, threadNumView, savePathView,
			addressView;
	private LinearLayout downloadInfo;
	private InfoView mInfoView;
	private DLDatabaseHelper mHelper;

	// 得到剪贴板管理器
	private ClipboardManager cmb;

	/** Called when the activity is first created. */
	public BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public synchronized void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (DLConstant.BROADCAST_TASK.equals(action)) {
				String str = intent.getStringExtra(DLConstant.DL_TASK_MSG);
				DLTask task = (DLTask) intent
						.getSerializableExtra(DLConstant.DL_TASK_OBJ);
				if (mDLTask.equals(task)) {
					mInfoView.invalidate();
				}
			}
		}

	};

	private OnClickListener myClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == nameBtn) {
				cmb.setText(nameBtn.getText());
				Toast.makeText(BaseDownLoadInfo.this, "文件名已经复制到剪切板",
						Toast.LENGTH_SHORT).show();
				return;
			} else if (v == addressBtn) {
				cmb.setText(addressView.getText());
				Toast.makeText(BaseDownLoadInfo.this, "下载地址已经复制到剪切板",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.download_default_info);

		mHelper = new DLDatabaseHelper(this);
		;
		String str = getIntent().getStringExtra("task");
		DLTask localDLTask1 = Downloader.getDLTask(str);
		if (localDLTask1 == null) {
			mDLTask = mHelper.getTaskByPath(str);
			mDLTask.states.set(DLConstant.TASK_SUCESS);
		} else {
			mDLTask = localDLTask1;
		}

		init();
		addListener();
		registerDownloadReceiver();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	private void init() {

		cmb = (ClipboardManager) this
				.getSystemService(Context.CLIPBOARD_SERVICE);

		nameBtn = (Button) findViewById(R.id.dl_info_name_btn);
		addressBtn = (Button) findViewById(R.id.dl_info_address_btn);

		nameView = (TextView) findViewById(R.id.dl_info_name);
		sizeView = (TextView) findViewById(R.id.dl_info_size);
		threadNumView = (TextView) findViewById(R.id.dl_info_thread);
		savePathView = (TextView) findViewById(R.id.dl_info_save);
		addressView = (TextView) findViewById(R.id.dl_info_address);

		nameView.setText(mDLTask.fileName);
		sizeView.setText(BaseUtil.getFileSize(mDLTask.totalSize));
		threadNumView.setText(Downloader.allTasks.get(mDLTask).size() + "个");
		savePathView.setText(mDLTask.savePath);
		addressView.setText(mDLTask.downLoadUrl);

		downloadInfo = (LinearLayout) findViewById(R.id.dl_info_layout);

		mInfoView = new InfoView(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (mInfoView.hangNum * (mInfoView.boxLength + mInfoView.jg)));
		lp.topMargin = 5;
		downloadInfo.setLayoutParams(lp);

		downloadInfo.addView(mInfoView);
	}

	private void addListener() {
		nameBtn.setOnClickListener(myClick);
		addressBtn.setOnClickListener(myClick);
	}

	public void registerDownloadReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(DLConstant.BROADCAST_TASK);
		// filter.addAction(DLConstant.DL_TASK_MSG);
		// filter.addAction(DLConstant.BROADCAST_PROGRESS);
		registerReceiver(mReceiver, filter);
	}

	class InfoView extends View {
		public float boxLength;
		public int hangNum;
		public int lieNum;
		public float jg;
		public float startDrawX;
		public Paint greyPaint, greenPaint, yellowPaint;

		private float boxValue; // 每一个方块代表的值

		public InfoView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			Display display = ((WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();
			int screenWidth = display.getWidth();
			int screenHeight = display.getHeight();

			hangNum = lieNum = 20;

			startDrawX = screenWidth / 18.0f;
			jg = (screenWidth - startDrawX * 2) / 99;
			boxLength = jg * 4;
			startDrawX = 0;
			greenPaint = new Paint();
			greenPaint.setColor(Color.parseColor("#a06699FF"));
			greenPaint.setAntiAlias(true);

			yellowPaint = new Paint();
			yellowPaint.setColor(Color.parseColor("#a0FFCC00"));
			yellowPaint.setAntiAlias(true);

			greyPaint = new Paint();
			greyPaint.setColor(Color.parseColor("#a0CCCCCC"));
			greyPaint.setAntiAlias(true);

			boxValue = mDLTask.totalSize * 1.0f / (hangNum * lieNum);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);

			// boolean isFinish = mDownloadTask.judgeDownloadFinish();
			for (int i = 0; i < hangNum; i++) {
				for (int j = 0; j < lieNum; j++) {
					float value = boxValue * (i * lieNum + j);
					Paint p = greenPaint;
					if (mDLTask.states.get() != DLConstant.TASK_SUCESS) {
						final Set<DLThreadTask> tasks = Downloader.allTasks
								.get(mDLTask);
						int threadNum = -1; // 是哪个线程
						for (DLThreadTask dtt : tasks) {
							threadNum++;
							if (value < dtt.downloadBlock * (threadNum + 1)) {
								// 就是这个N线程
								float start = dtt.downloadBlock * threadNum;
								float index = value - start;
								if (index <= dtt.hasDownloadLength - boxValue) {
									p = greenPaint;
								} else if (index > dtt.hasDownloadLength
										- boxValue
										&& index <= dtt.hasDownloadLength
												+ boxValue) {
									p = yellowPaint;
								} else {
									p = greyPaint;
								}
								break;
							}
						}
					}
					canvas.drawRoundRect(new RectF(startDrawX + j
							* (boxLength + jg), i * (boxLength + jg),
							startDrawX + j * (boxLength + jg) + boxLength, i
									* (boxLength + jg) + boxLength), 3, 3, p);
					// canvas.drawRect(startDrawX + j * (boxLength + jg), i
					// * (boxLength + jg), startDrawX + j
					// * (boxLength + jg) + boxLength, i
					// * (boxLength + jg) + boxLength, p);
				}
			}
		}
	}

}
