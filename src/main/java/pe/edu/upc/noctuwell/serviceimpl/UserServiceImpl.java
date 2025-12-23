package pe.edu.upc.noctuwell.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upc.noctuwell.dtos.DTOUser;
import pe.edu.upc.noctuwell.entities.Authority;
import pe.edu.upc.noctuwell.entities.User;
import pe.edu.upc.noctuwell.exceptions.KeyRepeatedDataExeception;
import pe.edu.upc.noctuwell.exceptions.ResourceNotFoundException;
import pe.edu.upc.noctuwell.repositories.AuthorityRepository;
import pe.edu.upc.noctuwell.repositories.UserRepository;
import pe.edu.upc.noctuwell.services.AuthorityService;
import pe.edu.upc.noctuwell.services.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public User findById(Long id) {
        User userFound = userRepository.findById(id).orElse(null);
        if(userFound == null){
            throw new ResourceNotFoundException("User with Id: "+ id+" not found");
        }
        return userFound;
    }

    @Override
    public DTOUser findByIdDTO(Long id) {
        User user = findById(id);

        // Convertir authorities a String separado por ";"
        String authoritiesString = user.getAuthorities()
                .stream()
                .map(a -> a.getName())
                .collect(Collectors.joining(";"));

        DTOUser userFound = new DTOUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authoritiesString.isEmpty() ? null : authoritiesString
        );

        return userFound;
    }

    @Override
    public User findByUsername(String username) {
        User userFound = userRepository.findByUsername(username);
        if(userFound == null){
            throw new ResourceNotFoundException("User with Username: "+ username+" not found");
        }
        return userFound;
    }

    /**
     * Convierte un String de authorities a una lista de Authority
     * Como un usuario solo tiene UN rol, solo procesará el primero
     */
    private List<Authority> authoritiesFromString(String authorities) {
        List<Authority> authoritiesList = new ArrayList<>();

        // Validar que authorities no sea null o vacío
        if (authorities == null || authorities.trim().isEmpty()) {
            return authoritiesList; // Retornar lista vacía
        }

        // Como solo se permite UN rol, tomar solo el primero si hay varios separados por ";"
        String firstRole = authorities.split(";")[0].trim();

        if (!firstRole.isEmpty()) {
            Authority authority = authorityService.findByName(firstRole);
            if (authority != null) {
                authoritiesList.add(authority);
            }
        }

        return authoritiesList;
    }

    @Override
    public User addUser(DTOUser dtoUser, String role) {
        // Validar si el usuario ya existe
        User userFound = userRepository.findByUsername(dtoUser.getUsername());
        if(userFound != null){
            throw new KeyRepeatedDataExeception("Username: "+ dtoUser.getUsername()+" is already registered");
        }

        // Crear un nuevo usuario
        User user = new User();
        user.setUsername(dtoUser.getUsername());

        // ✅ ENCRIPTAR LA CONTRASEÑA
        user.setPassword(new BCryptPasswordEncoder().encode(dtoUser.getPassword()));

        // ✅ HABILITAR EL USUARIO
        user.setEnabled(true);


        Authority authority = authorityRepository.findById(4L)
                .orElseThrow(() -> new RuntimeException("Authority with ID 4 not found"));


        user.setAuthorities(Collections.singletonList(authority));

        // Guardar el nuevo usuario
        return userRepository.save(user);
    }

    @Override
    public User addUser(User user) {
        User userFound = userRepository.findByUsername(user.getUsername());
        if(userFound != null){
            throw new KeyRepeatedDataExeception("Username: "+ user.getUsername()+" is already registered");
        }

        // Paso 1: Validar si el username y el password cumplen con los requisitos
        // TODO: Implementar validaciones de longitud mínima, máxima, tipos de caracteres

        // Paso 2: Encriptar el password
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        // Paso 3: Actualizar los campos adicionales según tu lógica de negocio
        user.setEnabled(true);

        // Asegurar que authorities no sea null
        if (user.getAuthorities() == null) {
            user.setAuthorities(new ArrayList<>());
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> listAll() {
        return userRepository.findAll();
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User updateUserAuthority(String username, String newAuthority) {
        // Buscar al usuario por nombre de usuario
        User user = userRepository.findByUsername(username);

        // Si el usuario no existe, lanzamos una excepción
        if (user == null) {
            throw new ResourceNotFoundException("User with Username: " + username + " not found");
        }

        // Buscar la autoridad (rol) correspondiente
        Authority authority = authorityService.findByName(newAuthority);

        // Si no se encuentra la autoridad, lanzamos una excepción
        if (authority == null) {
            throw new ResourceNotFoundException("Authority " + newAuthority + " not found");
        }

        // Crear una nueva lista de autoridades con la nueva autoridad
        List<Authority> authorityList = new ArrayList<>();
        authorityList.add(authority);

        // Asignar la nueva lista de autoridades al usuario
        user.setAuthorities(authorityList);

        // Guardar el usuario actualizado
        return userRepository.save(user);
    }
}