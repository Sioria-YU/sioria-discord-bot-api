<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
<script src="/static/js/scripts.js"></script>
<c:if test="${not empty msg}">
    <script>
        $(function(){
           alert('${msg}');
        });
    </script>
</c:if>


<script>
    const attachFileDelete = (fileName, selector) => {
        if(confirm("삭제된 파일은 복구되지 않습니다.\n삭제하시겠습니까?")) {
            $.ajax({
                url: '/api/attach/delete',
                type: 'DELETE',
                async: false,
                data: {
                    fileName     : fileName,
                    deleteMode : 'D'
                },
                success: function (data) {
                    $("#"+selector).remove();
                    alert("삭제 처리되었습니다.");
                },
                error: function (request, status, error) {
                    console.error(error);
                    alert("오류가 발생하였습니다.");
                }
            });
        }
    }

    const attachFileDownload = (fileName) => {
        window.open("/api/attach/download/"+fileName);
    }

    function showLoading() {
        document.getElementById("loadingOverlay").classList.remove("d-none");
    }

    // 로딩 화면 숨기기
    function hideLoading() {
        document.getElementById("loadingOverlay").classList.add("d-none");
    }
</script>

<!-- Loading 화면 오버레이 -->
<div id="loadingOverlay" class="d-none" style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 9999; display: flex; align-items: center; justify-content: center;">
    <div class="spinner-border text-light" role="status">
        <span class="visually-hidden">Loading...</span>
    </div>
</div>