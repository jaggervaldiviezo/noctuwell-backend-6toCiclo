package pe.edu.upc.noctuwell.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.noctuwell.dtos.SpecialistExperienceDTO;
import pe.edu.upc.noctuwell.dtos.SpecialistMostDiagnosesDTO;
import pe.edu.upc.noctuwell.dtos.SpecialistWithoutReviewsDTO;
import pe.edu.upc.noctuwell.entities.Specialist;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
    @Query(value = """
        SELECT 
            s.first_name, 
            s.last_name, 
            AVG(r.calificacion) as promedio
        FROM specialists s
        JOIN reviews r ON s.id = r.specialist_id
        GROUP BY s.id, s.first_name, s.last_name
        ORDER BY promedio DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTopRatedSpecialistsRaw(@Param("limit") int limit);

// En SpecialistRepository:

    @Query("SELECT new pe.edu.upc.noctuwell.dtos.SpecialistExperienceDTO(s.firstName || ' ' || s.lastName, s.experience) " +
            "FROM Specialist s " +
            "ORDER BY s.experience DESC")
    List<SpecialistExperienceDTO> findSpecialistExperienceRanking();

    @Query("""
           SELECT new pe.edu.upc.noctuwell.dtos.SpecialistMostDiagnosesDTO(
               s.id,
               s.firstName,
               s.lastName,
               s.phone,
               s.certification,
               s.description,
               s.experience,
               COUNT(d)
           )
           FROM Specialist s
           JOIN s.diagnoses d
           GROUP BY s.id, s.firstName, s.lastName,
                    s.phone, s.certification, s.description, s.experience
           ORDER BY COUNT(d) DESC
           """)
    List<SpecialistMostDiagnosesDTO> findSpecialistsOrderByDiagnosesDesc();

    @Query("""
           SELECT new pe.edu.upc.noctuwell.dtos.SpecialistWithoutReviewsDTO(
               s.id,
               s.firstName,
               s.lastName,
               s.phone,
               s.certification,
               s.description,
               s.experience
           )
           FROM Specialist s
           LEFT JOIN s.reviews r
           WHERE r IS NULL
           """)
    List<SpecialistWithoutReviewsDTO> findSpecialistsWithoutReviews();
    Specialist findByUserUsername(String username);
    Optional<Specialist> findByUserId(Long userId);


}
