<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<style>
	#divContent {width: 100%; background: #f7f7f7; padding: 20px; border-top: 1px solid #bdc0c4; border-bottom: 1px solid #bdc0c4}
</style>

<div id="divContent">
	<sbux-label id="lbId" name="lbId" uitype="normal" text="사용자ID" for-id="txtId"></sbux-label>
	<sbux-input id="txtId" name="txtId" uitype="text" maxlength="9"></sbux-input>
	<sbux-label id="lbPwd" name="lbPwd" uitype="normal" text="비밀번호 4자리" for-id="txtPwd"></sbux-label>
	<sbux-input id="txtPwd" name="txtPwd" uitype="password" maxlength="4" onkeyenter="Cmd_Click()"></sbux-input>
</div>
<sbux-label id="lbTip" name="lbTip" uitype="normal" text="초기화시 비밀번호 4자리로 세팅됩니다." style="text-align:center; margin-top:10px"></sbux-label>
<div id="pwdFooter" style="text-align:center; margin-top:15px">
	<sbux-button id="btnReg" name="btnReg" uitype="normal" text="등록" onclick="Cmd_Click()"></sbux-button>
<!-- Cmd_Ip1 -->
	<sbux-button id="btnClose" name="btnClose" uitype="normal" text="닫기" onclick="closeAlert()"></sbux-button>
</div>
<script type="text/javascript" src="<c:url value="/js/ecams/modal/PopUserInfoPwd.js"/>"></script>