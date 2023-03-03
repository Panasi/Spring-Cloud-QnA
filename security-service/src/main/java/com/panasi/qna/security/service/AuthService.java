package com.panasi.qna.security.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.panasi.qna.security.entity.Role;
import com.panasi.qna.security.entity.User;
import com.panasi.qna.security.exception.DuplicateRegistrationException;
import com.panasi.qna.security.payload.ERole;
import com.panasi.qna.security.payload.JwtResponse;
import com.panasi.qna.security.payload.SignInRequest;
import com.panasi.qna.security.payload.SignUpRequest;
import com.panasi.qna.security.repository.RoleRepository;
import com.panasi.qna.security.repository.UserRepository;
import com.panasi.qna.security.util.JwtUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
	
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final JwtUtils jwtUtils;
	private final PasswordEncoder encoder;
	
	public JwtResponse singnInUser(SignInRequest signInRequest) {
		
		boolean isUserExists = userRepository.existsByUsername(signInRequest.getUsername());
		if (!isUserExists) {
			throw new BadCredentialsException("Username not found");
		}
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.toList();
		return new JwtResponse(jwt, 
				 userDetails.getId(), 
				 userDetails.getUsername(), 
				 userDetails.getEmail(), 
				 roles);
		
	}
	
	public void signUpUser(SignUpRequest signUpRequest) throws DuplicateRegistrationException {
		
		boolean isUsernameTaken = userRepository.existsByUsername(signUpRequest.getUsername());
		boolean isEmailTaken = userRepository.existsByEmail(signUpRequest.getEmail());
		if (isUsernameTaken) {
			throw new DuplicateRegistrationException("Username is already taken");
		}
		if (isEmailTaken) {
			throw new DuplicateRegistrationException("Email is already in use");
		}
		
		User user = new User(signUpRequest.getUsername(), 
				 signUpRequest.getEmail(),
				 encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER);
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				if (role.equals("admin")) {
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
					roles.add(adminRole);
				} else {
					Role userRole = roleRepository.findByName(ERole.ROLE_USER);
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);
	}

}
