package com.testSpring.exception;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;


@Configuration
public class SoapExceptionResolver {


    @Bean
    public SoapFaultMappingExceptionResolver exceptionResolver() {

        SoapFaultMappingExceptionResolver resolver =
                new SoapFaultMappingExceptionResolver();


        Properties mappings =
                new Properties();


        // =========================================
        // TREE NOT FOUND
        // =========================================

        mappings.setProperty(
                TreeNotFoundException.class.getName(),
                "CLIENT,Tree not found"
        );


        // =========================================
        // TREE ALREADY EXISTS
        // =========================================

        mappings.setProperty(
                TreeAlreadyExistsException.class.getName(),
                "CLIENT,Tree already exists"
        );


        // =========================================
        // GENERAL / UNKNOWN EXCEPTION
        // =========================================

        mappings.setProperty(
                Exception.class.getName(),
                "SERVER,Internal server error"
        );


        resolver.setExceptionMappings(mappings);

        resolver.setOrder(1);


        return resolver;
    }
}