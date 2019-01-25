package com.jongminkim.demorestapi.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;
    @Test
    public void findByUsername() {

        String password = "jongmin";
        String email = "hiake21c@gmail.com";
        Account account = Account.builder()
                .email(email)
                .password(password)
                .roles(new HashSet<>(Arrays.asList(AccountRole.AMDIN, AccountRole.USER))) // Java 9 부터 Set.of(AccountRole.AMDIN, AccountRole.USER)
                .build();

        this.accountRepository.save(account);

        UserDetailsService userDetailsService = accountService;

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertThat(userDetails.getPassword()).isEqualTo(password);
    }

/*
예외만 예측 할 수 있는 방법
    @Test(expected = UsernameNotFoundException.class)
    public void findByUsernameFail() {
    //Given
        String username = "random@email.com";
    //When
        accountService.loadUserByUsername(username);
    }
*/
/*
메시지 까지 예측 할 수 있는 방법
하지만 코드가 장황해짐
    @Test
    public void findByUsernameFail() {
    //Given
        String username = "random@email.com";

    // When
        try{
            accountService.loadUserByUsername(username);
            fail("supposed to be filed");
        } catch (UsernameNotFoundException e){
            assertThat(e.getMessage()).containsSequence(username);
        }
    }
*/

    /**
     * 먼저 예측하는 방법 하지만 Given when Then 순서가 맞지 안아 헷갈릴 수 있음
     */
    @Test
    public void findByUsernameFail() {
        //Given
        String username = "random@email.com";

        //Then
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(username));

        //When
        accountService.loadUserByUsername(username);
    }

}