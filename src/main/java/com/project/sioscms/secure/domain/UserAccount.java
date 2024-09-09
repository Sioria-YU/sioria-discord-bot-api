package com.project.sioscms.secure.domain;

import com.project.sioscms.apps.account.domain.dto.AccountDto;
import com.project.sioscms.apps.account.domain.entity.Account;
import com.project.sioscms.apps.admin.domain.dto.AdminMenuAuthDto;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAccount extends User {
    private final Account account;
    private final List<AdminMenuAuthDto.Response> adminMenuAuthList;

    public UserAccount(Account account, List<AdminMenuAuthDto.Response> adminMenuAuthList) {
        super(account.getUserId(), account.getUserPassword(), List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name())));
        this.account = account;
        this.adminMenuAuthList = adminMenuAuthList;
    }

    public AccountDto.Response getAccountDto(){
        if(this.account == null){
            return null;
        }else{
            return this.account.toResponse();
        }
    }

    public List<AdminMenuAuthDto.Response> getAdminMenuAuthList(){
        return this.adminMenuAuthList;
    }
}
