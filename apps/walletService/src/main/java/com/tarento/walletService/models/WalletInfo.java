package com.tarento.walletService.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

/**
 * Wallet information for a specific User
 */
@Entity
@Table(name = "wallet")
public class WalletInfo {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @JsonProperty("id")
    private String id = null;

    @Column(name = "expiry_date")
    @JsonProperty("expiry_date")
    private Long expiryDate = null;

    @Column(name = "number_of_coins")
    @JsonProperty("number_of_coins")
    private Integer numberOfCoins = null;

    @Column(name = "created_by")
    @JsonProperty("created_by")
    private String createdBy = null;

    @Column(name = "creation_time")
    @JsonProperty("creation_time")
    private Long creationTime = null;

    @Column(name = "updated_by")
    @JsonProperty("updated_by")
    private String updatedBy = null;

    public UserWallet getUser_walletInfo() {
        return userWalletInfo;
    }

    public void setUser_walletInfo(UserWallet user_walletInfo) {
        this.userWalletInfo = user_walletInfo;
    }

    @Column(name = "updated_time")
    @JsonProperty("updation_time")
    private Long updationTime = null;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_wallet_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    @JsonProperty("user_walletInfo")
    private UserWallet userWalletInfo;


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

    public WalletInfo expiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
        return this;
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

    public WalletInfo numberOfCoins(Integer numberOfCoins) {
        this.numberOfCoins = numberOfCoins;
        return this;
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

    public UserWallet getUserWalletInfo() {
        return userWalletInfo;
    }

    public void setUserWalletInfo(UserWallet userWalletInfo) {
        this.userWalletInfo = userWalletInfo;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WalletInfo walletInfo = (WalletInfo) o;
        return Objects.equals(this.id, walletInfo.id) &&
                Objects.equals(this.userWalletInfo, walletInfo.userWalletInfo) &&
                Objects.equals(this.expiryDate, walletInfo.expiryDate) &&
                Objects.equals(this.numberOfCoins, walletInfo.numberOfCoins) &&
                Objects.equals(this.createdBy, walletInfo.createdBy) &&
                Objects.equals(this.creationTime, walletInfo.creationTime) &&
                Objects.equals(this.updatedBy, walletInfo.updatedBy) &&
                Objects.equals(this.updationTime, walletInfo.updationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userWalletInfo, expiryDate, numberOfCoins, createdBy, creationTime, updatedBy, updationTime);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class WalletInfo {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    userWalletId: ").append(toIndentedString(userWalletInfo)).append("\n");
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