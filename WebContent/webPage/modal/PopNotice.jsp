<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<section>
	<sbux-label id="lbSub" name="lbSub" uitype="normal" text="제목"></sbux-label>
	<sbux-input id="txtTitle" name="txtTitle" uitype="text"></sbux-input>
<!-- 	CM_TITLE -->
	<sbux-textarea id="textareaContents" name="textareaContents" uitype="normal"></sbux-textarea>
<!-- 	CM_CONTENTS -->
	<sbux-checkbox id="chkNotice" name="chkNotice" uitype="normal" text="팝업공지" onclick="Chk_NotiYN_Click()"></sbux-checkbox>
<!-- 	Chk_NotiYN -->
	<sbux-button id="btnFile" name="btnFile" uitype="normal" text="파일첨부" onclick="fileOpen()"></sbux-button>
<!-- 	Cmd_Ip1 -->
	<sbux-button id="btnFile1" name="btnFile1" uitype="normal" text="첨부파일" onclick="fileOpen()"></sbux-button>
<!-- 	Cmd_Ip1_2 -->
	<sbux-button id="btnRem" name="btnRem" uitype="normal" text="삭제" onclick="del()"></sbux-button>
<!-- 	Cmd_Ip3 -->
	<sbux-button id="btnReg" name="btnReg" uitype="normal" text="등록" onclick="update()"></sbux-button>
<!-- 	Cmd_Ip2 -->
	<sbux-button id="btnClo" name="btnClo" uitype="normal" text="닫기" onclick="parentfun()"></sbux-button>
	<sbux-picker id="dateStD" name="dateStD" uitype="date" mode="popup" date-format="YYYY-MM-DD"></sbux-picker>
<!-- 	CM_STDATE -->
	<sbux-label id="lbFrom" name="lbFrom" uitype="normal" text="~"></sbux-label>
<!-- 	Lal_wave -->
	<sbux-picker id="dateEdD" name="dateEdD" uitype="date" mode="popup" date-format="YYYY-MM-DD"></sbux-picker>
<!-- 	CM_EDDATE -->

	<sbux-label id="lbTo" name="lbTo" uitype="normal" text="까지"></sbux-label>
<!-- 	Lal_util -->
</section>
<sbux-modal id="modalFileUp" name="modalFileUp" uitype="middle" header-title="공지사항 등록" body-html-id="modalBody">
</sbux-modal>
<div id="modalBody">
	<IFRAME id="popFileUp" src="<c:url value="/webPage/modal/FileUp.jsp"/>" width="564" height="480"></IFRAME>
</div>
				
<script type="text/javascript" src="<c:url value="/js/ecams/modal/PopNotice.js"/>"></script>