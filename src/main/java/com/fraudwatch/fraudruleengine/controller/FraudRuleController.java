package com.fraudwatch.fraudruleengine.controller;

import com.fraudwatch.fraudruleengine.dto.FraudRuleDTO;
import com.fraudwatch.fraudruleengine.exception.ResourceNotFoundException;
import com.fraudwatch.fraudruleengine.service.FraudRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fraud/rules")
@Tag(name = "Fraud Rules", description = "Fraud rule management endpoints")
public class FraudRuleController {

    private final FraudRuleService fraudRuleService;

    public FraudRuleController(FraudRuleService fraudRuleService) {
        this.fraudRuleService = fraudRuleService;
    }

    @GetMapping
    @Operation(summary = "Get all fraud rules")
    public ResponseEntity<Page<FraudRuleDTO>> getAllRules(Pageable pageable) {
        return ResponseEntity.ok(fraudRuleService.getAllRules(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get fraud rule by ID")
    public ResponseEntity<FraudRuleDTO> getRuleById(@PathVariable Long id) {
        FraudRuleDTO dto = fraudRuleService.getRuleById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("Rule not found with id: " + id);
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "Create a new fraud rule")
    public ResponseEntity<FraudRuleDTO> createRule(@Valid @RequestBody FraudRuleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fraudRuleService.createRule(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing fraud rule")
    public ResponseEntity<FraudRuleDTO> updateRule(@PathVariable Long id, @Valid @RequestBody FraudRuleDTO dto) {
        FraudRuleDTO updated = fraudRuleService.updateRule(id, dto);
        if (updated == null) {
            throw new ResourceNotFoundException("Rule not found with id: " + id);
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a fraud rule")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        fraudRuleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
