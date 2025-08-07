<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%-- 메뉴버튼 권한 --%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property='principal.adminMenuAuthList' var="adminMenuAuthList"/>
<c:set var="adminMenuAuth" value=""/>
<c:forEach var="auth" items="${adminMenuAuthList}">
    <c:if test="${auth.menu.menuName eq '리그 관리'}">
        <c:set var="adminMenuAuth" value="${auth}"/>
    </c:if>
</c:forEach>

<script>
    const deleteLeagues = () => {
        if($(".checkItem:checked").length < 1){
            alert("최소 1개 이상 선택해야합니다.");
            return false;
        }

        if(confirm("삭제하시겠습니까?")) {
            let ids = [];
            $(".checkItem:checked").each(
                function(){
                    ids.push(this.value)
                });

            $.ajax({
                url: '/api/discord/multi-delete',
                type: 'DELETE',
                async: false,
                data: {
                    ids : ids
                },
                success: function (data) {
                    if(data) {
                        alert("삭제 처리되었습니다.");
                        location.reload();
                    }else{
                        alert("삭제 처리 중 오류가 발생하였습니다.")
                    }
                },
                error: function (request, status, error) {
                    console.error(error);
                    alert("오류가 발생하였습니다.");
                }
            });
        }
    }

    $(function () {
        $("#checkAll").on('click', function () {
            if ($("#checkAll").is(":checked")) {
                $(".checkItem").prop("checked", true);
            } else {
                $(".checkItem").prop("checked", false);
            }
        });

        $(".checkItem").on('click', function () {
            if (!$(this).is(":checked")) {
                $("#checkAll").prop("checked", false);
            }
        });
    });
</script>

<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">리그 관리</h1>
                <nav>
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                        <li class="breadcrumb-item">사이트 관리</li>
                        <li class="breadcrumb-item">디스코드 관리</li>
                        <li class="breadcrumb-item active">리그 관리</li>
                    </ol>
                </nav>
            </div>
        </div>

        <div class="container-fluid px-4">
            <div class="icon">
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">리그 관리</h4>
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
                                <label for="startDate" class="col-sm-2 col-form-label">리그 기간</label>
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
                    <i class="bi bi-record-circle-fill"></i><h4 class="card-title">리그 목록</h4>
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
                        <th scope="col">리그명</th>
                        <th scope="col">시작일</th>
                        <th scope="col">종료일</th>
                        <th scope="col">시간</th>
                        <th scope="col">상태</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty resultList}">
                            <c:forEach var="result" items="${resultList}" varStatus="status">
                                <tr>
                                    <td><input type="checkbox" class="form-check-input checkItem" name="boardMasterCheck" value="${result.id}"></td>
                                    <th scope="row">${pageInfo.totalCount - ((pageInfo.pageNumber-1) * pageInfo.pageOffset + status.index)}</th>
                                    <td><a href="/cms/discord/league/view/${result.id}">${result.leagueName}</a></td>
                                    <td>${result.startDate}</td>
                                    <td>${result.endDate}</td>
                                    <td>${result.leagueTime}</td>
                                    <td>
                                        <%
                                            java.util.Calendar cal = java.util.Calendar.getInstance();
                                            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
                                            cal.set(java.util.Calendar.MINUTE, 0);
                                            cal.set(java.util.Calendar.SECOND, 0);
                                            cal.set(java.util.Calendar.MILLISECOND, 0);
                                            pageContext.setAttribute("nowDt", cal.getTime());
                                        %>
                                        <jsp:useBean id="now" class="java.util.Date" />
                                        <fmt:parseDate var="startDt" value="${fn:replace(result.startDate,'-','')}" pattern="yyyyMMdd"/>
                                        <fmt:parseDate var="endDt" value="${fn:replace(result.endDate,'-','')}" pattern="yyyyMMdd"/>
                                        <c:choose>
                                            <c:when test="${nowDt < startDt}"><span class="btn btn-warning btn-mg">예정</span></c:when>
                                            <c:when test="${nowDt > endDt}"><span class="btn btn-secondary btn-mg">종료</span></c:when>
                                            <c:otherwise><span class="btn btn-success btn-mg">운영중</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr class="text-center">
                                <td colspan="8">조회된 데이터가 존재하지 않습니다.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
                <jsp:include page="/WEB-INF/jsp/common/commonPagenation.jsp"/>

                <div class="form-btn-set text-end">
                    <c:if test="${not empty adminMenuAuth and adminMenuAuth.isDelete}">
                        <button type="button" class="btn btn-danger btn-lg" onclick="deleteLeagues();">선택 삭제</button>
                    </c:if>
                    <c:if test="${not empty adminMenuAuth and adminMenuAuth.isInsert}">
                        <button type="button" class="btn btn-success btn-lg" onclick="location.href='./regist';">등록</button>
                    </c:if>
                </div>
            </div>
        </div>
    </main>
</div>