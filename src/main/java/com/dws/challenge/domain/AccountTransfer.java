package com.dws.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountTransfer {
    @NotNull
    @NotEmpty
    private final String accountFrom;
    @NotNull
    @NotEmpty
    private final String accountTo;

    @NotNull
    @Min(value = 0, message = "amount must be positive.")
    private BigDecimal amount;

    @JsonCreator
    public AccountTransfer(@JsonProperty("accountTo") String accountTo,
                   @JsonProperty("accountFrom") String accountFrom,
                   @JsonProperty("amount") BigDecimal amount) {
        this.accountTo = accountTo;
        this.accountFrom = accountFrom;
        this.amount = amount;
    }
}
