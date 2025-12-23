package pe.edu.upc.noctuwell.services;

import pe.edu.upc.noctuwell.dtos.*;
import pe.edu.upc.noctuwell.entities.Specialist;
import java.util.List;

public interface SpecialistService {
    List<Specialist> listAll();
    Specialist findById(Long id);
    SpecialistDTO add(SpecialistDTO specialistpostDTO);
    SpecialistDTO edit(SpecialistDTO dto);
    void delete(Long id);
    List<SpecialistRatingDTO> getTopRatedSpecialists(int limit);
    List<SpecialistExperienceDTO> getSpecialistExperienceRanking();
    List<SpecialistMostDiagnosesDTO> listSpecialistsWithMostDiagnoses();
    List<SpecialistWithoutReviewsDTO> listSpecialistsWithoutReviews();
    Specialist getMyProfile(String username);
}
