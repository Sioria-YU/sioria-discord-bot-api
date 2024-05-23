<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script>
    const appendTrack = () => {
        let trackName = $("#trackSelect option:selected").text();
        let trackId = $("#trackSelect option:selected").val();
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
        html += '&nbsp;<button type="button" class="btn btn-sm btn-danger" onclick="$(\'#reagueBtnArea_'+reagueBtnCnt+'\').remove()">삭제</button>';
        html += '</div>';

        $("#reagueBtnWrap").append(html);
    }

    const formSubmitEvent = () => {
        //region data verification
        if($("#reagueName").val() === ''){
            alert("리그명을 입력하세요.");
            $("#reagueName").focus();
            return false;
        }

        if($("#title").val() === ''){
            alert("설명 제목을 입력하세요.");
            $("#title").focus();
            return false;
        }

        if($("#description").val() === ''){
            alert("설명을 입력하세요.");
            $("#description").focus();
            return false;
        }

        if($("#startDate").val() === ''){
            alert("운영기간 시작일을 입력하세요.");
            $("#startDate").focus();
            return false;
        }

        if($("#endDate").val() === ''){
            alert("운영기간 종료일을 입력하세요.");
            $("#endDate").focus();
            return false;
        }

        if($("#noticeChannelId option:selected").val() === ''){
            alert("게시 채널을 선택하세요.");
            $("#endDate").focus();
            return false;
        }

        if($("input:checkbox[name=joinAceptMentionList]:checked").length < 1){
            alert("참여 가능 역할을 1개 이상 선택하세요.");
            $("#mention_0").focus();
            return false;
        }

        if($("#joinMemberLimit").val() === ''){
            alert("참가인원을 입력하세요.");
            $("#joinMemberLimit").focus();
            return false;
        }

        if($("#joinMemberLimit").val() < 1 || $("#joinMemberLimit").val() > 20){
            alert("참가인원은 1~20명 사이의 값이여야 합니다. 입력 값 : " + $("#joinMemberLimit").val());
            $("#joinMemberLimit").focus();
            return false;
        }

        if($("#tracksArea").children().length < 1){
            alert("트랙을 추가하세요.");
            $("#trackSelect").focus();
            return false;
        }
        for(let i=0; i <  $("#reagueBtnWrap").children().length; i++) {
            if($("#reagueBtnWrap").children(i).children("input").val() === ''){
                alert((i+1) + " 번째 카테고리명을 입력하세요.");
                $("#trackSelect").focus();
                return false;
            }
        }
        //endregion data verification

        //region data setup
        let form = document.getElementById("registForm");

        //게시 시간
        let noticeTime = $("#noticeHour option:selected").val() + ":" + $("#noticeMinute option:selected").val() + ":00";
        $("#noticeTime").val(noticeTime);
        //리그 시작 시간
        let reagueTime = $("#reagueHour option:selected").val() + ":" + $("#reagueMinute option:selected").val() + ":00";
        $("#reagueTime").val(reagueTime);

        //region tracks to array
        for(let i=0; i <  $("#tracksArea").children().length; i++) {
            let trackId = $("#tracksArea").children("div").get(i).id;
            let trackNameInput = document.createElement("input");
            let trackDateInput = document.createElement("input");
            trackNameInput.setAttribute("type", "hidden");
            trackNameInput.setAttribute("name", "trackList["+i+"].name");
            trackNameInput.setAttribute("value", $("#" + trackId + "_name").val());
            trackDateInput.setAttribute("type", "hidden");
            trackDateInput.setAttribute("name", "trackList["+i+"].date");
            trackDateInput.setAttribute("value", $("#" + trackId + "_date").val());

            form.appendChild(trackNameInput);
            form.appendChild(trackDateInput);
        }
        //endregion tracks to array

        //region categorys to array
        for(let i=0; i <  $("#reagueBtnWrap").children().length; i++) {
            let reagueButtonNameInput = document.createElement("input");
            let reagueButtonTypeInput = document.createElement("input");
            reagueButtonNameInput.setAttribute("type", "hidden");
            reagueButtonNameInput.setAttribute("name", "reagueButtonList["+i+"].name");
            reagueButtonNameInput.setAttribute("value", $("#reagueBtnArea_"+i).children("input").val());
            reagueButtonTypeInput.setAttribute("type", "hidden");
            reagueButtonTypeInput.setAttribute("name", "reagueButtonList["+i+"].type");
            reagueButtonTypeInput.setAttribute("value", $("#reagueBtnArea_"+i).children("select").val());
            form.appendChild(reagueButtonNameInput);
            form.appendChild(reagueButtonTypeInput);
        }
        //endregion categorys to array
        //endregion data setup

        form.submit();
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

            <form id="registForm" name="registForm" method="post" enctype="multipart/form-data" action="${empty result? '/cms/discord/reague/save':'/cms/discord/reague/update'}">
                <input type="hidden" id="reagueTime" name="reagueTime" value="">
                <input type="hidden" id="noticeTime" name="noticeTime" value="">
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
                        <td><input type="text" class="form-control" id="reagueName" name="reagueName" value="${result.reagueName}" aria-label="리그명" placeholder="리그명을 입력하세요."></td>
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
                        <th class="table-title"><label for="startDate">운영 기간</label></th>
                        <td>
                            <input type="date" id="startDate" name="startDate" pattern="yyyy-mm-dd" value="${result.startDate}"/>
                            &nbsp;~&nbsp;
                            <input type="date" id="endDate" name="endDate" pattern="yyyy-mm-dd" value="${result.endDate}"/>
                            <c:set var="reagueHour" value=""/>
                            <c:set var="reagueMinute" value=""/>
                            <c:if test="${not empty result}">
                                <c:set var="reagueHour" value="${fn:substring(result.reagueTime,0,2)}"/>
                                <c:set var="reagueMinute" value="${fn:substring(result.reagueTime,3,5)}"/>
                            </c:if>
                            <select class="form-control-sm" id="reagueHour" name="reagueHour">
                                <c:forEach var="item" begin="12" end="24" step="1">
                                    <option value="${item}" <c:if test="${item eq reagueHour}" >selected</c:if>>${item}시</option>
                                </c:forEach>
                            </select>
                            <select class="form-control-sm" id="reagueMinute" name="reagueMinute">
                                <c:forEach var="item" begin="0" end="5" step="1">
                                    <option value="${item}0" <c:if test="${item eq fn:substring(reagueMinute,0,1)}" >selected</c:if>>${item}0분</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="noticeChannelId">게시 채널</label></th>
                        <td>
                            <select class="form-control-sm" id="noticeChannelId" name="noticeChannelId">
                                <option value="">선택</option>
                                <c:forEach var="item" items="${newsChannelList}">
                                    <option value="${item.id}" ${result.noticeChannelId eq item.id? 'selected':''}>${item.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="noticeHour">게시 시간</label></th>
                        <td>
                            <c:set var="noticeHour" value=""/>
                            <c:set var="noticeMinute" value=""/>
                            <c:if test="${not empty result}">
                                <c:set var="noticeHour" value="${fn:substring(result.noticeTime,0,2)}"/>
                                <c:set var="noticeMinute" value="${fn:substring(result.noticeTime,3,5)}"/>
                            </c:if>
                            <select class="form-control-sm" id="noticeHour" name="noticeHour">
                                <c:forEach var="item" begin="12" end="24" step="1">
                                    <option value="${item}" <c:if test="${item eq noticeHour}" >selected</c:if>>${item}시</option>
                                </c:forEach>
                            </select>
                            <select class="form-control-sm" id="noticeMinute" name="noticeMinute">
                                <c:forEach var="item" begin="0" end="5" step="1">
                                    <option value="${item}0" <c:if test="${item eq fn:substring(noticeMinute,0,1)}" >selected</c:if>>${item}0분</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="mention_0">참여 가능 역할</label></th>
                        <td>
                            <c:forEach var="item" items="${discordMentionLise}" varStatus="index">
                                <c:set var="isSelected" value="false"/>
                                <c:forEach var="joinAceptMention" items="${result.joinAceptMentions}">
                                    <c:if test="${joinAceptMention.discordMention.roleId eq item.roleId}">
                                        <c:set var="isSelected" value="true"/>
                                    </c:if>
                                </c:forEach>
                                <label for="mention_${index.index}"><input type="checkbox" id="mention_${index.index}" name="joinAceptMentionList" value="${item.roleId}" ${isSelected eq 'true'? 'checked':''}> ${item.roleName}</label>
                            </c:forEach>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="startDate">참가인원</label></th>
                        <td><input type="number" id="joinMemberLimit" name="joinMemberLimit" max="20" value="${result.joinMemberLimit}"/></td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="trackSelect">트랙 선택</label></th>
                        <td>
                            <select class="form-control-sm" id="trackSelect" name="trackSelet">
                                <c:forEach var="item" items="${tackCodeList}" varStatus="index">
                                    <option value="${item.codeId}">${item.codeLabel}</option>
                                </c:forEach>
                            </select>
                            <button type="button" class="btn btn-sm btn-success" onclick="appendTrack();">추가</button>
                            <div id="tracksArea">
                                <c:if test="${not empty result}">
                                    <c:forEach var="reagueTrack" items="${result.reagueTracks}" varStatus="index">
                                        <div id="track_${index.index}">
                                            <input type="text" class="form-control-small w-25" id="track_${index.index}_name" value="${reagueTrack.trackCode.codeLabel}" readOnly/>
                                            <input type="date" id="track_${index.index}_date" pattern="yyyy-mm-dd" value="${reagueTrack.trackDate}"/>
                                            <button type="button" class="btn btn-sm btn-danger" onclick="$('#track_${index.index}').remove()">삭제</button>
                                        </div>
                                    </c:forEach>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="reagueButtonName_0">참여 카테고리</label></th>
                        <td>
                            <p style="color: red;font-weight: bold">※ 카테고리 이름과 버튼이름은 동일하게 생성됩니다.</p>
                            <div id="reagueBtnWrap">
                                <c:if test="${not empty result}">
                                    <c:forEach var="reagueButton" items="${result.reagueButtons}" varStatus="index">
                                        <div id="reagueBtnArea_${index.index}">
                                        <input type="text" class="form-control-small w-25 reagueButtonName" id="reagueButtonName_${index.index}" name="reagueButtonName_${index.index}" value="${reagueButton.buttonName}" placeholder="버튼명 입력" />
                                        <select class="form-control-sm" id="reagueButtonColor_${index.index}" name="reagueButtonColor_${index.index}">
                                            <option value="Primary" ${reagueButton.buttonType eq 'Primary'? 'selected':''}>파랑</option>
                                            <option value="Success" ${reagueButton.buttonType eq 'Success'? 'selected':''}>초록</option>
                                            <option value="Secondary" ${reagueButton.buttonType eq 'Secondary'? 'selected':''}>그레이</option>
                                            <option value="Danger" ${reagueButton.buttonType eq 'Danger'? 'selected':''}>빨강</option>
                                        </select>
                                            <c:if test="${index.first}">
                                                <button type="button" class="btn btn-sm btn-success" id="reagueButtonAdd" name="reagueButtonAdd" onclick="appendReagueButton();">추가</button>
                                            </c:if>
                                            <c:if test="${not index.first}">
                                                <button type="button" class="btn btn-sm btn-danger" onclick="$('#reagueBtnArea_${index.index}').remove()">삭제</button>
                                            </c:if>
                                        </div>
                                    </c:forEach>
                                </c:if>
                                <c:if test="${empty result}">
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
                                </c:if>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div class="form-btn-set text-center">
                    <c:if test="${not empty result}">
                        <button type="button" class="btn btn-dark btn-lg" onclick="alert('미구현');">즉시 공지</button>
                    </c:if>
                    <button type="button" class="btn btn-success btn-lg" onclick="formSubmitEvent();">${empty result? '등록':'수정'}</button>
                    <button type="button" class="btn btn-secondary btn-lg" onclick="location.href='/cms/discord/reague/list';">취소</button>
                </div>
            </form>
        </div>
    </main>
</div>