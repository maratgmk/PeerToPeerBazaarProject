package org.gafiev.peertopeerbazaar.service;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.request.SellerOfferCreateRequest;
import org.gafiev.peertopeerbazaar.dto.request.SellerOfferFilterRequest;
import org.gafiev.peertopeerbazaar.dto.response.SellerOfferResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.mapper.SellerOfferMapper;
import org.gafiev.peertopeerbazaar.repository.AddressRepository;
import org.gafiev.peertopeerbazaar.repository.ProductRepository;
import org.gafiev.peertopeerbazaar.repository.SellerOfferRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.service.interfaces.SellerOfferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

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
     * получение предложения продавца по его Id вместе с подтягиванием всех выбранных уже частей заказа покупателя,
     * подтягивание ленивой части,
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
     * @param userId идентификатор пользователя
     * @return DTO sellerOfferSet
     */
    @Override
    public Set<SellerOfferResponse> getAllMySellerOffers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(userId))));
        return sellerOfferMapper.toSellerOfferResponseSet(user.getSellerOfferSet());
    }

    /**
     * получение множества офферов всех продавцов из БД согласно настроенного фильтра
     * @param filterRequest фильтр определяющий условия и параметры поиска
     * @return DTO sellerOfferSet
     */
    @Override
    public Set<SellerOfferResponse> getAllSellerOffers(SellerOfferFilterRequest filterRequest) {
        return Set.of();
    }

    /**
     * создание нового оффера продавца
     *
     * @param sellerOfferCreate информация от продавца на создание оффера
     * @return DTO оффера
     */
    @Override
    @Transactional
    public SellerOfferResponse createSellerOffer(SellerOfferCreateRequest sellerOfferCreate) {
        User seller = userRepository.findById(sellerOfferCreate.sellerId())
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(sellerOfferCreate.sellerId()))));

        Product product = productRepository.findById(sellerOfferCreate.productId())
                .orElseThrow(() -> new EntityNotFoundException(Product.class, Map.of("id", String.valueOf(sellerOfferCreate.productId()))));

        Address address = addressRepository.findById(sellerOfferCreate.addressId())
                .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("id", String.valueOf(sellerOfferCreate.addressId()))));

        SellerOffer sellerOffer = new SellerOffer();
        sellerOffer.setUnitCount(sellerOfferCreate.unitCount());
        sellerOffer.setOfferStatus(sellerOfferCreate.offerStatus());
        sellerOffer.setComment(sellerOfferCreate.comment());
        sellerOffer.setCreationDateTime(sellerOfferCreate.creationDateTime());
        sellerOffer.setFinishDateTime(sellerOfferCreate.finishedDateTime());
        sellerOffer.setProduct(product);
        sellerOffer.setAddress(address);
        sellerOffer.setSeller(seller);
        //  sellerOffer.setPartOfferToBuySet(); можно сделать?

        sellerOfferRepository.save(sellerOffer);
        return sellerOfferMapper.toSellerOfferResponse(sellerOffer);
    }

    /**
     * обновление существующего оффера продавца
     *
     * @param id             идентификатор существующего оффера
     * @param sellerOfferNew информация от продавца, что необходимо поменять
     * @return DTO оффера
     */
    @Override
    @Transactional
    public SellerOfferResponse updateMySellerOffer(Long id, SellerOfferCreateRequest sellerOfferNew) {
        SellerOffer sellerOffer = sellerOfferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SellerOffer.class, Map.of("id", String.valueOf(id))));

        User seller = userRepository.findById(sellerOfferNew.sellerId())
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(sellerOfferNew.sellerId()))));

        Product product = productRepository.findById(sellerOfferNew.productId())
                .orElseThrow(() -> new EntityNotFoundException(Product.class, Map.of("id", String.valueOf(sellerOfferNew.productId()))));

        Address address = addressRepository.findById(sellerOfferNew.addressId())
                .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("id", String.valueOf(sellerOfferNew.addressId()))));

        sellerOffer.setUnitCount(sellerOffer.getUnitCount());
        sellerOffer.setOfferStatus(sellerOfferNew.offerStatus());
        sellerOffer.setComment(sellerOfferNew.comment());
        sellerOffer.setCreationDateTime(sellerOffer.getCreationDateTime());
        sellerOffer.setFinishDateTime(sellerOffer.getFinishDateTime());
        sellerOffer.setProduct(product);
        sellerOffer.setAddress(address);
        sellerOffer.setSeller(seller);
        sellerOffer.setPartOfferToBuySet(sellerOffer.getPartOfferToBuySet());

        sellerOfferRepository.save(sellerOffer);

        return sellerOfferMapper.toSellerOfferResponse(sellerOffer);
    }

    /**
     * удаление оффера из БД по его Id
     * @param id идентификатор оффера
     */
    @Override
    @Transactional
    public void deleteSellerOffer(Long id) {
        sellerOfferRepository.deleteById(id);
    }
}
