package com.jongminkim.demorestapi.config;

import com.jongminkim.demorestapi.accounts.Account;
import com.jongminkim.demorestapi.accounts.AccountRole;
import com.jongminkim.demorestapi.accounts.AccountService;
import com.jongminkim.demorestapi.common.BaseControllerTest;
import com.jongminkim.demorestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.HashSet;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception{
        //Given
        String password = "jongmin2";
        String username = "jongmin2@email.com";

        Account jongmin = Account.builder()
                .email(username)
                .password(password)
                .roles(new HashSet<>(Arrays.asList(AccountRole.AMDIN, AccountRole.USER)))
                .build();

        this.accountService.saveAccount(jongmin);

        String clientId = "myApp";
        String clientSecret = "pass";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
                ;
    }
}