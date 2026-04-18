package com.wayroc.wayrocchatbot.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {JwtUtils.class, LoginFilter.class})
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-must-be-at-least-32-bytes-long!!",
        "jwt.expire-seconds=3600"
})
class LoginFilterTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private LoginFilter loginFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void excludePath_login_passesThroughWithoutAuth() throws Exception {
        CapturingChain chain = new CapturingChain();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/user/login");
        MockHttpServletResponse res = new MockHttpServletResponse();

        loginFilter.doFilter(req, res, chain);

        assertThat(chain.called).isTrue();
        assertThat(res.getStatus()).isEqualTo(200);
    }

    @Test
    void protectedPath_noAuthorization_returns401() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/chart/genchart");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        loginFilter.doFilter(req, res, chain);

        assertThat(res.getStatus()).isEqualTo(401);
        assertThat(res.getContentType()).contains("application/json");
        JsonNode body = objectMapper.readTree(res.getContentAsString());
        assertThat(body.get("code").asInt()).isEqualTo(40100);
        assertThat(body.get("message").asText()).contains("not login");
    }

    @Test
    void protectedPath_invalidToken_returns401() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/chart/genchart");
        req.addHeader("Authorization", "Bearer totally-invalid");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        loginFilter.doFilter(req, res, chain);

        assertThat(res.getStatus()).isEqualTo(401);
    }

    @Test
    void protectedPath_validBearerToken_setsLoginUserIdAndContinuesChain() throws Exception {
        long userId = 777L;
        String token = jwtUtils.generateToken(userId);

        CapturingChain chain = new CapturingChain();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/chart/genchart");
        req.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse res = new MockHttpServletResponse();

        loginFilter.doFilter(req, res, chain);

        assertThat(chain.called).isTrue();
        assertThat(req.getAttribute(LoginFilter.LOGIN_USER_ID)).isEqualTo(userId);
        assertThat(res.getStatus()).isEqualTo(200);
    }

    @Test
    void protectedPath_validRawTokenWithoutBearer_setsLoginUserId() throws Exception {
        long userId = 888L;
        String token = jwtUtils.generateToken(userId);

        CapturingChain chain = new CapturingChain();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/chart/genchart");
        req.addHeader("Authorization", token);
        MockHttpServletResponse res = new MockHttpServletResponse();

        loginFilter.doFilter(req, res, chain);

        assertThat(chain.called).isTrue();
        assertThat(req.getAttribute(LoginFilter.LOGIN_USER_ID)).isEqualTo(userId);
    }

    private static final class CapturingChain implements FilterChain {
        boolean called;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response)
                throws IOException, ServletException {
            called = true;
        }
    }
}
