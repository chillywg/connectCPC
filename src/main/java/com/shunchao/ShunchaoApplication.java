package com.shunchao;

import com.shunchao.cpc.util.buttonListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

import javax.swing.*;
import java.awt.*;

//@SpringBootApplication
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class ShunchaoApplication {

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "程序已启动", "duou",JOptionPane.INFORMATION_MESSAGE);
        SpringApplication.run(ShunchaoApplication.class, args);
    }

}
