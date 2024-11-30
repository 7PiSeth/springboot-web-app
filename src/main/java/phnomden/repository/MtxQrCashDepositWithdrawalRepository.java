package phnomden.repository;

import phnomden.entity.MtxQrCashDepositWithdrawalEntity;
import phnomden.dto.Data;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MtxQrCashDepositWithdrawalRepository extends JpaRepository<MtxQrCashDepositWithdrawalEntity, Data> {
	@Query(name = "retrieveQRCashTxn")
	Data retrieveQRCashTxn(
			@Param("reqId")String reqId, 
			@Param("atmId")String atmId, 
			@Param("txnRefNo")String txnRefNo) throws Exception;
		
	@Transactional
	@Query(value = "SELECT * FROM MTX_QR_CASH_DEPOSIT_WITHDRAWAL WHERE req_id = :reqId AND atm_id = :atmId AND txn_ref_no = :txnRefNo FOR UPDATE", nativeQuery = true)
	MtxQrCashDepositWithdrawalEntity findQRCashTxnForUpdate(
			@Param("reqId")String reqId, 
			@Param("atmId")String atmId, 
			@Param("txnRefNo")String txnRefNo) throws Exception;
}