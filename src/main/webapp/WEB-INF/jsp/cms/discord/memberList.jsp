<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">가입자 관리</h1>
                <nav>
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                        <li class="breadcrumb-item">사이트 관리</li>
                        <li class="breadcrumb-item">디스코드 관리</li>
                        <li class="breadcrumb-item active">가입자 관리</li>
                    </ol>
                </nav>
            </div>
        </div>

        <div class="container-fluid px-4">
            <div class="icon">
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">디스코드 관리</h4>
            </div>

            <div class="container-fluid px-4">
                <div class="search-box">
                    <div class="search-box-title">
                        <h4>검색 영역</h4>
                    </div>
                    <div class="search-box-body">
                        <form id="searchForm" name="searchForm" action="./list">
                            <input type="hidden" id="pageNumber" name="pageNumber" value="${empty param.pageNumber? 1:param.pageNumber}">
                            <input type="hidden" id="pageOffset" name="pageOffset" value="${empty param.pageOffset? 10:param.pageOffset}">
                            <input type="hidden" id="pageSize" name="pageSize" value="${empty param.pageSize? 5:param.pageSize}">
                            <div class="row mb-3">
                                <label for="username" class="col-sm-2 col-form-label">닉네임</label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" id="username" name="username" value="${param.title}" placeholder="닉네임을 입력하세요." aria-label="닉네임을 입력하세요.">
                                </div>
                            </div>
                            <div class="form-btn-set text-center">
                                <button type="submit" class="btn btn-primary">검색</button>
                                <button type="reset" class="btn btn-secondary">초기화</button>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="icon">
                    <i class="bi bi-record-circle-fill"></i><h4 class="card-title">가입자 목록</h4>
                </div>

                <c:if test="${not empty pageInfo}">
                    <div>
                    <span class="badge bg-secondary">
                        <h6 style="margin-bottom: 3px;">
                        전체 <span class="badge bg-white text-secondary">${empty pageInfo.totalCount? 0:pageInfo.totalCount}</span> 건
                            <span class="badge bg-white text-secondary">${empty pageInfo.pageNumber? 1:pageInfo.pageNumber}</span>
                            / <span class="badge bg-white text-secondary">${empty pageInfo.totalPageSize? 1:pageInfo.totalPageSize}</span> 페이지
                        </h6>
                    </span>
                    </div>
                </c:if>
                <table class="table text-center">
                    <thead>
                    <tr>
                        <th><label for="checkAll"><input type="checkbox" class="form-check-input" id="checkAll"/></label></th>
                        <th scope="col">순번</th>
                        <th scope="col">아이디</th>
                        <th scope="col">닉네임</th>
                        <th scope="col">닉네임(전체)</th>
                        <th scope="col">역할</th>
                        <th scope="col">비고</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty resultList}">
                            <c:forEach var="result" items="${resultList}" varStatus="status">
                                <fmt:parseDate var="createdDateTime" value="${result.createdDateTime}" pattern="yyyy-MM-dd" type="both"/>
                                <tr>
                                    <td><input type="checkbox" class="form-check-input checkItem" name="boardMasterCheck" value="${result.id}"></td>
                                    <th scope="row">${pageInfo.totalCount - ((pageInfo.pageNumber-1) * pageInfo.pageOffset + status.index)}</th>
                                    <td><a href="/cms/discord/member-view/${result.id}">${result.userId}</a></td>
                                    <td><a href="/cms/discord/member-view/${result.id}">${result.username}</a></td>
                                    <td><a href="/cms/discord/member-view/${result.id}">${result.globalName}</a></td>
                                    <td>${result.discriminator}</td>
                                    <td>-</td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr class="text-center">
                                <td colspan="7">조회된 데이터가 존재하지 않습니다.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
                <jsp:include page="/WEB-INF/jsp/common/commonPagenation.jsp"/>

                <div class="form-btn-set text-end">
                    <button type="button" class="btn btn-dark btn-lg" onclick="">가입자 새로고침</button>
                    <button type="button" class="btn btn-danger btn-lg" onclick="deleteBoards();">선택 삭제</button>
                    <button type="button" class="btn btn-success btn-lg" onclick="location.href='./regist';">등록</button>
                </div>
            </div>
        </div>
    </main>
</div>