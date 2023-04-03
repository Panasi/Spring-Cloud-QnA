package com.panasi.qna.answer;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class GuestAnswerControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private RabbitTemplate rabbitTemplateMock;

	@BeforeEach
	public void mockRabbitTemplate() {
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerRatingQueue", 1)).thenReturn(5.0);
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerRatingQueue", 2)).thenReturn(5.0);
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerRatingQueue", 3)).thenReturn(5.0);
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerRatingQueue", 4)).thenReturn(5.0);
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerRatingQueue", 5)).thenReturn(5.0);
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerRatingQueue", 6)).thenReturn(5.0);
		when(rabbitTemplateMock.convertSendAndReceive("isQuestionExistsQueue", 1)).thenReturn(true);
	}

	// Get

	@Test
	public void showAllAnswers_then_Status401() throws Exception {

		mvc.perform(get("/admin/answers").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());

	}

	@Test
	public void showUserAnswers_then_Status200() throws Exception {

		mvc.perform(get("/answers/user/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public answer")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

		mvc.perform(get("/answers/user/2").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User1 public answer")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

		mvc.perform(get("/answers/user/3").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User2 public answer")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

	}

	@Test
	public void showUserPublicAnswers_then_Status200() throws Exception {

		mvc.perform(get("/answers/user/1?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public answer")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

		mvc.perform(get("/answers/user/2?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User1 public answer")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

		mvc.perform(get("/answers/user/3?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User2 public answer")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

	}

	@Test
	public void showUserPrivateAnswers_then_Status200() throws Exception {

		mvc.perform(get("/answers/user/1?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content").doesNotHaveJsonPath());

		mvc.perform(get("/answers/user/2?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content").doesNotHaveJsonPath());

		mvc.perform(get("/answers/user/3?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content").doesNotHaveJsonPath());

	}

	@Test
	public void showAnswerById_then_Status200() throws Exception {

		mvc.perform(get("/answers/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("Admin public answer"))).andExpect(jsonPath("questionId", is(3)));

	}

	@Test
	public void showAnswerById_then_Status403() throws Exception {

		mvc.perform(get("/answers/2").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content").doesNotHaveJsonPath());

	}

	@Test
	public void showAnswerById_then_Status404() throws Exception {

		mvc.perform(get("/answers/99").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Element not found")));

	}

	@Test
	public void showAnswerById_then_Status400() throws Exception {

		mvc.perform(get("/answers/wtf").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath(
						"message", is("Argument type mismatch")));

	}

	// Post

	@Test
	public void addNewAnswer_then_Status401() throws Exception {

		mvc.perform(post("/answers").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"RandomAnswer\"," + "\"questionId\": 1," + "\"isPrivate\": true }")
				.characterEncoding("utf-8")).andExpect(status().isForbidden());

	}

	// Put

	@Test
	public void updateAnswer_then_Status401() throws Exception {

		mvc.perform(put("/answers/6").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"User1 updated answer\"}")).andExpect(status().isForbidden());

	}

	// Delete

	@Test
	public void deleteAnswer_then_Status401() throws Exception {

		mvc.perform(delete("/admin/answers/6").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

	}

}
