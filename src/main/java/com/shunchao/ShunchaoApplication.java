package com.shunchao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.swing.*;

//@SpringBootApplication
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class ShunchaoApplication {

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "程序已启动", "duou",JOptionPane.INFORMATION_MESSAGE);
        SpringApplication.run(ShunchaoApplication.class, args);
    }

}
