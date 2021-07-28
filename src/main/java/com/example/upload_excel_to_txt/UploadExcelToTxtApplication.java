package com.example.upload_excel_to_txt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@SpringBootApplication
public class UploadExcelToTxtApplication extends SpringBootServletInitializer{

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(UploadExcelToTxtApplication.class);
    }

	public static void main(String[] args) {
		SpringApplication.run(UploadExcelToTxtApplication.class, args);
	}

	// @Bean(name = "multipartResolver")
    // public CommonsMultipartResolver multipartResolver(){
    //     CommonsMultipartResolver resolver = new CommonsMultipartResolver();
    //     resolver.setDefaultEncoding("UTF-8");
    //     resolver.setMaxInMemorySize(-1);
    //     return  resolver;
    // }


}
