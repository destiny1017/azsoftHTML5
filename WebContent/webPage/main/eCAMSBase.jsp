<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="sessionID" value="${param.sessionID }"></c:set>    
<!DOCTYPE html>
<html>
<head>
	<c:import url="/webPage/common/common.jsp" />
	<title>형상관리 시스템</title>
</head>
<body>

<!-- Header -->
<div id="header">
    <!-- <div class="color-line">
    </div> -->
    <div id="logo" class="light-version">
    	<img src="<c:url value="/img/top_log.gif"/>" alt="logo" onclick="goHome()">
    </div>
    <nav role="navigation">
        <div class="header-link hide-menu"><i class="fa fa-bars"></i></div>
        <div class="small-logo">
            <span class="text-primary">eCAMS LOGO</span>
        </div>
        
        <form role="search" class="navbar-title-text-main" method="post" action="#">
        	<span id="ecamsTitleText">eCAMS MAIN PAGE</span>
        </form> 
      <!--  	<form role="search" class="navbar-form-custom" method="post" action="#">
            <div class="form-group">
                <input type="text" placeholder="검색하세요." class="form-control" name="search">
               
            </div>
        </form> -->
        <div class="mobile-menu">
            <button type="button" class="navbar-toggle mobile-menu-toggle" data-toggle="collapse" data-target="#mobile-collapse">
                <i class="fa fa-chevron-down"></i>
            </button>
            <div class="collapse mobile-navbar" id="mobile-collapse">
                <ul class="nav navbar-nav">
                    <li>
                        <a class="" href="login.html">Login</a>
                    </li>
                    <li>
                        <a class="" href="login.html">Logout</a>
                    </li>
                    <li>
                        <a class="" href="profile.html">Profile</a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="navbar-right">
            <ul class="nav navbar-nav no-borders">
                <li class="dropdown">
                    <a href="#" style="font-size: 10px; margin-top: 2px;" id="loginUserName">로그인</a>
                </li>
                <li class="dropdown">
                    <a href="#" onclick="logOut()">
                        <i class="pe-7s-upload pe-rotate-90"></i>
                    </a>
                </li>
            </ul>
        </div>
    </nav>
</div>

<!-- Navigation -->
<aside id="menu" style="overflow:auto;">
    <div id="navigation" >
        <%-- 
        	사이드 메뉴 상단에 프로필(접속자 사진 , 접속자명 등) 주석.. 
        <div class="profile-picture">
            <a href="index.jsp">
                <img src="<c:url value="/img/profile.jpg"/>" class="img-circle m-b" alt="logo">
            </a>

            <div class="stats-label text-color">
                <span class="font-extra-bold font-uppercase" id="loginUserName">접속자이름여기에</span>

                <div class="dropdown">
                    <a class="dropdown-toggle" href="#" data-toggle="dropdown">
                        <small class="text-muted">개인정보(로그아웃,프로필등) <b class="caret"></b></small>
                    </a>
                    <ul class="dropdown-menu animated flipInX m-t-xs">
                        <li><a href="contacts.html">Contacts</a></li>
                        <li><a href="profile.html">Profile</a></li>
                        <li><a href="analytics.html">Analytics</a></li>
                        <li class="divider"></li>
                        <li><a href="login.html">Logout</a></li>
                    </ul>
                </div>
            </div>
        </div> --%>
        <ul class="nav" id="side-menu">
        </ul>
    </div>
</aside>

<div id="wrapper">
	<div id="eCAMSFrame">
	</div>
</div>
<!-- Footer-->
<footer class="footer">
    <span class="pull-right">
        Copyright ⓒ AZSoft Corp. All Right Reserved
    </span>
	<img src="<c:url value="/img/azsoft.jpg"/>">
</footer>
<input id="txtSessionID" type="hidden" value="${sessionID}">

<c:import url="/js/ecams/common/commonscript.jsp" />
<script type="text/javascript" src="<c:url value="/js/ecams/main/eCAMSBase.js"/>"></script>

</body>

</html>