package com.channelsoft.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.channelsoft.workattent.controller")
public class DispatcherServletConfig extends WebMvcConfigurerAdapter{

    /**
     * {@inheritDoc}
     * <p>This implementation is empty.
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {

        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setSuffix(".jsp");
        resolver.setPrefix("/views/");
        registry.viewResolver(resolver);
    }

    /**
     * {@inheritDoc}
     * <p>This implementation is empty.
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {

        configurer.enable();
    }
}
