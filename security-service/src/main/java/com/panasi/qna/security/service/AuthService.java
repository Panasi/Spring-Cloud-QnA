package com.panasi.qna.security.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.panasi.qna.security.entity.Role;
import com.panasi.qna.security.entity.Token;
import com.panasi.qna.security.entity.User;
import com.panasi.qna.security.exception.DuplicateException;
import com.panasi.qna.security.payload.ERole;
import com.panasi.qna.security.payload.ETokenType;
import com.panasi.qna.security.payload.JwtResponse;
import com.panasi.qna.security.payload.SignInInput;
import com.panasi.qna.security.payload.SignUpInput;
import com.panasi.qna.security.repository.RoleRepository;
import com.panasi.qna.security.repository.TokenRepository;
import com.panasi.qna.security.repository.UserRepository;
import com.panasi.qna.security.util.JwtUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final TokenRepository tokenRepository;
	private final JwtUtils jwtUtils;
	private final PasswordEncoder passwordEncoder;

	public JwtResponse signInUser(SignInInput signInRequest) throws NotFoundException {
		
		User user = userRepository.findByUsername(signInRequest.getUsername()).orElseThrow(NotFoundException::new);
		
		boolean isPasswordCorrect = passwordEncoder.matches(signInRequest.getPassword(), user.getPassword());
		
		if (!isPasswordCorrect) {
			throw new BadCredentialsException("Wrong password");
		}
	    
	    List<Token> userTokens = user.getTokens();
	    
	    for (Token token : userTokens) {
	        token.setExpired(true);
	        token.setRevoked(true);
	    }
	    
	    String newJwt = jwtUtils.generateJwtToken(user);
	    Token newToken = Token.builder()
	            .jwt(newJwt)
	            .type(ETokenType.BEARER)
	            .expired(false)
	            .revoked(false)
	            .build();

	 	tokenRepository.save(newToken);
	 	userTokens.add(newToken);
	    user.setTokens(userTokens);
	    userRepository.save(user);
	    
	    List<ERole> roles = user.getRoles().stream().map(Role::getName).toList();
	    return new JwtResponse(newJwt, user.getId(), user.getUsername(), user.getEmail(), roles);
	    
	}
	
	public void signOutUser(String authHeader) throws NotFoundException {
		
		if (authHeader.startsWith("Bearer ")) {
			String jwt = authHeader.substring(7, authHeader.length());
			Integer userId = jwtUtils.getUserIdFromJwt(jwt);
			List<Token> userTokens = userRepository.findAllTokensByUserId(userId);
			for (Token token : userTokens) {
		        token.setExpired(true);
		        token.setRevoked(true);
		    }
			User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
			user.setTokens(userTokens);
			userRepository.save(user);
		}
		
	}

	public void signUpUser(SignUpInput signUpRequest) throws DuplicateException {

		boolean isUsernameTaken = userRepository.existsByUsername(signUpRequest.getUsername());
		boolean isEmailTaken = userRepository.existsByEmail(signUpRequest.getEmail());
		if (isUsernameTaken) {
			throw new DuplicateException("Username is already taken");
		}
		if (isEmailTaken) {
			throw new DuplicateException("Email is already in use");
		}

		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				passwordEncoder.encode(signUpRequest.getPassword()));

		Set<String> signUpRequestRoles = signUpRequest.getRoles();
		Set<Role> userRoles = new HashSet<>();
		
		if (signUpRequestRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER);
			userRoles.add(userRole);
		} else {
			signUpRequestRoles.forEach(role -> {
				if (role.equals("admin")) {
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
					userRoles.add(adminRole);
				} else {
					Role userRole = roleRepository.findByName(ERole.ROLE_USER);
					userRoles.add(userRole);
				}
			});
		}
		
		user.setRoles(userRoles);
		
		List<Token> tokens = new ArrayList<>();
		user.setTokens(tokens);
		
		userRepository.save(user);
		
	}
	
	@RabbitListener(queues = "isJwtRevokedQueue")
	public Boolean checkJwtRevoked(String jwt) {
		
		Token token = tokenRepository.findByJwt(jwt);
		return token.getRevoked();
		
	}

}
