package com.honsoft.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honsoft.repository.RoleRepository;

import com.honsoft.entity.Role;

@RestController
@RequestMapping("/api/v1")
public class RoleRestController {

    private final RoleRepository roleRepository;

    public RoleRestController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping("roles")
    public List<Role> list() {
        return roleRepository.findAll();
    }
}