package com.panasi.qna.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.panasi.qna.security.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Integer>{
	
	public List<Token> findAll();
	
	public Token findByJwt(String jwt);

}
