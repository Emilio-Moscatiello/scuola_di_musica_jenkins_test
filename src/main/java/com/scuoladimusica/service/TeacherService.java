package com.scuoladimusica.service;

import com.scuoladimusica.model.dto.request.TeacherRequest;
import com.scuoladimusica.model.dto.response.TeacherResponse;
import com.scuoladimusica.repository.CourseRepository;
import com.scuoladimusica.repository.TeacherRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import com.scuoladimusica.exception.BusinessRuleException;
import com.scuoladimusica.exception.DuplicateResourceException;
import com.scuoladimusica.exception.ResourceNotFoundException;
import com.scuoladimusica.model.entity.Course;
import com.scuoladimusica.model.entity.Teacher;

@Service
@Transactional
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    /**
     * TODO: Creare un nuovo insegnante.
     *
     * Requisiti:
     * - Verificare unicità matricola insegnante (DuplicateResourceException)
     * - Verificare unicità CF (DuplicateResourceException)
     * - Validare che lo stipendio sia > 0 (BusinessRuleException)
     * - Salvare e restituire la response
     *
     * Eccezioni da usare: DuplicateResourceException, BusinessRuleException
     */
    public TeacherResponse createTeacher(TeacherRequest request) {
        if (teacherRepository.existsByMatricolaInsegnante(request.matricolaInsegnante())) {
            throw new DuplicateResourceException(
                    "Insegnante con matricola '" + request.matricolaInsegnante() + "' già esistente");
        }

        if (teacherRepository.existsByCf(request.cf())) {
            throw new DuplicateResourceException(
                    "Insegnante con codice fiscale '" + request.cf() + "' già esistente");
        }

        if (request.stipendio() <= 0) {
            throw new BusinessRuleException("Lo stipendio deve essere maggiore di 0 euro");
        }

        Teacher teacher = Teacher.builder()
                .matricolaInsegnante(request.matricolaInsegnante())
                .cf(request.cf())
                .nome(request.nome())
                .cognome(request.cognome())
                .dataNascita(request.dataNascita())
                .telefono(request.telefono())
                .stipendio(request.stipendio())
                .specializzazione(request.specializzazione())
                .anniEsperienza(request.anniEsperienza())
                .build();

        Teacher saved = teacherRepository.save(teacher);

        return new TeacherResponse(
                saved.getId(),
                saved.getMatricolaInsegnante(),
                saved.getCf(),
                saved.getNome(),
                saved.getCognome(),
                saved.getNomeCompleto(),
                saved.getDataNascita(),
                saved.getTelefono(),
                saved.getStipendio(),
                saved.getSpecializzazione(),
                saved.getAnniEsperienza(),
                saved.getNumeroCorsiTenuti());
    }

    /**
     * TODO: Recuperare un insegnante per matricola.
     *
     * Requisiti:
     * - Cercare per matricolaInsegnante
     * - ResourceNotFoundException se non trovato
     * - Restituire la response con il numero di corsi tenuti
     */
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherByMatricola(String matricolaInsegnante) {
        Teacher t = teacherRepository.findByMatricolaInsegnante(matricolaInsegnante)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "L'insegnante con matricola '" + matricolaInsegnante + "' non esiste"));

        int numeroCorsi = courseRepository.findByTeacherId(t.getId()).size();

        return new TeacherResponse(
                t.getId(),
                t.getMatricolaInsegnante(),
                t.getCf(),
                t.getNome(),
                t.getCognome(),
                t.getNomeCompleto(),
                t.getDataNascita(),
                t.getTelefono(),
                t.getStipendio(),
                t.getSpecializzazione(),
                t.getAnniEsperienza(),
                numeroCorsi);
    }

    /**
     * TODO: Recuperare tutti gli insegnanti.
     */
    @Transactional(readOnly = true)
    public List<TeacherResponse> getAllTeachers() {
        List<TeacherResponse> result = new ArrayList<>();
        for (Teacher t : teacherRepository.findAll()) {
            result.add(new TeacherResponse(
                    t.getId(),
                    t.getMatricolaInsegnante(),
                    t.getCf(),
                    t.getNome(),
                    t.getCognome(),
                    t.getNomeCompleto(),
                    t.getDataNascita(),
                    t.getTelefono(),
                    t.getStipendio(),
                    t.getSpecializzazione(),
                    t.getAnniEsperienza(),
                    t.getNumeroCorsiTenuti()));
        }
        return result;
    }

    /**
     * TODO: Aggiornare un insegnante.
     *
     * Requisiti:
     * - Trovare per matricola (ResourceNotFoundException se non trovato)
     * - Aggiornare: nome, cognome, telefono, stipendio, specializzazione,
     * anniEsperienza
     * - NON aggiornare: matricolaInsegnante, cf, dataNascita
     * - Salvare e restituire la response
     */
    public TeacherResponse updateTeacher(String matricolaInsegnante, TeacherRequest request) {
        Teacher t = teacherRepository.findByMatricolaInsegnante(matricolaInsegnante)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "L'insegnante con matricola '" + matricolaInsegnante + "' non esiste"));

        t.setNome(request.nome());
        t.setCognome(request.cognome());
        t.setTelefono(request.telefono());
        t.setStipendio(request.stipendio());
        t.setSpecializzazione(request.specializzazione());
        t.setAnniEsperienza(request.anniEsperienza());

        Teacher saved = teacherRepository.save(t);
        return new TeacherResponse(
                saved.getId(), saved.getMatricolaInsegnante(), saved.getCf(), saved.getNome(), saved.getCognome(),
                saved.getNomeCompleto(), saved.getDataNascita(), saved.getTelefono(), saved.getStipendio(),
                saved.getSpecializzazione(), saved.getAnniEsperienza(), saved.getNumeroCorsiTenuti());
    }

    /**
     * TODO: Eliminare un insegnante per matricola.
     *
     * Requisiti:
     * - Trovare per matricola (ResourceNotFoundException se non trovato)
     * - Eliminare dal database
     */
    public void deleteTeacher(String matricolaInsegnante) {
        Teacher t = teacherRepository.findByMatricolaInsegnante(matricolaInsegnante)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "L'insegnante con matricola '" + matricolaInsegnante + "' non esiste"));

        teacherRepository.delete(t);
    }

    /**
     * TODO: Assegnare un corso a un insegnante.
     *
     * Requisiti:
     * - Trovare l'insegnante per matricola (ResourceNotFoundException se non
     * trovato)
     * - Trovare il corso per codice (ResourceNotFoundException se non trovato)
     * - Verificare che il corso non sia già assegnato a un insegnante
     * (BusinessRuleException)
     * - Impostare il teacher sul corso e salvare
     */
    public void assignCourse(String matricolaInsegnante, String codiceCorso) {
        Teacher t = teacherRepository.findByMatricolaInsegnante(matricolaInsegnante)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "L'insegnante con matricola '" + matricolaInsegnante + "' non esiste"));

        Course c = courseRepository.findByCodiceCorso(codiceCorso).orElseThrow(
                () -> new ResourceNotFoundException("Il corso con codice '" + codiceCorso + "' non esiste"));

        if (c.getTeacher() != null) {
            throw new BusinessRuleException("Il corso è già assegnato a un insegnante");
        }

        c.setTeacher(t);
        courseRepository.save(c);
    }
}
