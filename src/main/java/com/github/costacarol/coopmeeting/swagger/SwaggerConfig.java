package com.github.costacarol.coopmeeting.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

import static springfox.documentation.builders.PathSelectors.regex;


@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.costacarol.coopmeeting"))
                .paths(regex("/meeting.*"))
                .build()
                .apiInfo(metaInfo());
    }

    private ApiInfo metaInfo(){
        ApiInfo apiInfo = new ApiInfo(
                "CoopMeeting APi",
                "API REST of a cooperative meeting.",
                "1.0",
                "Terms of Service",
                new Contact("Carolina da Costa", "https://carolinadacosta.000webhostapp.com/",
                        "caroliscosta.ti@gmail.com"),
                "",
                "", new ArrayList<>());
        return apiInfo;
    }
}
