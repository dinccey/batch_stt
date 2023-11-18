package org.vaslim.batch_stt.constants;

import java.util.List;

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

    interface Files {
        List<String> transcribeExtensions = List.of(".srt",".vtt",".txt",".json",".tsv");
        List<String> ignoreExtensions = List.of("mp3");
    }
}
