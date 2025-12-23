package pe.edu.upc.noctuwell.services;

import pe.edu.upc.noctuwell.dtos.PatientDTO;
import pe.edu.upc.noctuwell.dtos.ReviewCreateDTO;
import pe.edu.upc.noctuwell.dtos.ReviewDTO;
import pe.edu.upc.noctuwell.entities.Review;

import java.util.List;

public interface ReviewService {
    ReviewDTO add(ReviewDTO reviewDTO);
    ReviewDTO edit(ReviewDTO dto);
    void delete(Long id);
    Review findById(Long id);
    List<Review> listAll();
    ReviewDTO createReviewForPatient(ReviewCreateDTO reviewCreateDTO);
}
