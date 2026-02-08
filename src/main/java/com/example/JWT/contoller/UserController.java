package com.example.JWT.contoller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.JWT.DTO.UserDTO;
import com.example.JWT.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api")
public class UserController {

private UserService userService;

public UserController(UserService userService) {
    this.userService = userService;
}

@PostMapping("/register")
public String registeruser(@RequestBody UserDTO userDTO) {
    userService.register(userDTO);
    return "user created sucessfully";
}

@GetMapping("/user")
public UserDTO getMethodName(@RequestParam String name) {
    return userService.findUser(name);
}


}
