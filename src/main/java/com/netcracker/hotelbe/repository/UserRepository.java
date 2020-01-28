package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Override
    Optional<User> findById(Long id);


    @Query(value = "SELECT u.id, u.login, u.password, u.user_role, u.first_name, u.last_name, u.email, u.phone_number, u.points" +
            " FROM USERS u",
            nativeQuery = true)
    List<User> findAllNative();

    Optional<User> findByLogin(String login);
}
