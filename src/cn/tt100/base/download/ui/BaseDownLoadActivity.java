package cn.tt100.base.download.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.tt100.base.download.Downloader;
import cn.tt100.base.download.adapter.DefalutDownLoadedAdapter;
import cn.tt100.base.download.adapter.DefalutDownLoadingAdapter;
import cn.tt100.base.download.db.DLDatabaseHelper;

public class BaseDownLoadActivity extends Activity{
	
  private static final int MSG_SERVICE_START = 17;
  private Button backBtn,clearBtn;
 
  private Handler defaultHandler;
  private AlertDialog dialog;
  private ListView downloaded_content;
  private ImageView downloaded_expand,downloaded_no,downloading_expand;
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
  
  public BroadcastReceiver mReceiver;
  
  private OnClickListener myClick = new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
};

  public BaseDownLoadActivity()
  {
    BaseDownLoadActivity.1 local1 = new BaseDownLoadActivity.1(this);
    this.mReceiver = local1;
    BaseDownLoadActivity.2 local2 = new BaseDownLoadActivity.2(this);
    this.myClick = local2;
    BaseDownLoadActivity.3 local3 = new BaseDownLoadActivity.3(this);
    this.defaultHandler = local3;
  }

  private void defalutInit()
  {
    TextView localTextView1 = (TextView)findViewById(2131165191);
    this.downloading_text = localTextView1;
    TextView localTextView2 = (TextView)findViewById(2131165197);
    this.downloaded_text = localTextView2;
    ImageView localImageView1 = (ImageView)findViewById(2131165193);
    this.downloading_no = localImageView1;
    ImageView localImageView2 = (ImageView)findViewById(2131165199);
    this.downloaded_no = localImageView2;
    ImageView localImageView3 = (ImageView)findViewById(2131165192);
    this.downloading_expand = localImageView3;
    ImageView localImageView4 = (ImageView)findViewById(2131165198);
    this.downloaded_expand = localImageView4;
    RelativeLayout localRelativeLayout1 = (RelativeLayout)findViewById(2131165189);
    this.downloading_layout = localRelativeLayout1;
    RelativeLayout localRelativeLayout2 = (RelativeLayout)findViewById(2131165195);
    this.downloaded_layout = localRelativeLayout2;
    ListView localListView1 = (ListView)findViewById(2131165194);
    this.downloading_content = localListView1;
    ListView localListView2 = (ListView)findViewById(2131165200);
    this.downloaded_content = localListView2;
    Button localButton1 = (Button)findViewById(2131165186);
    this.clearBtn = localButton1;
    Button localButton2 = (Button)findViewById(2131165187);
    this.flashBtn = localButton2;
    Button localButton3 = (Button)findViewById(2131165188);
    this.backBtn = localButton3;
    Button localButton4 = this.clearBtn;
    View.OnClickListener localOnClickListener1 = this.myClick;
    localButton4.setOnClickListener(localOnClickListener1);
    Button localButton5 = this.flashBtn;
    View.OnClickListener localOnClickListener2 = this.myClick;
    localButton5.setOnClickListener(localOnClickListener2);
    Button localButton6 = this.backBtn;
    View.OnClickListener localOnClickListener3 = this.myClick;
    localButton6.setOnClickListener(localOnClickListener3);
    defalutInitDownLoadData();
  }

  private void defalutInitDownLoadData()
  {
    List localList = this.mOperator.getDLOverTasks();
    if (Downloader.allTasks == null)
      Downloader.allTasks = Collections.synchronizedMap(new HashMap());
    DLDatabaseHelper localDLDatabaseHelper = this.mOperator;
    DefalutDownLoadingAdapter localDefalutDownLoadingAdapter1 = new DefalutDownLoadingAdapter(this, localDLDatabaseHelper);
    this.loadingAdapter = localDefalutDownLoadingAdapter1;
    ListView localListView1 = this.downloading_content;
    DefalutDownLoadingAdapter localDefalutDownLoadingAdapter2 = this.loadingAdapter;
    localListView1.setAdapter(localDefalutDownLoadingAdapter2);
    reflashLoadingAdapter();
    DefalutDownLoadedAdapter localDefalutDownLoadedAdapter1 = new DefalutDownLoadedAdapter(this, localList);
    this.loadedAdapter = localDefalutDownLoadedAdapter1;
    ListView localListView2 = this.downloaded_content;
    DefalutDownLoadedAdapter localDefalutDownLoadedAdapter2 = this.loadedAdapter;
    localListView2.setAdapter(localDefalutDownLoadedAdapter2);
    reflashLoadedAdapter();
    ListView localListView3 = this.downloaded_content;
    BaseDownLoadActivity.4 local4 = new BaseDownLoadActivity.4(this);
    localListView3.setOnItemClickListener(local4);
    ListView localListView4 = this.downloading_content;
    BaseDownLoadActivity.5 local5 = new BaseDownLoadActivity.5(this);
    localListView4.setOnItemClickListener(local5);
  }

  private void onDefaultCreate(Bundle paramBundle)
  {
    setContentView(2130903041);
    DLDatabaseHelper localDLDatabaseHelper = new DLDatabaseHelper(this);
    this.mOperator = localDLDatabaseHelper;
    defalutInit();
    registerDownloadReceiver();
  }

  private void reflashLoadedAdapter()
  {
    if (this.loadedAdapter.getAllTasks().size() > 0)
    {
      this.downloaded_no.setVisibility(8);
      this.downloaded_content.setVisibility(0);
    }
    while (true)
    {
      TextView localTextView = this.downloaded_text;
      StringBuilder localStringBuilder = new StringBuilder("已下载(");
      int i = this.loadedAdapter.getAllTasks().size();
      String str = i + ")";
      localTextView.setText(str);
      return;
      this.downloaded_content.setVisibility(8);
      this.downloaded_no.setVisibility(0);
    }
  }

  private void reflashLoadingAdapter()
  {
    if (Downloader.allTasks.size() > 0)
    {
      this.downloading_no.setVisibility(8);
      this.downloading_content.setVisibility(0);
    }
    while (true)
    {
      TextView localTextView = this.downloading_text;
      StringBuilder localStringBuilder = new StringBuilder("正在下载(");
      int i = Downloader.allTasks.size();
      String str = i + ")";
      localTextView.setText(str);
      return;
      this.downloading_content.setVisibility(8);
      this.downloading_no.setVisibility(0);
    }
  }

  private void registerDownloadReceiver()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("BROADCAST_TASK");
    BroadcastReceiver localBroadcastReceiver = this.mReceiver;
    Intent localIntent = registerReceiver(localBroadcastReceiver, localIntentFilter);
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    boolean bool = requestWindowFeature(1);
    onDefaultCreate(paramBundle);
  }

  protected void onDestroy()
  {
    super.onDestroy();
    BroadcastReceiver localBroadcastReceiver = this.mReceiver;
    unregisterReceiver(localBroadcastReceiver);
  }
}
