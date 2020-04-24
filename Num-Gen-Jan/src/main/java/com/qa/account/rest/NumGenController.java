package com.qa.account.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qa.account.service.AccountNumGenService;

@RestController
@RequestMapping("/numgen")
public class NumGenController {

	private AccountNumGenService service;

	public NumGenController(AccountNumGenService service) {
		this.service = service;
	}


	@GetMapping("/generate")
	public String genNumber() {
		return this.service.genNumber();
	}

}
