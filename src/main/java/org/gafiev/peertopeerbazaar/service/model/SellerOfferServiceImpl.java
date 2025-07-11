package org.gafiev.peertopeerbazaar.service.model;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.SellerOfferResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuyStatus;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.exception.IllegalBusinessStateException;
import org.gafiev.peertopeerbazaar.mapper.SellerOfferMapper;
import org.gafiev.peertopeerbazaar.repository.AddressRepository;
import org.gafiev.peertopeerbazaar.repository.ProductRepository;
import org.gafiev.peertopeerbazaar.repository.SellerOfferRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.repository.specification.SellerOfferSpecifications;
import org.gafiev.peertopeerbazaar.service.model.interfaces.SellerOfferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class SellerOfferServiceImpl implements SellerOfferService {

    private final SellerOfferRepository sellerOfferRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final SellerOfferMapper sellerOfferMapper;

    /**
     * получение предложения продавца по его Id
     *
     * @param id идентификатор предложения продавца
     * @return DTO оффера
     */
    @Override
    public SellerOfferResponse getSellerOfferById(Long id) {
        SellerOffer sellerOffer = sellerOfferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SellerOffer.class, Map.of("id", String.valueOf(id))));
        return sellerOfferMapper.toSellerOfferResponse(sellerOffer);
    }

    /**
     * получение предложения продавца по его Id вместе с подтягиванием всех выбранных уже частей данного заказа покупателя,
     * подтягивание ленивой части,
     *
     * @param id идентификатор предложения продавца
     * @return DTO оффера
     */
    @Override
    public SellerOfferResponse getSellerOfferByIdWithPartOfferToBuy(Long id) {
        SellerOffer sellerOffer = sellerOfferRepository.findByIdWithPartOfferToBuy(id)
                .orElseThrow(() -> new EntityNotFoundException(SellerOffer.class, Map.of("id", String.valueOf(id))));
        return sellerOfferMapper.toSellerOfferResponse(sellerOffer);
    }

    /**
     * получения множества всех офферов продавца по клиентскому Id
     *
     * @param sellerId идентификатор пользователя
     * @return DTO sellerOfferSet
     */
    @Override
    public Set<SellerOfferResponse> getAllMySellerOffers(Long sellerId) {
        User user = userRepository.findByIdWithBuyerOrdersAndSellerOffers(sellerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(sellerId))));
        return sellerOfferMapper.toSellerOfferResponseSet(user.getSellerOfferSet());
    }

    /**
     * получение множества офферов всех продавцов из БД согласно настроенного фильтра
     *
     * @param filterRequest фильтр определяющий условия и параметры поиска
     * @return DTO sellerOfferSet
     */
    @Override
    public Set<SellerOfferResponse> getAllSellerOffers(SellerOfferFilterRequest filterRequest) {
        List<SellerOffer> sellerOfferList = sellerOfferRepository.findAll(SellerOfferSpecifications.filterByParams(filterRequest));
        return sellerOfferMapper.toSellerOfferResponseSet(new HashSet<>(sellerOfferList));
    }

    /**
     * создание нового оффера продавца
     *
     * @param sellerId идентификатор пользователя
     * @param sellerOfferCreate информация от продавца на создание оффера
     * @return DTO оффера
     */
    @Override
    @Transactional
    public SellerOfferResponse createSellerOffer(Long sellerId, SellerOfferCreateRequest sellerOfferCreate) {
        User seller = userRepository.findByIdWithBuyerOrdersAndSellerOffers(sellerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(sellerId))));

        Product product = productRepository.findByIdWithSellerOffers(sellerOfferCreate.productId())
                .orElseThrow(() -> new EntityNotFoundException(Product.class, Map.of("id", String.valueOf(sellerOfferCreate.productId()))));

        Address address = addressRepository.findByIdWithSellerOffersAndDeliveries(sellerOfferCreate.addressId())
                .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("id", String.valueOf(sellerOfferCreate.addressId()))));


        SellerOffer sellerOffer = new SellerOffer();
        for (int i = 0; i < sellerOfferCreate.unitCount(); i++) {
            PartOfferToBuy partOfferToBuy = PartOfferToBuy.builder().build();
            sellerOffer.addPartOfferToBuy(partOfferToBuy);
        }
        sellerOffer.setOfferStatus(sellerOfferCreate.offerStatus());
        sellerOffer.setComment(sellerOfferCreate.comment());
        sellerOffer.setCreationDateTime(sellerOfferCreate.creationDateTime());
        sellerOffer.setFinishDateTime(sellerOfferCreate.finishedDateTime());
        product.addSellerOffer(sellerOffer);
        address.addSellerOffer(sellerOffer);
        seller.addSellerOffer(sellerOffer);

        sellerOffer = sellerOfferRepository.save(sellerOffer);
        return sellerOfferMapper.toSellerOfferResponse(sellerOffer);
    }

    /**
     * обновление существующего оффера продавца.
     * обновление рейтинга продавца при отмене оффера.
     *
     * @param id идентификатор существующего оффера
     * @param sellerOfferNew информация от продавца, что необходимо поменять
     * @return DTO оффера
     */
    @Override
    @Transactional
    public SellerOfferResponse updateMySellerOffer(Long sellerId, Long id, SellerOfferCreateRequest sellerOfferNew) {
        SellerOffer sellerOffer = sellerOfferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SellerOffer.class, Map.of("id", String.valueOf(id))));

        // Проверка, принадлежит ли оффер этому продавцу
        if (!sellerOffer.getSeller().getId().equals(sellerId)) {
            throw new IllegalBusinessStateException("You do not have permission to update this offer.");
        }

        //TODO написать логику проверки частей: если частей стало больше, то досоздать PartOfferToBuy и добавить их в SellerOffer, если частей стало меньше, то удалить незарезервированные части из оффера
        int partSize = sellerOffer.getPartOfferToBuyList().size();
        if (sellerOfferNew.unitCount() > partSize) {
            for (int i = 0; i < sellerOfferNew.unitCount() - partSize; i++) {
                PartOfferToBuy partOfferToBuy = PartOfferToBuy.builder().build();
                sellerOffer.addPartOfferToBuy(partOfferToBuy);
            }
        } else {
            Iterator<PartOfferToBuy> it = sellerOffer.getPartOfferToBuyList().iterator();
            int countRemove = partSize - sellerOfferNew.unitCount();
            while (it.hasNext() || countRemove > 0) {
                PartOfferToBuy part = it.next();
                if (part.getStatus().equals(PartOfferToBuyStatus.NOT_RESERVED)) {
                    it.remove();
                    countRemove--;
                }
            }
        }
        sellerOffer.setOfferStatus(sellerOfferNew.offerStatus());
        sellerOffer.setComment(sellerOfferNew.comment());
        sellerOffer.setCreationDateTime(sellerOfferNew.creationDateTime());
        sellerOffer.setFinishDateTime(sellerOfferNew.finishedDateTime());

        // Логика по изменению рейтинга продавца при отмене оффера
        if (sellerOfferNew.offerStatus() == OfferStatus.CANCELLED) {
            int currentRating = sellerOffer.getSeller().getRatingSeller() != null ? sellerOffer.getSeller().getRatingSeller() : 0;
            sellerOffer.getSeller().setRatingSeller(Math.max(0, currentRating - 1));
            userRepository.save(sellerOffer.getSeller());
        }

        sellerOffer = sellerOfferRepository.save(sellerOffer);

        return sellerOfferMapper.toSellerOfferResponse(sellerOffer);
    }

    /**
     * удаление оффера из БД по его Id
     *
     * @param id идентификатор оффера
     */
    @Override
    @Transactional
    public void deleteSellerOffer(Long id) {
        sellerOfferRepository.deleteById(id);
    }

    @Override
    public Integer getActualUnitCount(Long id) {
        SellerOffer sellerOffer = sellerOfferRepository.findByIdWithPartOfferToBuy(id)
                .orElseThrow(() -> new EntityNotFoundException(SellerOffer.class, Map.of("id", String.valueOf(id))));
        return sellerOffer.getActualUnitCount();
    }
}
