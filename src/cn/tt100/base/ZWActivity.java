package cn.tt100.base;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import cn.tt100.base.annotation.AutoInitialize;
import cn.tt100.base.annotation.AutoOnClick;
import cn.tt100.base.annotation.LayoutSelector;
import cn.tt100.base.annotation.OberverLoad;
import cn.tt100.base.exception.ZWAppException;
import cn.tt100.base.util.BaseUtil;
import cn.tt100.base.util.ZWLogger;
import cn.tt100.base.util.net.ZWNetWorkUtil.NetType;
import cn.tt100.base.util.rest.ZWAsyncTask;

public abstract class ZWActivity extends Activity implements Observer {
	// private LinkedList<WeakReference<ZWAsyncTask<?>>> taskList;
	private Collection<ZWAsyncTask<?>> taskList;

	private static String packageName;
	private String activityName;

	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		ZWAppException.getInstance(this);
		if (packageName == null) {
			packageName = getPackageName();
		}

		activityName = getClass().getSimpleName();
		Application app = getApplication();
		if (app instanceof ZWApplication) {
			ZWApplication zwApp = (ZWApplication) app;
			zwApp.mActivityManager.pushActivity(this);
		}

		if (ZWApplication.isLoadRestRequest) {
			taskList = ZWApplication.isInterceptSameRequest ? new HashSet<ZWAsyncTask<?>>()
					: new LinkedList<ZWAsyncTask<?>>();
		}

		onBaseCreate(savedInstanceState);
		loadField();

		initialize();
		addListener();

	}

	protected void onBaseCreate(Bundle savedInstanceState) {
		Class<? extends Activity> clazz = getClass();
		LayoutSelector selector = clazz.getAnnotation(LayoutSelector.class);
		try {
			if (selector != null) {
				setContentView(selector.id());
			} else {
				setContentView(getResources().getIdentifier(
						activityName.toLowerCase().replace("activity", ""),
						"layout", packageName));
			}
		} catch (Exception e) {
			// ���ò���ʧ��
			ZWLogger.printLog(activityName, "Activity����:" + activityName
					+ "���ز���ʧ��!");
			e.printStackTrace();
		}
	}

	/**
	 * ��ʼ��
	 */
	protected abstract void initialize();

	/**
	 * ��Ӽ�����
	 */
	protected abstract void addListener();

	private final void loadField() {
		// Class<? extends View> viewClazz = Class.forName("android.view.View");

		Class<? extends Activity> clazz = getClass();
		//�õ���������(����������)
		Field[] declaredFields = clazz.getDeclaredFields();
		Field[] publicFields = clazz.getFields();
		List<Field> allFields = new ArrayList<Field>();
		for(Field f : declaredFields){
			allFields.add(f);
		}
		
		for(Field f : publicFields){
			boolean isExist = false;
			for(int i = 0;i<declaredFields.length;i++){
				if(declaredFields[i].getName().equals(f.getName())){
					isExist = true;
					break;
				}
			}
			
			if(!isExist){
				allFields.add(f);
			}
		}
		
		for (Field f : allFields) {
			if(View.class.isAssignableFrom(f.getType())){
				//view������
				// �Զ���ʼ��
				AutoInitialize autoInitialize = f
						.getAnnotation(AutoInitialize.class);
				if (autoInitialize != null) {
					String idFormat = autoInitialize.idFormat().replace("?",
							f.getName()); // main_textBtn
					int value = getIdValueIntoR(idFormat);
					f.setAccessible(true);
					try {
						f.set(this, findViewById(value));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						ZWLogger.printLog(this, f.getName() + "��ֵʧ��!");
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						ZWLogger.printLog(this, f.getName() + "��ֵʱ����ʧ��!");
					}
				}

				// �Զ�����OnClcikListener
				AutoOnClick autoOnClick = f.getAnnotation(AutoOnClick.class);
				if (autoOnClick != null) {
					Class<?> viewClazz = f.getType();
					// Object subView = f.get(this);
					Method clickMethod = null;
					try {
						clickMethod = viewClazz.getMethod("setOnClickListener",
								OnClickListener.class);
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						ZWLogger.printLog(this, f.getName()
								+ "����setOnClickListener()����ʧ��!");
					}
					if (clickMethod != null) {
						Field clickField;
						try {
							clickField = clazz.getDeclaredField(autoOnClick
									.clickSelector());
							clickField.setAccessible(true);
							clickMethod.invoke(f.get(this), clickField.get(this));
						} catch (NoSuchFieldException e) {
							// TODO Auto-generated catch block
							ZWLogger.printLog(this,
									"û���ҵ�����:" + autoOnClick.clickSelector());
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							ZWLogger.printLog(BaseUtil.class, f.getName() + "��ֵʧ��!");
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							ZWLogger.printLog(BaseUtil.class, f.getName()
									+ "��ֵʱ����ʧ��!");
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

				// �Զ����ù۲��
				OberverLoad oberverLoad = f.getAnnotation(OberverLoad.class);
				if (oberverLoad != null) {
					f.setAccessible(true);
					Object obj;
					try {
						obj = f.get(this);
						ModelObservable mModelObservable = new ModelObservable(obj);
						mModelObservable.addObserver(this);
						ObserverContainer.addObservable(f.getName(),
								mModelObservable);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						ZWLogger.printLog(BaseUtil.class, f.getName() + "��ֵʧ��!");
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						ZWLogger.printLog(BaseUtil.class, f.getName() + "��ֵʱ����ʧ��!");
					}
				}
			}else if(LayoutInflater.class.isAssignableFrom(f.getType())){
				//LayoutInflater ����
				AutoInitialize autoInitialize = f
						.getAnnotation(AutoInitialize.class);
				if (autoInitialize != null) {
					f.setAccessible(true);
					try {
						f.set(this, LayoutInflater.from(getApplicationContext()));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						ZWLogger.printLog(this, f.getName() + "��ֵʧ��!");
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						ZWLogger.printLog(this, f.getName() + "��ֵʱ����ʧ��!");
					}
				}
			}
			
		}
	}

	private int getIdValueIntoR(String idName) {
		// Class clazz = R.id.class;
		// int value = -1;
		// try {
		// Field f = clazz.getField(idName);
		// value = f.getInt(null);
		// } catch (NoSuchFieldException e) {
		// System.out.println("δ�ҵ�id:["+idName+"]�Ŀؼ�");
		// } catch (Exception e) {
		// System.out.println("δ�ҵ�id:["+idName+"]�Ŀؼ�");
		// }
		// return value;
		return getResources().getIdentifier(idName, "id", packageName);
	}

	@Override
	public final void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		if (observable instanceof ModelObservable) {
			ModelObservable mModelObservable = (ModelObservable) observable;
			notifyObserver(mModelObservable.getData(), data);
			mModelObservable.setData(data);
		}

	}

	/**
	 * �������
	 * 
	 * @param task
	 */
	public boolean addTask(ZWAsyncTask<?> task) {
		if(!ZWApplication.isInterceptSameRequest){
			taskList.add(task);
			return true;
		}
		
		if (ZWApplication.isLoadRestRequest) {
			boolean isExist = false;
			for (ZWAsyncTask<?> zwTask : taskList) {
				if (zwTask.equals(task)) {
					isExist = true;
					break;
				}
			}
			if (isExist) {
				ZWLogger.printLog(this, "����:" + task.getTaskGuid()
						+ "�Ѿ�������,������ִ��!");
			} else {
				taskList.add(task);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Application app = getApplication();
		if (app instanceof ZWApplication) {
			ZWApplication zwApp = (ZWApplication) app;
			zwApp.mActivityManager.popActivity(this);
		}
		clearTaskList();
	}

	public void clearTaskList() {
		for (ZWAsyncTask<?> task : taskList) {
			if (task != null) {
				task.cancel(true);
			}
		}
		taskList.clear();
	}

	/**
	 * �Ƴ�����
	 * 
	 * @param task
	 */
	public void removeTask(ZWAsyncTask<?> task) {
		if (ZWApplication.isLoadRestRequest) {
			taskList.remove(task);
		}
	}

	public abstract void notifyObserver(Object oldObj, Object newObj);

	/**
	 * ������������ʱ����
	 */
	public void onConnect(NetType type) {

	}

	/**
	 * ��ǰû����������
	 */
	public void onDisConnect() {

	}
}
