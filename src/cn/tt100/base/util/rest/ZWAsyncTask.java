package cn.tt100.base.util.rest;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import cn.tt100.base.ZWActivity;
import cn.tt100.base.util.ZWLogger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class ZWAsyncTask<PARSEOBJ> extends
		AsyncTask<ZWRequestConfig, Void, ZWResult<PARSEOBJ>> {
	private static final String TAG = "ZWAsyncTask";
	// 用于计时
	private static final Map<String, Long> timeingMap = new HashMap<String, Long>();

	public static final byte PRE_TASK_NORMAL = 0x01;
	public static final byte PRE_TASK_CUSTOM = 0x02;
	public static final byte PRE_TASK_DONOTHING = 0x03;

	private String taskGuid;
	public WeakReference<Context> ctx;
	// 请求处理器
	private AsyncTaskHandler<PARSEOBJ> handler;

	private AtomicBoolean isCancel;
	// private Class<PARSEOBJ> parseObjClazz;
	private TypeReference<PARSEOBJ> reference;

	/**
	 * 自带请求的config 在队列时候用
	 */
	public ZWRequestConfig config;
	private Queue<ZWAsyncTask<?>> allTask;

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

	/**
	 * 将任务添加到队列中,并返回队列的第一个任务执行
	 * 
	 * @param tasks
	 * @return
	 */
	public static void addTaskIntoQueueAndExcute(ZWAsyncTask<?>... tasks) {
		Queue<ZWAsyncTask<?>> allTask = new LinkedList<ZWAsyncTask<?>>();
		for (ZWAsyncTask<?> task : tasks) {
			if (task.config == null) {
				throw new NullPointerException("队列里面任务必须提前设置 Config!");
			}
			task.allTask = allTask;
			allTask.add(task);
		}
		if (allTask.size() > 0) {
			ZWAsyncTask<?> task = allTask.poll();
			if (task != null) {
				task.execute(task.config);
			}
		}
	}

	/**
	 * 静态的 请求方法
	 * 
	 * @param ctx
	 * @param url
	 *            地址 类似与http://example.com/hotels/{hotel}/bookings/{booking}
	 * @param method
	 *            GET POST PUT等
	 * @param clazz
	 *            返回什么类型
	 * @param paras
	 *            URL参数
	 * @param handler
	 *            任务回调用
	 */
	public static <T> void excuteTask(Context ctx, String url,
			HttpMethod method, TypeReference<T> reference,
			Map<String, String> paras, AsyncTaskHandler<T> handler) {
		ZWAsyncTask<T> task = new ZWAsyncTask<T>(ctx, handler);
		task.reference = reference;

		ZWRequestConfig config = ZWRequestConfig.copyDefault();
		if (method != null) {
			config.httpMethod = method;
		}

		config.url = url;
		if (paras != null) {
			config.getMaps().putAll(paras);
		}
		task.execute(config);
	}

	/**
	 * 静态的 请求方法
	 * 
	 * @param ctx
	 * @param url
	 *            地址 类似与http://example.com/hotels/{hotel}/bookings/{booking}
	 * @param method
	 *            GET POST PUT等
	 * @param clazz
	 *            返回什么类型
	 * @param paras
	 *            URL参数
	 * @param handler
	 *            任务回调用
	 */
	public static <T> void excuteTaskWithParas(Context ctx, String url,
			HttpMethod method, TypeReference<T> reference,
			AsyncTaskHandler<T> handler, Object... paras) {
		ZWAsyncTask<T> task = new ZWAsyncTask<T>(ctx, handler);
		task.reference = reference;

		ZWRequestConfig config = ZWRequestConfig.copyDefault();
		if (method != null) {
			config.httpMethod = method;
		}

		config.url = url;
		if (paras != null) {
			config.setParas(paras);
		}
		task.execute(config);
	}

	public static <T> void excuteTaskWithOutMethod(Context ctx, String url,
			TypeReference<T> reference, AsyncTaskHandler<? extends T> handler,
			Object... paras) {
		excuteTaskWithParas(ctx, url, null, reference,
				(AsyncTaskHandler<T>) handler, paras);

	}

	public static <T> void excuteTask(Context ctx, String urlWithoutPar,
			HttpMethod method, TypeReference<T> reference,
			AsyncTaskHandler<? extends T> handler) {
		excuteTask(ctx, urlWithoutPar, method, reference, null,
				(AsyncTaskHandler<T>) handler);
	}

	public static <T> void excuteTask(Context ctx, String urlWithoutPar,
			TypeReference<T> reference, AsyncTaskHandler<? extends T> handler) {
		excuteTask(ctx, urlWithoutPar, null, reference, null,
				(AsyncTaskHandler<T>) handler);
	}

	// public static <T> void excuteTask(Context ctx,String
	// urlWithoutPar,Class<T> clazz, AsyncTaskHandler<T> handler) {
	// excuteTask(ctx,urlWithoutPar,null,clazz,null,handler);
	// }

	@Override
	protected void onPreExecute() {
		// 任务执行前
		super.onPreExecute();
		taskGuid = UUID.randomUUID().toString();
		ZWLogger.printLog(TAG, "任务开始,任务ID为:" + taskGuid);
		timeingMap.put(taskGuid, System.currentTimeMillis());

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
					"excute task parameter（ZWRequestConfig） must not null");
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
			//设置超时
			ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();
			if(requestFactory instanceof HttpComponentsClientHttpRequestFactory ){
				HttpComponentsClientHttpRequestFactory  mComponentsClientHttpRequestFactory
				 = (HttpComponentsClientHttpRequestFactory)requestFactory;
				mComponentsClientHttpRequestFactory.setConnectTimeout(config.connTimeOut);
				mComponentsClientHttpRequestFactory.setReadTimeout(config.readTimeOut);
			}
//			requestFactory.
			restTemplate.getMessageConverters().add(config.converter);

			ResponseEntity<String> responseEntity = null;
			if (config.getParas() != null) {
				responseEntity = restTemplate.exchange(config.url,
						config.httpMethod, requestEntity, String.class,
						config.getParas());
			} else if (config.getMaps().size() != 0) {
				responseEntity = restTemplate.exchange(config.url,
						config.httpMethod, requestEntity, String.class,
						config.getMaps());
			} else {
				responseEntity = restTemplate.exchange(config.url,
						config.httpMethod, requestEntity, String.class);
			}

			String result = responseEntity.getBody();

			r.requestCode = responseEntity.getStatusCode();
			// if(List.class.isAssignableFrom(parseObjClazz)){
			r.bodyObj = JSON.parseObject(result, reference);
			// }else{
			// r.bodyObj = JSON.parseObject(result, parseObjClazz);
			// }
			r.errorException = null;
			// if (config.parseClazz != null) {
			// if (config.isList) {
			// r.bodyObj = JSON.parseArray(result, config.parseClazz);
			// } else {
			// r.bodyObj = JSON.parseObject(result, config.parseClazz);
			// }
			// }
		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
			ZWLogger.printLog(TAG, "请求出现异常:"+e.toString());
			r.errorException = e;
//			return null;
		}
		return r;
	}

	@Override
	protected void onPostExecute(ZWResult<PARSEOBJ> result) {
		// 任务执行后
		super.onPostExecute(result);
		if (judgeTaskValid() && handler != null) {
			handler.afterTaskDoing();
			// result.bodyStr
			if (result.errorException != null) {
				//出现错误的时候
				handler.postError(result, result.errorException);
				return;
			}
			handler.postResult(result);

			Context context = ctx.get();
			if (context instanceof ZWActivity) {
				((ZWActivity) context).removeTask(this);
			}
			if (allTask != null) {
				if (allTask.size() == 0) {
					// 队列里面任务允许完毕

				} else {
					if (config == null) {
						throw new NullPointerException("队列里面任务必须提前设置 Config!");
					}
					ZWLogger.printLog(TAG, "队列有任务,继续执行!");
					ZWAsyncTask<?> task = allTask.poll();
					task.execute(config);
				}
			} else {
				cycle();
			}
		}

		ZWLogger.printLog(
				TAG,
				"任务Over,任务ID为:"
						+ taskGuid
						+ "  耗时:"
						+ (System.currentTimeMillis() - timeingMap
								.remove(taskGuid)) + "毫秒!");
	}

	public final boolean judgeTaskValid() {
		Context mContext = ctx.get();
		return mContext != null && !((Activity) mContext).isFinishing()
				&& !isCancel();
	}

	/**
	 * 判断 是不是这个类
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

	/**
	 * 清空数据
	 */
	private void cycle() {
		ctx.clear();
		ctx = null;
		// 请求处理器
		handler = null;

		if (allTask != null) {
			allTask.clear();
			allTask = null;
		}
	}
}
