package br.com.m4rc310.graphql.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The Class MMultiRegitry.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class MMultiRegitry<K, V> {
	
	/** The map. */
	private final ConcurrentMap<K, Set<V>> map = new ConcurrentHashMap<>();

	/**
	 * Adds the.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	public synchronized void add(K key, V value) {
		Set<V> set = map.get(key);
		if (set != null) {
			set.add(value);
		} else {
			map.put(key, createConcurrentSet(value));
		}
	}

	/**
	 * Removes the.
	 *
	 * @param key the key
	 */
	public synchronized void remove(K key) {
		Set<V> set = map.get(key);
		if (set != null) {
			//set.remove(value);
			//if (set.isEmpty()) {
				map.remove(key);
			//}
		}
	}

	/**
	 * Contains.
	 *
	 * @param key   the key
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean contains(K key, V value) {
		return get(key).contains(value);
	}

	/**
	 * Contains.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean contains(K key) {
		return !get(key).isEmpty();
	}

	/**
	 * Gets the keys.
	 *
	 * @param type the type
	 * @return the keys
	 */
	public List<K> getKeys(Class<?> type) {
		List<K> ret = new ArrayList<>();

		final String typeName = type.getSimpleName();
		map.forEach((k, v) -> {
			String skey = (String) k;
			if (skey.startsWith(typeName)) {
				ret.add(k);
			}
		});

		return ret == null ? Collections.<K>emptyList() : ret;
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the sets the
	 */
	public Set<V> get(K key) {
		Set<V> set = map.get(key);
		return set == null ? Collections.<V>emptySet() : set;
	}

	/**
	 * Creates the concurrent set.
	 *
	 * @param value the value
	 * @return the sets the
	 */
	protected Set<V> createConcurrentSet(V value) {
		Set<V> set = ConcurrentHashMap.newKeySet();
		set.add(value);
		return set;
	}

	

}