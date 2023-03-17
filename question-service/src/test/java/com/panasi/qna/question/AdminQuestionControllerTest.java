package com.panasi.qna.question;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
public class AdminQuestionControllerTest {

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

		mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8765/external/categories/exists/3"))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mockedIsCategory1Exists.toString()));
	}

	// Get

	@Test
	@WithUserDetails("Admin")
	public void showAllQuestions_then_Status200() throws Exception {

		mvc.perform(get("/admin/questions").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[1].content", is("User1 private question")))
				.andExpect(jsonPath("$[2].content", is("User2 public question")))
				.andExpect(jsonPath("$[3].content", is("Admin private question")))
				.andExpect(jsonPath("$[4].content", is("User1 public question")))
				.andExpect(jsonPath("$[5].content", is("User2 private question")))
				.andExpect(jsonPath("$[6].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showAllPublicQuestions_then_Status200() throws Exception {

		mvc.perform(get("/admin/questions?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[1].content", is("User2 public question")))
				.andExpect(jsonPath("$[2].content", is("User1 public question")))
				.andExpect(jsonPath("$[3].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showAllPrivateQuestions_then_Status200() throws Exception {

		mvc.perform(get("/admin/questions?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User1 private question")))
				.andExpect(jsonPath("$[1].content", is("Admin private question")))
				.andExpect(jsonPath("$[2].content", is("User2 private question")))
				.andExpect(jsonPath("$[3].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showAllQuestionsFromCategory_then_Status200() throws Exception {

		mvc.perform(get("/admin/questions/category/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[1].content", is("User1 private question")))
				.andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showPublicQuestionsFromCategory_then_Status200() throws Exception {

		mvc.perform(get("/admin/questions/category/1?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showPrivateQuestionsFromCategory_then_Status200() throws Exception {

		mvc.perform(get("/admin/questions/category/1?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User1 private question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showAllQuestionsFromSubcategories_then_Status200() throws Exception {

		mvc.perform(get("/admin/questions/subcategory/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[0].rating", is(5.0)))
				.andExpect(jsonPath("$[1].content", is("User1 private question")))
				.andExpect(jsonPath("$[1].rating", is(5.0)))
				.andExpect(jsonPath("$[2].content", is("User2 public question")))
				.andExpect(jsonPath("$[2].rating", is(5.0)))
				.andExpect(jsonPath("$[3].content", is("Admin private question")))
				.andExpect(jsonPath("$[3].rating", is(5.0))).andExpect(jsonPath("$[4].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showPublicQuestionsFromSubcategories_then_Status200() throws Exception {

		mvc.perform(get("/admin/questions/subcategory/1?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[0].rating", is(5.0)))
				.andExpect(jsonPath("$[1].content", is("User2 public question")))
				.andExpect(jsonPath("$[1].rating", is(5.0))).andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showPrivateQuestionsFromSubcategories_then_Status200() throws Exception {

		mvc.perform(get("/admin/questions/subcategory/1?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User1 private question")))
				.andExpect(jsonPath("$[0].rating", is(5.0)))
				.andExpect(jsonPath("$[1].content", is("Admin private question")))
				.andExpect(jsonPath("$[1].rating", is(5.0))).andExpect(jsonPath("$[2].name").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showAllUserQuestions_then_Status200() throws Exception {

		mvc.perform(get("/admin/questions/user/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[1].content", is("Admin private question")))
				.andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

		mvc.perform(get("/admin/questions/user/2").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User1 private question")))
				.andExpect(jsonPath("$[1].content", is("User1 public question")))
				.andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

		mvc.perform(get("/admin/questions/user/3").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User2 public question")))
				.andExpect(jsonPath("$[1].content", is("User2 private question")))
				.andExpect(jsonPath("$[2].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showUserPublicQuestions_then_Status200() throws Exception {

		mvc.perform(get("/admin/questions/user/1?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin public question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

		mvc.perform(get("/admin/questions/user/2?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User1 public question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

		mvc.perform(get("/admin/questions/user/3?access=public").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User2 public question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showUserPrivateQuestions_then_Status200() throws Exception {

		mvc.perform(get("/admin/questions/user/1?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("Admin private question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

		mvc.perform(get("/admin/questions/user/2?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User1 private question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

		mvc.perform(get("/admin/questions/user/3?access=private").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].content", is("User2 private question")))
				.andExpect(jsonPath("$[1].content").doesNotHaveJsonPath());

	}

	@Test
	@WithUserDetails("Admin")
	public void showQuestionById_then_Status200() throws Exception {

		mvc.perform(get("/admin/questions/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("Admin public question"))).andExpect(jsonPath("categoryId", is(1)))
				.andExpect(jsonPath("rating", is(5.0)));

		mvc.perform(get("/admin/questions/6").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("User2 private question"))).andExpect(jsonPath("categoryId", is(3)))
				.andExpect(jsonPath("rating", is(5.0)));

	}

	@Test
	@WithUserDetails("Admin")
	public void showQuestionById_then_Status404() throws Exception {

		mvc.perform(get("/admin/questions/99").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Element not found")));

	}

	@Test
	@WithUserDetails("Admin")
	public void showQuestionById_then_Status400() throws Exception {

		mvc.perform(get("/admin/questions/wtf").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Argument type mismatch")));

	}

	// Post

	@Test
	@WithUserDetails("Admin")
	public void addNewQuestion_then_Status201() throws Exception {

		mvc.perform(post("/admin/questions").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"RandomQuestion\"," + "\"categoryId\": 3," + "\"isPrivate\": true }")
				.characterEncoding("utf-8")).andExpect(status().isCreated());

	}

	// Put

	@Test
	@WithUserDetails("Admin")
	public void updateQuestion_then_Status202() throws Exception {

		mvc.perform(put("/admin/questions/6").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Admin updated question\"}")).andExpect(status().isAccepted());

		mvc.perform(get("/admin/questions/6").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("content", is("Admin updated question")));

	}

	@Test
	@WithUserDetails("Admin")
	public void updateQuestion_then_Status404() throws Exception {

		mvc.perform(put("/admin/questions/99").contentType(MediaType.APPLICATION_JSON)
				.content("{\"content\": \"Admin updated question\"}")).andExpect(status().isNotFound());

	}

	// Delete

	@Test
	@WithUserDetails("Admin")
	public void deleteQuestion_then_Status200() throws Exception {

		mvc.perform(delete("/admin/questions/6").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		mvc.perform(get("/admin/questions/6").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Element not found")));

	}

	@Test
	@WithUserDetails("Admin")
	public void deleteQuestion_then_Status404() throws Exception {

		mvc.perform(delete("/admin/questions/99").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("message", is("Internal exception")));

	}

}
