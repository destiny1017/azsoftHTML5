<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>     


<style>
	.menu{width: 80%; margin: 0 !important;}
</style>

<sbux-menu 	
		id="menu_json" 
		name="menu_json"
		wrap-class="menu" 
		uitype="normal" 
		jsondata-ref="menuJson" 
		jsondata-text="text" 
        jsondata-link="link" 
        jsondata-id="id" 
        jsondata-pid="pid" 
        jsondata-order="order" 
        css-style="font-size:12px"
        trigger="click"
        onclick="menuBarClick(event)">
	<brand-item text="eCAMS" image-src="<c:url value="/img/top_log.gif"/>" image-title="로고이미지"></brand-item>
</sbux-menu>

<div class="right-menu-contents">
	<div class="row">
	    <div class="margin-15-right">
		    <sbux-label 
				id="lbLoginUserName"
				text="xx님 로그인" 
				class="right-menu-contents-label"
				uitype="normal">
			</sbux-label>
			<sbux-label 
				id="lbLogOut"
				text="로그아웃" 
				class="right-menu-contents-label"
				uitype="normal"
				onclick="logOut()">
			</sbux-label>
			<sbux-label 
				id="lbHome"
				text="HOME" 
				class="right-menu-contents-label"
				uitype="normal"
				onclick="goHome()">
			</sbux-label>
		</div>
	</div>
</div>	


<script type="text/javascript" src="<c:url value="/js/ecams/main/menuBar.js"/>"></script>
