package org.vaslim.batch_stt.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.locks.ReentrantLock;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper;
    }

    @Bean
    public ReentrantLock fileRefreshTaskReentrantLock(){
        return new ReentrantLock();
    }
    @Bean
    public ReentrantLock fileProcessingTaskReentrantLock(){
        return new ReentrantLock();
    }


}
