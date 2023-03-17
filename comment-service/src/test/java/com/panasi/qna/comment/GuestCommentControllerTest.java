package com.panasi.qna.comment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class GuestCommentControllerTest {

	@Autowired
	private MockMvc mvc;

	// Get

	@Test
	public void showAllCommentsToQuestion_then_Status401() throws Exception {

		mvc.perform(get("/comments/question/1/all").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

	}

	@Test
	public void showAllCommentsToAnswer_then_Status401() throws Exception {

		mvc.perform(get("/comments/answer/1/all").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

	}

	@Test
	public void showQuestionCommentById_then_Status401() throws Exception {

		mvc.perform(get("/comments/questions/comment/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

	}

	@Test
	public void showAnswerCommentById_then_Status401() throws Exception {

		mvc.perform(get("/comments/answers/comment/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

	}

	// Post

	@Test
	public void addQuestionComment_then_Status401() throws Exception {

		mvc.perform(post("/comments/question/3").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random Comment\"," + "\"rate\": 5}").characterEncoding("utf-8"))
				.andExpect(status().isForbidden());

	}

	// Put

	@Test
	public void updateQuestionComment_then_Status401() throws Exception {

		mvc.perform(put("/comments/questions/comment/6").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random Comment Updated\"}")).andExpect(status().isForbidden());

	}

	// Delete

	@Test
	public void deleteQuestionComment_then_Status401() throws Exception {

		mvc.perform(delete("/admin/comments/questions/comment/5").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

}
