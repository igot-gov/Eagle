package com.tarento.walletService.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Object to keep the old records of the
 */
public class WalletAudit {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("user_wallet_id")
    private String userWalletId = null;

    @JsonProperty("expiry_date")
    private Long expiryDate = null;

    @JsonProperty("number_of_coins")
    private Integer numberOfCoins = null;

    @JsonProperty("created_by")
    private String createdBy = null;

    @JsonProperty("creation_time")
    private Long creationTime = null;

    @JsonProperty("updated_by")
    private String updatedBy = null;

    @JsonProperty("updation_time")
    private Long updationTime = null;

    /**
     * Auto Generated Primary Key Id
     *
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Reference to Primary Key Id of User_Wallet entry
     *
     * @return userWalletId
     **/
    public String getUserWalletId() {
        return userWalletId;
    }

    public void setUserWalletId(String userWalletId) {
        this.userWalletId = userWalletId;
    }

    /**
     * Expiry date of coins in EPOCH format
     *
     * @return expiryDate
     **/
    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Number of coins available
     *
     * @return numberOfCoins
     **/
    public Integer getNumberOfCoins() {
        return numberOfCoins;
    }

    public void setNumberOfCoins(Integer numberOfCoins) {
        this.numberOfCoins = numberOfCoins;
    }

    /**
     * Reference to UserId of the person who creates the entry
     *
     * @return createdBy
     **/
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Created DateTime in EPOCH format
     *
     * @return creationTime
     **/
    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public WalletAudit updatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        return this;
    }

    /**
     * Reference to UserId of the person who updates this entry
     *
     * @return updatedBy
     **/
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Modified DateTime in EPOCH format
     *
     * @return updationTime
     **/
    public Long getUpdationTime() {
        return updationTime;
    }

    public void setUpdationTime(Long updationTime) {
        this.updationTime = updationTime;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WalletAudit walletAudit = (WalletAudit) o;
        return Objects.equals(this.id, walletAudit.id) &&
                Objects.equals(this.userWalletId, walletAudit.userWalletId) &&
                Objects.equals(this.expiryDate, walletAudit.expiryDate) &&
                Objects.equals(this.numberOfCoins, walletAudit.numberOfCoins) &&
                Objects.equals(this.createdBy, walletAudit.createdBy) &&
                Objects.equals(this.creationTime, walletAudit.creationTime) &&
                Objects.equals(this.updatedBy, walletAudit.updatedBy) &&
                Objects.equals(this.updationTime, walletAudit.updationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userWalletId, expiryDate, numberOfCoins, createdBy, creationTime, updatedBy, updationTime);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class WalletAudit {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    userWalletId: ").append(toIndentedString(userWalletId)).append("\n");
        sb.append("    expiryDate: ").append(toIndentedString(expiryDate)).append("\n");
        sb.append("    numberOfCoins: ").append(toIndentedString(numberOfCoins)).append("\n");
        sb.append("    createdBy: ").append(toIndentedString(createdBy)).append("\n");
        sb.append("    creationTime: ").append(toIndentedString(creationTime)).append("\n");
        sb.append("    updatedBy: ").append(toIndentedString(updatedBy)).append("\n");
        sb.append("    updationTime: ").append(toIndentedString(updationTime)).append("\n");
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