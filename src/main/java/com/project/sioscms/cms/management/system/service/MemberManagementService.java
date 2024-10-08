package com.project.sioscms.cms.management.system.service;

import com.project.sioscms.apps.account.domain.dto.AccountDto;
import com.project.sioscms.apps.account.domain.entity.Account;
import com.project.sioscms.apps.account.domain.repository.AccountRepository;
import com.project.sioscms.apps.account.mapper.AccountMapper;
import com.project.sioscms.apps.admin.domain.entity.AdminAuth;
import com.project.sioscms.apps.admin.domain.repository.AdminAuthRepository;
import com.project.sioscms.cms.management.system.domain.dto.MemberSearchDto;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberManagementService extends EgovAbstractServiceImpl {
    private final AccountRepository accountRepository;
    private final AdminAuthRepository adminAuthRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //region ADMIN
    /**
     * 관리자 목록 조회
     * @param requestDto: userId, name, gender
     * @return SiosPage<AccountDto.Response>
     */
    public SiosPage<AccountDto.Response> getAdminList(MemberSearchDto requestDto) throws Exception {

        //List 동적쿼리 조건생성
        ChangSolJpaRestriction restriction = new ChangSolJpaRestriction();  //기본 값 AND 조건으로 적용됨.
        restriction.equals("isDelete", false);
        restriction.equals("role", Account.Role_Type.ADMIN);

        //검색 조건 추가
        if(!ObjectUtils.isEmpty(requestDto.getUserId())){
            restriction.equals("userId", requestDto.getUserId());
        }
        if(!ObjectUtils.isEmpty(requestDto.getName())){
            restriction.equals("name", requestDto.getName());
        }
        if(!ObjectUtils.isEmpty(requestDto.getGender())){
            restriction.equals("gender", requestDto.getGender());
        }

        return new SiosPage<>(accountRepository.findAll(restriction.toSpecification()
                , requestDto.toPageableWithSortedByCreatedDateTime(Sort.Direction.DESC)).map(Account::toResponse), requestDto.getPageSize());
    }

    /**
     * 관리자 계정 조회
     * @param id 계정 PK
     * @return AccountDto.Response
     * @throws Exception
     */
    public AccountDto.Response getAdmin(long id) throws Exception{
        Account account = (Account) accountRepository.findById(id).orElseThrow(NullPointerException::new);
        if(account.getIsDelete() || !Account.Role_Type.ADMIN.equals(account.getRole())){
            return null;
        }else {
            return account.toResponse();
        }
    }

    /**
     * 관리자 등록
     * @param dto :AccountDto.Request
     * @return AccountDto.Response
     */
    @Transactional
    public AccountDto.Response saveAdmin(AccountDto.Request dto){
        if(accountRepository.countAccountByUserId(dto.getUserId()) > 0){
            log.error("중복 아이디 발생!!!");
            return null;
        }

        if(dto.getUserPassword() != null){
            dto.setUserPassword(passwordEncoder.encode(dto.getUserPassword()));
        }

        dto.setState("T");
        Account account = AccountMapper.mapper.toEntity(dto);

        if(account != null){
            accountRepository.save(account);
            return account.toResponse();
        }else{
            log.error("관리자 등록 데이터 오류 발생!!!");
            return null;
        }
    }

    /**
     * 관리자 수정
     * @param dto :AccountDto.Request
     * @return AccountDto.Response
     */
    @Transactional
    public AccountDto.Response modifyAdmin(AccountDto.Request dto){
        Account account = accountRepository.findById(dto.getId()).orElse(null);

        if(account != null){
            account.setName(dto.getName());
            account.setPhone(dto.getPhone());
            account.setGender(dto.getGender());

            AdminAuth adminAuth = adminAuthRepository.findById(dto.getAdminAuthId()).orElse(null);
            if(adminAuth != null) {
                account.setAdminAuth(adminAuth);
            }
            return account.toResponse();
        }else{
            log.error("관리자 수정 - 회원 데이터 조회 불가!!!");
            return null;
        }
    }
    //endregion

    //region USER
    /**
     * 사용자 목록 조회
     * @param requestDto : userId, name, gender
     * @return SiosPage<AccountDto.Response>
     */
    public SiosPage<AccountDto.Response> getUserList(MemberSearchDto requestDto) throws Exception{
        ChangSolJpaRestriction restriction = new ChangSolJpaRestriction();
        restriction.equals("isDelete", false);
        restriction.equals("role", Account.Role_Type.USER);

        //검색 조건 추가
        if(!ObjectUtils.isEmpty(requestDto.getUserId())){
            restriction.equals("userId", requestDto.getUserId());
        }
        if(!ObjectUtils.isEmpty(requestDto.getName())){
            restriction.equals("name", requestDto.getName());
        }
        if(!ObjectUtils.isEmpty(requestDto.getGender())){
            restriction.equals("gender", requestDto.getGender());
        }

        return new SiosPage<>(accountRepository.findAll(restriction.toSpecification(), requestDto.toPageableWithSortedByCreatedDateTime(Sort.Direction.DESC)).map(Account::toResponse));
    }

    public AccountDto.Response getUser(long id) throws Exception{
        Account account = (Account) accountRepository.findById(id).orElseThrow(NullPointerException::new);
        if(account.getIsDelete() || !Account.Role_Type.USER.equals(account.getRole())){
            return null;
        }else {
            return account.toResponse();
        }
    }

    /**
     * 사용자 등록
     * @param dto :AccountDto.Request
     * @return AccountDto.Response
     */
    @Transactional
    public AccountDto.Response saveUser(AccountDto.Request dto){
        if(accountRepository.countAccountByUserId(dto.getUserId()) > 0){
            log.error("중복 아이디 발생!!!");
            return null;
        }

        if(dto.getUserPassword() != null){
            dto.setUserPassword(passwordEncoder.encode(dto.getUserPassword()));
        }

        dto.setState("T");
        Account account = AccountMapper.mapper.toEntity(dto);

        if(account != null){
            accountRepository.save(account);
            return account.toResponse();
        }else{
            log.error("사용자 등록 데이터 오류 발생!!!");
            return null;
        }
    }

    /**
     * 사용자 수정
     * @param dto :AccountDto.Request
     * @return AccountDto.Response
     */
    @Transactional
    public AccountDto.Response modifyUser(AccountDto.Request dto){
        Account account = accountRepository.findById(dto.getId()).orElse(null);

        if(account != null){
            account.setName(dto.getName());
            account.setPhone(dto.getPhone());
            account.setGender(dto.getGender());
            return account.toResponse();
        }else{
            log.error("사용자 수정 - 회원 데이터 조회 불가!!!");
            return null;
        }
    }
    //endregion
}
