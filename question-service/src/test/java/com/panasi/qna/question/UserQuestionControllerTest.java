package com.panasi.qna.question;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
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
public class UserQuestionControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private RabbitTemplate rabbitTemplateMock;

	@BeforeEach
	public void mockRabbitTemplate() {
		ParameterizedTypeReference<List<Integer>> listIntType = new ParameterizedTypeReference<List<Integer>>() {};
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionRatingQueue", 1)).thenReturn(5.0);
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionRatingQueue", 2)).thenReturn(5.0);
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionRatingQueue", 3)).thenReturn(5.0);
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionRatingQueue", 4)).thenReturn(5.0);
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionRatingQueue", 5)).thenReturn(5.0);
		when(rabbitTemplateMock.convertSendAndReceive("getQuestionRatingQueue", 6)).thenReturn(5.0);
		when(rabbitTemplateMock.convertSendAndReceiveAsType("getAllSubcategoryIdQueue", 1, listIntType)).thenReturn(List.of(2));
		when(rabbitTemplateMock.convertSendAndReceiveAsType("getAllSubcategoryIdQueue", 2, listIntType)).thenReturn(List.of());
		when(rabbitTemplateMock.convertSendAndReceive("isCategoryExistsQueue", 1)).thenReturn(true);
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
