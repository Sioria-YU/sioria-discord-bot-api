<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script>
    const appendTrack = () => {
        let trackName = $("#trackSelet option:selected").text();
        let trackId = $("#trackSelet option:selected").val();
        console.log("name:",trackName, ", id:", trackId);

        if(!!document.getElementById("track_" + trackId)){
            alert("이미 추가한 트랙입니다.");
            return false;
        }

        let trackCnt = Math.max($("#tracksArea").children().length, 0);
        let html = "";
        html += '<div id="track_' + trackId + '">';
        html += '<input type="text" class="form-control-small w-25" id="track_' + trackId + '_name" value="' + trackName + '" readOnly/>';
        html += '&nbsp;<input type="date" id="track_' + trackId + '_date" pattern="yyyy-mm-dd"/>';
        html += '&nbsp;<button type="button" class="btn btn-sm btn-danger" onclick="$(\'#track_' + trackId + '\').remove()">삭제</button>';
        html += '</div>';
        $("#tracksArea").append(html);
    }

    const appendReagueButton = () => {
        let reagueBtnCnt = Math.max($("#reagueBtnWrap").children().length, 0);

        let html = "";
        html += '<div id="reagueBtnArea_'+reagueBtnCnt+'">';
        html += '<input type="text" class="form-control-small w-25 reagueButtonName" id="reagueButtonName_'+reagueBtnCnt+'" name="reagueButtonName_'+reagueBtnCnt+'" placeholder="버튼명 입력" />';
        html += '&nbsp;<select class="form-control-sm" id="reagueButtonColor_'+reagueBtnCnt+'" name="reagueButtonColor_'+reagueBtnCnt+'">';
        html += '<option value="Primary">파랑</option>';
        html += '<option value="Success">초록</option>';
        html += '<option value="Secondary">그레이</option>';
        html += '<option value="Danger">빨강</option>';
        html += '</select>';
        html += '&nbsp;<button type="button" class="btn btn-sm btn-danger" onclick="">삭제</button>';
        html += '</div>';

        $("#reagueBtnWrap").append(html);
    }
</script>

<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">리그 등록</h1>
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
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">게시글 등록</h4>
            </div>

            <form id="registForm" name="registForm" method="post" enctype="multipart/form-data" action="./save">
                <input type="hidden" name="reagueId" value="${param.reagueId}">
                <input type="hidden" id="regueTime" name="regueTime" value="">
                <c:if test="${not empty result}">
                    <input type="hidden" name="id" value="${result.id}">
                </c:if>
                <table class="table">
                    <colgroup>
                        <col style="width: 15%">
                        <col>
                    </colgroup>
                    <tbody>
                    <tr>
                        <th class="table-title"><label for="reagueName">리그명</label></th>
                        <td><input type="text" class="form-control" id="reagueName" name="title" value="${result.reagueName}" aria-label="리그명" placeholder="리그명을 입력하세요."></td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="files">이미지 첨부</label></th>
                        <td>
                            <c:if test="${not empty result.attachFileGroup}">
                                <input type="hidden" name="attachFileGroupId" value="${result.attachFileGroup.id}">
                                <c:if test="${not empty result.attachFileGroup.attachFileList}">
                                    <c:forEach var="attachfile" items="${result.attachFileGroup.attachFileList}" varStatus="status">
                                        <div class="block mb-1" id="attachFileWrap_${status.count}">
                                            <a href="#" class="me-1" onclick="attachFileDownload('${attachfile.fileName}');" aria-label="첨부파일${status.count} 다운로드">${attachfile.originFileName}</a>
                                            <i class="bi bi-x-circle-fill" onclick="attachFileDelete('${attachfile.fileName}', 'attachFileWrap_${status.count}');" aria-label="첨부파일${status.count} 삭제"></i>
                                        </div>
                                    </c:forEach>
                                </c:if>
                            </c:if>
                            <input type="file" class="form-control" id="files" name="files" accept="image/*">
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="title">설명 제목</label></th>
                        <td><input type="text" class="form-control" id="title" name="title" value="${result.title}" aria-label="설명 제목" placeholder="설명 제목을 입력하세요."></td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="description">설명</label></th>
                        <td><textarea id="description" name="description" rows="5" style="width: 100%">${result.description}</textarea></td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="startDate">운영 기간</label></th>
                        <td>
                            <input type="date" id="startDate" name="startDate" pattern="yyyy-mm-dd" value="${result.startDate}"/>
                            &nbsp;~&nbsp;
                            <input type="date" id="endDate" name="endDate" pattern="yyyy-mm-dd" value="${result.endDate}"/>
                            &nbsp;
                            <select class="form-control-sm" id="reagueHour" name="reagueHour">
                                <c:set var="reagueHour" value=""/>
                                <c:set var="reagueMinute" value=""/>
                                <c:if test="${not empty result}">
                                    <c:set var="reagueHour" value="${fn:substring(result.reagueTime,0,2)}"/>
                                    <c:set var="reagueMinute" value="${fn:substring(result.reagueTime,3,2)}"/>
                                </c:if>
                                <c:forEach var="item" begin="12" end="24" step="1">
                                    <option value="${item}" <c:if test="${item eq reagueHour}" >selected</c:if>>${item}시</option>
                                </c:forEach>
                            </select>
                            <select class="form-control-sm" id="reagueMinute" name="reagueMinute">
                                <c:forEach var="item" begin="0" end="5" step="1">
                                    <option value="${item}0" <c:if test="${item eq reagueMinute}" >selected</c:if>>${item}0분</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="noticeChannel">게시 채널</label></th>
                        <td>
                            <select class="form-control-sm" id="noticeChannel" name="noticeChannel">
                                <option value="">선택</option>
                                <c:forEach var="item" items="${newsChannelList}">
                                    <option value="${item.id}">${item.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="startDate">게시 시간</label></th>
                        <td>
                            <select class="form-control-sm" id="noticeHour" name="noticeHour">
                                <c:forEach var="item" begin="12" end="24" step="1">
                                    <option value="${item}" <c:if test="${item eq reagueHour}" >selected</c:if>>${item}시</option>
                                </c:forEach>
                            </select>
                            <select class="form-control-sm" id="noticeMinute" name="noticeMinute">
                                <c:forEach var="item" begin="0" end="5" step="1">
                                    <option value="${item}0" <c:if test="${item eq reagueMinute}" >selected</c:if>>${item}0분</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="startDate">참여 가능 역할</label></th>
                        <td>
                            <c:forEach var="item" items="${discordMentionLise}" varStatus="index">
                                <label for="mention_${index.index}"><input type="checkbox" id="mention_${index.index}" name="mention" value="${item.roleId}"> ${item.roleName}</label>
                            </c:forEach>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="startDate">참가인원</label></th>
                        <td><input type="number" id="joinMemberCount" name="joinMemberCount" max="20"/></td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="color">메세지 컬러</label></th>
                        <td>
                            <select class="form-control-sm" id="color" name="color">
                                <option value="red" ${result.color eq 'red'? 'selected':''}>red</option>
                                <option value="blue" ${result.color eq 'blue'? 'selected':''}>blue</option>
                                <option value="yellow" ${result.color eq 'yellow'? 'selected':''}>yellow</option>
                                <option value="green" ${result.color eq 'green'? 'selected':''}>green</option>
                                <option value="white" ${result.color eq 'white'? 'selected':''}>white</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="reagueTrack0">트랙 선택</label></th>
                        <td>
                            <select class="form-control-sm" id="trackSelet" name="trackSelet">
                                <c:forEach var="item" items="${tackCodeList}" varStatus="index">
                                    <option value="${item.codeId}">${item.codeLabel}</option>
                                </c:forEach>
                            </select>
                            <button type="button" class="btn btn-sm btn-success" onclick="appendTrack();">추가</button>
                            <div id="tracksArea"></div>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="reagueButtonName_0">참여 카테고리</label></th>
                        <td>
                            <p style="color: red;font-weight: bold">※ 카테고리 이름과 버튼이름은 동일하게 생성됩니다.</p>
                            <div id="reagueBtnWrap">
                                <div id="reagueBtnArea_0">
                                    <input type="text" class="form-control-small w-25 reagueButtonName" id="reagueButtonName_0" name="reagueButtonName_0" placeholder="버튼명 입력" />
                                    <select class="form-control-sm" id="reagueButtonColor_0" name="reagueButtonColor_0">
                                        <option value="Primary">파랑</option>
                                        <option value="Success">초록</option>
                                        <option value="Secondary">그레이</option>
                                        <option value="Danger">빨강</option>
                                    </select>
                                    <button type="button" class="btn btn-sm btn-success" id="reagueButtonAdd" name="reagueButtonAdd" onclick="appendReagueButton();">추가</button>
                                </div>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div class="form-btn-set text-center">
                    <button type="button" class="btn btn-secondary btn-lg" onclick="location.href='./list?boardMasterId=${boardMasterId}';">취소</button>
                    <button type="button" class="btn btn-success btn-lg" onclick="formSubmitEvent();">${empty result? '등록':'수정'}</button>
                </div>
            </form>
        </div>
    </main>
</div>