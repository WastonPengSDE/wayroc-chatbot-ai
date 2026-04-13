package com.wayroc.wayrocchatbot.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = JwtUtils.class)
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-must-be-at-least-32-bytes-long!!",
        "jwt.expire-seconds=3600"
})
class JwtUtilsTest {

    private static final String TEST_SECRET = "test-secret-key-must-be-at-least-32-bytes-long!!";

    @Autowired
    private JwtUtils jwtUtils;
    @Test
    void test_generateAndParseToken() {
        long userId = 1001L;
        String token = jwtUtils.generateToken(userId);

        assertThat(jwtUtils.getUserIdFromToken(token)).isEqualTo(userId);
    }

    @Test
    void test_BearerToken() {
        long userId = 2002L;
        String token = jwtUtils.generateToken(userId);

        assertThat(jwtUtils.getUserIdFromToken("Bearer " + token)).isEqualTo(userId);
        assertThat(jwtUtils.getUserIdFromToken("Bearer  " + token)).isEqualTo(userId);
    }

    @Test
    void test_invalidToken() {
        assertThat(jwtUtils.getUserIdFromToken("not-a-jwt")).isNull();
    }
}
