package org.gafiev.peertopeerbazaar.service.model;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.ProductResponse;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.gafiev.peertopeerbazaar.entity.user.Role;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.mapper.ProductMapper;
import org.gafiev.peertopeerbazaar.repository.ProductRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.repository.specification.ProductSpecifications;
import org.gafiev.peertopeerbazaar.service.model.interfaces.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Product.class, Map.of("id", String.valueOf(id))));
        return productMapper.toProductResponse(product);
    }

    @Override
    public Set<ProductResponse> getAllProducts(ProductFilterRequest filterRequest) {
        List<Product> productList = productRepository.findAll(ProductSpecifications.filterByParams(filterRequest));
        Set<Product> productSet = new HashSet<>(productList);
        return productMapper.toProductResponseSet(productSet);
    }

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
        if (!user.getRoles().contains(Role.SELLER)) {
            user.addRole(Role.SELLER);
        }
        product.setCreatedAt(Instant.now());

        product = productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateRequest updateRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Product.class, Map.of("id", String.valueOf(id))));


        product.setName(updateRequest.name());
        product.setDescription(updateRequest.description());
        product.setCategory(updateRequest.category());
        product.setWeightKg(updateRequest.weight());
        product.setVolumeLtr(updateRequest.volume());
        product.setPrice(updateRequest.price());
        product.setImageURI(updateRequest.imageURI());
        product.setQrCode(updateRequest.qrCode());

        product = productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new EntityNotFoundException(Product.class, Map.of("id", String.valueOf(id))));
        User author = product.getAuthor();
        author.removeProduct(product);
        if (author.getProductSet().isEmpty()) {
            author.removeRole(Role.SELLER);
        }
        userRepository.save(author);
        productRepository.deleteById(id);
    }

    @Override
    public Set<ProductResponse> getProductByAuthorId(Long authorId) {
        Set<Product> productSet = productRepository.findByAuthorId(authorId);
        return productMapper.toProductResponseSet(productSet);
    }
}
