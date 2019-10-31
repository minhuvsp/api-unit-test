package com.vsp.api.audit.service;

import com.vsp.il.util.Preferences;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;


/**
 * Spring application context class.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.vsp.api.productbusupdate", "com.vsp.api.audit.service"})
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() throws URISyntaxException {

        if (!Preferences.initialized()) {
            Preferences.initialize();
        }
    }


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

    private static Class<Application> applicationClass = Application.class;
}

