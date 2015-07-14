/*
 * Copyright (C) 2012 Square, Inc.
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.shrek.base.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.shrek.base.ZWConstants;
import cn.shrek.base.annotation.Produce;
import cn.shrek.base.annotation.Subscribe;

final class AnnotatedHandlerFinder {

	/** Cache event bus subscriber methods for each class. */
	private static final Map<Class<?>, Map<ZWEvent, Set<Method>>> SUBSCRIBERS_CACHE = new HashMap<Class<?>, Map<ZWEvent, Set<Method>>>();
//	private static final Map<Class<?>, Map<ZWEvent, Method>> INTERCEPTOR_CACHE = new HashMap<Class<?>, Map<ZWEvent, Method>>();
	/**
	 * Load all methods annotated with {@link Produce} or {@link Subscribe} into
	 * their respective caches for the specified class.
	 */
	private static void loadAnnotatedMethods(Class<?> listenerClass) {
		Map<ZWEvent, Set<Method>> subscriberMethods = new HashMap<ZWEvent, Set<Method>>();

		for (Method method : listenerClass.getDeclaredMethods()) {
			
			if (method.isBridge()) {
				continue;
			}
			
			if (method.isAnnotationPresent(Subscribe.class)) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				Subscribe sub = method.getAnnotation(Subscribe.class);

				// 是否包含 ZWEventPara接口作为参数
				boolean isContainEventPara = false;
				// 参数里面是否包含接口 如果自动注入 无法建实例
				boolean isContainInterface = false;
				
				Class<? extends ZWEventPara> clazz = null;
				
				for (Class<?> paraType : parameterTypes) {
					if (ZWEventPara.class.isAssignableFrom(paraType)) {
						isContainEventPara = true;
						clazz = (Class<? extends ZWEventPara>) paraType;
						break;
					}

					if (paraType.isInterface()) {
						isContainInterface = true;
					}
				}

				// 方法参数只有1个
				if (sub.tag().equals(ZWConstants.NULL_STR_VALUE)
						&& sub.flag() == ZWConstants.NULL_INT_VALUE
						&& !isContainEventPara) {
					throw new IllegalArgumentException("Method " + method
							+ " 不合法,必须设置标识或者 ZWEventPara实现类");
				}

				// 参数能为接口  但是必须要开启注入
				if (!sub.isInjectParas() && isContainInterface) {
					throw new IllegalArgumentException("Method " + method
							+ "未设置自动注入,不能给接口参数实例化!");
				}

				// 方法public
				if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
					throw new IllegalArgumentException("Method " + method
							+ "  is not 'public'.");
				}

				ZWEvent event = ZWEvent.obtain(sub.tag(), sub.flag(), clazz);
				
				Set<Method> methods = subscriberMethods.get(event);
				if (methods == null) {
					methods = new HashSet<Method>();
					subscriberMethods.put(event, methods);
				}
				methods.add(method);
			}
		}

		SUBSCRIBERS_CACHE.put(listenerClass, subscriberMethods);
	}

	/**
	 * This implementation finds all methods marked with a {@link Subscribe}
	 * annotation.
	 */
	static Map<ZWEvent, Set<EventHandler>> findAllSubscribers(Object listener) {
		Class<?> listenerClass = listener.getClass();
		Map<ZWEvent, Set<EventHandler>> handlersInMethod = new HashMap<ZWEvent, Set<EventHandler>>();

		if (!SUBSCRIBERS_CACHE.containsKey(listenerClass)) {
			loadAnnotatedMethods(listenerClass);
		}
		Map<ZWEvent, Set<Method>> methods = SUBSCRIBERS_CACHE.get(listenerClass);
		if (!methods.isEmpty()) {
			for (Map.Entry<ZWEvent, Set<Method>> e : methods.entrySet()) {
				Set<EventHandler> handlers = new HashSet<EventHandler>();
				for (Method m : e.getValue()) {
					handlers.add(new EventHandler(listener, m));
				}
				handlersInMethod.put(e.getKey(), handlers);
			}
		}

		return handlersInMethod;
	}

	
	
	private static Map<ZWEvent, Method> getInterceptorMethods(Class<?> listenerClass) {
		Map<ZWEvent, Method> interceptor = new HashMap<ZWEvent,Method>();

		for (Method method : listenerClass.getDeclaredMethods()) {
			if (method.isBridge()) {
				continue;
			}
			if (method.isAnnotationPresent(Subscribe.class)) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				Subscribe sub = method.getAnnotation(Subscribe.class);

				// 是否包含 ZWEventPara接口作为参数
				boolean isContainEventPara = false;
				// 参数里面是否包含接口 如果自动注入 无法建实例
				boolean isContainInterface = false;
				
				Class<? extends ZWEventPara> clazz = null;
				
				for (Class<?> paraType : parameterTypes) {
					if (ZWEventPara.class.isAssignableFrom(paraType)) {
						isContainEventPara = true;
						clazz = (Class<? extends ZWEventPara>) paraType;
						break;
					}

					if (paraType.isInterface()) {
						isContainInterface = true;
					}
				}

				// 方法参数只有1个
				if (sub.tag().equals(ZWConstants.NULL_STR_VALUE)
						&& sub.flag() == ZWConstants.NULL_INT_VALUE
						&& !isContainEventPara) {
					throw new IllegalArgumentException("Method " + method
							+ " 不合法,必须设置标识或者 ZWEventPara实现类");
				}

				// 参数能为接口  但是必须要开启注入
				if (!sub.isInjectParas() && isContainInterface) {
					throw new IllegalArgumentException("Method " + method
							+ "未设置自动注入,不能给接口参数实例化!");
				}

				// 方法public
				if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
					throw new IllegalArgumentException("Method " + method
							+ "  is not 'public'.");
				}
				
				Class<?> returnClazz = method.getReturnType();
				if(!returnClazz.isAssignableFrom(Boolean.class)
						&&  !returnClazz.isAssignableFrom(boolean.class)){
					throw new IllegalArgumentException("Method " + method
							+ "  return type must boolean!");
				}

				ZWEvent event = ZWEvent.obtain(sub.tag(), sub.flag(), clazz);
				
				if(interceptor.containsKey(event)){
					throw new IllegalArgumentException(event.toString()+" 已经注册了拦截器,请勿重复注册!");
				}
				
				interceptor.put(event, method);
			}
		}
		return interceptor;
	}

	
	/**
	 * This implementation finds all methods marked with a {@link Subscribe}
	 * annotation.
	 */
	static Map<ZWEvent, EventHandler> findAllInterceptors(Object listener) {
		Class<?> listenerClass = listener.getClass();
		Map<ZWEvent,EventHandler> handlersInMethod = new HashMap<ZWEvent,EventHandler>();

		Map<ZWEvent, Method> methods = getInterceptorMethods(listenerClass);
		
		if (!methods.isEmpty()) {
			for (Map.Entry<ZWEvent, Method> entry : methods.entrySet()) {
				EventHandler handler = new EventHandler(listener, entry.getValue());
				handlersInMethod.put(entry.getKey(), handler);
			}
		}
		return handlersInMethod;
	}
	
	private AnnotatedHandlerFinder() {
		// No instances.
	}

}
