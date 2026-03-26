package com.scuoladimusica.service;

import com.scuoladimusica.exception.DuplicateResourceException;
import com.scuoladimusica.exception.ResourceNotFoundException;
import com.scuoladimusica.model.dto.request.StudentRequest;
import com.scuoladimusica.model.dto.response.StudentReportResponse;
import com.scuoladimusica.model.dto.response.StudentResponse;
import com.scuoladimusica.model.entity.Enrollment;
import com.scuoladimusica.model.entity.Livello;
import com.scuoladimusica.model.entity.Student;
import com.scuoladimusica.repository.EnrollmentRepository;
import com.scuoladimusica.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    /**
     * TODO: Creare un nuovo studente a partire dalla request.
     *
     * Requisiti:
     * - Verificare che non esista già uno studente con la stessa matricola
     * (altrimenti DuplicateResourceException)
     * - Verificare che non esista già uno studente con lo stesso CF (altrimenti
     * DuplicateResourceException)
     * - Se il livello nella request è null, usare PRINCIPIANTE come default
     * - Salvare lo studente nel database
     * - Restituire la response mappata dall'entity salvata
     *
     * Eccezioni da usare: DuplicateResourceException
     */
    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.existsByMatricola(request.matricola())) {
            throw new DuplicateResourceException("Studente con matricola '" + request.matricola() + "' già esistente");
        }

        if (studentRepository.existsByCf(request.cf())) {
            throw new DuplicateResourceException("Studente con codice fiscale '" + request.cf() + "' già esistente");
        }

        Livello livello = request.livello() != null ? request.livello() : Livello.PRINCIPIANTE;

        Student student = Student.builder()
                .matricola(request.matricola())
                .cf(request.cf())
                .nome(request.nome())
                .cognome(request.cognome())
                .dataNascita(request.dataNascita())
                .telefono(request.telefono())
                .livello(livello)
                .build();

        Student saved = studentRepository.save(student);

        return new StudentResponse(
                saved.getId(),
                saved.getMatricola(),
                saved.getCf(),
                saved.getNome(),
                saved.getCognome(),
                saved.getNomeCompleto(),
                saved.getDataNascita(),
                saved.getTelefono(),
                saved.getLivello(),
                saved.getNumeroCorsiFrequentati(),
                saved.getMediaVoti());
    }

    /**
     * TODO: Recuperare uno studente tramite la matricola.
     *
     * Requisiti:
     * - Cercare lo studente per matricola
     * - Se non trovato, lanciare ResourceNotFoundException
     * - Restituire la response mappata dall'entity
     *
     * Eccezioni da usare: ResourceNotFoundException
     */
    @Transactional(readOnly = true)
    public StudentResponse getStudentByMatricola(String matricola) {
        Student s = studentRepository.findByMatricola(matricola).orElseThrow(
                () -> new ResourceNotFoundException("Lo studente con matricola '" + matricola + "' non esiste"));

        return new StudentResponse(
                s.getId(),
                s.getMatricola(),
                s.getCf(),
                s.getNome(),
                s.getCognome(),
                s.getNomeCompleto(),
                s.getDataNascita(),
                s.getTelefono(),
                s.getLivello(),
                s.getNumeroCorsiFrequentati(),
                s.getMediaVoti());
    }

    /**
     * TODO: Recuperare tutti gli studenti.
     *
     * Requisiti:
     * - Restituire la lista di tutti gli studenti come response
     */
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        List<StudentResponse> result = new ArrayList<>();
        for (Student s : studentRepository.findAll()) {
            result.add(new StudentResponse(
                    s.getId(),
                    s.getMatricola(),
                    s.getCf(),
                    s.getNome(),
                    s.getCognome(),
                    s.getNomeCompleto(),
                    s.getDataNascita(),
                    s.getTelefono(),
                    s.getLivello(),
                    s.getNumeroCorsiFrequentati(),
                    s.getMediaVoti()));
        }
        return result;
    }

    /**
     * TODO: Recuperare gli studenti filtrati per livello.
     *
     * Requisiti:
     * - Usare il repository per filtrare per livello
     * - Restituire la lista filtrata come response
     */
    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByLivello(Livello livello) {
        List<StudentResponse> result = new ArrayList<>();
        for (Student s : studentRepository.findByLivello(livello)) {
            result.add(new StudentResponse(
                    s.getId(),
                    s.getMatricola(),
                    s.getCf(),
                    s.getNome(),
                    s.getCognome(),
                    s.getNomeCompleto(),
                    s.getDataNascita(),
                    s.getTelefono(),
                    s.getLivello(),
                    s.getNumeroCorsiFrequentati(),
                    s.getMediaVoti()));
        }
        return result;
    }

    /**
     * TODO: Aggiornare uno studente esistente.
     *
     * Requisiti:
     * - Trovare lo studente per matricola (ResourceNotFoundException se non
     * trovato)
     * - Aggiornare i campi: nome, cognome, telefono, livello
     * - NON aggiornare: matricola, cf, dataNascita (sono immutabili)
     * - Salvare e restituire la response aggiornata
     *
     * Eccezioni da usare: ResourceNotFoundException
     */
    public StudentResponse updateStudent(String matricola, StudentRequest request) {
        Student s = studentRepository.findByMatricola(matricola).orElseThrow(
                () -> new ResourceNotFoundException("Lo studente con matricola '" + matricola + "' non esiste"));

        s.setNome(request.nome());
        s.setCognome(request.cognome());
        s.setTelefono(request.telefono());
        if (request.livello() != null) {
            s.setLivello(request.livello());
        }

        Student saved = studentRepository.save(s);
        return new StudentResponse(
                saved.getId(), saved.getMatricola(), saved.getCf(), saved.getNome(), saved.getCognome(),
                saved.getNomeCompleto(), saved.getDataNascita(), saved.getTelefono(), saved.getLivello(),
                saved.getNumeroCorsiFrequentati(), saved.getMediaVoti());
    }

    /**
     * TODO: Eliminare uno studente per matricola.
     *
     * Requisiti:
     * - Trovare lo studente per matricola (ResourceNotFoundException se non
     * trovato)
     * - Eliminare lo studente dal database
     *
     * Eccezioni da usare: ResourceNotFoundException
     */
    public void deleteStudent(String matricola) {
        Student s = studentRepository.findByMatricola(matricola).orElseThrow(
                () -> new ResourceNotFoundException("Lo studente con matricola '" + matricola + "' non esiste"));

        studentRepository.delete(s);
    }

    /**
     * TODO: Generare il report di uno studente.
     *
     * Requisiti:
     * - Trovare lo studente per matricola (ResourceNotFoundException se non
     * trovato)
     * - Costruire un StudentReportResponse con:
     * - studente: nome completo
     * - livello: livello attuale
     * - numCorsi: numero di corsi frequentati
     * - mediaVoti: media dei voti
     * - corsi: lista dei nomi dei corsi a cui è iscritto
     *
     * Eccezioni da usare: ResourceNotFoundException
     */
    @Transactional(readOnly = true)
    public StudentReportResponse getStudentReport(String matricola) {
        Student s = studentRepository.findByMatricola(matricola).orElseThrow(
                () -> new ResourceNotFoundException("Lo studente con matricola '" + matricola + "' non esiste"));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(s.getId());

        List<String> nomiCorsi = enrollments.stream()
                .map(e -> e.getCourse().getNome())
                .toList();

        int numCorsi = enrollments.size();

        List<Integer> voti = enrollments.stream()
                .map(Enrollment::getVotoFinale)
                .filter(v -> v != null)
                .toList();
        double mediaVoti = voti.isEmpty() ? 0.0
                : voti.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        return new StudentReportResponse(
                s.getNomeCompleto(),
                s.getLivello(),
                numCorsi,
                mediaVoti,
                nomiCorsi);
    }
}
