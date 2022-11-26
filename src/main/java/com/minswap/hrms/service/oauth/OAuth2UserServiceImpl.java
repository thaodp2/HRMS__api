package com.minswap.hrms.service.oauth;

import com.minswap.hrms.entities.MyOAuthUser;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.PersonRole;
import com.minswap.hrms.entities.Role;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.PersonRoleRepository;
import com.minswap.hrms.repsotories.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    private final RoleRepository roleRepository;
    private final PersonRoleRepository personRoleRepository;
    private final PersonRepository personRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        Person person = personRepository.getUserByEmail(oAuth2User.getAttribute("email"));
//        List<PersonRole> personRoles = person == null? null : personRoleRepository.findByPersonId(person.getPersonId());
//        List<Role> roles = personRoles == null? null : roleRepository.findByRoleIdIn(personRoles.stream().map(PersonRole::getRoleId).collect(Collectors.toList()));
//        return new MyOAuthUser(oAuth2User, roles);
        return null;
    }
}
