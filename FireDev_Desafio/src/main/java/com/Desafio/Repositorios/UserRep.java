package com.Desafio.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Desafio.FireDevDesafioApplication.User;

@Repository
public interface UserRep extends JpaRepository<User, Long> {}

