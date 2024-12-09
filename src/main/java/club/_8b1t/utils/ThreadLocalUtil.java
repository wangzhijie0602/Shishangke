package club._8b1t.utils;

/**
 * @author 8bit
 */
public class ThreadLocalUtil {

    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    public static void setToken(String username) {
        THREAD_LOCAL.set(username);
    }

    public static String getToken() {
        return THREAD_LOCAL.get();
    }

    public static void clearToken() {
        THREAD_LOCAL.remove();
    }
}
