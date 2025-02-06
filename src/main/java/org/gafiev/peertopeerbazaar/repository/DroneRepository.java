package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DroneRepository extends JpaRepository<Drone,Long>, JpaSpecificationExecutor<Drone> {

    /**
     * Получает сущность Drone по её ID вместе с ассоциированными доставками.
     * Этот метод использует аннотацию @EntityGraph для жадной загрузки
     * коллекции deliverySet, что позволяет избежать проблемы N+1
     * при извлечении данных.
     *
     * @param id ID дрона, которого нужно получить
     * @return Optional, содержащий дрон, если он найден,
     * или пустой Optional, если пользователь с указанным ID не существует
     */
    @EntityGraph(attributePaths = {"deliverySet"})
    @Query("SELECT d FROM Drone d  WHERE d.id = :id")
    Optional<Drone> findByIdWithDeliveries(Long id);

    @EntityGraph(attributePaths = {"deliverySet"})
    @Query("SELECT d FROM Drone d  WHERE d.droneServiceId = :droneServiceId")
    Optional<Drone> findByDroneServiceIdWithDeliveries(Long droneServiceId);


}
