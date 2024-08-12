<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script>
    const deleteList = () =>{
        alert("미구현");
        return false;
    }

    const leagueSelect = () => {


        $("#modalLeagueNameSeletor").empty();
        $("#modalLeagueNameSeletor").append($("<option value='1'>11111111111</option>"));
        $("#modalLeagueNameSeletor").append($("<option value='2'>22222222222</option>"));
        $("#modalLeagueNameSeletor").show();
    }

    const discordMemberSelect = () => {
        $("#modalDiscordMemberNameSelector").hide();

        $.ajax({
            url: '/cms/api/discord/member/list',
            type: 'GET',
            async: false,
            data: {
                username: $("#modalDiscordMemberName").val()
            },
            success: function (data) {
                if (!!data) {
                    $("#modalDiscordMemberNameSelector").empty();
                    for(let item of data){
                        let nickName = !!item.nickname? item.nickname : (!!item.globalName? item.globalName : item.username);
                        $("#modalDiscordMemberNameSelector").append($("<option value='" + item.id + "'>" + nickName + "</option>"));
                    }
                    $("#modalDiscordMemberNameSelector").show();
                } else {
                    $("#modalDiscordMemberNameSelector").empty();
                    $("#modalDiscordMemberNameSelector").append($("<option value=''>조회된 데이터가 없습니다.</option>"));
                    $("#modalDiscordMemberNameSelector").show();
                }
            },
            error: function (request, status, error) {
                console.error(error);
                alert("오류가 발생하였습니다.");
            }
        });


    }

    const formCheck = () => {

    }
</script>

<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">패널티 현황 목록</h1>
                <nav>
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                        <li class="breadcrumb-item">사이트 관리</li>
                        <li class="breadcrumb-item">디스코드 관리</li>
                        <li class="breadcrumb-item active">패널티 현황 관리</li>
                    </ol>
                </nav>
            </div>
        </div>

        <div class="container-fluid px-4">
            <div class="icon">
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">패널티 현황 관리</h4>
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
                                <label for="username" class="col-sm-2 col-form-label">사용자명</label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" id="username" name="username" value="${param.username}" placeholder="사용자명을 입력하세요." aria-label="유저명을 입력하세요.">
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="startDate" class="col-sm-2 col-form-label">기간</label>
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
                    <i class="bi bi-record-circle-fill"></i><h4 class="card-title">패널티 현황 목록</h4>
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
                        <th scope="col">사용자명</th>
                        <th scope="col">패널티구분</th>
                        <th scope="col">패널티사유</th>
                        <th scope="col">적용일</th>
                        <th scope="col">누적 횟수</th>
                        <th scope="col">버튼</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td><input type="checkbox" class="form-check-input checkItem" name="boardMasterCheck" value="${result.id}"></td>
                        <th scope="row">1</th>
                        <td>Re그</td>
                        <td>겜북</td>
                        <td>2주일 참여금지</td>
                        <td>과도한 추월 시도 및 충돌 2번째</td>
                        <td>2024-08-04</td>
                        <td>2</td>
                        <td>
                            <button type="button" class="btn btn-secondary btn-mg" onclick="">수정</button>
                            <button type="button" class="btn btn-danger btn-mg" onclick="">삭제</button>
                        </td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" class="form-check-input checkItem" name="boardMasterCheck" value="${result.id}"></td>
                        <th scope="row">1</th>
                        <td>Re그</td>
                        <td>겜북</td>
                        <td>1주일 참여금지</td>
                        <td>과도한 추월 시도 및 충돌</td>
                        <td>2024-07-02</td>
                        <td>1</td>
                        <td>
                            <button type="button" class="btn btn-secondary btn-mg" onclick="">수정</button>
                            <button type="button" class="btn btn-danger btn-mg" onclick="">삭제</button>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <jsp:include page="/WEB-INF/jsp/common/commonPagenation.jsp"/>

                <div class="form-btn-set text-end">
                    <button type="button" class="btn btn-danger btn-lg" onclick="deleteList();">선택 삭제</button>
                    <button type="button" class="btn btn-success btn-lg" data-bs-toggle="modal" data-bs-target="#penaltyModal">등록</button>
                </div>
            </div>
        </div>
        <!-- Modal -->
        <div class="modal fade" id="penaltyModal" tabindex="-1" aria-labelledby="penaltyModalTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <i class="bi bi-record-circle-fill"></i>&nbsp;<h5 class="modal-title" id="penaltyModalTitle">패널티 등록</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="penaltyForm" name="penaltyForm" method="post" action="./save">
                            <div class="row mb-3">
                                <label for="modalLeagueName" class="col-sm-3 col-form-label">리그명</label>
                                <div class="col-sm-7">
                                    <input type="text" class="form-control-small" id="modalLeagueName" name="leagueName" value="" placeholder="리그명을 입력하세요." aria-label="리그명을 입력하세요." maxlength="100">
                                    <button type="button" class="btn btn-secondary btn-sm" onclick="leagueSelect();">검색</button>
                                    <br>
                                    <select class="form-control-small" id="modalLeagueNameSeletor" name="modalLeagueNameSeletor" style="display: none"></select>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="modalDiscordMemberName" class="col-sm-3 col-form-label">사용자명</label>
                                <div class="col-sm-7">
                                    <input type="text" class="form-control-small" id="modalDiscordMemberName" name="username" value="" placeholder="사용자명을 입력하세요." aria-label="사용자명을 입력하세요." maxlength="100">
                                    <button type="button" class="btn btn-secondary btn-sm" onclick="discordMemberSelect();">검색</button>
                                    <br>
                                    <select class="form-control-small" id="modalDiscordMemberNameSelector" name="modalDiscordMemberNameSelector" style="display: none"></select>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="penaltyType" class="col-sm-3 col-form-label">패널티구분</label>
                                <div class="col-sm-7">
                                    <select class="form-control-small" id="penaltyType" name="panaltyType">
                                        <option value="">선택</option>
                                        <c:forEach var="code" items="${penaltyTypeCdList}">
                                            <option value="${code.codeId}">${code.codeLabel}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="penaltyNote" class="col-sm-3 col-form-label">패널티사유</label>
                                <div class="col-sm-7">
                                    <input type="text" class="form-control-small" id="penaltyNote" name="penaltyNote" value="" placeholder="패널티사유를 입력하세요." aria-label="패널티사유를 입력하세요." maxlength="100">
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="applyDate" class="col-sm-3 col-form-label">적용일</label>
                                <div class="col-sm-7">
                                    <input type="date" class="form-control-small" id="applyDate" name="applyDate" value="">
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
        <!-- end Modal -->
    </main>
</div>