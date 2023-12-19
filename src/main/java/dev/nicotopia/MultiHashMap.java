package dev.nicotopia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiHashMap<K, V> extends HashMap<K, List<V>> {
    public List<V> getOrEmptyList(K key) {
        List<V> l = this.get(key);
        if (l == null) {
            this.put(key, l = new ArrayList<>());
        }
        return l;
    }

    public List<V> removeOrEmptyList(String key) {
        List<V> l = this.remove(key);
        return l == null ? Collections.emptyList() : l;
    }

    public MultiHashMap<K, V> addAllToLists(Map<K, List<V>> other) {
        other.entrySet().forEach(e -> this.getOrEmptyList(e.getKey()).addAll(e.getValue()));
        return this;
    }
}
