<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- 메뉴버튼 권한 --%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<script>
    $(function () {
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
    });

    const changePageOffset = (cnt) => {
        $("#pageOffset").val(cnt);
        $("#searchForm").submit();
    };

    const deleteMentions = () => {
        if($(".checkItem:checked").length < 1){
            alert("최소 1개 이상 선택해야합니다.");
            return false;
        }

        if(confirm("삭제하시겠습니까?")) {
            showLoading();

            setTimeout(() => {
                let ids = [];
                $(".checkItem:checked").each(function() {
                    ids.push(this.value);
                });

                $.ajax({
                    url: '/cms/api/discord/mention/multi-delete',
                    type: 'DELETE',
                    async: false,
                    data: {
                        mentionId: ${mentionInfo.id},
                        ids: ids
                    },
                    success: function(data) {
                        if (data) {
                            alert("삭제 처리되었습니다.");
                            location.reload();
                        } else {
                            alert("삭제 처리 중 오류가 발생하였습니다.");
                        }
                    },
                    error: function(request, status, error) {
                        console.error(error);
                        alert("오류가 발생하였습니다.");
                    },
                    complete: function() { // AJAX 완료 후 로딩 숨기기
                        hideLoading();
                    }
                });
            }, 100); // 약간의 딜레이를 줌
        }
    }
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
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">멘션 상세 조회</h4>
            </div>

            <div class="container-fluid px-4">
                <form id="searchForm" name="searchForm" action="/cms/discord/mention/view/${mentionInfo.id}">
                    <input type="hidden" id="pageNumber" name="pageNumber" value="${empty param.pageNumber? 1:param.pageNumber}">
                    <input type="hidden" id="pageOffset" name="pageOffset" value="${empty param.pageOffset? 10:param.pageOffset}">
                    <input type="hidden" id="pageSize" name="pageSize" value="${empty param.pageSize? 5:param.pageSize}">
                </form>

                <div class="icon">
                    <i class="bi bi-record-circle-fill"></i><h4 class="card-title">멘션 정보</h4>
                </div>

                <table class="table text-center">
                    <tr>
                        <th scope="col" style="background-color: #f5f6f9">권한 아이디</th>
                        <td>${mentionInfo.roleId}</td>
                    </tr>
                    <tr>
                        <th scope="col" style="background-color: #f5f6f9">권한명</th>
                        <td>${mentionInfo.roleName}</td>
                    </tr>
                    <tr>
                        <th scope="col" style="background-color: #f5f6f9">멘션</th>
                        <td>${mentionInfo.mention}</td>
                    </tr>
                </table>

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
                                <option value="200" ${param.pageOffset eq 200? 'selected':''}>200개씩</option>
                                <option value="500" ${param.pageOffset eq 500? 'selected':''}>500개씩</option>
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
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty resultList}">
                            <c:forEach var="result" items="${resultList}" varStatus="status">
                                <tr>
                                    <td><input type="checkbox" class="form-check-input checkItem" name="boardMasterCheck" value="${result.id}"></td>
                                    <th scope="row">${pageInfo.totalCount - ((pageInfo.pageNumber-1) * pageInfo.pageOffset + status.index)}</th>
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
                    <button type="button" class="btn btn-secondary btn-lg" onclick="location.href='/cms/discord/mention/list';">목록</button>
                    <button type="button" class="btn btn-danger btn-lg" onclick="deleteMentions();">선택 제거</button>
                </div>
            </div>
        </div>
    </main>
</div>
