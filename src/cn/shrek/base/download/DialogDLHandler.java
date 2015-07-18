package cn.shrek.base.download;

import java.io.File;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import cn.shrek.base.download.bo.DLTask;
import cn.shrek.base.util.BaseUtil;
import cn.shrek.base.util.ZWLogger;
import cn.shrek.base.util.thread.HandlerEnforcer;
import cn.shrek.base.util.thread.ZWThreadEnforcer;

/**
 * Ĭ�ϵ� ������ ������
 * 
 * @author shrek
 * 
 */
public abstract class DialogDLHandler implements DLHandler {
	private ProgressDialog progressDialog;
	private Context ctx;
	private Handler handler;

	ZWThreadEnforcer enforcer;

	public DialogDLHandler(Context ctx) {
		super();
		this.ctx = ctx;

		enforcer = HandlerEnforcer.newInstance();
	}

	@Override
	public int downLoadError(final DLTask task, Exception exception) {
		// TODO Auto-generated method stub
		enforcer.enforceMainThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				showNormalError(false, "������", "���س����쳣", new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						BaseUtil.downloadFile(ctx, task, DialogDLHandler.this);
						ZWLogger.printLog(DialogDLHandler.this, "����������,����·����"
								+ task.downLoadUrl);
					}
				});
			}
		});

		return DLConstant.ERROR_DEFAULT;
	}

	@Override
	public abstract boolean isDLFileExist(DLTask task);

	@Override
	public void postDownLoadingOnUIThread(final DLTask task) {
		// TODO Auto-generated method stub
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (!task.isAutoOpen) {
			showNormalError(true, "�������", "�ļ�" + task.fileName + "�������!",
					new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							ZWLogger.printLog(DialogDLHandler.this,
									"���ļ�,����·����" + task.downLoadUrl);
							BaseUtil.openFile(new File(new File(task.savePath),
									task.fileName), ctx);
						}
					});
		}
	}

	@Override
	public void downLoadingProgressOnOtherThread(final DLTask task, final int hasDownSize) {
		// TODO Auto-generated method stub
		if (progressDialog != null) {
			enforcer.enforceMainThread(new Runnable() {

				@Override
				public void run() {
					progressDialog.setMessage("��������" + task.fileName);
					progressDialog.setMax((int) task.totalSize);
					progressDialog.setProgress(hasDownSize);
				}
			});
		}
	}

	@Override
	public void preDownloadDoingOnUIThread(DLTask task) {
		// TODO Auto-generated method stub
		if (ctx != null) {
			progressDialog = new ProgressDialog(ctx);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setTitle("������");
			progressDialog.setMessage("���ڻ�ȡ������Ϣ,���Ե�...");
			progressDialog.setCancelable(false);
			if (ctx != null && progressDialog != null
					&& !progressDialog.isShowing())
				progressDialog.show();
		}

		handler = new Handler();
	}

	@Override
	public boolean sdcardNoExistOnUIThread(DLTask task) {
		// TODO Auto-generated method stub
		AlertDialog.Builder build = new AlertDialog.Builder(ctx);
		build.setTitle("����ʧ��").setMessage("SD��������,û���洢�ļ�Ŷ!")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
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
	public int threadNumConflictOnOtherThread(DLTask task, int oldThreadNum) {
		// TODO Auto-generated method stub
		return DLConstant.CONFLICT_DEFAULT;
	}

	@Override
	public void openFileErrorOnOtherThread(final DLTask task, Exception e) {
		// TODO Auto-generated method stub
		showNormalError(false, "������", "���ļ�ʧ��!", new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				BaseUtil.downloadFile(ctx, task, DialogDLHandler.this);
				ZWLogger.i(DialogDLHandler.this, "����������,����·����"
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
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
//									dialog.cancel();
									dialog.dismiss();
									// run.run();
								}
							});

			build.setNegativeButton(isSuccess ? "��" : "����",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							if(run != null){
								run.run();
							}
							arg0.dismiss();
						}

					});

			if (ctx != null) {
				build.show();
			}
		}

	}
}
