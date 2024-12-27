package com.leedae.boardandadmin.service;


import com.leedae.boardandadmin.dto.UserAccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAccountManagementService {


    public List<UserAccountDto> getUserAccounts() {
        return List.of();
    }

    public UserAccountDto getUserAccount(String userId){
        return null;
    }

    public void deleteUserAccount(String userId){

    }
}
