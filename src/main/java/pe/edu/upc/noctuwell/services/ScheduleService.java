package pe.edu.upc.noctuwell.services;

import pe.edu.upc.noctuwell.dtos.ScheduleDTO;
import pe.edu.upc.noctuwell.dtos.SlotDTO;
import pe.edu.upc.noctuwell.entities.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    List<Schedule> listAll();
    Schedule findById(Long id, String username);
    ScheduleDTO add(ScheduleDTO scheduleDTO, String username);
    ScheduleDTO edit(ScheduleDTO scheduleDTO, String username);
    void delete(Long id, String username);
    List<Schedule> listBySpecialist(String username);
}
