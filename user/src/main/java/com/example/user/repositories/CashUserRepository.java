package com.example.user.repositories;

import com.example.user.model.CashUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashUserRepository extends JpaRepository<CashUser, Long> {

    List<CashUser> findAll();

    List<CashUser> findByName(String name);

}
