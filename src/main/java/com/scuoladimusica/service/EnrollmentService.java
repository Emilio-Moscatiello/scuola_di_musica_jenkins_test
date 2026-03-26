package com.scuoladimusica.service;

import com.scuoladimusica.exception.BusinessRuleException;
import com.scuoladimusica.exception.DuplicateResourceException;
import com.scuoladimusica.exception.ResourceNotFoundException;
import com.scuoladimusica.model.dto.response.EnrollmentResponse;
import com.scuoladimusica.model.entity.Course;
import com.scuoladimusica.model.entity.Enrollment;
import com.scuoladimusica.model.entity.Student;
import com.scuoladimusica.repository.CourseRepository;
import com.scuoladimusica.repository.EnrollmentRepository;
import com.scuoladimusica.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EnrollmentService {

        @Autowired
        private EnrollmentRepository enrollmentRepository;

        @Autowired
        private StudentRepository studentRepository;

        @Autowired
        private CourseRepository courseRepository;

        /**
         * TODO: Iscrivere uno studente a un corso.
         *
         * Requisiti:
         * - Trovare lo studente per matricola (ResourceNotFoundException se non
         * trovato)
         * - Trovare il corso per codice (ResourceNotFoundException se non trovato)
         * - Verificare che lo studente non sia già iscritto allo stesso corso
         * (DuplicateResourceException)
         * - Creare l'Enrollment con studente, corso e anno di iscrizione
         * - Salvare e restituire la EnrollmentResponse
         *
         * Eccezioni da usare: ResourceNotFoundException, DuplicateResourceException
         */
        public EnrollmentResponse enrollStudent(String matricola, String codiceCorso, int annoIscrizione) {
                Student s = studentRepository.findByMatricola(matricola).orElseThrow(
                                () -> new ResourceNotFoundException(
                                                "Lo studente con matricola '" + matricola + "' non esiste"));
                Course c = courseRepository.findByCodiceCorso(codiceCorso).orElseThrow(
                                () -> new ResourceNotFoundException(
                                                "Il corso con codice '" + codiceCorso + "' non esiste"));

                if (enrollmentRepository.existsByStudentIdAndCourseId(s.getId(), c.getId())) {
                        throw new DuplicateResourceException(
                                        "Lo studente con matricola '" + matricola + "' risulta già iscritto al corso");
                }

                Enrollment enrollment = Enrollment.builder()
                                .student(s)
                                .course(c)
                                .annoIscrizione(annoIscrizione)
                                .build();

                Enrollment saved = enrollmentRepository.save(enrollment);

                return new EnrollmentResponse(
                                saved.getId(),
                                saved.getStudent().getMatricola(),
                                saved.getStudent().getNome() + " " + saved.getStudent().getCognome(),
                                saved.getCourse().getCodiceCorso(),
                                saved.getCourse().getNome(),
                                saved.getAnnoIscrizione(),
                                saved.getVotoFinale());
        }

        /**
         * TODO: Registrare un voto per un'iscrizione.
         *
         * Requisiti:
         * - Trovare l'iscrizione tramite matricola e codice corso
         * (ResourceNotFoundException se non trovata - "Iscrizione non trovata")
         * - Verificare che il voto sia tra 18 e 30 (BusinessRuleException)
         * - Impostare il votoFinale sull'iscrizione e salvare
         * - Restituire la EnrollmentResponse aggiornata
         *
         * Eccezioni da usare: ResourceNotFoundException, BusinessRuleException
         */
        public EnrollmentResponse registerVote(String matricola, String codiceCorso, int voto) {
                Enrollment enrollment = enrollmentRepository
                                .findByStudentMatricolaAndCourseCodiceCorso(matricola, codiceCorso)
                                .orElseThrow(() -> new ResourceNotFoundException("Iscrizione non trovata"));

                if (voto < 18 || voto > 30) {
                        throw new BusinessRuleException("Il voto deve essere compreso tra 18 e 30");
                }

                enrollment.setVotoFinale(voto);
                Enrollment saved = enrollmentRepository.save(enrollment);

                return new EnrollmentResponse(
                                saved.getId(),
                                saved.getStudent().getMatricola(),
                                saved.getStudent().getNome() + " " + saved.getStudent().getCognome(),
                                saved.getCourse().getCodiceCorso(),
                                saved.getCourse().getNome(),
                                saved.getAnnoIscrizione(),
                                saved.getVotoFinale());
        }

        /**
         * TODO: Recuperare tutte le iscrizioni di uno studente.
         *
         * Requisiti:
         * - Trovare lo studente per matricola (ResourceNotFoundException se non
         * trovato)
         * - Restituire la lista di EnrollmentResponse
         */
        @Transactional(readOnly = true)
        public List<EnrollmentResponse> getEnrollmentsByStudent(String matricola) {
                Student s = studentRepository.findByMatricola(matricola).orElseThrow(
                                () -> new ResourceNotFoundException(
                                                "Lo studente con matricola '" + matricola + "' non esiste"));

                return enrollmentRepository.findByStudentId(s.getId()).stream()
                                .map(e -> new EnrollmentResponse(
                                                e.getId(),
                                                e.getStudent().getMatricola(),
                                                e.getStudent().getNome() + " " + e.getStudent().getCognome(),
                                                e.getCourse().getCodiceCorso(),
                                                e.getCourse().getNome(),
                                                e.getAnnoIscrizione(),
                                                e.getVotoFinale()))
                                .toList();
        }

        /**
         * TODO: Recuperare tutte le iscrizioni per un corso.
         *
         * Requisiti:
         * - Trovare il corso per codice (ResourceNotFoundException se non trovato)
         * - Restituire la lista di EnrollmentResponse
         */
        @Transactional(readOnly = true)
        public List<EnrollmentResponse> getEnrollmentsByCourse(String codiceCorso) {
                Course c = courseRepository.findByCodiceCorso(codiceCorso).orElseThrow(
                                () -> new ResourceNotFoundException(
                                                "Il corso con codice '" + codiceCorso + "' non esiste"));

                return enrollmentRepository.findByCourseId(c.getId()).stream()
                                .map(e -> new EnrollmentResponse(
                                                e.getId(),
                                                e.getStudent().getMatricola(),
                                                e.getStudent().getNome() + " " + e.getStudent().getCognome(),
                                                e.getCourse().getCodiceCorso(),
                                                e.getCourse().getNome(),
                                                e.getAnnoIscrizione(),
                                                e.getVotoFinale()))
                                .toList();
        }
}
