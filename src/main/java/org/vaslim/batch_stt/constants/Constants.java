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
        List<String> TRANSCRIBE_EXTENSIONS = List.of(".srt",".vtt",".txt",".json",".tsv");
        List<String> IGNORE_EXTENSIONS = List.of("mp3","zip","jpg","png","tar", "gz", "pdf");
    }
}
