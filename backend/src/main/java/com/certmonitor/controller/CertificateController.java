package com.certmonitor.controller;

import com.certmonitor.dto.*;
import com.certmonitor.service.CertificateService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = "http://localhost:4200")
public class CertificateController {
    private final CertificateService service;
    public CertificateController(CertificateService service) { this.service = service; }

    @GetMapping public List<CertificateResponse> list() { return service.findAll(); }
    @GetMapping("/{id}") public CertificateResponse get(@PathVariable Long id) { return service.findById(id); }
    @PostMapping
    public ResponseEntity<CertificateResponse> create(@Valid @RequestBody CertificateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
    @PutMapping("/{id}") public CertificateResponse update(@PathVariable Long id, @Valid @RequestBody CertificateRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id); return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/acknowledge")
    public CertificateResponse acknowledge(@PathVariable Long id, @RequestBody(required=false) Map<String,String> body) {
        return service.acknowledge(id, body == null ? null : body.get("acknowledgedBy"));
    }
}
