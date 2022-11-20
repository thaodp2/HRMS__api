package com.minswap.hrms.service.oauth;

import com.minswap.hrms.entities.Person;
import com.minswap.hrms.repsotories.PersonRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {
    private final PersonRepository personRepository;

    public void register(String email) {
        Person existUser = personRepository.getUserByEmail(email);

        if (existUser == null) {
            Person newUser = new Person();
            newUser.setEmail(email);
            personRepository.save(newUser);
        }

    }
}
