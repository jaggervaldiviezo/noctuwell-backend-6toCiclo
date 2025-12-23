package pe.edu.upc.noctuwell.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.noctuwell.dtos.MessageDTO;
import pe.edu.upc.noctuwell.entities.*;
import pe.edu.upc.noctuwell.repositories.MessageRepository;
import pe.edu.upc.noctuwell.services.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private SpecialistService specialistService;


    @Override
    public MessageDTO add(MessageDTO messageDTO, String senderUsername, String role) {
        Appointment appointment = appointmentService.findById(messageDTO.getAppointmentId());
        if (appointment == null) {
            throw new RuntimeException("Cita no encontrada");
        }

        // --- VALIDACIÓN DE SEGURIDAD FALTANTE ---
        // Verificar que quien envía el mensaje es el paciente O el especialista de ESTA cita.
        String patientUser = appointment.getPatient().getUser().getUsername();
        String specialistUser = appointment.getSpecialist().getUser().getUsername();

        if (!senderUsername.equals(patientUser) && !senderUsername.equals(specialistUser)) {
            throw new RuntimeException("No formas parte de esta cita, no puedes enviar mensajes.");
        }
        // ----------------------------------------

        if ("COMPLETO".equals(appointment.getStatus()) || "CANCELADO".equals(appointment.getStatus())) {
            throw new RuntimeException("El chat está cerrado porque la cita ha finalizado.");
        }

        long messageCount = messageRepository.countByAppointmentId(appointment.getId());

        if (messageCount == 0 && role.contains("ROLE_PATIENT")) {
            throw new RuntimeException("Debes esperar a que el especialista inicie la conversación.");
        }

        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setSentAt(LocalDateTime.now());
        message.setSenderUsername(senderUsername);
        message.setAppointment(appointment);
        message.setPatient(appointment.getPatient());
        message.setSpecialist(appointment.getSpecialist());

        message = messageRepository.save(message);

        // Retornamos el DTO actualizado con ID y fecha
        messageDTO.setId(message.getId());
        messageDTO.setSentAt(message.getSentAt());
        messageDTO.setSenderUsername(senderUsername);

        return messageDTO;
    }

    @Override
    public Message edit(Message message) {
        Message found = findById(message.getId());
        if (found == null) return null;

        if (message.getContent() != null)
            found.setContent(message.getContent());

        return messageRepository.save(found);
    }

    @Override
    public void delete(Long id) {
        Message found = findById(id);
        if (found != null)
            messageRepository.deleteById(id);
    }

    @Override
    public Message findById(Long id) {
        return messageRepository.findById(id).orElse(null);
    }

    @Override
    public List<Message> listAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<MessageDTO> listByAppointment(Long appointmentId, String currentUsername) {
        Appointment appointment = appointmentService.findById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("Cita no encontrada");
        }

        String patientUser = appointment.getPatient().getUser().getUsername();
        String specialistUser = appointment.getSpecialist().getUser().getUsername();

        // Validación de visualización
        if (!currentUsername.equals(patientUser) && !currentUsername.equals(specialistUser)) {
            throw new RuntimeException("No tienes permiso para ver esta conversación.");
        }

        List<Message> messages = messageRepository.findByAppointmentIdOrderBySentAtAsc(appointmentId);

        // CONVERTIR ENTIDAD A DTO PARA EVITAR ERRORES DE JSON
        return messages.stream().map(m -> {
            return new MessageDTO(
                    m.getId(),
                    m.getContent(),
                    m.getSentAt(),
                    m.getSenderUsername(),
                    m.getAppointment().getId(),
                    m.getPatient().getId(),
                    m.getSpecialist().getId()
            );
        }).toList();
    }
}
