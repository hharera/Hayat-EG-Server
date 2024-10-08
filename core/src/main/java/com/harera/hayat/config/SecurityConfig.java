package com.harera.hayat.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harera.hayat.filter.JwtRequestFilter;
import com.harera.hayat.model.exception.ApiError;
import com.harera.hayat.model.exception.GlobalMessage;
import com.harera.hayat.repository.GlobalMessageRepository;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final GlobalMessageRepository globalMessageRepository;
    private final ObjectMapper mapper;
    private final String UNAUTHORIZED = "unauthorized";
    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(GlobalMessageRepository globalMessageRepository,
                    ObjectMapper mapper, JwtRequestFilter jwtRequestFilter) {
        this.globalMessageRepository = globalMessageRepository;
        this.mapper = mapper;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        final String[] publicUris = { "/api/v1/login", "/api/v1/oauth/**",
                "/api/v1/signup/**", "/api/v1/cities/**", "/api/v1/states/**",
                "/api/v1/notifications/**", "/v3/api-docs/**", "/swagger-ui/**",
                "/swagger-resources/**" };

        httpSecurity.csrf().disable().httpBasic().disable().formLogin().disable().cors()
                        .and().authorizeRequests().antMatchers(publicUris).permitAll()
                        .antMatchers("/**").authenticated().and()
                        .addFilterBefore(jwtRequestFilter,
                                        UsernamePasswordAuthenticationFilter.class)
                        .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // permit all
        auth.inMemoryAuthentication().withUser("admin")
                        .password(bCryptPasswordEncoder().encode("admin")).roles("ADMIN");
    }

    private void authenticateExceptionHandler(HttpServletRequest httpServletRequest,
                    HttpServletResponse httpServletResponse, AuthenticationException e)
                    throws IOException {
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
        apiError.setDisplayMessage(assignDisplayMessage(httpServletRequest));
        httpServletResponse.setCharacterEncoding("UTF-8");
        final PrintWriter writer = httpServletResponse.getWriter();
        writer.write(mapper.writeValueAsString(apiError));
        httpServletResponse.setContentType("application/json");

    }

    private String assignDisplayMessage(HttpServletRequest request) {
        final String LANG_HEADER = "language";
        final String header = request.getHeader(LANG_HEADER);
        String language = StringUtils.isNotEmpty(header) ? header : "en";
        final Optional<GlobalMessage> globalMessage = globalMessageRepository
                        .findByLanguageAndCode(language, UNAUTHORIZED);
        return globalMessage.get().getMessage();
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
