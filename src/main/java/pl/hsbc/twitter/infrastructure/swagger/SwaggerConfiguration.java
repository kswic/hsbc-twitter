package pl.hsbc.twitter.infrastructure.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static pl.hsbc.twitter.api.controller.TwitterApiController.API_TAG_TWITTER;

@EnableSwagger2
@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket api(@Value("${app.api.swagger.enabled}") boolean swaggerEnable) {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnable)
                .select()
                .apis(RequestHandlerSelectors.basePackage("pl.hsbc.twitter.api.controller"))
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .build()
                .tags(
                        new Tag(API_TAG_TWITTER, "Operations used for Twitter like app.")
                )
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Twitter like app REST API",
                "API of Twitter like demo application.",
                "unversioned",
                "Terms of service",
                new Contact("Konrad Swic", "", "konrad.swic@gmail.com"),
                "License of API", "TODO", Collections.emptyList()
        );
    }

}
