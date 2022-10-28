package eu.derzauberer.javautils.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import eu.derzauberer.javautils.util.DataUtil;

/**
 * The class provides a parser based on keys. Each key represents
 * a value, but the value can be null. A string key is a path separated by dots.
 * Stored values can be null.<br>
 * <br>
 * Example:<br>
 * 
 * <pre>
 * parser.set("my.path", "Input");
 * 
 * Object object = parser.get("my.path");
 * String string = parser.get("my.path", String.class);
 * </pre>
 * 
 * Parsed output:<br>
 * <pre>
 *     {
 *         "my": {
 *             "path": "Input"
 *         }
 *     }
 * <pre>
 */
public class JsonParser extends KeyValueParser<JsonParser> {
	
	public JsonParser() {
		super();
	}
	
	public JsonParser(final String string) {
		super(string);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parseIn(final String input) {
		final StringBuilder key = new StringBuilder();
		final StringBuilder name = new StringBuilder();
		final StringBuilder value = new StringBuilder();
		final ArrayList<Object> array = new ArrayList<>();
		int arrayLayer = 0;
		boolean isString = false;
		boolean isValue = false;
		boolean isArray = false;
		char lastCharacter = ' ';
		for (char character : input.toCharArray()) {
			if (character == '"' && lastCharacter != '\\') {
				isString = !isString;
				if (isArray) value.append(character);
			} else if (isArray) {
				if ((character == ',' || character == ']') && arrayLayer == 0) {
					if (value.length() > 0 && value.charAt(0) == '{' && value.charAt(value.length() - 1) == '}') {
						array.add(new JsonParser(value.toString()));
					} else if (value.length() > 0 && value.charAt(0) == '[' && value.charAt(value.length() - 1) == ']') {
						array.add(new JsonParser(value.toString()).get("null"));
					} else if (value.length() > 0) {
						array.add(DataUtil.autoDeserializePrimitive(value.toString()));
					}
					value.setLength(0);
					if (character == ']') {
						isArray = false;
						isValue = false;
						if (name.length() == 0) name.append("null");
						getStructrue().add(key.toString() + name.toString());
						getEntries().put(key.toString() + name.toString(), array.clone());
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
					getStructrue().add(key.toString() + name.toString());
					getEntries().put(key.toString() + name.toString(), DataUtil.autoDeserializePrimitive(value.toString()));
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setObject(final String key, final Object value) {
		if (key == null || !key.contains(".")) {
			super.setObject(key, value);
			return;
		};
		String keys[] = key.split("\\.");
		String minKey = key.substring(0, key.lastIndexOf("."));
		if (containsKey(minKey)) remove(minKey);
		final List<String> oldKeys = getStructrue()
				.stream()
				.filter(maxKey -> maxKey.startsWith(key) && maxKey.split("\\.").length - 1 == keys.length)
				.collect(Collectors.toList());
		getStructrue().removeAll(oldKeys);
		super.setObject(key, value);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String parseOut() {
		return parseOut(false, 0);
	}
	
	/**
	 * Gets the output of the parser. The object structure will be
	 * converted back to a string.
	 * 
	 * @param oneliner if the output should be given in a single line
	 * @return the output of the parser
	 */
	public String parseOut(final boolean oneliner) {
		return parseOut(oneliner, 0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	private String parseOut(final boolean oneliner, final int offset) {
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
			if (!getEntries().containsKey("null")) tabs = tab;
			for (int i = 0; i < offset; i++) tabs += tab; 
		}
		if (getEntries().containsKey("null") && (isCollection("null") || isArray("null"))) {
			string.append("[");
			if (getCollection("null").isEmpty()) {
				string.append("]");
				return string.toString();
			}
		} else {
			string.append("{");
		}
		for (String key : getStructrue()) {
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
			if (!isArray(key) && !isCollection(key)) {
				string.append(tabs + "\"" + name + "\":" + space + DataUtil.autoSerializePrimitive(get(key), true));
			} else {
				final Collection<?> list = getCollection(key);
				if (list.isEmpty()) {
					if (!key.equals("null")) string.append(tabs + "\"" + name + "\":" + space + "[]");
				} else {
					if (!key.equals("null")) string.append(tabs + "\"" + name + "\":" + space + "[" + newLine);
					if (!oneliner) tabs += tab;
					for (Object object : list) {
						if (object instanceof KeyValueParser) {
							final JsonParser parser = new JsonParser();
							((KeyValueParser<?>) object).forEach((parserKey, parserValue) -> parser.set(parserKey, parserValue));
							string.append(tabs + parser.parseOut(oneliner, tabs.length()) + "," + newLine);
						} else if (object.getClass().isArray() || object instanceof Collection<?>) {
							final JsonParser parser = new JsonParser();
							parser.setObject("null", object);
							string.append(tabs + parser.parseOut(oneliner, tabs.length()) + "," + newLine);
						} else {
							string.append(tabs + DataUtil.autoSerializePrimitive(object, true) + "," + newLine);
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
		if (!oneliner && !containsKey("null")) tabs = tabs.substring(0, tabs.length() - 1);
		if (containsKey("null")) string.append(tabs + "]"); else string.append(newLine + tabs + "}");
		return string.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JsonParser getImplementationInstance() {
		return new JsonParser();
	}

}
