package com.tarento.walletService.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class UserWallet {

    @JsonProperty("id")
    private String id = null;

    @JsonProperty("wid")
    private String wid = null;

    @JsonProperty("wallet_type")
    private WalletType walletType = null;

    @JsonProperty("created_by")
    private String createdBy = null;

    @JsonProperty("creation_time")
    private Long creationTime = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public WalletType getWalletType() {
        return walletType;
    }

    public void setWalletType(WalletType walletType) {
        this.walletType = walletType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wid, walletType, createdBy, creationTime);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UserWallet {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    wid: ").append(toIndentedString(wid)).append("\n");
        sb.append("    walletType: ").append(toIndentedString(walletType)).append("\n");
        sb.append("    createdBy: ").append(toIndentedString(createdBy)).append("\n");
        sb.append("    creationTime: ").append(toIndentedString(creationTime)).append("\n");
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