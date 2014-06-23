package cn.shrek.base.util.rest;

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
import cn.shrek.base.ui.ZWActivity;
import cn.shrek.base.util.ZWCache;
import cn.shrek.base.util.ZWLogger;

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

	public static int NO_CACHE = -0x01;

	private static ZWCache restCache; // 用于请求的
	/**
	 * taskGuid用于识别task是否为同一个
	 * taskUUID 为任务的UUID
	 */
	private String taskGuid,taskUUID;
	public WeakReference<Context> ctx;
	// 请求处理器
	private AsyncTaskHandler<PARSEOBJ> handler;

	private AtomicBoolean isCancel;
	// private Class<PARSEOBJ> parseObjClazz;
	private TypeReference<PARSEOBJ> reference;
	// 缓存保存时间(单位:s) 如果为NO_CACHE 不开启缓存
	public int cacheSaveTime;

	/**
	 * 自带请求的config 在队列时候用
	 */
	public ZWRequestConfig config;
	private Queue<ZWAsyncTask<?>> allTask;

	public ZWAsyncTask(Context ctx, AsyncTaskHandler<PARSEOBJ> handler) {
		this.ctx = new WeakReference<Context>(ctx);
		this.handler = handler;
		isCancel = new AtomicBoolean(false);
		cacheSaveTime = NO_CACHE;
		if (handler != null) {
			handler.setTask(this);
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
				if(task.config != null){
					task.taskGuid = task.config.getUniqueKey();
				}
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
	public static <T> void excuteTaskWithMap(Context ctx, String url,
			HttpMethod method, TypeReference<T> reference,AsyncTaskHandler<T> handler,
			Map<String, String> paras) {
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
		task.taskGuid = config.getUniqueKey();
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
			HttpMethod method,TypeReference<T> reference, AsyncTaskHandler<T> handler, Object... paras) {
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
		task.taskGuid = config.getUniqueKey();
		task.execute(config);
	}

	public static <T> void excuteTaskWithParas(Context ctx, String url,
			AsyncTaskHandler<? extends T> handler, Object... paras) {
		excuteTaskWithParas(ctx, url, null, (AsyncTaskHandler<T>) handler,
				paras);

	}

	public static <T> void excuteTaskWithMap(Context ctx, String urlWithoutPar,
			HttpMethod method, TypeReference<T> reference,
			AsyncTaskHandler<? extends T> handler) {
		excuteTaskWithMap(ctx, urlWithoutPar, method,reference,
				(AsyncTaskHandler<T>) handler, null);
	}

	public static <T> void excuteTaskWithMap(Context ctx, String urlWithoutPar,TypeReference<T> reference,
			AsyncTaskHandler<? extends T> handler) {
		excuteTaskWithMap(ctx, urlWithoutPar, null,reference,
				(AsyncTaskHandler<T>) handler, null);
	}

	// public static <T> void excuteTask(Context ctx,String
	// urlWithoutPar,Class<T> clazz, AsyncTaskHandler<T> handler) {
	// excuteTask(ctx,urlWithoutPar,null,clazz,null,handler);
	// }

	@Override
	protected void onPreExecute() {
		// 任务执行前
		super.onPreExecute();
		taskUUID = UUID.randomUUID().toString();
		if(taskGuid == null){
			taskGuid = taskUUID;
		}
		
		if(judgeTaskValid()){
			Context context = ctx.get();
			if (context instanceof ZWActivity) {
				if(!((ZWActivity) context).addTask(this)){
					isCancel.set(true);
					setCancel(true);
					cycle();
					return;
				}
				
			}
			if ( handler != null) {
				handler.preDoing();
			}
		}
		
		ZWLogger.printLog(TAG, "任务开始,任务ID为:" + taskGuid+"  任务UUID为:"+taskUUID);
		timeingMap.put(taskUUID, System.currentTimeMillis());
	}
	
	

	@Override
	protected ZWResult<PARSEOBJ> doInBackground(ZWRequestConfig... params) {
		// TODO Auto-generated method stub
		ZWRequestConfig config = params[0];
		this.config = config;
		if (config == null) {
			throw new NullPointerException(
					"excute task parameter（ZWRequestConfig） must not null");
		}
		// 判断 task是否被终结
		if (!judgeTaskValid()) {
			return null;
		}

		ZWResult<PARSEOBJ> r = new ZWResult<PARSEOBJ>();

		Context context = ctx.get();
		// 判断是否开启缓存
		if (isOpenCache()) {
			ZWCache cache = getRestCache(context);
			PARSEOBJ parserObj = cache.getAsJsonObject(config.getUniqueKey(),
					reference);
			if (parserObj != null) {
				r.bodyObj = parserObj;
				ZWLogger.printLog(this, "有效的缓存数据,不必请求网络!");
				return r;
			}else{
				ZWLogger.printLog(this, "缓存数据已经超时,需请求网络!");
			}
		}

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
			// 设置超时
			ClientHttpRequestFactory requestFactory = restTemplate
					.getRequestFactory();
			if (requestFactory instanceof HttpComponentsClientHttpRequestFactory) {
				HttpComponentsClientHttpRequestFactory mComponentsClientHttpRequestFactory = (HttpComponentsClientHttpRequestFactory) requestFactory;
				mComponentsClientHttpRequestFactory
						.setConnectTimeout(config.connTimeOut);
				mComponentsClientHttpRequestFactory
						.setReadTimeout(config.readTimeOut);
			}
			// requestFactory.
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
			
//			if(reference.getType().getClass().isAssignableFrom(String.class)){
//				r.bodyObj = (PARSEOBJ) result;
//			}else{
//				
//			}
			r.bodyObj = JSON.parseObject(result, reference);		
			r.errorException = null;
		} catch (Exception e) {
			// TODO: handle exception
			// e.printStackTrace();
			ZWLogger.printLog(TAG, "请求出现异常:" + e.toString());
			r.errorException = e;
			// return null;
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
				// 出现错误的时候
				handler.postError(result, result.errorException);
				return;
			}
			// 判断是否开始缓存
			if (isOpenCache() && result.bodyObj != null) {
				ZWCache cache = getRestCache(ctx.get());
				cache.putJson(config.getUniqueKey(), result.bodyObj,
						cacheSaveTime);
				ZWLogger.printLog(this, "网络的数据已缓存,缓存时间为:"+cacheSaveTime+"秒");
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
			
			ZWLogger.printLog(
					TAG,
					"任务Over,任务ID为:"
							+ taskGuid
							+ "  耗时:"
							+ (System.currentTimeMillis() - timeingMap
									.remove(taskUUID)) + "毫秒!");
		}

		
	}

	public final boolean judgeTaskValid() {
		if(ctx == null){
			return false;
		}
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
	 * 判断是否开启缓存
	 * @return
	 */
	public boolean isOpenCache(){
		return cacheSaveTime != NO_CACHE;
	}
	
	/**
	 * 得到 缓存 管理类
	 * 
	 * @param ctx
	 * @return
	 */
	public static ZWCache getRestCache(Context ctx) {
		if (restCache == null) {
			restCache = ZWCache.get(ctx);
		}
		return restCache;
	}

	// public static void setRestCache(ZWCache restCache) {
	// ZWApplication.restCache = restCache;
	// }
	
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof ZWAsyncTask){
			ZWAsyncTask<?> objTask = (ZWAsyncTask)o;
			if(taskGuid != null && taskGuid.equals(objTask.taskGuid)){
				return true;
			}
		}
		return false;
	}

	
	public String getTaskGuid() {
		return taskGuid;
	}

	public void setTaskGuid(String taskGuid) {
		this.taskGuid = taskGuid;
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
