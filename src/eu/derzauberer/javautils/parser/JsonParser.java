package eu.derzauberer.javautils.parser;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import eu.derzauberer.javautils.annotations.JsonElement;

public class JsonParser {

	private String string;
	private boolean hasParent;
	private ArrayList<String> structure;
	private HashMap<String, Object> elements;

	public JsonParser() {
		this("");
	}
	
	public JsonParser(String string) {
		this.string = string;
		hasParent = false;
		structure = new ArrayList<>();
		elements = new HashMap<>();
		parse();
	}
	
	public JsonParser(Object object) {
		this("");
		setClassAsJsonObject("", object);
	}
	
	public JsonParser setNull(String key) {
		setObject(key, null);
		return this;
	}
	
	public JsonParser set(String key, String string) {
		if (!isNull(key, string)) {
			setObject(key, removeEscapeCodes(string));
		}
		return this;
	}
	
	public JsonParser set(String key, boolean value) {
		setObject(key, value);
		return this;
	}
	
	public JsonParser set(String key, byte value) {
		setObject(key, value);
		return this;
	}
	
	public JsonParser set(String key, short value) {
		setObject(key, value);
		return this;
	}
	
	public JsonParser set(String key, int value) {
		setObject(key, value);
		return this;
	}
	
	public JsonParser set(String key, long value) {
		setObject(key, value);
		return this;
	}
	
	public JsonParser set(String key, float value) {
		setObject(key, value);
		return this;
	}
	
	public JsonParser set(String key, double value) {
		setObject(key, value);
		return this;
	}
	
	public JsonParser set(String key, JsonParser object) {
		if (!isNull(key, object)) {
			if (!key.endsWith(".")) {
				key += ".";
			}
			for (String string : object.getKeys()) {
				setObject(key + string, object.get(string));
			}
		}
		return this;
	}
	
	public JsonParser set(String key, List<?> list) {
		if (!isNull(key, list)) {
			List<Object> objects = new ArrayList<>();
			for (Object element : list) {
				if (element instanceof String) {
					objects.add(removeEscapeCodes(element.toString()));
				} else {
					if (element instanceof JsonParser) {
						((JsonParser) element).hasParent = true;
					}
					objects.add(element);
				}
			}
			setObject(key, objects);
		}
		return this;
	}
	
	public void setClassAsJsonObject(String key, Object object) {
		HashMap<Field, String> fields = getFieldsFromClass(key, object);
		for (Field field : fields.keySet()) {
			try {
				if (field.get(object) instanceof Number || field.get(object) instanceof Boolean || field.get(object) instanceof String) {
					setObject(fields.get(field), field.get(object));
				} else if (field.get(object) instanceof List<?>) {
					Type type = field.getGenericType();
					Class<?> classType = getClassFromGenericTypeOfList(type);
					if (classType != Boolean.class && classType != Byte.class && classType != Short.class && classType != Integer.class && classType != Long.class && classType != Float.class && classType != Double.class && classType != String.class) {
						List<JsonParser> list = new ArrayList<>();
						for (Object listObject : (List<?>) field.get(object)) {
							JsonParser parser = new JsonParser();
							parser.setClassAsJsonObject("", listObject);
							list.add(parser);
						}
						setObject(fields.get(field), list);
					} else {
						setObject(fields.get(field), field.get(object));
					}
				} else {
					JsonParser parser = new JsonParser();
					parser.setClassAsJsonObject("", field.get(object));
					set(fields.get(field), parser);
				}
			} catch (NullPointerException exception) {
				setObject(fields.get(field), null);
			} catch (IllegalArgumentException exception) {
			} catch (IllegalAccessException exception) {
			}
		}
	}
	
	public JsonParser remove(String key) {
		structure.remove(key);
		elements.remove(key);
		return this;
	}
	
	public JsonParser removeSection(String key) {
		ArrayList<Object> removed = new ArrayList<>();
		for (String string : structure) {
			if (string.startsWith(key)) {
				removed.add(string);
				elements.remove(key);
			}
		}
		structure.removeAll(removed);
		return this;
	}
	
	public boolean exist(String key) {
		return structure.contains(key);
	}
	
	public boolean isEmpty() {
		return elements.isEmpty();
	}
	
	public int size() {
		return elements.size();
	}
	
	public List<String> getKeys() {
		ArrayList<String> keys = new ArrayList<>();
		for (String string : structure) {
			keys.add(string);
		}
		return keys;
	}
	
	public List<String> getKeys(String key) {
		return getKeys(key, true);
	}
	
	public List<String> getKeys(String key, boolean fullkeys) {
		List<String> keys = getKeys();
		for (int i = 0; i < keys.size(); i++) {
			if (!keys.get(i).startsWith(key)) {
				keys.remove(i);
				i--;
			} else if (!fullkeys) {
				keys.set(i, keys.get(i).substring(key.length() + 1));
			}
		}
		return keys;
	}
	
	public Object get(String key) {
		return getObject(key);
	}
	
	public String getString(String key) {
		if (getObject(key) != null && !(getObject(key) instanceof JsonParser)) {
			return addEscapeCodes(getStringFromObject(getObject(key), false));
		} else {
			return null;
		}
	}
	
	public boolean getBoolean(String key) {
		return getBooleanFromObject(getObject(key));
	}
	
	public byte getByte(String key) {
		return getNumberFromObject(getObject(key), Byte.class);
	}
	
	public short getShort(String key) {
		return getNumberFromObject(getObject(key), Short.class);
	}
	
	public int getInt(String key) {
		return getNumberFromObject(getObject(key), Integer.class);
	}
	
	public long getLong(String key) {
		return getNumberFromObject(getObject(key), Long.class);
	}
	
	public float getFloat(String key) {
		return getNumberFromObject(getObject(key), Float.class);
	}
	
	public double getDouble(String key) {
		return getNumberFromObject(getObject(key), Double.class);
	}
	
	public JsonParser getJsonObject(String key) {
		if (key.isEmpty()) return this;
		if (getObject(key) instanceof JsonParser) {
			return (JsonParser) getObject(key);
		} else if (getObject(key) instanceof List) {
			JsonParser jsonParser = new JsonParser();
			jsonParser.setObject("null", get(key));
			return jsonParser;
		} else {
			JsonParser jsonParser = new JsonParser();
			if ((key.contains(".[") || key.startsWith("[")) && key.contains("].")) {
				String subKey = key.substring(0, key.lastIndexOf("]") + 1);
				String newKey = key.substring(key.lastIndexOf("]") + 2);
				for (String string : getJsonObject(subKey).getKeys(newKey, false)) {
					jsonParser.setObject(string, getObject(key + "." + string));
				}
			} else {
				for (String string : getKeys(key, false)) {
					jsonParser.setObject(string, getObject(key + "." + string));
				}
			}
			return jsonParser;
		}
	}
	
	public List<Object> getObjectList(String key) {
		return getListFromObject(getObject(key));
	}
	
	public List<String> getStringList(String key) {
		return getListFromObject(getObject(key)).stream().map(object -> addEscapeCodes(getStringFromObject(object, false))).collect(Collectors.toList());
	}
	
	public List<Boolean> getBooleanList(String key) {
		return getBooleanFromList(key);
	}
	
	public List<Byte> getByteList(String key) {
		return getNumberFromList(key, Byte.class);
	}
	
	public List<Short> getShortList(String key) {
		return getNumberFromList(key, Short.class);
	}
	
	public List<Integer> getIntList(String key) {
		return getNumberFromList(key, Integer.class);
	}
	
	public List<Long> getLongList(String key) {
		return getNumberFromList(key, Long.class);
	}
	
	public List<Float> getFloatList(String key) {
		return getNumberFromList(key, Float.class);
	}
	
	public List<Double> getDoubleList(String key) {
		return getNumberFromList(key, Double.class);
	}
	
	public List<JsonParser> getJsonObjectList(String key) {
		try {
			List <JsonParser> list = new ArrayList<>();
			getListFromObject(getObject(key)).forEach(object -> list.add((JsonParser)object));
			return list;
		} catch (ClassCastException exception) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getClassAsJsonObject(String key, Object object) {
		HashMap<Field, String> fields = getFieldsFromClass(key, object);
		for (Field field : fields.keySet()) {
			try {
				if (field.get(object) instanceof Number || field.get(object) instanceof Boolean || field.get(object) instanceof String) {
					field.set(object, get(fields.get(field)));
				} else if (field.get(object) instanceof List<?>) {
					Type type = field.getGenericType();
					Class<?> classType = getClassFromGenericTypeOfList(type);
					if (classType == Boolean.class) {
						field.set(object, getBooleanList(fields.get(field)));
					} else if (classType == Byte.class) {
						field.set(object, getByteList(fields.get(field)));
					} else if (classType == Short.class) {
						field.set(object, getShortList(fields.get(field)));
					} else if (classType == Integer.class) {
						field.set(object, getIntList(fields.get(field)));
					} else if (classType == Long.class) {
						field.set(object, getLongList(fields.get(field)));
					} else if (classType == Float.class) {
						field.set(object, getFloatList(fields.get(field)));
					} else if (classType == Double.class) {
						field.set(object, getDoubleList(fields.get(field)));
					} else if (classType == String.class){
						field.set(object, getStringList(fields.get(field)));
					} else {
						try {
							@SuppressWarnings("rawtypes")
							List objectList = new ArrayList<>();
							for (JsonParser listJsonObject : getJsonObjectList(fields.get(field))) {
								Object listObject = classType.newInstance();
								listJsonObject.getClassAsJsonObject("", classType.cast(listObject));
								objectList.add(classType.cast(listObject));
							}
							field.set(object, objectList);
						} catch (InstantiationException exception) {}
					}
				} else {
					try {
						JsonParser parser = getJsonObject(fields.get(field));
						Object subObject = Class.forName(field.getGenericType().getTypeName()).newInstance();
						parser.getClassAsJsonObject("", subObject.getClass().cast(subObject));
						field.set(object, subObject.getClass().cast(subObject));
					} catch (ClassNotFoundException exception) {
					} catch (InstantiationException exception) {}
				}
			} catch (IllegalArgumentException exception) {
				try {
					if (field.get(object) instanceof Number && get(fields.get(field)) == null) {
						field.set(object, 0);
					}
				} catch (IllegalArgumentException innerException) {
				} catch (IllegalAccessException innerException) {
				}
			} catch (IllegalAccessException exception) {}
		}
	}

	@Override
	public String toString() {
		return getOutput(false);
	}

	public String toOneLineString() {
		return getOutput(true);
	}

	private void parse() {
		removeSpaces();
		String key = "";
		String name = null;
		String value = null;
		ArrayList<Object> array = new ArrayList<>();
		int lastBreakPoint = 0;
		boolean isString = false;
		boolean isArray = false;
		boolean isValue = false;
		for (int i = 0; i < string.length(); i++) {
			if (!isString || string.charAt(i) == '"') {
				if (string.charAt(i) == '{') {
					if (isArray) {
						lastBreakPoint = i;
						int counter = 1;
						while (counter > 0) {
							i++;
							if (string.charAt(i) == '{') {
								counter++;
							} else if (string.charAt(i) == '}') {
								counter--;
							}
						}
						JsonParser parser = new JsonParser(string.substring(lastBreakPoint, i + 1));
						parser.hasParent = true;
						array.add(parser);
					} else {
						isValue = false;
						if (name != null) {
							key += name + ".";
							name = null;
						}
					}
				} else if (string.charAt(i) == '}' || string.charAt(i) == ',') {
					if (isValue && string.charAt(i - 1) != '"') {
						value = string.substring(lastBreakPoint + 1, i);
						if (isArray) {
							if (string.charAt(i - 1) != '}') {
								array.add(getObjectFromString(value));
								lastBreakPoint = i;
							}
						} else {
							structure.add(key + name);
							elements.put(key + name, getObjectFromString(value));
							isValue = false;
						}
					}
					if (string.charAt(i) == '}' && !isArray) {
						name = null;
						if (!key.isEmpty() && key.substring(0, key.length() - 1).contains(".")) {
							key = key.substring(0, key.length() - 1);
							key = key.substring(0, key.lastIndexOf('.'));
							key += ".";
						} else {
							key = "";
						}
					}
				} else if (string.charAt(i) == '"') {
					if (isString) {
						String substring = string.substring(lastBreakPoint + 1, i);
						if (!isValue) {
							name = substring;
						} else if (isArray) {
							value = substring;
							array.add(value);
						} else {
							value = substring;
							isValue = false;
							structure.add(key + name);
							elements.put(key + name, value);
							value = null;
							name = null;
						}
					}
					isString = !isString;
					lastBreakPoint = i;
				} else if (string.charAt(i) == ':') {
					isValue = true;
					lastBreakPoint = i;
				} else if (string.charAt(i) == '[') {
					isArray = true;
					isValue = true;
					lastBreakPoint = i;
					array.clear();
				} else if (string.charAt(i) == ']') {
					if (string.charAt(i - 1) != '"' && string.charAt(i - 1) != '}' && string.charAt(i - 1) != '[') {
						array.add(getObjectFromString(string.substring(lastBreakPoint + 1, i)));
					}
					isValue = false;
					isArray = false;
					structure.add(key + name);
					elements.put(key + name, array.clone());
					array.clear();
				}
			}
		}
	}

	private void setObject(String key, Object value) {
		if (!key.startsWith("null") && ((getKeys().contains("null") && getKeys().size() == 1) || (getKeys().size() == 0 && key.isEmpty() && value instanceof List<?>))) {
			if (key.isEmpty()) {
				key = "null";
			} else {
				key = "null." + key;
			}
		}
		if ((key.contains(".[") || key.startsWith("[")) && (key.contains("].") || key.endsWith("]"))) {
			JsonParser parser = null;
			String newKey = key.substring(key.indexOf(']') + 1);
			
			String index = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
			String subKey = key.substring(0, key.indexOf('['));
			if (subKey.endsWith(".")) {
				subKey = subKey.substring(0, subKey.length() - 1);
			}
			if (newKey.startsWith(".")) {
				newKey = newKey.substring(1);
			}
			if (index.matches("[0-9]+") && elements.get(subKey) != null) {
				parser = getJsonObjectList(subKey).get(Integer.parseInt(index));
				if (newKey.isEmpty() && value instanceof JsonParser) {
					parser = (JsonParser) value;
				} else {
					parser.setObject(newKey, value);
				}
			}
		} else {
			elements.put(key, value);
			if (!structure.contains(key)) {
				if (key.contains(".")) {
					String keys[] = key.split("\\.");
					int layer = -1;
					int position = 0;
					for (String struct : structure) {
						String structKeys[] = struct.split("\\.");
						for (int i = 0; i < structKeys.length; i++) {
							if (!structKeys[i].equals(keys[i])) {
								if (layer <= i - 1) {
									layer = i - 1;
									break;
								} else {
									structure.add(position, key);
									removeWrongKeys(key, keys);
									return;
								}
							} else if (keys.length - 1 < i + 1 && struct.startsWith(key)) {
								structure.add(position + 1, key);
								removeWrongKeys(key, keys);
								return;
							} else if (i == structKeys.length - 1 && structKeys.length <= keys.length) {
								structure.add(position + 1, key);
								removeWrongKeys(key, keys);
								return;
							}
						}
						position++;
					}
					structure.add(position, key);
				} else {
					structure.add(key);
				}
			}
		}
	}
	
	private Object getObject(String key) {
		if (!key.startsWith("null") && getKeys().contains("null") && getKeys().size() == 1) {
			if (key.isEmpty()) {
				key = "null";
			} else {
				key = "null." + key;
			}
		}
		if ((key.contains(".[") || key.startsWith("[")) && (key.contains("].") || key.endsWith("]"))) {
			JsonParser parser = null;
			String newKey = key.substring(key.indexOf(']') + 1);
			String index = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
			String subKey = key.substring(0, key.indexOf('['));
			if (subKey.endsWith(".")) {
				subKey = subKey.substring(0, subKey.length() - 1);
			}
			if (newKey.startsWith(".")) {
				newKey = newKey.substring(1);
			}
			if (index.matches("[0-9]+") && getJsonObjectList(subKey) != null) {
				parser = getJsonObjectList(subKey).get(Integer.parseInt(index));
				return parser.getObject(newKey);
			} else if (getJsonObjectList(subKey) == null && getObject(subKey) instanceof List<?>) {
				return getObjectList(subKey).get(Integer.parseInt(index));
			}
			return null;
		} else {
			if (key.isEmpty()) return this;
			return elements.get(key);
		}
	}

	private String getOutput(boolean oneliner) {
		StringBuilder string = new StringBuilder();
		String tab = "";
		String space = "";
		String newLine = "";
		String qm = "\"";
		if (!oneliner) {
			tab = "\t";
			space = " ";
			newLine = "\n";
		}
		int layer = 0;
		int position = 0;
		boolean isValue = false;
		boolean nextPosition = false;
		String lastKey = "";
		if (!elements.containsKey("null")) {
			string.append("{" + newLine);
		}
		for (String key : structure) {
			String keys[] = key.split("\\.");
			position = keys.length - 1;
			nextPosition = isNextPosition(key, lastKey);
			if (isValue) {
				string.append(getSeperator(position, layer, nextPosition) + newLine);
				isValue = false;
			}
			if (nextPosition) {
				addLine(string, layer, tab, "}," + newLine);
				layer--;
			}
			while (layer != position) {
				if (layer < position) {
					addLine(string, layer + 1, tab, qm + keys[layer] + qm + ":" + space + "{" + newLine);
					layer++;
				} else if (layer > position) {
					addLine(string, layer, tab, "}" + getSeperator(position, layer - 1, !nextPosition) + newLine);
					layer--;
					lastKey = lastKey.substring(0, lastKey.lastIndexOf("."));
					if (isNextPosition(key, lastKey)) {
						addLine(string, layer, tab, "}," + newLine);
						layer--;
					}
				}
			}
			if (elements.get(key) instanceof ArrayList<?>) {
				if (key.equals("null")) {
					addLine(string, position, tab, "[" + newLine);
					layer--;
					position--;
				} else {
					addLine(string, position + 1, tab, qm + keys[keys.length - 1] + qm + ":" + space + "[" + newLine);
				}
				List<Object> list = getListFromObject(elements.get(key));
				if (list.isEmpty()) {
					string.deleteCharAt(string.length() - 1);
					string.append("]");
				} else {
					for (int i = 0; i < list.size(); i++) {
						if (getJsonObjectFromObject(list.get(i)) != null) {
							String object;
							if (oneliner) {
								object = getJsonObjectFromObject(list.get(i)).toOneLineString();
								string.append(object);
							} else {
								object = getJsonObjectFromObject(list.get(i)).toString();
								for (String line : object.split("\n")) {
									if (line.endsWith("}")) {
										string.append(newLine);
									}
									if (string.charAt(string.length() - 1) == '\n' && string.charAt(string.length() - 2) == '\n') {
										string.deleteCharAt(string.length() - 1);
									}
									addLine(string, position + 2, tab, line);
									if (string.charAt(string.length() - 1) != '}') {
										string.append(newLine);
									}
								}
								if (hasParent) {
									string.append(newLine);
								}
							}
						} else {
							addLine(string, position + 2, tab, getStringFromObject(list.get(i), true));
						}
						if (i == list.size() - 1) {
							string.append(newLine);
						} else {
							string.append("," + newLine);
						}
					}
					addLine(string, position + 1, tab, "]");
				}
			} else {
				addLine(string, position + 1, tab, qm + keys[keys.length - 1] + qm + ":" + space + getStringFromObject(elements.get(key), true));
			}
			isValue = true;
			lastKey = key;
		}
		if (isValue) {
			string.append(newLine);
			isValue = false;
		}
		for (int i = layer; i > 0; i--) {
			addLine(string, i, tab, "}" + newLine);
		}
		if (string.charAt(string.length() - 1) != '\n') {
			string.append(newLine);
		}
		if (!elements.containsKey("null")) {
			string.append("}");
		} else if (string.charAt(string.length() - 1) == '\n') {
			string.deleteCharAt(string.length() - 1);
		}
		return string.toString();
	}

	private void removeSpaces() {
		StringBuilder string = new StringBuilder(this.string);
		boolean isString = false;
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == '"') {
				isString = !isString;
			} else if (!isString && (string.charAt(i) == ' ' || string.charAt(i) == '\n' || string.charAt(i) == '\r' || string.charAt(i) == '\t')) {
				string.deleteCharAt(i);
				i--;
			}
		}
		this.string = string.toString();
	}

	private void addLine(StringBuilder stringbuilder, int layer, String tab, String line) {
		for (int i = 0; i < layer; i++) {
			stringbuilder.append(tab);
		}
		stringbuilder.append(line);
	}

	private String getSeperator(int lenght1, int lenght2, boolean nextPosition) {
		if (lenght1 >= lenght2 && !nextPosition) {
			return ",";
		}
		return "";
	}

	private boolean isNextPosition(String key1, String key2) {
		if (key1.contains(".") && key2.contains(".")) {
			key1 = key1.substring(0, key1.lastIndexOf("."));
			key2 = key2.substring(0, key2.lastIndexOf("."));
		} else if (!key1.contains(".") && key2.contains(".")) {
			key2 = key2.substring(0, key2.lastIndexOf("."));
		} else {
			return false;
		}
		if (!key1.equals(key2) && key1.split("\\.").length == key2.split("\\.").length) return true;
		return false;
	}
	
	private void removeWrongKeys(String key, String[] keys) {
		if (key.contains(".")) {
			String parentKey = key.substring(0, key.lastIndexOf("."));
			if (structure.contains(parentKey)) {
				remove(parentKey);
			}
		}
		for (int i = 0; i < structure.size(); i++) {
			if (structure.get(i).startsWith(key) && structure.get(i).split("\\.").length - 1 == keys.length) {
				remove(structure.get(i));
			}
		}
	}
	
	private Object getObjectFromString(String string) {
		if (string.equalsIgnoreCase("null")) {
			return null;
		} else if (string.equalsIgnoreCase("true")) {
			return true;
		} else if (string.equalsIgnoreCase("false")) {
			return false;
		} else if (isNumericString(string)) {
			if (!string.contains(".")) {
				long number = Long.parseLong(string);
				if (Byte.MIN_VALUE <= number && number <= Byte.MAX_VALUE) {
					return (byte) number;
				} else if (Short.MIN_VALUE <= number && number <= Short.MAX_VALUE){
					return (short) number;
				} else if (Integer.MIN_VALUE <= number && number <= Integer.MAX_VALUE) {
					return (int) number;
				} else {
					return number;
				}
			} else {
				double number = Double.parseDouble(string);
				if (Float.MIN_VALUE <= number && number <= Float.MAX_VALUE) {
					return (float) number;
				} else {
					return number;
				}
			}
			
		}
		return string;
	}
	
	private boolean isNull(String key, Object object) {
		if (object == null) {
			setNull(key);
			return true;
		}
		return false;
	}
	
	private boolean isNumericString(String string) {
		Pattern numericRegex = Pattern.compile(
		        "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
		        "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
		        "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
		        "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");
		return numericRegex.matcher(string).matches();
	}
	
	private String getStringFromObject(Object object, boolean stringWithQotationMark) {
		if (object == null) {
			return null;
		} else if (object instanceof Boolean) {
			return object.toString().toLowerCase();
		} else if (object instanceof Number) {
			return object.toString();
		} else if (stringWithQotationMark){
			return "\"" + object.toString() + "\"";
		} else {
			return object.toString();
		}
	}
	
	private Boolean getBooleanFromObject(Object object) {
		if (object != null) {
			if (object instanceof Boolean) {
				return (Boolean) object;
			} else if (object instanceof Number) {
				if (((Number)object).longValue() == 0) {return false;} else {return true;}
			} else if (object instanceof String) {
				if (object.toString().equalsIgnoreCase("true")) {return true;} 
				else if (object.toString().equalsIgnoreCase("false")) {return false;}
				else if (isNumericString(object.toString())) {
					if (Double.parseDouble(object.toString().replace("\"", "")) == 0) {return false;} else {return true;}
				}
			}
		}
		return false;
	}
	
	private List<Boolean> getBooleanFromList(String key) {
		List <Boolean> list = new ArrayList<>();
		for (Object object : getListFromObject(getObject(key))) {
			list.add(getBooleanFromObject(object));
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Number> T getNumberFromObject(Object object, Class<T> type) {
		if (object != null) {
			if (object instanceof Number) {
				if (type == Byte.class) {return (T) new Byte(((Number)object).byteValue());}
				else if (type == Short.class) {return (T) new Short(((Number)object).shortValue());}
				else if (type == Integer.class) {return (T) new Integer(((Number)object).intValue());}
				else if (type == Long.class) {return (T) new Long(((Number)object).longValue());}
				else if (type == Float.class) {return (T) new Float(((Number)object).floatValue());}
				else if (type == Double.class) {return (T) new Double(((Number)object).doubleValue());}
			} else if (object instanceof Boolean) {
				if ((boolean) object) {
					return getNumberFromObject(new Integer(1), type);
				} else {
					return getNumberFromObject(new Integer(0), type);
				}
			} else if (object instanceof String) {
				if (object.toString().equalsIgnoreCase("true")) {
					return getNumberFromObject(new Integer(1), type);
				} else if (object.toString().equalsIgnoreCase("false")) {
					return getNumberFromObject(new Integer(0), type);
				} else if (isNumericString(object.toString())) {
					try {
						return getNumberFromObject(Double.parseDouble(object.toString().replace("\"", "")), type);
					} catch (NumberFormatException exception) {
						return getNumberFromObject(new Integer(0), type);
					}
				}
			}
		}
		return getNumberFromObject(new Integer(0), type);
	}
	
	private <T extends Number> List<T> getNumberFromList(String key, Class<T> type) {
		List <T> list = new ArrayList<>();
		for (Object object : getListFromObject(getObject(key))) {
			list.add(getNumberFromObject(object, type));
		}
		return list;
	}
	
	private List<Object> getListFromObject(Object object) {
		try {
			return new ArrayList<>((Collection<?>)object);
		} catch (ClassCastException | NullPointerException exception) {
			return new ArrayList<>();
		}
	}
	
	private JsonParser getJsonObjectFromObject(Object object) {
		if (object instanceof JsonParser) {
			return (JsonParser) object;
		} else {
			return null;
		}
	}
	
	private HashMap<Field, String> getFieldsFromClass(String key, Object object) {
		HashMap<Field, String> fields = new HashMap<>();
		for (Field field : object.getClass().getDeclaredFields()) {
			if (field.getAnnotation(JsonElement.class) != null) {
				field.setAccessible(true);
				String name;
				if (field.getAnnotation(JsonElement.class).key().equals("")) {
					name = field.getName();
				} else {
					name = field.getAnnotation(JsonElement.class).key();
				}
				if (key != null && !key.equals("")) {
					name = key + "." + name;
				}
				fields.put(field, name);
			}
		}
		return fields;
	}
	
	private Class<?> getClassFromGenericTypeOfList(Type type) {
		try {
			String name = type.getTypeName();
			name = name.substring(name.indexOf("<") + 1, name.length() - 1);
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			return Object.class;
		}
	}
	
	private String removeEscapeCodes(String string) {
		string = string.replace("\"", "\\\"");
		string = string.replace("\b", "\\b");
		string = string.replace("\n", "\\n");
		string = string.replace("\r", "\\r");
		string = string.replace("\t", "\\t");
		string = string.replace("/", "\\/");
		return string;
	}
	
	private String addEscapeCodes(String string) {
		string = string.replace("\\\"", "\"");
		string = string.replace("\\b", "\b");
		string = string.replace("\\n", "\n");
		string = string.replace("\\r", "\r");
		string = string.replace("\\t", "\t");
		string = string.replace("\\/", "/");
		return string;
	}
	
}