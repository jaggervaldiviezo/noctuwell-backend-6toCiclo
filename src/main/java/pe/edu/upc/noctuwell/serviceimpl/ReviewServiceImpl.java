package pe.edu.upc.noctuwell.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.noctuwell.dtos.ReviewCreateDTO;
import pe.edu.upc.noctuwell.dtos.ReviewDTO;
import pe.edu.upc.noctuwell.entities.Patient;
import pe.edu.upc.noctuwell.entities.Review;
import pe.edu.upc.noctuwell.entities.Specialist;
import pe.edu.upc.noctuwell.repositories.PatientRepository;
import pe.edu.upc.noctuwell.repositories.ReviewRepository;
import pe.edu.upc.noctuwell.repositories.SpecialistRepository;
import pe.edu.upc.noctuwell.services.PatientService;
import pe.edu.upc.noctuwell.services.ReviewService;
import pe.edu.upc.noctuwell.services.SpecialistService;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    PatientService patientService;

    @Autowired
    SpecialistService specialistService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SpecialistRepository specialistRepository;

    @Override
    public ReviewDTO add(ReviewDTO reviewDTO) {
        Specialist specialistFound = specialistService.findById(reviewDTO.getSpecialistId());
        Patient patientFound = patientService.findById(reviewDTO.getPatientId());
        if(specialistFound == null || patientFound == null){
            System.out.println("Specialist or Patient not found");
            return null;
        }
        Review review = new Review(null,
                reviewDTO.getComment(),
                reviewDTO.getFecha(),
                reviewDTO.getCalificacion(),
                patientFound,
                specialistFound
        );
        review = reviewRepository.save(review);
        reviewDTO.setId(review.getId());
        return reviewDTO;
    }

    @Override
    public ReviewDTO edit(ReviewDTO dto) {

        Review review = reviewRepository.findById(dto.getId()).orElse(null);
        if (review == null) {
            return null;
        }

        Patient patient = patientService.findById(dto.getPatientId());
        Specialist specialist = specialistService.findById(dto.getSpecialistId());

        review.setComment(dto.getComment());
        review.setFecha(dto.getFecha());
        review.setCalificacion(dto.getCalificacion());
        review.setPatient(patient);
        review.setSpecialist(specialist);

        reviewRepository.save(review);

        return dto;
    }

    @Override
    public void delete(Long id) {
        Review found= findById(id);
        if(found != null){
            reviewRepository.deleteById(id);
        }
    }

    @Override
    public Review findById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @Override
    public List<Review> listAll() {
        return reviewRepository.findAll();
    }

    @Override
    public ReviewDTO createReviewForPatient(ReviewCreateDTO reviewCreateDTO) {
        // Obtener el paciente (puedes obtenerlo del contexto de sesión si es necesario)
        Patient patient = patientRepository.findById(1L).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Obtener el especialista
        Specialist specialist = specialistRepository.findById(reviewCreateDTO.getSpecialistId())
                .orElseThrow(() -> new RuntimeException("Especialista no encontrado"));

        // Crear la reseña
        Review review = new Review();
        review.setComment(reviewCreateDTO.getComment());
        review.setCalificacion(reviewCreateDTO.getCalificacion());

        // Asegúrate de convertir la fecha actual (si es necesaria) de LocalDateTime a LocalDate
        review.setFecha(LocalDate.now()); // Cambiar la fecha para que sea de tipo LocalDate

        review.setPatient(patient);
        review.setSpecialist(specialist);

        // Guardar la reseña en la base de datos
        review = reviewRepository.save(review);

        // Convertir a DTO para devolverlo
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setId(review.getId());
        reviewDTO.setComment(review.getComment());
        reviewDTO.setFecha(review.getFecha());  // Asegúrate de que esté usando LocalDate
        reviewDTO.setCalificacion(review.getCalificacion());
        reviewDTO.setPatientId(review.getPatient().getId());
        reviewDTO.setSpecialistId(review.getSpecialist().getId());

        return reviewDTO;
    }
}
