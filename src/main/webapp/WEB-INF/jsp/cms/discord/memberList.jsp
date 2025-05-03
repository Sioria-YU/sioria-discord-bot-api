<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- 메뉴버튼 권한 --%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property='principal.adminMenuAuthList' var="adminMenuAuthList"/>
<c:set var="adminMenuAuth" value=""/>
<c:forEach var="auth" items="${adminMenuAuthList}">
    <c:if test="${auth.menu.menuName eq '가입자 관리'}">
        <c:set var="adminMenuAuth" value="${auth}"/>
    </c:if>
</c:forEach>

<script>
    const refreshMembers = () => {
        if(confirm("동기화 하시겠습니까?")) {
            showLoading();

            setTimeout(() => {
                $.ajax({
                    type: 'GET',
                    url: '/api/discord/member-refresh',
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
                    },
                    complete: function() { // AJAX 완료 후 로딩 숨기기
                        hideLoading();
                    }
                });
            }, 100); // 약간의 딜레이를 줌
        }else {
            return false;
        }
    }
   <%--
    const refreshMembersNickname = () => {
        if(confirm("동기화 하시겠습니까?")) {
            showLoading();

            setTimeout(() => {
                $.ajax({
                    type: 'GET',
                    url: '/cms/api/discord/member/refresh-nickname',
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
                    },
                    complete: function() { // AJAX 완료 후 로딩 숨기기
                        hideLoading();
                    }
                });
            }, 100); // 약간의 딜레이를 줌
        }else {
            return false;
        }
    }
    --%>

    const changePageOffset = (cnt) =>{
        $("#pageOffset").val(cnt);
        $("#searchForm").submit();
    }
</script>

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
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">가입자 관리</h4>
            </div>

            <div class="container-fluid px-4">
                <div class="search-box">
                    <div class="search-box-title">
                        <h4>검색 영역</h4>
                    </div>
                    <div class="search-box-body">
                        <form id="searchForm" name="searchForm" action="./member-list">
                            <input type="hidden" id="pageNumber" name="pageNumber" value="${empty param.pageNumber? 1:param.pageNumber}">
                            <input type="hidden" id="pageOffset" name="pageOffset" value="${empty param.pageOffset? 10:param.pageOffset}">
                            <input type="hidden" id="pageSize" name="pageSize" value="${empty param.pageSize? 5:param.pageSize}">
                            <div class="row mb-3">
                                <label for="username" class="col-sm-2 col-form-label">닉네임(전부다)</label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" id="username" name="username" value="${param.username}" placeholder="닉네임을 입력하세요." aria-label="닉네임을 입력하세요.">
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="discordUserMension" class="col-sm-2 col-form-label">역할</label>
                                <div class="col-sm-6">
                                    <select class="form-select" id="discordUserMension" name="discordUserMension">
                                        <option value="">선택</option>
                                        <c:forEach var="mention" items="${discordMentionList}">
                                            <option value="${mention.roleId}" ${param.discordUserMension eq mention.roleId? 'selected':''}>${mention.roleName}</option>
                                        </c:forEach>
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

                <div class="icon">
                    <i class="bi bi-record-circle-fill"></i><h4 class="card-title">가입자 목록</h4>
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
                        <th><label for="checkAll"><input type="checkbox" class="form-check-input" id="checkAll"/></label></th>
                        <th scope="col">순번</th>
                        <th scope="col">아이디</th>
                        <th scope="col">닉네임</th>
                        <th scope="col">닉네임(길드)</th>
                        <th scope="col">닉네임(전체)</th>
                        <th scope="col">역할</th>
                        <th scope="col">멘션</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty resultList}">
                            <c:forEach var="result" items="${resultList}" varStatus="status">
                                <tr>
                                    <td><input type="checkbox" class="form-check-input checkItem" name="boardMasterCheck" value="${result.id}"></td>
                                    <th scope="row">${pageInfo.totalCount - ((pageInfo.pageNumber-1) * pageInfo.pageOffset + status.index)}</th>
<%--                                    <td><a href="/cms/discord/member-view/${result.id}">${result.userId}</a></td>--%>
                                    <td>${result.userId}</td>
                                    <td>${result.username}</td>
                                    <td>${result.nickname}</td>
                                    <td>${result.globalName}</td>
                                    <td>
                                        <c:forEach var="mension" items="${result.discordUserMensionSet}" varStatus="index">
                                            <c:choose>
                                                <c:when test="${index.first}"><label class="tags">${mension.discordMention.roleName}</label></c:when>
                                                <c:otherwise>&nbsp;<label class="tags">${mension.discordMention.roleName}</label></c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </td>
                                    <td>${result.userMension}</td>
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
                    <c:if test="${not empty adminMenuAuth and adminMenuAuth.isDelete}">
                        <button type="button" class="btn btn-danger btn-lg" onclick="deleteBoards();">선택 삭제</button>
                    </c:if>
                    <button type="button" class="btn btn-success btn-lg" onclick="refreshMembers();">가입자 동기화</button>
<%--                    <button type="button" class="btn btn-success btn-lg" onclick="refreshMembersNickname();">닉네임 동기화</button>--%>
                </div>
            </div>
        </div>
    </main>
</div>