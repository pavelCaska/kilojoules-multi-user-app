package com.pc.kilojoules.repository;

import com.pc.kilojoules.entity.User;
import com.pc.kilojoules.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findUserProfileByUser(User user);
    boolean existsUserProfileByUser(User user);
    boolean existsUserProfileByEmail(String email);

}