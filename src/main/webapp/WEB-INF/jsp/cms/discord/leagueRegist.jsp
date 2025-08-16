<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script>
    <c:set var="totalTrackCount" value="0"/>
    <c:set var="totalButtonCount" value="1"/>
    <c:if test="${not empty result and not empty result.leagueTracks}">
        <c:set var="totalTrackCount" value="${result.leagueTracks.size()}"/>
    </c:if>
    <c:if test="${not empty result and not empty result.leagueButtons}">
    <c:set var="totalButtonCount" value="${result.leagueButtons.size()}"/>
    </c:if>


    var totalTrackCount = ${totalTrackCount};
    var totalButtonCount = ${totalButtonCount};

    const appendTrack = () => {
        let trackName = $("#trackSelect option:selected").text();
        let trackId = $("#trackSelect option:selected").val();

        let trackOpions = "";
        <c:forEach var="item" items="${tackCodeList}">
        trackOpions += '<option value="${item.codeId}">${item.codeLabel}</option>\n'
        </c:forEach>

        let trackCnt = Math.max($("#tracksArea").children().length, 0);
        let html = "";
        html += '<div id="track_' + totalTrackCount + '">';
        html += '<select class="form-control-sm" id="track_' + totalTrackCount + '_name" name="trackSelect" data-id="">';
        html += trackOpions;
        html += '</select>';
        // html += '<input type="text" class="form-control-small w-25" id="track_' + trackId + '_name" value="' + trackName + '" readOnly/>';
        html += '&nbsp;<input type="date" id="track_' + totalTrackCount + '_date" pattern="yyyy-mm-dd"/>';
        html += '&nbsp;<button type="button" class="btn btn-sm btn-danger" onclick="$(\'#track_' + totalTrackCount + '\').remove()">삭제</button>';
        html += '</div>';
        $("#tracksArea").append(html);
        $("#track_" + totalTrackCount + "_name").val(trackId).prop("selected", true);
        totalTrackCount++;
    }

    const removeTrack = (id) => {
        $("#track_" + id).remove();
    }

    const appendLeagueButton = () => {
        let html = "";
        html += '<div id="leagueBtnArea_'+totalButtonCount+'">';
        html += '<input type="text" class="form-control-small w-25 leagueButtonName" id="leagueButtonName_'+totalButtonCount+'" name="leagueButtonName_'+totalButtonCount+'" data-id="" placeholder="버튼명 입력" />';
        html += '&nbsp;<select class="form-control-sm" id="leagueButtonColor_'+totalButtonCount+'" name="leagueButtonColor_'+totalButtonCount+'">';
        html += '<option value="Primary">파랑</option>';
        html += '<option value="Success">초록</option>';
        html += '<option value="Secondary">그레이</option>';
        html += '<option value="Danger">빨강</option>';
        html += '</select>';
        html += '&nbsp;<button type="button" class="btn btn-sm btn-danger" onclick="$(\'#leagueBtnArea_'+totalButtonCount+'\').remove()">삭제</button>';
        html += '</div>';

        $("#leagueBtnWrap").append(html);
        totalButtonCount++;
    }

    const formSubmitEvent = () => {
        //region data verification
        if($("#leagueName").val() === ''){
            alert("리그명을 입력하세요.");
            $("#leagueName").focus();
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
        for(let i=0; i <  $("#leagueBtnWrap").children().length; i++) {
            if($("#leagueBtnWrap").children(i).children("input").val() === ''){
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
        let leagueTime = $("#leagueHour option:selected").val() + ":" + $("#leagueMinute option:selected").val() + ":00";
        $("#leagueTime").val(leagueTime);

        //region tracks to array
        for(let i=0; i <  $("#tracksArea").children().length; i++) {
            let trackId = $("#tracksArea").children("div").get(i).id;
            let trackNameInput = document.createElement("input");
            let trackDateInput = document.createElement("input");
            let trackIdInput = document.createElement("input");

            trackNameInput.setAttribute("type", "hidden");
            trackNameInput.setAttribute("name", "trackList["+i+"].name");
            trackNameInput.setAttribute("value", $("#" + trackId + "_name option:selected").val());
            trackDateInput.setAttribute("type", "hidden");
            trackDateInput.setAttribute("name", "trackList["+i+"].date");
            trackDateInput.setAttribute("value", $("#" + trackId + "_date").val());
            trackIdInput.setAttribute("type", "hidden");
            trackIdInput.setAttribute("name", "trackList["+i+"].id");
            if($("#" + trackId + "_name").data("id") !== ''){
                trackIdInput.setAttribute("value", $("#" + trackId + "_name").data("id"));
            }else {
                trackIdInput.setAttribute("value", '');
            }

            form.appendChild(trackNameInput);
            form.appendChild(trackDateInput);
            form.appendChild(trackIdInput);
        }
        //endregion tracks to array

        //region categorys to array
        for(let i=0; i <  $("#leagueBtnWrap").children().length; i++) {
            let buttonId = $("#leagueBtnWrap").children("div").get(i).id;
            let leagueButtonNameInput = document.createElement("input");
            let leagueButtonTypeInput = document.createElement("input");
            let leagueButtonId = document.createElement("input");
            leagueButtonNameInput.setAttribute("type", "hidden");
            leagueButtonNameInput.setAttribute("name", "leagueButtonList["+i+"].name");
            leagueButtonNameInput.setAttribute("value", $("#"+buttonId).children("input").val());
            leagueButtonTypeInput.setAttribute("type", "hidden");
            leagueButtonTypeInput.setAttribute("name", "leagueButtonList["+i+"].type");
            leagueButtonTypeInput.setAttribute("value", $("#"+buttonId).children("select").val());
            leagueButtonId.setAttribute("type", "hidden");
            leagueButtonId.setAttribute("name", "leagueButtonList["+i+"].id");
            if($("#"+buttonId).children("input").data("id") !== '') {
                leagueButtonId.setAttribute("value", $("#" + buttonId).children("input").data("id"));
            }else{
                leagueButtonId.setAttribute("value", '');
            }

            form.appendChild(leagueButtonNameInput);
            form.appendChild(leagueButtonTypeInput);
            form.appendChild(leagueButtonId);
        }
        //endregion categorys to array
        //endregion data setup

        form.submit();
    }

    <c:if test="${not empty result}">
    const leagueMessagePush = () => {
        let now = new Date();

        let dateMatchCount = 0;
        for(let i=0; i <  $("#tracksArea").children().length; i++) {
            let trackId = $("#tracksArea").children("div").get(i).id;
            let trackDate = $("#" + trackId + "_date").val();
            if(!!trackDate){
                let date = new Date(trackDate);
                if(now.getFullYear() === date.getFullYear()
                && now.getMonth() === date.getMonth()
                && now.getDate() === date.getDate()){
                    dateMatchCount++;
                }
            }
        }

        if(dateMatchCount === 0){
            alert("오늘 시작라는 경기가 없습니다.\n트랙 정보를 확인해주세요.");
            $("#trackSelect").focus();
            return false;
        }

        if(confirm("즉시 공지를 하더라도 게시 시간에 메세지가 전송됩니다.\n수정되지 않은 내용은 공지에 반영되지 않습니다.\n수정 완료 후 공지 기능을 사용하세요.\n즉시 공지하겠습니까?")){
            $.ajax({
                url: '/api/discord/league-msg-push',
                type: 'GET',
                async: false,
                data: {
                    leagueId : '${result.id}'
                },
                success: function (data) {
                    alert("공지가 완료되었습니다.");
                },
                error: function (request, status, error) {
                    console.error(error);
                    alert("오류가 발생하였습니다.");
                }
            });
        }
    }
    </c:if>
</script>

<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">리그 ${empty result? '등록':'수정'}</h1>
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
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">리그 ${empty result? '등록':'수정'}</h4>
            </div>

            <form id="registForm" name="registForm" method="post" enctype="multipart/form-data" action="${empty result? '/cms/discord/league/save':'/cms/discord/league/update'}">
                <input type="hidden" id="leagueTime" name="leagueTime" value="">
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
                        <th class="table-title"><label for="leagueName">리그명</label></th>
                        <td><input type="text" class="form-control" id="leagueName" name="leagueName" value="${result.leagueName}" aria-label="리그명" placeholder="리그명을 입력하세요."></td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="files">이미지 첨부</label></th>
                        <td>
                            <c:if test="${not empty result.attachFileGroup}">
                                <input type="hidden" name="attachFileGroupId" value="${result.attachFileGroup.id}">
                                <c:if test="${not empty result.attachFileGroup.attachFileList}">
                                    <c:forEach var="attachfile" items="${result.attachFileGroup.attachFileList}" varStatus="status">
                                        <div class="block mb-1" id="attachFileWrap_${status.count}">

                                            <%--<img src="/static${attachfile.filePath.replace('\\','/').split('static')[1]}${attachfile.fileName}" size="width=400px"/>--%>
                                            <img src="/api/attach/get-image/${attachfile.fileName}" style="max-width:300px;max-height: 800px;"/>
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
                        <td><textarea id="description" name="description" rows="20" style="overflow:scroll;width: 100%">${result.description}</textarea></td>
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
                            <c:set var="leagueHour" value=""/>
                            <c:set var="leagueMinute" value=""/>
                            <c:if test="${not empty result}">
                                <c:set var="leagueHour" value="${fn:substring(result.leagueTime,0,2)}"/>
                                <c:set var="leagueMinute" value="${fn:substring(result.leagueTime,3,5)}"/>
                            </c:if>
                            <select class="form-control-sm" id="leagueHour" name="leagueHour">
                                <c:forEach var="item" begin="12" end="24" step="1">
                                    <option value="${item eq '24'? '00':item}" <c:if test="${item eq leagueHour or '00' eq leagueHour}" >selected</c:if>>${item}시</option>
                                </c:forEach>
                            </select>
                            <select class="form-control-sm" id="leagueMinute" name="leagueMinute">
                                <c:forEach var="item" begin="0" end="5" step="1">
                                    <option value="${item}0" <c:if test="${item eq fn:substring(leagueMinute,0,1)}" >selected</c:if>>${item}0분</option>
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
                                    <option value="${item eq '24'? '00':item}" <c:if test="${item eq noticeHour or '00' eq noticeHour}" >selected</c:if>>${item}시</option>
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
                        <th class="table-title"><label for="isJoinDisplay_Y">참여자목록 표기 여부</label></th>
                        <td>
                            <label for="isJoinDisplay_Y"><input type="radio" id="isJoinDisplay_Y" name="isJoinDisplay" value="true" ${result.isJoinDisplay eq true? 'checked':''}>표기</label>
                            <label for="isJoinDisplay_N"><input type="radio" id="isJoinDisplay_N" name="isJoinDisplay" value="false" ${result.isJoinDisplay eq false? 'checked':''}>미표기</label>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="mention_0">참여 가능 역할</label></th>
                        <td>
                            <c:forEach var="item" items="${discordMentionList}" varStatus="index">
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
                        <td><input type="number" id="joinMemberLimit" name="joinMemberLimit" max="20" value="${empty result.joinMemberLimit? 20:result.joinMemberLimit}"/></td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="trackSelect">트랙 선택</label></th>
                        <td>
                            <p style="color: red;font-weight: bold">※ 등록된 트랙을 삭제할 경우 해당 날짜에 참여신청한 데이터도 함께 삭제됩니다.</p>
                            <select class="form-control-sm" id="trackSelect" name="trackSelect">
                                <c:forEach var="item" items="${tackCodeList}" varStatus="index">
                                    <option value="${item.codeId}">${item.codeLabel}</option>
                                </c:forEach>
                            </select>
                            <button type="button" class="btn btn-sm btn-success" onclick="appendTrack();">추가</button>
                            <div id="tracksArea">
                                <c:if test="${not empty result}">
                                    <c:forEach var="leagueTrack" items="${result.leagueTracks}" varStatus="index">
                                        <div id="track_${index.index}">
                                            <select class="form-control-sm" id="track_${index.index}_name" name="trackSelect" data-id="${leagueTrack.id}">
                                                <c:forEach var="item" items="${tackCodeList}">
                                                    <option value="${item.codeId}" ${leagueTrack.trackCode.codeId eq item.codeId? 'selected':''}>${item.codeLabel}</option>
                                                </c:forEach>
                                            </select>
<%--                                            <input type="text" class="form-control-small w-25" id="track_${index.index}_name" value="${leagueTrack.trackCode.codeLabel}" readOnly/>--%>
                                            <input type="date" id="track_${index.index}_date" pattern="yyyy-mm-dd" value="${leagueTrack.trackDate}"/>
                                            <button type="button" class="btn btn-sm btn-danger" onclick="$('#track_${index.index}').remove();">삭제</button>
                                        </div>
                                    </c:forEach>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="leagueButtonName_0">참여 카테고리</label></th>
                        <td>
                            <p style="color: red;font-weight: bold">※ 카테고리 이름과 버튼이름은 동일하게 생성됩니다.</p>
                            <div id="leagueBtnWrap">
                                <c:if test="${not empty result}">
                                    <c:forEach var="leagueButton" items="${result.leagueButtons}" varStatus="index">
                                        <div id="leagueBtnArea_${index.index}">
                                        <input type="text" class="form-control-small w-25 leagueButtonName" id="leagueButtonName_${index.index}" name="leagueButtonName_${index.index}" value="${leagueButton.buttonName}" data-id="${leagueButton.id}" placeholder="버튼명 입력" />
                                        <select class="form-control-sm" id="leagueButtonColor_${index.index}" name="leagueButtonColor_${index.index}">
                                            <option value="Primary" ${leagueButton.buttonType eq 'Primary'? 'selected':''}>파랑</option>
                                            <option value="Success" ${leagueButton.buttonType eq 'Success'? 'selected':''}>초록</option>
                                            <option value="Secondary" ${leagueButton.buttonType eq 'Secondary'? 'selected':''}>그레이</option>
                                            <option value="Danger" ${leagueButton.buttonType eq 'Danger'? 'selected':''}>빨강</option>
                                        </select>
                                            <c:if test="${index.first}">
                                                <button type="button" class="btn btn-sm btn-success" id="leagueButtonAdd" name="leagueButtonAdd" onclick="appendLeagueButton();">추가</button>
                                            </c:if>
                                            <c:if test="${not index.first}">
                                                <button type="button" class="btn btn-sm btn-danger" onclick="$('#leagueBtnArea_${index.index}').remove()">삭제</button>
                                            </c:if>
                                        </div>
                                    </c:forEach>
                                </c:if>
                                <c:if test="${empty result}">
                                    <div id="leagueBtnArea_0">
                                        <input type="text" class="form-control-small w-25 leagueButtonName" id="leagueButtonName_0" name="leagueButtonName_0" data-id="" placeholder="버튼명 입력" />
                                        <select class="form-control-sm" id="leagueButtonColor_0" name="leagueButtonColor_0">
                                            <option value="Primary">파랑</option>
                                            <option value="Success">초록</option>
                                            <option value="Secondary">그레이</option>
                                            <option value="Danger">빨강</option>
                                        </select>
                                        <button type="button" class="btn btn-sm btn-success" id="leagueButtonAdd" name="leagueButtonAdd" onclick="appendLeagueButton();">추가</button>
                                    </div>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div class="form-btn-set text-center">
                    <c:if test="${not empty result}">
                        <button type="button" class="btn btn-dark btn-lg" onclick="leagueMessagePush();">즉시 공지</button>
                    </c:if>
                    <button type="button" class="btn btn-success btn-lg" onclick="formSubmitEvent();">${empty result? '등록':'수정'}</button>
                    <button type="button" class="btn btn-secondary btn-lg" onclick="location.href='/cms/discord/league/list';">취소</button>
                    <c:if test="${not empty result}">
                        <button type="button" class="btn btn-danger btn-lg" onclick="location.href='/cms/discord/league/delete/${result.id}'">삭제</button>
                    </c:if>
                </div>
            </form>
        </div>
    </main>
</div>