package com.panasi.qna.answer.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.panasi.qna.answer.security.User;
import com.panasi.qna.answer.security.UserDetailsImpl;

@Configuration
public class TestConfig {

	@Bean
	UserDetailsService userDetailsService() {
		Map<String, User> users = new HashMap<>();
		users.put("Admin", new User(1, "Admin", new HashSet<>(Arrays.asList("ROLE_ADMIN"))));
		users.put("User1", new User(2, "User1", new HashSet<>(Arrays.asList("ROLE_USER"))));
		users.put("User2", new User(3, "User2", new HashSet<>(Arrays.asList("ROLE_USER"))));

		return new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				User user = users.get(username);
				if (user == null) {
					throw new UsernameNotFoundException("Mocked user not found: " + username);
				}
				return UserDetailsImpl.build(user);
			}
		};
	}
}
