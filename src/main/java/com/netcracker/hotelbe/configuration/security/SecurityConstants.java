package com.netcracker.hotelbe.configuration.security;

public class SecurityConstants {
    public static final String AUTH_LOGIN_URL = "/authenticate";
    public static final String JWT_SECRET = "6v9y$B&E)H@McQfTjWnZr4t7w!z%C*F-JaNdRgUkXp2s5v8x/A?D(G+KbPeShVmY";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "secure-api";
    public static final String TOKEN_AUDIENCE = "secure-app";
    public static final Integer TOKEN_LIFETIME = 10800000;

    private SecurityConstants() {
        throw new IllegalStateException("Cannot create instance of static util class");
    }
}
