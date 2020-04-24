package com.qa.account.test.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qa.account.persistence.domain.Account;
import com.qa.account.service.AccountService;
import com.qa.account.service.PrizeGenService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;

	private ObjectMapper mapper = new ObjectMapper();

	private Account account = new Account("Bary", "Allen", null, 0);
	
	@Autowired
	private AccountService service;

	@Autowired
	private PrizeGenService prizeGen;

	
	@Test
	public void create() throws JsonProcessingException, Exception {
		RequestBuilder request = request(HttpMethod.POST, "/account/register")
				.content(mapper.writeValueAsString(account)).contentType(MediaType.APPLICATION_JSON);

		String responseBody = this.mvc.perform(request).andExpect(status().isCreated()).andReturn().getResponse()
				.getContentAsString();

		Account createdAccount = this.mapper.readValue(responseBody, Account.class);

		assertEquals("First name doesn't match", account.getFirstName(), createdAccount.getFirstName());

		assertEquals("Surname doesn't match", account.getLastName(), createdAccount.getLastName());

		assertNotNull("No account number", createdAccount.getAccountNumber());

		assertEquals("Invalid prize", this.prizeGen.genPrize(createdAccount.getAccountNumber()),
				createdAccount.getPrize(), 0.1);
	}

	@Test
	public void delete() throws UnsupportedEncodingException, Exception {
		
		final long ID = this.service.addAccount(new Account()).getBody().getId();
		RequestBuilder request = request(HttpMethod.DELETE, "/account/delete/1")
				.contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(request).andExpect(status().isNoContent());
	}

	@Test
	public void getAll() throws Exception {
		Account saved = this.service.addAccount(new Account()).getBody();
		RequestBuilder request = request(HttpMethod.GET, "/account/getAll")
				.contentType(MediaType.APPLICATION_JSON);

		String responseBody = this.mvc.perform(request).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		Account[] accounts = this.mapper.readValue(responseBody, Account[].class);
		List<Account> accountList = Stream.of(accounts).collect(Collectors.toList());
		
		assertTrue(accountList.contains(saved));
	}
	
	@Test
	public void getAccount() throws Exception {
		Account saved = this.service.addAccount(new Account()).getBody();
		RequestBuilder request = request(HttpMethod.GET, "/account/get/" + saved.getId())
				.contentType(MediaType.APPLICATION_JSON);

		String responseBody = this.mvc.perform(request).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		Account account = this.mapper.readValue(responseBody, Account.class);
		
		assertEquals(saved, account);
	}
	
	
	
}
