<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

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
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">참여 현황 상세</h4>
            </div>

            <div class="icon">
                <i class="bi bi-record-circle-fill"></i><h4 class="card-title">정규리그</h4>
            </div>

            <p style="font-size: 18px;font-weight: bold;margin-bottom: 25px">
                <i class="bi bi-record-circle-fill"></i> 트랙 선택
                <select class="" id="" name="">
                    <option value="">바레인</option>
                    <option value="">케나다</option>
                </select>
            </p>


            <div class="container-fluid px-4">
                ● ✅️ 참가
                <span class="btn btn-success btn-mg">추가</span>
                <table class="table text-center">
                    <thead>
                    <tr>
                        <th scope="col">순번</th>
                        <th scope="col">아이디(닉네임)</th>
                        <th scope="col">참여구분</th>
                        <th scope="col">포인트</th>
                        <th scope="col"></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <th scope="row">3</th>
                        <td>시오리아</td>
                        <td>
                            <select>
                                <option selected>DNF</option>
                                <option>DNS</option>
                                <option>완주</option>
                                <option>중계</option>
                            </select>
                        </td>
                        <td><input type="number" class="form-control" value=""></td>
                        <td>
                            <span class="btn btn-danger btn-mg">삭제</span>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row">2</th>
                        <td>철선</td>
                        <td>
                            <select>
                                <option>DNF</option>
                                <option>DNS</option>
                                <option selected>완주</option>
                                <option>중계</option>
                            </select>
                        </td>
                        <td><input type="number" class="form-control" value="25"></td>
                        <td>
                            <span class="btn btn-danger btn-mg">삭제</span>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row">3</th>
                        <td>알파고</td>
                        <td>
                            <select>
                                <option>DNF</option>
                                <option>DNS</option>
                                <option selected>완주</option>
                                <option>중계</option>
                            </select>
                        </td>
                        <td><input type="number" class="form-control" value="20"></td>
                        <td>
                            <span class="btn btn-danger btn-mg">삭제</span>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <div class="container-fluid px-4">
                ● 🎙️ 중계
                <span class="btn btn-success btn-mg">추가</span>
                <table class="table text-center">
                    <thead>
                    <tr>
                        <th scope="col">순번</th>
                        <th scope="col">아이디(닉네임)</th>
                        <th scope="col">참여구분</th>
                        <th scope="col">포인트</th>
                        <th scope="col"></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <th scope="row">2</th>
                        <td>리프팅턴</td>
                        <td>
                            <select>
                                <option>DNF</option>
                                <option>DNS</option>
                                <option>완주</option>
                                <option selected>중계</option>
                            </select>
                        </td>
                        <td><input type="number" class="form-control" value="" disabled></td>
                        <td>
                            <span class="btn btn-danger btn-mg">삭제</span>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row">1</th>
                        <td>스크렛</td>
                        <td>
                            <select>
                                <option>DNF</option>
                                <option>DNS</option>
                                <option>완주</option>
                                <option selected>중계</option>
                            </select>
                        </td>
                        <td><input type="number" class="form-control" value="25" disabled></td>
                        <td>
                            <span class="btn btn-danger btn-mg">삭제</span>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>