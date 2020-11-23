package com.shunchao.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class AccessDBConfig {
	@Value(value = "${access_data.char_set}")
	public String charSet;
	
	@Value(value = "${access_data.user}")
	public String user;
	
	@Value(value = "${access_data.password}")
	public String password;
}
