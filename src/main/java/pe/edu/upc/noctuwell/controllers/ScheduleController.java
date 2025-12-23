package pe.edu.upc.noctuwell.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.noctuwell.dtos.ScheduleDTO;
import pe.edu.upc.noctuwell.entities.Schedule;
import pe.edu.upc.noctuwell.services.ScheduleService;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/noctuwell/schedules")
@CrossOrigin("*")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> listBySpecialist(Authentication authentication) {
        // Obtenemos el username del token
        String username = authentication.getName();

        List<ScheduleDTO> schedules = scheduleService.listBySpecialist(username).stream()
                .map(s -> new ScheduleDTO(
                        s.getId(),
                        s.getHoraInicio(),
                        s.getSpecialist().getId()
                )).collect(Collectors.toList());
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }
    /*@GetMapping
    public ResponseEntity<List<ScheduleDTO>> listAll() {
        List<ScheduleDTO> schedules = scheduleService.listAll().stream()
                .map( s -> new ScheduleDTO(
                        s.getId(),
                        s.getHoraInicio(),
                        s.getSpecialist().getId()
                )).collect(Collectors.toList());
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> findById(@PathVariable Long id, Authentication authentication) {
        try {
            Schedule schedule = scheduleService.findById(id, authentication.getName());
            return new ResponseEntity<>(schedule, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping
    public ResponseEntity<ScheduleDTO> add(@RequestBody ScheduleDTO scheduleDTO, Authentication authentication) {
        scheduleDTO = scheduleService.add(scheduleDTO, authentication.getName());
        return new ResponseEntity<>(scheduleDTO, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> edit(@RequestBody ScheduleDTO dto, Authentication authentication) {
        try {
            ScheduleDTO edited = scheduleService.edit(dto, authentication.getName());
            return new ResponseEntity<>(edited, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication authentication) {
        try {
            scheduleService.delete(id, authentication.getName());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
