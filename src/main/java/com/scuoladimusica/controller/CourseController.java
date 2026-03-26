package com.scuoladimusica.controller;

import com.scuoladimusica.model.dto.request.CourseRequest;
import com.scuoladimusica.model.dto.request.LessonRequest;
import com.scuoladimusica.model.dto.response.CourseResponse;
import com.scuoladimusica.model.dto.response.LessonResponse;
import com.scuoladimusica.model.dto.response.MessageResponse;
import com.scuoladimusica.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * TODO: POST /api/courses - Creare un nuovo corso.
     *
     * Requisiti:
     * - Solo ADMIN può creare corsi
     * - Validare la request con @Valid
     * - Restituire 201 CREATED con la CourseResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * TODO: GET /api/courses - Recuperare tutti i corsi.
     *
     * Requisiti:
     * - Tutti gli utenti autenticati possono vedere i corsi
     * - Restituire 200 OK con la lista
     */
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /**
     * TODO: GET /api/courses/online - Recuperare solo i corsi online.
     *
     * Requisiti:
     * - Tutti gli utenti autenticati
     * - Restituire 200 OK con la lista filtrata
     */
    @GetMapping("/online")
    public ResponseEntity<List<CourseResponse>> getOnlineCourses() {
        return ResponseEntity.ok(courseService.getOnlineCourses());
    }

    /**
     * TODO: GET /api/courses/{codiceCorso} - Recuperare un corso per codice.
     *
     * Requisiti:
     * - Tutti gli utenti autenticati
     * - Restituire 200 OK con la CourseResponse
     */
    @GetMapping("/{codiceCorso}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable String codiceCorso) {
        return ResponseEntity.ok(courseService.getCourseByCode(codiceCorso));
    }

    /**
     * TODO: PUT /api/courses/{codiceCorso} - Aggiornare un corso.
     *
     * Requisiti:
     * - Solo ADMIN e TEACHER possono aggiornare
     * - Validare la request
     * - Restituire 200 OK con la CourseResponse aggiornata
     */
    @PutMapping("/{codiceCorso}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable String codiceCorso,
            @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(codiceCorso, request));
    }

    /**
     * TODO: DELETE /api/courses/{codiceCorso} - Eliminare un corso.
     *
     * Requisiti:
     * - Solo ADMIN può eliminare
     * - Restituire 204 NO CONTENT
     */
    @DeleteMapping("/{codiceCorso}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable String codiceCorso) {
        courseService.deleteCourse(codiceCorso);
        return ResponseEntity.noContent().build();
    }

    /**
     * TODO: POST /api/courses/{codiceCorso}/lessons - Aggiungere una lezione.
     *
     * Requisiti:
     * - Solo ADMIN e TEACHER possono aggiungere lezioni
     * - Validare la request
     * - Restituire 201 CREATED con la LessonResponse
     */
    @PostMapping("/{codiceCorso}/lessons")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<LessonResponse> addLesson(
            @PathVariable String codiceCorso,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.addLesson(codiceCorso, request));
    }

    /**
     * TODO: POST /api/courses/{codiceCorso}/instruments/{codiceStrumento} - Aggiungere strumento.
     *
     * Requisiti:
     * - Solo ADMIN può aggiungere strumenti ai corsi
     * - Restituire 200 OK con MessageResponse("Strumento aggiunto al corso")
     */
    @PostMapping("/{codiceCorso}/instruments/{codiceStrumento}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> addInstrument(
            @PathVariable String codiceCorso,
            @PathVariable String codiceStrumento) {
        courseService.addInstrumentToCourse(codiceCorso, codiceStrumento);
        return ResponseEntity.ok(new MessageResponse("Strumento aggiunto al corso"));
    }
}
