package net.itsmware.common.rpc.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author aol_aog@163.com 2014-11-13
 */
public class ReflectionCache {
	private static final Map<String, Class<?>> PRIMITIVE_CLASS = new HashMap<String, Class<?>>();

	private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<String, Class<?>>(
			128);

	private static final Map<String, Method> METHOD_CACHE = new HashMap<String, Method>(
			1024);
	static {
		PRIMITIVE_CLASS.put("boolean", boolean.class);
		PRIMITIVE_CLASS.put("byte", byte.class);
		PRIMITIVE_CLASS.put("short", short.class);
		PRIMITIVE_CLASS.put("int", int.class);
		PRIMITIVE_CLASS.put("long", long.class);
		PRIMITIVE_CLASS.put("long", long.class);
		PRIMITIVE_CLASS.put("float", float.class);
		PRIMITIVE_CLASS.put("double", double.class);
		PRIMITIVE_CLASS.put("void", void.class);

		CLASS_CACHE.putAll(PRIMITIVE_CLASS);
	}

	public static Class<?> getClass(String className)
			throws ClassNotFoundException {
		Class<?> clazz = CLASS_CACHE.get(className);
		if (null != clazz) {
			return clazz;
		}
		synchronized (CLASS_CACHE) {
			if (null == CLASS_CACHE.get(className)) {
				clazz = PRIMITIVE_CLASS.get(className);
				if (null == clazz) {
					clazz = Class.forName(className);
				}
				CLASS_CACHE.put(className, clazz);
				return clazz;
			} else {
				return CLASS_CACHE.get(className);
			}
		}
	}

	public static Method getMethod(String className, String methodName,
			Class[] parameterClasses) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException {
		String key = className + "-" + methodName + "-"
				+ join(parameterClasses, ";");
		Method method = METHOD_CACHE.get(key);
		if (null != method) {
			return method;
		}
		synchronized (METHOD_CACHE) {
			if (null == METHOD_CACHE.get(key)) {
				Class<?> clazz = getClass(className);
				method = clazz.getMethod(methodName, parameterClasses);
				METHOD_CACHE.put(key, method);
				return method;
			} else {
				return METHOD_CACHE.get(key);
			}
		}
	}

	private static String join(Class[] strs, String seperator) {
		if (null == strs || 0 == strs.length) {
			return "";
		}
		StringBuilder sb = new StringBuilder(1024);
		sb.append(strs[0]);
		for (int i = 1; i < strs.length; i++) {
			sb.append(seperator).append(strs[i].getName());
		}
		return sb.toString();
	}
}
