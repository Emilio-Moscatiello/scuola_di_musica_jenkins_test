package com.scuoladimusica.repository;

import com.scuoladimusica.model.entity.Livello;
import com.scuoladimusica.model.entity.Student;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository per la gestione degli studenti.
 *
 * Estendendo JpaRepository si ottengono automaticamente i metodi CRUD di base:
 * save(), findById(), findAll(), deleteById(), count(), ecc.
 *
 * TODO: Aggiungere i seguenti metodi di query derivati:
 *
 * 1. Metodo per trovare uno studente tramite la sua matricola.
 * Deve restituire un Optional<Student>. FATTO
 *
 * 2. Metodo per verificare se esiste uno studente con una data matricola.
 * Deve restituire un boolean. FATTO
 *
 * 3. Metodo per verificare se esiste uno studente con un dato codice fiscale.
 * Deve restituire un boolean. FATTO
 *
 * 4. Metodo per trovare tutti gli studenti che hanno un certo livello.
 * Deve restituire una List<Student>.
 * Il parametro è di tipo Livello (enum). FATTO
 *
 * SUGGERIMENTO: Spring Data JPA genera automaticamente l'implementazione
 * a partire dal nome del metodo. Ad esempio:
 * Optional<Entity> findByNomeCampo(TipoCampo valore)
 * boolean existsByNomeCampo(TipoCampo valore)
 * List<Entity> findByNomeCampo(TipoCampo valore)
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Metodo 1
    Optional<Student> findByMatricola(String matricola);

    // Metodo 2
    Boolean existsByMatricola(String matricola);

    // Metodo 3
    Boolean existsByCf(String cf);

    // Metodo 4
    List<Student> findByLivello(Livello livello);
}
