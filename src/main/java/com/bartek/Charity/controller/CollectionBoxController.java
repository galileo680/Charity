package com.bartek.Charity.controller;

import com.bartek.Charity.dto.request.AddMoneyRequest;
import com.bartek.Charity.dto.request.AssignCollectionBoxRequest;
import com.bartek.Charity.dto.request.RegisterCollectionBoxRequest;
import com.bartek.Charity.dto.response.CollectionBoxResponse;
import com.bartek.Charity.dto.response.MoneyTransferResponse;
import com.bartek.Charity.service.CollectionBoxService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collection-boxes")
@RequiredArgsConstructor
public class CollectionBoxController {

    private final CollectionBoxService collectionBoxService;

    @PostMapping
    public ResponseEntity<CollectionBoxResponse> registerCollectionBox(
            @Valid @RequestBody RegisterCollectionBoxRequest request
    ) {
        CollectionBoxResponse response = collectionBoxService.registerCollectionBox(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CollectionBoxResponse>> listAllCollectionBoxes() {
        List<CollectionBoxResponse> boxes = collectionBoxService.listAllCollectionBoxes();
        return ResponseEntity.ok(boxes);
    }

    @DeleteMapping("/{identifier}")
    public ResponseEntity<Void> unregisterCollectionBox(
            @PathVariable String identifier
    ) {
        collectionBoxService.unregisterCollectionBox(identifier);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{identifier}/assign")
    public ResponseEntity<CollectionBoxResponse> assignCollectionBox(
            @PathVariable String identifier,
            @Valid @RequestBody AssignCollectionBoxRequest request
    ) {
        CollectionBoxResponse response = collectionBoxService.assignCollectionBox(identifier, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{identifier}/money")
    public ResponseEntity<Void> addMoneyToBox(
            @PathVariable String identifier,
            @Valid @RequestBody AddMoneyRequest request
    ) {
        collectionBoxService.addMoneyToBox(identifier, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{identifier}/empty")
    public ResponseEntity<MoneyTransferResponse> emptyCollectionBox(
            @PathVariable String identifier
    ) {
        MoneyTransferResponse response = collectionBoxService.emptyCollectionBox(identifier);
        return ResponseEntity.ok(response);
    }
}