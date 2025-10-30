package com.fabrica.p6f5.springapp.repository;

import com.fabrica.p6f5.springapp.entity.User;
import com.fabrica.p6f5.springapp.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByUser(User user);
}



