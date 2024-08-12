package br.com.m4rc310.gql.services;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.reactivestreams.Publisher;

import br.com.m4rc310.gql.dto.MUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * The Class MFluxService.
 *
 * @author marcelo
 * @version $Id: $Id
 */
public class MFluxService {
	/** The registry. */
	protected final MMultiRegitry<String, Object> registry = new MMultiRegitry<>();

	private MUser user;

	/**
	 * Publish.
	 *
	 * @param <T>  the generic type
	 * @param type the type
	 * @param key  the key
	 * @return the publisher
	 */
	public <T> Publisher<T> publish(Class<T> type, Object key) {
		return publish(type, key, null);
	}

	/**
	 * <p>
	 * publish.
	 * </p>
	 *
	 * @param type         a {@link java.lang.Class} object
	 * @param key          a {@link java.lang.Object} object
	 * @param defaultValue a T object
	 * @param <T>          a T class
	 * @return a {@link org.reactivestreams.Publisher} object
	 */
	public <T> Publisher<T> publish(Class<T> type, Object key, T defaultValue) {
		String skey = makeId(type, key);
//		return (Publisher<T>) Flux.create(fs -> {
//			registry.add(skey, fs.onDispose(() -> registry.remove(skey)));
//			if (Objects.nonNull(defaultValue)) {
//				fs.next(defaultValue);
//			}
//		}, FluxSink.OverflowStrategy.BUFFER);

		return Flux.create(fs -> {
			registry.add(skey, fs.onDispose(() -> registry.remove(skey)));
			if (Objects.nonNull(defaultValue)) {
				fs.next(defaultValue);
			}
		}, FluxSink.OverflowStrategy.BUFFER);
	}

	/**
	 * <p>
	 * getSizeRegistries.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object
	 * @return a {@link java.lang.Integer} object
	 */
	public Integer getSizeRegistries(Class<?> type) {
		return registry.getSizeRegistries(type);
	}

	/**
	 * <p>
	 * publishList.
	 * </p>
	 *
	 * @param type         a {@link java.lang.Class} object
	 * @param key          a {@link java.lang.Object} object
	 * @param defaultValue a {@link java.util.List} object
	 * @param <T>          a T class
	 * @return a {@link reactor.core.publisher.Flux} object
	 */
	public <T> Flux<List<T>> publishList(Class<T> type, Object key, List<T> defaultValue) {
		String skey = makeId(type, key);
		return Flux.create(fs -> {
			registry.add(skey, fs.onDispose(() -> registry.remove(skey)));
			fs.next(defaultValue);

		}, FluxSink.OverflowStrategy.BUFFER);
	}

	/**
	 * In publish.
	 *
	 * @param type the type
	 * @param key  the key
	 * @return true, if successful
	 */
	public boolean inPublish(Class<?> type, Object key) {
		String skey = makeId(type, key);
		return registry.contains(skey);
	}

	/**
	 * Call publish.
	 *
	 * @param <T>   the generic type
	 * @param key   the key
	 * @param value the value
	 * @throws java.lang.Exception the exception
	 */
	@SuppressWarnings("unchecked")
	public <T> void callPublish(Object key, T value) throws Exception {
		if (value == null) {
			throw new Exception("Value is null");
		}

		Class<?> type = value.getClass();

		if (!inPublish(type, key)) {
			throw new Exception("No published listener!");
		}

		String skey = makeId(type, key);

		List<FluxSink<?>> sinks = registry.get(skey);
		if (sinks != null) {
			sinks.forEach(sub -> ((FluxSink<T>) sub).next(value));
		}

		// registry.get(skey).forEach(sub -> sub.next(value));
	}

	/**
	 * Call publish from type.
	 *
	 * @param <T>   the generic type
	 * @param value the value
	 * @throws java.lang.Exception the exception
	 */
	public <T> void callPublish(Class<T> type, T value) throws Exception {
		registry.getKeys(type).forEach((key) -> {
			try {
				callPublish(key, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

	/**
	 * Removes the publish.
	 *
	 * @param <T>  the generic type
	 * @param type the type
	 * @param key  the key
	 */
	public <T> void removePublish(Class<T> type, String key) {
		String skey = makeId(type, key);
		registry.remove(skey);
	}

	/**
	 * Call publish.
	 *
	 * @param <T>   the generic type
	 * @param type  the type
	 * @param key   the key
	 * @param value the value
	 * @throws java.lang.Exception the exception
	 */
	@SuppressWarnings("unchecked")
	public <T> void callPublish(Class<T> type, Object key, T value) throws Exception {
		String skey = makeId(type, key);
		// registry.get(skey).forEach(sub -> sub.next(value));
		List<FluxSink<?>> sinks = registry.get(skey);
		if (sinks != null) {
			sinks.forEach(sub -> ((FluxSink<T>) sub).next(value));
		}
	}

	/**
	 * <p>
	 * callListPublish.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object
	 * @param key  a {@link java.lang.Object} object
	 * @param list a {@link java.util.List} object
	 * @param <T>  a T class
	 */
	@SuppressWarnings("unchecked")
	public <T> void callListPublish(Class<T> type, Object key, List<T> list) {
		String skey = makeId(type, key);
		List<FluxSink<?>> sinks = registry.get(skey);
		if (sinks != null) {
			sinks.forEach(sub -> ((FluxSink<List<T>>) sub).next(list));
		}
	}

	/**
	 * Authenticated user.
	 *
	 * @return the m user
	 */
//	public MUser authenticatedUser() {
//		try {
//			return (MUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		} catch (Exception e) {
//			return null;
//		}
//	}

	/**
	 * Make id.
	 *
	 * @param type the type
	 * @param key  the key
	 * @return the string
	 */
	private String makeId(Class<?> type, Object key) {
		return String.format("%s-%s", type.getSimpleName(), key);
	}

	/**
	 * Clone ato B.
	 *
	 * @param a the a
	 * @param b the b
	 */
	public void cloneAtoB(Object a, Object b) {

		List<Field> fas = listAllFields(a);
		List<Field> fbs = listAllFields(b);

		fas.forEach(fa -> {
			try {
				fa.setAccessible(true);
				fbs.forEach(fb -> {
					fb.setAccessible(true);
					if (fa.getName().equals(fb.getName())) {
						try {
							Object value = fa.get(a);
							if (Objects.nonNull(value)) {
								try {
									if (Objects.isNull(fb.get(b))) {
										fb.set(b, value);
									}
								} catch (Exception e) {
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// Class<?> typeA = listAllFields(a);
		// Class<?> typeB = b.getClass();

//		listAllFields(a).forEach(fieldA -> {
		// try {
//				for(Field fieldA : typeA.getDeclaredFields()) {
//					fieldA.setAccessible(true);
//					
//					for(Field fieldB : typeB.getDeclaredFields()) {
//						
//					}
//					
//					
//					//log.info("{}", field.get(a));
//				}
//				
//				
//				
//				Class<?> typeB = b.getClass();
//				
//				
//				
//				
//				Field fieldB = typeB.getField(fieldA.getName());
//				if (Objects.nonNull(fieldB) && Objects.isNull(fieldB.get(b))) {
//					fieldB.setAccessible(true);
//					fieldB.set(b, fieldA.get(a));
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
	}

	/**
	 * Gets the field from type.
	 *
	 * @param type      the type
	 * @param typeField the type field
	 * @return the field from type
	 */
	public Field getFieldFromType(Class<?> type, Class<?> typeField) {
		List<Field> fields = new ArrayList<>();
		fields.addAll(Arrays.asList(type.getFields()));
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		for (Field field : fields) {
			field.setAccessible(true);
			if (field.getType().getName().equals(typeField.getName())) {
				return field;
			}
		}
		;

		return null;
	}

	/**
	 * List all fields.
	 *
	 * @param o the o
	 * @return the list
	 */
	private List<Field> listAllFields(Object o) {
		List<Field> fields = new ArrayList<>();
		Class<?> type = o.getClass();

		fields.addAll(Arrays.asList(type.getFields()));
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		return fields;
	}

	public void setUser(MUser user) {
		this.user = user;
	}

	public MUser getUser() {
		return user;
	}
}
