package com.fabrica.p6f5.shipment.repository;

import com.fabrica.p6f5.shipment.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findByStatus(Shipment.Status status);
}



