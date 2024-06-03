<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M" crossorigin="anonymous">
        <link href="https://getbootstrap.com/docs/4.0/examples/signin/signin.css" rel="stylesheet" crossorigin="anonymous">
        <title>Siosws</title>
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f0f2f5;
                margin: 0;
                padding: 0;
                display: flex;
                flex-direction: column;
                align-items: center;
            }
            header {
                background-color: #282c34;
                color: white;
                padding: 1rem 0;
                width: 100%;
                text-align: center;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }
            header h1 {
                margin: 0;
                font-size: 2.5rem;
            }
            nav {
                margin: 2rem 0;
                display: flex;
                gap: 1rem;
            }
            nav a {
                padding: 0.5rem 1rem;
                text-decoration: none;
                color: #282c34;
                background-color: white;
                border: 2px solid #282c34;
                border-radius: 25px;
                transition: background-color 0.3s, color 0.3s;
            }
            nav a:hover {
                background-color: #282c34;
                color: white;
            }
            .content {
                text-align: center;
                max-width: 800px;
                margin: 2rem;
                background-color: white;
                padding: 2rem;
                border-radius: 10px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            }
            .content h2 {
                font-size: 2rem;
                margin-bottom: 1rem;
            }
            .content p {
                font-size: 1.2rem;
                line-height: 1.6;
                color: #333;
            }
        </style>
    </head>
    <body>
        <header>
            <h1>Siosws</h1>
        </header>
        <nav>
            <a href="#">VLOG</a>
            <a href="#">ESK - F1 Esports Korea</a>
        </nav>
        <div class="content">
            <h2>Welcome to Siosws!</h2>
            <p>Explore our VLOG and Racing categories. Here you can find the latest updates, news, and exciting content about our favorite topics. Dive into our carefully curated videos and articles, and join our community of enthusiasts.</p>
        </div>
    </body>
</html>