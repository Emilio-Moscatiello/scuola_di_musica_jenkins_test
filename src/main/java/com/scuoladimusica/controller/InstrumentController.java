package com.scuoladimusica.controller;

import com.scuoladimusica.model.dto.request.InstrumentRequest;
import com.scuoladimusica.model.dto.request.LoanRequest;
import com.scuoladimusica.model.dto.request.ReturnRequest;
import com.scuoladimusica.model.dto.response.InstrumentResponse;
import com.scuoladimusica.model.dto.response.LoanResponse;
import com.scuoladimusica.service.InstrumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instruments")
public class InstrumentController {

    @Autowired
    private InstrumentService instrumentService;

    /**
     * TODO: POST /api/instruments - Creare un nuovo strumento.
     *
     * Requisiti:
     * - Solo ADMIN può creare strumenti
     * - Validare la request con @Valid
     * - Restituire 201 CREATED con la InstrumentResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InstrumentResponse> createInstrument(
            @Valid @RequestBody InstrumentRequest request) {
        InstrumentResponse response = instrumentService.createInstrument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * TODO: GET /api/instruments - Recuperare tutti gli strumenti.
     *
     * Requisiti:
     * - Tutti gli utenti autenticati
     * - Restituire 200 OK con la lista
     */
    @GetMapping
    public ResponseEntity<List<InstrumentResponse>> getAllInstruments() {
        return ResponseEntity.ok(instrumentService.getAllInstruments());
    }

    /**
     * TODO: GET /api/instruments/available - Recuperare strumenti disponibili.
     *
     * Requisiti:
     * - Tutti gli utenti autenticati
     * - Restituire 200 OK con la lista filtrata
     */
    @GetMapping("/available")
    public ResponseEntity<List<InstrumentResponse>> getAvailableInstruments() {
        return ResponseEntity.ok(instrumentService.getAvailableInstruments());
    }

    /**
     * TODO: GET /api/instruments/{codiceStrumento} - Recuperare uno strumento.
     *
     * Requisiti:
     * - Tutti gli utenti autenticati
     * - Restituire 200 OK con la InstrumentResponse
     */
    @GetMapping("/{codiceStrumento}")
    public ResponseEntity<InstrumentResponse> getInstrument(@PathVariable String codiceStrumento) {
        return ResponseEntity.ok(instrumentService.getInstrumentByCode(codiceStrumento));
    }

    /**
     * TODO: POST /api/instruments/{codiceStrumento}/loan - Prestare strumento.
     *
     * Requisiti:
     * - Solo ADMIN e TEACHER possono prestare strumenti
     * - Validare la request con @Valid
     * - Restituire 201 CREATED con la LoanResponse
     */
    @PostMapping("/{codiceStrumento}/loan")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<LoanResponse> loanInstrument(
            @PathVariable String codiceStrumento,
            @Valid @RequestBody LoanRequest request) {
        LoanResponse response = instrumentService.loanToStudent(codiceStrumento, request.matricolaStudente(),
                request.dataInizio());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * TODO: POST /api/instruments/{codiceStrumento}/return - Restituire strumento.
     *
     * Requisiti:
     * - Solo ADMIN e TEACHER possono gestire restituzioni
     * - Validare la request con @Valid
     * - Restituire 200 OK con la LoanResponse
     */
    @PostMapping("/{codiceStrumento}/return")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<LoanResponse> returnInstrument(
            @PathVariable String codiceStrumento,
            @Valid @RequestBody ReturnRequest request) {
        LoanResponse response = instrumentService.returnInstrument(codiceStrumento, request.dataFine());
        return ResponseEntity.ok(response);
    }
}
