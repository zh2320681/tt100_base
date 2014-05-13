package cn.tt100.base.download;

import java.io.File;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import cn.tt100.base.download.bo.DLTask;
import cn.tt100.base.util.BaseUtil;
import cn.tt100.base.util.ZWLogger;

/**
 * 默认的 弹出框 处理器
 * 
 * @author shrek
 * 
 */
public abstract class DialogDLHandler implements DLHandler {
	private ProgressDialog progressDialog;
	private Context ctx;
	private Handler handler;
	public DialogDLHandler(Context ctx){
		super();
		this.ctx = ctx;
	}
	
	@Override
	public int downLoadError(final DLTask task, Exception exception) {
		// TODO Auto-generated method stub
		showNormalError(false, "出错啦", "下载出现异常", new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				BaseUtil.downloadFile(ctx, task, DialogDLHandler.this);
				ZWLogger.printLog(DialogDLHandler.this, "任务重试中,任务路径："
						+ task.downLoadUrl);
			}
		});
		return DLConstant.ERROR_DEFAULT;
	}

	@Override
	public abstract boolean isDLFileExist(DLTask task);

	@Override
	public void postDownLoading(final DLTask task) {
		// TODO Auto-generated method stub
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (!task.isAutoOpen) {
			showNormalError(true, "下载完成", "文件" + task.fileName + "下载完成!",
					new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							ZWLogger.printLog(DialogDLHandler.this,
									"打开文件,任务路径：" + task.downLoadUrl);
							BaseUtil.openFile(new File(new File(task.savePath),
									task.fileName), ctx);
						}
					});
		}
	}

	@Override
	public void downLoadingProgress(final DLTask task, final int hasDownSize) {
		// TODO Auto-generated method stub
		
		if (progressDialog != null) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
//					String totalStr = BaseUtil.getFileSize(task.totalSize);
//					String downStr = BaseUtil.getFileSize(hasDownSize);
					progressDialog.setMessage("正在下载" + task.fileName);
					progressDialog.setMax((int) task.totalSize);
					progressDialog.setProgress(hasDownSize);
				}
			});
			
		}
	}

	@Override
	public void preDownloadDoing(DLTask task) {
		// TODO Auto-generated method stub
		if (ctx != null) {
			progressDialog = new ProgressDialog(ctx);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setTitle("下载中");
			progressDialog.setMessage("正在获取下载信息,请稍等...");
			progressDialog.setCancelable(false);
			if (ctx != null && progressDialog != null
					&& !progressDialog.isShowing())
				progressDialog.show();
		}
		
		handler = new Handler();
	}

	@Override
	public boolean sdcardNoExist(DLTask task) {
		// TODO Auto-generated method stub
		AlertDialog.Builder build = new AlertDialog.Builder(ctx);
		build.setTitle("下载失败").setMessage("SD卡不存在,没法存储文件哦!")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		if (ctx != null) {
			build.show();
		}
		return false;
	}

	@Override
	public int threadNumConflict(DLTask task, int oldThreadNum) {
		// TODO Auto-generated method stub
		return DLConstant.CONFLICT_DEFAULT;
	}

	@Override
	public void openFileError(final DLTask task, Exception e) {
		// TODO Auto-generated method stub
		showNormalError(false, "出错啦", "打开文件失败!", new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				BaseUtil.downloadFile(ctx, task, DialogDLHandler.this);
				ZWLogger.printLog(DialogDLHandler.this, "任务重试中,任务路径："
						+ task.downLoadUrl);
			}
		});
	}

	private void showNormalError(boolean isSuccess, String errorTitle,
			String errorContent, final Runnable run) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		if (ctx != null && errorTitle != null && errorContent != null) {
			AlertDialog.Builder build = new AlertDialog.Builder(ctx);
			build.setTitle(errorTitle)
					.setMessage(errorContent)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									// run.run();
								}
							});

			build.setNegativeButton(isSuccess ? "打开" : "重试",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							run.run();
						}

					});

			if (ctx != null) {
				build.show();
			}
		}

	}
}
