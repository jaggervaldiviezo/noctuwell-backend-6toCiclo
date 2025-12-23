package pe.edu.upc.noctuwell.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.noctuwell.dtos.ScheduleDTO;
import pe.edu.upc.noctuwell.dtos.SlotDTO;
import pe.edu.upc.noctuwell.entities.Schedule;
import pe.edu.upc.noctuwell.entities.Specialist;
import pe.edu.upc.noctuwell.repositories.ScheduleRepository;
import pe.edu.upc.noctuwell.repositories.SpecialistRepository;
import pe.edu.upc.noctuwell.services.ScheduleService;
import pe.edu.upc.noctuwell.services.SpecialistService;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    SpecialistService specialistService;
    @Autowired
    SpecialistRepository specialistRepository;

    @Override
    public List<Schedule> listAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public Schedule findById(Long id, String username) {
        Schedule schedule = scheduleRepository.findById(id).orElse(null);
        if (schedule == null) return null;
        if (!schedule.getSpecialist().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Acceso denegado: Este horario no te pertenece.");
        }
        return schedule;
    }

    @Override
    public ScheduleDTO add(ScheduleDTO scheduleDTO, String username) {
        Specialist specialist = specialistRepository.findByUserUsername(username);

        if(specialist == null) {
            throw new RuntimeException("El usuario logueado no es un especialista registrado");
        }

        Schedule schedule = new Schedule(
                null,
                scheduleDTO.getHoraInicio(),
                specialist
        );

        schedule = scheduleRepository.save(schedule);
        scheduleDTO.setId(schedule.getId());
        return scheduleDTO;
    }

    @Override
    public ScheduleDTO edit(ScheduleDTO dto, String username) {
        Schedule schedule = scheduleRepository.findById(dto.getId()).orElse(null);
        if (schedule == null) {
            throw new RuntimeException("Horario no encontrado");
        }

        // 2. VALIDACIÓN DE PROPIEDAD
        if (!schedule.getSpecialist().getUser().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permiso para editar este horario.");
        }

        // 3. Actualizamos solo la hora (el especialista NO cambia)
        schedule.setHoraInicio(dto.getHoraInicio());

        scheduleRepository.save(schedule);
        return dto;
    }

    @Override
    public void delete(Long id, String username) {
        Schedule schedule = scheduleRepository.findById(id).orElse(null);
        if (schedule == null) return;

        if (!schedule.getSpecialist().getUser().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permiso para eliminar este horario.");
        }

        scheduleRepository.deleteById(id);
    }


    @Override
    public List<Schedule> listBySpecialist(String username) {
        Specialist specialist = specialistRepository.findByUserUsername(username);
        if(specialist == null) return List.of();

        return scheduleRepository.findBySpecialist(specialist);
    }

}
