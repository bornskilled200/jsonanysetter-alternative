package com.unseenspace.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.FieldProperty;
import com.fasterxml.jackson.databind.deser.impl.InnerClassProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

/**
 * Created by madsk_000 on 8/30/2016.
 */
public class UserDeserializationTest {

    private static ObjectMapper objectMapper;

    @BeforeClass
    public static void beforeClass() {
        BeanDeserializerModifier modifier = new PermissionGrouper();
        DeserializerFactory dFactory = BeanDeserializerFactory.instance.withDeserializerModifier(modifier);
        objectMapper = new ObjectMapper(null, null, new DefaultDeserializationContext.Impl(dFactory));
    }

    public static class PermissionGrouper extends BeanDeserializerModifier {

        @Override
        public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
            if (!User.class.equals(beanDesc.getBeanClass())) {
                return builder;
            }

            JavaType type = config.constructType(Object.class);
            builder.setAnySetter(new MySettableAnyProperty(type));

            return builder;
        }

        private static class MySettableAnyProperty extends SettableAnyProperty {
            public MySettableAnyProperty(JavaType type) {
                super(null, null, type, type.getValueHandler(), type.getTypeHandler());
            }

            public MySettableAnyProperty(BeanProperty property, AnnotatedMember setter, JavaType type, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser) {
                super(property, setter, type, valueDeser, typeDeser);
            }

            @Override
            public SettableAnyProperty withValueDeserializer(JsonDeserializer<Object> deser) {
                return new MySettableAnyProperty(_property, _setter, _type, deser, _valueTypeDeserializer);
            }

            @Override
            public void set(Object instance, String propName, Object value) throws IOException {
                User user = (User) instance;
                if (propName.startsWith("permission_") && (value instanceof Integer && !Objects.equals(value, 0) || Objects.equals(value, true) || Objects.equals(value, "true"))) {
                    user.getPermissions().add(Permission.valueOf(propName.substring("permission_".length()).toUpperCase()));
                }
                System.out.println(propName);
            }
        }
    }

    @Test
    public void groupByPermissions() throws IOException {
        User user = objectMapper.readValue(UserDeserializationTest.class.getResourceAsStream("FlatUser.json"), User.class);

        assertThat(user, hasProperty("name", is("flat user")));
        assertThat(user, hasProperty("password", is("password")));
        assertThat(user, hasProperty("permissions", hasItems(Permission.LEFT, Permission.UP, Permission.DOWN)));
        System.out.println(user);
    }
}
