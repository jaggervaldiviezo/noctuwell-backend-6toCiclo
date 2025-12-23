package pe.edu.upc.noctuwell.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.noctuwell.dtos.*;
import pe.edu.upc.noctuwell.entities.Specialist;
import pe.edu.upc.noctuwell.entities.TypeSpecialist;
import pe.edu.upc.noctuwell.entities.User;
import pe.edu.upc.noctuwell.repositories.SpecialistRepository;
import pe.edu.upc.noctuwell.services.SpecialistService;
import pe.edu.upc.noctuwell.services.TypeSpecialistService;
import pe.edu.upc.noctuwell.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecialistServiceImpl implements SpecialistService {

    @Autowired
    private SpecialistRepository specialistRepository;
    @Autowired
    public UserService userService;
    @Autowired
    public TypeSpecialistService typeSpecialistService;
    @Override
    public List<Specialist> listAll() {
        return specialistRepository.findAll();
    }

    @Override
    public Specialist findById(Long id) {
        return specialistRepository.findById(id).orElse(null);
    }

    @Override
    public SpecialistDTO add(SpecialistDTO specialistpostDTO) {
        User userFound = userService.findById(specialistpostDTO.getUserId());
        TypeSpecialist typeSpecialistFound = typeSpecialistService.findById(specialistpostDTO.getTypeSpecialistId());
        if (userFound == null) {
            System.out.println("ID de usuario no encontrado");
            return null;
        }
        if (specialistpostDTO.getFirstName() == null || specialistpostDTO.getFirstName().isBlank()) {
            return null;
        }
        Specialist specialist = new Specialist(null,
                specialistpostDTO.getFirstName(),
                specialistpostDTO.getLastName(),
                specialistpostDTO.getPhone(),
                specialistpostDTO.getCertification(),
                specialistpostDTO.getDescription(),
                specialistpostDTO.getExperience(),
                userFound,
                typeSpecialistFound,
                null,
                null,
                null,
                null,
                null);
        specialist=specialistRepository.save(specialist);

        specialistpostDTO.setId(specialist.getId());
        return specialistpostDTO;
    }


    @Override
    public SpecialistDTO edit(SpecialistDTO dto) {

        Specialist specialist = specialistRepository.findById(dto.getId()).orElse(null);
        if (specialist == null) {
            return null;
        }

        User user = userService.findById(dto.getUserId());
        TypeSpecialist typeSpecialist = typeSpecialistService.findById(dto.getTypeSpecialistId());

        specialist.setFirstName(dto.getFirstName());
        specialist.setLastName(dto.getLastName());
        specialist.setPhone(dto.getPhone());
        specialist.setCertification(dto.getCertification());
        specialist.setDescription(dto.getDescription());
        specialist.setExperience(dto.getExperience());
        specialist.setUser(user);
        specialist.setTypeSpecialist(typeSpecialist);

        specialistRepository.save(specialist);

        return dto;
    }

    @Override
    public void delete(Long id) {
        specialistRepository.deleteById(id);
    }

    @Override
    public List<SpecialistRatingDTO> getTopRatedSpecialists(int limit) {
        List<Object[]> rawData = specialistRepository.findTopRatedSpecialistsRaw(limit);

        return rawData.stream()
                .map(record -> new SpecialistRatingDTO(
                        // Concatenamos Nombre + Apellido para el gráfico
                        (String) record[0] + " " + (String) record[1],
                        // Convertimos el promedio a Double
                        ((Number) record[2]).doubleValue()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialistExperienceDTO> getSpecialistExperienceRanking() {
        return specialistRepository.findSpecialistExperienceRanking();
    }


    @Override
    public List<SpecialistMostDiagnosesDTO> listSpecialistsWithMostDiagnoses() {
        return specialistRepository.findSpecialistsOrderByDiagnosesDesc();
    }

    @Override
    public List<SpecialistWithoutReviewsDTO> listSpecialistsWithoutReviews() {
        return specialistRepository.findSpecialistsWithoutReviews();
    }

    @Override
    public Specialist getMyProfile(String username) {
        Specialist specialist = specialistRepository.findByUserUsername(username);
        if (specialist == null) {
            throw new RuntimeException("El usuario logueado no es un especialista registrado");
        }
        return specialist;
    }
}
