<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<style>
	   .sbgrid-form #select_option { width: 98%; height: 100px; text-align: center;border: #757575 solid 2px;background-color: #fafafa; margin: 5px 10px 5px 15px;}
	   .sbgrid-form #select_option tr td{width: 5%;}
	   .sbgrid-form #inputData { width: 98%; height: 100%; text-align: center;border: #757575 solid 2px;background-color: #fafafa; margin: 5px 10px 5px 15px;}
	   .sbgrid-form #inputData tr td{ width: 5%; border: #757575 solid 2px;}
	   
	   label{margin-top: 5px;}
	   button{width:80px;}
	   
	  	#sbGridArea{width: 100%; height:500px;}
	  
	   .sbgrid-tem-contents{width : 100%; height:100%;}
	   #titleBar{background-color: #E0F8F7; width:100%; height: 2%;}	   
</style>

<section>
	<div class="container-fluid" style="border: #757575 solid 2px;  margin: 0px 15px 0px 15px; padding: 5px 0px 5px 0px;  overflow:hidden;">
		<div class="row-fluid">
			<div class="row">
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbSysCd" name="lbSysCd" uitype="normal" text="시스템"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1">
						<sbux-select id="cboSyscd" name="cboSyscd" uitype="single" jsondata-ref = "" jsondata-text="" jsondata-value=""  style="width:100%;"></sbux-select>				
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbBlank" name="lbBlank" uitype="normal" text="결재사유"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1">
						<sbux-select id="cboSin" name="cboSin" uitype="single" jsondata-ref = "" jsondata-text="" jsondata-value=""  style="width:100%;"></sbux-select>				
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbGbn" name="lbGbn" uitype="normal" text="처리구분"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-select id="cboGbn" name="cboGbn" uitype="single" jsondata-ref = "" jsondata-text="" jsondata-value=""  style="width:100%;"></sbux-select>				
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbEditor" name="lbEditor" uitype="normal" text="신청인"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-2">
					<sbux-input id="txtProgName" name="txtProgName" uitype="text" title="신청인" placeholder="신청인을 입력하세요." onkeyenter="cmdQry_Proc()"></sbux-input>
				</div>
				<div class="col-xs-12 col-sm-2">
					<sbux-radio id="rdoStrDate" name="rdoDate" uitype="normal" text="신청일기준" value="0" checked></sbux-radio>
					<sbux-radio id="rdoPrcDate" name="rdoDate" uitype="normal" text="적용예정기준" value="2"></sbux-radio>
					<sbux-radio id="rdoEndDate" name="rdoDate" uitype="normal" text="결재일기준" value="1"></sbux-radio>
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-button id=btnExcel name="btnExcel" uitype="normal" text="엑셀저장"></sbux-button>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbDept" name="lbDept" uitype="normal" text="신청부서"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1">
						<sbux-select id="cboTeam" name="cboTeam" uitype="single" jsondata-ref = "" jsondata-text="" jsondata-value=""  style="width:100%;"></sbux-select>				
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbSta" name="lbSta" uitype="normal" text="결재상태"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1">
						<sbux-select id="cboSta" name="cboSta" uitype="single" jsondata-ref = "" jsondata-text="" jsondata-value=""  style="width:100%;"></sbux-select>				
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbProc" name="lbProc" uitype="normal" text="진행상태"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1">
						<sbux-select id="cboProc" name="cboProc" uitype="single" jsondata-ref = "" jsondata-text="" jsondata-value=""  style="width:100%;"></sbux-select>				
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbSpms" name="lbSpms" uitype="normal" text="SR-ID/SR명"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-2">
					<sbux-input id="txtSpms" name="txtSpms" uitype="text" title=SR-ID/SR명 placeholder="SR-ID/SR명을 입력하세요." onkeyenter="cmdQry_Proc()"></sbux-input>				
				</div>
				<div class="col-xs-12 col-sm-2">
						<sbux-picker id="datStD" name="datStD" uitype="date" mode="popup" show-button-bar="false" style="width:100%;"></sbux-picker>
						<sbux-picker id="datEdD" name="datEdD" uitype="date" mode="popup" show-button-bar="false" style="width:100%;"></sbux-picker>
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-button id="btnCmdQry" name="btnCmdQry" uitype="normal" text="조&nbsp;&nbsp;&nbsp;&nbsp;회" onclick="cmdQry_Proc()"></sbux-button>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12 col-sm-1" style="background-color: #FF0000;">
					<sbux-label id="lbCnl" name="lbCnl" uitype="normal" text="반려 또는 취소" style="color:#FFFFFF; padding-bottom:5px;"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1" style="background-color: #BE81F7;">
					<sbux-label id="lbErr" name="lbErr" uitype="normal" text="시스템처리 중 에러발생" style="color:#FFFFFF; padding-bottom:5px;"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1" style="background-color: #000000;">
					<sbux-label id="lbEnd" name="lbEnd" uitype="normal" text="처리완료" style="color:#FFFFFF; padding-bottom:5px;"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1" style="background-color: #0000FF;">
					<sbux-label id="lbIng" name="lbIng" uitype="normal" text="진행중" style="color:#FFFFFF; padding-bottom:5px;"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1 col-sm-offset-6">
					<sbux-label id="lbCnt" name="lbCnt" uitype="normal" text="총0건"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-button id="btnClear" name="btnClear" uitype="normal" text="초기화"></sbux-button>
				</div>
			</div>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid" style="height:600px; margin: 0px 15px 0px 15px; padding: 5px 0px 5px 0px;  overflow:hidden;">
			<div id="sbGridArea"></div>
	</div>
</section>	

<script type="text/javascript" src="<c:url value="/js/ecams/approval/ApprovalStatus.js"/>"></script>