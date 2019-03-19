<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<section>

	<div class="pop-header">
		<div class="col-xs-4">
			<label id="lbSub">공지사항 등록</label>
		</div>
		<div class="col-xs-4"></div>
		<div class="col-xs-4">
			<button type="button" class="close" aria-label="닫기" onclick="popClose()">
			  <span aria-hidden="true">&times;</span>
			</button>
		</div> 
	</div>
	<div class="container-fluid">
		<div class="row">
			<div class="col-sm-12 col-xs-12">
				<label id="lbSub">제목</label>
				<input id="txtTitle" name="txtTitle" class="form-control" type="text"></input>
			</div>
		</div>
		<!-- 	CM_TITLE -->
		<div class="row">
			<div class="col-sm-12 col-xs-12">
				<textarea id="textareaContents" name="textareaContents" class="form-control margin-15-top" rows="12"></textarea>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-2 col-xs-2">
				<div class="form-check" id="chkNotice" onclick="Chk_NotiYN_Click()">
					<input type="checkbox" class="form-check-input" id="exampleCheck1">
		    		<label class="form-check-label" for="exampleCheck1" >팝업공지</label>
				</div>
			</div>
			<div class="col-sm-6 col-xs-6">
				<div id="divPicker" class="input-group" data-ax5picker="basic">
		            <input id="dateStD" name="dateStD" type="text" class="form-control" placeholder="yyyy/mm/dd">
					<span class="input-group-addon">~</span>
					<input id="dateEdD" name="dateEdD" type="text" class="form-control" placeholder="yyyy/mm/dd">
		            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
				</div>
			</div>
		</div>
		
		<div class="row">
			<div class="col-sm-2 col-xs-2">
				<button id="btnFile" name="btnFile" class="btn btn-default margin-15-top" onclick="fileOpen()">파일첨부</button>
			</div>
			<div class="col-sm-5 col-xs-5"></div>
			<div class="col-sm-5 col-xs-5">
				<div class="col-sm-4 col-xs-4 no-padding">
					<button id="btnRem" name="btnRem" class="btn btn-default margin-15-top width-100" onclick="del()">삭제</button>
				</div>
				<div class="col-sm-4 col-xs-4 no-padding">
					<button id="btnReg" name="btnReg" class="btn btn-default margin-15-top width-100" onclick="update()">등록</button>
				</div>
				<div class="col-sm-4 col-xs-4 no-padding">
					<button id="btnClo" name="btnClo" class="btn btn-default margin-15-top width-100" onclick="popClose()">닫기</button>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- <modal id="modalFileUp" name="modalFileUp" uitype="middle" header-title="공지사항 등록" body-html-id="modalBody">
</modal> 
<div id="modalBody">
	<IFRAME id="popFileUp" src="<c:url value="/webPage/modal/FileUp.jsp"/>" width="564" height="480"></IFRAME>
</div>
	-->			
<c:import url="/js/ecams/common/commonscript.jsp" />	
<script type="text/javascript" src="<c:url value="/js/ecams/modal/PopNotice.js"/>"></script>