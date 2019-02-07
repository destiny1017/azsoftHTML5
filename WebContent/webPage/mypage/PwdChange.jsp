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
					<div class="col-xs-12 col-sm-3">
						<sbux-label id="lbUserId" name="lbUserId" uitype="normal" text="사용자ID" for-id="userId"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-1">
					</div>
					<div class="col-xs-12 col-sm-8">
						<sbux-input id="txtUserId" name="txtUserId" uitype="text" readonly></sbux-input>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-3">
						<sbux-label id="lbUserName" name="lbUserName" uitype="normal" text="성명" for-id="txtUserName"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-1">
					</div>
					<div class="col-xs-12 col-sm-8">
						<sbux-input id="txtUserName" name="txtUserName" uitype="text" readonly></sbux-input>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-3">
						<sbux-label id="lbPw" name="lbPw" uitype="normal" text="변경전비밀번호" for-id="txtPw"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-1">
					</div>
					<div class="col-xs-12 col-sm-8">
						<sbux-input id="txtPw" name="txtPw" uitype="password" minlength="8" maxlength="12"></sbux-input>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-3">
						<sbux-label id="lbUpdatePw1" name="lbUpdatePw1" uitype="normal" text="변경후비밀번호" for-id="txtUpdatePw1"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-1">
					</div>
					<div class="col-xs-12 col-sm-8">
						<sbux-input id="txtUpdatePw1" name="txtUpdatePw1" uitype="password" minlength="8" maxlength="12"></sbux-input>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-3">
						<sbux-label id="lbUpdatePw2" name="lbUpdatePw2" uitype="normal" text="확인비밀번호" for-id="txtUpdatePw2"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-1">
					</div>
					<div class="col-xs-12 col-sm-8">
						<sbux-input id="txtUpdatePw2" name="txtUpdatePw2" uitype="password" minlength="8" maxlength="12"></sbux-input>
					</div>
				</div>
				<div class="row margin-15-top">
					<div class="col-xs-12 col-sm-2">
						<sbux-label id="lb1" name="lb1" uitype="normal" text="TIP"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-10">
						<sbux-label id="lb2" name="lb2" uitype="normal" text="비밀번호는 숫자/영문/특수기호를 포함하는" style="color:#bdc0c4"></sbux-label>
					</div>
				</div>
				<div class="row margin-10-top">
					<div class="col-xs-12 col-sm-2">
					</div>
					<div class="col-xs-12 col-sm-10">
						<sbux-label id="lb3" name="lb3" uitype="normal" text="8-12자리 이어야 합니다. (분기별 패스워드 변경)" style="color:#bdc0c4"></sbux-label>
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

<script type="text/javascript" src="<c:url value="/js/ecams/mypage/PwdChange.js"/>"></script>