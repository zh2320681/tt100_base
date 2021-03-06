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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.shrek.base.annotation.Subscribe;
import cn.shrek.base.ui.inject.Injector;

/**
 * Wraps a single-argument 'handler' method on a specific object.
 */
class EventHandler {

	/** Object sporting the handler method. */
	private final Object target;
	/** Handler method. */
	private final Method method;
	/** Object hash code. */
	private final int hashCode;
	/** Should this handler receive events? */
	private boolean valid = true;

	EventHandler(Object target, Method method) {
		if (target == null) {
			throw new NullPointerException(
					"EventHandler target cannot be null.");
		}
		if (method == null) {
			throw new NullPointerException(
					"EventHandler method cannot be null.");
		}

		this.target = target;
		this.method = method;
		method.setAccessible(true);

		// Compute hash code eagerly since we know it will be used frequently
		// and we cannot estimate the runtime of the
		// target's hashCode call.
		final int prime = 31;
		hashCode = (prime + method.hashCode()) * prime + target.hashCode();
	}

	public boolean isValid() {
		return valid;
	}

	/**
	 * If invalidated, will subsequently refuse to handle events.
	 *
	 * Should be called when the wrapped object is unregistered from the Bus.
	 */
	public void invalidate() {
		valid = false;
	}
	
	public ThreadMode getThreadMode(){
		Subscribe sub =method.getAnnotation(Subscribe.class);
		return sub.threadMode();
	}

	/**
	 * Invokes the wrapped handler method to handle {@code event}.
	 *
	 * @param event
	 *            event to handle
	 * @throws java.lang.IllegalStateException
	 *             if previously invalidated.
	 * @throws java.lang.reflect.InvocationTargetException
	 *             if the wrapped method throws any {@link Throwable} that is
	 *             not an {@link Error} ({@code Error}s are propagated as-is).
	 */
	public Object handleEvent(ZWEvent event) throws InvocationTargetException {
		if (!valid) {
			throw new IllegalStateException(toString()
					+ " has been invalidated and can no longer handle events.");
		}
		
		Subscribe sub =method.getAnnotation(Subscribe.class);
		
		Class<?>[] paraTypes = method.getParameterTypes();
		Object[] objs = new Object[paraTypes.length];
		for (int i = 0; i < paraTypes.length; i++) {
			Class<?> paraType = paraTypes[i];
			//如果是event类型
			if(ZWEvent.class.isAssignableFrom(paraType)){
				objs[i] = event;
				continue;
			}
			
			//如果是para类型 说明要参数
			if(ZWEventPara.class.isAssignableFrom(paraType)){
				objs[i] = event.obj;
				continue;
			}
			
			//如果需要注入
			if(sub.isInjectParas()){
				objs[i] = Injector.instance().getDefaultInstance(paraType);
				continue;
			}
			objs[i] = null;
		}
		
		Object obj;
		try {
			obj = method.invoke(target, objs);
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof Error) {
				throw (Error) e.getCause();
			}
			throw e;
		}
		return obj;
	}

	@Override
	public String toString() {
		return "[EventHandler " + method + "]";
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final EventHandler other = (EventHandler) obj;

		return method.equals(other.method) && target == other.target;
	}

}
