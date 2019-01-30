<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>     

<sbux-menu 	id="menu_json" 
			name="menu_json" 
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

<script type="text/javascript" src="<c:url value="/js/ecams/main/menuBar.js"/>"></script>
