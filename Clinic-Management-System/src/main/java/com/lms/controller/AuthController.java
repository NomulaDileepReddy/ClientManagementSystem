package com.lms.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.model.JwtRequest;
import com.lms.model.JwtResponse;
import com.lms.model.Users;
import com.lms.security.JWTHelper;
import com.lms.service.UserService;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
	@Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private UserService userservice;
    
    
    @Autowired
    private JWTHelper helper;

//    private Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {

        this.doAuthenticate(request.getEmail(), request.getPassword());


        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername())
                .role(userservice.getRoleByEmail(request.getEmail()))
                .name(userservice.getNamebyEmail(request.getEmail())).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    private void doAuthenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);


        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }

    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid !!";
    }
    
    
    @PostMapping("/create-user")
    public Users createUser(@RequestBody Users user) {
    	return userservice.createUser(user);
    }
    @GetMapping("/users")
	public List<Users> getUsers()
	{
		return userservice.getUsers();
	}
    @GetMapping("/useremail")
	public Users getUsersbyId(@RequestParam String email)
	{
		return userservice.getUsersbyemail(email);
	}
	
    
   

}
