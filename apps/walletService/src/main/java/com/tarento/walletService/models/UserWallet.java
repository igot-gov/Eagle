package com.tarento.walletService.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user_wallet")
public class UserWallet {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "wid")
    @JsonProperty("wid")
    private String wid = null;

    @Column(name = "wallet_type")
    @JsonProperty("wallet_type")
    private WalletType walletType = null;

    @Column(name = "created_by")
    @JsonProperty("created_by")
    private String createdBy = null;

    @Column(name = "creation_time")
    @JsonProperty("creation_time")
    private Long creationTime = null;

    @OneToMany(mappedBy = "userWalletInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<WalletInfo> walletInfos;

    @OneToMany(mappedBy = "fromUserWalletLedger", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<WalletLedger> fromWalletLedgers;

    @OneToMany(mappedBy = "toUserWalletLedger", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<WalletLedger> toWalletLedgers;

    @OneToMany(mappedBy = "walletAudit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<WalletAudit> audits;


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

    public Set<WalletInfo> getWalletInfos() {
        return walletInfos;
    }

    public void setWalletInfos(Set<WalletInfo> walletInfos) {
        this.walletInfos = walletInfos;
    }

    public Set<WalletLedger> getFromWalletLedgers() {
        return fromWalletLedgers;
    }

    public void setFromWalletLedgers(Set<WalletLedger> fromWalletLedgers) {
        this.fromWalletLedgers = fromWalletLedgers;
    }

    public Set<WalletLedger> getToWalletLedgers() {
        return toWalletLedgers;
    }

    public void setToWalletLedgers(Set<WalletLedger> toWalletLedgers) {
        this.toWalletLedgers = toWalletLedgers;
    }

    public Set<WalletAudit> getAudits() {
        return audits;
    }

    public void setAudits(Set<WalletAudit> audits) {
        this.audits = audits;
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