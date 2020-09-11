package com.tarento.walletService.web.models.communication;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCoin {

    @JsonProperty("numberOfCoins")
    private Integer numberOfCoins;

    @JsonProperty("expiryDate")
    private Long ExpiryDate;

    public Integer getNumberOfCoins() {
        return numberOfCoins;
    }

    public void setNumberOfCoins(Integer numberOfCoins) {
        this.numberOfCoins = numberOfCoins;
    }

    public Long getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        ExpiryDate = expiryDate;
    }
}
