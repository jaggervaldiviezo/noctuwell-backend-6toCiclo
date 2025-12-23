package pe.edu.upc.noctuwell.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.noctuwell.dtos.DTOToken;
import pe.edu.upc.noctuwell.dtos.DTOUser;
import pe.edu.upc.noctuwell.entities.Authority;
import pe.edu.upc.noctuwell.entities.User;
import pe.edu.upc.noctuwell.exceptions.ResourceNotFoundException;
import pe.edu.upc.noctuwell.security.JwtUtilService;
import pe.edu.upc.noctuwell.security.UserSecurity;
import pe.edu.upc.noctuwell.services.AuthorityService;
import pe.edu.upc.noctuwell.services.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/upc")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private JwtUtilService jwtUtilService;


    @PostMapping("/users")
    public ResponseEntity<User> insertarUser(@RequestBody User user) {
        User newUser = userService.addUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id, Authentication authentication) {
        boolean isTaylor = authentication.getAuthorities().stream()
                .anyMatch(auth ->
                        auth.getAuthority().equals("ROLE_TAYLOR") ||
                                auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isTaylor) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only ROLE_TAYLOR and ROLE_ADMIN can access this resource :(");
        }

        DTOUser user = userService.findByIdDTO(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/login")
    public ResponseEntity<DTOToken> login(@RequestBody DTOUser user) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        UserSecurity userSecurity = (UserSecurity) userDetailsService.loadUserByUsername(user.getUsername());

        String jwt = jwtUtilService.generateToken(userSecurity);
        Long id = userSecurity.getUser().getId();
        String authorities = userSecurity.getUser().getAuthorities()
                .stream()
                .map(a -> a.getName())
                .collect(Collectors.joining(";", "", ""));

        return new ResponseEntity<>(new DTOToken(jwt, id, authorities), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<DTOUser>> listAll() {
        List<User> users = userService.listAll();

        List<DTOUser> userDTOs = users.stream().map(user -> {
            DTOUser dto = new DTOUser();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            // Convertimos las autoridades a String separadas por ;
            String authorities = user.getAuthorities().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.joining(";"));
            dto.setAuthorities(authorities);
            dto.setPassword(""); // No enviamos el password
            return dto;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping("/users/register")
    public ResponseEntity<DTOUser> registerUser(@RequestBody DTOUser dtoUser) {
        // Si authorities está vacío, lo dejamos como cadena vacía
        if (dtoUser.getAuthorities() == null || dtoUser.getAuthorities().trim().isEmpty()) {
            dtoUser.setAuthorities("");  // Asigna un string vacío
        }

        // Asignamos authorityId = 4 para definir el rol vacío
        // **No agregamos authorityId al DTO, lo gestionamos internamente**
        String defaultRole = "";  // Esto representa el rol vacío (ID = 4)

        // Crear el usuario con el rol vacío por defecto
        User newUser = userService.addUser(dtoUser, defaultRole);  // Pasamos el rol vacío al servicio
        dtoUser.setPassword(""); // No exponer la contraseña
        dtoUser.setId(newUser.getId());

        return new ResponseEntity<>(dtoUser, HttpStatus.CREATED);
    }


    // Metodo para actualizar la autoridad de un usuario (sin recibir el id)
    @PutMapping("/users/update-authority")
    public ResponseEntity<User> updateUserAuthority(@RequestBody DTOUser dtoUser) {
        try {
            // Actualizar la autoridad del usuario
            User updatedUser = userService.updateUserAuthority(dtoUser.getUsername(), dtoUser.getAuthorities());
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
