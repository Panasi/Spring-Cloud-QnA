package com.panasi.qna.comment;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class AdminCommentControllerTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private RestTemplate restTemplate;
	private MockRestServiceServer mockServer;

	@BeforeEach
	public void init() throws Exception {
		mockServer = MockRestServiceServer.bindTo(this.restTemplate).ignoreExpectOrder(true).build();
		setUpMockExternalCommentsServer();
	}

	private void setUpMockExternalCommentsServer() throws Exception {
		Boolean mockedIsExists = true;

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/questions/exists/2"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedIsExists.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/answers/exists/2"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedIsExists.toString()));
	}

	// Get

	@Test
	@WithUserDetails("Admin")
	public void showAllCommentsToQuestion_then_Status200() throws Exception {

		mvc.perform(get("/admin/comments/question/1/all").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Comment2"))).andExpect(jsonPath("$[0].rate", is(2)))
				.andExpect(jsonPath("$[1].content", is("Comment1"))).andExpect(jsonPath("$[1].rate", is(1)))
				.andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showAllCommentsToAnswer_then_Status200() throws Exception {

		mvc.perform(get("/admin/comments/answer/1/all").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Comment2"))).andExpect(jsonPath("$[0].rate", is(2)))
				.andExpect(jsonPath("$[1].content", is("Comment1"))).andExpect(jsonPath("$[1].rate", is(1)))
				.andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showAllUserCommentsToQuestions_then_Status200() throws Exception {

		mvc.perform(get("/admin/comments/user/2/questions").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Comment2"))).andExpect(jsonPath("$[0].rate", is(2)))
				.andExpect(jsonPath("$[1].content", is("Comment1"))).andExpect(jsonPath("$[1].rate", is(1)))
				.andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showAllUserCommentsToAnswers_then_Status200() throws Exception {

		mvc.perform(get("/admin/comments/user/2/answers").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Comment2"))).andExpect(jsonPath("$[0].rate", is(2)))
				.andExpect(jsonPath("$[1].content", is("Comment1"))).andExpect(jsonPath("$[1].rate", is(1)))
				.andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showQuestionCommentById_then_Status200() throws Exception {

		mvc.perform(get("/admin/comments/questions/comment/2").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("Comment2"))).andExpect(jsonPath("rate", is(2)));

	}

	@Test
	@WithUserDetails("Admin")
	public void showAnswerCommentById_then_Status200() throws Exception {

		mvc.perform(get("/admin/comments/answers/comment/2").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("Comment2"))).andExpect(jsonPath("rate", is(2)));

	}

	@Test
	@WithUserDetails("Admin")
	public void showCommentById_then_Status404() throws Exception {

		mvc.perform(get("/admin/comments/answers/comment/99").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Element not found")));

	}

	@Test
	@WithUserDetails("Admin")
	public void showCommentById_then_Status400() throws Exception {

		mvc.perform(get("/admin/comments/answers/comment/wtf").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Argument type mismatch")));

	}

	// Post

	@Test
	@WithUserDetails("Admin")
	public void addQuestionComment_then_Status201() throws Exception {

		mvc.perform(post("/admin/comments/question/2").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random Comment\"," + "\"rate\": 5}").characterEncoding("utf-8"))
				.andExpect(status().isCreated());

	}

	@Test
	@WithUserDetails("Admin")
	public void addAnswerComment_then_Status201() throws Exception {

		mvc.perform(post("/admin/comments/answer/2").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random Comment\"," + "\"rate\": 5}").characterEncoding("utf-8"))
				.andExpect(status().isCreated());

	}

	// Put

	@Test
	@WithUserDetails("Admin")
	public void updateQuestionComment_then_Status202() throws Exception {

		mvc.perform(put("/admin/comments/questions/comment/3").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random Comment Updated\"}")).andExpect(status().isAccepted());

		mvc.perform(get("/admin/comments/questions/comment/3").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("Random Comment Updated")));

	}

	@Test
	@WithUserDetails("Admin")
	public void updateAnswerComment_then_Status202() throws Exception {

		mvc.perform(put("/admin/comments/answers/comment/3").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random Comment Updated\"}")).andExpect(status().isAccepted());

		mvc.perform(get("/admin/comments/answers/comment/3").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("Random Comment Updated")));

	}

	@Test
	@WithUserDetails("Admin")
	public void updateComment_then_Status404() throws Exception {

		mvc.perform(put("/admin/comments/answers/comment/99").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random comment Updated\"}")).andExpect(status().isNotFound());

	}

	// Delete

	@Test
	@WithUserDetails("Admin")
	public void deleteQuestionComment_then_Status200() throws Exception {

		mvc.perform(delete("/admin/comments/questions/comment/5").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		mvc.perform(get("/admin/comments/questions/comment/5").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Element not found")));

	}

	@Test
	@WithUserDetails("Admin")
	public void deleteAnswerComment_then_Status200() throws Exception {

		mvc.perform(delete("/admin/comments/answers/comment/5").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		mvc.perform(get("/admin/comments/answers/comment/5").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Element not found")));

	}

	@Test
	@WithUserDetails("Admin")
	public void deleteComment_then_Status404() throws Exception {

		mvc.perform(delete("/admin/comments/answers/comment/99").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Internal exception")));

	}

}
