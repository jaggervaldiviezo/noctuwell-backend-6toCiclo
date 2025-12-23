package pe.edu.upc.noctuwell.serviceimpl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upc.noctuwell.dtos.HistoryDTO;
import pe.edu.upc.noctuwell.entities.*;
import pe.edu.upc.noctuwell.repositories.*;
import pe.edu.upc.noctuwell.services.HistoryService;
import pe.edu.upc.noctuwell.services.PatientService;
import pe.edu.upc.noctuwell.services.TypeSpecialistService;

import java.util.List;

@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SpecialistRepository specialistRepository;

    @Autowired
    private TypeSpecialistService typeSpecialistService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public HistoryDTO add(HistoryDTO historyDTO, String username) {

        Patient patientFound = patientService.findById(historyDTO.getPatientId());
        if (patientFound == null) {
            throw new RuntimeException("Paciente no encontrado");
        }

        // HALLAR ESPECIALISTA LOGEADO
        Specialist specialist = specialistRepository.findByUserUsername(username);
        if (specialist == null) {
            throw new RuntimeException("El usuario logeado no es un especialista registrado");
        }

        // 3. Tipo de especialista (desde el especialista, NO desde el DTO)
        TypeSpecialist typeSpecialistFound = specialist.getTypeSpecialist();
        if (typeSpecialistFound == null) {
            throw new RuntimeException("El especialista no tiene tipo de especialidad configurado");
        }

        // 4. Validación: 1 historia por paciente + tipo
        historyRepository.findByPatientAndTypeSpecialist(patientFound, typeSpecialistFound)
                .ifPresent(h -> {
                    throw new RuntimeException(
                            "Ya existe una historia clínica para este paciente con el tipo de especialidad: "
                                    + typeSpecialistFound.getName() + ". Debe editar la historia existente.");
                });

        // 5. Crear entidad
        History history = new History(
                null,
                historyDTO.getBackground(),
                historyDTO.getAllergies(),
                historyDTO.getMedications(),
                patientFound,
                typeSpecialistFound
        );

        history = historyRepository.save(history);

        // 6. Devolver DTO actualizado
        historyDTO.setId(history.getId());
        historyDTO.setTypeSpecialistId(typeSpecialistFound.getId());

        return historyDTO;
    }


    @Override
    public History edit(History history) {
        History found = findById(history.getId());
        if (found == null) return null;

        if (history.getBackground() != null)
            found.setBackground(history.getBackground());
        if (history.getAllergies() != null)
            found.setAllergies(history.getAllergies());
        if (history.getMedications() != null)
            found.setMedications(history.getMedications());

        return historyRepository.save(found);
    }

    @Override
    public void delete(Long id) {
        History found = findById(id);
        if (found != null)
            historyRepository.deleteById(id);
    }

    @Override
    public History findById(Long id) {
        return historyRepository.findById(id).orElse(null);
    }

    @Override
    public List<History> listAll() {
        return historyRepository.findAll();
    }

    @Override
    public List<History> listByPatientUsername(String username) {
        Patient patient = patientRepository.findByUserUsername(username);
        if (patient == null) {
            throw new RuntimeException("El usuario logueado no es un paciente registrado.");
        }
        return historyRepository.findByPatient(patient);
    }

    @Override
    public List<History> listBySpecialistUsername(String username) {
        Specialist specialist = specialistRepository.findByUserUsername(username);
        if (specialist == null) {
            throw new RuntimeException("El usuario logueado no es un especialista registrado.");
        }

        // 1. Traemos todas las historias de sus pacientes
        List<History> historias = historyRepository.findBySpecialistPatients(specialist);

        // 2. Obtenemos el tipo de especialidad del especialista logueado
        TypeSpecialist miTipo = specialist.getTypeSpecialist();
        if (miTipo == null) {
            return List.of(); // o lanzar error si quieres obligar a tener tipo
        }

        // 3. Filtramos solo las historias que coincidan con ese tipo
        return historias.stream()
                .filter(h -> h.getTypeSpecialist() != null
                        && h.getTypeSpecialist().getId().equals(miTipo.getId()))
                .toList(); // si usas Java 8: .collect(Collectors.toList())
    }

    @Override
    public List<History> getHistoriesByPatientForLoggedSpecialist(Long patientId) {
        if (patientId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "patientId no puede ser null");
        }

        // 1) Obtener usuario autenticado desde SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        String username = auth.getName();

        // 2) Obtener la entidad User por username (tu repo devuelve User directo)
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado en base de datos");
        }

        Long userId = user.getId();

        // 3) Obtener especialista por userId para extraer typeSpecialistId
        Specialist specialist = specialistRepository.findByUserId(userId).orElse(null);

        // 4) Filtrar historias: primero por paciente, luego por tipo si existe
        if (specialist != null && specialist.getTypeSpecialistId() != null && specialist.getTypeSpecialistId() > 0) {
            Long typeSpecialistId = specialist.getTypeSpecialistId();
            return historyRepository.findByPatientIdAndTypeSpecialistId(patientId, typeSpecialistId);
        }

        // Fallback: devolver todas las historias del paciente
        return historyRepository.findByPatientId(patientId);
    }
}