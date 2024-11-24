package phnomden.repository;

import phnomden.entity.QRCashTxnEntity;
import phnomden.dto.Data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QRCashTxnRepository extends JpaRepository<QRCashTxnEntity, Data> {
	@Query(name = "retrieveQRCashTxn")
	Data retrieveQRCashTxn(
			@Param("reqId")String reqId, 
			@Param("atmId")String atmId, 
			@Param("txnRefNo")String txnRefNo) throws Exception;
}
