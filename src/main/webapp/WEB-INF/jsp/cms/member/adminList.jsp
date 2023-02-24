<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">관리자 관리</h1>
                <nav>
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                        <li class="breadcrumb-item">시스템관리</li>
                        <li class="breadcrumb-item active">관리자 관리</li>
                    </ol>
                </nav>
            </div>
        </div>
        <%--<div class="container-fluid px-4">
            <h1 class="mt-4">관리자 관리</h1>
            <ol class="breadcrumb mb-4">
                <li class="breadcrumb-item active">Administrator management</li>
            </ol>
        </div>--%>
        <div class="container-fluid px-4">
            <div class="search-box">
                <div class="search-box-title">
                    <h4>검색 영역</h4>
                </div>
                <div class="search-box-body">
                    <form id="searchForm" name="searchForm" action="./admin-list">
                        <div class="row mb-3">
                            <label for="userId" class="col-sm-1 col-form-label">아이디</label>
                            <div class="col-sm-5">
                                <input type="text" class="form-control" id="userId" name="userId" value="${param.userId}" placeholder="관리자 아이디를 입력하세요." aria-label="관리자 아이디를 입력하세요.">
                            </div>
                            <label for="name" class="col-sm-1 col-form-label">성명</label>
                            <div class="col-sm-5">
                                <input type="text" class="form-control" id="name" name="name" value="${param.name}" placeholder="관리자 성명을 입력하세요." aria-label="관리자 성명을 입력하세요.">
                            </div>
                        </div>
                        <%--<div class="row mb-3">
                            <label for="userId" class="col-sm-2 col-form-label">아이디</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" id="userId">
                            </div>
                        </div>--%>
                        <div class="row mb-3">
                            <label for="gender" class="col-sm-1 col-form-label">성별</label>
                            <div class="col-sm-5">
                                <select class="form-select" id="gender" name="gender" aria-label="Default select example">
                                    <option value="" ${empty param.gender? 'selected':''}>선택</option>
                                    <option value="M" ${param.gender eq 'M'? 'selected':''}>남성</option>
                                    <option value="F" ${param.gender eq 'F'? 'selected':''}>여성</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-btn-set text-center">
                            <button type="submit" class="btn btn-primary">검색</button>
                            <button type="reset" class="btn btn-secondary">초기화</button>
                        </div>
                    </form>
                </div>
            </div>

            <%--<div class="row">
                <div class="col-lg-4">
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">검색 영역</h5>

                            <!-- Horizontal Form -->
                            <form id="searchForm" name="searchForm" action="./admin-list">
                                <div class="row mb-3">
                                    <label for="name" class="col-sm-2 col-form-label">성명</label>
                                    <div class="col-sm-10">
                                        <input type="text" class="form-control" id="name">
                                    </div>
                                </div>
                                <div class="row mb-3">
                                    <label for="userId" class="col-sm-2 col-form-label">아이디</label>
                                    <div class="col-sm-10">
                                        <input type="text" class="form-control" id="userId">
                                    </div>
                                </div>
                                <div class="row mb-3">
                                    <label for="gender" class="col-sm-2 col-form-label">성별</label>
                                    <div class="col-sm-10">
                                        <select class="form-select" id="gender" name="gender" aria-label="Default select example">
                                            <option selected="">선택</option>
                                            <option value="M">남성</option>
                                            <option value="F">여성</option>
                                        </select>
                                    </div>
                                </div>
                                &lt;%&ndash;<fieldset class="row mb-3">
                                    <legend class="col-form-label col-sm-2 pt-0">Radios</legend>
                                    <div class="col-sm-10">
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="gridRadios" id="gridRadios1" value="option1" checked="">
                                            <label class="form-check-label" for="gridRadios1">
                                                First radio
                                            </label>
                                        </div>
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="gridRadios" id="gridRadios2" value="option2">
                                            <label class="form-check-label" for="gridRadios2">
                                                Second radio
                                            </label>
                                        </div>
                                        <div class="form-check disabled">
                                            <input class="form-check-input" type="radio" name="gridRadios" id="gridRadios3" value="option3" disabled="">
                                            <label class="form-check-label" for="gridRadios3">
                                                Third disabled radio
                                            </label>
                                        </div>
                                    </div>
                                </fieldset>
                                <div class="row mb-3">
                                    <div class="col-sm-10 offset-sm-2">
                                        <div class="form-check">
                                            <input class="form-check-input" type="checkbox" id="gridCheck1">
                                            <label class="form-check-label" for="gridCheck1">
                                                Example checkbox
                                            </label>
                                        </div>
                                    </div>
                                </div>&ndash;%&gt;
                                <div class="text-center">
                                    <button type="submit" class="btn btn-primary">검색</button>
                                    <button type="reset" class="btn btn-secondary">초기화</button>
                                </div>
                            </form><!-- End Horizontal Form -->

                        </div>
                    </div>
                </div>
            </div>--%>

            <div class="icon">
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">관리자 목록</h4>
            </div>
            <table class="table">
                <thead>
                <tr>
                    <th scope="col">순번</th>
                    <th scope="col">성명</th>
                    <th scope="col">아이디</th>
                    <th scope="col">휴대번호</th>
                    <th scope="col">성별</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty resultList}">
                        <c:forEach var="result" items="${resultList}" varStatus="status">
                            <tr>
                                <th scope="row">${status.count}</th>
                                <td>${result.name}</td>
                                <td>${result.userId}</td>
                                <td>${result.phone}</td>
                                <td>${empty result.gender? '':result.gender eq 'M'? '남성':'여성'}</td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr class="text-center">
                            <td colspan="5">조회된 데이터가 존재하지 않습니다.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </main>
</div>