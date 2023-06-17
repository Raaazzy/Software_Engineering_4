package com.idz4.userservice.controller;

import com.idz4.userservice.entity.Session;
import com.idz4.userservice.entity.Users;
import com.idz4.userservice.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
public class UsersController {
    @Autowired
    private UsersService usersService;
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Users users, @RequestParam("password") String password) {
        log.info("Inside registerUser method of UsersController");
        if (!Users.isValidEmailAddress(users.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Email in not valid");
        }
        users.setPassword_hash(Integer.toString(password.hashCode()));
        usersService.registerUser(users);
        return ResponseEntity.status(HttpStatus.OK).body("User created successfully");
    }

    @PostMapping("/authorize")
    public ResponseEntity<String> authorizeUser(@RequestParam("email") String email, @RequestParam("password") String password) {
        log.info("Inside authorizeUser method of UsersController");
        if (!usersService.checkIfUserExists(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with such email not found");
        }
        Users user = usersService.getUserByEmail(email).get();
        if (!Integer.toString(password.hashCode()).equals(user.getPassword_hash())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wrong password");
        }
        Session session = new Session();
        session.setUserId(user.getId());
        session.setSessionToken(Session.generateToken(user.getUsername()));
        Date now = new Date();
        session.setExpiresAt(new Date(now.getTime() + Session.EXPIRATION_TIME));
        usersService.addUserToSessions(session);
        return ResponseEntity.status(HttpStatus.OK).body(session.getSessionToken());
    }

    @PostMapping("/findwithtoken")
    public ResponseEntity<String> findWithToken(@RequestParam("token") String token) {
        log.info("Inside findWithToken method of UsersController");
        Optional<Session> optionalSession = usersService.getSessionByToken(token);
        if (optionalSession.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user with such token");
        }
        Date now = new Date();
        if (now.after(optionalSession.get().getExpiresAt())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This token has expired");
        }
        Optional<Users> user = usersService.getUserById(optionalSession.get().getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user with such id");
        }
        return ResponseEntity.status(HttpStatus.OK).body(user.toString());
    }

    @PostMapping("/setrole")
    public ResponseEntity<String> setRole(@RequestParam("token") String token, @RequestParam("role") String role) {
        log.info("Inside setRole method of UsersController");
        Optional<Session> optionalSession = usersService.getSessionByToken(token);
        if (optionalSession.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user with such token");
        }
        Date now = new Date();
        if (now.after(optionalSession.get().getExpiresAt())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This token has expired");
        }
        Optional<Users> user = usersService.getUserById(optionalSession.get().getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user with such id");
        }
        if (!Objects.equals(role, "customer") && !Objects.equals(role, "chef") && !Objects.equals(role, "manager")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wrong role");
        }
        user.get().setRole(role);
        usersService.registerUser(user.get());
        return ResponseEntity.status(HttpStatus.OK).body("Successfully changed role");
    }

    @GetMapping("/getrole")
    public ResponseEntity<String> getRole(@RequestParam String token) {
        log.info("Inside getRole method of UsersController");
        Optional<Session> optionalSession = usersService.getSessionByToken(token);
        if (optionalSession.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user with such token");
        }
        Date now = new Date();
        if (now.after(optionalSession.get().getExpiresAt())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This token has expired");
        }
        Optional<Users> user = usersService.getUserById(optionalSession.get().getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user with such id");
        }
        return ResponseEntity.status(HttpStatus.OK).body(user.get().getRole());
    }
}
