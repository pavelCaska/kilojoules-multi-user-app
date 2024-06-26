package com.pc.kilojoules.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    private Long userAccountId;

    @OneToOne
    @JoinColumn(name = "user_account_id")
    @MapsId
    private User user;

    @Size(max = 12)
    private String firstName;
    @Size(max = 12)
    private String lastName;
    @Size(max = 12)
    private String city;
    @Size(max = 12)
    private String country;

    @Column(nullable = true, unique = true)
    @Email(regexp = ".+@.+\\..{2,}")
    private String email;

    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    @UpdateTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

}
