package org.gafiev.peertopeerbazaar.service;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.repository.PartOfferToBuyRepository;
import org.gafiev.peertopeerbazaar.service.interfaces.PartOfferToBuyService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PartOfferToBuyServiceImpl implements PartOfferToBuyService {
    public final PartOfferToBuyRepository partOfferToBuyRepository;

}
