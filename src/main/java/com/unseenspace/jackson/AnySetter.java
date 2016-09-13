package com.unseenspace.jackson;

/**
 * Created by madsk_000 on 9/11/2016.
 */
public interface AnySetter<T> {
    void set(T t, String property, Object value);
}
