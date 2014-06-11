package cn.shrek.base.example;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.shrek.base.ZWActivity;
import cn.shrek.base.annotation.AutoInitialize;
import cn.shrek.base.annotation.AutoOnClick;
import cn.shrek.base.download.DLHandler;
import cn.shrek.base.download.DialogDLHandler;
import cn.shrek.base.download.bo.DLTask;
import cn.shrek.base.util.BaseUtil;

public class DLTestActivity extends ZWActivity {
	@AutoInitialize(idFormat = "dl_?")
	@AutoOnClick(clickSelector = "mClick")
	Button downTestBtn,downDialogTestBtn;
	
	public OnClickListener mClick = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(arg0 ==  downTestBtn){
				DLTask task = new DLTask("http://img6.cache.netease.com/photo/0001/2014-05-13/9S48SRBA00AN0001.jpg");
				BaseUtil.downloadFile(getApplicationContext(), task, new DLHandler() {
					
					@Override
					public int threadNumConflict(DLTask task, int oldThreadNum) {
						// TODO Auto-generated method stub
						System.out.println("=====>threadNumConflict");
						return 0;
					}
					
					@Override
					public boolean sdcardNoExist(DLTask task) {
						// TODO Auto-generated method stub
						System.out.println("=====>sdcardNoExist");
						return false;
					}
					
					@Override
					public void preDownloadDoing(DLTask task) {
						// TODO Auto-generated method stub
						System.out.println("=====>preDownloadDoing");
					}
					
					@Override
					public void postDownLoading(DLTask task) {
						// TODO Auto-generated method stub
						System.out.println("=====>postDownLoading");
					}
					
					@Override
					public void openFileError(DLTask task, Exception e) {
						// TODO Auto-generated method stub
						System.out.println("=====>openFileError");
					}
					
					@Override
					public boolean isDLFileExist(DLTask task) {
						// TODO Auto-generated method stub
						System.out.println("=====>isDLFileExist");
						return false;
					}
					
					@Override
					public int downLoadError(DLTask task, Exception exception) {
						// TODO Auto-generated method stub
						System.out.println("=====>downLoadError");
						return 0;
					}

					@Override
					public void downLoadingProgress(DLTask task, int hasDownSize) {
						// TODO Auto-generated method stub
						
					}
				});
			}else if(arg0 == downDialogTestBtn){
				DLTask task = new DLTask("http://img6.cache.netease.com/photo/0001/2014-05-13/9S48SRBA00AN0001.jpg");
				BaseUtil.downloadFile(getApplicationContext(), task,new DialogDLHandler(DLTestActivity.this) {
					
					@Override
					public boolean isDLFileExist(DLTask task) {
						// TODO Auto-generated method stub
						return false;
					}
				});
			}
		}
	};
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyObserver(Object oldObj, Object newObj) {
		// TODO Auto-generated method stub

	}

}
