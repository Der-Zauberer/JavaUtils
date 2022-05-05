package eu.derzauberer.javautils.accessible;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class AccessibleMethod {
	
	private String name;
	private Object parent;
	private Method method;
	
	public AccessibleMethod(Object parent, Method method) {
		this.parent = parent;
		this.method = method;
		if (getAnnotation() != null) {
			method.setAccessible(true);
			if (!getAnnotation().name().isEmpty()) {
				name = getAnnotation().name();
			} else {
				name = method.getName();
			}
		} else {
			throw new AccessibleException("The method " + method.getName() + " does not have an @AccessableMethod annotation!");
		}
	}
	
	public Object invoke(Object... args) {
		try {
			return method.invoke(parent, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			throw new AccessibleException(exception.getMessage());
		}
	}
	
	public String getName() {
		return name;
	}
	
	public int getParaneterCount() {
		return getParaneterCount();
	}
	
	public Parameter[] getParameters() {
		return method.getParameters();
	}
	
	public Class<?>[] getParameterTypes() {
		return method.getParameterTypes();
	}
	
	public Type[] getGenericParameterTypes() {
		return method.getGenericParameterTypes();
	}
	
	public Class<?> getReturnType() {
		return method.getReturnType();
	}
	
	public Type getGenericReturnType() {
		return method.getGenericReturnType();
	}
	
	private eu.derzauberer.javautils.annotations.AccessibleMethod getAnnotation() {
		return method.getAnnotation(eu.derzauberer.javautils.annotations.AccessibleMethod.class);
	}

}
