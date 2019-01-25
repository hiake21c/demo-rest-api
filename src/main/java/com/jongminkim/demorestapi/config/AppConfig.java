package com.jongminkim.demorestapi.config;

import com.jongminkim.demorestapi.accounts.Account;
import com.jongminkim.demorestapi.accounts.AccountRepository;
import com.jongminkim.demorestapi.accounts.AccountRole;
import com.jongminkim.demorestapi.accounts.AccountService;
import com.jongminkim.demorestapi.common.AppProperties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner() {

        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;

            @Autowired
            AccountRepository accountRepository;

            @Autowired
            AppProperties appProperties;

            @Override
            public void run(ApplicationArguments args) throws Exception {

                Optional<Account> accountOptional = accountRepository.findByEmail(appProperties.getAdminUsername());

                if(!accountOptional.isPresent()) {
                    Account jongmin = Account.builder()
                            .email(appProperties.getAdminUsername())
                            .password(appProperties.getAdminPassword())
                            .roles(new HashSet<>(Arrays.asList(AccountRole.AMDIN, AccountRole.USER)))
                            .build();

                    accountService.saveAccount(jongmin);
                }


                Optional<Account> userAccountOptional = accountRepository.findByEmail(appProperties.getUserUserName());

                if(!userAccountOptional.isPresent()) {
                    Account userAccount = Account.builder()
                            .email(appProperties.getUserUserName())
                            .password(appProperties.getUserPassword())
                            .roles(new HashSet<>(Arrays.asList(AccountRole.USER)))
                            .build();

                    accountService.saveAccount(userAccount);
                }

            }
        };
    }
}
