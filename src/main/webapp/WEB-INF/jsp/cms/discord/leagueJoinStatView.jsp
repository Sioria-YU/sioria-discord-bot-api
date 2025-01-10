<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%-- 메뉴버튼 권한 --%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property='principal.adminMenuAuthList' var="adminMenuAuthList"/>
<c:set var="adminMenuAuth" value=""/>
<c:forEach var="auth" items="${adminMenuAuthList}">
    <c:if test="${auth.menu.menuName eq '페널티 현황'}">
        <c:set var="adminMenuAuth" value="${auth}"/>
    </c:if>
</c:forEach>

<script>
    $(function(){
        //등록 팝업 초기화
        $("#addMemberModal").on('show.bs.modal', function () {
            $("#modalDiscordMemberNameSelector").hide();
            $("#modalDiscordMemberNameSelector").empty();
            $("#modalDiscordMemberName").val('');
        });

        $("#modalDiscordMemberName").on("keydown",({key, isComposing}) => {
            if (isComposing || key !== "Enter") {
                return;
            }else {
                discordMemberSelect('save');
                $("#modalDiscordMemberNameSelector").focus();
            }
        });
    });

    const changePage = (obj) => {
        location.href = "/cms/discord/league-join-stat/view/" + obj.value;
    }

    const allowOnlyNumbers = (obj) => {
        // 숫자가 아닌 데이터 삭제처리
        if (!/^\d$/.test(obj.value)) {
            obj.value = obj.value.replace(/[^0-9]/g, '');
            if(obj.value === ''){
                obj.value = 0;
            }
        }
    }

    const formSubmitEvent = () =>{
        $("#data-form").submit();
    }

    const deleteTrackMember = (id) =>{
        if(confirm("삭제하시겠습니까?")) {
            $.post("/cms/api/discord/league-track-member/delete", {leagueTrackMemberId: id}, function (data) {
                if (data) {
                    alert("삭제 처리 되었습니다.");
                    location.reload();
                } else {
                    alert("처리중 오류가 발생하였습니다. 관리자에게 문의해주세요.");
                }
            });
        }
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

    const addMember = () => {
        if(confirm("추가하시겠습니까?")) {
            $.post("/cms/api/discord/league-track-member/add-member"
                , {
                    trackId: '${leagueTrackInfo.id}',
                    buttonId: $("#modalCategorySelector option:selected").val(),
                    memberId: $("#modalDiscordMemberNameSelector option:selected").val()
                }, function (data) {
                if (data) {
                    alert("정상 처리 되었습니다.");
                    location.reload();
                } else {
                    alert("처리중 오류가 발생하였습니다. 관리자에게 문의해주세요.");
                }
            });
        }
    }
</script>

<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">참여 현황 상세</h1>
                <nav>
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                        <li class="breadcrumb-item">사이트 관리</li>
                        <li class="breadcrumb-item">디스코드 관리</li>
                        <li class="breadcrumb-item active">참여 현황 관리</li>
                    </ol>
                </nav>
            </div>
        </div>

        <div class="container-fluid px-4">
            <div class="icon">
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">${leagueInfo.leagueName}</h4>
            </div>

            <p style="font-size: 18px;font-weight: bold;margin-bottom: 25px">
                <label for="leagueTrackId"><i class="bi bi-record-circle-fill"></i> 트랙 선택</label>
                <select class="dataTable-selector" id="leagueTrackId" name="leagueTrackId" onchange="changePage(this);">
                    <c:forEach var="item" items="${allTrackInfo}">
                        <option value="${item.id}" ${item.id eq leagueTrackInfo.id? 'selected':''}>${item.trackCode.codeLabel}</option>
                    </c:forEach>
                </select>
                <button type="button" class="btn btn-success btn-mg" data-bs-toggle="modal" data-bs-target="#addMemberModal">추가</button>
            </p>
            <form id="data-form" name="data-form" method="post" action="/cms/discord/league-join-stat/update">
                <input type="hidden" name="id" value="${leagueTrackInfo.id}"/>
                <c:forEach var="leagueButton" items="${leagueInfo.leagueButtons}">
                <div class="container-fluid px-4">
                    ${leagueButton.buttonName}
                    <table class="table text-center">
                        <thead>
                        <tr>
                            <th scope="col" style="width: 10%">순번</th>
                            <th scope="col">아이디(닉네임)</th>
                            <th scope="col">참여구분</th>
                            <th scope="col" style="width: 20%;min-width:60px;">포인트</th>
                            <th scope="col">삭제</th>
                        </tr>
                        </thead>
                        <tbody>
                            <c:set var="memberIndex" value="1"/>
                            <c:forEach var="trackMember" items="${trackMembersInfo}" varStatus="idx">
                                <c:if test="${trackMember.leagueButton.id eq leagueButton.id}">
                                    <input type="hidden" name="trackMemberIds[${idx.index}]" value="${trackMember.id}"/>
                                    <tr>
                                        <th scope="row">${memberIndex}</th>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty trackMember.discordMember.nickname}">${trackMember.discordMember.nickname}</c:when>
                                                <c:when test="${not empty trackMember.discordMember.globalName}">${trackMember.discordMember.globalName}</c:when>
                                                <c:otherwise>${trackMember.discordMember.username}</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <select class="dataTable-selector" name="joinTypes[${idx.index}]">
                                                <c:forEach var="cd" items="${joinTypeCodeList}">
                                                    <option value="${cd.codeId}" ${trackMember.joinType.codeId eq cd.codeId? 'selected':''}>${cd.codeLabel}</option>
                                                </c:forEach>
                                            </select>
                                        </td>
                                        <td><input type="text" class="form-control text-center" name="scores[${idx.index}]" value="${trackMember.score}" onkeyup="allowOnlyNumbers(this)"></td>
                                        <td>
                                            <button type="button" class="btn btn-danger btn-mg" onclick="deleteTrackMember('${trackMember.id}');">삭제</button>
                                        </td>
                                    </tr>
                                    <c:set var="memberIndex" value="${memberIndex + 1}"/>
                                </c:if>
                                <c:if test="${idx.last and memberIndex eq 1}">
                                    <tr class="text-center">
                                        <td colspan="5">조회된 데이터가 존재하지 않습니다.</td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                </c:forEach>
            </form>
            <div class="form-btn-set text-center">
                <button type="button" class="btn btn-success btn-lg" onclick="formSubmitEvent();">저장</button>
                <button type="button" class="btn btn-secondary btn-lg" onclick="location.href='/cms/discord/league-join-stat/list';">목록</button>
            </div>
        </div>

        <!-- Modal -->
        <div class="modal fade" id="addMemberModal" tabindex="-1" aria-labelledby="addMemberModalTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <i class="bi bi-record-circle-fill"></i>&nbsp;<h5 class="modal-title" id="addMemberModalTitle">참가자 추가</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="addForm" name="addForm" method="post" action="">
                            <div class="row mb-3">
                                <label for="modalCategorySelector" class="col-sm-3 col-form-label">카테고리</label>
                                <div class="col-sm-7">
                                    <select class="form-control-small" id="modalCategorySelector" name="modalCategorySelector">
                                        <c:forEach var="leagueButton" items="${leagueInfo.leagueButtons}">
                                            <option value="${leagueButton.id}">${leagueButton.buttonName}</option>
                                        </c:forEach>
                                    </select>
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
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                        <button type="button" class="btn btn-success" onclick="addMember();">등록</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- end Modal -->
    </main>
</div>