<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<style>
    #divContent {width: 380px; padding: 20px; border:1px solid black; margin: 0 auto}
    #myGrid1Area {width:100%; height:23%}
    #opt {width: 380px; margin: 0 auto}
    #btnReg {background: #656970; color:white; font-style:bold;}
    .row {margin-top: 10px}
</style>

<section>
	<div class="container-fluid padding-40-top">
		<div id="opt">
			<sbux-radio id="rdoOpt0" name="radiogroup" uitype="normal" text="등록" value="reg"
				onclick="$('#btnReg').children('span').text($('#rdoOpt0').attr('text'));">
			</sbux-radio>
			<sbux-radio id="rdoOpt1" name="radiogroup" uitype="normal" text="해제" value="cancel"
				onclick="$('#btnReg').children('span').text($('#rdoOpt1').attr('text'));">
			</sbux-radio>
			<sbux-label id="lbTit" name="lbTit" uitype="normal"></sbux-label>
		</div>
	</div>
</section>
	
<section>
	<div class="container-fluid margin-15-top">
		<div id="divContent">
			<div class="row-fluid">
				<div class="row">
					<div class="col-xs-12 col-sm-4">
						<sbux-label id="lbUser" name="lbUser" uitype="normal" text="부 재 자"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-8">
						<sbux-input id="txtUser" name="txtUser" uitype="text" onkeyenter="Search_click1()"></sbux-input>
						<sbux-select id="cboUser" name="cboUser" uitype="single"
							jsondata-ref="comboDp1" jsondata-text="username"
							jsondata-value="cm_userid" onchange="Cbo_User_Click()"></sbux-select>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-4">
						<sbux-label id="lbDaeSign" name="lbDaeSign" uitype="normal" text="대결재자"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-8">
						<sbux-input id="txtName" name="txtName" uitype="text" onkeyenter="Search_click2()"></sbux-input>
						<sbux-select
							id="cboDaeSign" name="cboDaeSign" uitype="single"
							jsondata-ref="Sql_tmp_dp1" jsondata-text="username"
							jsondata-value="cm_userid" onchange="DaeSign_username_Set()"></sbux-select>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-4">
						<sbux-label id="lbSayu" name="lbSayu" uitype="normal" text="부재사유"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-8">
						<sbux-select id="cboSayu" name="cboSayu" uitype="single"
							jsondata-ref="comboDp3" jsondata-text="cm_codename"
							jsondata-value="cm_codename" onchange="Cbo_Sayu_Click()"></sbux-select>
						<sbux-input id="txtSayu" name="txtSayu" uitype="text"></sbux-input>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-4">
						<sbux-label id="lbDate" name="lbDate" uitype="normal" text="부재기간"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-8">
						<sbux-label id="lbFrom" name="lbFrom" uitype="normal" text="FROM"></sbux-label>
						<sbux-picker id="dateStD" name="dateStD" uitype="date" mode="popup" date-format="yyyy/mm/dd"></sbux-picker>
						<sbux-label id="lbTo" name="lbTo" uitype="normal" text="TO"></sbux-label>
						<sbux-picker id="dateEdD" name="dateEdD" uitype="date" mode="popup" date-format="yyyy/mm/dd"></sbux-picker>
					</div>
				</div>
			</div>
			<div id="myGrid1Area"></div>
		</div>
	</div>
</section>

<section>
	<div id="divPw" class="margin-15-top" style="text-align:center">
		<sbux-button id="btnReg" name="btnReg" uitype="normal" text="등록" onclick="cmd_click()"></sbux-button>
	</div>
</section>

<script type="text/javascript" src="<c:url value="/js/ecams/mypage/AbsenceRegister.js"/>"></script>