<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<!DOCTYPE html>
<html>
<head>
	<c:import url="/webPage/common/common.jsp" />
	<title>형상관리 시스템</title>
</head>

<body>
	<c:import url="/webPage/main/menuBar.jsp"/>
	
	<section>
		<div class="row-fluid title-bar">
			<img src="<c:url value="/img/iconTitle.gif"/>">
			<span id="titleText">[개발] | 체크아웃현황</span> <!--  #E0F8F7 -->
		</div>
	</section>
	
	<iframe  class="ecams-subframe" name="ecamsSubframe"></iframe>
	<script type="text/javascript" src="<c:url value="/js/ecams/main/eCAMSBase.js"/>"></script>
</body>

</html>