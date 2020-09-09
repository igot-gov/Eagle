package com.tarento.walletService.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

/**
 * Keeps the information about each transaction of coins
 */
@Entity
@Table(name = "wallet_ledger")
public class WalletLedger {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @JsonProperty("id")
    private String id = null;

    @Column(name = "number_of_coins")
    @JsonProperty("number_of_coins")
    private Integer numberOfCoins = null;

    @Column(name = "comments")
    @JsonProperty("comments")
    private String comments = null;

    @Column(name = "created_by")
    @JsonProperty("transaction_by")
    private String transactionBy = null;

    @Column(name = "creation_time")
    @JsonProperty("transaction_datetime")
    private Long transactionDatetime = null;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_user_wallet_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    @JsonProperty("from_user_walletLedger")
    private UserWallet fromUserWalletLedger;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_user_wallet_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    @JsonProperty("to_user_walletLedger")
    private UserWallet toUserWalletLedger;


    /**
     * Auto generated Primary Key Id
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
     * Number of coins transferred
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
     * Comments about the transaction
     *
     * @return comments
     **/
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Reference to UserId of the person who creates the entry
     *
     * @return transactionBy
     **/
    public String getTransactionBy() {
        return transactionBy;
    }

    public void setTransactionBy(String transactionBy) {
        this.transactionBy = transactionBy;
    }

    /**
     * Created DateTime in EPOCH format
     *
     * @return transactionDatetime
     **/
    public Long getTransactionDatetime() {
        return transactionDatetime;
    }

    public void setTransactionDatetime(Long transactionDatetime) {
        this.transactionDatetime = transactionDatetime;
    }

    public UserWallet getFromUserWalletLedger() {
        return fromUserWalletLedger;
    }

    public void setFromUserWalletLedger(UserWallet fromUserWalletLedger) {
        this.fromUserWalletLedger = fromUserWalletLedger;
    }

    public UserWallet getToUserWalletLedger() {
        return toUserWalletLedger;
    }

    public void setToUserWalletLedger(UserWallet toUserWalletLedger) {
        this.toUserWalletLedger = toUserWalletLedger;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WalletLedger walletLedger = (WalletLedger) o;
        return Objects.equals(this.id, walletLedger.id) &&
                Objects.equals(this.fromUserWalletLedger, walletLedger.fromUserWalletLedger) &&
                Objects.equals(this.toUserWalletLedger, walletLedger.toUserWalletLedger) &&
                Objects.equals(this.numberOfCoins, walletLedger.numberOfCoins) &&
                Objects.equals(this.comments, walletLedger.comments) &&
                Objects.equals(this.transactionBy, walletLedger.transactionBy) &&
                Objects.equals(this.transactionDatetime, walletLedger.transactionDatetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromUserWalletLedger, toUserWalletLedger, numberOfCoins, comments, transactionBy, transactionDatetime);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class WalletLedger {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    fromUserWalletId: ").append(toIndentedString(fromUserWalletLedger)).append("\n");
        sb.append("    toUserWalletId: ").append(toIndentedString(toUserWalletLedger)).append("\n");
        sb.append("    numberOfCoins: ").append(toIndentedString(numberOfCoins)).append("\n");
        sb.append("    comments: ").append(toIndentedString(comments)).append("\n");
        sb.append("    transactionBy: ").append(toIndentedString(transactionBy)).append("\n");
        sb.append("    transactionDatetime: ").append(toIndentedString(transactionDatetime)).append("\n");
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