package com.example.amalisecuresail.service.impl;


import com.example.amalisecuresail.repository.UserAccountRepository;
import com.example.amalisecuresail.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserAccountServiceImp implements UserAccountService {
    private final UserAccountRepository userAccountRepository;

}
