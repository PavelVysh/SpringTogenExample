package com.example.securityou;

import com.example.securityou.auth.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityouApplicationTests {

	@Autowired
	private MockMvc mockMvc;


	@Test
	void badRequest() throws Exception {
		mockMvc.perform(get("/api/v1/demo-controller"))
				.andExpect(status().is4xxClientError());

	}

	@Test
	void goodRequest() throws Exception {
		String user = """
				{
				"username": "TestUser",
				"password": "TestPassword"
				}
				""";
		MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(user))
				.andExpect(status().isOk())
				.andReturn();

		String body = result.getResponse().getContentAsString();
		String token = new ObjectMapper().readValue(body, Token.class).getToken();

		mockMvc.perform(get("/api/v1/demo-controller")
				.header("Authorization", "Bearer " + token))
						.andExpect(status().isOk())
								.andExpect(content().string("You made it here"));

	}
	@Test
	void authRequest()  throws Exception {
		String user = """
				{
				"username": "TestUser",
				"password": "TestPassword"
				}
				""";
		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(user))
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(post("/api/v1/auth/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(user))
				.andExpect(status().isOk());
	}


}
