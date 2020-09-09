package com.tarento.walletService.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Object to hold the Wallet Ledger details
 */
public class WalletLedgerResponse {
    @JsonProperty("reponseInfo")
    private ResponseInfo reponseInfo = null;

    @JsonProperty("walletLedger")
    private List<WalletLedger> walletLedger = null;

    public WalletLedgerResponse reponseInfo(ResponseInfo reponseInfo) {
        this.reponseInfo = reponseInfo;
        return this;
    }

    /**
     * Get reponseInfo
     *
     * @return reponseInfo
     **/
    public ResponseInfo getReponseInfo() {
        return reponseInfo;
    }

    public void setReponseInfo(ResponseInfo reponseInfo) {
        this.reponseInfo = reponseInfo;
    }

    public WalletLedgerResponse walletLedger(List<WalletLedger> walletLedger) {
        this.walletLedger = walletLedger;
        return this;
    }

    public WalletLedgerResponse addWalletLedgerItem(WalletLedger walletLedgerItem) {
        if (this.walletLedger == null) {
            this.walletLedger = new ArrayList<WalletLedger>();
        }
        this.walletLedger.add(walletLedgerItem);
        return this;
    }

    /**
     * Get walletLedger
     *
     * @return walletLedger
     **/
    public List<WalletLedger> getWalletLedger() {
        return walletLedger;
    }

    public void setWalletLedger(List<WalletLedger> walletLedger) {
        this.walletLedger = walletLedger;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WalletLedgerResponse walletLedgerResponse = (WalletLedgerResponse) o;
        return Objects.equals(this.reponseInfo, walletLedgerResponse.reponseInfo) &&
                Objects.equals(this.walletLedger, walletLedgerResponse.walletLedger);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reponseInfo, walletLedger);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class WalletLedgerResponse {\n");

        sb.append("    reponseInfo: ").append(toIndentedString(reponseInfo)).append("\n");
        sb.append("    walletLedger: ").append(toIndentedString(walletLedger)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}