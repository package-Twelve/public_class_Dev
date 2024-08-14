package com.sparta.publicclassdev.domain.users.entity;

public enum RoleEnum {
    USER(Authority.USER),
    ADMIN(Authority.ADMIN),
    WITHDRAW(Authority.WITHDRAW);
    private final String authority;

    public String getAuthority() {
        return this.authority;
    }
    RoleEnum(String authority) {
        this.authority = authority;
    }
    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String WITHDRAW = "ROLE_WITHDRAW";
    }
}
