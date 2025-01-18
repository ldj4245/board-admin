package com.leedae.boardandadmin.service;

import com.leedae.boardandadmin.domain.AdminAccount;
import com.leedae.boardandadmin.domain.constant.RoleType;
import com.leedae.boardandadmin.dto.AdminAccountDto;
import com.leedae.boardandadmin.repository.AdminAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminAccountService {

    private final AdminAccountRepository adminAccountRepository;

    @Transactional(readOnly = true)
    public Optional<AdminAccountDto> searchUser(String username){
        return adminAccountRepository.findById(username)
                .map(AdminAccountDto::from);
    }

    public AdminAccountDto saveUser(String username, String password, Set<RoleType> roleTypes, String email, String nickname, String memo){
        return AdminAccountDto.from(adminAccountRepository.save(AdminAccount.of(username, password, roleTypes, email, nickname, memo,username)));

    }

    @Transactional(readOnly = true)
    public List<AdminAccountDto> users(){
        return adminAccountRepository.findAll().stream()
                .map(AdminAccountDto::from)
                .toList();
    }

    public void deleteUser(String username){
        adminAccountRepository.deleteById(username);

    }


}
