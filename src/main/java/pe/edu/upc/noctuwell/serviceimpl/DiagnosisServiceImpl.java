package pe.edu.upc.noctuwell.serviceimpl;

import org.aspectj.weaver.patterns.HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.noctuwell.dtos.DiagnosisDTO;
import pe.edu.upc.noctuwell.entities.*;
import pe.edu.upc.noctuwell.repositories.DiagnosisRepository;
import pe.edu.upc.noctuwell.repositories.HistoryRepository;
import pe.edu.upc.noctuwell.repositories.PatientRepository;
import pe.edu.upc.noctuwell.services.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiagnosisServiceImpl implements DiagnosisService {

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private SpecialistService specialistService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private PatientRepository  patientRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private PatientService patientService;

    @Override
    public DiagnosisDTO add(DiagnosisDTO diagnosisDTO) {
        // Buscar Appointment, Specialist, History y Patient por sus IDs
        Appointment appointmentFound = appointmentService.findById(diagnosisDTO.getAppointmentId());
        Specialist specialistFound = specialistService.findById(diagnosisDTO.getSpecialistId());
        History historyFound = historyService.findById(diagnosisDTO.getHistoryId());
        // FIX: usar diagnosisDTO.getPatientId() en lugar de getSpecialistId()
        Patient patientFound = patientService.findById(diagnosisDTO.getPatientId());

        // Verificar que Appointment, Specialist, History y Patient no sean nulos
        if (appointmentFound == null || specialistFound == null || historyFound == null || patientFound == null) {
            return null;  // Si alguno es nulo, devolvemos null
        }

        // Crear un nuevo objeto Diagnosis
        Diagnosis diagnosis = new Diagnosis(
                null,  // ID se genera automáticamente
                diagnosisDTO.getDescription(),
                diagnosisDTO.getType(),
                diagnosisDTO.getRecommendations(),
                diagnosisDTO.getDate(),
                appointmentFound,  // Establecer Appointment
                historyFound,      // Establecer History
                specialistFound,   // Establecer Specialist
                patientFound       // Establecer Patient (CORREGIDO)
        );

        // Guardar el diagnóstico en la base de datos
        diagnosis = diagnosisRepository.save(diagnosis);

        // Establecer el ID en el DTO
        diagnosisDTO.setId(diagnosis.getId());

        // Devolver el DTO con el ID generado
        return diagnosisDTO;
    }


    @Override
    public Diagnosis edit(Diagnosis diagnosis) {
        Diagnosis found = findById(diagnosis.getId());
        if (found == null) return null;

        if (diagnosis.getDescription() != null)
            found.setDescription(diagnosis.getDescription());
        if (diagnosis.getType() != null)
            found.setType(diagnosis.getType());
        if (diagnosis.getRecommendations() != null)
            found.setRecommendations(diagnosis.getRecommendations());
        if (diagnosis.getDate() != null)
            found.setDate(diagnosis.getDate());
        if (diagnosis.getSpecialist() != null)
            found.setSpecialist(diagnosis.getSpecialist());
        if (diagnosis.getPatient() != null)
            found.setPatient(diagnosis.getPatient());
        if (diagnosis.getAppointment() != null)
            found.setAppointment(diagnosis.getAppointment());
        if (diagnosis.getHistory() != null)
            found.setHistory(diagnosis.getHistory());

        return diagnosisRepository.save(found);
    }

    @Override
    public void delete(Long id) {
        Diagnosis found = findById(id);
        if (found != null)
            diagnosisRepository.deleteById(id);
    }

    @Override
    public Diagnosis findById(Long id) {
        return diagnosisRepository.findById(id).orElse(null);
    }

    @Override
    public List<Diagnosis> listAll() {
        return diagnosisRepository.findAll();
    }

    @Override
    public List<Diagnosis> listByPatientUsername(String username) {
        // Obtener el paciente usando el username
        Patient patient = patientRepository.findByUserUsername(username);

        if (patient == null) {
            throw new RuntimeException("Paciente no encontrado para el usuario: " + username);
        }

        // Obtener las historias del paciente
        List<History> histories = historyRepository.findByPatientId(patient.getId());

        // Obtener los diagnósticos asociados a esas historias
        List<Diagnosis> diagnoses = diagnosisRepository.findByHistoryIn(histories);

        return diagnoses;
    }

    @Override
    public List<Diagnosis> listBySpecialistUsername(String username) {
        return diagnosisRepository.findBySpecialistUserUsername(username);
    }



}

