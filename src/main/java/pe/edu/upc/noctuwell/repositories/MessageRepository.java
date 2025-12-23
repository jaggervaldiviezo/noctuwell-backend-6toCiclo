package pe.edu.upc.noctuwell.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.noctuwell.entities.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByAppointmentIdOrderBySentAtAsc(Long appointmentId);

    long countByAppointmentId(Long appointmentId);

    List<Message> findByPatientId(Long patientId);
    List<Message> findBySpecialistId(Long specialistId);
}
