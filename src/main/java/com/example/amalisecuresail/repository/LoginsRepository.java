package com.example.amalisecuresail.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.amalisecuresail.entity.Logins;

@Repository
public interface LoginsRepository extends JpaRepository<Logins, Integer> {

}