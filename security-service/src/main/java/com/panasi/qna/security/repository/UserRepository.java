package com.panasi.qna.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.panasi.qna.security.entity.User;


public interface UserRepository extends JpaRepository<User, Integer> {
	
public User findByUsername(String username);
	
	public Boolean existsByUsername(String username);
	
	public Boolean existsByEmail(String email);

}
