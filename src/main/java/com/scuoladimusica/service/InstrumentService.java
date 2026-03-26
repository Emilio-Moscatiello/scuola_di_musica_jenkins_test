package com.scuoladimusica.service;

import com.scuoladimusica.exception.BusinessRuleException;
import com.scuoladimusica.exception.DuplicateResourceException;
import com.scuoladimusica.exception.ResourceNotFoundException;
import com.scuoladimusica.model.dto.request.InstrumentRequest;
import com.scuoladimusica.model.dto.response.InstrumentResponse;
import com.scuoladimusica.model.dto.response.LoanResponse;
import com.scuoladimusica.model.entity.Instrument;
import com.scuoladimusica.model.entity.Loan;
import com.scuoladimusica.model.entity.Student;
import com.scuoladimusica.repository.InstrumentRepository;
import com.scuoladimusica.repository.LoanRepository;
import com.scuoladimusica.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class InstrumentService {

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * TODO: Creare un nuovo strumento.
     *
     * Requisiti:
     * - Verificare unicità codice strumento (DuplicateResourceException)
     * - Salvare e restituire la response
     */
    public InstrumentResponse createInstrument(InstrumentRequest request) {
        if (instrumentRepository.existsByCodiceStrumento(request.codiceStrumento())) {
            throw new DuplicateResourceException(
                    "Lo strumento con codice '" + request.codiceStrumento() + "' risulta già registrato");
        }

        Instrument instrument = Instrument.builder().codiceStrumento(request.codiceStrumento()).nome(request.nome())
                .tipoStrumento(request.tipoStrumento()).marca(request.marca()).annoProduzione(request.annoProduzione())
                .build();

        Instrument saved = instrumentRepository.save(instrument);

        return new InstrumentResponse(
                saved.getId(),
                saved.getCodiceStrumento(),
                saved.getNome(),
                saved.getTipoStrumento(),
                saved.getMarca(),
                saved.getAnnoProduzione(),
                saved.isDisponibile());
    }

    /**
     * TODO: Recuperare uno strumento per codice.
     *
     * Requisiti:
     * - ResourceNotFoundException se non trovato
     * - Includere nella response se è disponibile o meno
     */
    @Transactional(readOnly = true)
    public InstrumentResponse getInstrumentByCode(String codiceStrumento) {
        Instrument i = instrumentRepository.findByCodiceStrumento(codiceStrumento).orElseThrow(
                () -> new ResourceNotFoundException("Lo strumento con codice '" + codiceStrumento + "' non esiste"));

        boolean disponibile = !loanRepository.existsByInstrumentIdAndDataFineIsNull(i.getId());
        return new InstrumentResponse(
                i.getId(),
                i.getCodiceStrumento(),
                i.getNome(),
                i.getTipoStrumento(),
                i.getMarca(),
                i.getAnnoProduzione(),
                disponibile);
    }

    /**
     * TODO: Recuperare tutti gli strumenti.
     */
    @Transactional(readOnly = true)
    public List<InstrumentResponse> getAllInstruments() {
        List<InstrumentResponse> result = new ArrayList<>();
        for (Instrument i : instrumentRepository.findAll()) {
            result.add(new InstrumentResponse(
                    i.getId(),
                    i.getCodiceStrumento(),
                    i.getNome(),
                    i.getTipoStrumento(),
                    i.getMarca(),
                    i.getAnnoProduzione(),
                    i.isDisponibile()));
        }
        return result;
    }

    /**
     * TODO: Recuperare solo gli strumenti disponibili (senza prestiti attivi).
     *
     * Requisiti:
     * - Usare il metodo findAvailable() del repository
     */
    @Transactional(readOnly = true)
    public List<InstrumentResponse> getAvailableInstruments() {
        List<InstrumentResponse> result = new ArrayList<>();
        for (Instrument i : instrumentRepository.findStrumentiDisponibili()) {
            result.add(new InstrumentResponse(
                    i.getId(),
                    i.getCodiceStrumento(),
                    i.getNome(),
                    i.getTipoStrumento(),
                    i.getMarca(),
                    i.getAnnoProduzione(),
                    true));
        }
        return result;
    }

    /**
     * TODO: Prestare uno strumento a uno studente.
     *
     * Requisiti:
     * - Trovare lo strumento per codice (ResourceNotFoundException se non trovato)
     * - Trovare lo studente per matricola (ResourceNotFoundException se non
     * trovato)
     * - Verificare che lo strumento sia disponibile (BusinessRuleException se già
     * in prestito)
     * - Creare un nuovo Loan con dataInizio e dataFine = null (prestito attivo)
     * - Salvare e restituire la LoanResponse
     *
     * Eccezioni da usare: ResourceNotFoundException, BusinessRuleException
     */
    public LoanResponse loanToStudent(String codiceStrumento, String matricolaStudente, LocalDate dataInizio) {
        Instrument instrument = instrumentRepository.findByCodiceStrumento(codiceStrumento).orElseThrow(
                () -> new ResourceNotFoundException("Lo strumento con codice '" + codiceStrumento + "' non esiste"));

        Student student = studentRepository.findByMatricola(matricolaStudente).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Lo studente con matricola '" + matricolaStudente + "' non esiste"));

        if (loanRepository.existsByInstrumentIdAndDataFineIsNull(instrument.getId())) {
            throw new BusinessRuleException("Lo strumento '" + codiceStrumento + "' è già in prestito");
        }

        Loan loan = Loan.builder()
                .instrument(instrument)
                .student(student)
                .dataInizio(dataInizio)
                .dataFine(null)
                .build();

        Loan saved = loanRepository.save(loan);

        return new LoanResponse(
                saved.getId(),
                saved.getInstrument().getCodiceStrumento(),
                saved.getInstrument().getNome(),
                saved.getStudent().getMatricola(),
                saved.getStudent().getNome() + " " + saved.getStudent().getCognome(),
                saved.getDataInizio(),
                saved.getDataFine());
    }

    /**
     * TODO: Restituire uno strumento (chiudere il prestito attivo).
     *
     * Requisiti:
     * - Trovare lo strumento per codice (ResourceNotFoundException se non trovato)
     * - Trovare il prestito attivo (dataFine IS NULL) per questo strumento
     * (BusinessRuleException se non c'è un prestito attivo)
     * - Verificare che dataFine >= dataInizio del prestito (BusinessRuleException)
     * - Impostare la dataFine sul prestito e salvare
     * - Restituire la LoanResponse
     *
     * Eccezioni da usare: ResourceNotFoundException, BusinessRuleException
     */
    public LoanResponse returnInstrument(String codiceStrumento, LocalDate dataFine) {
        Instrument instrument = instrumentRepository.findByCodiceStrumento(codiceStrumento).orElseThrow(
                () -> new ResourceNotFoundException("Lo strumento con codice '" + codiceStrumento + "' non esiste"));

        Loan loan = loanRepository.findByInstrumentIdAndDataFineIsNull(instrument.getId())
                .orElseThrow(() -> new BusinessRuleException(
                        "Lo strumento '" + codiceStrumento + "' non ha un prestito attivo"));

        if (dataFine.isBefore(loan.getDataInizio())) {
            throw new BusinessRuleException(
                    "La data di restituzione non può essere precedente alla data di inizio prestito");
        }

        loan.setDataFine(dataFine);
        Loan saved = loanRepository.save(loan);

        return new LoanResponse(
                saved.getId(),
                saved.getInstrument().getCodiceStrumento(),
                saved.getInstrument().getNome(),
                saved.getStudent().getMatricola(),
                saved.getStudent().getNome() + " " + saved.getStudent().getCognome(),
                saved.getDataInizio(),
                saved.getDataFine());
    }
}
