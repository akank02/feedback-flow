package ticket.system.feedbackFlow.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret",
                "test-secret-key-must-be-at-least-32-characters-long");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs",
                86400000L);
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = jwtUtil.generateToken("test@gmail.com", "USER");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractEmail_shouldReturnCorrectEmail() {
        String token = jwtUtil.generateToken("test@gmail.com", "USER");
        assertEquals("test@gmail.com", jwtUtil.extractEmail(token));
    }

    @Test
    void extractRole_shouldReturnCorrectRole() {
        String token = jwtUtil.generateToken("test@gmail.com", "ADMIN");
        assertEquals("ADMIN", jwtUtil.extractRole(token));
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken("test@gmail.com", "USER");
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalseForTamperedToken() {
        String token = jwtUtil.generateToken("test@gmail.com", "USER");
        String tampered = token + "tampered";
        assertFalse(jwtUtil.isTokenValid(tampered));
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() {
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", -1000L);
        String token = jwtUtil.generateToken("test@gmail.com", "USER");
        assertFalse(jwtUtil.isTokenValid(token));
    }
}