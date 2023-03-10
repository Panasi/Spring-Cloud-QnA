package com.panasi.qna.comment.payload;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.panasi.qna.comment.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Utils {

	// Get current user id
	public static int getCurrentUserId() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return userDetails.getId();
	}

	// Get current user name
	public static String getCurrentUserName() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return userDetails.getUsername();
	}

}
