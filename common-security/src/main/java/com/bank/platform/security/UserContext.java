package com.bank.platform.security;

public class UserContext {
    private static final ThreadLocal<String> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> role = new ThreadLocal<>();
    private static final ThreadLocal<String> username = new ThreadLocal<>();

//    public static void set(String userId, String role, String username) {
////        UserContext.userId.set(userId);
//        UserContext.role.set(role);
//        UserContext.username.set(username);
//    }

    public static void set(String role, String username) {
        UserContext.role.set(role);
        UserContext.username.set(username);
    }

    public static String getUserId() {
        return userId.get();
    }

    public static String getRole() {
        return role.get();
    }

    public static String getUsername() {
        return username.get();
    }

    public static void clear() {
        userId.remove();
        role.remove();
        username.remove();
    }
}
