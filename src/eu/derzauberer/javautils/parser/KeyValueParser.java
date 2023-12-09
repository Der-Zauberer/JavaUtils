package eu.derzauberer.javautils.parser;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import eu.derzauberer.javautils.accessible.AccessibleVisibility;
import eu.derzauberer.javautils.accessible.Accessor;
import eu.derzauberer.javautils.accessible.AccessorException;

/**
 * Provides a parser based on key-value pairs and basic parser
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
 * @see {@link Parsable}
 */
public abstract class KeyValueParser<P extends KeyValueParser<P>> implements Parsable<P> {

	private final List<String> structure = new ArrayList<>();
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
	 * @param file the file to read
	 * @throws SecurityException if java has no permission to write to the file
	 * @throws IOException       if an I/O exception occurs
	 */
	public KeyValueParser(Path file) throws IOException {
		parseFromFile(file);
	}
	
	/**
	 * Creates a shallow copy of the original parser.
	 * 
	 * @param parser the parser to copy
	 */
	public KeyValueParser(KeyValueParser<?> parser) {
		structure.addAll(parser.getStructure());
		parser.getEntries().forEach(entries::put);
	}

	/**
	 * Sets the value to a given key. A key represents a value, but the value can be
	 * null. The key null represents the root list. A string key is a path separated
	 * by dots. Supported types are primitive types and their wrappers,
	 * {@link String}, {@link Collection} and superclasses, {@link Map} and
	 * superclasses and {@link KeyValueParser}. All other objects will be saved as
	 * {@link String} by their <tt>toString()</tt> method. The value will be
	 * overwritten, if the key already stores a value. Adding the root list with the
	 * key null will remove all existing entries. Adding entries while a root list
	 * exists will result in removing the list.<br>
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
	 * @param key   the path that represents the value
	 * @param value any object
	 * @return the own parser object for further customizations
	 */
	@SuppressWarnings("unchecked")
	public P set(String key, Object value) {
		setValue(key, value);
		return (P) this;
	}

	/**
	 * Removes the value by its key if present. The key null represents the root
	 * list.
	 * 
	 * @param key the path that represents the value
	 * @return the own parser object for further customizations
	 */
	@SuppressWarnings("unchecked")
	public P remove(String key) {
		structure.remove(key);
		entries.remove(key);
		return (P) this;
	}

	/**
	 * Gets the value by its key and convert it to the requested type. If there is
	 * no value then it will return null. The key null represents the root list.
	 * 
	 * @param key the path that represents the value
	 * @return the value represented by its key
	 */
	public Object get(String key) {
		return getValue(key);
	}

	/**
	 * Gets the value by its key and convert it to the requested type. The key null
	 * represents the root list. If there is no value then it will return null.
	 * 
	 * @param <T>  type that the value will be cast in
	 * @param key  the path that represents the value
	 * @param type type that the value will be cast in
	 * @return the value represented by its key
	 * @see {@link DataUtil}
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> type) {
		if (type.isArray()) return (T) getAsArray(key, type);
		return ParsingUtils.convertObject(getValue(key), type);
	}

	/**
	 * Gets the value by its key and convert it to the requested type. The key null
	 * represents the root list. If there is no value then it will return the
	 * standard value from the parameter.
	 * 
	 * @param <T>      type that the value will be cast in
	 * @param key      the path that represents the value
	 * @param type     type that the value will be cast in
	 * @param standard the standard value, which will returned, if the requested
	 *                 value is null
	 * @return the value represented by its key
	 * @see {@link DataUtil}
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> type, T standard) {
		if (!isPresent(key)) return standard;
		if (type.isArray()) return (T) getAsArray(key, type);
		return ParsingUtils.convertObject(getValue(key), type);
	}
	
	/**
	 * Gets the value by its key and convert it to the requested type in an
	 * optional. The key null represents the root list. If there is no value then it
	 * will return an empty optional.
	 * 
	 * @param <T>  type that the value will be cast in
	 * @param key  the path that represents the value
	 * @param type type that the value will be cast in value is null
	 * @return the value as {@link Optional} represented by its key
	 * @see {@link DataUtil}, {@link Optional}
	 */
	@SuppressWarnings("unchecked")
	public <T> Optional<T> getOptional(String key, Class<T> type) {
		if (!isPresent(key)) return Optional.empty();
		if (type.isArray()) return Optional.of((T) getAsArray(key, type));
		return Optional.of(ParsingUtils.convertObject(getValue(key), type));
	}
	
	/**
	 * Gets the object by it's key. If the value is not an object, it will
	 * put it as single value in a new object. It will also return an
	 * empty object if nothing was found by the given key.
	 * 
	 * @param key the path that represents the value
	 * @return the new object as parser
	 */
	public P getObject(String key) {
		final P parser = getImplementationInstance();
		for (String entry : getKeys(key)) {
			final String entryKey = key.equals(entry) ? entry.substring(entry.lastIndexOf('.') + 1) : entry.substring(key.length() + 1);
			parser.set(entryKey, getValue(entry));
		}
		return parser;
	}

	/**
	 * Gets the collection as value by its key The key null represents the root
	 * list. If there is no value then it will return null. Note, that the returned
	 * value is a copy of the original one, changes doesn't have any impact on the
	 * original object. To change the value please put the object back in with
	 * {{@link #set(String, Object)}} or with
	 * {@link #setAtArrayIndex(String, int, Object)}.
	 * 
	 * @param key the path that represents the value
	 * @return collection as value represented by its key
	 * @see {@link Collection}
	 */
	public Collection<?> getAsCollection(String key) {
		if (!isCollection(key) && !isArray(key)) return null;
		final Collection<Object> collection = new ArrayList<>();
		for (Object object : isArray(key) ? Arrays.asList(get(key)) : (Collection<?>) getValue(key)) {
			collection.add(object);
		}
		return collection;
	}
	
	/**
	 * Gets the list as value by its key The key null represents the root list. If
	 * there is no value then it will return null. Note, that the returned value is
	 * a copy of the original one, changes doesn't have any impact on the original
	 * object. To change the value please put the object back in with
	 * {{@link #set(String, Object)}} or with
	 * {@link #setAtArrayIndex(String, int, Object)}.
	 * 
	 * @param key the path that represents the value
	 * @return collection as value represented by its key
	 * @see {@link Collection}
	 */
	public List<?> getAsList(String key) {
		return getAsCollection(key, new ArrayList<>(), Object.class);
	}
	
	/**
	 * Gets the set as value by its key The key null represents the root list. If
	 * there is no value then it will return null. Note, that the returned value is
	 * a copy of the original one, changes doesn't have any impact on the original
	 * object. To change the value please put the object back in with
	 * {{@link #set(String, Object)}} or with
	 * {@link #setAtArrayIndex(String, int, Object)}.
	 * 
	 * @param key the path that represents the value
	 * @return collection as value represented by its key
	 * @see {@link Collection}
	 */
	public Set<?> getAsSet(String key) {
		return getAsCollection(key, new HashSet<>(), Object.class);
	}

	/**
	 * Gets the collection as value by its key and convert it to the requested type.
	 * The key null represents the root list. If there is no value then it will
	 * return null. Note, that the returned value is a copy of the original one,
	 * changes doesn't have any impact on the original object. To change the value
	 * please put the object back in with {{@link #set(String, Object)}} or with
	 * {@link #setAtArrayIndex(String, int, Object)}.
	 * 
	 * @param <T>  type that the value will be cast in
	 * @param key  the path that represents the value
	 * @param type type that the value will be cast in
	 * @return the collection as value represented by its key
	 * @see {@link DataUtil}, {@link Collection}
	 */
	public <T, C extends Collection<T>> C getAsCollection(String key, C collection, Class<T> type) {
		if (!isCollection(key) && !isArray(key)) return null;
		for (Object object : isArray(key) ? Arrays.asList(get(key)) : (Collection<?>) getValue(key)) {
			collection.add(ParsingUtils.convertObject(object, type));
		}
		return collection;
	}
	
	/**
	 * Gets the array as value by its key. The key null represents the root list. If
	 * there is no value then it will return null. Note, that the returned value is
	 * a copy of the original one, changes doesn't have any impact on the original
	 * object. To change the value please put the object back in with
	 * {{@link #set(String, Object)}} or with
	 * {@link #setAtArrayIndex(String, int, Object)}.
	 * 
	 * @param key the path that represents the value
	 * @return the array as value represented by its key
	 */
	public Object[] getAsArray(String key) {
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
	 * Gets the array as value by its key and convert it to the requested type. The
	 * key null represents the root list. If there is no value then it will return
	 * null. Note, that the returned value is a copy of the original one, changes
	 * doesn't have any impact on the original object. To change the value please
	 * put the object back in with {{@link #set(String, Object)}} or with
	 * {@link #setAtArrayIndex(String, int, Object)}.
	 * 
	 * @param <T>  type that the value will be cast in
	 * @param key  the path that represents the value
	 * @param type type that the value will be cast in
	 * @return the array as value represented by its key
	 * @see {@link DataUtil}
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] getAsArray(String key, Class<T> type) {
		if (!isCollection(key) && !isArray(key)) return null;
		final int size = isArray(key) ? ((Object[]) get(key)).length : ((Collection<?>) get(key)).size();
		final T t[] = (T[]) Array.newInstance(type.isArray() ? type.getComponentType() : type, size);
		int i = 0;
		for (Object object : isArray(key) ? (Object[]) get(key) : ((Collection<?>) get(key)).toArray()) {
			t[i] = ParsingUtils.convertObject(object, type);
			i++;
		}
		return t;
	}
	
	/**
	 * Sets a value at at specific index of an array or a collection by it's key.
	 * The key null represents the root list. It does nothing if the key doesn't
	 * point to an array or collection. The operation can manipulate {@link List}
	 * objects directly. A new {@link List} has to be created for all other arrays
	 * and collection types. This list overrides the existing array or collection.
	 * 
	 * @param key   he path that represents the value
	 * @param index the array or collection index to put the value in
	 * @param value the value to put in the array or collection at the index
	 * @return the own parser object for further customizations
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *                                   ({@code index < 0 || index >= size()})
	 */
	@SuppressWarnings("unchecked")
	public P setAtArrayIndex(String key, int index, Object value) {
		if (!isCollection(key) && !isArray(key)) return (P) this;
		ArrayList<Object> list;
		if (getValue(key) instanceof ArrayList<?>) { 
			list = (ArrayList<Object>) getValue(key);
		} else if (isCollection(key)) {
			list = new ArrayList<>((Collection<?>) getValue(key));
		} else {
			list = new ArrayList<>(Arrays.asList((Object[]) getValue(key)));
		}
		if (index < list.size()) {
			list.set(index, value);
		} else {
			for (int i = 0; i < index - list.size() - 1; i++) list.add(null);
			list.add(value);
		}
		setValue(key, list);
		return (P) this;
	}
	
	/**
	 * Gets the object from a specific index of an array or a collection by it's
	 * key. The key null represents the root list. It returns null if the value that
	 * is found by the key is not an array or collection.
	 * 
	 * @param key   the path that represents the value
	 * @param index the array or collection index to take the value from
	 * @return the value from the index of an array or a collection
	 * @throws IndexOutOfBoundsException if the index is outside of the array
	 */
	public Object getAtArrayIndex(String key, int index) {
		if (isCollection(key)) {
			return ((Collection<?>) getValue(key)).toArray()[index];
		} else if (isArray(key)) {
			return ((Object[]) getValue(key))[index];
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the value from a specific index of an array or a collection by it's key
	 * and convert it to the requested type. The key null represents the root list.
	 * It returns null if the object that is found by the key is not an array or
	 * collection.
	 * 
	 * @param <T>   type that the value will be casted in
	 * @param key   key the path that represents the value
	 * @param index the array or collection index to take the value from
	 * @param type  the type that the value will be casted in
	 * @return the value from the index of an array or a collection
	 * @throws IndexOutOfBoundsException if the index is outside of the array
	 * @throws ClassCastException        if it is not possible to convert the object
	 *                                   into the given type
	 */
	public <T> T getAtArrayIndex(String key, int index, Class<T> type) {
		if (isCollection(key)) {
			return type.cast(((Collection<?>) getValue(key)).toArray()[index]);
		} else if (isArray(key)) {
			return type.cast(((Object[]) getValue(key))[index]);
		} else {
			return null;
		}
	}
	
	/**
	 * Serializes an object into the parser with all fields defined by the
	 * {@link AccessibleVisibility}, which should be an annotation in the class to
	 * serialize.
	 * 
	 * @param accessor the object to serialize
	 * @return the own parser object for further customizations
	 * 
	 * @see {@link Accessor}
	 */
	@SuppressWarnings("unchecked")
	public P serialize(Accessor<?> accessor) {
		serialize("", accessor);
		return (P) this;
	}
	
	/**
	 * Serializes an object from the {@link Accessor} into the parser with all
	 * fields defined by the {@link AccessibleVisibility}, which should be an
	 * annotation in the class to serialize.
	 * 
	 * @param key      the path that represents the value
	 * @param accessor the accessor with the object to serialize
	 * @return the own parser object for further customizations
	 * 
	 * @see {@link Accessor}
	 */
	@SuppressWarnings("unchecked")
	public P serialize(String key, Accessor<?> accessor) {
		accessor.getFields().forEach(field -> {
			final String fieldKey = key.trim().isEmpty() ? field.getName() : key + "." + field.getName();
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
						collection.add(getImplementationInstance().serialize((new Accessor<>(object))));
					}
				}
				set(fieldKey, collection);
			} else if (Map.class.isAssignableFrom(field.getClassType())) {
				((Map<?, ?>) field.getValue()).forEach((mapKey, mapValue) -> {
					//TODO
				});
			} else if (Enum.class.isAssignableFrom(field.getClassType())) {
				set(fieldKey, field.getValue().toString());
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
	 * 
	 * @see {@link Accessor} {@link DataUtil}
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
	 * @param key      the path that represents the value
	 * @param accessor the accessor with the object to serialize
	 * @return the deserialized object
	 * 
	 * @see {@link Accessor} {@link DataUtil}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T deserialize(String key, Accessor<T> accessor) {
		accessor.getFields().forEach(field -> {
			final String fieldKey = key.trim().isEmpty() ? field.getName() : key + "." + field.getName();
			if (field.getValue() == null ||
					field.getClassType().isPrimitive() || 
					String.class.isAssignableFrom(field.getClassType()) ||
					Character.class.isAssignableFrom(field.getClassType()) ||
					Boolean.class.isAssignableFrom(field.getClassType()) ||
					Number.class.isAssignableFrom(field.getClassType())) {
				field.setObjectValue(get(fieldKey, field.getClassType()));
			} else if (Collection.class.isAssignableFrom(field.getClassType())) {
				try {
					final Class<?> classType = extractGenericClass(field.getGenericType(), 0);
					final Class<?> collectionType = field.isPresent() ? field.getValue().getClass() : field.getClassType();
	            	if (collectionType.isInterface() && Modifier.isAbstract(collectionType.getModifiers())) {
	            		new SerializationException("Can't instantiate abstract or interface object " + collectionType + ", please instantiate it in the constructor!").printStackTrace();
	            	} else {
	            		field.setObjectValue(deserializeCollection(getAsCollection(fieldKey), collectionType, classType));
	            	}
				} catch (SerializationException exception) {
					exception.printStackTrace();
				}
			} else if (field.getClassType().isArray()) {
				field.setObjectValue(getAsArray(fieldKey, field.getClassType().getComponentType()));
				final Collection collection = deserializeCollection(getAsCollection(fieldKey), ArrayList.class, field.getClassType().getComponentType());
				field.setObjectValue(collection.toArray(Accessor.instantiateArray(field.getClassType().getComponentType(), collection.size())));				
			} else if (Map.class.isAssignableFrom(field.getClassType())) {
				//TODO
			} else if (Enum.class.isAssignableFrom(field.getClassType())) {
				final String value = get(fieldKey, String.class);
				if (value != null) Arrays.stream(field.getClassType().getEnumConstants()).filter(object -> object.toString().equals(value)).findAny().ifPresent(field::setObjectValue);
			} else {
				if (field.isPresent()) {
					field.setObjectValue(deserialize(fieldKey, new Accessor<>(field.getValue())));
				} else if (field.getClass().isInterface() && Modifier.isAbstract(field.getClass().getModifiers())) {
					new SerializationException("Can't instantiate the abstract or interface object " + field.getClassType() + ", please instantiate it in the constructor!").printStackTrace();
				} else {
					try {
						field.setObjectValue(deserialize(fieldKey, new Accessor<>(field.getClassType())));
					} catch (IllegalArgumentException | AccessorException exception) {
						new SerializationException("Can't instantiate the " + field.getClassType() + " object, please instantiate it in the constructor!", exception).printStackTrace();
					}
				}
			}
		});
		return accessor.getObject();
	}
	
	/**
	 * Deserializes a collection of primitive types and complex objects using the
	 * {@link Accessor} and the {@link DataUtil} to convert the values.
	 * 
	 * @param parserCollection the collection to deserialize
	 * @param collectionType   the type of the collection, when serializes
	 * @param classType        the type of the collections entities
	 * @return the deserialized collection
	 * 
	 * @see {@link Accessor} {@link DataUtil}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Collection<?> deserializeCollection(Collection<?> parserCollection, Class<?> collectionType, Class<?> classType) {
		try {
			Collection collection = (Collection) Accessor.instantiate(collectionType);
			for (Object object : parserCollection) {
				if (object == null) {
					collection.add(null);
				} else if ((object instanceof String || object instanceof Character || object instanceof Boolean || object instanceof Number) && (
						classType.isPrimitive() || 
						String.class.isAssignableFrom(classType) ||
						Character.class.isAssignableFrom(classType) ||
						Boolean.class.isAssignableFrom(classType) ||
						Number.class.isAssignableFrom(classType))) {
					collection.add(ParsingUtils.convertObject(object, classType));
				} else if (object instanceof Collection<?>) {
					collection.add(deserializeCollection((Collection<?>) object, object.getClass(), classType));
				} else if (object instanceof KeyValueParser<?>) {
					try {
						collection.add(((KeyValueParser<?>) object).deserialize(new Accessor<>(classType)));
					} catch (AccessorException | IllegalArgumentException exception) {
						new SerializationException("Can't instantiate an " + classType + " object, please instantiate it in the constructor!", exception).printStackTrace();
					}
				}
			}
			return collection;
		} catch (AccessorException | IllegalArgumentException exception) {
			new SerializationException("Can't instantiate the " + classType + " object, please instantiate it in the constructor!", exception).printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the class from a generic parameter.
	 * 
	 * @param type     the type with the parameter
	 * @param position the position of the requested class
	 * @return the class of the generic parameter
	 * @throws SerializationException if the class can't be extracted
	 * 
	 * @see {@link ParameterizedType}
	 */
	private Class<?> extractGenericClass(Type type, int position) throws SerializationException {
		if (!(type instanceof ParameterizedType)) {
			throw new SerializationException("Can't extract generic types in " + type + "!");
		}
		if (position < 0 || position >= ((ParameterizedType) type).getActualTypeArguments().length) {
			throw new SerializationException("Can't extract generic types in " + type + "!", new IndexOutOfBoundsException(position));
		}
		final String name = ((ParameterizedType) type).getActualTypeArguments()[position].getTypeName();
		if (name.equals("?")) {
			return Object.class;
		} else if (!name.contains(" ")) {
    		try {
    			return Class.forName(name);
			} catch (ClassNotFoundException exception) {
				throw new SerializationException("Can't extract generic types in " + type + "!", exception);
			}
    	} else if (name.contains(" ")) {
    		throw new SerializationException("Can't extract class from generic wildcard <" + type + ">!");
    	}
		return null;
	}
	
	/**
	 * Gets the array as value by its key and check if it is primitive. The key null
	 * represents the root list. A primitive type is what isn't a
	 * {@link Collection}, an array or a {@link KeyValueParser}. All other values
	 * will be converted to strings and are not parsed as objects.
	 * 
	 * @param key the path that represents the value
	 * @return if the value represented by its key is primitive
	 */
	public boolean isPrimitive(String key) {
		if (!isPresent(key)) return false;
		return !isCollection(key) && !isArray(key) && !isObject(key);
	}

	/**
	 * Gets the array as value by its key and check if it is a {@link Collection}.
	 * The key null represents the root list.
	 * 
	 * @param key the path that represents the value
	 * @return if the value represented by its key is a {@link Collection}
	 */
	public boolean isCollection(String key) {
		if (!isPresent(key)) return false;
		return get(key) instanceof Collection<?>;
	}

	/**
	 * Gets the array as value by its key and check if it is an array. The key null
	 * represents the root list.
	 * 
	 * @param key the path that represents the value
	 * @return if the value represented by its key is an array
	 */
	public boolean isArray(String key) {
		if (!isPresent(key)) return false;
		return get(key).getClass().isArray();
	}

	/**
	 * Gets the array as value by its key and check if it is an object
	 * ({@link KeyValueParser}). The key null represents the root list.
	 * 
	 * @param key the path that represents the value
	 * @return if the value represented by its key is an object
	 *         ({@link KeyValueParser})
	 */
	public boolean isObject(String key) {
		if (containsKey(key)) return false;
		return getKeys(key).size() > 0;
	}

	/**
	 * Checks if the value of the key is present. The key null represents
	 * the root list. It will also return false if the value does not exist. If you
	 * want to check if the value exists please use {@link #containsKey(String)}.
	 * 
	 * @param key the path that represents the value
	 * @return if the value is present
	 */
	public boolean isPresent(String key) {
		return entries.get(key) != null;
	}

	/**
	 * Checks if the value of the key exists. The key null represents the
	 * root list. I will also return true if the value does exist but is null. If
	 * you want to check if the value is present please use
	 * {@link #isPresent(String)}.
	 * 
	 * @param key the path that represents the value
	 * @return if the value exist
	 */
	public boolean containsKey(String key) {
		return entries.containsKey(key);
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
	 * represented by its key.
	 * 
	 * @return the amount of entries
	 */
	public int size() {
		return entries.size();
	}

	/**
	 * Returns a {@link Set} of all keys in the parser. Each key represents a
	 * value, but the value can be null.
	 * 
	 * @return the amount of entries
	 */
	public List<String> getKeys() {
		return Collections.unmodifiableList(structure);
	}

	/**
	 * Returns a {@link List} of all keys in the parser, which are sub elements of
	 * the given key. Each key represents a value, but the value can be null.
	 * 
	 * @param key the path that contains the keys
	 * @return the amount of entries
	 */
	public List<String> getKeys(String key) {
		return getKeys().stream().filter(string -> string.startsWith(Objects.requireNonNull(key)))
				.collect(Collectors.toUnmodifiableList());
	}

	/**
	 * Iterates over all key value pairs in the parser.
	 * 
	 * @param action the action to iterate over for all key value pairs
	 */
	public void forEach(BiConsumer<String, Object> action) {
		for (String key : structure) action.accept(key, entries.get(key));
	}

	/**
	 * Sets the value to a given key. A key represents a value, but the value can be
	 * null. The key null represents the root list. A string key is a path separated
	 * by dots. Supported types are primitive types and their wrappers,
	 * {@link String}, {@link Collection} and superclasses, {@link Map} and
	 * superclasses and {@link KeyValueParser}. All other objects will be saved as
	 * {@link String} by their <tt>toString()</tt> method. The value will be
	 * overwritten, if the key already stores a value. Adding the root list with the
	 * key null will remove all existing entries. Adding entries while a root list
	 * exists will result in removing the list.
	 * 
	 * @param key   the path that represents the value
	 * @param value any object
	 */
	protected void setValue(String key, Object value) {
		if (key == null && !(value instanceof KeyValueParser<?> || value instanceof Map<?, ?>)) {
			if (value instanceof Collection<?> || (value != null && value.getClass().isArray())) {
				structure.clear();
				entries.clear();
				structure.add(null);
				entries.put(null, value);
			} else {
				throw new ParserException("The key null can only be used for Collection, Map and KeyValueParser!");
			}
		} else if (value instanceof Map<?, ?>) {
			((Map<?, ?>) value).forEach((mapKey, mapValue) -> {
				setValue((key == null ? "" : key + ".") + mapKey, mapValue);
			});
		} else if (value instanceof KeyValueParser) {
			((KeyValueParser<?>) value).forEach((parserKey, parserValue) -> {
				setValue(key + "." + parserKey, parserValue);
				setValue((key == null ? "" : key + ".") + parserKey, parserValue);
			});
		} else {
			if (containsKey(null)) remove(null);
			entries.put(key, value);
			if (structure.contains(key)) return;
			final String[] keys = key.split("\\.");
			String[] currentKeys;
			int layer = -1;
			for (int i = 0; i < structure.size(); i++) {
				currentKeys = structure.get(i).split("\\.");
				for (int j = 0; j < currentKeys.length && j < keys.length; j++) {
					if (currentKeys[j].equals(keys[j])) {
						if (j > layer)
							layer = j;
					} else if (j <= layer && layer != -1) {
						structure.add(i, key);
						return;
					} else {
						break;
					}
				}
			}
			structure.add(key);
		}
	}

	/**
	 * Gets the value by its key. The key null represents the root list. If there is
	 * no value then it will return null.
	 * 
	 * @param key the path that represents the value
	 * @return the value represented by its key
	 */
	protected Object getValue(String key) {
		if (key == null) return entries.get(key);
		final List<String> list = structure.stream().filter(path -> {
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
	 * will affect the output.
	 * 
	 * @return the ordered list of the keys
	 */
	protected List<String> getStructure() {
		return structure;
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
	 * Creates an instance of the {@link KeyValueParser} implementation and returns it.
	 * 
	 * @return the instance of the implementation
	 */
	protected abstract P getImplementationInstance();

}
