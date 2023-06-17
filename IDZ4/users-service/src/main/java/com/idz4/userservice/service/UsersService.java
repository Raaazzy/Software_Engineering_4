package com.idz4.userservice.service;

import com.idz4.userservice.entity.Session;
import com.idz4.userservice.entity.Users;
import com.idz4.userservice.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.idz4.userservice.repository.SessionRepository;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SessionRepository sessionRepository;

    public void registerUser(Users users) {
        log.info("Inside registerUser method of UsersService");
        usersRepository.save(users);
    }

    public Boolean checkIfUserExists(String email) {
        log.info("Inside checkIfUserExists method of UsersService");
        return usersRepository.findByEmail(email).isPresent();
    }

    public Optional<Users> getUserByEmail(String email) {
        log.info("Inside getUserByEmail method of UsersService");
        return usersRepository.findByEmail(email);
    }

    public void addUserToSessions(Session session) {
        log.info("Inside addUserToSessions method of UsersService");
        sessionRepository.save(session);
    }

    public Optional<Session> getSessionByToken(String token) {
        log.info("Inside getSessionByToken method of UsersService");
        return sessionRepository.findBySessionToken(token);
    }

    public Optional<Users> getUserById(Long id) {
        log.info("Inside getUserById method of UsersService");
        return usersRepository.findById(id);
    }
}
