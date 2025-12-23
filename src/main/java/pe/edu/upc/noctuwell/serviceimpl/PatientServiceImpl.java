package pe.edu.upc.noctuwell.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upc.noctuwell.dtos.PatientDTO;
import pe.edu.upc.noctuwell.dtos.PatientDiagnosesDTO;
import pe.edu.upc.noctuwell.dtos.PatientWithoutAppointmentsDTO;
import pe.edu.upc.noctuwell.entities.Patient;
import pe.edu.upc.noctuwell.entities.Plan;
import pe.edu.upc.noctuwell.entities.Specialist;
import pe.edu.upc.noctuwell.entities.User;
import pe.edu.upc.noctuwell.repositories.PatientRepository;
import pe.edu.upc.noctuwell.repositories.SpecialistRepository;
import pe.edu.upc.noctuwell.services.PatientService;
import pe.edu.upc.noctuwell.services.PlanService;
import pe.edu.upc.noctuwell.services.UserService;

import java.time.LocalDate;
import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    public UserService userService;

    @Autowired
    private PlanService planService;

    @Autowired
    private SpecialistRepository  specialistRepository;

    @Override
    public List<Patient> listAll() {
        return patientRepository.findAll();
    }

    @Override
    public Patient findById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    private void validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            return; // si decides que sea opcional, puedes quitar esta línea
        }

        // Validación: la fecha de nacimiento no puede ser futura
        if (birthDate.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de nacimiento no puede ser una fecha futura"
            );
        }

        // Validación: el paciente debe ser mayor de 18 años
        if (birthDate.isAfter(LocalDate.now().minusYears(18))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El paciente debe ser mayor de 18 años"
            );
        }
    }

    @Override
    public PatientDTO add(PatientDTO patientpostDTO) {

        User userFound = userService.findById(patientpostDTO.getUserId());
        Plan planFound = planService.findById(patientpostDTO.getPlanId());

        if (userFound == null) {
            System.out.println("ID de usuario no encontrado");
            return null;
        }
        if (patientpostDTO.getFirstName() == null || patientpostDTO.getFirstName().isBlank()) {
            System.out.println("Ingrese el nombre del paciente");
            return null;
        }
        if (patientpostDTO.getPhone() == null || patientpostDTO.getPhone().isBlank()) {
            System.out.println("Ingrese el numero de telefono del paciente");
            return null;
        }

        // Validaciones
        validateBirthDate(patientpostDTO.getBirthDate()); // Validación de fecha y mayoría de edad

        Patient patient = new Patient(
                null,
                patientpostDTO.getFirstName(),
                patientpostDTO.getLastName(),
                patientpostDTO.getGender(),
                patientpostDTO.getWeight(),
                patientpostDTO.getHeight(),
                patientpostDTO.getPhone(),
                patientpostDTO.getBirthDate(),
                userFound,
                planFound,
                null,
                null,
                null,
                null
        );

        patient = patientRepository.save(patient);
        patientpostDTO.setId(patient.getId());
        return patientpostDTO;
    }


    @Override
    public PatientDTO edit(PatientDTO dto) {

        if (dto.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient id is required");
        }

        Patient patient = patientRepository.findById(dto.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found")
                );

        User user = userService.findById(dto.getUserId());
        Plan plan = planService.findById(dto.getPlanId());

        // Validaciones
        validateBirthDate(dto.getBirthDate()); // Validación de fecha y mayoría de edad

        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setGender(dto.getGender());
        patient.setWeight(dto.getWeight());
        patient.setHeight(dto.getHeight());
        patient.setPhone(dto.getPhone());
        patient.setBirthDate(dto.getBirthDate());
        patient.setUser(user);
        patient.setPlan(plan);

        patientRepository.save(patient);

        return dto;
    }



    @Override
    public void delete(Long id) {
        patientRepository.deleteById(id);
    }

    @Override
    public List<PatientDiagnosesDTO> getTopPatientsByDiagnoses(int limit) {
        List<PatientDiagnosesDTO> list = patientRepository.findPatientsByDiagnoses();
        if (limit >= list.size()) return list;
        return list.subList(0, limit);
    }

    @Override
    public List<PatientWithoutAppointmentsDTO> listPatientsWithoutAppointments() {
        return patientRepository.findPatientsWithoutAppointments();
    }

    @Override
    public List<Patient> listMyPatients(String username) {
        Specialist specialist = specialistRepository.findByUserUsername(username);
        if (specialist == null) {
            throw new RuntimeException("El usuario logueado no es un especialista registrado");
        }

        return patientRepository.findDistinctBySpecialist(specialist);
    }

    @Override
    public Patient getPatientByUserId(Long userId) {
        return patientRepository.findByUser_Id(userId);
    }
}
