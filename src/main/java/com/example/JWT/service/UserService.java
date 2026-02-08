package com.example.JWT.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.JWT.DTO.UserDTO;
import com.example.JWT.Entity.User;
import com.example.JWT.Repo.UserRepo;


@Service
public class UserService implements UserDetailsService {


    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    public UserService(UserRepo userRepo,  PasswordEncoder passwordEncoder)
    {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    public UserDTO findUser(String name)
    {
        UserDTO uDto= new UserDTO();
        User usr = userRepo.findByName(name).orElse(null);
        uDto.setName(usr.getName());
        uDto.setPassword(null);
        String roles = usr.getAuthorities().stream().map(a -> a.getAuthority()).reduce((a,b) -> a + "," + b).orElse("");
        uDto.setRoles(roles);
        return uDto;
    }
    public void register(UserDTO uDto)
    {
        User usr = new User();
        usr.setName(uDto.getName());
        usr.setPassword(passwordEncoder.encode(uDto.getPassword()));
        usr.setRoles(uDto.getRoles());
        userRepo.save(usr);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user= userRepo.findByName(username).orElseThrow( () -> new UsernameNotFoundException("Username is not present" + username));
        return org.springframework.security.core.userdetails.User.builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .authorities(user.getRoles())   // <-- MUST be authorities
        .build();
    }

}

