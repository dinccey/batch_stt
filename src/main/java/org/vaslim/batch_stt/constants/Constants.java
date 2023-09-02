package org.vaslim.batch_stt.constants;

public interface Constants
{
    interface Endpoint{
        String ALL_PATHS = "/**";
    }

    interface SecurityConstants {
        String SECRET = "SECRET_KEY";
        long EXPIRATION_TIME = 900_000; // 15 mins
        String TOKEN_PREFIX = "Bearer ";
        String HEADER_STRING = "Authorization";
    }
}
