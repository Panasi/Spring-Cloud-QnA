package com.panasi.qna.comment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class UserCommentControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private RabbitTemplate rabbitTemplateMock;

	@BeforeEach
	public void mockRabbitTemplate() {
		when(rabbitTemplateMock.convertSendAndReceive("isQuestionExistsQueue", 1)).thenReturn(true);
		when(rabbitTemplateMock.convertSendAndReceive("isQuestionExistsQueue", 2)).thenReturn(true);
		when(rabbitTemplateMock.convertSendAndReceive("isQuestionExistsQueue", 3)).thenReturn(true);
		when(rabbitTemplateMock.convertSendAndReceive("isQuestionExistsQueue", 6)).thenReturn(true);
		when(rabbitTemplateMock.convertSendAndReceive("isAnswerExistsQueue", 1)).thenReturn(true);
		when(rabbitTemplateMock.convertSendAndReceive("isAnswerExistsQueue", 2)).thenReturn(true);
		when(rabbitTemplateMock.convertSendAndReceive("isAnswerExistsQueue", 3)).thenReturn(true);
		when(rabbitTemplateMock.convertSendAndReceive("isAnswerExistsQueue", 6)).thenReturn(true);
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionIsPrivateQueue", 1)).thenReturn(false);
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionIsPrivateQueue", 2)).thenReturn(true);
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionIsPrivateQueue", 3)).thenReturn(false);
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionIsPrivateQueue", 6)).thenReturn(true);
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerIsPrivateQueue", 1)).thenReturn(false);
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerIsPrivateQueue", 2)).thenReturn(true);
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerIsPrivateQueue", 3)).thenReturn(false);
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerIsPrivateQueue", 6)).thenReturn(true);
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionAuthorIdQueue", 1)).thenReturn(1);
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionAuthorIdQueue", 2)).thenReturn(1);
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionAuthorIdQueue", 3)).thenReturn(3);
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionAuthorIdQueue", 6)).thenReturn(3);
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerAuthorIdQueue", 1)).thenReturn(1);
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerAuthorIdQueue", 2)).thenReturn(1);
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerAuthorIdQueue", 3)).thenReturn(3);
		when(rabbitTemplateMock.convertSendAndReceive("getAnswerAuthorIdQueue", 6)).thenReturn(3);
	}

	// Get

	@Test
	@WithUserDetails("User1")
	public void showAllCommentsToQuestion_then_Status200() throws Exception {

		mvc.perform(get("/comments/question/1/all").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Comment2"))).andExpect(jsonPath("$[0].rate", is(2)))
				.andExpect(jsonPath("$[1].content", is("Comment1"))).andExpect(jsonPath("$[1].rate", is(1)))
				.andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("User1")
	public void showAllCommentsToQuestion_then_Status403() throws Exception {

		mvc.perform(get("/comments/question/2/all").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Access denied")));

	}

	@Test
	@WithUserDetails("User1")
	public void showAllCommentsToAnswer_then_Status200() throws Exception {

		mvc.perform(get("/comments/answer/1/all").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Comment2"))).andExpect(jsonPath("$[0].rate", is(2)))
				.andExpect(jsonPath("$[1].content", is("Comment1"))).andExpect(jsonPath("$[1].rate", is(1)))
				.andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("User1")
	public void showAllCommentsToAnswer_then_Status403() throws Exception {

		mvc.perform(get("/comments/answer/2/all").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Access denied")));

	}

	@Test
	@WithUserDetails("User1")
	public void showQuestionCommentById_then_Status200() throws Exception {

		mvc.perform(get("/comments/questions/comment/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("Comment1"))).andExpect(jsonPath("rate", is(1)));

	}

	@Test
	@WithUserDetails("User1")
	public void showAnswerCommentById_then_Status200() throws Exception {

		mvc.perform(get("/comments/answers/comment/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("Comment1"))).andExpect(jsonPath("rate", is(1)));

	}

	@Test
	@WithUserDetails("User2")
	public void showPrivateQuestionCommentById_then_Status200() throws Exception {

		mvc.perform(get("/comments/questions/comment/7").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("Comment7"))).andExpect(jsonPath("rate", is(5)));

	}

	@Test
	@WithUserDetails("User1")
	public void showPrivateQuestionCommentById_then_Status403() throws Exception {

		mvc.perform(get("/comments/questions/comment/3").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Access denied")));

	}

	@Test
	@WithUserDetails("User1")
	public void showCommentById_then_Status404() throws Exception {

		mvc.perform(get("/comments/questions/comment/99").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Element not found")));

	}

	@Test
	@WithUserDetails("User1")
	public void showCommentById_then_Status400() throws Exception {

		mvc.perform(get("/comments/questions/comment/wtf").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Argument type mismatch")));

	}

	// Post

	@Test
	@WithUserDetails("User1")
	public void addQuestionComment_then_Status201() throws Exception {

		mvc.perform(post("/comments/question").contentType(MediaType.APPLICATION_JSON)
				.content("{\"targetId\": 3," + "\"content\": \"Random Comment\"," + "\"rate\": 5}").characterEncoding("utf-8"))
				.andExpect(status().isCreated());

	}

	@Test
	@WithUserDetails("User1")
	public void addAnswerComment_then_Status201() throws Exception {

		mvc.perform(post("/comments/answer").contentType(MediaType.APPLICATION_JSON)
				.content("{\"targetId\": 3," + "\"content\": \"Random Comment\"," + "\"rate\": 5}").characterEncoding("utf-8"))
				.andExpect(status().isCreated());

	}

	@Test
	@WithUserDetails("User1")
	public void addQuestionComment_then_Status403() throws Exception {

		mvc.perform(post("/comments/question").contentType(MediaType.APPLICATION_JSON)
				.content("{\"targetId\": 6," + "\"content\": \"Random Comment\"," + "\"rate\": 5}").characterEncoding("utf-8"))
				.andExpect(status().isForbidden()).andExpect(jsonPath("message", is("Access denied")));

	}

	@Test
	@WithUserDetails("User1")
	public void addAnswerComment_then_Status403() throws Exception {

		mvc.perform(post("/comments/answer").contentType(MediaType.APPLICATION_JSON)
				.content("{\"targetId\": 6," + "\"content\": \"Random Comment\"," + "\"rate\": 5}").characterEncoding("utf-8"))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("message", is("Access denied")));

	}

	// Put

	@Test
	@WithUserDetails("User2")
	public void updateQuestionComment_then_Status202() throws Exception {

		mvc.perform(put("/comments/questions/comment/6").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random Comment Updated\"}")).andExpect(status().isAccepted());

		mvc.perform(get("/comments/questions/comment/6").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("Random Comment Updated")));

	}

	@Test
	@WithUserDetails("User2")
	public void updateAnswerComment_then_Status202() throws Exception {

		mvc.perform(put("/comments/answers/comment/6").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random Comment Updated\"}")).andExpect(status().isAccepted());

		mvc.perform(get("/comments/answers/comment/6").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("Random Comment Updated")));

	}

	@Test
	@WithUserDetails("User1")
	public void updateQuestionComment_then_Status403() throws Exception {

		mvc.perform(put("/comments/questions/comment/6").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random Comment Updated\"}")).andExpect(status().isForbidden())
				.andExpect(jsonPath("message", is("Access denied")));

	}

	@Test
	@WithUserDetails("User1")
	public void updateComment_then_Status404() throws Exception {

		mvc.perform(put("/comments/answer/comment/99").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random comment\"}")).andExpect(status().isNotFound());

	}

}
