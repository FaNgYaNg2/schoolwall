package com.fy.schoolwall.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus; // 导入 HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler; // 导入 HttpStatusReturningLogoutSuccessHandler

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 启用方法级别的安全注解，如 @PreAuthorize
public class SecurityConfig {

    // 在 Spring Security 6.x 中，AuthenticationManager 会自动配置
    // 只要你的应用上下文中存在 UserDetailsService 和 PasswordEncoder 的 Bean
    // 因此，不再需要在这里注入 CustomUserDetailsService 并手动构建 AuthenticationManager Bean

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF。注意：对于基于会话的认证，CSRF 保护通常是重要的。
                // 在 REST API 中，如果前端不发送 CSRF token，禁用它会简化开发。
                // 生产环境中，如果使用会话，强烈建议启用 CSRF 并确保前端正确处理 token。
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // 允许所有用户访问注册和登录接口
                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers("/login").permitAll() // Spring Security 默认的登录处理URL
                        // 只有 ADMIN 角色可以访问 /admin/** 路径下的资源
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin // 启用基于表单的登录认证
                        .loginProcessingUrl("/login") // 指定处理登录表单提交的 URL
                        .usernameParameter("username") // 登录请求中用户名参数的名称
                        .passwordParameter("password") // 登录请求中密码参数的名称
                        // 登录成功时的处理，返回 200 OK 和 JSON 消息
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.OK.value());
                            response.getWriter().write("{\"message\": \"Login successful\"}");
                            response.getWriter().flush();
                        })
                        // 登录失败时的处理，返回 401 Unauthorized 和 JSON 消息
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.getWriter().write("{\"message\": \"Invalid username or password\"}");
                            response.getWriter().flush();
                        }))
                .logout(logout -> logout // 启用注销功能
                        .logoutUrl("/logout") // 指定注销的 URL
                        .invalidateHttpSession(true) // 注销时使当前 HttpSession 失效
                        .deleteCookies("JSESSIONID") // 注销时删除名为 JSESSIONID 的 Cookie
                        // 注销成功时的处理，返回 200 OK
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)));
        // 默认的 SessionCreationPolicy 就是 IF_REQUIRED，
        // 意味着 Spring Security 在需要时会创建会话。
        // 通常不需要显式配置，除非有特殊需求。
        // .sessionManagement(session -> session
        // .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        // );

        return http.build();
    }

    /**
     * 定义密码编码器 Bean。
     * Spring Security 会自动检测此 Bean 并用于密码验证。
     * 
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 在 Spring Security 6.x 中，AuthenticationManager 的配置更加自动化。
    // 只要 CustomUserDetailsService 和 PasswordEncoder 作为 Bean 存在于 Spring 容器中，
    // Spring Security 就会自动将它们注册到内部的 AuthenticationManagerBuilder 中，
    // 从而形成一个可用的 AuthenticationManager。
    // 因此，无需显式定义此 Bean 方法。
}