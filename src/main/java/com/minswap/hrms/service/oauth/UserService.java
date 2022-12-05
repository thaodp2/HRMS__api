package com.minswap.hrms.service.oauth;

import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.PersonRole;
import com.minswap.hrms.entities.Role;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.PersonRoleRepository;
import com.minswap.hrms.repsotories.RoleRepository;
import com.minswap.hrms.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserService {
    private final PersonRepository personRepository;
    private final PersonRoleRepository personRoleRepository;
    private final RoleRepository roleRepository;

    public void register(String email) {
//        Person existUser = personRepository.getUserByEmail(email);

//        if (existUser == null) {
//            Person newUser = new Person();
//            newUser.setEmail(email);
//            personRepository.save(newUser);
//        }

    }

    public UserPrincipal loadUserByEmail(String userEmail) {
        Person person = personRepository.findPersonByEmail(userEmail).orElse(null);
        List<PersonRole> personRoles = personRoleRepository.findByPersonId(person != null ? person.getPersonId() : null);
        List<Role> roles = roleRepository.findByRoleIdIn(personRoles.stream().map(PersonRole::getRoleId).collect(Collectors.toList()));
        return UserPrincipal.create(person, roles);
    }
}
