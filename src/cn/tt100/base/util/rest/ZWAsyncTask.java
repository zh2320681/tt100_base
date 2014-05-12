package cn.tt100.base.util.rest;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import cn.tt100.base.ZWActivity;

public class ZWAsyncTask<PARSEOBJ> extends
		AsyncTask<ZWRequestConfig, Void, ZWResult<PARSEOBJ>> {
	private static final String TAG = "BaseAsyncTask";

	public static final byte PRE_TASK_NORMAL = 0x01;
	public static final byte PRE_TASK_CUSTOM = 0x02;
	public static final byte PRE_TASK_DONOTHING = 0x03;

	public WeakReference<Context> ctx;
	// ��������
	private AsyncTaskHandler<PARSEOBJ> handler;

	private AtomicBoolean isCancel;
	private Class<PARSEOBJ> parseObjClazz;
	
	public ZWAsyncTask(Context ctx, AsyncTaskHandler<PARSEOBJ> handler) {
		this.ctx = new WeakReference<Context>(ctx);
		this.handler = handler;
		isCancel = new AtomicBoolean(false);

		if (handler != null) {
			handler.setTask(this);
		}
		if (ctx instanceof ZWActivity) {
			((ZWActivity) ctx).addTask(this);
		}

	}

	public static <T> void excuteTask(Context ctx,String url, HttpMethod method,Class<T> clazz,Map<String,String> paras, AsyncTaskHandler<T> handler) {
		ZWAsyncTask<T> task =  new ZWAsyncTask<T>(ctx,handler);
		task.parseObjClazz = clazz;
		
		ZWRequestConfig config = ZWRequestConfig.copyDefault();
		if(method != null){
			config.httpMethod = method;
		}
		
		config.url = url;
		if(paras != null){
			config.getMaps().putAll(paras);
		}
		
		task.execute(config);
	}

	public static <T> void excuteTask(Context ctx,String urlWithoutPar,HttpMethod method,Class<T> clazz, AsyncTaskHandler<T> handler) {
		excuteTask(ctx,urlWithoutPar,method,clazz,null,handler);
	}
	
	public static <T> void excuteTask(Context ctx,String urlWithoutPar,Class<T> clazz, AsyncTaskHandler<T> handler) {
		excuteTask(ctx,urlWithoutPar,null,clazz,null,handler);
	}
	
//	public static <T> void excuteTask(Context ctx,String urlWithoutPar,Class<T> clazz, AsyncTaskHandler<T> handler) {
//		excuteTask(ctx,urlWithoutPar,null,clazz,null,handler);
//	}
	
	@Override
	protected void onPreExecute() {
		// ����ִ��ǰ
		super.onPreExecute();
		if (judgeTaskValid() && handler != null) {
			handler.preDoing();
		}
	}

	@Override
	protected ZWResult<PARSEOBJ> doInBackground(ZWRequestConfig... params) {
		// TODO Auto-generated method stub
		ZWRequestConfig config = params[0];
		if (config == null) {
			throw new NullPointerException(
					"excute task parameter��ZWRequestConfig�� must not null");
		}
		ZWResult<PARSEOBJ> r = new ZWResult<PARSEOBJ>();
		try {
			HttpHeaders requestHeaders = new HttpHeaders();
			for (Map.Entry<String, String> entry : config.getHeaders()
					.entrySet()) {
				requestHeaders.add(entry.getKey(), entry.getValue());
			}
			// requestHeaders.setAccept(Collections.singletonList(new
			// MediaType("application","json")));
			HttpEntity<?> requestEntity = new HttpEntity<Object>(
					config.getBody(), requestHeaders);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(config.converter);
			ResponseEntity<PARSEOBJ> responseEntity = restTemplate.exchange(
					config.url, config.httpMethod, requestEntity, parseObjClazz,
					config.getMaps());
//			String result = responseEntity.getBody();
			r.requestCode = responseEntity.getStatusCode();
			r.bodyObj = responseEntity.getBody();
			r.errorException = null;
//			if (config.parseClazz != null) {
//				if (config.isList) {
//					r.bodyObj = JSON.parseArray(result, config.parseClazz);
//				} else {
//					r.bodyObj = JSON.parseObject(result, config.parseClazz);
//				}
//			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			r.errorException = e;
		}
		return r;
	}

	@Override
	protected void onPostExecute(ZWResult<PARSEOBJ> result) {
		// ����ִ�к�
		super.onPostExecute(result);
		if (judgeTaskValid() && handler != null) {
			handler.afterTaskDoing();
			// result.bodyStr
			handler.postResult(result);
		}

	}

	public final boolean judgeTaskValid() {
		Context mContext = ctx.get();
		return mContext != null && !((Activity) mContext).isFinishing()
				&& !isCancel();
	}

	/**
	 * �ж� �ǲ��������
	 */
	public boolean judgeContextToActivity(Class<? extends Context> activityClass) {
		Context mContext = ctx.get();
		if (mContext != null) {
			Class<? extends Context> ctxClass = mContext.getClass();
			if (ctxClass.hashCode() == activityClass.hashCode()) {
				return true;
			}
		}
		return false;
	}

	public boolean isCancel() {
		return isCancel.get();
	}

	public void setCancel(boolean isCancel) {
		this.isCancel.set(isCancel);
	}
}
