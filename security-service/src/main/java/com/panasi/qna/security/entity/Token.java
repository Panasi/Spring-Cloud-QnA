package com.panasi.qna.security.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.panasi.qna.security.payload.ETokenType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "jwt")
	private String jwt;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private ETokenType type;
	
	@Column(name = "expired")
	private Boolean expired;
	
	@Column(name = "revoked")
	private Boolean revoked;

}
