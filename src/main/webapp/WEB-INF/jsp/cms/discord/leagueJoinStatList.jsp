<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script>
    const changePageOffset = (cnt) =>{
        $("#pageOffset").val(cnt);
        $("#searchForm").submit();
    }
</script>

<div id="layoutSidenav_content">
<main>
    <div class="container-fluid px-4">
        <div class="pagetitle">
            <h1 class="mt-4">참여 현황</h1>
            <nav>
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                    <li class="breadcrumb-item">사이트 관리</li>
                    <li class="breadcrumb-item">디스코드 관리</li>
                    <li class="breadcrumb-item active">참여 현황</li>
                </ol>
            </nav>
        </div>
    </div>

    <div class="container-fluid px-4">
        <div class="icon">
            <i class="bi bi-record-circle-fill"></i><h4 class="card-title">참여 현황</h4>
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
                                <label for="leagueName" class="col-sm-2 col-form-label">리그명</label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" id="leagueName" name="leagueName" value="${param.leagueName}" placeholder="리그명을 입력하세요." aria-label="리그명을 입력하세요.">
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="startDate" class="col-sm-2 col-form-label">경기 기간</label>
                                <div class="col-sm-3">
                                    <input type="date" class="form-control" id="startDate" name="startDate" value="${param.startDate}" placeholder="시작일을 선택하세요." aria-label="시작일을 선택하세요.">
                                </div>
                                <div class="col-sm-3">
                                    <input type="date" class="form-control" id="endDate" name="endDate" value="${param.endDate}" placeholder="종료일을 선택하세요." aria-label="종료일을 선택하세요.">
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
                    <i class="bi bi-record-circle-fill"></i><h4 class="card-title">참여 현황 목록</h4>
                </div>

                <c:if test="${not empty pageInfo}">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <!-- 왼쪽 영역 -->
                        <span class="badge bg-secondary">
                            <h6 style="margin-bottom: 3px;">
                            전체 <span class="badge bg-white text-secondary">${empty pageInfo.totalCount? 0:pageInfo.totalCount}</span> 건
                                <span class="badge bg-white text-secondary">${empty pageInfo.pageNumber? 1:pageInfo.pageNumber}</span>
                                / <span class="badge bg-white text-secondary">${empty pageInfo.totalPageSize? 1:pageInfo.totalPageSize}</span> 페이지
                            </h6>
                        </span>
                        <!-- 오른쪽 영역 -->
                        <div>
                            <select id="selectPageOffset" onchange="changePageOffset(this.value);">
                                <option value="5" ${param.pageOffset eq 5? 'selected':''}>5개씩</option>
                                <option value="10" ${empty param.pageOffset or param.pageOffset eq 10? 'selected':''}>10개씩</option>
                                <option value="20" ${param.pageOffset eq 20? 'selected':''}>20개씩</option>
                                <option value="50" ${param.pageOffset eq 50? 'selected':''}>50개씩</option>
                                <option value="100" ${param.pageOffset eq 100? 'selected':''}>100개씩</option>
                            </select>
                        </div>
                    </div>
                </c:if>
                <table class="table text-center">
                    <thead>
                    <tr>
                        <th scope="col">순번</th>
                        <th scope="col">리그명</th>
                        <th scope="col">트랙명</th>
                        <th scope="col">트랙데이</th>
                        <th scope="col">마감</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty resultList}">
                            <c:forEach var="result" items="${resultList}" varStatus="status">
                                <tr>
                                    <th scope="row">${pageInfo.totalCount - ((pageInfo.pageNumber-1) * pageInfo.pageOffset + status.index)}</th>
                                    <td>${result.league.leagueName}</td>
                                    <td><a href="/cms/discord/league-join-stat/view/${result.id}">${result.trackCode.codeLabel}</a></td>
                                    <td>${result.trackDate}</td>
                                    <td>${fn:toUpperCase(result.isColsed)}</td>
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
                <jsp:include page="/WEB-INF/jsp/common/commonPagenation.jsp"/>

            </div>
        </div>
    </main>
</div>