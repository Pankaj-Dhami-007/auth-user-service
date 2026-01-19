package com.company.auth.contextHolder;

public class ClientContextHolder {

    private static final ThreadLocal<Long> CLIENT_ID = new ThreadLocal<>();

    private ClientContextHolder() {}

    public static void setClientId(Long clientId) {
        CLIENT_ID.set(clientId);
    }

    public static Long getClientId() {
        return CLIENT_ID.get();
    }

    public static void clear() {
        CLIENT_ID.remove();
    }
}
