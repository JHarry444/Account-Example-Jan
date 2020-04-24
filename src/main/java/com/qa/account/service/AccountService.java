package com.qa.account.service;

import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.qa.account.persistence.domain.Account;
import com.qa.account.persistence.repo.AccountRepo;

@Service
public class AccountService {

	private AccountRepo repo;
	private AccountNumGenService numGen;
	private PrizeGenService prizeGen;

	public AccountService(AccountRepo repo, AccountNumGenService numGen, PrizeGenService prizeGen) {
		super();
		this.repo = repo;
		this.numGen = numGen;
		this.prizeGen = prizeGen;
	}

	public ResponseEntity<List<Account>> getAccounts() {
		return ResponseEntity.ok(repo.findAll());
	}

	public ResponseEntity<Account> getAccount(Long id) {
		try {
			Account found = repo.findById(id).orElseThrow(() -> new AccountNotFoundException(id.toString()));
			return ResponseEntity.ok(found);
		} catch (AccountNotFoundException anfe) {
			return ResponseEntity.notFound().build();
		}

	}

	public ResponseEntity<Account> addAccount(Account account) {
		account.setAccountNumber(this.numGen.genNumber());
		account.setPrize(prizeGen.genPrize(account.getAccountNumber()));
		return new ResponseEntity<Account>(this.repo.save(account), HttpStatus.CREATED);
	}

	public ResponseEntity<Object> deleteAccount(Long id) {
		if (accountExists(id)) {
			repo.deleteById(id);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

	private boolean accountExists(Long id) {
		return repo.findById(id).isPresent();
	}

	public ResponseEntity<Object> updateAccount(Account account, Long id) {
		if (accountExists(id)) {
			Account toUpdate = this.repo.findById(id).get();
			toUpdate.setFirstName(account.getFirstName());
			toUpdate.setLastName(account.getLastName());
			repo.save(account);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}

}
