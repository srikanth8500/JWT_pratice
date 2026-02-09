package com.example.JWT.service;

import java.util.List;

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

    public UserDTO findUser(String name) throws UsernameNotFoundException
    {
        try {
            
        
        System.out.println("entrying service " + name);
        UserDTO uDto= new UserDTO();
        User usr = userRepo.findByUsername(name).orElseThrow(() -> new UsernameNotFoundException("User not found with name: " + name));
        System.out.println("User network call");
        uDto.setName(usr.getUsername());
        uDto.setPassword(null);
        String roles = usr.getAuthorities().stream().map(a -> a.getAuthority()).reduce((a,b) -> a + "," + b).orElse("");
        uDto.setRoles(roles);
        return uDto;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Exception in service: " + e.getMessage());
            throw e;
        }
    }

    public List<UserDTO> findAllUsers()
    {
        List<User> users = userRepo.findAll();
        return users.stream().map(usr -> {
            UserDTO uDto = new UserDTO();
            uDto.setName(usr.getUsername());
            uDto.setPassword(null);
            String roles = usr.getAuthorities().stream().map(a -> a.getAuthority()).reduce((a,b) -> a + "," + b).orElse("");
            uDto.setRoles(roles);
            return uDto;
        }).toList();
    }
    public void register(UserDTO uDto)
    {
        User usr = new User();
        usr.setUsername(uDto.getName());
        usr.setPassword(passwordEncoder.encode(uDto.getPassword()));
        usr.setRoles(uDto.getRoles());
        userRepo.save(usr);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try{
        User user= userRepo.findByUsername(username).orElseThrow( () -> new UsernameNotFoundException("Username is not present" + username));
        return org.springframework.security.core.userdetails.User.builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .authorities(user.getRoles())
        .build();
    }
catch (Exception e) {    
    System.out.println("Exception in loadUserByUsername: " + e.getMessage());
    throw e;    
}
    }

}

