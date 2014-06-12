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
	// ���ڼ�ʱ
	private static final Map<String, Long> timeingMap = new HashMap<String, Long>();

	public static final byte PRE_TASK_NORMAL = 0x01;
	public static final byte PRE_TASK_CUSTOM = 0x02;
	public static final byte PRE_TASK_DONOTHING = 0x03;

	public static int NO_CACHE = -0x01;

	private static ZWCache restCache; // ���������
	/**
	 * taskGuid����ʶ��task�Ƿ�Ϊͬһ��
	 * taskUUID Ϊ�����UUID
	 */
	private String taskGuid,taskUUID;
	public WeakReference<Context> ctx;
	// ��������
	private AsyncTaskHandler<PARSEOBJ> handler;

	private AtomicBoolean isCancel;
	// private Class<PARSEOBJ> parseObjClazz;
	private TypeReference<PARSEOBJ> reference;
	// ���汣��ʱ��(��λ:s) ���ΪNO_CACHE ����������
	public int cacheSaveTime;

	/**
	 * �Դ������config �ڶ���ʱ����
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
	 * ��������ӵ�������,�����ض��еĵ�һ������ִ��
	 * 
	 * @param tasks
	 * @return
	 */
	public static void addTaskIntoQueueAndExcute(ZWAsyncTask<?>... tasks) {
		Queue<ZWAsyncTask<?>> allTask = new LinkedList<ZWAsyncTask<?>>();
		for (ZWAsyncTask<?> task : tasks) {
			if (task.config == null) {
				throw new NullPointerException("�����������������ǰ���� Config!");
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
	 * ��̬�� ���󷽷�
	 * 
	 * @param ctx
	 * @param url
	 *            ��ַ ������http://example.com/hotels/{hotel}/bookings/{booking}
	 * @param method
	 *            GET POST PUT��
	 * @param clazz
	 *            ����ʲô����
	 * @param paras
	 *            URL����
	 * @param handler
	 *            ����ص���
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
	 * ��̬�� ���󷽷�
	 * 
	 * @param ctx
	 * @param url
	 *            ��ַ ������http://example.com/hotels/{hotel}/bookings/{booking}
	 * @param method
	 *            GET POST PUT��
	 * @param clazz
	 *            ����ʲô����
	 * @param paras
	 *            URL����
	 * @param handler
	 *            ����ص���
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
		// ����ִ��ǰ
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
		
		ZWLogger.printLog(TAG, "����ʼ,����IDΪ:" + taskGuid+"  ����UUIDΪ:"+taskUUID);
		timeingMap.put(taskUUID, System.currentTimeMillis());
	}
	
	

	@Override
	protected ZWResult<PARSEOBJ> doInBackground(ZWRequestConfig... params) {
		// TODO Auto-generated method stub
		ZWRequestConfig config = params[0];
		this.config = config;
		if (config == null) {
			throw new NullPointerException(
					"excute task parameter��ZWRequestConfig�� must not null");
		}
		// �ж� task�Ƿ��ս�
		if (!judgeTaskValid()) {
			return null;
		}

		ZWResult<PARSEOBJ> r = new ZWResult<PARSEOBJ>();

		Context context = ctx.get();
		// �ж��Ƿ�������
		if (isOpenCache()) {
			ZWCache cache = getRestCache(context);
			PARSEOBJ parserObj = cache.getAsJsonObject(config.getUniqueKey(),
					reference);
			if (parserObj != null) {
				r.bodyObj = parserObj;
				ZWLogger.printLog(this, "��Ч�Ļ�������,������������!");
				return r;
			}else{
				ZWLogger.printLog(this, "���������Ѿ���ʱ,����������!");
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
			// ���ó�ʱ
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
			ZWLogger.printLog(TAG, "��������쳣:" + e.toString());
			r.errorException = e;
			// return null;
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
			if (result.errorException != null) {
				// ���ִ����ʱ��
				handler.postError(result, result.errorException);
				return;
			}
			// �ж��Ƿ�ʼ����
			if (isOpenCache() && result.bodyObj != null) {
				ZWCache cache = getRestCache(ctx.get());
				cache.putJson(config.getUniqueKey(), result.bodyObj,
						cacheSaveTime);
				ZWLogger.printLog(this, "����������ѻ���,����ʱ��Ϊ:"+cacheSaveTime+"��");
			}

			handler.postResult(result);

			Context context = ctx.get();
			if (context instanceof ZWActivity) {
				((ZWActivity) context).removeTask(this);
			}
			if (allTask != null) {
				if (allTask.size() == 0) {
					// �������������������

				} else {
					if (config == null) {
						throw new NullPointerException("�����������������ǰ���� Config!");
					}
					ZWLogger.printLog(TAG, "����������,����ִ��!");
					ZWAsyncTask<?> task = allTask.poll();
					task.execute(config);
				}
			} else {
				cycle();
			}
			
			ZWLogger.printLog(
					TAG,
					"����Over,����IDΪ:"
							+ taskGuid
							+ "  ��ʱ:"
							+ (System.currentTimeMillis() - timeingMap
									.remove(taskUUID)) + "����!");
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

	/**
	 * �ж��Ƿ�������
	 * @return
	 */
	public boolean isOpenCache(){
		return cacheSaveTime != NO_CACHE;
	}
	
	/**
	 * �õ� ���� ������
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
	 * �������
	 */
	private void cycle() {
		ctx.clear();
		ctx = null;
		// ��������
		handler = null;

		if (allTask != null) {
			allTask.clear();
			allTask = null;
		}
	}
}
