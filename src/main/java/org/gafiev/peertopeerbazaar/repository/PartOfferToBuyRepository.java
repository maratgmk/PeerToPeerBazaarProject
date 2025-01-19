package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PartOfferToBuyRepository extends JpaRepository<PartOfferToBuy,Long>, JpaSpecificationExecutor<PartOfferToBuy> {


}
