<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<style>
	   label{margin-top: 5px;}
	   #sbGridArea{width: 100%; height:500px;}
	   #titleBar{background-color: #E0F8F7; width:100%; height: 2%;}	   
</style>
	<section>
		<div class="container-fluid" style="border: #757575 solid 2px;  margin: 0px 15px 0px 15px; padding: 5px 0px 5px 0px;">
			<div class="row-fluid">
				<div class="row">
					<div class="col-xs-12 col-sm-1">
						<sbux-label id="lbSysCd" name="lbSysCd" uitype="normal" text="시스템"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-2">
						<sbux-select id="cboSyscd" name="cboSyscd" uitype="single" jsondata-ref = "cboSyscd" jsondata-text="cm_sysmsg" jsondata-value="cm_syscd"  style="width:100%;"></sbux-select>				
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-label id="lbStep" name="lbStep" uitype="normal" text="진행상태"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-2">
						<sbux-select id="cboStep" name="cboStep" uitype="single" jsondata-ref = "cboStep" jsondata-text="label" jsondata-value="value"  style="width:100%;" onchange="step_combo_change_resultHandler(cboStep)"></sbux-select>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-label id="lbExGbn" name="lbExGbn" uitype="normal" text="처리구분"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-2">
						<sbux-select id="cboGbn" name="cboGbn" uitype="single" jsondata-ref = "cboGbn" jsondata-text="cm_codename" jsondata-value="cm_micode"  style="width:100%;"></sbux-select>
					</div>
					<div class="col-xs-12 col-sm-2">
						<sbux-radio id="rdo_norm1" name="rdoDate" uitype="normal" text="신청일기준" value="0" checked></sbux-radio>
						<sbux-radio id="rdo_norm2" name="rdoDate" uitype="normal" text="완료일기준" value="1"></sbux-radio>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-button id="btnCmdQry" name="btnCmdQry" uitype="normal" text="조&nbsp;&nbsp;&nbsp;&nbsp;회" onclick="cmdQry_Proc()"></sbux-button>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-1">
						<sbux-label id="lbRequest" name="lbRequest" uitype="normal" text="신청종류"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-2">
						<sbux-select id="cboRequest" name="cboRequest" uitype="single" jsondata-ref = "cboRequest" jsondata-text="cm_codename" jsondata-value="cm_micode"  style="width:100%;"></sbux-select>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-label id="lbProgText" name="lbProgText" uitype="normal" text="프로그램명/설명"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-2">
						<sbux-input id="txtProgName" name="txtProgName" uitype="text" title="프로그램명/설명" placeholder="프로그램명/설명을 입력하세요." onkeyenter="cmdQry_Proc()"></sbux-input>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-label id="lbUserName" name="lbUserName" uitype="normal" text="신청자명"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-2">
						<sbux-input id="txtUserName" name="txtUserName" uitype="text" title="신청자명" onkeyenter="cmdQry_Proc()" disabled></sbux-input>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-checkbox id="chkSelf" name="chkSelf" uitype="normal" text="본인건만" checked></sbux-checkbox>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-picker id="datStD" name="datStD" uitype="date" mode="popup" show-button-bar="false"></sbux-picker>
						<sbux-picker id="datEdD" name="datEdD" uitype="date" mode="popup" show-button-bar="false"></sbux-picker>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-button id=btnExcel name="btnExcel" uitype="normal" text="엑셀저장"></sbux-button>
					</div>
				</div>
				<div class="row-fulid">
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
					<div class="col-xs-12 col-sm-2">
						<sbux-label id="lbSrId" name="lbSrId" uitype="normal" text="SR-ID/SR명/문서번호"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-4">
						<sbux-input id="txtSrIdInput" name="txtSrIdInput" uitype="text" title="SR-ID/SR명/문서번호" placeholder="SR-ID/SR명/문서번호를 입력하세요." onkeyenter="cmdQry_Proc()"></sbux-input>
					</div>
					<div class="col-xs-12 col-sm-1">
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
	<c:import url="/js/ecams/common/commonscript.jsp" />
	<script type="text/javascript" src="<c:url value="/js/ecams/dev/DistributeStatus.js"/>"></script>
