package com.example.user.repositories;

import com.example.user.dtos.CashUserDTO;
import com.example.user.model.CashUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CashUserRepository extends JpaRepository<CashUser, Long> {
    List<CashUser> findAll();
    Optional<CashUser> findByName(String name);

}
