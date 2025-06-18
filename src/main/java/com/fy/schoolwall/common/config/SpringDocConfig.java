package com.fy.schoolwall.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                // 定义全局API信息。
                // 同时，定义一个名为 "cookieAuth" 的安全方案来代表基于Session的认证。
                // springdoc会与Spring Security集成，自动为受保护的端点显示锁形图标。
                return new OpenAPI()
                                .info(new Info()
                                                .title("SchoolWall API")
                                                .version("v1.0.0")
                                                .description("校园墙项目 API 文档，分为公共、用户和管理员三个部分。")
                                                .contact(new Contact().name("API Support")
                                                                .email("support@example.com")))
                                .components(new Components()
                                                .addSecuritySchemes("cookieAuth", new SecurityScheme()
                                                                .type(SecurityScheme.Type.APIKEY)
                                                                .in(SecurityScheme.In.COOKIE)
                                                                .name("JSESSIONID"))) // Spring Security 默认的 session
                                                                                      // cookie 名称
                                // 添加一个全局的安全需求，这会提示UI大多数接口都需要认证
                                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"));
        }

        // @Bean
        // public GroupedOpenApi publicApi() {
        // // 1. 公共API组：无需任何认证即可访问的接口。
        // // 注意：/login 和 /logout 是由Spring Security处理的URL，不是由Controller处理的，
        // // 因此它们不会被SpringDoc扫描到，不应包含在这里。
        // return GroupedOpenApi.builder()
        // .group("1. Public APIs (公共接口)")
        // .pathsToMatch(
        // "/auth/**"
        // )
        // .build();
        // }

        @Bean
        public GroupedOpenApi userApi() {
                // 2. 用户API组：需要普通用户登录后才能访问的接口。
                // 使用具体的路径模式代替宽泛的"/**"，这样更清晰且能避免匹配到非预期的端点（如/error）。
                // 根据项目结构推断出可能的路径，如果还有其他用户模块，请在此处添加。
                return GroupedOpenApi.builder()
                                .group("UserAPIs")
                                .pathsToMatch(
                                                "/posts/**",
                                                "/users/**",
                                                "/comments/**",
                                                "/community/**")
                                .build();
        }

        @Bean
        public GroupedOpenApi adminApi() {
                // 3. 管理员API组：仅限管理员角色访问的接口。
                // 这个定义是清晰的，保持不变。
                return GroupedOpenApi.builder()
                                .group("AdminAPIs")
                                .pathsToMatch("/admin/**")
                                .build();
        }
}
