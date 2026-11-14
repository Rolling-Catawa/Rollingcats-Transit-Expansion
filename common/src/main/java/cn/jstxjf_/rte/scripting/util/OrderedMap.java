package cn.jstxjf_.rte.scripting.util;

import java.util.*;

public class OrderedMap<K, V> implements Map<K, V> {

    public enum PlacementOrder {
        UPPER, CENTRAL, LOWER, START, END
    }

    private final HashMap<K, V> valueMap = new LinkedHashMap<>();
    private final ArrayList<K> upperKeys = new ArrayList<>();
    private final ArrayList<K> centralKeys = new ArrayList<>();
    private final ArrayList<K> lowerKeys = new ArrayList<>();

    public OrderedMap() {
    }

    public OrderedMap(Map<? extends K, ? extends V> m) {
        putAll(m);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        putAll(m, PlacementOrder.CENTRAL);
    }

    public void putAll(Map<? extends K, ? extends V> m, PlacementOrder order) {
        if (m instanceof OrderedMap<?, ?> orderedMap) {
            OrderedMap<? extends K, ? extends V> other = (OrderedMap<? extends K, ? extends V>) orderedMap;
            for (K key : other.upperKeys) {
                put(key, other.valueMap.get(key), PlacementOrder.UPPER);
            }
            for (K key : other.centralKeys) {
                put(key, other.valueMap.get(key), PlacementOrder.CENTRAL);
            }
            for (K key : other.lowerKeys) {
                put(key, other.valueMap.get(key), PlacementOrder.LOWER);
            }
        } else {
            for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
                put(entry.getKey(), entry.getValue(), order);
            }
        }
    }

    @Override
    public V put(K key, V value) {
        return put(key, value, PlacementOrder.CENTRAL);
    }

    public V put(K key, V value, PlacementOrder order) {
        removeKeyFromOrder(key);
        switch (order) {
            case UPPER:
            case START:
                upperKeys.add(key);
                break;
            case LOWER:
            case END:
                lowerKeys.add(key);
                break;
            default:
                centralKeys.add(key);
                break;
        }
        return valueMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        removeKeyFromOrder(key);
        return valueMap.remove(key);
    }

    @Override
    public void clear() {
        valueMap.clear();
        upperKeys.clear();
        centralKeys.clear();
        lowerKeys.clear();
    }

    @Override
    public int size() {
        return valueMap.size();
    }

    @Override
    public boolean isEmpty() {
        return valueMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return valueMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return valueMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return valueMap.get(key);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new LinkedHashSet<>(entryList());
    }

    @Override
    public Set<K> keySet() {
        LinkedHashSet<K> keys = new LinkedHashSet<>();
        keys.addAll(upperKeys);
        keys.addAll(centralKeys);
        keys.addAll(lowerKeys);
        return keys;
    }

    @Override
    public Collection<V> values() {
        List<V> list = new ArrayList<>();
        for (K key : keySet()) {
            list.add(get(key));
        }
        return list;
    }

    public List<Map.Entry<K, V>> entryList() {
        List<Map.Entry<K, V>> list = new ArrayList<>();
        for (K key : upperKeys) {
            list.add(new Entry<>(key, valueMap.get(key)));
        }
        for (K key : centralKeys) {
            list.add(new Entry<>(key, valueMap.get(key)));
        }
        for (K key : lowerKeys) {
            list.add(new Entry<>(key, valueMap.get(key)));
        }
        return list;
    }

    private void removeKeyFromOrder(Object key) {
        upperKeys.remove(key);
        centralKeys.remove(key);
        lowerKeys.remove(key);
    }

    public class Entry<A, B> implements Map.Entry<A, B> {
        private final A key;
        private B value;

        public Entry(A key, B value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public A getKey() {
            return key;
        }

        @Override
        public B getValue() {
            return value;
        }

        @Override
        public B setValue(B value) {
            B oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }
}
