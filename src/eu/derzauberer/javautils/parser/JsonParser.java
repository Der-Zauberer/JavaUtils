package eu.derzauberer.javautils.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonParser {

	private String string;
	private ArrayList<String> structure;
	private HashMap<String, Object> elements;

	public JsonParser() {
		this("");
	}
	
	public JsonParser(String string) {
		this.string = string;
		structure = new ArrayList<>();
		elements = new HashMap<>();
		removeSpaces();
		parse();
	}

	private void set(String key, Object object) {
		setObject(key, object);
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
	
	public void set(String key, JsonParser object) {
		if (!key.endsWith(".")) {
			key += ".";
		}
		for (String string : object.getKeys()) {
			setObject(key + string, object.get(string));
		}
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
		return elements.get(key);
	}
	
	public String getString(String key) {
		if (elements.get(key) != null) {
			return addEscapeCodes(getStringFromObject(elements.get(key), false));
		} else {
			return null;
		}
	}
	
	public boolean getBoolean(String key) {
		if (elements.get(key) != null || elements.get(key) instanceof Boolean) {
			return (Boolean) elements.get(key);
		}
		return false;
	}
	
	public byte getByte(String key) {
		Class<?> classtype = elements.get(key).getClass();
		if (classtype == Byte.class) {return (byte) elements.get(key);}
		else if (classtype == Short.class) {return (byte) ((short) elements.get(key));}
		else if (classtype == Integer.class) {return (byte) ((int) elements.get(key));}
		else if (classtype == Long.class) {return (byte) ((long) elements.get(key));}
		else if (classtype == Float.class) {return (byte) ((float) elements.get(key));}
		else if (classtype == Double.class) {return (byte) ((double) elements.get(key));}
		else if (classtype == String.class) {try {return (byte) Double.parseDouble(elements.get(key).toString().replace("\"", ""));} catch (Exception exception) {}}
		return 0;
	}
	
	public short getShort(String key) {
		Class<?> classtype = elements.get(key).getClass();
		if (classtype == Byte.class) {return (short) ((byte) elements.get(key));}
		else if (classtype == Short.class) {return (short) elements.get(key);}
		else if (classtype == Integer.class) {return (short) ((int) elements.get(key));}
		else if (classtype == Long.class) {return (short) ((long) elements.get(key));}
		else if (classtype == Float.class) {return (short) ((float) elements.get(key));}
		else if (classtype == Double.class) {return (short) ((double) elements.get(key));}
		else if (classtype == String.class) {try {return (short) Double.parseDouble(elements.get(key).toString().replace("\"", ""));} catch (Exception exception) {}}
		return 0;
	}
	
	public int getInt(String key) {
		Class<?> classtype = elements.get(key).getClass();
		if (classtype == Byte.class) {return (int) ((byte) elements.get(key));}
		else if (classtype == Short.class) {return (int) ((short) elements.get(key));}
		else if (classtype == Integer.class) {return (int) elements.get(key);}
		else if (classtype == Long.class) {return (int) ((long) elements.get(key));}
		else if (classtype == Float.class) { return (int) ((float) elements.get(key));}
		else if (classtype == Double.class) {return (int) ((double) elements.get(key));}
		else if (classtype == String.class) {try {return (int) Double.parseDouble(elements.get(key).toString().replace("\"", ""));} catch (Exception exception) {}}
		return 0;
	}
	
	public long getLong(String key) {
		Class<?> classtype = elements.get(key).getClass();
		if (classtype == Byte.class) {return (long) ((byte) elements.get(key));}
		else if (classtype == Short.class) {return (long) ((short) elements.get(key));}
		else if (classtype == Integer.class) {return (long) ((int) elements.get(key));}
		else if (classtype == Long.class) {return (long) elements.get(key);}
		else if (classtype == Float.class) {return (long) ((float) elements.get(key));}
		else if (classtype == Double.class) {return (long) ((double) elements.get(key));}
		else if (classtype == String.class) {try {return (long) Double.parseDouble(elements.get(key).toString().replace("\"", ""));} catch (Exception exception) {}}
		return 0;
	}
	
	public float getFloat(String key) {
		Class<?> classtype = elements.get(key).getClass();
		if (classtype == Byte.class) {return (float) ((byte) elements.get(key));}
		else if (classtype == Short.class) {return (float) ((short) elements.get(key));}
		else if (classtype == Integer.class) {return (float) ((int) elements.get(key));}
		else if (classtype == Long.class) {return (float) ((long) elements.get(key));}
		else if (classtype == Float.class) {return (float) elements.get(key);}
		else if (classtype == Double.class) {return (float) ((double) elements.get(key));}
		else if (classtype == String.class) {try {return (float) Double.parseDouble(elements.get(key).toString().replace("\"", ""));} catch (Exception exception) {}}
		return 0;
	}
	
	public double getDouble(String key) {
		Class<?> classtype = elements.get(key).getClass();
		if (classtype == Byte.class) {return (double) ((byte) elements.get(key));}
		else if (classtype == Short.class) {return (double) ((short) elements.get(key));}
		else if (classtype == Integer.class) {return (double) ((int) elements.get(key));}
		else if (classtype == Long.class) {return (double) ((long) elements.get(key));}
		else if (classtype == Float.class) {return (double) ((float) elements.get(key));}
		else if (classtype == Double.class) {return (double) elements.get(key);}
		else if (classtype == String.class) {try {return Double.parseDouble(elements.get(key).toString().replace("\"", ""));} catch (Exception exception) {}}
		return 0;
	}
	
	public JsonParser getJsonObject(String key) {
		if (elements.get(key) instanceof JsonParser) {
			return (JsonParser) elements.get(key);
		} else {
			JsonParser jsonParser = new JsonParser();
			for (String string : getKeys(key, false)) {
				jsonParser.set(string, elements.get(string));
			}
			return jsonParser;
		}
	}
	
	public List<Object> getObjectList(String key) {
		return getListFromObject(elements.get(key));
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
					if (((String) object).equalsIgnoreCase("true")) {
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
		for (Object object : getListFromObject(elements.get(key))) {
			Class<?> classtype = object.getClass();
			if (classtype == Byte.class) {list.add((byte) object);}
			else if (classtype == Short.class) {list.add((byte) ((short) object));}
			else if (classtype == Integer.class) {list.add((byte) ((int) object));}
			else if (classtype == Long.class) {list.add((byte) ((long) object));}
			else if (classtype == Float.class) {list.add((byte) ((float) object));}
			else if (classtype == Double.class) {list.add((byte) ((double) object));}
			else if (classtype == String.class) {try {list.add((byte) Double.parseDouble(object.toString().replace("\"", "")));} catch (Exception exception) {list.add((byte) 0);}}
			else {list.add((byte) 0);}
		}
		return list;
	}
	
	public List<Short> getShortList(String key) {
		List <Short> list = new ArrayList<>();
		for (Object object : getListFromObject(elements.get(key))) {
			Class<?> classtype = object.getClass();
			if (classtype == Byte.class) {list.add((short) ((byte) object));}
			else if (classtype == Short.class) {list.add((short) object);}
			else if (classtype == Integer.class) {list.add((short) ((int) object));}
			else if (classtype == Long.class) {list.add((short) ((long) object));}
			else if (classtype == Float.class) {list.add((short) ((float) object));}
			else if (classtype == Double.class) {list.add((short) ((double) object));}
			else if (classtype == String.class) {try {list.add((short) Double.parseDouble(object.toString().replace("\"", "")));} catch (Exception exception) {list.add((short) 0);}}
			else {list.add((short) 0);}
		}
		return list;
	}
	
	public List<Integer> getIntList(String key) {
		List <Integer> list = new ArrayList<>();
		for (Object object : getListFromObject(elements.get(key))) {
			Class<?> classtype = object.getClass();
			if (classtype == Byte.class) {list.add((int) ((byte) object));}
			else if (classtype == Short.class) {list.add((int) ((short) object));}
			else if (classtype == Integer.class) {list.add((int) object);}
			else if (classtype == Long.class) {list.add((int) ((long) object));}
			else if (classtype == Float.class) {list.add((int) ((float) object));}
			else if (classtype == Double.class) {list.add((int) ((double) object));}
			else if (classtype == String.class) {try {list.add((int) Double.parseDouble(object.toString().replace("\"", "")));} catch (Exception exception) {list.add((int) 0);}}
			else {list.add((int) 0);}
		}
		return list;
	}
	
	public List<Long> getLongList(String key) {
		List <Long> list = new ArrayList<>();
		for (Object object : getListFromObject(elements.get(key))) {
			Class<?> classtype = object.getClass();
			if (classtype == Byte.class) {list.add((long) ((byte) object));}
			else if (classtype == Short.class) {list.add((long) ((short) object));}
			else if (classtype == Integer.class) {list.add((long) ((int) object));}
			else if (classtype == Long.class) {list.add((long) object);}
			else if (classtype == Float.class) {list.add((long) ((float) object));}
			else if (classtype == Double.class) {list.add((long) ((double) object));}
			else if (classtype == String.class) {try {list.add((long) Double.parseDouble(object.toString().replace("\"", "")));} catch (Exception exception) {list.add((long) 0);}}
			else {list.add((long) 0);}
		}
		return list;
	}
	
	public List<Float> getFloatList(String key) {
		List <Float> list = new ArrayList<>();
		for (Object object : getListFromObject(elements.get(key))) {
			Class<?> classtype = object.getClass();
			if (classtype == Byte.class) {list.add((float) ((byte) object));}
			else if (classtype == Short.class) {list.add((float) ((short) object));}
			else if (classtype == Integer.class) {list.add((float) ((int) object));}
			else if (classtype == Long.class) {list.add((float) ((long) object));}
			else if (classtype == Float.class) {list.add((float) object);}
			else if (classtype == Double.class) {list.add((float) ((double) object));}
			else if (classtype == String.class) {try {list.add((float) Double.parseDouble(object.toString().replace("\"", "")));} catch (Exception exception) {list.add((float) 0);}}
			else {list.add((float) 0);}
		}
		return list;
	}
	
	public List<Double> getDoubleList(String key) {
		List <Double> list = new ArrayList<>();
		for (Object object : getListFromObject(elements.get(key))) {
			Class<?> classtype = object.getClass();
			if (classtype == Byte.class) {list.add((double) ((byte) object));}
			else if (classtype == Short.class) {list.add((double) ((short) object));}
			else if (classtype == Integer.class) {list.add((double) ((int) object));}
			else if (classtype == Long.class) {list.add((double) ((long) object));}
			else if (classtype == Float.class) {list.add((double) ((float) object));}
			else if (classtype == Double.class) {list.add((double) object);}
			else if (classtype == String.class) {try {list.add((double) Double.parseDouble(object.toString().replace("\"", "")));} catch (Exception exception) {list.add((double) 0);}}
			else {list.add((double) 0);}
		}
		return list;
	}
	
	public List<JsonParser> getJsonObjectList(String key) {
		List <JsonParser> list = new ArrayList<>();
		getListFromObject(elements.get(key)).forEach(object -> list.add((JsonParser)object));
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
						array.add(new JsonParser(string.substring(lastBreakPoint, i + 1)));
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
			if (elements.get(key) instanceof ArrayList<?>) {
				addLine(string, position + 1, tab, qm + keys[keys.length - 1] + qm + ":" + space + "[" + newLine);
				List<Object> list = getListFromObject(elements.get(key));
				for (int i = 0; i < list.size(); i++) {
					if (getJsonObjectFromObject(list.get(i)) != null) {
						String object;
						if (oneliner) {
							object = getJsonObjectFromObject(list.get(i)).toOneLineString();
							string.append(object);
						} else {
							object = getJsonObjectFromObject(list.get(i)).toString();
							for (String line : object.split("\n")) {
								addLine(string, position + 2, tab, line);
								if (string.charAt(string.length() - 1) != '}') {
									string.append(newLine);
								}
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
		string.append("}");
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
	
	private List<Object> getListFromObject(Object object) {
	    return new ArrayList<>((Collection<?>)object);
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
