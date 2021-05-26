package com.rl.mpquoridor.models.common;

import com.google.common.collect.BiMap;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.transaction.NotSupportedException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConcurrentHashBiMap<K, V> implements BiMap <K, V> {

    private ConcurrentHashMap<K, V> hashMap;
    private ConcurrentHashMap<V, K> reverseHashMap;

    public ConcurrentHashBiMap(ConcurrentHashMap<K, V> hashMap, ConcurrentHashMap<V, K> reverseHashMap) {
        this.hashMap = hashMap;
        this.reverseHashMap = reverseHashMap;
    }

    public ConcurrentHashBiMap() {
        this.hashMap = new ConcurrentHashMap<>();
        this.reverseHashMap = new ConcurrentHashMap<>();
    }

    @Nullable
    @Override
    public V put(@Nullable K key, @Nullable V value) {
        V lastValue = hashMap.get(key);

        if (lastValue != null)
            reverseHashMap.remove(lastValue);

        hashMap.put(key, value);
        reverseHashMap.put(value, key);
        return value;
    }

    @Nullable
    @Override
    public V forcePut(@Nullable K key, @Nullable V value) {
        throw new UnsupportedOperationException("This method not supported!");
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        map.entrySet().forEach(entry -> put(entry.getKey(), entry.getValue()));
    }

    @Override
    public Set<V> values() {
        return new HashSet<>(hashMap.values());
    }

    @Override
    public BiMap<V, K> inverse() {
        return new ConcurrentHashBiMap<>(reverseHashMap, hashMap);
    }

    @Override
    public int size() {
        return hashMap.size();
    }

    @Override
    public boolean isEmpty() {
        return hashMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return hashMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return hashMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return hashMap.get(key);
    }

    @Override
    public V remove(Object key) {
        V value = hashMap.remove(key);
        reverseHashMap.remove(value);
        return value;
    }

    @Override
    public void clear() {
        hashMap.clear();
        reverseHashMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return new HashSet<>(hashMap.keySet());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new HashSet<>(hashMap.entrySet());
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("This method not supported!");
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("This method not supported!");
    }
}
