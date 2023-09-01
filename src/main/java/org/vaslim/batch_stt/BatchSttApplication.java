package org.vaslim.batch_stt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.vaslim.batch_stt.configuration.WhisperApiConfig;

@SpringBootApplication
@Import(WhisperApiConfig.class)
public class BatchSttApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchSttApplication.class, args);
	}

}
