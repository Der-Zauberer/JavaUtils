package eu.derzauberer.javautils.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import eu.derzauberer.javautils.util.DataUtil2;

/**
 * The class provides a parser based on a {@link TreeMap} and basic parser
 * operations. Input and output operation are based on keys. Each key represents
 * a value, but the value can be null. A string key is a path separated by dots.
 * Stored values can be null and will be returned as {@link Optional}.<br>
 * <br>
 * Example:<br>
 * 
 * <pre>
 * parser.set("my.path", "Input");
 * 
 * Object object = parser.get("my.path").orElse(null);
 * String string = parser.get("my.path", String.class).orElse("default");
 * </pre>
 * 
 * @see {@link TreeMap}, {@link Optional}
 */
public abstract class KeyParser {

	private final ArrayList<String> structrue = new ArrayList<>();
	private final HashMap<String, Object> entries = new HashMap<>();

	public KeyParser() {}
	
	public KeyParser(final String string) {
		parse(string);
	}
	
	public KeyParser(Map<String, ?> map) {
		map.forEach(this::setObject);
	}

	/**
	 * Set the value to a given key. A key represents a value, but the value can be
	 * null. A string key is a path separated by dots. Supported types are primitive
	 * types and their wrappers, {@link String}, {@link Collection} and
	 * superclasses, {@link Map} and superclasses and {@link KeyParser}. All other
	 * objects will be saved as {@link String} by their <tt>toString()</tt> method.
	 * The value will be overwritten, if the key already stores a value.<br>
	 * Example:<br>
	 * 
	 * <pre>
	 * parser.set("my.string", "Input");
	 * parser.set("my.integer", 3);
	 * parser.set("my.float", 3.5F);
	 * parser.set("my.list", new ArrayList());
	 * </pre>
	 * 
	 * @param key   the path, which represents to the value
	 * @param value any object
	 * @throws NullPointerException if the key is null
	 */
	public void set(final String key, final Object value) {
		setObject(key, value);
	}

	/**
	 * Remove the value by it's key if present.
	 * @param key the path, which represents to the value
	 * @throws NullPointerException if the key is null
	 */
	public void remove(final String key) {
		entries.remove(Objects.requireNonNull(key));
	}

	/**
	 * Get the value by it's key if present in an {@link Optional}. If there is no
	 * value then it will return an empty {@link Optional}.
	 * @param key the path, which refers to the value
	 * @return an {@link Optional} with the value represented by it's key
	 * @throws NullPointerException if the key is null
	 * @see {@link Optional}
	 */
	public Optional<?> get(final String key) {
		return getObject(key);
	}

	/**
	 * Get the value by it's key if present in an {@link Optional} in a specific
	 * type. If there is no value then it will return an empty {@link Optional}.
	 * @param <T>   type which the value will be casted in
	 * @param key   the path, which represents to the value
	 * @param clazz type which the value will be casted in
	 * @return an {@link Optional} with the value represented by it's key
	 * @throws NullPointerException if the key is null
	 * @see {@link Optional}, {@link DataUtil2}
	 */
	public <T> Optional<T> getType(final String key, final Class<T> clazz) {
		final Optional<?> optional = getObject(Objects.requireNonNull(key));
		// if (optional.isPresent()) return DataUtil.convert(optional.get(),
		// Objects.requireNonNull(clazz));
		//TODO
		return Optional.empty();
	}
	
	public <T> Collection<T> getCollection(final String key, final Collection<T> collection, final Class<T> clazz) {
		return null; //TODO
	}
	
	public <T> Collection<T> getCollection(final String key, final Class<T> clazz) {
		return null; //TODO
	}
	
	public <T> Set<T> getSet(final String key, final Class<T> clazz) {
		return null; //TODO
	}
	
	public <T> List<T> getList(final String key, final Class<T> clazz) {
		return null; //TODO
	}
	
	public <T> Queue<T> getQueue(final String key, final Class<T> clazz) {
		return null; //TODO
	}
	
	public <T> Deque<T> getDeque(final String key, final Class<T> clazz) {
		return null; //TODO
	}

	/**
	 * Check if the value of the key is present. It will also return false if the
	 * value does not exist. If you want to check if the value exists please use
	 * {@link #containsKey(String)}.
	 * @param key the path, which represents to the value
	 * @return if the value is present
	 * @throws NullPointerException if the key is null
	 */
	public boolean isPresent(final String key) {
		return entries.get(Objects.requireNonNull(key)) == null;
	}

	/**
	 * Check if the value of the key exists. I will also return true if the value
	 * does exist but is null. If you want to check if the value is present please
	 * use {@link #isPresent(String)}.
	 * @param key the path, which represents to the value
	 * @return if the value exist
	 * @throws NullPointerException if the key is null
	 */
	public boolean containsKey(final String key) {
		return entries.containsKey(Objects.requireNonNull(key));
	}

	/**
	 * Check if there is nothing stored in the parser.
	 * @return if there is nothing stored
	 */
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	/**
	 * Returns the amount of entries in the parser. Each entry is a value
	 * represented by it's key.
	 * @return the amount of entries
	 */
	public int size() {
		return entries.size();
	}

	/**
	 * Returns a {@link List} of all keys in the parser. Each key represents a value,
	 * but the value can be null.
	 * @return the amount of entries
	 */
	public List<String> getKeys() {
		return new ArrayList<>(structrue);
	}

	/**
	 * Returns a {@link List} of all keys in the parser which are sub elements of the
	 * given key. Each key represents a value, but the value can be null.
	 * @param the path, which contains the keys
	 * @return the amount of entries
	 */
	public List<String> getKeys(final String key) {
		return getKeys()
				.stream()
				.filter(string -> string.startsWith(Objects.requireNonNull(key)))
				.collect(Collectors.toList());
	}

	/**
	 * Returns a {@link Map} representation of the parser. The map may not have 
	 * the correct order of the keys. Changes of this map doesn't have any impact on 
	 * the original parser.
	 * @return a {@link Map} representation of the parser
	 */
	public Map<String, Object> getMap() {
		final Map<String, Object> map = new TreeMap<>();
		for (String key : structrue) map.put(key, entries.get(key));
		return map;
	}

	/**
	 * Set the value to a given key. A key represents a value, but the value can be
	 * null. A string key is a path separated by dots. Supported types are primitive
	 * types and their wrappers, {@link String}, {@link Collection} and
	 * superclasses, {@link Map} and superclasses and {@link KeyParser}. All other
	 * objects will be saved as {@link String} by their <tt>toString()</tt> method.
	 * The value will be overwritten, if the key already stores a value.<br>
	 * @param key   the path, which represents to the value
	 * @param value any object
	 * @throws NullPointerException if the key is null
	 */
	protected void setObject(final String key, final Object value) {
		Objects.requireNonNull(key);
		if (value instanceof Map<?, ?> || value instanceof KeyParser) {
			Map<?, ?> map;
			if (value instanceof Map<?, ?>) map = (Map<?, ?>) value;
			else map = ((KeyParser) value).getMap();
			map.forEach((mapKey, mapValue) -> {
				setObject(key + "." + mapKey, mapValue);
			});
		} else {
			entries.put(key, value);
			if (structrue.contains(key)) return;
			final String keys[] = key.split("\\.");
			String currentKeys[];
			int layer = -1;
			for (int i = 0; i < structrue.size(); i++) {
				currentKeys = structrue.get(i).split("\\.");
				for (int j = 0; j < currentKeys.length && j < keys.length; j++) {
					if (currentKeys[j].equals(keys[j])) {
						if (j > layer) layer = j;
					} else if (j <= layer && layer != -1) {
						structrue.add(i, key);
						return;
					} else {
						break;
					}
				}
			}
			structrue.add(key);
		}
	}

	/**
	 * Get the value by it's key if present in an {@link Optional}. If there is no
	 * value then it will return an empty {@link Optional}.
	 * @param key the path, which refers to the value
	 * @return an {@link Optional} with the value represented by it's key
	 * @throws NullPointerException if the key is null
	 * @see {@link Optional}
	 */
	protected Optional<Object> getObject(final String key) {
		final List<String> list = structrue.stream().filter(path -> {
			return path.startsWith(key) && (key.length() == path.length() || path.charAt(key.length()) ==  '.');
		}).collect(Collectors.toList());
		if (list.isEmpty()) {
			return Optional.empty();
		} else if (list.size() == 1) {
			return Optional.of(list.get(0));
		} else {
			KeyParser parser = getImplementationInstance();
			list.forEach(path -> parser.set(path.substring(key.length()), entries.get(path)));
			return Optional.of(parser);
		}
		//TODO
	}
	
	/**
	 * This is the output method of the parser. The object structure will be
	 * converted back to a string.
	 * @return the output of the parser
	 */
	@Override
	public String toString() {
		return out();
	}

	/**
	 * Get the list of all keys in the right order, changing the order of the keys
	 * will effect the output.
	 * @return the ordered list of the keys
	 */
	protected ArrayList<String> getStructrue() {
		return structrue;
	}

	/**
	 * Get the {@link HashMap} of all entries in the parser. Each key represents
	 * a value, but the value can be null. A string key is a path separated by dots.
	 * @return the {@link HashMap} of all entries in the parser
	 */
	protected HashMap<String, Object> getEntries() {
		return entries;
	}

	/**
	 * Create an instance of the KeyParser implementation and returns it.
	 * @return the instance of the implementation
	 */
	protected abstract KeyParser getImplementationInstance();
	
	/**
	 * The parse method is the input for the parser, the string will be parsed to
	 * the object structure of the parser in this method.
	 * @param input the input for the parser
	 */
	public abstract void parse(final String input);

	/**
	 * This is the output method of the parser. The object structure will be
	 * converted back to a string.
	 * @return the output of the parser
	 */
	public abstract String out();

}
