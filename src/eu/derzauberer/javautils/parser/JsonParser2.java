package eu.derzauberer.javautils.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import eu.derzauberer.javautils.accessible.Accessible;
import eu.derzauberer.javautils.accessible.AccessibleField;
import eu.derzauberer.javautils.util.DataUtil2;

public class JsonParser2 {

	private ArrayList<String> structure;
	private HashMap<String, Object> elements;

	public JsonParser2() {
		this("");
	}
	
	public JsonParser2(String string) {
		structure = new ArrayList<>();
		elements = new HashMap<>();
		parse(string);
	}
	
	public JsonParser2(Object object) {
		this("");
		serializeJson(object);
	}
	
	public JsonParser2 setNull(String key) {
		return setObject(key, null);
	}
	
	public JsonParser2 set(String key, String string) {
		return setObject(key, string);
	}
	
	public JsonParser2 set(String key, boolean value) {
		return setObject(key, value);
	}
	
	public JsonParser2 set(String key, byte value) {
		return setObject(key, value);
	}
	
	public JsonParser2 set(String key, short value) {
		setObject(key, value);
		return this;
	}
	
	public JsonParser2 set(String key, int value) {
		return setObject(key, value);
	}
	
	public JsonParser2 set(String key, long value) {
		return setObject(key, value);
	}
	
	public JsonParser2 set(String key, float value) {
		return setObject(key, value);
	}
	
	public JsonParser2 set(String key, double value) {
		return setObject(key, value);
	}
	
	public JsonParser2 set(String key, JsonParser2 object) {
		return setObject(key, object);
	}
	
	public JsonParser2 set(String key, List<?> list) {
		if (list == null) {
			setNull(key);
			return this;
		} else {
			final List<Object> objects = new ArrayList<>();
			for (Object object : list) {
				objects.add(object);
			}
			setObject(key, objects);
		}
		return this;
	}
	
	public JsonParser2 remove(String key) {
		final JsonPath path = getJsonPath(key);
		final JsonParser2 parser = path.parent;
		final String subKey = path.key;
		if (path.isListItem) {
			path.list.remove(path.index);
			return this;
		} else if (parser.elements.get(subKey) == null && parser.getKeys(subKey).size() > 0) {
			for (String objectKey : parser.getKeys(subKey)) {
				parser.structure.remove(objectKey);
				parser.elements.remove(objectKey);
			}
		} else {
			parser.structure.remove(subKey);
			parser.elements.remove(subKey);
		}
		return this;
	}
	
	public boolean exists(String key) {
		final JsonPath path = getJsonPath(key);
		return path.parent.structure.contains(path.key);
	}
	
	public boolean isEmpty() {
		return elements.isEmpty();
	}
	
	public int size() {
		return elements.size();
	}
	
	public List<String> getKeys() {
		final ArrayList<String> keys = new ArrayList<>();
		for (String string : structure) {
			keys.add(string);
		}
		return keys;
	}
	
	public List<String> getKeys(String key) {
		return getKeys(key, true);
	}
	
	public List<String> getKeys(String key, boolean fullkeys) {
		final List<String> keys = getKeys();
		for (int i = 0; i < keys.size(); i++) {
			if (!(keys.get(i).equals(key) || (keys.get(i).length() > key.length() && keys.get(i).startsWith(key + ".")))) {
				keys.remove(i);
				i--;
			} else if (!fullkeys && keys.get(i).length() > key.length()) {
				keys.set(i, keys.get(i).substring(key.length() + 1));
			}
		}
		
		return keys;
	}
	
	public Object get(String key) {
		return getObject(key);
	}
	
	public String getString(String key) {
		return DataUtil2.getObject(getObject(key), String.class);
	}
	
	public boolean getBoolean(String key) {
		return DataUtil2.getBoolean(getObject(key));
	}
	
	public byte getByte(String key) {
		return DataUtil2.getNumber(getObject(key), byte.class);
	}
	
	public short getShort(String key) {
		return DataUtil2.getNumber(getObject(key), short.class);
	}
	
	public int getInt(String key) {
		return DataUtil2.getNumber(getObject(key), int.class);
	}
	
	public long getLong(String key) {
		return DataUtil2.getNumber(getObject(key), long.class);
	}
	
	public float getFloat(String key) {
		return DataUtil2.getNumber(getObject(key), float.class);
	}
	
	public double getDouble(String key) {
		return DataUtil2.getNumber(getObject(key), double.class);
	}
	
	public <T> T getData(String key, Class<T> clazz) {
		return DataUtil2.getObject(getObject(key), clazz);
	}
	
	public JsonParser2 getJsonObject(String key) {
		final Object object = getObject(key);
		if (key.isEmpty()) return this;
		else if (object instanceof JsonParser2) {
			return (JsonParser2) object;
		} else if (object instanceof List) {
			return new JsonParser2().setObject("null", object);
		} else {
			String newKey = key;
			if (key.contains(".") && !key.endsWith(".")) newKey = key.substring(key.lastIndexOf('.') + 1, key.length());
			return new JsonParser2().setObject(newKey, object);
		}
	}
	
	public <T> List<T> getList(String key, Class<T> clazz) {
		List<T> list = new ArrayList<>();
		if (DataUtil2.isPrimitiveType(clazz)) {
			list = DataUtil2.getPrimitiveTypeList(getObject(key), clazz);
		} else {
			list = DataUtil2.getList(getObject(key), clazz);
		}
		return list;
	}
	
	public List<Object> getObjectList(String key) {
		return DataUtil2.getList(getObject(key), Object.class);
	}
	
	public List<String> getStringList(String key) {
		return getList(key, String.class);
	}
	
	public List<JsonParser2> getJsonObjectList(String key) {
		return getList(key, JsonParser2.class);
	}
	
	public JsonParser2 serializeJson(Object object) {
		return serializeJson("", object);
	}
	
	public JsonParser2 serializeJson(String key, Object object) {
		final Accessible accessible = new Accessible(object);
		for (AccessibleField field : accessible.getAccessibleFields()) {
			String name = field.getName();
			if (key != null && !key.isEmpty()) name = key + "." + field.getName();
			if (field.getValue() != null) {
				if (DataUtil2.isPrimitiveType(field.getClassType()) || (field.getClassType() == Object.class && DataUtil2.isInstanceOfPrimitiveType(field.getValue()))) {
					setObject(name, field.getValue());
				} else if (field.getValue() instanceof JsonParser2) {
					set(name, (JsonParser2) field.getValue());
				} else if (field.getValue() instanceof List) {
					final List<Object> objectList = new ArrayList<>();
					@SuppressWarnings("rawtypes")
					final List list = (List) field.getValue();
					for (Object listObject : list) {
						if (DataUtil2.isPrimitiveType(listObject.getClass())) {
							objectList.add(DataUtil2.getObject(listObject, listObject.getClass()));
						} else {
							objectList.add(new JsonParser2(listObject));
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
	
	public <T> T deserializeJson(Object object) {
		return deserializeJson("", new Accessible(object));
	}
	
	public <T> T deserializeJson(String key, Object object) {
		return deserializeJson(key, new Accessible(object));
	}
	
	public <T> T deserializeJson(Class<T> clazz) {
		return deserializeJson("", new Accessible(clazz));
	}
	
	public <T> T deserializeJson(String key, Class<T> clazz) {
		return deserializeJson(key, new Accessible(clazz));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> T deserializeJson(String key, Accessible accessible) {
		for (AccessibleField field : accessible.getAccessibleFields()) {
			String name = field.getName();
			if (key != null && !key.isEmpty()) name = key + "." + field.getName();
			final Object object = get(name);
				if (object != null) {
					if (DataUtil2.isPrimitiveType(field.getClassType()) || (field.getClass() == object && DataUtil2.isPrimitiveType(object.getClass()))) {
						if (DataUtil2.isPrimitiveType(object.getClass())) field.setValue(DataUtil2.getObject(object, field.getClassType()));
					} else if ((field.getClassType() == List.class || field.getClassType() == ArrayList.class || field.getClassType() == LinkedList.class) && field.getGenericType() != null) {
						List list = null;
						if (field.getClassType() == List.class || field.getClassType() == ArrayList.class) {
							list = new ArrayList<>();
						} else if (field.getClassType() == LinkedList.class) {
							list = new LinkedList<>();
						}
						if (list != null) {
							final Class<?> listType = DataUtil2.getClassFromGenericTypeOfList(field.getGenericType());
							if (DataUtil2.isPrimitiveType(listType) || listType == Object.class) {
								for (Object listObject : getList(name, listType)) {
									list.add(listObject);
								}
							} else {
								for (JsonParser2 parserObject : getJsonObjectList(name)) {
									list.add(parserObject.deserializeJson(DataUtil2.getClassFromGenericTypeOfList(field.getGenericType())));
								}
							}
							field.setValue(list);
						}
					} else {
						final JsonParser2 value = getJsonObject(name);
						if (new Accessible(field.getClassType()).getAccessibleFields().isEmpty()) field.setValue(value);
						else if (!value.isEmpty()) field.setValue(value.deserializeJson(field.getClassType()));
					}
				}
			}
		return (T) accessible.getObject();
	}

	@Override
	public String toString() {
		return getOutput(false, 0);
	}

	public String toOneLineString() {
		return getOutput(true, 0);
	}

	private void parse(String string) {
		final StringBuilder key = new StringBuilder();
		final StringBuilder name = new StringBuilder();
		final StringBuilder value = new StringBuilder();
		final ArrayList<Object> array = new ArrayList<>();
		int arrayLayer = 0;
		boolean isString = false;
		boolean isValue = false;
		boolean isArray = false;
		char lastCharacter = ' ';
		for (char character : string.toCharArray()) {
			if (character == '"' && lastCharacter != '\\') {
				isString = !isString;
				if (isArray) value.append(character);
			} else if (isArray) {
				if ((character == ',' || character == ']') && arrayLayer == 0) {
					if (value.length() > 0 && value.charAt(0) == '{' && value.charAt(value.length() - 1) == '}') {
						array.add(new JsonParser2(value.toString()));
					} else if (value.length() > 0 && value.charAt(0) == '[' && value.charAt(value.length() - 1) == ']') {
						array.add(new JsonParser2(value.toString()).get("null"));
					} else if (value.length() > 0) {
						array.add(getObjectFromString(value.toString()));
					}
					value.setLength(0);
					if (character == ']') {
						isArray = false;
						isValue = false;
						if (name.length() == 0) name.append("null");
						structure.add(key.toString() + name.toString());
						elements.put(key.toString() + name.toString(), array.clone());
						name.setLength(0);
						value.setLength(0);
						array.clear();
					}
				} 
				if (isString || (character != ' ' && character != '\t' && character != '\n' && character != '\r') && !(arrayLayer == 0 && (character == ',' || character == ']'))) {
					value.append(character);
				}
				if (!isString && isArray) {
					if (character == '{' || character == '[') arrayLayer++;
					else if (character == '}' || character == ']') arrayLayer--;
				}
			} else if (isString) {
				if (!isValue) name.append(character); else value.append(character);
			} else if (isValue && character != ',' && character != '{' && character != '}' && character != '[' && character != ']') {
				if (character != ' ' && character != '\t' && character != '\n' && character != '\r') value.append(character);
			} else if (character == ':') {
				isValue = true;
			} else if (character == '{') {
				isValue = false;
				if (name.length() > 0) key.append(name.toString() + ".");
				name.setLength(0);
				value.setLength(0);
			} else if (character == '}' || character == ',') {
				if (isValue) {
					isValue = false;
					structure.add(key.toString() + name.toString());
					elements.put(key.toString() + name.toString(), getObjectFromString(value.toString()));
					name.setLength(0);
					value.setLength(0);
				}
				if (character == '}') {
					if (key.length() > 0 && key.toString().contains(".") && key.indexOf(".") != key.lastIndexOf(".")) {
						key.deleteCharAt(key.length() - 1);
						key.delete(key.lastIndexOf("."), key.length());
					} else {
						key.setLength(0);
					}
				}
			} else if (character == '[') {
				isArray = true;
			}
			lastCharacter = character;
		}
	}

	@SuppressWarnings("unchecked")
	private JsonParser2 setObject(String key, Object value) {
		final JsonPath path = getJsonPath(key);
		final JsonParser2 parser = path.parent;
		final String subKey = path.key;
		if (path.isListItem) {
			path.list.set(path.index, value);
			return this;
		} else if (value instanceof JsonParser2) {
			final JsonParser2 jsonObject = (JsonParser2) value;
			for (String objectKey : jsonObject.getKeys()) {
				setObject((!subKey.isEmpty()) ?  subKey + "." + objectKey : objectKey, jsonObject.get(objectKey));
			}
			return this;
		} else {
			parser.elements.put(subKey, value);
		}
		if (!parser.structure.contains(subKey)) {
			if (subKey.contains(".")) {
				final String keys[] = subKey.split("\\.");
				int layer = -1;
				int position = 0;
				for (String struct : parser.structure) {
					final String structKeys[] = struct.split("\\.");
					for (int i = 0; i < structKeys.length; i++) {
						if (!structKeys[i].equals(keys[i])) {
							if (layer <= i - 1) {
								layer = i - 1;
								break;
							} else {
								parser.structure.add(position, subKey);
								removeWrongKeys(subKey, keys);
								return this;
							}
						} else if (keys.length - 1 < i + 1 && struct.startsWith(subKey)) {
							parser.structure.add(position + 1, subKey);
							removeWrongKeys(subKey, keys);
							return this;
						} else if (i == structKeys.length - 1 && structKeys.length <= keys.length) {
							parser.structure.add(position + 1, subKey);
							removeWrongKeys(subKey, keys);
							return  this;
						}
					}
					position++;
				}
				parser.structure.add(position, subKey);
			} else {
				parser.structure.add(subKey);
			}
		}
		return this;
	}
	
	private Object getObject(String key) {
		final JsonPath path = getJsonPath(key);
		final JsonParser2 parser = path.parent;
		final String subKey = path.key;
		if (path.isListItem) return path.list.get(path.index);
		Object value = parser.elements.get(subKey);
		if (parser.elements.get(subKey) == null && parser.getKeys(subKey).size() > 0) {
			JsonParser2 jsonObject = new JsonParser2();
			for (String objectKey : parser.getKeys(subKey, false)) {
				jsonObject.structure.add(objectKey);
				jsonObject.elements.put(objectKey, parser.elements.get((!subKey.isEmpty()) ? subKey + "." + objectKey : objectKey));
			}
			return jsonObject;
		}
		if (!subKey.isEmpty()) value = parser.elements.get(subKey);
		if (value instanceof String) value = DataUtil2.addEscapeCodes((String) value);
		return value;
	}

	private String getOutput(boolean oneliner, int offset) {
		final StringBuilder string = new StringBuilder();
		String tab = "";
		String space = "";
		String newLine = "";
		String tabs = "";
		String keys[] = {};
		String lastKeys[] = {};
		int layer = 1;
		int lastLayer = 1;
		if (!oneliner) {
			tab = "\t";
			space = " ";
			newLine = "\n";
			if (!elements.containsKey("null")) tabs = tab;
			for (int i = 0; i < offset; i++) tabs += tab; 
		}
		if (elements.containsKey("null")) {
			string.append("[");
			if (elements.get("null") instanceof List<?> && ((List<?>) elements.get("null")).isEmpty()) {
				string.append("]");
				return string.toString();
			}
		} else {
			string.append("{");
		}
		for (String key : structure) {
			keys = key.split("\\.");
			final String name = keys[keys.length - 1];
			layer = keys.length;
			int sameLayer = 0;
			for (String subKey : keys) {
				if (lastKeys.length >= sameLayer + 1 && subKey.equals(lastKeys[sameLayer])) sameLayer++; else break;
			}
			if (lastKeys.length > 0 && !(layer < lastLayer || sameLayer + 1 < lastLayer)) string.append("," + newLine); else string.append(newLine);
			while (layer < lastLayer || sameLayer + 1 < lastLayer) {
				lastLayer--;
				if (!oneliner) tabs = tabs.substring(0, tabs.length() - 1);
				string.append(tabs + "}");
				if (!(layer < lastLayer || sameLayer + 1 < lastLayer)) string.append("," + newLine); else string.append(newLine);
			}
			while (layer > lastLayer) {
				string.append(tabs + "\"" + keys[lastLayer - 1] + "\":" + space + "{");
				string.append(newLine);
				if (!oneliner) tabs += tab;
				lastLayer++;
			}
			if (!(elements.get(key) instanceof List<?>)) {
				string.append(tabs + "\"" + name + "\":" + space + getStringFromObject(elements.get(key), true));
			} else {
				final List<Object> list = DataUtil2.getList(getObject(key), Object.class);
				if (list.isEmpty()) {
					if (!key.equals("null")) string.append(tabs + "\"" + name + "\":" + space + "[]");
				} else {
					if (!key.equals("null")) string.append(tabs + "\"" + name + "\":" + space + "[" + newLine);
					if (!oneliner) tabs += tab;
					for (Object object : list) {
						if (object instanceof JsonParser2) {
							string.append(tabs + ((JsonParser2) object).getOutput(oneliner, tabs.length()) + "," + newLine);
						} else if (object instanceof List<?>) {
							string.append(tabs + new JsonParser2().setObject("null", object).getOutput(oneliner, tabs.length()) + "," + newLine);
						} else {
							string.append(tabs + getStringFromObject(object, true) + "," + newLine);
						}
					}
					if (!oneliner) tabs = tabs.substring(0, tabs.length() - 1);
					if (oneliner) string.deleteCharAt(string.length() - 1); else string.deleteCharAt(string.length() - 2);
					if (!key.equals("null")) string.append(tabs + "]");
				}
				
			}
			lastLayer = layer;
			lastKeys = keys;
		}
		while (1 < lastLayer) {
			string.append(newLine);
			lastLayer--;
			if (!oneliner) tabs = tabs.substring(0, tabs.length() - 1);
			string.append(tabs + "}");
		}
		if (!oneliner && !elements.containsKey("null")) tabs = tabs.substring(0, tabs.length() - 1);
		if (elements.containsKey("null")) string.append(tabs + "]"); else string.append(newLine + tabs + "}");
		return string.toString();
	}
	
	private void removeWrongKeys(String key, String[] keys) {
		if (key.contains(".")) {
			final String parentKey = key.substring(0, key.lastIndexOf("."));
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
		} else if (DataUtil2.isNumericString(string)) {
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
		return DataUtil2.addEscapeCodes(string);
	}
	
	private String getStringFromObject(Object object, boolean stringWithQotationMark) {
		if (object == null) {
			return null;
		} else if (!(object instanceof Boolean || object instanceof Number) && stringWithQotationMark){
			return "\"" + DataUtil2.removeEscapeCodes(object.toString()) + "\"";
		} else {
			return DataUtil2.removeEscapeCodes(object.toString());
		}
	}
	
	private JsonPath getJsonPath(String key) {
		String generatedkey = key;
		if (generatedkey.matches("^\\[\\d+\\].*")) generatedkey = "null." + generatedkey;
		else if (generatedkey.isEmpty()) generatedkey = "null";
		JsonParser2 parser = this;
		List<?> list = null;
		int i = 0;
		while (generatedkey.contains("[") && generatedkey.contains("]") && generatedkey.matches("(.+\\.\\[\\d+\\]\\..+)|(^\\[\\d+\\]\\..+)|(.+\\.\\[\\d+\\]$)|(^\\[\\d+\\]$)")) {
			String newKey = generatedkey.substring(generatedkey.indexOf(']') + 1);
			String index = generatedkey.substring(generatedkey.indexOf('[') + 1, generatedkey.indexOf(']'));
			String subKey = generatedkey.substring(0, generatedkey.indexOf('['));
			if (subKey.endsWith(".")) subKey = subKey.substring(0, subKey.length() - 1);
			if (newKey.startsWith(".")) newKey = newKey.substring(1);
			if (index.matches("^\\d+$") && parser.elements.get(subKey) != null) {
				i = Integer.parseInt(index);
				list = (List<?>) parser.elements.get(subKey);
				generatedkey = newKey;
				if (generatedkey.isEmpty()) {
					if (!list.isEmpty()) {
						if (list.get(0) instanceof JsonParser2) parser = (JsonParser2) list.get(i);
						return new JsonPath(true, parser, generatedkey, list, i);
					}
				} else {
					Object object = list.get(i);
					if (object instanceof JsonParser2) {
						parser = (JsonParser2) object;
					} else if (object instanceof List<?>) {
						System.out.println(newKey);
						parser = new JsonParser2().setObject("null", object);
						generatedkey = "null." + newKey;
					}
				}
			}
		}
		return new JsonPath(false, parser, generatedkey, list, i);
	}
	
	@SuppressWarnings("rawtypes")
	private class JsonPath {
		
		private boolean isListItem;
		private JsonParser2 parent;
		private String key;
		private List list;
		private int index;
		
		private JsonPath(boolean isListItem, JsonParser2 parent, String key, List list, int index) {
			this.isListItem = isListItem;
			this.parent = parent;
			this.key = key;
			this.list = list;
			this.index = index;
		}
		
	}
	
}