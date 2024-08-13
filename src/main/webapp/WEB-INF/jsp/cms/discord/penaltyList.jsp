<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script>
    $(function(){
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

        $("#modalLeagueName").on("keydown",({key, isComposing}) => {
            if (isComposing || key !== "Enter") {
                return;
            }else {
                leagueSelect('save');
            }
        });

        $("#modalDiscordMemberName").on("keydown",({key, isComposing}) => {
            if (isComposing || key !== "Enter") {
                return;
            }else {
                discordMemberSelect('save');
            }
        });

        $("#modalEditLeagueName").on("keydown",({key, isComposing}) => {
            if (isComposing || key !== "Enter") {
                return;
            }else {
                leagueSelect('edit');
            }
        });

        $("#modalEditDiscordMemberName").on("keydown",({key, isComposing}) => {
            if (isComposing || key !== "Enter") {
                return;
            }else {
                discordMemberSelect('edit');
            }
        });

        //등록 팝업 초기화
        $("#penaltyModal").on('show.bs.modal', function () {
            $("#modalLeagueNameSeletor").hide();
            $("#modalDiscordMemberNameSelector").hide();
            $("#modalLeagueNameSeletor").empty();
            $("#modalDiscordMemberNameSelector").empty();
            $("#modalLeagueName").val('');
            $("#modalDiscordMemberName").val('');
            $("#penaltyTypeCode").val('').prop("selected", true);
            $("#penaltyNote").val('');
            $("#applyDate").val('');
        });

        //수정 팝업 초기화
        $("#penaltyEditModal").on('show.bs.modal', function () {
            $("#modalEditLeagueNameSeletor").hide();
            $("#modalEditDiscordMemberNameSelector").hide();
            $("#modalEditLeagueNameSeletor").empty();
            $("#modalEditDiscordMemberNameSelector").empty();
            $("#modalEditLeagueName").val('');
            $("#modalEditDiscordMemberName").val('');
            $("#modalEditPenaltyTypeCode").val('').prop("selected", true);
            $("#modalEditPenaltyNote").val('');
            $("#modalEditApplyDate").val('');
            $("#modalEditFrequency").val('');
            $("#editModalPenaltyId").val('');
        });
    });

    const leagueSelect = (mode) => {
        $("#modalLeagueNameSeletor").hide();

        let inputName = mode === 'save'? 'modalLeagueName' : 'modalEditLeagueName';
        let selectName = mode === 'save'? 'modalLeagueNameSeletor' : 'modalEditLeagueNameSeletor';

        $.ajax({
            url: '/cms/api/discord/league/list',
            type: 'GET',
            async: false,
            data: {
                leagueName: $("#"+inputName).val()
            },
            success: function (data) {
                if (!!data && data.length > 0) {
                    $("#"+selectName).empty();
                    for(let item of data){
                        let option = $("<option value='" + item.id + "'>" + item.leagueName + "(" + item.startDate + "-" + item.endDate + ")</option>");
                        $("#"+selectName).append(option);
                    }
                    $("#"+selectName).show();
                } else {
                    $("#"+selectName).empty();
                    $("#"+selectName).append($("<option value=''>조회된 데이터가 없습니다.</option>"));
                    $("#"+selectName).show();
                }
            },
            error: function (request, status, error) {
                console.error(error);
                alert("오류가 발생하였습니다.");
            }
        });
    }

    const discordMemberSelect = (mode) => {
        $("#modalDiscordMemberNameSelector").hide();
        let inputName = mode === 'save'? 'modalDiscordMemberName' : 'modalEditDiscordMemberName';
        let selectName = mode === 'save'? 'modalDiscordMemberNameSelector' : 'modalEditDiscordMemberNameSelector';

        $.ajax({
            url: '/cms/api/discord/member/list',
            type: 'GET',
            async: false,
            data: {
                username: $("#"+inputName).val()
            },
            success: function (data) {
                if (!!data && data.length > 0) {
                    $("#"+selectName).empty();
                    for(let item of data){
                        let nickName = !!item.nickname? item.nickname : (!!item.globalName? item.globalName : item.username);
                        $("#"+selectName).append($("<option value='" + item.id + "'>" + nickName + "(" + item.username + ")</option>"));
                    }
                    $("#"+selectName).show();
                } else {
                    $("#"+selectName).empty();
                    $("#"+selectName).append($("<option value=''>조회된 데이터가 없습니다.</option>"));
                    $("#"+selectName).show();
                }
            },
            error: function (request, status, error) {
                console.error(error);
                alert("오류가 발생하였습니다.");
            }
        });
    }

    const getPenalty = (id) => {
        $.ajax({
            url: '/cms/api/discord/penalty/get/' + id,
            type: 'GET',
            async: false,
            success: function (data) {
                if (!!data) {
                    $("#editModalPenaltyId").val(id);
                    $("#modalEditLeagueNameSeletor").empty();
                    $("#modalEditDiscordMemberNameSelector").empty();

                    let leagueOption = $("<option value='" + data.league.id + "'>" + data.league.leagueName + "(" + data.league.startDate + "-" + data.league.endDate + ")</option>");
                    $("#modalEditLeagueNameSeletor").append(leagueOption);
                    $("#modalEditLeagueNameSeletor").val(data.league.id).prop("selected", true);

                    let nickName = !!data.discordMember.nickname? data.discordMember.nickname : (!!data.discordMember.globalName? data.discordMember.globalName : data.discordMember.username);
                    let discordMemberOption = $("<option value='" + data.discordMember.id + "'>" + nickName + "(" + data.discordMember.username + ")</option>");
                    $("#modalEditDiscordMemberNameSelector").append(discordMemberOption);
                    $("#modalEditDiscordMemberNameSelector").val(data.discordMember.id).prop("selected", true);

                    $("#modalEditPenaltyTypeCode").val(data.penaltyTypeCode.codeId).prop("selected", true);
                    $("#modalEditPenaltyNote").val(data.penaltyNote);
                    $("#modalEditApplyDate").val(data.applyDate);
                    $("#modalEditFrequency").val(data.frequency);

                    $("#modalEditLeagueNameSeletor").show();
                    $("#modalEditDiscordMemberNameSelector").show();
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

    const formCheck = () => {
        if(!$("#modalLeagueNameSeletor option:selected").val() || $("#modalLeagueNameSeletor option:selected").val() === ''){
            alert("선택된 리그가 없습니다.");
            return false;
        }
        if(!$("#modalDiscordMemberNameSelector option:selected").val() || $("#modalDiscordMemberNameSelector option:selected").val() === ''){
            alert("선택된 멤버가 없습니다.");
            return false;
        }
        if(!$("#penaltyTypeCode option:selected").val() || $("#penaltyTypeCode option:selected").val() === ''){
            alert("페널티구분을 선택하세요.");
            $("#penaltyTypeCode").focus();
            return false;
        }
        if($("#penaltyNote").val() === ''){
            alert("페널티 사유를 입력하세요.");
            $("#penaltyNote").focus();
            return false;
        }
        if($("#applyDate").val() === ''){
            alert("적용일을 입력하세요.");
            $("#applyDate").focus();
            return false;
        }

        return true;
    }

    const formSubmit = () => {
        if(formCheck() && confirm("등록하시겠습니까?")){
            $.post("/cms/api/discord/penalty/save", {
                leagueId : $("#modalLeagueNameSeletor option:selected").val()
                , discordMemberId : $("#modalDiscordMemberNameSelector option:selected").val()
                , penaltyTypeCodeId : $("#penaltyTypeCode option:selected").val()
                , penaltyNote : $("#penaltyNote").val()
                , applyDate : $("#applyDate").val()
            }, function(data){
                if(data){
                    alert("등록되었습니다.");
                    location.reload();
                }else{
                    alert("오류가 발생하였습니다.");
                }
            });
        }
    }

    const editSubmit = () => {
        if($("#editModalPenaltyId").val() === ''){
            alert("수정할 페널티가 잘못 선택 되었습니다.\n새로고침 후 다시 시도해 주세요.");
            return false;
        }
        if(!$("#modalEditLeagueNameSeletor option:selected").val() || $("#modalEditLeagueNameSeletor option:selected").val() === ''){
            alert("선택된 리그가 없습니다.");
            return false;
        }
        if(!$("#modalEditDiscordMemberNameSelector option:selected").val() || $("#modalEditDiscordMemberNameSelector option:selected").val() === ''){
            alert("선택된 멤버가 없습니다.");
            return false;
        }
        if(!$("#modalEditPenaltyTypeCode option:selected").val() || $("#modalEditPenaltyTypeCode option:selected").val() === ''){
            alert("페널티구분을 선택하세요.");
            $("#penaltyTypeCode").focus();
            return false;
        }
        if($("#modalEditPenaltyNote").val() === ''){
            alert("페널티 사유를 입력하세요.");
            $("#penaltyNote").focus();
            return false;
        }
        if($("#modalEditApplyDate").val() === ''){
            alert("적용일을 입력하세요.");
            $("#applyDate").focus();
            return false;
        }
        if($("#modalEditFrequency").val() === ''){
            alert("누적 횟수를 입력하세요.");
            $("#modalEditFrequency").focus();
            return false;
        }

        if(confirm("수정하시겠습니까?")){
            $.ajax({
                url : "/cms/api/discord/penalty/update",
                type : "PUT",
                data : {
                    id : $("#editModalPenaltyId").val()
                    , leagueId : $("#modalEditLeagueNameSeletor option:selected").val()
                    , discordMemberId : $("#modalEditDiscordMemberNameSelector option:selected").val()
                    , penaltyTypeCodeId : $("#modalEditPenaltyTypeCode option:selected").val()
                    , penaltyNote : $("#modalEditPenaltyNote").val()
                    , applyDate : $("#modalEditApplyDate").val()
                    , frequency : $("#modalEditFrequency").val()
                },
                success : function(data){
                    if(data){
                        alert("수정되었습니다.");
                        location.reload();
                    }else{
                        alert("오류가 발생하였습니다.");
                    }
                },
                error: function (request, status, error) {
                    console.error(error);
                    alert("오류가 발생하였습니다.");
                }
            });
        }
    }

    const deletePenalty = (id) => {
        if(confirm("삭제하시겠습니까?")) {
            $.ajax({
                url: "/cms/api/discord/penalty/delete/" + id,
                type: "DELETE",
                success: function (data) {
                    if (data) {
                        alert("삭제되었습니다.");
                        location.reload();
                    } else {
                        alert("오류가 발생하였습니다.");
                    }
                },
                error: function (request, status, error) {
                    console.error(error);
                    alert("오류가 발생하였습니다.");
                }
            });
        }
    }

    const deletePenaltys = () => {
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
                url: '/cms/api/discord/penalty/multi-delete',
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
</script>

<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">페널티 현황 목록</h1>
                <nav>
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                        <li class="breadcrumb-item">사이트 관리</li>
                        <li class="breadcrumb-item">디스코드 관리</li>
                        <li class="breadcrumb-item active">페널티 현황 관리</li>
                    </ol>
                </nav>
            </div>
        </div>

        <div class="container-fluid px-4">
            <div class="icon">
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">페널티 현황 관리</h4>
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
                    <i class="bi bi-record-circle-fill"></i><h4 class="card-title">페널티 현황 목록</h4>
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
                        <th scope="col">페널티구분</th>
                        <th scope="col">페널티사유</th>
                        <th scope="col">적용일</th>
                        <th scope="col">누적 횟수</th>
                        <th scope="col">버튼</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${empty resultList}">
                        <tr>
                            <td colspan="9">조회된 데이터가 없습니다.</td>
                        </tr>
                    </c:if>
                    <c:if test="${not empty resultList}">
                        <c:forEach var="result" items="${resultList}" varStatus="status">
                            <tr>
                                <td><input type="checkbox" class="form-check-input checkItem" name="boardMasterCheck" value="${result.id}"></td>
                                <th scope="row">${pageInfo.totalCount - ((pageInfo.pageNumber-1) * pageInfo.pageOffset + status.index)}</th>
                                <td>${result.league.leagueName}</td>
                                <c:set var="nickname">
                                    <c:choose>
                                        <c:when test="${not empty result.discordMember.nickname}">${result.discordMember.nickname}</c:when>
                                        <c:when test="${not empty result.discordMember.globalName}">${result.discordMember.globalName}</c:when>
                                        <c:otherwise>${result.discordMember.username}</c:otherwise>
                                    </c:choose>
                                </c:set>
                                <td>${nickname}</td>
                                <td>${result.penaltyTypeCode.codeLabel}</td>
                                <td>${result.penaltyNote}</td>
                                <td>${result.applyDate}</td>
                                <td>${result.frequency}</td>
                                <td>
                                    <button type="button" class="btn btn-secondary btn-mg" onclick="getPenalty(${result.id});" data-bs-toggle="modal" data-bs-target="#penaltyEditModal">수정</button>
                                    <button type="button" class="btn btn-danger btn-mg" onclick="deletePenalty(${result.id});">삭제</button>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:if>
                    </tbody>
                </table>
                <jsp:include page="/WEB-INF/jsp/common/commonPagenation.jsp"/>

                <div class="form-btn-set text-end">
                    <button type="button" class="btn btn-danger btn-lg" onclick="deletePenaltys();">선택 삭제</button>
                    <button type="button" class="btn btn-success btn-lg" data-bs-toggle="modal" data-bs-target="#penaltyModal">등록</button>
                </div>
            </div>
        </div>
        <!-- Modal -->
        <div class="modal fade" id="penaltyModal" tabindex="-1" aria-labelledby="penaltyModalTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <i class="bi bi-record-circle-fill"></i>&nbsp;<h5 class="modal-title" id="penaltyModalTitle">페널티 등록</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="penaltyForm" name="penaltyForm" method="post" action="">
                            <div class="row mb-3">
                                <label for="modalLeagueName" class="col-sm-3 col-form-label">리그명</label>
                                <div class="col-sm-7">
                                    <input type="text" class="form-control-small" id="modalLeagueName" name="leagueName" value="" placeholder="리그명을 입력하고 엔터를 누르세요." aria-label="리그명을 입력하고 엔터를 누르세요." maxlength="100">
                                    <button type="button" class="btn btn-secondary btn-sm" onclick="leagueSelect('save');">검색</button>
                                    <br>
                                    <select class="form-control-small" id="modalLeagueNameSeletor" name="modalLeagueNameSeletor" style="display: none"></select>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="modalDiscordMemberName" class="col-sm-3 col-form-label">사용자명</label>
                                <div class="col-sm-7">
                                    <input type="text" class="form-control-small" id="modalDiscordMemberName" name="username" value="" placeholder="사용자명을 입력하고 엔터를 누르세요." aria-label="사용자명을  입력하고 엔터를 누르세요." maxlength="100">
                                    <button type="button" class="btn btn-secondary btn-sm" onclick="discordMemberSelect('save');">검색</button>
                                    <br>
                                    <select class="form-control-small" id="modalDiscordMemberNameSelector" name="modalDiscordMemberNameSelector" style="display: none"></select>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="penaltyTypeCode" class="col-sm-3 col-form-label">페널티구분</label>
                                <div class="col-sm-7">
                                    <select class="form-control-small" id="penaltyTypeCode" name="penaltyTypeCode">
                                        <option value="">선택</option>
                                        <c:forEach var="code" items="${penaltyTypeCdList}">
                                            <option value="${code.codeId}">${code.codeLabel}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="penaltyNote" class="col-sm-3 col-form-label">페널티사유</label>
                                <div class="col-sm-7">
                                    <input type="text" class="form-control-small" id="penaltyNote" name="penaltyNote" value="" placeholder="페널티사유를 입력하세요." aria-label="페널티사유를 입력하세요." maxlength="100">
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
                        <button type="button" class="btn btn-success" onclick="formSubmit();">등록</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- end Modal -->

        <!-- edit Modal -->
        <div class="modal fade" id="penaltyEditModal" tabindex="-1" aria-labelledby="penaltyEditModalTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <i class="bi bi-record-circle-fill"></i>&nbsp;<h5 class="modal-title" id="penaltyEditModalTitle">페널티 수정</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="penaltyEditForm" name="penaltyForm" method="post" action="">
                            <input type="hidden" id="editModalPenaltyId" name="editModalPenaltyId" value="">
                            <div class="row mb-3">
                                <label for="modalEditLeagueName" class="col-sm-3 col-form-label">리그명</label>
                                <div class="col-sm-7">
                                    <input type="text" class="form-control-small" id="modalEditLeagueName" name="leagueName" value="" placeholder="리그명을 입력하고 엔터를 누르세요." aria-label="리그명을 입력하고 엔터를 누르세요." maxlength="100">
                                    <button type="button" class="btn btn-secondary btn-sm" onclick="leagueSelect('edit');">검색</button>
                                    <br>
                                    <select class="form-control-small" id="modalEditLeagueNameSeletor" name="modalLeagueNameSeletor" style="display: none"></select>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="modalEditDiscordMemberName" class="col-sm-3 col-form-label">사용자명</label>
                                <div class="col-sm-7">
                                    <input type="text" class="form-control-small" id="modalEditDiscordMemberName" name="username" value="" placeholder="사용자명을 입력하고 엔터를 누르세요." aria-label="사용자명을  입력하고 엔터를 누르세요." maxlength="100">
                                    <button type="button" class="btn btn-secondary btn-sm" onclick="discordMemberSelect('edit');">검색</button>
                                    <br>
                                    <select class="form-control-small" id="modalEditDiscordMemberNameSelector" name="modalDiscordMemberNameSelector" style="display: none"></select>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="modalEditPenaltyTypeCode" class="col-sm-3 col-form-label">페널티구분</label>
                                <div class="col-sm-7">
                                    <select class="form-control-small" id="modalEditPenaltyTypeCode" name="penaltyTypeCode">
                                        <option value="">선택</option>
                                        <c:forEach var="code" items="${penaltyTypeCdList}">
                                            <option value="${code.codeId}">${code.codeLabel}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="modalEditPenaltyNote" class="col-sm-3 col-form-label">페널티사유</label>
                                <div class="col-sm-7">
                                    <input type="text" class="form-control-small" id="modalEditPenaltyNote" name="penaltyNote" value="" placeholder="페널티사유를 입력하세요." aria-label="페널티사유를 입력하세요." maxlength="100">
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="modalEditApplyDate" class="col-sm-3 col-form-label">적용일</label>
                                <div class="col-sm-7">
                                    <input type="date" class="form-control-small" id="modalEditApplyDate" name="applyDate" value="">
                                </div>
                            </div>
                            <div class="row mb-3">
                                <label for="modalEditFrequency" class="col-sm-3 col-form-label">누적 횟수</label>
                                <div class="col-sm-7">
                                    <input type="number" class="form-control-small" id="modalEditFrequency" name="applyDate" value="">
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                        <button type="button" class="btn btn-success" onclick="editSubmit();">수정</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- end Modal -->
    </main>
</div>