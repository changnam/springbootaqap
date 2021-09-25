package com.honsoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honsoft.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
