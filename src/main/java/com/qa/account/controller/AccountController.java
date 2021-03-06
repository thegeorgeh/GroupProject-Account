package com.qa.account.controller;

import java.util.List;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.discovery.EurekaClient;
import com.qa.account.entities.Account;
import com.qa.account.entities.Constants;
import com.qa.account.entities.CreateAccount;
import com.qa.account.entities.Login;
import com.qa.account.entities.UpdateAccount;
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
	
	@PutMapping(Constants.LOGIN)
	public Account login(@RequestBody Login login) {
		return srvc.login(login, getAllAccounts());
	}
	
	@PutMapping(Constants.CHECK_VALID)
	public String checkValid(@RequestBody CreateAccount account) {	
		return srvc.checkAccount(account, getAllAccounts());
			
		
	}
	@PutMapping(Constants.CHECK_UPDATE_VALID)
	public String checkUpdateValid(@RequestBody UpdateAccount account) {
		return srvc.checkUpdateAccount(account, getAccount(account.getId()), getAllAccounts());	
	}

	@PutMapping(Constants.ENCRYPT)
	public String encrypt(@RequestBody String password) {
		return srvc.encryptPassword(password);
	}	

	public List<Account> getAllAccounts(){
		return this.rest.build().exchange(client.getNextServerFromEureka(Constants.GATEWAY, false).getHomePageUrl()+Constants.GET_ACCOUNTS_PATH, 
				HttpMethod.GET, null, new ParameterizedTypeReference<List<Account>>(){}).getBody();
	}
	
	private Account getAccount(Long accountId) {
		HttpEntity<Long> entity = new HttpEntity<>(accountId);
		return this.rest.build().exchange(client.getNextServerFromEureka(Constants.GATEWAY, false).getHomePageUrl()+Constants.GET_ACCOUNT_PATH, 
				HttpMethod.PUT, entity, Account.class).getBody();
	}

}
