package com.pc.kilojoules.repository;

import com.pc.kilojoules.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, PagingAndSortingRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsUserByUsername(String username);

    Optional<User> findUserById(Long userId);

    @Query(value = "SELECT u.id AS userId, " +
            "u.created_at AS userCreatedAt, " +
            "u.updated_at AS userUpdatedAt, " +
            "u.username AS username, " +
            "up.first_name AS firstName, " +
            "up.last_name AS lastName, " +
            "up.email AS email, " +
            "up.city AS city, " +
            "up.country AS country, " +
            "up.created_at AS profileCreatedAt, " +
            "up.updated_at AS profileUpdatedAt " +
            "FROM users u " +
            "LEFT OUTER JOIN user_profiles up ON u.id = up.user_account_id " +
            "WHERE u.role = 'ROLE_USER'",
            nativeQuery = true)
    List<Object[]> fetchCombinedUserData();

    @Query(value = "SELECT u.id AS userId, " +
            "u.created_at AS userCreatedAt, " +
            "u.updated_at AS userUpdatedAt, " +
            "u.username AS username, " +
            "up.first_name AS firstName, " +
            "up.last_name AS lastName, " +
            "up.email AS email, " +
            "up.city AS city, " +
            "up.country AS country, " +
            "up.created_at AS profileCreatedAt, " +
            "up.updated_at AS profileUpdatedAt " +
            "FROM users u " +
            "LEFT OUTER JOIN user_profiles up ON u.id = up.user_account_id " +
            "WHERE u.role = 'ROLE_ADMIN'",
            nativeQuery = true)
    List<Object[]> fetchCombinedAdminData();

}
