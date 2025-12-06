package org.gafiev.peertopeerbazaar.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferCreateRequest;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.product.Product;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestDataMapper {
    public static ProductCreateRequest toProductCreateRequest(Product product){
        return ProductCreateRequest.builder()
                .name(product.getName())
                .portionUnit(product.getPortionUnit())
                .description(product.getDescription())
                .category(product.getCategory())
                .weight(product.getWeightKg())
                .volume(product.getVolumeLtr())
                .price(product.getPrice())
                .userId(product.getAuthor().getId())
                .imageURI(product.getImageURI())
                .qrCode(product.getQrCode())
                .createdAt(product.getCreatedAt())
                .build();
    }
    public static ProductUpdateRequest toProductUpdateRequest(Product product){
        return ProductUpdateRequest.builder()
                .name(product.getName())
                .portionUnit(product.getPortionUnit())
                .description(product.getDescription())
                .category(product.getCategory())
                .weight(product.getWeightKg())
                .volume(product.getVolumeLtr())
                .price(product.getPrice())
                .userId(product.getAuthor().getId())
                .imageURI(product.getImageURI())
                .qrCode(product.getQrCode())
                .createdAt(product.getCreatedAt())
                .build();
    }
    public static SellerOfferCreateRequest toSellerOfferCreateRequest(SellerOffer sellerOffer){
        return SellerOfferCreateRequest.builder()
                .unitCount(ThreadLocalRandom.current().nextInt(1,11))
                .offerStatus(sellerOffer.getOfferStatus())
                .comment(sellerOffer.getComment())
                .creationDateTime(sellerOffer.getCreationDateTime())
                .finishedDateTime(sellerOffer.getFinishDateTime())
                .productId(sellerOffer.getProduct().getId())
                .addressId(sellerOffer.getAddress().getId())
                .createdAt(Instant.now())
                .build();
    }


}
