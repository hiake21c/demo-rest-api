package com.jongminkim.demorestapi.config;


import com.jongminkim.demorestapi.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public TokenStore tokenStore(){
        return new InMemoryTokenStore();
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/docs/index.html");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

//    이렇게 HttpSecurity로 맵핑 할 수 도 있지만 더 많은 일을 처리함으로 위와 같은 방법으로 하는 것이 좋다.
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .mvcMatchers("/docs/index.html").anonymous()
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .anonymous()
                .and()
            .formLogin()
                .and()
            .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/api/**").anonymous()
                .anyRequest().authenticated();
    }
}
