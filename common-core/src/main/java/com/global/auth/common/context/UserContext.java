package com.global.auth.common.context;

/**
 * 현재 요청을 보낸 사용자 정보를 ThreadLocal에 관리
 */
public class UserContext {
    private static final ThreadLocal<String> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> correlationId = new ThreadLocal<>();

    public static void setUserId(String id) { userId.set(id); }
    public static String getUserId() { return userId.get(); }
    
    public static void setCorrelationId(String id) { correlationId.set(id); }
    public static String getCorrelationId() { return correlationId.get(); }

    public static void clear() {
        userId.remove();
        correlationId.remove();
    }
}
