<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">관리자 메뉴 권한 관리</h1>
                <nav>
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                        <li class="breadcrumb-item">시스템관리</li>
                        <li class="breadcrumb-item active">관리자 메뉴 권한 관리</li>
                    </ol>
                </nav>
            </div>
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
                            <label for="name" class="col-sm-2 col-form-label">권한명</label>
                            <div class="col-sm-3">
                                <input type="text" class="form-control" id="name" name="name" value="${param.codeGroupId}" placeholder="권한명을 입력하세요." aria-label="권한명을 입력하세요.">
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
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">관리자 메뉴 권한 관리 목록</h4>
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
                    <th scope="col">순번</th>
                    <th scope="col">권한명</th>
                    <th scope="col">설명</th>
                    <th scope="col">작성일</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty resultList}">
                        <c:forEach var="result" items="${resultList}" varStatus="status">
                            <fmt:parseDate var="createdDateTime" value="${result.createdDateTime}" pattern="yyyy-MM-dd" type="both"/>
                            <tr>
                                <th scope="row">${pageInfo.totalCount - ((pageInfo.pageNumber-1) * pageInfo.pageOffset + status.index)}</th>
                                <td><a href="/cms/code/code-group/view/${result.codeGroupId}">${result.codeGroupId}</a></td>
                                <td>${result.codeGroupNote}</td>
                                <td><fmt:formatDate value="${createdDateTime}" pattern="yyyy-MM-dd"/></td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr class="text-center">
                            <td colspan="4">조회된 데이터가 존재하지 않습니다.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
            <jsp:include page="/WEB-INF/jsp/common/commonPagenation.jsp"/>
        </div>
    </main>
</div>