package com.scuoladimusica.service;

import com.scuoladimusica.exception.BusinessRuleException;
import com.scuoladimusica.exception.DuplicateResourceException;
import com.scuoladimusica.exception.ResourceNotFoundException;
import com.scuoladimusica.model.dto.request.CourseRequest;
import com.scuoladimusica.model.dto.request.LessonRequest;
import com.scuoladimusica.model.dto.response.CourseResponse;
import com.scuoladimusica.model.dto.response.LessonResponse;
import com.scuoladimusica.model.entity.Course;
import com.scuoladimusica.model.entity.Livello;
import com.scuoladimusica.repository.CourseRepository;
import com.scuoladimusica.repository.InstrumentRepository;
import com.scuoladimusica.repository.LessonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import com.scuoladimusica.model.entity.Instrument;
import com.scuoladimusica.model.entity.Lesson;

@Service
@Transactional
public class CourseService {

        @Autowired
        private CourseRepository courseRepository;

        @Autowired
        private LessonRepository lessonRepository;

        @Autowired
        private InstrumentRepository instrumentRepository;

        /**
         * TODO: Creare un nuovo corso.
         *
         * Requisiti:
         * - Verificare unicità codice corso (DuplicateResourceException)
         * - Verificare che dataFine > dataInizio (BusinessRuleException)
         * - Se il livello nella request è null, usare PRINCIPIANTE come default
         * - Salvare e restituire la response
         *
         * Eccezioni da usare: DuplicateResourceException, BusinessRuleException
         */
        public CourseResponse createCourse(CourseRequest request) {
                if (courseRepository.existsByCodiceCorso(request.codiceCorso())) {
                        throw new DuplicateResourceException(
                                        "Corso con codice '" + request.codiceCorso() + "' già esistente");
                }

                if (!request.dataFine().isAfter(request.dataInizio())) {
                        throw new BusinessRuleException("La data di fine deve essere successiva alla data di inizio");
                }

                Livello livello = request.livello() != null ? request.livello() : Livello.PRINCIPIANTE;

                Course course = Course.builder()
                                .codiceCorso(request.codiceCorso())
                                .nome(request.nome())
                                .dataInizio(request.dataInizio())
                                .dataFine(request.dataFine())
                                .costoOrario(request.costoOrario())
                                .totaleOre(request.totaleOre())
                                .online(request.online())
                                .livello(livello)
                                .build();

                Course saved = courseRepository.save(course);

                return new CourseResponse(
                                saved.getId(),
                                saved.getCodiceCorso(),
                                saved.getNome(),
                                saved.getDataInizio(),
                                saved.getDataFine(),
                                saved.getCostoOrario(),
                                saved.getTotaleOre(),
                                saved.getCostoTotale(),
                                saved.getDurataGiorni(),
                                saved.isOnline(),
                                saved.getLivello(),
                                null,
                                0,
                                List.of());
        }

        /**
         * TODO: Recuperare un corso per codice.
         *
         * Requisiti:
         * - Cercare per codiceCorso
         * - ResourceNotFoundException se non trovato
         * - Includere nella response: costoTotale, durataGiorni, nome insegnante (se
         * assegnato),
         * numero iscritti, lista lezioni
         */
        @Transactional(readOnly = true)
        public CourseResponse getCourseByCode(String codiceCorso) {
                Course c = courseRepository.findByCodiceCorso(codiceCorso)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException("Il corso con codice '"
                                                                + codiceCorso + "' non esiste"));

                String nomeInsegnante = c.getTeacher() != null
                                ? c.getTeacher().getNome() + " " + c.getTeacher().getCognome()
                                : null;

                List<LessonResponse> lezioni = c.getLessons().stream()
                                .map(l -> new LessonResponse(l.getId(), l.getNumero(), l.getData(),
                                                l.getOraInizio(), l.getDurata(), l.getAula(), l.getArgomento()))
                                .toList();

                return new CourseResponse(
                                c.getId(), c.getCodiceCorso(), c.getNome(),
                                c.getDataInizio(), c.getDataFine(),
                                c.getCostoOrario(), c.getTotaleOre(),
                                c.getCostoTotale(), c.getDurataGiorni(),
                                c.isOnline(), c.getLivello(),
                                nomeInsegnante,
                                c.getEnrollments().size(),
                                lezioni);
        }

        /**
         * TODO: Recuperare tutti i corsi.
         */
        @Transactional(readOnly = true)
        public List<CourseResponse> getAllCourses() {
                List<CourseResponse> result = new ArrayList<>();
                for (Course c : courseRepository.findAll()) {
                        result.add(new CourseResponse(
                                        c.getId(),
                                        c.getCodiceCorso(),
                                        c.getNome(),
                                        c.getDataInizio(),
                                        c.getDataFine(),
                                        c.getCostoOrario(),
                                        c.getTotaleOre(),
                                        c.getCostoTotale(),
                                        c.getDurataGiorni(),
                                        c.isOnline(),
                                        c.getLivello(),
                                        null,
                                        0,
                                        List.of()));
                }
                return result;
        }

        /**
         * TODO: Recuperare solo i corsi online.
         */
        @Transactional(readOnly = true)
        public List<CourseResponse> getOnlineCourses() {
                List<CourseResponse> result = new ArrayList<>();
                for (Course c : courseRepository.findByOnlineTrue()) {
                        result.add(new CourseResponse(
                                        c.getId(),
                                        c.getCodiceCorso(),
                                        c.getNome(),
                                        c.getDataInizio(),
                                        c.getDataFine(),
                                        c.getCostoOrario(),
                                        c.getTotaleOre(),
                                        c.getCostoTotale(),
                                        c.getDurataGiorni(),
                                        c.isOnline(),
                                        c.getLivello(),
                                        null,
                                        0,
                                        List.of()));
                }
                return result;
        }

        /**
         * TODO: Aggiornare un corso.
         *
         * Requisiti:
         * - Trovare per codice (ResourceNotFoundException se non trovato)
         * - Aggiornare: nome, dataInizio, dataFine, costoOrario, totaleOre, online,
         * livello
         * - Verificare che dataFine > dataInizio dopo l'aggiornamento
         * (BusinessRuleException)
         * - NON aggiornare: codiceCorso
         * - Salvare e restituire la response
         */
        public CourseResponse updateCourse(String codiceCorso, CourseRequest request) {
                Course c = courseRepository.findByCodiceCorso(codiceCorso).orElseThrow(
                                () -> new ResourceNotFoundException(
                                                "Il corso con codice '" + codiceCorso + "' non esiste"));

                if (!request.dataFine().isAfter(request.dataInizio())) {
                        throw new BusinessRuleException(
                                        "La data di fine non può essere precedente o uguale alla data di inizio");
                }

                c.setNome(request.nome());
                c.setDataInizio(request.dataInizio());
                c.setDataFine(request.dataFine());
                c.setCostoOrario(request.costoOrario());
                c.setTotaleOre(request.totaleOre());
                c.setOnline(request.online());
                c.setLivello(request.livello());
                Course saved = courseRepository.save(c);
                return new CourseResponse(
                                saved.getId(), saved.getCodiceCorso(), saved.getNome(),
                                saved.getDataInizio(), saved.getDataFine(),
                                saved.getCostoOrario(), saved.getTotaleOre(),
                                saved.getCostoTotale(), saved.getDurataGiorni(),
                                saved.isOnline(), saved.getLivello(),
                                null, 0, List.of());

        }

        /**
         * TODO: Eliminare un corso per codice.
         *
         * Requisiti:
         * - Trovare per codice (ResourceNotFoundException se non trovato)
         * - Eliminare dal database
         */
        public void deleteCourse(String codiceCorso) {
                Course c = courseRepository.findByCodiceCorso(codiceCorso).orElseThrow(
                                () -> new ResourceNotFoundException(
                                                "Il corso con codice '" + codiceCorso + "' non esiste"));

                courseRepository.delete(c);
        }

        /**
         * TODO: Aggiungere una lezione a un corso.
         *
         * Requisiti:
         * - Trovare il corso per codice (ResourceNotFoundException se non trovato)
         * - Verificare che non esista già una lezione con lo stesso numero nel corso
         * (DuplicateResourceException)
         * - Creare la lezione, associarla al corso e salvarla
         * - Restituire la LessonResponse
         *
         * Eccezioni da usare: ResourceNotFoundException, DuplicateResourceException
         */
        public LessonResponse addLesson(String codiceCorso, LessonRequest request) {
                Course c = courseRepository.findByCodiceCorso(codiceCorso).orElseThrow(
                                () -> new ResourceNotFoundException(
                                                "Il corso con codice '" + codiceCorso + "' non esiste"));

                boolean numeroEsistente = c.getLessons().stream()
                                .anyMatch(lesson -> lesson.getNumero() == request.numero());
                if (numeroEsistente) {
                        throw new DuplicateResourceException("Numero lezione già presente per il corso indicato");
                }

                Lesson lesson = Lesson.builder()
                                .course(c)
                                .numero(request.numero())
                                .data(request.data())
                                .oraInizio(request.oraInizio())
                                .durata(request.durata())
                                .aula(request.aula())
                                .argomento(request.argomento())
                                .build();

                Lesson saved = lessonRepository.save(lesson);
                // Sincronizza anche la collezione bidirezionale in sessione
                // (evita cache/stale collection che causa falsi negativi nei controlli
                // duplicati).
                c.getLessons().add(saved);

                return new LessonResponse(
                                saved.getId(),
                                saved.getNumero(),
                                saved.getData(),
                                saved.getOraInizio(),
                                saved.getDurata(),
                                saved.getAula(),
                                saved.getArgomento());
        }

        /**
         * TODO: Aggiungere uno strumento a un corso.
         *
         * Requisiti:
         * - Trovare il corso per codice (ResourceNotFoundException se non trovato)
         * - Trovare lo strumento per codice (ResourceNotFoundException se non trovato)
         * - Verificare che lo strumento non sia già associato al corso
         * (DuplicateResourceException)
         * - Aggiungere lo strumento alla lista strumenti del corso e salvare
         *
         * Eccezioni da usare: ResourceNotFoundException, DuplicateResourceException
         */
        public void addInstrumentToCourse(String codiceCorso, String codiceStrumento) {
                Course c = courseRepository.findByCodiceCorso(codiceCorso).orElseThrow(
                                () -> new ResourceNotFoundException(
                                                "Il corso con codice '" + codiceCorso + "' non esiste"));

                Instrument i = instrumentRepository.findByCodiceStrumento(codiceStrumento).orElseThrow(
                                () -> new ResourceNotFoundException(
                                                "Lo strumento con codice '" + codiceStrumento + "' non esiste"));

                if (c.getInstruments().contains(i)) {
                        throw new DuplicateResourceException(
                                        "Lo strumento '" + codiceStrumento + "' è già associato al corso");
                }

                c.getInstruments().add(i);
                courseRepository.save(c);
        }
}
