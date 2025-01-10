<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- 메뉴버튼 권한 --%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property='principal.adminMenuAuthList' var="adminMenuAuthList"/>
<c:set var="adminMenuAuth" value=""/>
<c:forEach var="auth" items="${adminMenuAuthList}">
    <c:if test="${auth.menu.menuName eq '멘션 관리'}">
        <c:set var="adminMenuAuth" value="${auth}"/>
    </c:if>
</c:forEach>

<script>
    /* 기존 스크립트 재사용 */
    const refreshMentions = () => {
        if(confirm("동기화 하시겠습니까?")) {
            $.ajax({
                type: 'GET',
                url: '/api/discord/roles-refresh',
                async: false,
                success: function (data) {
                    if (data) {
                        alert("동기화가 완료되었습니다.");
                        location.reload();
                    } else {
                        alert("오류가 발생하였습니다.");
                        return false;
                    }
                },
                error: function (request, status, error) {
                    console.log(error);
                }
            });
        } else {
            return false;
        }
    };

    const changePageOffset = (cnt) => {
        $("#pageOffset").val(cnt);
        $("#searchForm").submit();
    };
</script>

<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">멘션 관리</h1>
                <nav>
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                        <li class="breadcrumb-item">디스코드 관리</li>
                        <li class="breadcrumb-item active">멘션 관리</li>
                    </ol>
                </nav>
            </div>
        </div>

        <div class="container-fluid px-4">
            <div class="icon">
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">멘션 관리</h4>
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
                                <label for="roleName" class="col-sm-2 col-form-label">권한명</label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" id="roleName" name="roleName" value="${param.roleName}" placeholder="권한명을 입력하세요.">
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
                    <i class="bi bi-record-circle-fill"></i><h4 class="card-title">멘션 목록</h4>
                </div>

                <c:if test="${not empty pageInfo}">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span class="badge bg-secondary">
                            <h6 style="margin-bottom: 3px;">
                            전체 <span class="badge bg-white text-secondary">${empty pageInfo.totalCount? 0:pageInfo.totalCount}</span> 건
                                <span class="badge bg-white text-secondary">${empty pageInfo.pageNumber? 1:pageInfo.pageNumber}</span>
                                / <span class="badge bg-white text-secondary">${empty pageInfo.totalPageSize? 1:pageInfo.totalPageSize}</span> 페이지
                            </h6>
                        </span>
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
                        <th scope="col">권한 아이디</th>
                        <th scope="col">권한명</th>
                        <th scope="col">멘션</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty resultList}">
                            <c:forEach var="mention" items="${resultList}" varStatus="status">
                                <tr>
                                    <th scope="row">${pageInfo.totalCount - ((pageInfo.pageNumber-1) * pageInfo.pageOffset + status.index)}</th>
                                    <td>${mention.roleId}</td>
                                    <td><a href="/cms/discord/mention/view/${mention.id}">${mention.roleName}</a></td>
                                    <td>${mention.mention}</td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr class="text-center">
                                <td colspan="4">조회된 데이터가 없습니다.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
                <jsp:include page="/WEB-INF/jsp/common/commonPagenation.jsp"/>

                <div class="form-btn-set text-end">
                    <c:if test="${not empty adminMenuAuth and adminMenuAuth.isDelete}">
                        <button type="button" class="btn btn-danger btn-lg" onclick="deleteMentions();">선택 삭제</button>
                    </c:if>
                    <button type="button" class="btn btn-success btn-lg" onclick="refreshMentions();">멘션 동기화</button>
                </div>
            </div>
        </div>
    </main>
</div>
