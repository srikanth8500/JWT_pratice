package com.example.jwt.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jwt.Entity.userEntity;
import com.example.jwt.dto.Userdto;
import com.example.jwt.repo.userRepo;

@Service
public class userService implements UserDetailsService{

    private final userRepo userrepo;
    private final PasswordEncoder passwordEncoder;

    public userService(userRepo userrepo, PasswordEncoder passwordEncoder)
    {
        this.userrepo =userrepo;
        this.passwordEncoder=passwordEncoder;
        
    }

    public void saveUser(Userdto user)
    {
      userEntity newuser = new userEntity();
      newuser.setUsername(user.getName());
      newuser.setPassword(passwordEncoder.encode(user.getPassword()));
      newuser.setRoles(user.getRoles());
      userrepo.save(newuser);
    }

    public Userdto getUserDetails(String name)
    {
        userEntity newuser = userrepo.findByname(name).orElse(null);
        Userdto dto = new Userdto();
        dto.setName(newuser.getUsername());
        dto.setPassword(null);
        dto.setRoles(newuser.getRoles());
        return dto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        userEntity user=userrepo.findByname(username).orElseThrow(()-> new UsernameNotFoundException("user name is not Found"+username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles())
                .build();
    }

}
