package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.User;
import com.pc.kilojoules.entity.UserProfile;

public interface UserProfileService {

    UserProfile fetchUserProfileByUser(User user);

    boolean existsUserProfileByUser(User user);

    void save(UserProfile profile);

    boolean existsUserProfileByEmail(String email);

    UserProfile createUserProfile(User user, UserProfile userProfile);

    UserProfile updateUserProfile(UserProfile current, UserProfile userProfile);
}
