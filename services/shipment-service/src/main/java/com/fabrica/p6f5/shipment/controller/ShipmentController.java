package com.fabrica.p6f5.shipment.controller;

import com.fabrica.p6f5.shipment.model.Shipment;
import com.fabrica.p6f5.shipment.service.ShipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {
    private final ShipmentService service;

    public ShipmentController(ShipmentService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<Shipment>> list(@RequestParam(value = "status", required = false) Shipment.Status status) {
        return ResponseEntity.ok(service.list(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> get(@PathVariable Long id) { return ResponseEntity.ofNullable(service.get(id)); }
}



