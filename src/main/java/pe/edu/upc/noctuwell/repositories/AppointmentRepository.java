package pe.edu.upc.noctuwell.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.noctuwell.entities.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient_Id(Long patientId);
    List<Appointment> findBySpecialist_Id(Long specialistId);

    //HU66 JPQL -- Funciona
    @Query("SELECT a.specialist.id, CONCAT(a.specialist.firstName, ' ', a.specialist.lastName), COUNT(DISTINCT a.patient.id) " +
            "FROM Appointment a " +
            "WHERE a.status = 'COMPLETO' " +
            "GROUP BY a.specialist.id, a.specialist.firstName, a.specialist.lastName " +
            "ORDER BY COUNT(DISTINCT a.patient.id) DESC")
    List<Object[]> reportEspecialistasConMasPacientesJPQL();

    // HU69 SQL -- Funciona para mostrar a los 2 pacientes con mas citas
    @Query(value = "SELECT p.id, CONCAT(p.first_name, ' ', p.last_name) AS patient_name, COUNT(a.id) AS total_appointments " +
            "FROM appointments a " +
            "JOIN patients p ON a.patient_id = p.id " +
            "WHERE a.status = 'COMPLETO' " +
            "GROUP BY p.id, p.first_name, p.last_name " +
            "ORDER BY total_appointments DESC " +
            "LIMIT 2",
            nativeQuery = true)
    List<Object[]> reportPacientesConMasCitasSQL();

    // HU78 Query Method -- Funciona
    List<Appointment> findByStatus(String status);

    // para validar la fecha de una cita
    boolean existsBySpecialist_IdAndDateAndTime(Long specialistId, LocalDate date, LocalTime time);

    @Query("SELECT a.time FROM Appointment a WHERE a.specialist.id = :specialistId AND a.date = :date AND a.status <> 'CANCELADO'")
    List<LocalTime> findTakenSlots(@Param("specialistId") Long specialistId, @Param("date") LocalDate date);

    List<Appointment> findByPatient_User_Username(String username);
    List<Appointment> findBySpecialist_User_Username(String username);
}
