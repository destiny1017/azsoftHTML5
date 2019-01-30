<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />


<style>
    html,body{width : 100%; height : 100%; margin : 0; padding : 0; }
</style>

<div id="m_sbGridArea" style="height:550px;"></div>

<span style="float:right; padding-top:5px; padding-left:1px;"> 
	<sbux-button id="btnClose" name="btnClose" uitype="normal" text="닫기" button-size="middle" button-color="default" onclick="btnClose_Click()"></sbux-button> 
</span>
<span style="float:right; padding-top:5px; padding-left:1px;"> 
	<sbux-button id="btnDel" name="btnDel" uitype="normal" text="삭제" button-size="middle" button-color="default" onclick="btnDel_Click()"></sbux-button>
</span>
<span style="float:right; padding-top:5px; padding-left:1px;"> 
	<sbux-button id="btnEdit" name="btnEdit" uitype="normal" text="편집" button-size="middle" button-color="default" onclick="btnEdit_Click()"></sbux-button> <!-- onclick="btnEdit_Click()" target-id="modal_edit"-->
</span>
<span style="float:right; padding-top:5px; padding-left:1px;"> 
	<sbux-button id="btnNew" name="btnNew" uitype="normal" text="새로만들기" button-size="middle" button-color="default" onclick="btnNew_Click()" target-id="idxModal_small"></sbux-button> 
</span>

<script type="text/javascript" src="<c:url value="/js/ecams/modal/TaskInfo.js"/>"></script>
