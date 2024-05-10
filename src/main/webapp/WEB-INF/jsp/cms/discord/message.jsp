<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script>
    const sendMessage = () =>{
        $.ajax({
            url: '/cms/Discord/send-msg',
            type: 'POST',
            async: false,
            success: function (data) {
                alert("성공.");
            },
            error: function (request, status, error) {
                console.error(error);
                alert("오류가 발생하였습니다.");
            }
        });
    }
</script>

<div id="layoutSidenav_content">
    <main>
        <div class="container-fluid px-4">
            <div class="pagetitle">
                <h1 class="mt-4">콘텐츠 관리</h1>
                <nav>
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/cms/main"><i class="bi bi-house-door"></i></a></li>
                        <li class="breadcrumb-item">사이트 관리</li>
                        <li class="breadcrumb-item active">디스코드 관리</li>
                    </ol>
                </nav>
            </div>
        </div>

        <div class="container-fluid px-4">
            <div class="icon">
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">디스코드 관리</h4>
            </div>

            <button type="button" class="btn btn-success btn-lg" id="sendButton" onclick="sendMessage()">send22</button>
        </div>
    </main>
</div>