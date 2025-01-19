package org.gafiev.peertopeerbazaar.exception;

import lombok.Getter;

import java.util.Set;
@Getter
public class ClosedOfferException extends RuntimeException {
    private final Set<Long> partOfferToBuyIds;

    public ClosedOfferException( Set<Long> partOfferToBuyIds) {
        super("Parts of closed offers: " + partOfferToBuyIds + ". These parts are removed from the basket");
        this.partOfferToBuyIds = partOfferToBuyIds;
    }
}
