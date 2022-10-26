package eu.derzauberer.javautils.util;

public class DataUtil {

	/**
	 * Converts any object into a specific type. Supported types are primitive types
	 * and {@link String}. The method returns null if the input object is also null.
	 * It will return the same object back if the input is an instance of the class
	 * but will throw a {@link ClassCastException} if the class is an unsupported
	 * data type.
	 * @param <T>    the type into which the object should be converted
	 * @param object the object input of the convert function
	 * @param type  the class into which the object should be converted
	 * @return the converted object
	 * @throws ClassCastException thrown when it is not possible to convert the
	 *                            object into the given type
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convert(final Object object, final Class<T> type) {
		if (object == null) {
			return null;
		} else if (object.getClass() == type || type.isAssignableFrom(object.getClass())) {
			return (T) object;
		} else if (Number.class.isAssignableFrom(type) 
				|| type == byte.class || type == short.class || type == int.class || 
				type == long.class || type == float.class || type == double.class) {
			return (T) convertNumber(object, (Class<? extends Number>) type);
		} else if (type == Boolean.class || type == boolean.class) {
			if (object instanceof Boolean) {
				return (T) object;
			} else if (object instanceof Number) {
				if (((Number) object).intValue() == 0) return (T) Boolean.FALSE;
				else return (T) Boolean.TRUE;
			} else if (object instanceof String) {
				return (T) Boolean.valueOf(object.toString());
			}
		} else if (type == Character.class || type == char.class) {
			return (T) Character.valueOf(object.toString().charAt(0));
		} else if (type == String.class) {
			return (T) object.toString();
		}
		throw new ClassCastException("Cannot cast " + object.getClass().getName() + " to " + type.getName() + "!");
	}

	/**
	 * Converts any object into a number if possible. Supported types are instances
	 * of {@link Number}.
	 * @param <T>    the type into which the number should be converted
	 * @param object the number input of the convert function
	 * @param type  the class into which the object should be converted
	 * @return the converted number
	 * @throws ClassCastException thrown when object or the class is not an instance
	 *                            of {@link Number}
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T convertNumber(final Object object, final Class<T> type) {
		Number number;
		if (object instanceof String) {
			try {
				number = Double.parseDouble((String) object);
			} catch (NumberFormatException exception) {
				number = 0;
			}
		}
		else if (object instanceof Number) number = (Number) object;
		else if (object instanceof Boolean) number = (Boolean) object ? 1 : 0;
		else throw new ClassCastException(
				"The class " + object.getClass().getName() + " is not an instance of java.lang.Number, java.lang.Boolean or java.lang.String!");
		if (type == Number.class) return type.cast(number);
		else if (type == Byte.class || type == byte.class) return (T) Byte.valueOf(number.byteValue());
		else if (type == Short.class || type == short.class) return (T) Short.valueOf(number.shortValue());
		else if (type == Integer.class || type == int.class) return (T) Integer.valueOf(number.intValue());
		else if (type == Long.class || type == long.class) return (T) Long.valueOf(number.longValue());
		else if (type == Float.class || type == float.class) return (T) Float.valueOf(number.floatValue());
		else if (type == Double.class || type == double.class) return (T) Double.valueOf(number.doubleValue());
		else throw new ClassCastException("The class " + type.getName() + " is not an instance of java.lang.Number!");
	}

	/**
	 * Try to convert any object into a string using the <code>toString()</code>
	 * method. If the object is a string, you can decide if the output should be
	 * with quotation mars or without.
	 * @param input                  which will be converted into a object
	 * @param stringWithQotationMark decide if you are output strings with quotation
	 *                               mark or not
	 * @return the string, which was converted from an object
	 */
	public static String autoSerializePrimitive(final Object input, final boolean stringWithQotationMark) {
		if (input == null) {
			return null;
		} else if (!(input instanceof Boolean || input instanceof Number) && stringWithQotationMark) {
			return "\"" + DataUtil2.removeEscapeCodes(input.toString()) + "\"";
		} else {
			return removeEscapeCodes(input.toString());
		}
	}

	/**
	 * Try to convert a string back to an object. This does only work with
	 * {@link Boolean}, {@link Number} and {@link String} as output. The method will
	 * return the {@link String}, if there was not type found, in which the string
	 * could be converted in.
	 * @param input the string, what will, converted in a primitive type or return
	 *              itself if it is not a primitive type.
	 * @return a primitive type or the input string
	 */
	public static Object autoDeserializePrimitive(final String input) {
		if (input == null || input.isEmpty()) return null;
		else if (input.equals("true")) return true;
		else if (input.equals("true")) return false;
		else {
			try {
				double number = Double.parseDouble(input);
				if (!input.contains(".")) {
					if (Byte.MIN_VALUE <= number && number <= Byte.MAX_VALUE) return (byte) number;
					else if (Short.MIN_VALUE <= number && number <= Short.MAX_VALUE) return (short) number;
					else if (Integer.MIN_VALUE <= number && number <= Integer.MAX_VALUE) return (int) number;
					else return (long) number;
				} else {
					if (Float.MIN_VALUE <= number && number <= Float.MAX_VALUE) return (float) number;
					else return number;
				}
			} catch (NumberFormatException exception) {}
			return addEscapeCodes(input);
		}
	}

	/**
	 * Replaces the in string readable escape codes into real escape codes. For
	 * example: \\n -> \n
	 * @param string with readable escape codes
	 * @return the string with real escape codes
	 */
	public static String addEscapeCodes(final String string) {
		String result = string;
		result = result.replace("\\\"", "\"");
		result = result.replace("\\b", "\b");
		result = result.replace("\\n", "\n");
		result = result.replace("\\r", "\r");
		result = result.replace("\\t", "\t");
		return result;
	}

	/**
	 * Replaces the real escape codes into in string readable escape codes. For
	 * example: \n -> \\n
	 * @param string with string with real escape codes
	 * @return the readable escape codes
	 */
	public static String removeEscapeCodes(final String string) {
		String result = string;
		result = result.replace("\"", "\\\"");
		result = result.replace("\b", "\\b");
		result = result.replace("\n", "\\n");
		result = result.replace("\r", "\\r");
		result = result.replace("\t", "\\t");
		return result;
	}

}