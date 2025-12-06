package org.gafiev.peertopeerbazaar.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.github.tomakehurst.wiremock.WireMockServer;
import jakarta.annotation.Nonnull;
import lombok.SneakyThrows;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.mapper.DeliveryMapper;
import org.gafiev.peertopeerbazaar.mapper.TimeSlotMapper;
import org.gafiev.peertopeerbazaar.testutils.JwtTestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Testcontainers
@ComponentScan("org.gafiev.peertopeerbazaar")
public abstract class BaseIntegrationTest {

    protected static final String BEARER_PREFIX = "Bearer ";
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17.4-alpine")
            .withDatabaseName("BazaarDB")
            .withUsername("test")
            .withPassword("parole");

    protected static final WireMockServer AUTH_SERVER = new WireMockServer(wireMockConfig().dynamicPort());
    protected static final WireMockServer PAYMENT_SERVICE = new WireMockServer(wireMockConfig().dynamicPort());
    protected static final WireMockServer DRONE_OPERATOR = new WireMockServer(wireMockConfig().dynamicPort());

    static {
        POSTGRES.start();
        AUTH_SERVER.start();
        PAYMENT_SERVICE.start();
        DRONE_OPERATOR.start();
    }

    protected static RSAPrivateKey privateKey;
    protected static RSAPublicKey publicKey;
    protected static String keyId = "test_key_1";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired(required = true)
    protected TestRepositoryHelper testRepositoryHelper;

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected DeliveryMapper deliveryMapper;
    @Autowired
    protected TimeSlotMapper timeSlotMapper;

    protected final Faker faker = new Faker();

    @Nonnull
    protected PreparedData getPreparedData() {
        Address addressSample = TestDataAddress.getAddressSample();
        addressSample = testRepositoryHelper.saveAddress(addressSample);
        assertNotNull(addressSample);
        assertNotNull(addressSample.getId());

        Product productSample = TestDataProduct.getProductSample();
        productSample = testRepositoryHelper.saveProduct(productSample);
        assertNotNull(productSample);
        assertNotNull(productSample.getId());

        User author = productSample.getAuthor();
        assertNotNull(author);
        assertNotNull(author.getId());

//        OfferStatus offerStatus = Arrays.stream(OfferStatus.values()).skip(ThreadLocalRandom.current()
//                .nextInt(0, 3)).findFirst().orElseThrow();
        OfferStatus offerStatus = OfferStatus.OPENED;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime creationDT;
        if (offerStatus == OfferStatus.PRESALE) {
            // Для PRESALE, creationDT должен быть в будущем
            creationDT = now.plusDays(ThreadLocalRandom.current().nextInt(1, 5))  // В будущем
                    .withHour(ThreadLocalRandom.current().nextInt(0, 23))
                    .withMinute(ThreadLocalRandom.current().nextInt(0, 59))
                    .withSecond(0);

        } else {  // Для OPENED или других
            creationDT = now.minusDays(ThreadLocalRandom.current().nextInt(0, 5))
                    .minusHours(ThreadLocalRandom.current().nextInt(0, 23))
                    .minusMinutes(ThreadLocalRandom.current().nextInt(0, 59))
                    .withSecond(0);
        }

        LocalDateTime finishDT = creationDT.plusDays(ThreadLocalRandom.current().nextInt(6, 10))
                .withHour(ThreadLocalRandom.current().nextInt(1, 23))
                .withMinute(ThreadLocalRandom.current().nextInt(1, 59))
                .withSecond(0);
        return new PreparedData(addressSample, productSample, author, offerStatus, creationDT, finishDT);

    }

    protected record PreparedData(Address addressSample, Product productSample, User author, OfferStatus offerStatus,
                                  LocalDateTime creationDT, LocalDateTime finishDT) {
    }


    @SneakyThrows
    protected <T> String toJson(T dto) {
        return objectMapper.writeValueAsString(dto);
    }

    @SneakyThrows
    protected <T> T toDto(String json, Class<T> clazz) {
        return objectMapper.readValue(json, clazz);
    }

    @SneakyThrows
    protected <T> T toDto(String json, TypeReference<T> clazz) {
        return objectMapper.readValue(json, clazz);
    }

    @BeforeAll
    static void beforeAll() throws NoSuchAlgorithmException {
        // Генерируем RSA пару ключей
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        privateKey = (RSAPrivateKey) kp.getPrivate();
        publicKey = (RSAPublicKey) kp.getPublic();

        // Отдаём JWKS с публичным ключом
        String jwks = JwtTestUtils.buildJwksJson(publicKey, keyId);
        AUTH_SERVER.stubFor(get(urlEqualTo("/.well-known/jwks.json"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(jwks)));
    }

    /**
     * метод нужен, чтобы создать property в тестах (вместо application.properties)
     *
     * @param registry
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("bazaar.payment.merchantId", () -> "testmerchantId");
        registry.add("bazaar.payment.secretKey", () -> "testsecretKey");
        registry.add("bazaar.payment.returnUri", () -> "http://localhost:8080/frontend/return/");
        registry.add("bazaar.payment.callbackUri", () -> "http://localhost:8080/callback/notify/");
        registry.add("bazaar.payment.clientUri", PAYMENT_SERVICE::baseUrl);
        registry.add("bazaar.drone.clientUri", DRONE_OPERATOR::baseUrl);
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> AUTH_SERVER.baseUrl() + "/.well-known/jwks.json");
    }
}
