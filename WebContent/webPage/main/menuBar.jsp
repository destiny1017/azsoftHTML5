<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>     

<sbux-menu 	id="menu_json" 
			name="menu_json" 
			uitype="normal" 
			jsondata-ref="menuJson"
            css-style="font-size:12px"
            trigger="click"
            onclick="menuBarClick(event)">
<!-- 	<brand-item text="eCAMS"></brand-item> -->
	<brand-item text="eCAMS"></brand-item>
</sbux-menu>

<script type="text/javascript" src="<c:url value="/js/ecams/main/menuBar.js"/>"></script>
