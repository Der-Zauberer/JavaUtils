package eu.derzauberer.javautils.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class DataUtil2 {
	
	public static boolean getBoolean(Object object) {
		if (object != null) {
			if (object instanceof Boolean) return (Boolean) object; 
			if (object instanceof Number) {
				return (((Number) object).intValue() > 0);
			} else if (object instanceof String) {
				if (object.toString().equalsIgnoreCase("true")) return true;
				else if (object.toString().equalsIgnoreCase("false")) return false;
				else if (isNumericString(object.toString())) {
					return (Double.parseDouble(object.toString().replace("\"", "")) == 0);
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Number> T getNumber(Object object, Class<T> clazz) {
		final Class<T> numberClass = (Class<T>) getWrapperFromPrimitive(clazz);
		if (object != null) {
			if (object instanceof Number) {
				if (numberClass == Number.class) return numberClass.cast(object);
				else if (numberClass == Byte.class) return numberClass.cast(new Byte(((Number)object).byteValue()));
				else if (numberClass == Short.class) return numberClass.cast(new Short(((Number)object).shortValue()));
				else if (numberClass == Integer.class) return numberClass.cast(new Integer(((Number)object).intValue()));
				else if (numberClass == Long.class) return numberClass.cast(new Long(((Number)object).longValue()));
				else if (numberClass == Float.class) return numberClass.cast(new Float(((Number)object).floatValue()));
				else if (numberClass == Double.class) return numberClass.cast(new Double(((Number)object).doubleValue()));
			} else if (object instanceof Boolean) {
				if ((boolean) object) return getNumber(1, numberClass);
				else return getNumber(0, numberClass);
			} else if (object instanceof Character) {
				return getNumber(new Integer((Character) object).intValue(), numberClass);
			} else if (object instanceof String) {
				if (object.toString().equalsIgnoreCase("true")) {
					return getNumber(1, numberClass);
				} else if (object.toString().equalsIgnoreCase("false")) {
					return getNumber(0, numberClass);
				} else if (isNumericString(object.toString())) {
					try {
						return getNumber(Double.parseDouble(object.toString()), numberClass);
					} catch (NumberFormatException exception) {
						return getNumber(0, numberClass);
					}
				}
			}
		}
		return getNumber(0, numberClass);
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
		if (clazz == Object.class) return clazz.cast(object);
		final Class<T> objectClass = (Class<T>) getWrapperFromPrimitive(clazz);
		if (object.getClass() == objectClass) return objectClass.cast(object);
		T value = null;
		if (objectClass == Boolean.class) {
			value = objectClass.cast(getBoolean(object));
		} else if (objectClass == Number.class || objectClass == Byte.class || objectClass == Short.class || objectClass == Integer.class || objectClass == Long.class || objectClass == Float.class || objectClass == Double.class) {
			value = objectClass.cast(getNumber(object, (Class<? extends Number>) objectClass));
		} else if (objectClass == Character.class) {
			value = objectClass.cast(getCharacter(object));
		} else if (objectClass == String.class) {
			value = objectClass.cast(object.toString());
		} else {
			throw new IllegalArgumentException("Type " + objectClass.toGenericString() + " is not an allowed type!");
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
	
	public static boolean isInstanceOfPrimitiveType(Object object) {
		return object instanceof Boolean || object instanceof Byte || object instanceof Short || object instanceof Integer || object instanceof Long || object instanceof Float || object instanceof Double || object instanceof Character || object instanceof String;
	}
	
	public static boolean isBooleanString(String string) {
		return string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false");
	}
	
	public static boolean isNumericString(String string) {
		final Pattern numericRegex = Pattern.compile(
		        "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
		        "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
		        "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
		        "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");
		return numericRegex.matcher(string).matches();
	}
	
	public static boolean isIntegerString(String string) {
		return string.matches("^(-?|\\+?)\\d+$");
	}
	
	public static boolean isValidIdName(String string) {
		return string.matches("^([a-zA-Z0-9]|-|_)+$");
	}
	
	public static boolean isValidName(String string) {
		return string.matches("^(\\d|\\w|[ -_+^°!§$%&\\/\\\\()=?#'*~|\\x{00DC}\\x{00C4}\\x{00D6}\\x{00E4}\\x{00F6}\\x{00FC}])+$");
	}
	
	public static String addEscapeCodes(String string) {
		String result = string;
		result = result.replace("\\\"", "\"");
		result = result.replace("\\b", "\b");
		result = result.replace("\\n", "\n");
		result = result.replace("\\r", "\r");
		result = result.replace("\\t", "\t");
		return result;
	}
	
	public static String removeEscapeCodes(String string) {
		String result = string;
		result = result.replace("\"", "\\\"");
		result = result.replace("\b", "\\b");
		result = result.replace("\n", "\\n");
		result = result.replace("\r", "\\r");
		result = result.replace("\t", "\\t");
		return result;
	}
	
}
