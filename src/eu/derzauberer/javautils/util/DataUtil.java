package eu.derzauberer.javautils.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class DataUtil {
	
	public static boolean getBoolean(Object object) {
		if (object != null) {
			if (object instanceof Boolean) return (Boolean) object; 
			if (object instanceof Number) {
				if (((Number) object).intValue() > 0) return true; else return false;
			} else if (object instanceof String) {
				if (object.toString().equalsIgnoreCase("true")) return true;
				else if (object.toString().equalsIgnoreCase("false")) return false;
				else if (isNumericString(object.toString())) {
					if (Double.parseDouble(object.toString().replace("\"", "")) == 0) return false; else return true;
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Number> T getNumber(Object object, Class<T> clazz) {
		clazz = (Class<T>) getWrapperFromPrimitive(clazz);
		if (object != null) {
			if (object instanceof Number) {
				if (clazz == Number.class) return clazz.cast(object);
				else if (clazz == Byte.class) return clazz.cast(new Byte(((Number)object).byteValue()));
				else if (clazz == Short.class) return clazz.cast(new Short(((Number)object).shortValue()));
				else if (clazz == Integer.class) return clazz.cast(new Integer(((Number)object).intValue()));
				else if (clazz == Long.class) return clazz.cast(new Long(((Number)object).longValue()));
				else if (clazz == Float.class) return clazz.cast(new Float(((Number)object).floatValue()));
				else if (clazz == Double.class) return clazz.cast(new Double(((Number)object).doubleValue()));
			} else if (object instanceof Boolean) {
				if ((boolean) object) return getNumber(1, clazz);
				else return getNumber(0, clazz);
			} else if (object instanceof Character) {
				return getNumber(new Integer((Character) object).intValue(), clazz);
			} else if (object instanceof String) {
				if (object.toString().equalsIgnoreCase("true")) {
					return getNumber(1, clazz);
				} else if (object.toString().equalsIgnoreCase("false")) {
					return getNumber(0, clazz);
				} else if (isNumericString(object.toString())) {
					try {
						return getNumber(Double.parseDouble(object.toString()), clazz);
					} catch (NumberFormatException exception) {
						return getNumber(0, clazz);
					}
				}
			}
		}
		return getNumber(0, clazz);
	}
	
	public static char getCharacter(Object object) {
		if (object != null) {
			if (object instanceof Boolean) {
				if ((boolean) object) return 't'; else return 'f'; 
			} else if (object instanceof Number) {
				return (char) ((Number) object).intValue();
			} else if (object instanceof String && !((String) object).isEmpty()) {
				if (((String) object).length() > 1 && isNumericString((String) object)) return (char) getNumber(object, Integer.class).intValue();
				return ((String) object).toCharArray()[0];
			}
		}
		return ' ';
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getObject(Object object, Class<T> clazz) {
		clazz = (Class<T>) getWrapperFromPrimitive(clazz);
		if (object.getClass() == clazz) return clazz.cast(object);
		T value = null;
		if (clazz == Boolean.class) {
			value = clazz.cast(getBoolean(object));
		} else if (clazz == Number.class || clazz == Byte.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class || clazz == Float.class || clazz == Double.class) {
			value = clazz.cast(getNumber(object, (Class<? extends Number>) clazz));
		} else if (clazz == Character.class) {
			value = clazz.cast(getCharacter(object));
		} else if (clazz == String.class) {
			value = clazz.cast(object.toString());
		} else {
			throw new IllegalArgumentException("Type " + clazz.toGenericString() + " is not an allowed type!");
		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getList(Object object, Class<T> clazz) {
		try {
			return new ArrayList<>((Collection<T>)object);
		} catch (ClassCastException | NullPointerException exception) {
			return new ArrayList<>();
		}
	}
	
	public static <T> List<T> getPrimitiveTypeList(List<?> list, Class<T> clazz) {
		List <T> newList = new ArrayList<>();
		for (Object object : list) {
			newList.add(getObject(object, clazz));
		}
		return newList;
	}
	
	public static <T> List<T> getPrimitiveTypeList(Object object, Class<T> clazz) {
		if (object instanceof List<?>) {
			return getPrimitiveTypeList(getList(object, Object.class), clazz);
		}
		return null;
	}
	
	public static Class<?> getClassFromGenericTypeOfList(Type type) {
		try {
			String name = type.getTypeName();
			name = name.substring(name.indexOf("<") + 1, name.length() - 1);
			return Class.forName(name);
		} catch (ClassNotFoundException exception) {
			return null;
		}
	}
	
	public static Class<?> getWrapperFromPrimitive(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			if (clazz == boolean.class) return Boolean.class;
			else if (clazz == byte.class) return Byte.class;
			else if (clazz == short.class) return Short.class;
			else if (clazz == int.class) return Integer.class;
			else if (clazz == long.class) return Long.class;
			else if (clazz == float.class) return Float.class;
			else if (clazz == double.class) return Double.class;
			else if (clazz == char.class) return Character.class;
		}
		return clazz;
	}
	
	public static boolean isPrimitiveWrapperType(Class<?> clazz) {
		return clazz == Boolean.class || clazz == Byte.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class || clazz == Float.class || clazz == Double.class || clazz == Character.class || clazz == String.class;
	}
	
	public static boolean isPrimitiveType(Class<?> clazz) {
		return clazz.isPrimitive() || clazz == Boolean.class || clazz == Byte.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class || clazz == Float.class || clazz == Double.class || clazz == Character.class || clazz == String.class;
	}
	
	public static boolean isBooleanString(String string) {
		return string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false");
	}
	
	public static boolean isNumericString(String string) {
		Pattern numericRegex = Pattern.compile(
		        "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
		        "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
		        "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
		        "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");
		return numericRegex.matcher(string).matches();
	}
	
	public static boolean isIntegerString(String string) {
		return string.matches("^(-?|\\+?)\\d+$");
	}
	
}
