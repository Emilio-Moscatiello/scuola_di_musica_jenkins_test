package com.scuoladimusica.repository;

import com.scuoladimusica.model.entity.Course;
import com.scuoladimusica.model.entity.Livello;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository per la gestione dei corsi.
 *
 * TODO: Aggiungere i seguenti metodi di query derivati:
 *
 * 1. Metodo per trovare un corso tramite il suo codice corso.
 * Deve restituire un Optional<Course>. FATTO
 *
 * 2. Metodo per verificare se esiste un corso con un dato codice corso.
 * Deve restituire un boolean. FATTO
 *
 * 3. Metodo per trovare tutti i corsi online (campo online = true).
 * Deve restituire una List<Course>.
 * SUGGERIMENTO: findBy + NomeCampo + True FATTO
 *
 * 4. Metodo per trovare tutti i corsi filtrati per livello.
 * Deve restituire una List<Course>. FATTO
 *
 * 5. Metodo per trovare tutti i corsi assegnati a un insegnante (tramite
 * teacher_id).
 * Deve restituire una List<Course>.
 * SUGGERIMENTO: findByTeacherId(Long teacherId) FATTO
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Metodo 1
    Optional<Course> findByCodiceCorso(String codiceCorso);

    // Metodo 2
    Boolean existsByCodiceCorso(String codiceCorso);

    // Metodo 3
    List<Course> findByOnlineTrue();

    // Metodo 4
    List<Course> findByLivello(Livello livello);

    // Metodo 5
    List<Course> findByTeacherId(Long teacherId);
}
