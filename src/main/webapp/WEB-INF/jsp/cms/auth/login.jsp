<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta name="description" content="">
        <meta name="author" content="">

        <title>관리자 로그인 페이지</title>
        <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M" crossorigin="anonymous">
        <link href="https://getbootstrap.com/docs/4.0/examples/signin/signin.css" rel="stylesheet" crossorigin="anonymous">

        <style>
            body {
                margin: 0;
                font-family: Arial, sans-serif;
                background: linear-gradient(to bottom, #000, #333);
                color: #fff;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
            }
            .login-container {
                background: #111;
                border: 2px solid #f00;
                border-radius: 10px;
                box-shadow: 0 0 15px rgba(255, 0, 0, 0.5);
                padding: 20px;
                width: 90%;
                max-width: 400px;
                text-align: center;
            }
            .login-container h1 {
                font-size: 2rem;
                color: #f00;
                margin-bottom: 1rem;
            }
            .login-container input {
                width: 100%;
                padding: 10px;
                margin: 10px 0;
                border: none;
                border-radius: 5px;
            }
            .login-container button {
                background: #f00;
                color: #fff;
                padding: 10px;
                width: 100%;
                border: none;
                border-radius: 5px;
                font-size: 1rem;
                cursor: pointer;
                margin-top: 10px;
            }
            .login-container button:hover {
                background: #c00;
            }
            .logo {
                margin-bottom: 20px;
            }
            .logo img {
                max-width: 100%;
            }
        </style>
    </head>
    <body>
        <div class="login-container">
            <div class="logo">
                <img src="/static/assets/img/f1_logo.webp" alt="F1 Logo">
            </div>
            <h1>F1 ESK MANAGER'S</h1>
            <form method="POST" action="/cms/auth/login-process">
                <input type="text" id="userId" name="userId" placeholder="Username" autofocus required>
                <input type="password" id="userPw" name="userPw" placeholder="Password" required>
                <button type="submit" class="btn btn-lg btn-success btn-block">로그인</button>
            </form>
        </div>
    </body>
</html>
