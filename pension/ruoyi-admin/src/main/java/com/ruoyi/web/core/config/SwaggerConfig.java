package com.ruoyi.web.core.config;

import com.ruoyi.common.config.RuoYiConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private static final String TOKEN_KEY = "Authorization";

/** 系统基础配置 */

    @Autowired
    private RuoYiConfig ruoyiConfig;


    /**
    * 添加分组
     * @return
     */

    @Bean
    public GroupedOpenApi owonApi() {
        return GroupedOpenApi.builder()
                .group("owon")
                .pathsToMatch("/owon/**")
                .packagesToScan("com.ruoyi.pension")
                .build();
    }
    @Bean
    public GroupedOpenApi biolandApi() {
        return GroupedOpenApi.builder()
                .group("bioland")
                .pathsToMatch("/bioland/data")
                .packagesToScan("com.ruoyi.pension.bioland")
                .build();
    }
    @Bean
    public GroupedOpenApi generalApi() {
        return GroupedOpenApi.builder()
                .group("general")
                .pathsToMatch("/general/**")
                .packagesToScan("com.ruoyi.pension")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(info())
                .externalDocs(externalDocumentation())
                .components(new Components()
                        .addSecuritySchemes(TOKEN_KEY, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                        .addHeaders(TOKEN_KEY, new Header().required(true)
                                .description("token")
                                .schema(new StringSchema()).required(true)
                        )
                )
                //全局请求头参数添加: knife4j无法识别,需在api中单独添加
                // @Operation(security = { @SecurityRequirement(name = "bearer-key") })
                .addSecurityItem(new SecurityRequirement().addList(TOKEN_KEY));

    }


/**
     * 文档信息
     * @return
     */

    private Info info(){
        return new Info().title("智慧养老平台-接口文档")
                .description("用于系统部分接口测试")
                .version("版本号:" + ruoyiConfig.getVersion())
                .license(license())
                .contact(new Contact().name(ruoyiConfig.getName()));
    }
    private License license() {
        return new License()
                .name("MIT")
                .url("https://opensource.org/licenses/MIT");//开源协议
    }

/**
     * 外部文档信息
     * @return
     */

    private ExternalDocumentation externalDocumentation() {
        return new ExternalDocumentation()
                .description("外部参考文档")
                .url("https://sp.feiaikeji.cn");
    }
}

