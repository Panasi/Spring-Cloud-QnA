package com.panasi.qna.question;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.Matchers;
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

import com.panasi.qna.question.dto.AnswerDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class UserQuestionControllerTest {

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
		Double mockedRating = 5.0;
		List<Integer> mockedSubcategory1List = List.of(2);
		List<Integer> mockedSubcategoryList = List.of();
		List<AnswerDTO> mockedAnswerList = List.of();
		Boolean mockedIsCategory1Exists = true;

		mockServer
				.expect(ExpectedCount.manyTimes(),
						requestTo(Matchers
								.matchesPattern(".*http://localhost:8765/external/comments/question/rating/.*")))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedRating.toString()));

		mockServer
				.expect(ExpectedCount.manyTimes(),
						requestTo(Matchers.matchesPattern(".*http://localhost:8765/external/answers/.*")))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedAnswerList.toString()));

		mockServer
				.expect(ExpectedCount.manyTimes(),
						requestTo("http://localhost:8765/external/categories/subcategoriesId/1"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedSubcategory1List.toString()));

		mockServer
				.expect(ExpectedCount.manyTimes(),
						requestTo("http://localhost:8765/external/categories/subcategoriesId/2"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedSubcategoryList.toString()));

		mockServer
				.expect(ExpectedCount.manyTimes(),
						requestTo("http://localhost:8765/external/categories/subcategoriesId/3"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedSubcategoryList.toString()));

		mockServer
				.expect(ExpectedCount.manyTimes(),
						requestTo("http://localhost:8765/external/categories/subcategoriesId/4"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedSubcategoryList.toString()));

		mockServer
				.expect(ExpectedCount.manyTimes(),
						requestTo("http://localhost:8765/external/categories/subcategoriesId/5"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedSubcategoryList.toString()));

		mockServer
				.expect(ExpectedCount.manyTimes(),
						requestTo("http://localhost:8765/external/categories/subcategoriesId/6"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedSubcategoryList.toString()));

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/categories/exists/1"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedIsCategory1Exists.toString()));
	}

	// Get

	@Test
	@WithUserDetails("User1")
	public void showAllQuestions_then_Status403() throws Exception {

		mvc.perform(get("/admin/questions").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());

	}

	@Test
	@WithUserDetails("User2")
	public void showQuestionsFromCategory_then_Status200() throws Exception {

		mvc.perform(get("/questions/category/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("User2")
	public void showPublicQuestionsFromCategory_then_Status200() throws Exception {

		mvc.perform(get("/questions/category/1?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("User2")
	public void showPrivateQuestionsFromCategory_then_Status200() throws Exception {

		mvc.perform(get("/questions/category/1?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("User1")
	public void showQuestionsFromSubcategories_then_Status200() throws Exception {

		mvc.perform(get("/questions/subcategory/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[0].rating", is(5.0)))
				.andExpect(jsonPath("$[1].content", is("User1 private question")))
				.andExpect(jsonPath("$[1].rating", is(5.0)))
				.andExpect(jsonPath("$[2].content", is("User2 public question")))
				.andExpect(jsonPath("$[2].rating", is(5.0))).andExpect(jsonPath("$[3].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("User1")
	public void showPublicQuestionsFromSubcategories_then_Status200() throws Exception {

		mvc.perform(get("/questions/subcategory/1?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[0].rating", is(5.0)))
				.andExpect(jsonPath("$[1].content", is("User2 public question")))
				.andExpect(jsonPath("$[1].rating", is(5.0))).andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("User1")
	public void showPrivateQuestionsFromSubcategories_then_Status200() throws Exception {

		mvc.perform(get("/questions/subcategory/1?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User1 private question")))
				.andExpect(jsonPath("$[0].rating", is(5.0))).andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("User1")
	public void showAllUserQuestionsToUser1_then_Status200() throws Exception {

		mvc.perform(get("/questions/user/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

		mvc.perform(get("/questions/user/2").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User1 private question")))
				.andExpect(jsonPath("$[1].content", is("User1 public question")))
				.andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

		mvc.perform(get("/questions/user/3").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User2 public question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("User1")
	public void showPublicUserQuestionsToUser1_then_Status200() throws Exception {

		mvc.perform(get("/questions/user/1?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

		mvc.perform(get("/questions/user/2?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User1 public question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

		mvc.perform(get("/questions/user/3?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User2 public question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("User1")
	public void showPrivateUserQuestionsToUser1_then_Status200() throws Exception {

		mvc.perform(get("/questions/user/1?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content").doesNotHaveJsonPath());

		mvc.perform(get("/questions/user/2?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User1 private question")));

		mvc.perform(get("/questions/user/3?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("User2")
	public void showAllUserQuestionsToUser2_then_Status200() throws Exception {

		mvc.perform(get("/questions/user/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

		mvc.perform(get("/questions/user/2").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User1 public question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

		mvc.perform(get("/questions/user/3").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User2 public question")))
				.andExpect(jsonPath("$[1].content", is("User2 private question")))
				.andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("User1")
	public void showQuestionById_then_Status200() throws Exception {

		mvc.perform(get("/questions/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("Admin public question"))).andExpect(jsonPath("categoryId", is(1)));

		mvc.perform(get("/questions/4").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("User1 private question"))).andExpect(jsonPath("categoryId", is(1)));

		mvc.perform(get("/questions/6").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Access denied")));

	}

	@Test
	@WithUserDetails("User1")
	public void showQuestionById_then_Status404() throws Exception {

		mvc.perform(get("/questions/99").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Element not found")));

	}

	@Test
	@WithUserDetails("User1")
	public void showQuestionById_then_Status400() throws Exception {

		mvc.perform(get("/questions/wtf").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Argument type mismatch")));

	}

	// Post

	@Test
	@WithUserDetails("User1")
	public void addNewQuestion_then_Status201() throws Exception {

		mvc.perform(post("/questions").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"RandomQuestion\"," + "\"categoryId\": 1," + "\"isPrivate\": true }")
				.characterEncoding("utf-8")).andExpect(status().isCreated());

	}

	// Put

	@Test
	@WithUserDetails("User1")
	public void updateQuestion_then_Status202() throws Exception {

		mvc.perform(put("/questions/4").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"User1 updated question\"}")).andExpect(status().isAccepted());

		mvc.perform(get("/questions/4").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("User1 updated question")))
				.andExpect(jsonPath("isPrivate", is(true)));

	}

	@Test
	@WithUserDetails("User1")
	public void updateQuestion_then_Status403() throws Exception {

		mvc.perform(put("/questions/6").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"User2 updated question\"}")).andExpect(status().isForbidden())
				.andExpect(jsonPath("message", is("Access denied")));

	}

	@Test
	@WithUserDetails("User1")
	public void updateQuestion_then_Status404() throws Exception {

		mvc.perform(put("/questions/99").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"User1 updated question\"}")).andExpect(status().isNotFound());

	}

}
