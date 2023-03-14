package com.panasi.qna.mail.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {

	private String sender;
	private String recipient;
	private String msgBody;
	private String subject;
	private String attachment;

}
