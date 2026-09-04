package cn.jstxjf_.rte.scripting.compat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ScriptLongMap<V> implements Iterable<Object[]> {

    private final java.util.concurrent.ConcurrentHashMap<Long, V> backing = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public java.util.Iterator<Object[]> iterator() {
        final List<Object[]> pairs = new ArrayList<>(backing.size());
        for (final Map.Entry<Long, V> entry : backing.entrySet()) {
            pairs.add(new Object[]{entry.getKey(), entry.getValue()});
        }
        return pairs.iterator();
    }

    public V put(long key, V value) {
        return backing.put(key, value);
    }

    public V putIfAbsent(long key, V value) {
        return backing.putIfAbsent(key, value);
    }

    public V get(long key) {
        return backing.get(key);
    }

    public boolean containsKey(long key) {
        return backing.containsKey(key);
    }

    public V computeIfAbsent(long key, Function<? super Long, ? extends V> mappingFunction) {
        return backing.computeIfAbsent(key, mappingFunction);
    }

    public void clear() {
        backing.clear();
    }

    public int size() {
        return backing.size();
    }

    public boolean isEmpty() {
        return backing.isEmpty();
    }

    public Set<Long> keySet() {
        return backing.keySet();
    }

    public Collection<V> values() {
        return backing.values();
    }

    public Collection<Entry<V>> entrySet() {
        final List<Entry<V>> entries = new ArrayList<>(backing.size());
        for (final Map.Entry<Long, V> entry : backing.entrySet()) {
            entries.add(new Entry<>(entry.getKey(), entry.getValue()));
        }
        return entries;
    }

    public static final class Entry<V> {

        private final long key;
        private final V value;

        Entry(long key, V value) {
            this.key = key;
            this.value = value;
        }

        public long getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
