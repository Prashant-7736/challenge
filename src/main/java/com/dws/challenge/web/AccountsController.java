package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.AccountTransfer;
import com.dws.challenge.exception.AccountTransferException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.EmailNotificationService;
import com.dws.challenge.service.NotificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import javax.validation.Valid;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;
  private final NotificationService notificationService;

  @Autowired
  public AccountsController(AccountsService accountsService, NotificationService notificationService) {
    this.accountsService = accountsService;
      this.notificationService = notificationService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }

  @PostMapping(path = "/transfer",consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> accountTransfer(@RequestBody @Valid AccountTransfer accountTransfer) {
    log.info("transfer between accounts{}", accountTransfer);

    try {
      this.accountsService.accountTransfer(accountTransfer);
      notificationService.notifyAboutTransfer(new Account(accountTransfer.getAccountFrom()),"Debited amount : "+accountTransfer.getAmount());
      notificationService.notifyAboutTransfer(new Account(accountTransfer.getAccountTo()),"Credited amount : "+accountTransfer.getAmount());
    } catch (AccountTransferException ate) {
      return new ResponseEntity<>(ate.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
