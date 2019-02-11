<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<style>
	   label{margin-top: 5px;}
	   #sbGridArea{width: 100%; height:100%;}
</style>

	<section>
		<div class="container-fluid" style="border: #757575 solid 2px;  margin: 0px 15px 0px 15px; padding: 5px 0px 5px 0px;">
			<div class="row-fluid">
				<div class="row">
					<!-- 조회설정 -->
					<div class="col-xs-12 col-sm-1">
						<sbux-label id="lblDept1" name="lblDept1" uitype="normal" text="요청부서"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-2">
						<sbux-select id="cboDept1" name="cboDept1" uitype="single" jsondata-ref = "cboDept1" jsondata-text="cm_deptname" jsondata-value="cm_deptcd"  style="width:100%;"></sbux-select>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-label id="lblSta1" name="lblSta1" uitype="normal" text="SR상태"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-2">
						<sbux-select id="cboSta1" name="cboSta1" uitype="single" jsondata-ref = "cboSta1" jsondata-text="cm_codename" jsondata-value="cm_micode"  style="width:100%;" onchange="fnChange(cboSta1)"></sbux-select>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-label id="lblTit" name="lblTit" uitype="normal" text="SR-ID/SR명/문서번호"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-4">
						<sbux-input id="txtTit" name="txtTit" uitype="text" title="SR-ID/SR명/문서번호" placeholder="SR-ID/SR명/문서번호를 입력하세요" onkeyenter="cmdQry_Proc()"></sbux-input>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-button id="btnExl" name="btnExl" uitype="normal" text="엑셀저장" onclick="doDataToExcel()"></sbux-button>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-1">
						<sbux-label id="lblDept2" name="lblDept2" uitype="normal" text="등록부서"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-2">
						<sbux-select id="cboDept2" name="cboDept2" uitype="single" jsondata-ref = "cboDept2" jsondata-text="cm_deptname" jsondata-value="cm_deptcd" style="width:100%;"></sbux-select>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-label id="lblSta2" name="lblSta2" uitype="normal" text="개발자상태"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-2">
						<sbux-select id="cboSta2" name="cboSta2" uitype="single" jsondata-ref = "cboSta2" jsondata-text="cm_codename" jsondata-value="cm_micode" style="width:100%;" onchange="fnChange(cboSta2)"></sbux-select>
					</div>
					<div class="col-xs-12 col-sm-2">
						<sbux-radio id="rdo_norm1" name="rdo_norm" uitype="normal" text="전체대상" value="B" onclick="cmdQry_Proc()"></sbux-radio>
						<sbux-radio id="rdo_norm2" name="rdo_norm" uitype="normal" text="팀내진행건" value="A" onclick="cmdQry_Proc()" checked></sbux-radio>
						<sbux-radio id="rdo_norm3" name="rdo_norm" uitype="normal" text="내진행건만" value="T" onclick="cmdQry_Proc()"></sbux-radio>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-label id="lblStD" name="lblStD" uitype="normal" text="등록일"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-2">
						<sbux-picker id="datStD" name="datStD" uitype="date" mode="popup" show-button-bar="false"></sbux-picker>
						<sbux-picker id="datEdD" name="datEdD" uitype="date" mode="popup" show-button-bar="false"></sbux-picker>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-button id="btnCmdQry" name="btnCmdQry" uitype="normal" text="조&nbsp;&nbsp;&nbsp;&nbsp;회" onclick="cmdQry_Proc()"></sbux-button>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-1 col-sm-offset-1" style="background-color: #FF8000;">
						<sbux-label id="lblCmt1" name="lblCmt1" uitype="normal" text="접수" style="color:#FFFFFF; padding-bottom:5px;"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-1" style="background-color: #145A32;">
						<sbux-label id=lblCmt2 name=lblCmt2 uitype="normal" text="개발" style="color:#FFFFFF; padding-bottom:5px;"></sbux-label>				
					</div>
					<div class="col-xs-12 col-sm-1" style="background-color: #BE81F7;">
						<sbux-label id="lblCmt3" name="lblCmt3" uitype="normal" text="테스트" style="color:#FFFFFF; padding-bottom:5px;"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-1" style="background-color: #045FB4;">
						<sbux-label id="lblCmt4" name="lblCmt4" uitype="normal" text="적용" style="color:#FFFFFF; padding-bottom:5px;"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-1" style="background-color: #2E2E2E;">
						<sbux-label id="lblCmt5" name="lblCmt5" uitype="normal" text="처리완료" style="color:#FFFFFF; padding-bottom:5px;"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-1" style="background-color: #FF0000;">
						<sbux-label id="lblCmt6" name="lblCmt6" uitype="normal" text="반려 또는 취소" style="color:#FFFFFF; padding-bottom:5px;"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-1 col-sm-offset-3">
						<sbux-label id="lbTotalCnt" name="lbTotalCnt" uitype="normal" text="총0건"></sbux-label>
					</div>
					<div class="col-xs-12 col-sm-1">
						<sbux-button id="btnCmdClear" name="btnCmdClear" uitype="normal" text="초기화"></sbux-button>
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
	<script type="text/javascript" src="<c:url value="/js/ecams/register/SRStatus.js"/>"></script>
	
