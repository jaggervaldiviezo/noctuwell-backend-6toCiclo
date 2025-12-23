package pe.edu.upc.noctuwell.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.noctuwell.dtos.PatientDTO;
import pe.edu.upc.noctuwell.dtos.PatientDiagnosesDTO;
import pe.edu.upc.noctuwell.dtos.PatientWithoutAppointmentsDTO;
import pe.edu.upc.noctuwell.entities.Patient;
import pe.edu.upc.noctuwell.services.PatientService;

import java.util.List;

@RestController
@RequestMapping("/noctuwell/patients")
@CrossOrigin("*")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping
    public ResponseEntity<List<Patient>> listAll() {
        return new ResponseEntity<>(patientService.listAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> findById(@PathVariable Long id) {
        Patient patient = patientService.findById(id);
        return (patient != null)
                ? new ResponseEntity<>(patient, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<PatientDTO> add(@RequestBody PatientDTO patientpostDTO) {
        patientpostDTO = patientService.add(patientpostDTO);
        return new ResponseEntity<>(patientpostDTO, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<PatientDTO> edit(@RequestBody PatientDTO dto) {
        PatientDTO edited = patientService.edit(dto);
        return new ResponseEntity<>(edited, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/top-diagnoses")
    public ResponseEntity<List<PatientDiagnosesDTO>> getTopPatientsByDiagnoses(
            @RequestParam(defaultValue = "3") int limit) {

        return ResponseEntity.ok(patientService.getTopPatientsByDiagnoses(limit));
    }

    @GetMapping("/without-appointments")
    public ResponseEntity<List<PatientWithoutAppointmentsDTO>> getPatientsWithoutAppointments() {
        List<PatientWithoutAppointmentsDTO> list = patientService.listPatientsWithoutAppointments();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/my")
    public List<Patient> listMyPatients(@AuthenticationPrincipal UserDetails userDetails) {
        return patientService.listMyPatients(userDetails.getUsername());
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<Patient> getPatientByUserId(@PathVariable Long userId) {
        Patient patient = patientService.getPatientByUserId(userId);
        if (patient == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(patient);
    }
}


