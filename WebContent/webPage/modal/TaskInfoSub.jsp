<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<style>
    html,body{width : 100%; height : 100%; margin : 0; padding : 0; }
</style>

<div class="col-xs-12">
	<div class="row" style="height:30px; clear: both; background-color:#fafafa;">
		<div class="col-xs-4 col-sm-1" style="padding-top:11px; text-align:center;">
			<sbux-label id="idx_lab_gbn" text="업무코드&nbsp;&nbsp;" uitype="normal">
			</sbux-label>
		</div>
		<div class="col-xs-8 col-sm-2" style="padding-top:11px; text-align:center;">
			<sbux-label id="idx_lab_code" text="업무명&nbsp;&nbsp;" uitype="normal">
			</sbux-label>
		</div>
	</div>
	
	<div class="row" style="height:30px; clear: both; background-color:#fafafa;">
		<div class="col-xs-4 col-sm-2" style="padding-top:2px;">
			<sbux-input id="idx_input_jobcd" name="input_jobcd" uitype="text" datastore-id="idxData1"></sbux-input>
		</div>
	
		<div class="col-xs-8 col-sm-2" style="padding-top:2px;">
			<sbux-input id="idx_input_jobname" name="input_jobname" uitype="text" datastore-id="idxData1"></sbux-input>
		</div>
	</div>
</div>

<span style="float:right; padding-top:5px; padding-left:1px;"> 
	<sbux-button id="btnCncl" name="btnCncl" uitype="normal" text="취소" button-size="middle" button-color="default" onclick="btnCncl_Click()"></sbux-button>
</span>
	
<span style="float:right; padding-top:5px; padding-left:1px; padding-right:2px"> 
	<sbux-button id="btnUpdt" name="btnUpdt" uitype="normal" text="적용" button-size="middle" button-color="default" onclick="btnUpdt_Click()"></sbux-button> 
</span>


<script type="text/javascript" src="<c:url value="/js/ecams/modal/TaskInfoSub.js"/>"></script>