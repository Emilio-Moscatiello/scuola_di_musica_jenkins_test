package com.scuoladimusica.repository;

import com.scuoladimusica.model.entity.Enrollment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository per la gestione delle iscrizioni (studente-corso).
 *
 * TODO: Aggiungere i seguenti metodi:
 *
 * 1. Metodo per trovare tutte le iscrizioni di uno studente (tramite
 * student_id).
 * Deve restituire una List<Enrollment>. FATTO
 *
 * 2. Metodo per trovare tutte le iscrizioni per un corso (tramite course_id).
 * Deve restituire una List<Enrollment>. FATTO
 *
 * 3. Metodo per verificare se esiste un'iscrizione per una coppia
 * studente-corso.
 * Deve restituire un boolean.
 * SUGGERIMENTO: existsByStudentIdAndCourseId(Long studentId, Long courseId)
 * FATTO
 *
 * 4. Metodo per trovare un'iscrizione tramite la matricola dello studente
 * e il codice del corso (navigando le relazioni).
 * Deve restituire un Optional<Enrollment>.
 * SUGGERIMENTO: findByStudentMatricolaAndCourseCodiceCorso(...) FATTO
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Metodo 1
    List<Enrollment> findByStudentId(Long studentId);

    // Metodo 2
    List<Enrollment> findByCourseId(Long courseId);

    // Metodo 3
    Boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    // Metodo 4
    Optional<Enrollment> findByStudentMatricolaAndCourseCodiceCorso(String matricola, String codiceCorso);
}
