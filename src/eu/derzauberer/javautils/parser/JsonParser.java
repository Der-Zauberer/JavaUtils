package eu.derzauberer.javautils.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import eu.derzauberer.javautils.accessible.Accessible;
import eu.derzauberer.javautils.accessible.AccessibleField;
import eu.derzauberer.javautils.util.DataUtil;

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
		serializeJson(object);
	}
	
	public JsonParser setNull(String key) {
		setObject(key, null);
		return this;
	}
	
	public JsonParser set(String key, String string) {
		setObject(key, string);
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
			for (Object object : list) {
				if (object instanceof String) {
					objects.add(removeEscapeCodes(object.toString()));
				} else {
					if (object instanceof JsonParser) {
						((JsonParser) object).hasParent = true;
					}
					objects.add(object);
				}
			}
			setObject(key, objects);
		}
		return this;
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
	
	public boolean exists(String key) {
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
			if (!(keys.get(i).equals(key) || (keys.get(i).length() > key.length() && keys.get(i).startsWith(key + ".")))) {
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
		return DataUtil.getObject(getObject(key), String.class);
	}
	
	public boolean getBoolean(String key) {
		return DataUtil.getBoolean(getObject(key));
	}
	
	public byte getByte(String key) {
		return DataUtil.getNumber(getObject(key), byte.class);
	}
	
	public short getShort(String key) {
		return DataUtil.getNumber(getObject(key), short.class);
	}
	
	public int getInt(String key) {
		return DataUtil.getNumber(getObject(key), int.class);
	}
	
	public long getLong(String key) {
		return DataUtil.getNumber(getObject(key), long.class);
	}
	
	public float getFloat(String key) {
		return DataUtil.getNumber(getObject(key), float.class);
	}
	
	public double getDouble(String key) {
		return DataUtil.getNumber(getObject(key), double.class);
	}
	
	public <T> T getData(String key, Class<T> clazz) {
		return DataUtil.getObject(getObject(key), clazz);
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
				try {
					for (String string : getKeys(key, false)) {
						jsonParser.setObject(string, getObject(key + "." + string));
					}
				} catch (StringIndexOutOfBoundsException exception) {
					return null;
				}
			}
			return jsonParser;
		}
	}
	
	public <T> List<T> getList(String key, Class<T> clazz) {
		List<T> list = new ArrayList<>();
		if (DataUtil.isPrimitiveType(clazz)) {
			list = DataUtil.getPrimitiveTypeList(getObject(key), clazz);
		} else {
			list = DataUtil.getList(getObject(key), clazz);
		}
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof String) list.set(i, clazz.cast(addEscapeCodes((String) list.get(i)))); 
		}
		return list;
	}
	
	public List<Object> getObjectList(String key) {
		return DataUtil.getList(getObject(key), Object.class);
	}
	
	public List<String> getStringList(String key) {
		return getList(key, String.class);
	}
	
	public List<JsonParser> getJsonObjectList(String key) {
		return getList(key, JsonParser.class);
	}
	
	public JsonParser serializeJson(Object object) {
		return serializeJson("", object);
	}
	
	public JsonParser serializeJson(String key, Object object) {
		Accessible accessible = new Accessible(object);
		for (AccessibleField field : accessible.getAccessibleFields()) {
			String name = field.getName();
			if (key != null && !key.isEmpty()) name = key + "." + field.getName();
			if (field.getValue() != null) {
				if (DataUtil.isPrimitiveType(field.getClassType()) || (field.getClassType() == Object.class && DataUtil.isInstanceOfPrimitiveType(field.getValue()))) {
					setObject(name, field.getValue());
				} else if (field.getValue() instanceof JsonParser) {
					set(name, (JsonParser) field.getValue());
				} else if (field.getValue() instanceof List) {
					List<Object> objectList = new ArrayList<>();
					@SuppressWarnings("rawtypes")
					List list = (List) field.getValue();
					for (Object listObject : list) {
						if (DataUtil.isPrimitiveType(listObject.getClass())) {
							objectList.add(DataUtil.getObject(listObject, listObject.getClass()));
						} else {
							objectList.add(new JsonParser(listObject));
						}
					}
					set(name, objectList);
				} else {
					serializeJson(name, field.getValue());
				}
			}
		}
		return this;
	}
	
	public <T> T deserializeJson(Class<T> clazz) {
		return deserializeJson("", clazz);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T deserializeJson(String key, Class<T> clazz) {
		Accessible accessible = new Accessible(clazz);
		for (AccessibleField field : accessible.getAccessibleFields()) {
			String name = field.getName();
			if (key != null && !key.isEmpty()) name = key + "." + field.getName();
				if (getObject(name) != null || (getJsonObject(name) != null && !getJsonObject(name).isEmpty())) {
					if (DataUtil.isPrimitiveType(field.getClassType()) || (field.getClassType() == Object.class && getObject(name) != null && DataUtil.isPrimitiveType(getObject(name).getClass()))) {
						field.setValue(getData(name, field.getClassType()));
					} else if ((field.getClassType() == List.class || field.getClassType() == ArrayList.class || field.getClassType() == LinkedList.class) && field.getGenericType() != null) {
						List list = null;
						if (field.getClassType() == List.class || field.getClassType() == ArrayList.class) {
							list = new ArrayList<>();
						} else if (field.getClassType() == LinkedList.class) {
							list = new LinkedList<>();
						}
						if (list != null) {
							Class<?> listType = DataUtil.getClassFromGenericTypeOfList(field.getGenericType());
							if (DataUtil.isPrimitiveType(listType) || listType == Object.class) {
								for (Object object : getList(name, listType)) {
									list.add(object);
								}
							} else {
								for (JsonParser parserObject : getJsonObjectList(name)) {
									list.add(parserObject.deserializeJson(DataUtil.getClassFromGenericTypeOfList(field.getGenericType())));
								}
							}
							field.setValue(list);
						}
					} else {
						JsonParser value = getJsonObject(name);
						if (new Accessible(field.getClassType()).getAccessibleFields().isEmpty()) field.setValue(value);
						else if (!value.isEmpty()) field.setValue(value.deserializeJson(field.getClassType()));
					}
				}
			}
		return clazz.cast(accessible.getObject());
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setObject(String key, Object value) {
		if (value instanceof String) value = removeEscapeCodes((String) value);
		if (key.matches("^\\[\\d+\\].*")) key = "null." + key;
		else if (key.isEmpty()) key = "null";
		JsonParser parser = this;
		while (key.matches("(.+\\.\\[\\d+\\]\\..+)|(^\\[\\d+\\]\\..+)|(.+\\.\\[\\d+\\]$)|(^\\[\\d+\\]$)")) {
			String newKey = key.substring(key.indexOf(']') + 1);
			String index = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
			String subKey = key.substring(0, key.indexOf('['));
			if (subKey.endsWith(".")) subKey = subKey.substring(0, subKey.length() - 1);
			if (newKey.startsWith(".")) newKey = newKey.substring(1);
			if (index.matches("^\\d+$") && parser.elements.get(subKey) != null) {
				int i = Integer.parseInt(index);
				List list = (List) parser.elements.get(subKey);
				key = newKey;
				if (key.isEmpty()) {
					if (!list.isEmpty()) {
						list.set(i, value);
						return;
					}
				} else {
					parser = (JsonParser) list.get(i);
				}
			}
		}
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
	
	private Object getObject(String key) {
		if (key.matches("^\\[\\d+\\].*")) key = "null." + key;
		else if (key.isEmpty()) key = "null";
		JsonParser parser = this;
		Object value = this;
		while (key.matches("(.+\\.\\[\\d+\\]\\..+)|(^\\[\\d+\\]\\..+)|(.+\\.\\[\\d+\\]$)|(^\\[\\d+\\]$)")) {
			String newKey = key.substring(key.indexOf(']') + 1);
			String index = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
			String subKey = key.substring(0, key.indexOf('['));
			if (subKey.endsWith(".")) subKey = subKey.substring(0, subKey.length() - 1);
			if (newKey.startsWith(".")) newKey = newKey.substring(1);
			if (index.matches("^\\d+$") && parser.elements.get(subKey) != null) {
				value = ((List<?>) parser.elements.get(subKey)).get(Integer.parseInt(index));
				key = newKey;
				if (value instanceof JsonParser) parser = (JsonParser) value; else break;
			}
		}
		if (!key.isEmpty()) value = parser.elements.get(key);
		if (value instanceof String) value = addEscapeCodes((String) value);
		return value;
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
			if (elements.get(key) instanceof List<?>) {
				if (key.equals("null")) {
					addLine(string, position, tab, "[" + newLine);
					layer--;
					position--;
				} else {
					addLine(string, position + 1, tab, qm + keys[keys.length - 1] + qm + ":" + space + "[" + newLine);
				}
				List<Object> list = DataUtil.getList(getObject(key), Object.class);
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
								if (hasParent && i == list.size() - 1) {
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
		if (lenght1 >= lenght2 && !nextPosition) return ",";
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
		return !key1.equals(key2) && key1.split("\\.").length == key2.split("\\.").length;
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
		} else if (DataUtil.isNumericString(string)) {
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
	
	private String getStringFromObject(Object object, boolean stringWithQotationMark) {
		if (object == null) {
			return null;
		} else if (!(object instanceof Boolean || object instanceof Number) && stringWithQotationMark){
			return "\"" + object.toString() + "\"";
		} else {
			return object.toString();
		}
	}
	
	private JsonParser getJsonObjectFromObject(Object object) {
		if (object instanceof JsonParser) {
			return (JsonParser) object;
		} else {
			return null;
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