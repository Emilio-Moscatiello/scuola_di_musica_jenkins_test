package com.scuoladimusica.controller;

import com.scuoladimusica.model.dto.request.TeacherRequest;
import com.scuoladimusica.model.dto.response.MessageResponse;
import com.scuoladimusica.model.dto.response.TeacherResponse;
import com.scuoladimusica.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    /**
     * TODO: POST /api/teachers - Creare un nuovo insegnante.
     *
     * Requisiti:
     * - Solo ADMIN può creare insegnanti
     * - Validare la request con @Valid
     * - Restituire 201 CREATED con la TeacherResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherResponse> createTeacher(@Valid @RequestBody TeacherRequest request) {
        TeacherResponse response = teacherService.createTeacher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * TODO: GET /api/teachers - Recuperare tutti gli insegnanti.
     *
     * Requisiti:
     * - Solo ADMIN può vedere tutti gli insegnanti
     * - Restituire 200 OK con la lista
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TeacherResponse>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    /**
     * TODO: GET /api/teachers/{matricola} - Recuperare un insegnante per matricola.
     *
     * Requisiti:
     * - ADMIN e TEACHER possono cercare
     * - Restituire 200 OK con la TeacherResponse
     */
    @GetMapping("/{matricola}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<TeacherResponse> getTeacher(@PathVariable String matricola) {
        return ResponseEntity.ok(teacherService.getTeacherByMatricola(matricola));
    }

    /**
     * TODO: PUT /api/teachers/{matricola} - Aggiornare un insegnante.
     *
     * Requisiti:
     * - Solo ADMIN può aggiornare
     * - Validare la request
     * - Restituire 200 OK con la TeacherResponse aggiornata
     */
    @PutMapping("/{matricola}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherResponse> updateTeacher(
            @PathVariable String matricola,
            @Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.ok(teacherService.updateTeacher(matricola, request));
    }

    /**
     * TODO: DELETE /api/teachers/{matricola} - Eliminare un insegnante.
     *
     * Requisiti:
     * - Solo ADMIN può eliminare
     * - Restituire 204 NO CONTENT
     */
    @DeleteMapping("/{matricola}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeacher(@PathVariable String matricola) {
        teacherService.deleteTeacher(matricola);
        return ResponseEntity.noContent().build();
    }

    /**
     * TODO: POST /api/teachers/{matricola}/courses/{codiceCorso} - Assegnare un
     * corso.
     *
     * Requisiti:
     * - Solo ADMIN può assegnare corsi
     * - Restituire 200 OK con MessageResponse("Corso assegnato con successo")
     */
    @PostMapping("/{matricola}/courses/{codiceCorso}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> assignCourse(
            @PathVariable String matricola,
            @PathVariable String codiceCorso) {
        teacherService.assignCourse(matricola, codiceCorso);
        return ResponseEntity.ok(new MessageResponse("Corso assegnato con successo"));
    }
}
