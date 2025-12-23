package pe.edu.upc.noctuwell.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.noctuwell.entities.Diagnosis;
import pe.edu.upc.noctuwell.entities.History;

import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    List<Diagnosis> findByHistoryPatientId(Long patientId);

    // Consulta para obtener los diagnósticos de los pacientes de un especialista
    List<Diagnosis> findBySpecialistUserUsername(String username);
    List<Diagnosis> findByHistoryIn(List<History> histories);

}
