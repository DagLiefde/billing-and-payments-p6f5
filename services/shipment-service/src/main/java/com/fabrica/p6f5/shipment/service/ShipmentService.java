package com.fabrica.p6f5.shipment.service;

import com.fabrica.p6f5.shipment.model.Shipment;
import com.fabrica.p6f5.shipment.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentService {
    private final ShipmentRepository repository;

    public ShipmentService(ShipmentRepository repository) {
        this.repository = repository;
    }

    public List<Shipment> list(Shipment.Status status) {
        return status == null ? repository.findAll() : repository.findByStatus(status);
    }

    public Shipment get(Long id) { return repository.findById(id).orElse(null); }
}



