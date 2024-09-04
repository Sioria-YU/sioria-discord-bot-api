<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<script>
    $(function(){
        $(".viewCheckBoxAll").on('click',function(){
            if($(".viewCheckBoxAll").is(":checked")){
                $(".viewCheckBox").prop("checked", true);
            }else{
                $(".viewCheckBox").prop("checked", false);
            }
        });

        $(".viewCheckBox").on('click',function(){
            if(!$(this).is(":checked")){
                $(".viewCheckBoxAll").prop("checked", false);
            }
        });

        $(".createCheckBoxAll").on('click',function(){
            if($(".createCheckBoxAll").is(":checked")){
                $(".createCheckBox").prop("checked", true);
            }else{
                $(".createCheckBox").prop("checked", false);
            }
        });

        $(".createCheckBox").on('click',function(){
            if(!$(this).is(":checked")){
                $(".createCheckBoxAll").prop("checked", false);
            }
        });

        $(".updateCheckBoxAll").on('click',function(){
            if($(".updateCheckBoxAll").is(":checked")){
                $(".updateCheckBox").prop("checked", true);
            }else{
                $(".updateCheckBox").prop("checked", false);
            }
        });

        $(".updateCheckBox").on('click',function(){
            if(!$(this).is(":checked")){
                $(".updateCheckBoxAll").prop("checked", false);
            }
        });

        $(".deleteCheckBoxAll").on('click',function(){
            if($(".deleteCheckBoxAll").is(":checked")){
                $(".deleteCheckBox").prop("checked", true);
            }else{
                $(".deleteCheckBox").prop("checked", false);
            }
        });

        $(".deleteCheckBoxAll").on('click',function(){
            if(!$(this).is(":checked")){
                $(".deleteCheckBox").prop("checked", false);
            }
        });
    });

    const saveFormCheck = () =>{
        let ids = [];
        $(".adminMenuAuthId").each(
            function(){
                ids.push(this.value)
            });

        let adminMenuAuthList = [];
        for(let i of ids){
            let adminMenuAuth = {
                id : $("#adminMenuAuthId_"+i).val(),
                isSelect : $("#viewCheckBox_"+i).is(":checked"),
                isInsert : $("#createCheckBox_"+i).is(":checked"),
                isUpdate : $("#updateCheckBox_"+i).is(":checked"),
                isDelete : $("#deleteCheckBox_"+i).is(":checked")
            }
            adminMenuAuthList.push(adminMenuAuth);
        }

        let form = document.getElementById("saveForm");

        let inputAdminAuthId = document.createElement("input");
        inputAdminAuthId.setAttribute("type", "hidden");
        inputAdminAuthId.setAttribute("name", "adminAuthId");
        inputAdminAuthId.setAttribute("value", '${adminAuth.id}');
        form.appendChild(inputAdminAuthId);

        for(let i=0; i < adminMenuAuthList.length; i++){
            let adminMenuAuth = adminMenuAuthList[i];
            let inputId = document.createElement("input");
            let inputIsSelect = document.createElement("input");
            let inputIsInsert = document.createElement("input");
            let inputIsUpdate = document.createElement("input");
            let inputIsDelete = document.createElement("input");

            inputId.setAttribute("type", "hidden");
            inputId.setAttribute("name", "adminMenuAuthList["+i+"].id");
            inputId.setAttribute("value", adminMenuAuth.id);

            inputIsSelect.setAttribute("type", "hidden");
            inputIsSelect.setAttribute("name", "adminMenuAuthList["+i+"].isSelect");
            inputIsSelect.setAttribute("value", adminMenuAuth.isSelect);

            inputIsInsert.setAttribute("type", "hidden");
            inputIsInsert.setAttribute("name", "adminMenuAuthList["+i+"].isInsert");
            inputIsInsert.setAttribute("value", adminMenuAuth.isInsert);

            inputIsUpdate.setAttribute("type", "hidden");
            inputIsUpdate.setAttribute("name", "adminMenuAuthList["+i+"].isUpdate");
            inputIsUpdate.setAttribute("value", adminMenuAuth.isUpdate);

            inputIsDelete.setAttribute("type", "hidden");
            inputIsDelete.setAttribute("name", "adminMenuAuthList["+i+"].isDelete");
            inputIsDelete.setAttribute("value", adminMenuAuth.isDelete);

            form.appendChild(inputId);
            form.appendChild(inputIsSelect);
            form.appendChild(inputIsInsert);
            form.appendChild(inputIsUpdate);
            form.appendChild(inputIsDelete);
        }

        form.action = "/cms/admin-menu-auth/update";
        form.enctype = "multipart/form-data";
        form.method = "post"
        form.submit();
    }
</script>
<form id="saveForm"></form>

<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">관리자 메뉴 권한 관리</h1>
                <nav>
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                        <li class="breadcrumb-item">시스템관리</li>
                        <li class="breadcrumb-item active">관리자 메뉴 권한 관리 상세</li>
                    </ol>
                </nav>
            </div>
            <div class="container-fluid px-4">
                <div class="icon">
                    <i class="bi bi-record-circle-fill"></i><h4 class="card-title">관리자 메뉴 권한 관리 상세</h4>
                </div>

                <div class="row mb-3">
                    <h5>▶ 권한명 : ${adminAuth.name}</h5>
                </div>
            </div>

            <table class="table text-center">
                <thead>
                <tr>
                    <th scope="col" style="width: 20%">메뉴명</th>
                    <th scope="col" style="width: 20%"><label for="viewCheckBoxAll">조회 <input type="checkbox" class="viewCheckBoxAll" id="viewCheckBoxAll"></label></th>
                    <th scope="col" style="width: 20%"><label for="createCheckBoxAll">등록 <input type="checkbox" class="createCheckBoxAll" id="createCheckBoxAll"></label></th>
                    <th scope="col" style="width: 20%"><label for="updateCheckBoxAll">수정 <input type="checkbox" class="updateCheckBoxAll" id="updateCheckBoxAll"></label></th>
                    <th scope="col" style="width: 20%"><label for="deleteCheckBoxAll">삭제 <input type="checkbox" class="deleteCheckBoxAll" id="deleteCheckBoxAll"></label></th>
                </tr>
                </thead>
                <tbody>
                    <c:forEach var="adminMenuAuth" items="${adminMenuAuthList}" varStatus="status">
                        <input type="hidden" class="adminMenuAuthId" id="adminMenuAuthId_${adminMenuAuth.id}" name="adminMenuAuthId_${adminMenuAuth.id}" value="${adminMenuAuth.id}">
                        <tr>
                            <td style="text-align: left">
                                <c:if test="${not empty adminMenuAuth.menu.upperMenu and adminMenuAuth.menu.upperMenu.id ne 1}">
                                    <c:set var="item" value="${adminMenuAuth.menu}"/>
                                    <c:set var="parentItem" value="${adminMenuAuth.menu.upperMenu}"/>
                                    <c:forEach begin="1" end="5" step="1">
                                        <c:if test="${parentItem.id ne 1}">
                                            &emsp;
                                            <c:set var="item" value="${parentItem}"/>
                                            <c:set var="parentItem" value="${parentItem.upperMenu}"/>
                                        </c:if>
                                    </c:forEach>
                                    <c:out value="┖─"/>
                                </c:if>
                                ${adminMenuAuth.menu.menuName}
                            </td>
                            <td><input type="checkbox" class="viewCheckBox" id="viewCheckBox_${adminMenuAuth.id}" name="viewCheckBox_${adminMenuAuth.id}" ${!!adminMenuAuth.isSelect? 'checked':''}></td>
                            <td><input type="checkbox" class="createCheckBox" id="createCheckBox_${adminMenuAuth.id}" name="createCheckBox_${adminMenuAuth.id}" ${!!adminMenuAuth.isInsert? 'checked':''}></td>
                            <td><input type="checkbox" class="updateCheckBox" id="updateCheckBox_${adminMenuAuth.id}" name="updateCheckBox_${adminMenuAuth.id}" ${!!adminMenuAuth.isUpdate? 'checked':''}></td>
                            <td><input type="checkbox" class="deleteCheckBox" id="deleteCheckBox_${adminMenuAuth.id}" name="deleteCheckBox_${adminMenuAuth.id}" ${!!adminMenuAuth.isDelete? 'checked':''}></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" onclick="location.href='/cms/admin-menu-auth/list'">취소</button>
            <button type="button" class="btn btn-success" onclick="saveFormCheck();">저장</button>
        </div>
    </main>
</div>