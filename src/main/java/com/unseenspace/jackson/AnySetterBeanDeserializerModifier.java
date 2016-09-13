package com.unseenspace.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by madsk_000 on 9/12/2016.
 */
public class AnySetterBeanDeserializerModifier extends BeanDeserializerModifier {

    private Map<Class<?>, AnySetter<?>> anySetterMap = new HashMap<>();

    private class MySettableAnyProperty extends SettableAnyProperty {
        private AnySetter anySetter;

        MySettableAnyProperty(BeanProperty property, AnnotatedMember setter, JavaType type, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser, AnySetter<?> anySetter) {
            super(property, setter, type, valueDeser, typeDeser);
            this.anySetter = anySetter;
        }

        @Override
        public SettableAnyProperty withValueDeserializer(JsonDeserializer<Object> deser) {
            return new MySettableAnyProperty(_property, _setter, _type, deser, _valueTypeDeserializer, anySetter);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void set(Object instance, String propName, Object value) throws IOException {
            anySetter.set(instance, propName, value);
        }
    }

    public <T> void put(Class<T> key, AnySetter<T> value) {
        anySetterMap.put(key, value);
    }

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
        if (!anySetterMap.containsKey(beanDesc.getBeanClass())) {
            return builder;
        }

        JavaType type = config.constructType(Object.class);
        builder.setAnySetter(new MySettableAnyProperty(null, null, type, type.getValueHandler(), type.getTypeHandler(), anySetterMap.get(beanDesc.getBeanClass())));

        return builder;
    }
}
