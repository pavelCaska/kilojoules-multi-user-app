package com.pc.kilojoules.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUserProfileDto {

    private Long userId;
    private Date userCreatedAt;
    private Date userUpdatedAt;
    private String username;

    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String country;
    private Date profileCreatedAt;
    private Date profileUpdatedAt;

}
