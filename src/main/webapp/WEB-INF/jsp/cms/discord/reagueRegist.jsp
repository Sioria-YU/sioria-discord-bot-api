<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
                <input type="hidden" name="boardMasterId" value="${param.boardMasterId}">
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
                        <th class="table-title"><label for="title">리그명</label></th>
                        <td><input type="text" class="form-control" id="title" name="title" value="${result.title}" aria-label="리그명" placeholder="리그명을 입력하세요."></td>
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
                            <input type="file" class="form-control" id="files" name="files" accept="image/*" multiple>
                        </td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="description">설명</label></th>
                        <td><textarea id="description" name="description" style="width: 100%">${result.description}</textarea></td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="option2">옵션2</label></th>
                        <td><input type="text" class="form-control" id="option2" name="option2" value="${result.option2}" aria-label="옵션2" placeholder="옵션2을 입력하세요."></td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="option3">옵션3</label></th>
                        <td><input type="text" class="form-control" id="option3" name="option3" value="${result.option3}" aria-label="옵션3" placeholder="옵션3을 입력하세요."></td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="option4">옵션4</label></th>
                        <td><input type="text" class="form-control" id="option4" name="option4" value="${result.option4}" aria-label="옵션4" placeholder="옵션4을 입력하세요."></td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="option5">옵션5</label></th>
                        <td><input type="text" class="form-control" id="option5" name="option5" value="${result.option5}" aria-label="옵션5" placeholder="옵션5을 입력하세요."></td>
                    </tr>
                    <tr>
                        <th class="table-title"><label for="content">내용</label></th>
                        <td>
                            <textarea id="content" name="content" style="width: 100%">${result.content}</textarea>
                            <script type="text/javascript">
                                var oEditors = [];
                                nhn.husky.EZCreator.createInIFrame({
                                    oAppRef: oEditors,
                                    elPlaceHolder: "content",
                                    sSkinURI: "/static/se2/SmartEditor2Skin.html",
                                    fCreator: "createSEditor2"
                                });
                            </script>
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