package com.panasi.qna.comment;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
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
public class UserCommentControllerTest {

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
		Boolean mockedIsPrivate = true;
		Boolean mockedIsNotPrivate = false;
		Boolean mockedExists = true;
		Integer mockedQuestion1Author = 1;
		Integer mockedQuestion2Author = 1;
		Integer mockedQuestion3Author = 3;
		Integer mockedQuestion6Author = 3;
		Integer mockedAnswer1Author = 1;
		Integer mockedAnswer2Author = 1;
		Integer mockedAnswer3Author = 2;
		Integer mockedAnswer6Author = 3;

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/questions/isPrivate/1"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedIsNotPrivate.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/questions/isPrivate/2"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedIsPrivate.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/questions/isPrivate/3"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedIsNotPrivate.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/questions/isPrivate/6"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedIsPrivate.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/answers/isPrivate/1"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedIsNotPrivate.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/answers/isPrivate/2"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedIsPrivate.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/answers/isPrivate/3"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedIsNotPrivate.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/answers/isPrivate/6"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedIsPrivate.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/questions/authorId/1"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedQuestion1Author.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/questions/authorId/2"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedQuestion2Author.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/questions/authorId/3"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedQuestion3Author.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/questions/authorId/6"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedQuestion6Author.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/answers/authorId/1"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedAnswer1Author.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/answers/authorId/2"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedAnswer2Author.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/answers/authorId/3"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedAnswer3Author.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/answers/authorId/6"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedAnswer6Author.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/questions/exists/1"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedExists.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/questions/exists/2"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedExists.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/questions/exists/3"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedExists.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/questions/exists/6"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedExists.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/answers/exists/1"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedExists.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/answers/exists/2"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedExists.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/answers/exists/3"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedExists.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/answers/exists/6"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedExists.toString()));
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

		mvc.perform(post("/comments/question/3").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random Comment\"," + "\"rate\": 5}").characterEncoding("utf-8"))
				.andExpect(status().isCreated());

	}

	@Test
	@WithUserDetails("User1")
	public void addAnswerComment_then_Status201() throws Exception {

		mvc.perform(post("/comments/answer/3").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random Comment\"," + "\"rate\": 5}").characterEncoding("utf-8"))
				.andExpect(status().isCreated());

	}

	@Test
	@WithUserDetails("User1")
	public void addQuestionComment_then_Status403() throws Exception {

		mvc.perform(post("/comments/question/6").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random Comment\"," + "\"rate\": 5}").characterEncoding("utf-8"))
				.andExpect(status().isForbidden()).andExpect(jsonPath("message", is("Access denied")));

	}

	@Test
	@WithUserDetails("User1")
	public void addAnswerComment_then_Status403() throws Exception {

		mvc.perform(post("/comments/answer/6").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Random Comment\"," + "\"rate\": 5}").characterEncoding("utf-8"))
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
