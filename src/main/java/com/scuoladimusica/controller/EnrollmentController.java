package com.scuoladimusica.controller;

import com.scuoladimusica.model.dto.request.EnrollmentRequest;
import com.scuoladimusica.model.dto.request.VoteRequest;
import com.scuoladimusica.model.dto.response.EnrollmentResponse;
import com.scuoladimusica.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    /**
     * TODO: POST /api/enrollments - Iscrivere uno studente a un corso.
     *
     * Requisiti:
     * - ADMIN, TEACHER e STUDENT possono iscrivere
     * - Validare la request con @Valid
     * - Restituire 201 CREATED con la EnrollmentResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponse> enrollStudent(
            @Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.enrollStudent(request.matricolaStudente(),
                request.codiceCorso(), request.annoIscrizione());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * TODO: POST /api/enrollments/{matricola}/{codiceCorso}/vote - Registrare voto.
     *
     * Requisiti:
     * - Solo ADMIN e TEACHER possono registrare voti
     * - Validare la request con @Valid
     * - Restituire 200 OK con la EnrollmentResponse aggiornata
     */
    @PostMapping("/{matricola}/{codiceCorso}/vote")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<EnrollmentResponse> registerVote(
            @PathVariable String matricola,
            @PathVariable String codiceCorso,
            @Valid @RequestBody VoteRequest request) {
        EnrollmentResponse response = enrollmentService.registerVote(matricola, codiceCorso, request.voto());
        return ResponseEntity.ok(response);
    }

    /**
     * TODO: GET /api/enrollments/student/{matricola} - Iscrizioni di uno studente.
     *
     * Requisiti:
     * - ADMIN e TEACHER possono vedere le iscrizioni di qualsiasi studente
     * - Restituire 200 OK con la lista di EnrollmentResponse
     */
    @GetMapping("/student/{matricola}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByStudent(
            @PathVariable String matricola) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(matricola));
    }

    /**
     * TODO: GET /api/enrollments/course/{codiceCorso} - Iscrizioni per un corso.
     *
     * Requisiti:
     * - ADMIN e TEACHER possono vedere le iscrizioni per corso
     * - Restituire 200 OK con la lista di EnrollmentResponse
     */
    @GetMapping("/course/{codiceCorso}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByCourse(
            @PathVariable String codiceCorso) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(codiceCorso));
    }
}
