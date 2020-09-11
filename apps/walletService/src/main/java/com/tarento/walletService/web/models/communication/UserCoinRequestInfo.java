package com.tarento.walletService.web.models.communication;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserCoinRequestInfo {

    private List<Map<String, String>> userCoinReq = null;

    public List<Map<String, String>> getUserCoinReq() {
        return userCoinReq;
    }

    public void setUserCoinReq(List<Map<String, String>> userCoinReq) {
        this.userCoinReq = userCoinReq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCoinRequestInfo that = (UserCoinRequestInfo) o;
        return Objects.equals(userCoinReq, that.userCoinReq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userCoinReq);
    }

    @Override
    public String toString() {
        return "UserCoinRequestInfo{" +
                "userCoinReq=" + userCoinReq +
                '}';
    }

}
