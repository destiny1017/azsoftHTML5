<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<style>
	   label{margin-top: 5px;}
	   #sbGridArea{width: 100%; height:500px;}
</style>

<section>
	<div class="container-fluid" style="border: #757575 solid 2px;  margin: 0px 15px 0px 15px; padding: 5px 0px 5px 0px;">
		<div class="row-fluid">
			<div class="row">
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbSysCd" name="lbSysCd" uitype="normal" text="시스템"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-2">
					<sbux-select id="cboSysCd" name="cboSysCd" uitype="single" jsondata-ref = "cboSysCd" jsondata-text="cm_sysmsg" jsondata-value="cm_syscd"  style="width:100%;"></sbux-select>				
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbSelect1" name="lbSelect1" uitype="normal" text="조건선택1"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-select id="cbo_Cond10" name="cbo_Cond10" uitype="single" jsondata-ref = "cbo_Cond10" jsondata-text="cm_codename" jsondata-value="cm_micode"  style="width:100%;" onchange="setJogunOne(cbo_Cond10)"></sbux-select>				
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbSelect2" name="lbSelect2" uitype="normal" text="조건선택2"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-select id="cbo_Cond11" name="cbo_Cond11" uitype="single" jsondata-ref = "cbo_Cond11" jsondata-text="cm_codename" jsondata-value="cm_micode"  style="width:100%;" onchange="setJogunTwo(cbo_Cond11)"></sbux-select>				
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbScope" name="lbScope" uitype="normal" text="범위"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-select id="cbo_Option" name="cbo_Option" uitype="single" jsondata-ref = "cbo_Option" jsondata-text="cm_codename" jsondata-value="cm_micode"  style="width:100%;"></sbux-select>				
				</div>
				<div class="col-xs-12 col-sm-1 col-sm-offset-2">
					<sbux-button id=btnExcel name="btnExcel" uitype="normal" text="엑셀저장"></sbux-button>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12 col-sm-1 col-sm-offset-3">
					<sbux-label id="lbl_Cond0" name="lbl_Cond0" uitype="normal"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-select id="cbo_Cond2" name="cbo_Cond2" uitype="single" jsondata-ref = "cbo_Cond2" jsondata-text="cm_codename" jsondata-value="cm_micode"  style="width:100%;"></sbux-select>				
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbl_Cond1" name="lbl_Cond1" uitype="normal"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-input id="txt_Cond" name="txt_Cond" uitype="text" title="입력" onkeyenter="cmdQry_Proc()"></sbux-input>				
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-checkbox id="chkDetail" name="chkDetail" uitype="normal" text="세부항목포함" checked></sbux-checkbox>			
				</div>
				<div class="col-xs-12 col-sm-1 col-sm-offset-2">
					<sbux-label id="lbTotalCnt" name="lbTotalCnt" uitype="normal" text="총0건"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-1">
					<sbux-button id="btnCmdQry" name="btnCmdQry" uitype="normal" text="조&nbsp;&nbsp;&nbsp;&nbsp;회" onclick="cmdQry_Proc()"></sbux-button>
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
<script type="text/javascript" src="<c:url value="/js/ecams/report/PrgListReport.js"/>"></script>