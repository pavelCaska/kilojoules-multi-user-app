package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.User;
import com.pc.kilojoules.entity.UserProfile;
import com.pc.kilojoules.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfile fetchUserProfileByUser(User user) {
        return userProfileRepository.findUserProfileByUser(user).orElse(new UserProfile());
    }

    @Override
    public boolean existsUserProfileByUser(User user) {
        return userProfileRepository.existsUserProfileByUser(user);
    }

    @Override
    public void save(UserProfile profile) {
        userProfileRepository.save(profile);
    }

    @Override
    public boolean existsUserProfileByEmail(String email) {
        return userProfileRepository.existsUserProfileByEmail(email);
    }

    @Override
    public UserProfile createUserProfile(User user, UserProfile userProfile) {
        UserProfile newProfile = UserProfile.builder()
                .user(user)
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .email(userProfile.getEmail())
                .city(userProfile.getCity())
                .country(userProfile.getCountry())
                .build();
        return userProfileRepository.save(newProfile);

    }

    @Override
    public UserProfile updateUserProfile(UserProfile current, UserProfile userProfile) {
        current.setFirstName(userProfile.getFirstName());
        current.setLastName(userProfile.getLastName());
        current.setEmail(userProfile.getEmail());
        current.setCity(userProfile.getCity());
        current.setCountry(userProfile.getCountry());

        return userProfileRepository.save(current);
    }


}
