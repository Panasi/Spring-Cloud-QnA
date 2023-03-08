package com.panasi.qna.comment.security;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
	
	private Integer id;
	private String username;
	private Set<String> roles;

}
