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
	 * @param clazz  the class into which the object should be converted
	 * @return the converted object
	 * @throws ClassCastException thrown when it is not possible to convert the
	 *                            object into the given type
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convert(final Object object, final Class<T> clazz) {
		if (object == null) {
			return null;
		} else if (object.getClass() == clazz) {
			return clazz.cast(object);
		} else if (Number.class.isAssignableFrom(clazz)) {
			return clazz.cast(convertNumber(object, (Class<? extends Number>) clazz));
		} else if (clazz == Boolean.class || clazz == boolean.class) {
			if (object instanceof Number) {
				if (((Number) object).intValue() == 0) return clazz.cast(new Boolean(false));
				else return clazz.cast(new Boolean(true));
			} else if (object instanceof String) {
				return clazz.cast(Boolean.valueOf(object.toString()));
			}
		} else if (clazz == Character.class || clazz == char.class) {
			return clazz.cast(object.toString().charAt(0));
		} else if (clazz == String.class) {
			return clazz.cast(object.toString());
		}
		throw new ClassCastException("The class " + object.getClass().getName() + " can not be casted to " + clazz.getName() + "!");
	}

	/**
	 * Converts any object into a number if possible. Supported types are instances
	 * of {@link Number}.
	 * @param <T>    the type into which the number should be converted
	 * @param object the number input of the convert function
	 * @param clazz  the class into which the object should be converted
	 * @return the converted number
	 * @throws ClassCastException thrown when object or the class is not an instance
	 *                            of {@link Number}
	 */
	public static <T extends Number> T convertNumber(final Object object, final Class<T> clazz) {
		Number number;
		if (object instanceof String) number = Double.parseDouble((String) object);
		else if (object instanceof Number) number = (Number) object;
		else throw new ClassCastException(
				"The class " + object.getClass().getName() + " is not an instance of java.lang.Number or java.lang.String!");
		if (clazz == Number.class) return clazz.cast(number);
		else if (clazz == Byte.class || clazz == byte.class) return clazz.cast(number.byteValue());
		else if (clazz == Short.class || clazz == short.class) return clazz.cast(number.shortValue());
		else if (clazz == Integer.class || clazz == int.class) return clazz.cast(number.intValue());
		else if (clazz == Long.class || clazz == long.class) return clazz.cast(number.longValue());
		else if (clazz == Float.class || clazz == float.class) return clazz.cast(number.floatValue());
		else if (clazz == Double.class || clazz == double.class) return clazz.cast(number.doubleValue());
		else throw new ClassCastException("The class " + clazz.getName() + " is not an instance of java.lang.Number!");
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
	 * Try to convert a string back in an object. This does only work with
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
			} catch (NumberFormatException exception) {
			}
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