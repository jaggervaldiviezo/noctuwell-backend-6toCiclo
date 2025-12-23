package pe.edu.upc.noctuwell.services;

import pe.edu.upc.noctuwell.dtos.DTOUser;
import pe.edu.upc.noctuwell.entities.User;

import java.util.List;

public interface UserService {

    User findById (Long id);
    public DTOUser findByIdDTO (Long id);
    public User findByUsername(String username);

    User addUser(DTOUser dtoUser, String role);
    public User addUser(User user);
    List<User> listAll();


    void save(User user);
    User updateUserAuthority(String username, String newAuthority);
}
