<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
    .tags {
        display: inline-block;
        height: 24px;
        line-height: 24px;
        position: relative;
        margin: 0 16px 8px 0;
        padding: 0 10px 0 12px;
        background: #de3f3e;
        -webkit-border-bottom-right-radius: 3px;
        border-bottom-right-radius: 3px;
        -webkit-border-top-right-radius: 3px;
        border-top-right-radius: 3px;
        -webkit-box-shadow: 0 1px 2px rgba(0,0,0,0.2);
        box-shadow: 0 1px 2px rgba(0,0,0,0.2);
        color: #fff;
        font-size: 12px;
        font-family: "Lucida Grande","Lucida Sans Unicode",Verdana,sans-serif;
        text-decoration: none;
        text-shadow: 0 1px 2px rgba(0,0,0,0.2);
        font-weight: bold;
    }

    .tags:before {
        content: "";
        position: absolute;
        top:0;
        left: -12px;
        width: 0;
        height: 0;
        border-color: transparent #de3f3e transparent transparent;
        border-style: solid;
        border-width: 12px 12px 12px 0;
    }

    .tags:after {
        content: "";
        position: absolute;
        top: 10px;
        left: 1px;
        float: left;
        width: 5px;
        height: 5px;
        -webkit-border-radius: 50%;
        border-radius: 50%;
        background: #fff;
        -webkit-box-shadow: -1px -1px 2px rgba(0,0,0,0.4);
        box-shadow: -1px -1px 2px rgba(0,0,0,0.4);
    }
</style>

<script>
    const refreshMembers = () => {
        if(confirm("동기화 하시겠습니까?")) {
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
                }
            });
        }else {
            return false;
        }
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
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">디스코드 관리</h4>
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
                                <label for="username" class="col-sm-2 col-form-label">닉네임</label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" id="username" name="username" value="${param.username}" placeholder="닉네임을 입력하세요." aria-label="닉네임을 입력하세요.">
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
                                    <td><a href="/cms/discord/member-view/${result.id}">${result.userId}</a></td>
                                    <td>${result.username}</td>
                                    <td>${result.globalName}</td>
                                    <td>
                                        <c:forEach var="mension" items="${result.discordUserMensionSet}" varStatus="index">
                                            <c:choose>
                                                <c:when test="${index.first}"><label class="tags">${mension.discordMention.roleName}</label></c:when>
                                                <c:otherwise>, <label class="tags">${mension.discordMention.roleName}</label></c:otherwise>
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
                    <button type="button" class="btn btn-danger btn-lg" onclick="deleteBoards();">선택 삭제</button>
                    <button type="button" class="btn btn-success btn-lg" onclick="refreshMembers();">가입자 동기화</button>
                </div>
            </div>
        </div>
    </main>
</div>