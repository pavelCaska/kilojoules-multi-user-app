package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.User;
import com.pc.kilojoules.exception.RecordNotFoundException;
import com.pc.kilojoules.model.UserRegistrationDto;
import com.pc.kilojoules.model.UserUserProfileDto;
import com.pc.kilojoules.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public String encodePassword(String pwd) {
        return passwordEncoder.encode(pwd);
    }

    @Override
    public User fetchUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(()-> new RecordNotFoundException("User doesn't exist."));
    }

    @Override
    public User registerNewUser(String username, String password) {
        User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();
        return userRepository.save(newUser);

    }

    @Override
    public boolean existsUserByUsername(String username) {
        return userRepository.existsUserByUsername(username);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findUserById(userId).orElseThrow(()->new RecordNotFoundException("User not found."));
        userRepository.delete(user);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }
        return null;
    }

    @Override
    public List<UserUserProfileDto> fetchCombinedUserData() {
        return userRepository.fetchCombinedUserData().stream()
                .map(record -> new UserUserProfileDto(
                        (Long) record[0],
                        (Timestamp) record[1],
                        (Timestamp) record[2],
                        (String) record[3],
                        (String) record[4],
                        (String) record[5],
                        (String) record[6],
                        (String) record[7],
                        (String) record[8],
                        (Timestamp) record[9],
                        (Timestamp) record[10]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserUserProfileDto> fetchCombinedAdminData() {
        return userRepository.fetchCombinedAdminData().stream()
                .map(record -> new UserUserProfileDto(
                        (Long) record[0],
                        (Timestamp) record[1],
                        (Timestamp) record[2],
                        (String) record[3],
                        (String) record[4],
                        (String) record[5],
                        (String) record[6],
                        (String) record[7],
                        (String) record[8],
                        (Timestamp) record[9],
                        (Timestamp) record[10]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public User createUser(UserRegistrationDto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .password(this.encodePassword(dto.getPassword()))
                .role("ROLE_USER")
                .build();
        return userRepository.save(user);

    }
}
