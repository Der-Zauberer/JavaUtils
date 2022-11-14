package eu.derzauberer.javautils.parser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import eu.derzauberer.javautils.accessible.AccessibleVisibility;
import eu.derzauberer.javautils.accessible.Accessor;
import eu.derzauberer.javautils.accessible.AccessorException;
import eu.derzauberer.javautils.util.DataUtil;

/**
 * This provides a parser based on key-value pairs and basic parser
 * operations. Input and output operation are based on keys. Each key represents
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
 * @see {@link Parser}
 */
public abstract class KeyValueParser<P extends KeyValueParser<P>> implements Parser<P> {

	private final List<String> structrue = new ArrayList<>();
	private final Map<String, Object> entries = new HashMap<>();

	/**
	 * Creates a new empty parser.
	 */
	public KeyValueParser() {
	}

	/**
	 * Creates a new parser and parses the string into the parser
	 * object structure.
	 * 
	 * @param input the input for the parser
	 */
	public KeyValueParser(String input) {
		parseIn(input);
	}
	
	/**
	 * Creates a new parser and reads a file and parse the file
	 * content in the parser.
	 * 
	 * @param file file the file to read
	 * @throws SecurityException if java has no permission to write to the file
	 * @throws IOException       if an I/O exception occurs
	 */
	public KeyValueParser(File file) throws IOException {
		parseFromFile(file);
	}

	/**
	 * Creates a new parser and puts the map entries in the parser.
	 * 
	 * @param map the map for the parser
	 */
	public KeyValueParser(Map<String, ?> map) {
		map.forEach(this::setObject);
	}

	/**
	 * Sets the value to a given key. A key represents a value, but the value can be
	 * null. The key null or "null" represents the root list. A string key is a path
	 * separated by dots. Supported types are primitive types and their wrappers,
	 * {@link String}, {@link Collection} and superclasses, {@link Map} and
	 * superclasses and {@link KeyValueParser}. All other objects will be saved as
	 * {@link String} by their <tt>toString()</tt> method. The value will be
	 * overwritten, if the key already stores a value.<br>
	 * Example:<br>
	 * 
	 * <pre>
	 * parser.set("my.string", "Input");
	 * parser.set("my.integer", 3);
	 * parser.set("my.float", 3.5F);
	 * parser.set("my.list", new ArrayList());
	 * parser.set("my.array", new String[5]);
	 * </pre>
	 * 
	 * @param key   the path represented by the value
	 * @param value any object
	 * @return the own parser object for further customization
	 * @throws NullPointerException if the key is null
	 */
	@SuppressWarnings("unchecked")
	public P set(String key, Object value) {
		setObject(key, value);
		return (P) this;
	}

	/**
	 * Removes the value by it's key if present. The key null or "null" represents
	 * the root list.
	 * 
	 * @param key the path represented by the value
	 * @return the own parser object for further customization
	 * @throws NullPointerException if the key is null
	 */
	@SuppressWarnings("unchecked")
	public P remove(String key) {
		structrue.remove(key == null || key.equals("null") ? "null" : key);
		entries.remove(key == null || key.equals("null") ? "null" : key);
		return (P) this;
	}

	/**
	 * Gets the value by it's key and convert it to the requested type. If there is
	 * no value then it will return null. The key null or "null" represents the root
	 * list.
	 * 
	 * @param key the path represented by the value
	 * @return the value represented by it's key
	 */
	public Object get(String key) {
		return getObject(key);
	}

	/**
	 * Gets the value by it's key and convert it to the requested type. The key null
	 * or "null" represents the root list. If there is no value then it will return
	 * null.
	 * 
	 * @param <T>  type that the value will be casted in
	 * @param key  the path represented by the value
	 * @param type type that the value will be casted in
	 * @return the value represented by it's key
	 * @see {@link DataUtil}
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> type) {
		if (type.isArray())
			return (T) getArray(key, type);
		return DataUtil.convert(getObject(key), type);
	}

	/**
	 * Gets the value by it's key and convert it to the requested type. The key null
	 * or "null" represents the root list. If there is no value then it will return
	 * the standard value from the parameter.
	 * 
	 * @param <T>      type that the value will be casted in
	 * @param key      the path represented by the value
	 * @param type     type that the value will be casted in
	 * @param standard the standard value, which will returned, if the requested
	 *                 value is null
	 * @return the value represented by it's key
	 * @see {@link DataUtil}
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> type, T standard) {
		if (!isPresent(key)) return standard;
		if (type.isArray()) return (T) getArray(key, type);
		return DataUtil.convert(getObject(key), type);
	}
	
	/**
	 * Gets the value by it's key and convert it to the requested type in an optional. The key null
	 * or "null" represents the root list. If there is no value then it will return
	 * an empty optional.
	 * 
	 * @param <T>      type that the value will be casted in
	 * @param key      the path represented by the value
	 * @param type     type that the value will be casted in
	 *                 value is null
	 * @return the value as {@link Optional} represented by it's key
	 * @see {@link DataUtil}, {@link Optional}
	 */
	@SuppressWarnings("unchecked")
	public <T> Optional<T> getOptional(String key, Class<T> type) {
		if (!isPresent(key)) return Optional.empty();
		if (type.isArray()) return Optional.of((T) getArray(key, type));
		return Optional.of(DataUtil.convert(getObject(key), type));
	}

	/**
	 * Gets the collection as value by it's key The key null or "null" represents the
	 * root list. If there is no value then it will return null. Note, that the
	 * returned value is a copy of the original one, changes doesn't have any impact
	 * on the original object. The change the value please put the object back in
	 * with {{@link #set(String, Object)}}.
	 * 
	 * @param key the path represented by the value
	 * @return collection as value represented by it's key
	 * @see {@link Collection}
	 */
	public Collection<?> getCollection(String key) {
		if (!isCollection(key) && !isArray(key)) return null;
		final Collection<Object> collection = new ArrayList<>();
		for (Object object : isArray(key) ? Arrays.asList(get(key)) : (Collection<?>) getObject(key)) {
			collection.add(object);
		}
		return collection;
	}

	/**
	 * Gets the collection as value by it's key and convert it to the requested type.
	 * The key null or "null" represents the root list. If there is no value then it
	 * will return null. Note, that the returned value is a copy of the original
	 * one, changes doesn't have any impact on the original object. The change the
	 * value please put the object back in with {{@link #set(String, Object)}}.
	 * 
	 * @param <T>  type that the value will be casted in
	 * @param key  the path represented by the value
	 * @param type type that the value will be casted in
	 * @return the collection as value represented by it's key
	 * @see {@link DataUtil}, {@link Collection}
	 */
	public <T, C extends Collection<T>> C getCollection(String key, C collection, Class<T> type) {
		if (!isCollection(key) && !isArray(key)) return null;
		for (Object object : isArray(key) ? Arrays.asList(get(key)) : (Collection<?>) getObject(key)) {
			collection.add(DataUtil.convert(object, type));
		}
		return collection;
	}
	
	/**
	 * Gets the array as value by it's key. The key null or "null" represents the
	 * root list. If there is no value then it will return null. Note, that the
	 * returned value is a copy of the original one, changes doesn't have any impact
	 * on the original object. The change the value please put the object back in
	 * with {{@link #set(String, Object)}}.
	 * 
	 * @param key the path represented by the value
	 * @return the array as value represented by it's key
	 */
	public Object[] getArray(String key) {
		if (!isCollection(key) && !isArray(key)) return null;
		final int size = isArray(key) ? ((Object[]) get(key)).length : ((Collection<?>) get(key)).size();
		final Object[] array = new Object[size];
		int i = 0;
		for (Object object : isArray(key) ? (Object[]) get(key) : ((Collection<?>) get(key)).toArray()) {
			array[i] = object;
			i++;
		}
		return array;
	}

	/**
	 * Gets the array as value by it's key and convert it to the requested type. The
	 * key null or "null" represents the root list. If there is no value then it
	 * will return null. Note, that the returned value is a copy of the original
	 * one, changes doesn't have any impact on the original object. The change the
	 * value please put the object back in with {{@link #set(String, Object)}}.
	 * 
	 * @param <T>  type that the value will be casted in
	 * @param key  the path represented by the value
	 * @param type type that the value will be casted in
	 * @return the array as value represented by it's key
	 * @see {@link DataUtil}
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] getArray(String key, Class<T> type) {
		if (!isCollection(key) && !isArray(key)) return null;
		final int size = isArray(key) ? ((Object[]) get(key)).length : ((Collection<?>) get(key)).size();
		final T t[] = (T[]) Array.newInstance(type.isArray() ? type.getComponentType() : type, size);
		int i = 0;
		for (Object object : isArray(key) ? (Object[]) get(key) : ((Collection<?>) get(key)).toArray()) {
			t[i] = DataUtil.convert(object, type);
			i++;
		}
		return t;
	}
	
	/**
	 * Serializes an object into the parser with all fields defined by the
	 * {@link AccessibleVisibility}, which should be an annotation in the class to
	 * serialize.
	 * 
	 * @param object the object to serialize
	 * @return the own parser object for further customization
	 */
	@SuppressWarnings("unchecked")
	public P serialize(Accessor<?> accessor) {
		serialize("", accessor);
		return (P) this;
	}
	
	/**
	 * Serializes an object from the {@link Accessor} into the parser with all fields defined by the
	 * {@link AccessibleVisibility}, which should be an annotation in the class to
	 * serialize.
	 * 
	 * @param key    the path represented by the value
	 * @param accessor the accessor with the object to serialize
	 * @return the own parser object for further customization
	 */
	@SuppressWarnings("unchecked")
	public P serialize(String key, Accessor<?> accessor) {
		final String objectkey = key == null || key.equals("null") ? "null" : key;
		accessor.getFields().forEach(field -> {
			final String fieldKey = objectkey.trim().isEmpty() ? field.getName() : objectkey + "." + field.getName();
			if (field.getClassType().isPrimitive() || 
					field.getValue() == null ||
					String.class.isAssignableFrom(field.getClassType()) ||
					Character.class.isAssignableFrom(field.getClassType()) ||
					Boolean.class.isAssignableFrom(field.getClassType()) ||
					Number.class.isAssignableFrom(field.getClassType())) {
				set(fieldKey, field.getValue());
			} else if (Collection.class.isAssignableFrom(field.getClassType()) || field.getClassType().isArray()) {
				final Collection<Object> collection = new ArrayList<>();
				for (Object object : field.getClassType().isArray() ? Arrays.asList((Object[]) field.getValue()) : (Collection<?>) field.getValue()) {
					if (object == null || object instanceof String || object instanceof Character || object instanceof Boolean || object instanceof Number) {
						collection.add(object);
					} else {
						collection.add(serialize(new Accessor<>(object)));
					}
				}
				set(fieldKey, collection);
			} else if (Map.class.isAssignableFrom(field.getClassType())) {
				((Map<?, ?>) field.getValue()).forEach((mapkey, mapValue) -> {
					//TODO
				});
			} else {
				serialize(fieldKey, new Accessor<>(field.getValue()));
			}
		});
		return (P) this;
	}
	
	/**
	 * Deserializes an object in the {@link Accessor} from the parser with all
	 * fields defined by the {@link AccessibleVisibility}, which should be an
	 * annotation in the class to serialize.
	 * 
	 * @param <T>      the type of the deserialized object
	 * @param accessor the accessor with the object to serialize
	 * @return the deserialized object
	 */
	public <T> T deserialize(Accessor<T> accessor) {
		return deserialize("", accessor);
	}
	
	/**
	 * Deserializes an object in the {@link Accessor} from the parser with all
	 * fields defined by the {@link AccessibleVisibility}, which should be an
	 * annotation in the class to serialize.
	 * 
	 * @param <T>      the type of the deserialized object
	 * @param key      the path represented by the value
	 * @param accessor the accessor with the object to serialize
	 * @return the deserialized object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T deserialize(String key, Accessor<T> accessor) {
		final String objectkey = key == null || key.equals("null") ? "null" : key;
		accessor.getFields().forEach(field -> {
			final String fieldKey = objectkey.trim().isEmpty() ? field.getName() : objectkey + "." + field.getName();
			if (field.getValue() == null ||
					field.getClassType().isPrimitive() || 
					String.class.isAssignableFrom(field.getClassType()) ||
					Character.class.isAssignableFrom(field.getClassType()) ||
					Boolean.class.isAssignableFrom(field.getClassType()) ||
					Number.class.isAssignableFrom(field.getClassType()) ||
					field.getClassType().isArray()) {
				field.setObjectValue(get(fieldKey, field.getClassType()));
			} else if (Collection.class.isAssignableFrom(field.getClassType())) {
				Class<?> classType = null;
				String type = null;
	            if (field.getGenericType() instanceof ParameterizedType) {
	            	type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName();
	            	if (type.equals("?")) {
	            		classType = Object.class;
	            	} else if (!type.contains(" ")) {
	            		try {
	            			classType = Class.forName(type);
	    				} catch (ClassNotFoundException exception) {}
	            	}
	            }
	            if (type == null) {
	            	new AccessorException("Can't extract a generic type in " + field.getGenericType() + "!").printStackTrace();
	            } else if (classType == null) {
	            	new AccessorException("Can't extract class from generic wildcard <" + type + ">!").printStackTrace();
	            } else {
	            	final Class<?> collectionType = field.isPresent() ? field.getValue().getClass() : field.getClassType();
	            	if (collectionType.isInterface() && Modifier.isAbstract(collectionType.getModifiers())) {
	            		new AccessorException("Can't instanciate abstract or interface object " + collectionType + ", please instanciate it in the constructor!").printStackTrace();
	            	} else {
	            		Collection collection = null;
	            		try {
							collection = (Collection) new Accessor<>(collectionType).getObject();
							if (String.class.isAssignableFrom(classType) ||
		            				Character.class.isAssignableFrom(classType) ||
		            				Boolean.class.isAssignableFrom(classType) ||
		            				Number.class.isAssignableFrom(classType)) {
		            			for (Object object : getCollection(fieldKey)) {
		            				collection.add(DataUtil.convert(object, classType));
		            			}
							} else {
								for (KeyValueParser<?> parser : getCollection(fieldKey, new ArrayList<>(), KeyValueParser.class)) {
			            			try {
										collection.add(parser.deserialize(new Accessor<>(classType)));
									} catch (NoSuchMethodException | InstantiationException | IllegalAccessException| InvocationTargetException exception) {
										new AccessorException("Can't instanciate an " + collectionType + " object, please instanciate it in the constructor!").printStackTrace();
									}
			            		}
//		            		} else if (KeyValueParser.class.isAssignableFrom(classType)) {
//		            			for (KeyValueParser<?> parser : getCollection(fieldKey, new ArrayList<>(), KeyValueParser.class)) {
//			            			try {
//										collection.add(parser.deserialize(new Accessor<>(classType)));
//									} catch (NoSuchMethodException | InstantiationException | IllegalAccessException| InvocationTargetException exception) {
//										new AccessorException("Can't instanciate an " + collectionType + " object, please instanciate it in the constructor!").printStackTrace();
//									}
//			            		}
		            		}
							field.setObjectValue(collection);
						} catch (NoSuchMethodException | InstantiationException | IllegalAccessException| InvocationTargetException exception) {
							exception.printStackTrace();
							new AccessorException("Can't instanciate the " + collectionType + " object, please instanciate it in the constructor!").printStackTrace();
						}
	            	}
	            }
			} else if (field.getClassType().isArray()) {
				field.setObjectValue(getArray(fieldKey, field.getClassType().getComponentType()));
				//TODO
			} else if (Map.class.isAssignableFrom(field.getClassType())) {
				//TODO
			} else {
				if (field.isPresent()) {
					try {
						field.setObjectValue(deserialize(fieldKey, new Accessor<>(field.getClassType())));
					} catch (IllegalArgumentException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
						new AccessorException("Can't instanciate the " + field.getGenericType() + " object, please instanciate it in the constructor!").printStackTrace();
					}
				} else if (field.getClass().isInterface() && Modifier.isAbstract(field.getClass().getModifiers())) {
					new AccessorException("Can't instanciate the abstract or interface object " + field.getClassType() + ", please instanciate it in the constructor!").printStackTrace();
				} else {
					field.setObjectValue(deserialize(fieldKey, new Accessor<>(field.getValue())));
				}
			}
		});
		return accessor.getObject();
	}

	/**
	 * Gets the array as value by it's key and check if it is primitive. The key null
	 * or "null" represents the root list. A primitive type is what isn't a
	 * {@link Collection}, an array or a {@link KeyValueParser}. All other values
	 * will be converted to strings and are not parsed as objects.
	 * 
	 * @param key the path represented by the value
	 * @return if the value represented by it's key is primitive
	 */
	public boolean isPrimitive(String key) {
		if (!isPresent(key)) return false;
		return !isCollection(key) && !isArray(key) && !isObject(key);
	}

	/**
	 * Gets the array as value by it's key and check if it is a {@link Collection}.
	 * The key null or "null" represents the root list.
	 * 
	 * @param key the path represented by the value
	 * @return if the value represented by it's key is a {@link Collection}
	 */
	public boolean isCollection(String key) {
		if (!isPresent(key)) return false;
		return get(key) instanceof Collection<?>;
	}

	/**
	 * Gets the array as value by it's key and check if it is an array. The key null
	 * or "null" represents the root list.
	 * 
	 * @param key the path represented by the value
	 * @return if the value represented by it's key is an array
	 */
	public boolean isArray(String key) {
		if (!isPresent(key)) return false;
		return get(key).getClass().isArray();
	}

	/**
	 * Gets the array as value by it's key and check if it is an object
	 * ({@link KeyValueParser}). The key null or "null" represents the root list.
	 * 
	 * @param key the path represented by the value
	 * @return if the value represented by it's key is an object
	 *         ({@link KeyValueParser})
	 */
	public boolean isObject(String key) {
		if (!isPresent(key)) return false;
		return get(key) instanceof KeyValueParser;
	}

	/**
	 * Checks if the value of the key is present. The key null or "null" represents
	 * the root list. It will also return false if the value does not exist. If you
	 * want to check if the value exists please use {@link #containsKey(String)}.
	 * 
	 * @param key the path represented by the value
	 * @return if the value is present
	 */
	public boolean isPresent(String key) {
		return entries.get(key == null || key.equals("null") ? "null" : key) != null;
	}

	/**
	 * Checks if the value of the key exists. The key null or "null" represents the
	 * root list. I will also return true if the value does exist but is null. If
	 * you want to check if the value is present please use
	 * {@link #isPresent(String)}.
	 * 
	 * @param key the path represented by the value
	 * @return if the value exist
	 */
	public boolean containsKey(String key) {
		return entries.containsKey(key == null || key.equals("null") ? "null" : key);
	}

	/**
	 * Check if there is nothing stored in the parser.
	 * 
	 * @return if there is nothing stored
	 */
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	/**
	 * Returns the amount of entries in the parser. Each entry is a value
	 * represented by it's key.
	 * 
	 * @return the amount of entries
	 */
	public int size() {
		return entries.size();
	}

	/**
	 * Returns a {@link List} of all keys in the parser. Each key represents a
	 * value, but the value can be null.
	 * 
	 * @return the amount of entries
	 */
	public List<String> getKeys() {
		return new ArrayList<>(structrue);
	}

	/**
	 * Returns a {@link List} of all keys in the parser, which are sub elements of
	 * the given key. Each key represents a value, but the value can be null.
	 * 
	 * @param the path that contains the keys
	 * @return the amount of entries
	 */
	public List<String> getKeys(String key) {
		return getKeys().stream().filter(string -> string.startsWith(Objects.requireNonNull(key)))
				.collect(Collectors.toList());
	}

	/**
	 * Iterates over all entries in the parser.
	 * 
	 * @param iterates over all entries by key and value
	 */
	public void forEach(BiConsumer<String, Object> action) {
		for (String key : structrue) action.accept(key, entries.get(key));
	}

	/**
	 * Sets the value to a given key. A key represents a value, but the value can be
	 * null. The key null or "null" represents the root list. A string key is a path
	 * separated by dots. Supported types are primitive types and their wrappers,
	 * {@link String}, {@link Collection} and superclasses, {@link Map} and
	 * superclasses and {@link KeyValueParser}. All other objects will be saved as
	 * {@link String} by their <tt>toString()</tt> method. The value will be
	 * overwritten, if the key already stores a value.<br>
	 * 
	 * @param key   the path represented by the value
	 * @param value any object
	 * @throws NullPointerException if the key is null
	 */
	protected void setObject(String key, Object value) {
		if ((key == null || key.equals("null")) && (value instanceof Collection<?> || value.getClass().isArray())) {
			structrue.clear();
			entries.clear();
			structrue.add("null");
			entries.put("null", value.getClass().isArray() ? Arrays.asList((Object[]) value) : value);
			return;
		} else if (value instanceof Map<?, ?>) {
			((Map<?, ?>) value).forEach((mapKey, mapValue) -> {
				setObject((key == null || key.equals("null") ? "" : key + ".") + mapKey, mapValue);
			});
		} else if (value instanceof KeyValueParser) {
			((KeyValueParser<?>) value).forEach((parserKey, parserValue) -> {
				setObject(key + "." + parserKey, parserValue);
				setObject((key == null || key.equals("null") ? "" : key + ".") + parserKey, parserValue);
			});
		} else {
			if (value != null && value.getClass().isArray()) {
				entries.put(key, Arrays.asList((Object[]) value));
			} else {
				entries.put(key, value);
			}
			if (structrue.contains(key))
				return;
			final String keys[] = key.split("\\.");
			String currentKeys[];
			int layer = -1;
			for (int i = 0; i < structrue.size(); i++) {
				currentKeys = structrue.get(i).split("\\.");
				for (int j = 0; j < currentKeys.length && j < keys.length; j++) {
					if (currentKeys[j].equals(keys[j])) {
						if (j > layer)
							layer = j;
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
	 * Gets the value by it's key. The key null or "null" represents the root list.
	 * If there is no value then it will return null.
	 * 
	 * @param key the path represented by the value
	 * @return the value represented by it's key
	 */
	protected Object getObject(String key) {
		if (key == null || key.equals("null")) {
			return entries.get("null");
		}
		final List<String> list = structrue.stream().filter(path -> {
			return path.startsWith(key) && (key.length() == path.length() || path.charAt(key.length()) == '.');
		}).collect(Collectors.toList());
		if (list.isEmpty()) {
			return null;
		} else if (list.size() == 1) {
			return entries.get(key);
		} else {
			KeyValueParser<?> parser = getImplementationInstance();
			list.forEach(path -> parser.set(path.substring(key.length()), entries.get(path)));
			return parser;
		}
	}

	/**
	 * Gets the output of the parser. The object structure will be
	 * converted back to a string.
	 * 
	 * @return the output of the parser
	 */
	@Override
	public String toString() {
		return parseOut();
	}

	/**
	 * Gets the list of all keys in the right order, changing the order of the keys
	 * will effect the output.
	 * 
	 * @return the ordered list of the keys
	 */
	protected List<String> getStructrue() {
		return structrue;
	}

	/**
	 * Gets the {@link HashMap} of all entries in the parser. Each key represents a
	 * value, but the value can be null. A string key is a path separated by dots.
	 * 
	 * @return the {@link HashMap} of all entries in the parser
	 */
	protected Map<String, Object> getEntries() {
		return entries;
	}

	/**
	 * Creates an instance of the KeyParser implementation and returns it.
	 * 
	 * @return the instance of the implementation
	 */
	protected abstract P getImplementationInstance();

}
