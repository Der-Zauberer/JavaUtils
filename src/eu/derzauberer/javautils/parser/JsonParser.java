package eu.derzauberer.javautils.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
	
	/**
	 * Creates a new empty parser.
	 */
	public JsonParser() {
		super();
	}
	
	/**
	 * Creates a new parser and parses the string into the parser
	 * object structure.
	 * 
	 * @param jsonString input the input for the parser
	 */
	public JsonParser(String jsonString) {
		super(jsonString);
	}
	
	/**
	 * Creates a new parser and reads a file and parse the file
	 * content in the parser.
	 * 
	 * @param jsonFile the file to read
	 * @throws SecurityException if java has no permission to write to the file
	 * @throws IOException       if an I/O exception occurs
	 */
	public JsonParser(Path jsonFile) throws IOException {
		super(jsonFile);
	}
	
	/**
	 * Creates a shallow copy of the original parser.
	 * 
	 * @param parser the parser to copy
	 */
	public JsonParser(KeyValueParser<?> parser) {
		super(parser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JsonParser parseIn(String input) {
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
			} else if (isArray) {
				if ((character == ',' || character == ']') && arrayLayer == 0) {
					if (value.length() > 0 && value.charAt(0) == '{' && value.charAt(value.length() - 1) == '}') {
						array.add(new JsonParser(value.toString()));
					} else if (value.length() > 0 && value.charAt(0) == '[' && value.charAt(value.length() - 1) == ']') {
						array.add(new JsonParser(value.toString()).get(null));
					} else if (value.length() > 0) {
						array.add(ParsingUtils.autoDeserializePrimitive(value.toString()));
					}
					value.setLength(0);
					if (character == ']') {
						isArray = false;
						isValue = false;
						if (name.length() == 0) {
							getStructure().add(null);
							getEntries().put(null, array.toArray());
						} else {
							getStructure().add(key + name.toString());
							getEntries().put(key.toString() + name.toString(), array.toArray());
						}
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
				if (name.length() > 0) key.append(name + ".");
				name.setLength(0);
				value.setLength(0);
			} else if (character == '}' || character == ',') {
				if (isValue) {
					isValue = false;
					getStructure().add(key.toString() + name);
					getEntries().put(key.toString() + name, ParsingUtils.autoDeserializePrimitive(value.toString()));
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
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(String key, Object value) {
		if (key == null || !key.contains(".")) {
			super.setValue(key, value);
			return;
		}
		String[] keys = key.split("\\.");
		String minKey = key.substring(0, key.lastIndexOf("."));
		if (containsKey(minKey)) remove(minKey);
		final List<String> oldKeys = getStructure()
				.stream()
				.filter(maxKey -> maxKey.startsWith(key) && maxKey.split("\\.").length - 1 == keys.length)
				.collect(Collectors.toList());
		getStructure().removeAll(oldKeys);
		super.setValue(key, value);
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
	public String parseOut(boolean oneliner) {
		return parseOut(oneliner, 0);
	}
	
	/**
	 * Gets the output of the parser. The object structure will be
	 * converted back to a string.
	 * 
	 * @param oneliner if the output should be given in a single line
	 * @param offset   the tab offset for inner objects
	 * @return the output of the parser
	 */
	private String parseOut(boolean oneliner, int offset) {
		if (containsKey(null)) return parseOutArray(oneliner, offset, null, getAsArray(null));
		final StringBuilder string = new StringBuilder();
		final String TAB = oneliner ? "" : "\t";
		final String SPACE = oneliner ? "" : " ";
		final String NEW_LINE = oneliner ? "" : "\n";
		if (getStructure().isEmpty()) return TAB.repeat(offset) + "{}";
		int lastLayer = 1;
		String[] lastkeys = {};
		string.append(TAB.repeat(offset) + '{' + NEW_LINE);
		for (String key : getStructure()) {
			final String[] keys = key.split("\\.");
			if ((keys.length < lastLayer || !isSamePath(keys, lastkeys, lastLayer - 1)) && string.charAt(string.length() - (oneliner ? 1 : 2)) == ',') string.deleteCharAt(string.length() - (oneliner ? 1 : 2));
			while (keys.length < lastLayer || !isSamePath(keys, lastkeys, lastLayer - 1)) {
				string.append(TAB.repeat(--lastLayer + offset) + '}');
				if (keys.length >= lastLayer && isSamePath(keys, lastkeys, lastLayer - 1)) string.append(',');
				string.append(NEW_LINE);
			}
			while (keys.length > lastLayer) {
				string.append(TAB.repeat(lastLayer++ + offset) + '\"' + keys[lastLayer - 2] + "\":" + SPACE + '{' + NEW_LINE);
			}
			final String name = keys[lastLayer - 1];
			final Object value = getEntries().get(key);
			final String TABS = TAB.repeat(lastLayer + offset);
			if (value != null && value.getClass().isArray()) {
				string.append(parseOutArray(oneliner, offset + lastLayer, name, getAsArray(key)));
			} else {
				string.append(TABS + '\"' + name + "\":" + SPACE + ParsingUtils.autoSerializePrimitive(value, true));
			}
			string.append(',' + NEW_LINE);
			lastkeys = keys;
		}
		if (string.charAt(string.length() - (oneliner ? 1 : 2)) == ',') string.deleteCharAt(string.length() - (oneliner ? 1 : 2));
		while (1 < lastLayer) string.append(TAB.repeat(--lastLayer + offset) + '}' + NEW_LINE);
		string.append(TAB.repeat(offset) + '}');
		return string.toString();
	}
	
	/**
	 * Gets the output of a collection in the parser. The collection
	 * structure will be converted back to a string.
	 * 
	 * @param oneliner   if the output should be given in a single line
	 * @param offset     the tab offset for inner objects
	 * @param name       the name of the collection
	 * @param collection the collection to parse out
	 * @return the collection as string
	 */
	private String parseOutArray(boolean oneliner, int offset, String name, Object[] array) {
		final StringBuilder string = new StringBuilder();
		final String TAB = oneliner ? "" : "\t";
		final String SPACE = oneliner ? "" : " ";
		final String NEW_LINE = oneliner ? "" : "\n";
		final String TABS = TAB.repeat(offset + 1);
		if (array.length == 0 && name == null) return TAB.repeat(offset) + "[]";
		string.append(TAB.repeat(offset) + (name != null ? "\"" + name + "\":" + SPACE + "[" : "[") + (array.length == 0 ? "]" : NEW_LINE));
		for (Object value : array) {
			if (value != null && (value.getClass().isArray() || value instanceof Collection<?>)) {
				final Object[] innerArray = value.getClass().isArray() ? (Object[]) value : ((Collection<?>) value).toArray();
				string.append(parseOutArray(oneliner, offset + 1, null, innerArray));
			} else if (value != null && value instanceof KeyValueParser<?>) {
				JsonParser parser;
				if (value instanceof JsonParser) {
					parser = (JsonParser) value;
				} else {
					parser = new JsonParser();
					((KeyValueParser<?>) value).forEach((objectKey, objectValue) -> parser.set(objectKey, objectValue));
				}
				string.append(parser.parseOut(oneliner, offset + 1));
			} else {
				string.append(TABS + ParsingUtils.autoSerializePrimitive(value, true));
			}
			string.append(',' + NEW_LINE);
		}
		if (array.length != 0) {
			if (string.charAt(string.length() - (oneliner ? 1 : 2)) == ',') string.deleteCharAt(string.length() - (oneliner ? 1 : 2));
			string.append(TAB.repeat(offset) + "]");
		}
		return string.toString();
	}
	
	/**
	 * Checks if two arrays have the same entries until a specific index.
	 * 
	 * @param keys     the first array
	 * @param lastkeys the second array
	 * @param layer    the index until which the arrays have to be checked
	 * @return if the arrays have the same entries until a specific point
	 */
	private boolean isSamePath(String[] keys, String[] lastkeys, int layer) {
		for (int i = 0; i < layer && i < keys.length - 1 && i < lastkeys.length - 1; i++) {
			if (!keys[i].equals(lastkeys[i])) return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JsonParser getImplementationInstance() {
		return new JsonParser();
	}
	
}
