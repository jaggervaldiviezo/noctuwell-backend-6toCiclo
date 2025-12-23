package pe.edu.upc.noctuwell.services;

import pe.edu.upc.noctuwell.entities.Authority;

import java.util.List;

public interface AuthorityService {

    public Authority addAuthority(Authority authority);

    public Authority findByName(String authorityName);

    List<Authority> findAllById(List<Long> ids);
}
