<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<style>
	#lb1 {background: #bdc0c4; border-radius: 50%; color: white; font-style:bold}
	#btnPw {background: #656970; color:white; font-style:bold;}
	#divContent {width: 380px; background: #f7f7f7; padding: 20px; margin: 0 auto; border-top: 1px solid #bdc0c4; border-bottom: 1px solid #bdc0c4}
	.row {margin-top: 10px}
</style>

<section>
	<div class="container-fluid padding-40-top">
		<div id="divContent">
			<div class="row-fluid">
				<div class="row">
					<div class="col-xs-12 col-sm-4">
						<label id="lbUserId">사용자ID</label>
					</div>
					<div class="col-xs-12 col-sm-8">
						<input id="txtUserId" name="txtUserId" type="text" disabled="disabled"></input>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-4">
						<label id="lbUserName">성명</label>
					</div>
					<div class="col-xs-12 col-sm-8">
						<input id="txtUserName" name="txtUserName" type="text" disabled="disabled"></input>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-4">
						<label id="lbPw" >변경전비밀번호</label>
					</div>
					<div class="col-xs-12 col-sm-8">
						<input id="txtPw" name="txtPw" type="password" minlength="8" maxlength="12" ></input>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-4">
						<label id="lbUpdatePw1">변경후비밀번호</label>
					</div>
					<div class="col-xs-12 col-sm-8">
						<input id="txtUpdatePw1" name="txtUpdatePw1" type="password" minlength="8" maxlength="12"></input>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-4">
						<label id="lbUpdatePw2">확인비밀번호</label>
					</div>
					<div class="col-xs-12 col-sm-8">
						<input id="txtUpdatePw2" name="txtUpdatePw2" type="password" minlength="8" maxlength="12"></input>
					</div>
				</div>
				<div class="row margin-15-top">
					<div class="col-xs-12 col-sm-2">
						<label id="lb1">TIP</label>
					</div>
					<div class="col-xs-12 col-sm-10">
						<label id="lb2" style="color:#bdc0c4">비밀번호는 숫자/영문/특수기호를 포함하는</label>
					</div>
				</div>
				<div class="row margin-10-top">
					<div class="col-xs-12 col-sm-2">
					</div>
					<div class="col-xs-12 col-sm-10">
						<label id="lb3" style="color:#bdc0c4" >8-12자리 이어야 합니다. (분기별 패스워드 변경)</label>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<section>
	<div id="divPw" class="margin-15-top" style="text-align:center">
		<sbux-button id="btnPw" name="btnPw" uitype="normal" text="비밀번호변경" onclick="clickBtnPw()"></sbux-button>
	</div>
</section>
<c:import url="/js/ecams/common/commonscript.jsp" />
<script type="text/javascript" src="<c:url value="/js/ecams/mypage/PwdChange.js"/>"></script>