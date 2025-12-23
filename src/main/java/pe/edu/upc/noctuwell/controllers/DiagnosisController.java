package pe.edu.upc.noctuwell.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.noctuwell.dtos.DTOUser;
import pe.edu.upc.noctuwell.dtos.DiagnosisDTO;
import pe.edu.upc.noctuwell.entities.*;
import pe.edu.upc.noctuwell.services.*;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/noctuwell")
public class DiagnosisController {

    @Autowired
    private DiagnosisService diagnosisService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private SpecialistService specialistService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private PatientService patientService;

    @GetMapping("/diagnoses")
    public ResponseEntity<List<Diagnosis>> listAll() {
        return new ResponseEntity<>(diagnosisService.listAll(), HttpStatus.OK);
    }

    @GetMapping("/diagnoses/{id}")
    public ResponseEntity<Diagnosis> findById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(diagnosisService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/diagnoses")
    public ResponseEntity<DiagnosisDTO> add(@RequestBody DiagnosisDTO diagnosisDTO) {
        diagnosisDTO = diagnosisService.add(diagnosisDTO);
        return new ResponseEntity<>(diagnosisDTO, HttpStatus.CREATED);
    }

    @PutMapping("/diagnoses")
    public ResponseEntity<Diagnosis> edit(@RequestBody Diagnosis diagnosis) {
        Diagnosis edited = diagnosisService.edit(diagnosis);
        return new ResponseEntity<>(edited, HttpStatus.OK);
    }

    @DeleteMapping("/diagnoses/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        diagnosisService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/diagnoses/my")
    public ResponseEntity<List<DiagnosisDTO>> listMyDiagnoses(
            @AuthenticationPrincipal UserDetails userDetails) {

        // Obtener el nombre de usuario del usuario autenticado
        String username = userDetails.getUsername();

        // Obtener los diagnósticos asociados al paciente utilizando el username
        List<DiagnosisDTO> diagnosisDTOs = diagnosisService.listByPatientUsername(username).stream()
                .map(diagnosis -> new DiagnosisDTO(
                        diagnosis.getId(),
                        diagnosis.getDescription(),
                        diagnosis.getType(),
                        diagnosis.getRecommendations(),
                        diagnosis.getDate(),
                        diagnosis.getAppointment().getId(),
                        diagnosis.getHistory().getId(),
                        diagnosis.getSpecialist().getId(),
                        diagnosis.getPatient().getId()
                ))
                .collect(Collectors.toList());

        return new ResponseEntity<>(diagnosisDTOs, HttpStatus.OK);
    }

    @GetMapping("/diagnoses/my-patients")
    public ResponseEntity<List<DiagnosisDTO>> listDiagnosesOfMyPatients(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<DiagnosisDTO> diagnosisDTOs = diagnosisService.listBySpecialistUsername(userDetails.getUsername()).stream()
                .map(diagnosis -> new DiagnosisDTO(
                        diagnosis.getId(),
                        diagnosis.getDescription(),
                        diagnosis.getType(),
                        diagnosis.getRecommendations(),
                        diagnosis.getDate(),
                        diagnosis.getAppointment().getId(),
                        diagnosis.getHistory().getId(),
                        diagnosis.getSpecialist().getId(),
                        diagnosis.getPatient().getId()
                ))
                .collect(Collectors.toList());

        return new ResponseEntity<>(diagnosisDTOs, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Diagnosis> editDTO(@PathVariable Long id, @RequestBody DiagnosisDTO diagnosisDTO) {
        // Buscar entidades por ID
        Appointment appointment = appointmentService.findById(diagnosisDTO.getAppointmentId());
        Specialist specialist = specialistService.findById(diagnosisDTO.getSpecialistId());
        History history = historyService.findById(diagnosisDTO.getHistoryId());
        Patient patient = patientService.findById(diagnosisDTO.getPatientId());

        if (appointment == null || specialist == null || history == null || patient == null) {
            return ResponseEntity.badRequest().build();
        }

        // Crear entidad Diagnosis con las relaciones correctas
        Diagnosis diagnosis = new Diagnosis(
                id,
                diagnosisDTO.getDescription(),
                diagnosisDTO.getType(),
                diagnosisDTO.getRecommendations(),
                diagnosisDTO.getDate(),
                appointment,
                history,
                specialist,
                patient
        );

        Diagnosis updated = diagnosisService.edit(diagnosis);
        return ResponseEntity.ok(updated);
    }
}