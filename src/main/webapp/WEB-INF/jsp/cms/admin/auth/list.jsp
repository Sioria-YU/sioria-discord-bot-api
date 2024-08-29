<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<script>
    $(function() {
        //등록 팝업 초기화
        $("#adminAuthRegistModal").on('show.bs.modal', function () {
            $("#modalName").val('');
            $("#notice").val('');
        });
    });

    const formCheck = () => {
        if($("#modalName").val() === ''){
            alert("권한명을 입력하세요.");
            $("#modalName").focus();
            return false;
        }

        if($("#notice").val() === ''){
            alert("설명을 입력하세요.");
            $("#notice").focus();
            return false;
        }

        $("#adminAuthForm").submit();
    }

    const updateFormCheck = () => {
        if($("#modalModifyAuthId").val() === ''){
            alert("선택한 권한이 잘못되었습니다.\n잠시 후 다시 시도해 주세요.");
            location.reload();
            return false;
        }

        if($("#modalModifyName").val() === ''){
            alert("권한명을 입력하세요.");
            $("#modalModifyName").focus();
            return false;
        }

        if($("#modalModifyNotice").val() === ''){
            alert("설명을 입력하세요.");
            $("#modalModifyNotice").focus();
            return false;
        }

        $("#adminAuthModifyForm").submit();
    }

    const getAdminAuth = (id) => {
        $.ajax({
            url: '/cms/api/admin-auth/' + id,
            type: 'GET',
            async: false,
            success: function (data) {
                if (!!data) {
                    $("#modalModifyAuthId").val(id);
                    $("#modalModifyName").val(data.name);
                    $("#modalModifyNotice").val(data.notice);
                } else {
                    alert("데이터 조회 실패");
                }
            },
            error: function (request, status, error) {
                console.error(error);
                alert("오류가 발생하였습니다.");
            }
        });
    }
</script>

<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">관리자 권한 관리</h1>
                <nav>
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                        <li class="breadcrumb-item">시스템관리</li>
                        <li class="breadcrumb-item active">관리자 권한 관리</li>
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
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">관리자 권한 관리 목록</h4>
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
                    <th scope="col" style="width: 80px">순번</th>
                    <th scope="col" style="width: 20%">권한명</th>
                    <th scope="col" style="width: 20%">설명</th>
                    <th scope="col">작성일</th>
                    <th scope="col" style="width: 100px">수정</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty resultList}">
                        <c:forEach var="result" items="${resultList}" varStatus="status">
                            <fmt:parseDate var="createdDateTime" value="${result.createdDateTime}" pattern="yyyy-MM-dd" type="both"/>
                            <tr>
                                <th scope="row">${pageInfo.totalCount - ((pageInfo.pageNumber-1) * pageInfo.pageOffset + status.index)}</th>
                                <td><a href="/cms/admin-menu-auth/view/${result.id}">${result.name}</a></td>
                                <td>${result.notice}</td>
                                <td><fmt:formatDate value="${createdDateTime}" pattern="yyyy-MM-dd"/></td>
                                <td>
                                    <button type="button" class="btn btn-secondary btn-mg" onclick="getAdminAuth(${result.id});" data-bs-toggle="modal" data-bs-target="#adminAuthModifyModal">수정</button>
                                </td>
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

            <div class="form-btn-set text-end">
                <button type="button" class="btn btn-success btn-lg" data-bs-toggle="modal" data-bs-target="#adminAuthRegistModal">등록</button>
            </div>
        </div>

        <!-- Modal -->
        <div class="modal fade" id="adminAuthRegistModal" tabindex="-1" aria-labelledby="adminAuthRegistModalTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="adminAuthRegistModalTitle">관리자 권한 등록</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="adminAuthForm" name="adminAuthForm" method="post" action="./save">
                            <div class="row mb-3">
                                <label for="modalName" class="col-sm-3 col-form-label">권한명</label>
                                <div class="col-sm-7">
                                    <input type="text" class="form-control-small" id="modalName" name="name" value="" placeholder="권한명을 입력하세요." aria-label="권한명을 입력하세요." maxlength="100">
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="notice" class="col-sm-3 col-form-label">설명</label>
                                <div class="col-sm-7">
                                    <textarea class="textarea form-control" id="notice" name="notice" ></textarea>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                        <button type="button" class="btn btn-success" onclick="formCheck();">등록</button>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="adminAuthModifyModal" tabindex="-1" aria-labelledby="adminAuthModifyModalTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="adminAuthModifyModalTitle">관리자 권한 수정</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="adminAuthModifyForm" name="adminAuthModifyForm" method="post" action="./update">
                            <input type="hidden" id="modalModifyAuthId" name="id" value=""/>
                            <div class="row mb-3">
                                <label for="modalModifyName" class="col-sm-3 col-form-label">권한명</label>
                                <div class="col-sm-7">
                                    <input type="text" class="form-control-small" id="modalModifyName" name="name" value="" placeholder="권한명을 입력하세요." aria-label="권한명을 입력하세요." maxlength="100">
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="modalModifyNotice" class="col-sm-3 col-form-label">설명</label>
                                <div class="col-sm-7">
                                    <textarea class="textarea form-control" id="modalModifyNotice" name="notice" ></textarea>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                        <button type="button" class="btn btn-success" onclick="updateFormCheck();">수정</button>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>