package com.unseenspace.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by madsk_000 on 8/30/2016.
 */
public class UserDeserializationTest {

    private static ObjectMapper objectMapper;
    private static AnySetterBeanDeserializerModifier modifier;

    @BeforeClass
    public static void beforeClass() {
        modifier = new AnySetterBeanDeserializerModifier();
        DeserializerFactory dFactory = BeanDeserializerFactory.instance.withDeserializerModifier(modifier);
        objectMapper = new ObjectMapper(null, null, new DefaultDeserializationContext.Impl(dFactory));
    }

    @Test
    public void groupByPermissions() throws IOException {
        modifier.put(User.class, (u, p, v) -> {
            if (p.startsWith("permission_") && (v instanceof Integer && !Objects.equals(v, 0) || Objects.equals(v, true) || Objects.equals(v, "true"))) {
                u.getPermissions().add(Permission.valueOf(p.substring("permission_".length()).toUpperCase()));
            }
        });

        User user = objectMapper.readValue(UserDeserializationTest.class.getResourceAsStream("FlatUser.json"), User.class);

        assertThat(user, hasProperty("username", is("flat user")));
        assertThat(user, hasProperty("password", is("password")));
        assertThat(user, hasProperty("permissions", hasItems(Permission.LEFT, Permission.UP, Permission.DOWN)));
        System.out.println(user);
    }
}
