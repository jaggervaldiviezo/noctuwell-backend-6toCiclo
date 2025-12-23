package pe.edu.upc.noctuwell.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upc.noctuwell.dtos.AppointmentDTO;
import pe.edu.upc.noctuwell.dtos.AppointmentReportDTO;
import pe.edu.upc.noctuwell.dtos.SlotDTO;
import pe.edu.upc.noctuwell.entities.Appointment;
import pe.edu.upc.noctuwell.entities.Patient;
import pe.edu.upc.noctuwell.entities.Schedule;
import pe.edu.upc.noctuwell.entities.Specialist;
import pe.edu.upc.noctuwell.repositories.AppointmentRepository;

import pe.edu.upc.noctuwell.repositories.ScheduleRepository;
import pe.edu.upc.noctuwell.services.AppointmentService;
import pe.edu.upc.noctuwell.services.PatientService;
import pe.edu.upc.noctuwell.services.SpecialistService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    PatientService patientService;

    @Autowired
    SpecialistService specialistService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public AppointmentDTO add(AppointmentDTO appointmentDTO) {

        Specialist specialistFound = specialistService.findById(appointmentDTO.getSpecialistId());
        Patient patientFound = patientService.findById(appointmentDTO.getPatientId());

        if (specialistFound == null || patientFound == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Specialist or Patient not found"
            );
        }


        boolean taken = appointmentRepository.existsBySpecialist_IdAndDateAndTime(
                specialistFound.getId(),
                appointmentDTO.getDate(),
                appointmentDTO.getTime()
        );

        if (taken) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El especialista ya tiene una cita en esa fecha y hora"
            );
        }

        // 3. Crear y guardar la cita
        Appointment appointment = new Appointment(
                null,
                appointmentDTO.getDate(),
                appointmentDTO.getTime(),
                appointmentDTO.getReason(),
                appointmentDTO.getStatus(),
                patientFound,
                specialistFound,
                null,
                null
        );

        appointment = appointmentRepository.save(appointment);
        appointmentDTO.setId(appointment.getId());
        return appointmentDTO;
    }

    @Override
    public Appointment edit(Appointment appointment) {
        Appointment found = findById(appointment.getId());
        if (found == null) return null;

        if (appointment.getDate() != null)
            found.setDate(appointment.getDate());
        if (appointment.getTime() != null)
            found.setTime(appointment.getTime());
        if (appointment.getReason() != null)
            found.setReason(appointment.getReason());
        if (appointment.getStatus() != null)
            found.setStatus(appointment.getStatus());

        return appointmentRepository.save(found);
    }

    @Override
    public void delete(Long id) {
        Appointment found = findById(id);
        if (found != null)
            appointmentRepository.deleteById(id);
    }

    @Override
    public Appointment findById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Appointment> listAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public List<Appointment> listByPatientId(Long patientId) {
        return appointmentRepository.findByPatient_Id(patientId);
    }

    @Override
    public List<Appointment> listBySpecialistId(Long specialistId) {
        return appointmentRepository.findBySpecialist_Id(specialistId);
    }

    @Override
    public List<Appointment> findByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    @Override
    public List<AppointmentReportDTO> reportEspecialistasConMasPacientes() {
        List<Object[]> data = appointmentRepository.reportEspecialistasConMasPacientesJPQL();
        List<AppointmentReportDTO> report = new ArrayList<>();

        for (Object[] row : data) {
            AppointmentReportDTO dto = new AppointmentReportDTO(
                    ((Number) row[0]).longValue(),
                    (String) row[1],
                    ((Number) row[2]).intValue()
            );
            report.add(dto);
        }

        return report;
    }

    @Override
    public List<AppointmentReportDTO> reportPacientesConMasCitas() {
        List<Object[]> rows = appointmentRepository.reportPacientesConMasCitasSQL();
        List<AppointmentReportDTO> report = new ArrayList<>();

        for (Object[] row : rows) {
            AppointmentReportDTO dto = new AppointmentReportDTO(
                    ((Number) row[0]).longValue(),
                    (String) row[1],
                    ((Number) row[2]).intValue()
            );
            report.add(dto);
        }
        return report;
    }

    @Override
    public List<SlotDTO> getAvailableSlots(Long specialistId, LocalDate date) {
        // 1. Traemos TODOS los horarios base del doctor (Ej: 8:00, 9:00, 10:00)
        List<Schedule> baseSchedules = scheduleRepository.findBySpecialistId(specialistId);

        // 2. Traemos las horas que YA ESTÁN OCUPADAS en esa fecha específica
        // (Asegúrate que tu query en AppointmentRepository devuelva List<LocalTime>)
        List<LocalTime> takenHours = appointmentRepository.findTakenSlots(specialistId, date);

        // 3. Comparamos y creamos la lista de Slots
        List<SlotDTO> slots = new ArrayList<>();

        for (Schedule schedule : baseSchedules) {
            boolean isTaken = takenHours.contains(schedule.getHoraInicio());

            slots.add(new SlotDTO(
                    schedule.getId(),          // ID del horario original
                    schedule.getHoraInicio(),  // La hora (Ej: 08:00)
                    !isTaken                   // Si está ocupado, available = false
            ));
        }

        // Opcional: Ordenar por hora
        slots.sort((s1, s2) -> s1.getTime().compareTo(s2.getTime()));

        return slots;
    }

    @Override
    public List<Appointment> listMyAppointments(String username, String role) {
        if (role.contains("ROLE_PATIENT")) {
            return appointmentRepository.findByPatient_User_Username(username);
        }
        else if (role.contains("ROLE_SPECIALIST")) {
            return appointmentRepository.findBySpecialist_User_Username(username);
        }
        else if (role.contains("ROLE_ADMIN")) {
            // El admin puede ver todo
            return appointmentRepository.findAll();
        }

        return new ArrayList<>();
    }


}