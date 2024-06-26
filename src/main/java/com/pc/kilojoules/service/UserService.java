package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.User;
import com.pc.kilojoules.model.UserRegistrationDto;
import com.pc.kilojoules.model.UserUserProfileDto;

import java.util.List;

public interface UserService {

    User saveUser(User user);

    String encodePassword(String pwd);

    User fetchUserByUsername(String username);

    User registerNewUser(String username, String password);

    boolean existsUserByUsername(String username);

    void deleteUser(Long userId);

    User getCurrentUser();

    List<UserUserProfileDto> fetchCombinedUserData();

    List<UserUserProfileDto> fetchCombinedAdminData();

    User createUser(UserRegistrationDto dto);
}
