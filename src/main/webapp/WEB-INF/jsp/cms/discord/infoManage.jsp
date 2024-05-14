<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script>
    const refreshMembers = () => {
        if(confirm("동기화 하시겠습니까?")) {
            $.ajax({
                type: 'GET',
                url: '/api/discord/member-refresh',
                async: false,
                success: function (data) {
                    if (data) {
                        alert("동기화가 완료되었습니다.");
                        location.reload();
                    } else {
                        alert("오류가 발생하였습니다.");
                        return false;
                    }
                },
                error: function (request, status, error) {
                    console.log(error);
                }
            });
        }else {
            return false;
        }
    }

    const refreshRoles = () => {
        if(confirm("동기화 하시겠습니까?")) {
            $.ajax({
                type: 'GET',
                url: '/api/discord/roles-refresh',
                async: false,
                success: function (data) {
                    if (data) {
                        alert("동기화가 완료되었습니다.");
                        location.reload();
                    } else {
                        alert("오류가 발생하였습니다.");
                        return false;
                    }
                },
                error: function (request, status, error) {
                    console.log(error);
                }
            });
        }else {
            return false;
        }
    }
</script>

<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">정보 관리</h1>
                <nav>
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                        <li class="breadcrumb-item">사이트 관리</li>
                        <li class="breadcrumb-item">디스코드 관리</li>
                        <li class="breadcrumb-item active">정보 관리</li>
                    </ol>
                </nav>
            </div>
        </div>

        <div class="container-fluid px-4">
            <div class="icon">
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">디스코드 관리</h4>
            </div>

            <button type="button" class="btn btn-success btn-lg" onclick="refreshMembers();">가입자 동기화</button>
            <button type="button" class="btn btn-success btn-lg" onclick="refreshRoles();">권한(멘션) 동기화</button>

        </div>
    </main>
</div>