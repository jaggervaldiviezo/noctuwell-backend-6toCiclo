package pe.edu.upc.noctuwell.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.upc.noctuwell.dtos.TypeSpecialistAverageScoreDTO;
import pe.edu.upc.noctuwell.dtos.TypeSpecialistMostReviewsDTO;
import pe.edu.upc.noctuwell.entities.TypeSpecialist;

import java.util.List;

@Repository
public interface TypeSpecialistRepository extends JpaRepository<TypeSpecialist,Long> {
//reseña tipoespecialista suma
    @Query("""
           SELECT new pe.edu.upc.noctuwell.dtos.TypeSpecialistMostReviewsDTO(
               ts.id, ts.name, ts.description, COUNT(r)
           )
           FROM TypeSpecialist ts
           JOIN ts.specialists s
           JOIN s.reviews r
           GROUP BY ts.id, ts.name, ts.description
           ORDER BY COUNT(r) DESC
           """)
    List<TypeSpecialistMostReviewsDTO> findTypeSpecialistsOrderByTotalReviewsDesc();
//PROMEDIO TIPO ESPECIALISTA PUNTAJE
    @Query("""
       SELECT new pe.edu.upc.noctuwell.dtos.TypeSpecialistAverageScoreDTO(
            ts.id,
            ts.name,
            ts.description,
            AVG(r.calificacion),
            COUNT(r)
       )
       FROM TypeSpecialist ts
       JOIN ts.specialists s
       JOIN s.reviews r
       GROUP BY ts.id, ts.name, ts.description
       ORDER BY AVG(r.calificacion) DESC
       """)
    List<TypeSpecialistAverageScoreDTO> getAverageRatingByTypeSpecialist();


}
