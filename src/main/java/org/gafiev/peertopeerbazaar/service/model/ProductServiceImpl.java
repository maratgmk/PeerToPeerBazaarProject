package org.gafiev.peertopeerbazaar.service.model;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.ProductResponse;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.mapper.ProductMapper;
import org.gafiev.peertopeerbazaar.repository.ProductRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.repository.specification.ProductSpecifications;
import org.gafiev.peertopeerbazaar.service.model.interfaces.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    /**
     * получение DTO продукта по его Id из БД
     * @param id идентификатор продукта
     * @return DTO product
     */
    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Product.class, Map.of("id", String.valueOf(id))));
        return productMapper.toProductResponse(product);
    }


    /**
     * получение множества всех DTO продуктов согласно фильтра,
     * в котором устанавливаются различные условия и параметры отбора
     * @return DTO productSet
     */
    @Override
    public Set<ProductResponse> getAllProducts(ProductFilterRequest filterRequest) {
        List<Product> productList = productRepository.findAll(ProductSpecifications.filterByParams(filterRequest));
        Set<Product> productSet = new HashSet<>(productList);
        return productMapper.toProductResponseSet(productSet);
    }


    /**
     * создание нового продукта
     * @param candidate информация от клиента в виде DTO
     * @return DTO продукт
     */
    @Transactional
    @Override
    public ProductResponse createProduct(ProductCreateRequest candidate) {
        User user = userRepository.findById(candidate.userId())
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(candidate.userId()))));
        Product product = new Product();
        product.setName(candidate.name());
        product.setDescription(candidate.description());
        product.setCategory(candidate.category());
        product.setPortionUnit(candidate.portionUnit());
        product.setWeightKg(candidate.weight());
        product.setVolumeLtr(candidate.volume());
        product.setPrice(candidate.price());
        product.setQrCode(candidate.qrCode());
        product.setImageURI(candidate.imageURI());
        product.setAuthor(user);

        product =  productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

    /**
     * обновление существующего продукта по новым параметрам
     *
     * @param id         идентификатор существующего продукта
     * @param productNew информация для обновления в виде DTO
     * @return DTO обновленного продукта
     */
    @Transactional
    @Override
    public ProductResponse updateProduct(Long id, ProductCreateRequest productNew) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Product.class, Map.of("id", String.valueOf(id))));

        User user = userRepository.findById(productNew.userId())
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(productNew.userId()))));

        product.setName(productNew.name());
        product.setDescription(productNew.description());
        product.setCategory(productNew.category());
        product.setPortionUnit(productNew.portionUnit());
        product.setWeightKg(product.getWeightKg());
        product.setVolumeLtr(product.getVolumeLtr());
        product.setPrice(productNew.price());
        product.setImageURI(productNew.imageURI());
        product.setQrCode(productNew.qrCode());
        product.setAuthor(user);
        product =  productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

    /**
     * удаление продукта из БД по его ID
     *
     * @param id идентификатор продукта
     */
    @Transactional
    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * получение множества всех DTO продуктов созданных одним автором
     *
     * @param user_id идентификатор клиента
     * @return DTO productSet
     */
    @Override
    public Set<ProductResponse> getProductByAuthorId(Long user_id) {
        Set<Product> productSet = productRepository.findByAuthorId(user_id);
        return productMapper.toProductResponseSet(productSet);
    }
}
