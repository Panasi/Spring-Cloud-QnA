package com.panasi.qna.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.panasi.qna.security.entity.Role;
import com.panasi.qna.security.payload.ERole;


public interface RoleRepository extends JpaRepository<Role, Integer> {
	
	public Role findByName(ERole name);

}
