package com.tarento.walletService.repository;

import com.tarento.walletService.web.models.WalletInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletInfoRepo extends JpaRepository<WalletInfo, String> {

    @Query(nativeQuery=true,value = "select * from wallet walletinfo where walletinfo.user_wallet_id in (?1)")
    public List<WalletInfo> getUserCoins(List<String> userids);

}
