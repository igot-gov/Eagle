package com.tarento.walletService.repository;

import com.tarento.walletService.web.models.WalletAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletAuditRepo extends JpaRepository<WalletAudit, String> {
}
