package br.com.m4rc310.gql.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import reactor.core.publisher.FluxSink;

/**
 * <p>MMultiRegitry class.</p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
public class MMultiRegitry<K, T> {
	
	  private final ConcurrentMap<K, List<FluxSink<?>>> map = new ConcurrentHashMap<>();

	    /**
	     * <p>add.</p>
	     *
	     * @param key a K object
	     * @param value a {@link reactor.core.publisher.FluxSink} object
	     */
	    public synchronized void add(K key, FluxSink<?> value) {
	        List<FluxSink<?>> list = map.get(key);
	        if (list != null) {
	            list.add(value);
	        } else {
	            List<FluxSink<?>> newList = new CopyOnWriteArrayList<>();
	            newList.add(value);
	            map.put(key, newList);
	        }
	    }

	    /**
	     * <p>remove.</p>
	     *
	     * @param key a K object
	     */
	    public synchronized void remove(K key) {
	        map.remove(key);
	    }

	    /**
	     * <p>contains.</p>
	     *
	     * @param key a K object
	     * @param value a {@link reactor.core.publisher.FluxSink} object
	     * @return a boolean
	     */
	    public boolean contains(K key, FluxSink<?> value) {
	        List<FluxSink<?>> list = get(key);
	        return list != null && list.contains(value);
	    }

	    /**
	     * <p>contains.</p>
	     *
	     * @param key a K object
	     * @return a boolean
	     */
	    public boolean contains(K key) {
	        return map.containsKey(key);
	    }

	    /**
	     * <p>getKeys.</p>
	     *
	     * @param type a {@link java.lang.Class} object
	     * @return a {@link java.util.List} object
	     */
	    public List<K> getKeys(Class<?> type) {
	        List<K> ret = new ArrayList<>();

	        final String typeName = type.getSimpleName();
	        map.forEach((k, v) -> {
	            String skey = k.toString();
	            if (skey.startsWith(typeName)) {
	                ret.add(k);
	            }
	        });

	        return ret == null ? Collections.emptyList() : ret;
	    }
	    /**
	     * <p>getSizeRegistries.</p>
	     *
	     * @param type a {@link java.lang.Class} object
	     * @return a {@link java.lang.Integer} object
	     */
	    public Integer getSizeRegistries(Class<?> type) {
	    	try {
				return getKeys(type).size();
			} catch (Exception e) {
				return 0;
			}
	    }

	    /**
	     * <p>get.</p>
	     *
	     * @param key a K object
	     * @return a {@link java.util.List} object
	     */
	    public List<FluxSink<?>> get(K key) {
	        return map.getOrDefault(key, Collections.emptyList());
	    }

	    /**
	     * <p>createFluxSinkList.</p>
	     *
	     * @param value a {@link reactor.core.publisher.FluxSink} object
	     * @return a {@link java.util.List} object
	     */
	    protected List<FluxSink<?>> createFluxSinkList(FluxSink<?> value) {
	        List<FluxSink<?>> list = new CopyOnWriteArrayList<>();
	        list.add(value);
	        return list;
	    }

	    // Adicionando método para publicar valor para todas as instâncias de FluxSink associadas a uma chave
	    /**
	     * <p>publishValue.</p>
	     *
	     * @param key a K object
	     * @param value a T object
	     */
	    @SuppressWarnings("unchecked")
		public void publishValue(K key, T value) {
	        List<FluxSink<?>> sinks = map.get(key);
	        if (sinks != null) {
	            sinks.forEach(sub -> ((FluxSink<T>) sub).next(value));
	        }
	    }

}
