package org.gafiev.peertopeerbazaar.service;

import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.gafiev.peertopeerbazaar.repository.DroneRepository;
import org.gafiev.peertopeerbazaar.service.interfaces.DroneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DroneServiceImpl implements DroneService {
    private final DroneRepository droneRepository;

    @Autowired
    public DroneServiceImpl(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Drone> getDroneById(Long id) {
        return droneRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Drone> getAllDrones() {
        return droneRepository.findAll();
    }

    @Transactional
    public Drone createDrone(Drone drone) {
        return droneRepository.save(drone);
    }

    @Transactional
    public Drone updateDrone(Long id, Drone droneDetails) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drone not found with id " + id));
        drone.setDroneStatus(droneDetails.getDroneStatus());

        return droneRepository.save(drone);
    }

    @Transactional
    public void deleteQuadcopter(Long id) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drone not found with id " + id));
        droneRepository.delete(drone);
    }

}
