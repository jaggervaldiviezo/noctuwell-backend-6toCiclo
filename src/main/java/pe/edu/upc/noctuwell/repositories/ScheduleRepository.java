package pe.edu.upc.noctuwell.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.noctuwell.entities.Schedule;
import pe.edu.upc.noctuwell.entities.Specialist;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findBySpecialist(Specialist specialist);
    List<Schedule> findBySpecialistId(Long specialistId);
}
