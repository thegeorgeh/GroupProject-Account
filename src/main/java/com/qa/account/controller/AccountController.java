package com.qa.account.controller;

import java.util.List;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.discovery.EurekaClient;
import com.qa.account.entities.Account;
import com.qa.account.entities.Constants;
import com.qa.account.entities.CreateAccount;
import com.qa.account.entities.Login;
import com.qa.account.service.AccountServiceImpl;

@RestController
public class AccountController {
	
	private AccountServiceImpl srvc;
	private RestTemplateBuilder rest;
	private EurekaClient client;

	public AccountController(AccountServiceImpl srvc, RestTemplateBuilder rest, EurekaClient client) {
		this.srvc = srvc;
		this.rest = rest;
		this.client = client;
	}
	
	@PutMapping("/login")
	public Account login(@RequestBody Login login) {
		return srvc.login(login, getAllAccounts());
	}
	
	@PutMapping("/checkValid")
	public String checkValid(@RequestBody CreateAccount account) {
		return srvc.checkCreateAccount(account);
	}
	@PutMapping("/checkDuplicates")
	public String checkDuplicates(@RequestBody CreateAccount account) {
		return srvc.checkDuplicates(account, getAllAccounts());
	}

	@PutMapping("/encrypt")
	public String encrypt(@RequestBody String password) {
		return srvc.encryptPassword(password);
	}	

	public List<Account> getAllAccounts(){
		return this.rest.build().exchange(client.getNextServerFromEureka(Constants.GATEWAY, false).getHomePageUrl()+Constants.GET_ACCOUNTS_PATH, 
				HttpMethod.GET, null, new ParameterizedTypeReference<List<Account>>(){}).getBody();
	}

}