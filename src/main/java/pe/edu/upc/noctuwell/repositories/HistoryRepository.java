package pe.edu.upc.noctuwell.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.noctuwell.dtos.HistoryDTO;
import pe.edu.upc.noctuwell.entities.History;
import pe.edu.upc.noctuwell.entities.Patient;
import pe.edu.upc.noctuwell.entities.Specialist;
import pe.edu.upc.noctuwell.entities.TypeSpecialist;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findByPatient(Patient patient);

    // Especialista ve historias de SUS pacientes (vía citas)
    @Query("""
           SELECT DISTINCT h
           FROM History h
           WHERE h.patient IN (
               SELECT a.patient
               FROM Appointment a
               WHERE a.specialist = :specialist
           )
           """)
    List<History> findBySpecialistPatients(@Param("specialist") Specialist specialist);

    Optional<History> findByPatientAndTypeSpecialist(Patient patient, TypeSpecialist typeSpecialist);

    List<History> findByPatientId(Long id);

    List<History> findByPatientIdAndTypeSpecialistId(Long patientId, Long typeSpecialistId);
}
