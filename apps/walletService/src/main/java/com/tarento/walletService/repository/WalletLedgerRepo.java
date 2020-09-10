package com.tarento.walletService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tarento.walletService.web.models.WalletLedger;

@Repository
public interface WalletLedgerRepo extends JpaRepository<WalletLedger, String> {

	@Query(value = "SELECT * from wallet_ledger where from_user_wallet_id=?0 or to_user_wallet_id=?0 AND creation_time>=?1 AND creation_time<=?2", nativeQuery = true)
	public List<WalletLedger> findLedgerDetails(String userWalletId, long fromDate, long toDate);
}
