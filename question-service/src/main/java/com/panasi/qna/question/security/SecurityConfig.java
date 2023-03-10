package com.panasi.qna.question.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	UserDetailsImpl getAnonimus() {
		Set<String> roles = new HashSet<>();
		User testUser = new User(0, "Guest", roles);
		return UserDetailsImpl.build(testUser);
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, AuthTokenFilter authTokenFilter) throws Exception {
		http.httpBasic().disable().csrf().disable().cors().disable()
				.anonymous().principal(getAnonimus()).authorities("GUEST_ROLE")
				.and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests().anyRequest()
				.permitAll();

		http.addFilterBefore(authTokenFilter, BasicAuthenticationFilter.class);

		return http.build();
	}

}
