package org.gafiev.peertopeerbazaar.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.gafiev.peertopeerbazaar.data.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test") // Включает application-test.properties
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class AddressRepositoryTest {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    /**
     * метод выполняется перед каждым тестом.
     * очищает репозиторий и сохраняет тестовые данные.
     */
    @BeforeEach
    void setUp() {

        // очистка репозитория.
        addressRepository.deleteAll();

        // подтверждение, что очистили репозиторий.
        assertTrue(addressRepository.findAll().isEmpty());

        // сохранение в репозитории
        testEntityManager.persistAndFlush(ADDRESS1);
//        testEntityManager.merge(ADDRESS2);
    }

    @Test
        //TODO после проверки добавить связанные сущности
    void findByIdWithSellerOffersAndDeliveries() {
        ADDRESS1.addSellerOffer(SELLER_OFFER1);
        ADDRESS1.addDelivery(DELIVERY1);
        var expected = ADDRESS1;
        var actual = addressRepository.findByIdWithSellerOffersAndDeliveries(expected.getId()).orElseThrow();
        assertEquals(expected, actual);

    }

    @DisplayName("метод проверяет работу фильтров при поиске в БД")
    @Test
    void findAll() {

    }
}