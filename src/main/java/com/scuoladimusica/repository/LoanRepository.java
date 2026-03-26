package com.scuoladimusica.repository;

import com.scuoladimusica.model.entity.Instrument;
import com.scuoladimusica.model.entity.Loan;
import com.scuoladimusica.model.entity.Student;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository per la gestione dei prestiti strumenti.
 *
 * TODO: Aggiungere i seguenti metodi:
 *
 * 1. Metodo per trovare tutti i prestiti di uno studente (tramite student_id).
 * Deve restituire una List<Loan>. FATTO
 *
 * 2. Metodo per trovare tutti i prestiti di uno strumento (tramite
 * instrument_id).
 * Deve restituire una List<Loan>. FATTO
 *
 * 3. Metodo per trovare il prestito attivo di uno strumento
 * (cioè quello con dataFine IS NULL per un dato instrument_id).
 * Deve restituire un Optional<Loan>.
 * SUGGERIMENTO: findByInstrumentIdAndDataFineIsNull(Long instrumentId) FATTO
 *
 * 4. Metodo per verificare se esiste un prestito attivo per uno strumento.
 * Deve restituire un boolean.
 * SUGGERIMENTO: existsByInstrumentIdAndDataFineIsNull(Long instrumentId) FATTO
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Metodo 1
    List<Loan> findByStudentId(Long studentId);

    // Metodo 2
    List<Loan> findByInstrumentId(Long instrumentId);

    // Metodo 3
    Optional<Loan> findByInstrumentIdAndDataFineIsNull(Long instrumentId);

    // Metodo 4
    Boolean existsByInstrumentIdAndDataFineIsNull(Long instrumentId);
}
