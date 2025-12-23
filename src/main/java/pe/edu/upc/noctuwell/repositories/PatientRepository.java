package pe.edu.upc.noctuwell.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.noctuwell.dtos.PatientDiagnosesDTO;
import pe.edu.upc.noctuwell.dtos.PatientWithoutAppointmentsDTO;
import pe.edu.upc.noctuwell.entities.Patient;
import pe.edu.upc.noctuwell.entities.Specialist;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Query("""
        SELECT new pe.edu.upc.noctuwell.dtos.PatientDiagnosesDTO(
            p.id,
            p.firstName,
            p.lastName,
            COUNT(d)
        )
        FROM Patient p
        JOIN p.appointments a
        JOIN a.diagnoses d
        GROUP BY p.id, p.firstName, p.lastName
        ORDER BY COUNT(d) DESC
        """)
    List<PatientDiagnosesDTO> findPatientsByDiagnoses();



    @Query("""
       SELECT new pe.edu.upc.noctuwell.dtos.PatientWithoutAppointmentsDTO(
            p.id,
            p.firstName,
            p.lastName,
            p.gender,
            p.phone,
            p.birthDate
       )
       FROM Patient p
       LEFT JOIN p.appointments a
       WHERE a IS NULL
       """)
    List<PatientWithoutAppointmentsDTO> findPatientsWithoutAppointments();

    Patient findByUserUsername(String username);

    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.specialist = :specialist")
    List<Patient> findDistinctBySpecialist(@Param("specialist") Specialist specialist);

    Patient findByUser_Id(Long userId);

}

