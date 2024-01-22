package com.shunchao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter(){
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();

        //是否允许请求带有验证信息
        corsConfiguration.setAllowCredentials(true);
        //允许访问的客户端域名
        corsConfiguration.addAllowedOrigin("http://user.duou.com");
//        corsConfiguration.addAllowedOrigin("http://192.168.1.177:8093");
//        corsConfiguration.addAllowedOrigin("http://192.168.1.106:3000");
//        corsConfiguration.addAllowedOrigin("http://192.168.1.215:3000");
        //允许服务端访问的客户端请求头
        corsConfiguration.addAllowedHeader("*");
        //允许访问的方法名,GET POST等
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setMaxAge(50000000l);
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    /*@Bean
    public WebMvcConfigurer webMvcConfigurer(){
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //super.addCorsMappings(registry);
                registry.addMapping("/**").allowCredentials(true).allowedOrigins("*");
            }
        };
    }*/
}
