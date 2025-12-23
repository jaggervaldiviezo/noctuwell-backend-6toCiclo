package pe.edu.upc.noctuwell.services;

import pe.edu.upc.noctuwell.dtos.PatientDTO;
import pe.edu.upc.noctuwell.dtos.PatientDiagnosesDTO;
import pe.edu.upc.noctuwell.dtos.PatientWithoutAppointmentsDTO;
import pe.edu.upc.noctuwell.entities.Patient;
import java.util.List;

public interface PatientService {
    List<Patient> listAll();
    Patient findById(Long id);
    PatientDTO add(PatientDTO patientpostDTO);
    PatientDTO edit(PatientDTO dto);
    void delete(Long id);
    List<PatientDiagnosesDTO> getTopPatientsByDiagnoses(int limit);
    List<PatientWithoutAppointmentsDTO> listPatientsWithoutAppointments();
    List<Patient> listMyPatients(String username);
    public Patient getPatientByUserId(Long userId);
}
