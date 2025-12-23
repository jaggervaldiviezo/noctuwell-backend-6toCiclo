package pe.edu.upc.noctuwell.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.noctuwell.dtos.MessageDTO;
import pe.edu.upc.noctuwell.entities.Message;
import pe.edu.upc.noctuwell.services.MessageService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/noctuwell")
public class MessageController {

    @Autowired
    private MessageService messageService;
/*
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> listAll() {
        return new ResponseEntity<>(messageService.listAll(), HttpStatus.OK);
    }*/

    @GetMapping("/messages/{id}")
    public ResponseEntity<Message> findById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(messageService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/messages")
    public ResponseEntity<?> add(@RequestBody MessageDTO messageDTO, Authentication authentication) {
        try {
            String username = authentication.getName();
            String role = authentication.getAuthorities().toString(); // Ej: [ROLE_PATIENT]

            MessageDTO newMsg = messageService.add(messageDTO, username, role);
            return new ResponseEntity<>(newMsg, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Devolvemos un Bad Request con el mensaje de error (ej: "Espera al especialista")
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/messages")
    public ResponseEntity<Message> edit(@RequestBody Message message) {
        Message edited = messageService.edit(message);
        return new ResponseEntity<>(edited, HttpStatus.OK);
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        messageService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/messages/appointment/{appointmentId}")
    public ResponseEntity<?> listByAppointment(@PathVariable Long appointmentId, Authentication authentication) {
        try {
            String currentUsername = authentication.getName();
            // Ahora devuelve List<MessageDTO>
            List<MessageDTO> messages = messageService.listByAppointment(appointmentId, currentUsername);
            return new ResponseEntity<>(messages, HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

}