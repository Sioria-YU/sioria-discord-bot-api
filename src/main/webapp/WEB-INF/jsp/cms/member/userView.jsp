<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">사용자 관리</h1>
                <nav>
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                        <li class="breadcrumb-item">시스템관리</li>
                        <li class="breadcrumb-item active">사용자 관리</li>
                    </ol>
                </nav>
            </div>
        </div>

        <div class="container-fluid px-4">
            <div class="icon">
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">사용자 등록</h4>
            </div>

            <div class="container-fluid px-4">
                <div class="col-lg-12 card">
                    <div class="card-body">
                        <form id="userRegistForm" name="userRegistForm" action="/cms/member/user-update" method="post" autocomplete="off" onsubmit="return formCheck();">
                            <div class="row mb-3">
                                <label for="userId" class="col-sm-1 col-form-label text-center">아이디</label>
                                <div class="col-sm-5">
                                    <input type="hidden" name="id" value="${result.id}"/>
                                    <input type="text" class="form-control" id="userId" name="userId" value="${result.userId}" aria-label="아이디" readonly/>
                                </div>
                                <label for="passwordChangeButton" class="col-sm-1 col-form-label text-center">비밀번호</label>
                                <div class="col-sm-5">
                                    <button type="button" class="btn btn-dark align-top" id="passwordChangeButton" onclick="">비밀번호 변경</button>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="name" class="col-sm-1 col-form-label text-center">성명</label>
                                <div class="col-sm-5">
                                    <input type="text" class="form-control" id="name" name="name" value="${result.name}" aria-label="성명" required/>
                                </div>
                                <label for="phone" class="col-sm-1 col-form-label text-center">연락처</label>
                                <div class="col-sm-5">
                                    <input type="text" class="form-control" id="phone" name="phone" value="${result.phone}" aria-label="연락처" required/>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="gender" class="col-sm-1 col-form-label text-center">성별</label>
                                <div class="col-sm-5">
                                    <select class="form-control" id="gender" name="gender" required>
                                        <option value="">선택</option>
                                        <option value="M" ${result.gender eq 'M'? 'selected':''}>남성</option>
                                        <option value="F" ${result.gender eq 'F'? 'selected':''}>여성</option>
                                    </select>
                                </div>
                                <label for="role" class="col-sm-1 col-form-label text-center">회원구분</label>
                                <div class="col-sm-5">
                                    <select class="form-control" id="role" name="role" readonly>
                                        <option value="USER">사용자</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-btn-set text-center">
                                <button type="submit" class="btn btn-primary">수정</button>
                                <a href="/cms/member/user-list" class="btn btn-secondary">취소</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<script>
    const formCheck = ()=>{
        return confirm("수정하시겠습니까?");
    }
</script>