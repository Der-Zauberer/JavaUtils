package eu.derzauberer.javautils.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class JsonParser {

	private String string;
	private ArrayList<String> structure;
	private HashMap<String, Object> elements;

	public JsonParser(String string) {
		this.string = string;
		structure = new ArrayList<>();
		elements = new HashMap<>();
		removeSpaces();
		System.out.println(this.string);
		parse();
	}

	public void set(String key, String string) {
		setObject(key, removeEscapeCodes(string));
	}
	
	public void set(String key, boolean value) {
		setObject(key, value);
	}
	
	public void set(String key, byte value) {
		setObject(key, Byte.toString(value).toCharArray());
	}
	
	public void set(String key, short value) {
		setObject(key, Short.toString(value).toCharArray());
	}
	
	public void set(String key, int value) {
		setObject(key, Integer.toString(value).toCharArray());
	}
	
	public void set(String key, long value) {
		setObject(key, Long.toString(value).toCharArray());
	}
	
	public void set(String key, float value) {
		setObject(key, Float.toString(value).toCharArray());
	}
	
	public void set(String key, double value) {
		setObject(key, Double.toString(value).toCharArray());
	}
	
	public void set(String key, List<?> list) {
		boolean isString = true;
		List<Object> objects = new ArrayList<>();
		for (Object element : list) {
			if (!(element instanceof String)) {
				isString = false;
			}
		}
		if (isString) {
			list.forEach(object -> objects.add(removeEscapeCodes(object.toString())));
		} else {
			list.forEach(object -> objects.add(getObjectFromString(getStringFromObject(object, false))));
		}
		setObject(key, objects);
	}
	
	public void remove(String key) {
		structure.remove(key);
		elements.remove(key);
	}
	
	public boolean exist(String key) {
		if (structure.contains(key)) {
			return true;
		}
		return false;
	}
	
	public boolean isNull(String key) {
		if (elements.get(key) == null) {
			return true;
		}
		return false;
	}
	
	public Class<?> getType(String key) {
		Object object = elements.get(key);
		if (!elements.containsKey(key)) {
			for (String keys : elements.keySet()) {
				if (keys.startsWith(key)) {
					return Object.class;
				}
			}
			return null;
		}
		if (object == null) {
			return null;
		} else if (object instanceof Boolean) {
			return Boolean.class;
		} else if (object instanceof char[]) {
			return Number.class;
		} else if (object instanceof List<?>) {
			return List.class;
		}
		return String.class;
	}
	
	public Class<?> getListType(String key) {
		if (elements.get(key) instanceof List<?>) {
			List<Object> objects = getListFromObject(elements.get(key));
			if (objects.get(0)  == null) {
				boolean isNull = true;
				for (Object object : objects) {
					if (object != null) {
						isNull = false;
						break;
					}
				}
				if (isNull) {
					return null;
				}
			}
			if (objects.get(0) instanceof String) {	
				return String.class;
			} else {
				boolean isString = false;
				for (Object object : objects) {
					if (object instanceof String) {
						isString = true;
						break;
					}
				}
				if (isString) {
					return String.class;
				}
			}
			if (objects.get(0) instanceof Boolean) {
				return Boolean.class;
			} else if (objects.get(0) instanceof char[]) {
				return Number.class;
			} else if (objects.get(0) instanceof List<?>) {
				return List.class;
			}
			return String.class;
		} else {
			return null;
		}
	}
	
	public String getString(String key) {
		return addEscapeCodes(getStringFromObject(elements.get(key), false));
	}
	
	public boolean getBoolean(String key) {
		if (elements.get(key) != null || elements.get(key) instanceof Boolean) {
			return (Boolean) elements.get(key);
		}
		return false;
	}
	
	public byte getByte(String key) {
		return (byte) getNumberFromString(getStringFromObject(elements.get(key), false), Byte.class);
	}
	
	public short getShort(String key) {
		return (short) getNumberFromString(getStringFromObject(elements.get(key), false), Short.class);
	}
	
	public int getInt(String key) {
		return (int) getNumberFromString(getStringFromObject(elements.get(key), false), Integer.class);
	}
	
	public long getLong(String key) {
		return (long) getNumberFromString(getStringFromObject(elements.get(key), false), Long.class);
	}
	
	public float getFloat(String key) {
		return (float) getNumberFromString(getStringFromObject(elements.get(key), false), Float.class);
	}
	
	public double getDouble(String key) {
		return (double) getNumberFromString(getStringFromObject(elements.get(key), false), Double.class);
	}
	
	public List<String> getStringList(String key) {
		return getListFromObject(elements.get(key)).stream().map(object -> addEscapeCodes(getStringFromObject(object, false))).collect(Collectors.toList());
	}
	
	public List<Boolean> getBooleanList(String key) {
		List <Boolean> list = new ArrayList<>();
		for (Object object : getListFromObject(elements.get(key))) {
			if (object instanceof Boolean || object instanceof String) {
				if (object instanceof Boolean) {
					list.add((Boolean)object);
				} else if (object instanceof String) {
					if (((String) object).equals("true")) {
						list.add(true);
					} else {
						list.add(false);
					}
				}
			} else {
				list.add(false);
			}
		}
		return list;
	}
	
	public List<Byte> getByteList(String key) {
		List <Byte> list = new ArrayList<>();
		getListFromObject(elements.get(key)).forEach(object -> list.add((byte) getNumberFromString(getStringFromObject(object, false), Byte.class)));
		return list;
	}
	
	public List<Short> getShortList(String key) {
		List <Short> list = new ArrayList<>();
		getListFromObject(elements.get(key)).forEach(object -> list.add((short) getNumberFromString(getStringFromObject(object, false), Short.class)));
		return list;
	}
	
	public List<Integer> getIntList(String key) {
		List <Integer> list = new ArrayList<>();
		getListFromObject(elements.get(key)).forEach(object -> list.add((int) getNumberFromString(getStringFromObject(object, false), Integer.class)));
		return list;
	}
	
	public List<Long> getLongList(String key) {
		List <Long> list = new ArrayList<>();
		getListFromObject(elements.get(key)).forEach(object -> list.add((long) getNumberFromString(getStringFromObject(object, false), Long.class)));
		return list;
	}
	
	public List<Float> getFloatList(String key) {
		List <Float> list = new ArrayList<>();
		getListFromObject(elements.get(key)).forEach(object -> list.add((float) getNumberFromString(getStringFromObject(object, false), Float.class)));
		return list;
	}
	
	public List<Double> getDoubleList(String key) {
		List <Double> list = new ArrayList<>();
		getListFromObject(elements.get(key)).forEach(object -> list.add((double) getNumberFromString(getStringFromObject(object, false), Double.class)));
		return list;
	}

	@Override
	public String toString() {
		return getOutput(false);
	}

	public String toOneLineString() {
		return getOutput(true);
	}

	private void parse() {
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
					isValue = false;
					if (name != null) {
						key += name + ".";
						name = null;
					}
				} else if (string.charAt(i) == '}' || string.charAt(i) == ',') {
					if (isValue && string.charAt(i - 1) != '"') {
						value = string.substring(lastBreakPoint + 1, i);
						if (isArray) {
							array.add(getObjectFromString(value));
							lastBreakPoint = i;
						} else {
							structure.add(key + name);
							elements.put(key + name, getObjectFromString(value));
							isValue = false;
						}
					}
					if (string.charAt(i) == '}') {
						name = null;
						if (key != "" && key.substring(0, key.length() - 1).contains(".")) {
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
					if (string.charAt(i - 1) != '"') {
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
		string.append("{" + newLine);
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
					addLine(string, layer + 1, tab, qm + keys[layer] + qm + ":" + newLine);
					addLine(string, layer + 1, tab, "{" + newLine);
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
			if (elements.get(key) instanceof ArrayList<?>) {addLine(string, position + 1, tab, qm + keys[keys.length - 1] + qm + ":" + space + "[" + newLine);
				List<Object> list = getListFromObject(elements.get(key));
				for (int i = 0; i < list.size() - 1; i++) {addLine(string, position + 2, tab, getStringFromObject(list.get(i), true) + "," + newLine);
				}
				addLine(string, position + 2, tab, getStringFromObject(list.get(list.size() - 1), true) + newLine);
				addLine(string, position + 1, tab, "]");
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
		string.append(newLine + "}");
		return string.toString();
	}

	private void removeSpaces() {
		string = string.replace(" ", "");
		string = string.replace("\n", "");
		string = string.replace("\r", "");
		string = string.replace("\t", "");
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
		if (key1.split("\\.").length == key2.split("\\.").length) {
			if (!key1.equals(key2)) {
				return true;
			}
		}
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
		if (string.equals("null")) {
			return null;
		} else if (string.equals("true")) {
			return true;
		} else if (string.equals("false")) {
			return false;
		} else {
			try {Byte.parseByte(string); return string.toCharArray();} catch (NumberFormatException exception) {}
			try {Short.parseShort(string); return string.toCharArray();} catch (NumberFormatException exception) {}
			try {Integer.parseInt(string); return string.toCharArray();} catch (NumberFormatException exception) {}
			try {Long.parseLong(string); return string.toCharArray();} catch (NumberFormatException exception) {}
			try {Float.parseFloat(string); return string.toCharArray();} catch (NumberFormatException exception) {}
			try {Double.parseDouble(string); return string.toCharArray();} catch (NumberFormatException exception) {}
		}
		return string;
	}
	
	private Object getNumberFromString(String string, Class<?> numberClass) {
		if (numberClass == Byte.class) {
			try {return Byte.parseByte(getStringFromObject(string, false));} catch (NumberFormatException exception) {
				try {return (byte) Short.parseShort(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (byte) Integer.parseInt(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (byte) Long.parseLong(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (byte) Float.parseFloat(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (byte) Double.parseDouble(getStringFromObject(string, false));} catch (NumberFormatException except) {}
			}
		} else if (numberClass == Short.class) {
			try {return Short.parseShort(getStringFromObject(string, false));} catch (NumberFormatException exception) {
				try {return (short) Byte.parseByte(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (short) Integer.parseInt(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (short) Long.parseLong(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (short) Float.parseFloat(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (short) Double.parseDouble(getStringFromObject(string, false));} catch (NumberFormatException except) {}
			}
		} else if (numberClass == Integer.class) {
			try {return Integer.parseInt(getStringFromObject(string, false));} catch (NumberFormatException exception) {
				try {return (int) Byte.parseByte(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (int) Short.parseShort(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (int) Long.parseLong(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (int) Float.parseFloat(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (int) Double.parseDouble(getStringFromObject(string, false));} catch (NumberFormatException except) {}
			}
		} else if (numberClass == Long.class) {
			try {return Long.parseLong(getStringFromObject(string, false));} catch (NumberFormatException exception) {
				try {return (long) Byte.parseByte(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (long) Short.parseShort(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (long) Integer.parseInt(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (long) Float.parseFloat(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (long) Double.parseDouble(getStringFromObject(string, false));} catch (NumberFormatException except) {}
			}
		} else if (numberClass == Float.class) {
			try {return Float.parseFloat(getStringFromObject(string, false));} catch (NumberFormatException exception) {
				try {return (float) Byte.parseByte(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (float) Short.parseShort(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (float) Integer.parseInt(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (float) Long.parseLong(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (float) Double.parseDouble(getStringFromObject(string, false));} catch (NumberFormatException except) {}
			}
		} else if (numberClass == Double.class) {
			try {return Double.parseDouble(getStringFromObject(string, false));} catch (NumberFormatException exception) {
				try {return (double) Byte.parseByte(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (double) Short.parseShort(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (double) Integer.parseInt(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (double) Long.parseLong(getStringFromObject(string, false));} catch (NumberFormatException except) {}
				try {return (double) Float.parseFloat(getStringFromObject(string, false));} catch (NumberFormatException except) {}
			}
		}
		return 0;
	}
	
	private String getStringFromObject(Object object, boolean stringWithQotationMark) {
		if (object == null) {
			return null;
		} else if (object instanceof Boolean) {
			return object.toString().toLowerCase();
		} else if (object instanceof char[]) {
			return String.valueOf((char[]) object);
		} else if (stringWithQotationMark){
			return "\"" + object.toString() + "\"";
		} else {
			return object.toString();
		}
	}
	
	private List<Object> getListFromObject(Object object) {
	    return new ArrayList<>((Collection<?>)object);
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
