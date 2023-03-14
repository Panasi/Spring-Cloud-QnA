package com.panasi.qna.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.panasi.qna.security.entity.User;
import com.panasi.qna.security.payload.ERole;

public interface UserRepository extends JpaRepository<User, Integer> {

	public User findByUsername(String username);

	public Boolean existsByUsername(String username);

	public Boolean existsByEmail(String email);
	
	@Query("SELECT u.email FROM User u INNER JOIN u.roles r WHERE r.name = ?1 AND u.id NOT IN ?2")
	List<String> findInactiveUserEmails(ERole role, List<Integer> activeUsers);

}
