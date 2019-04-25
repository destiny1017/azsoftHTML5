<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="/webPage/common/common.jsp" />
<c:import url="/js/ecams/common/commonscript.jsp" />

<style>
select,input[type="text"] {
	width: 100% !important;
}

label{
	margin-top : 8px; !important;
}

div[class^="col"] { 
	padding: 0px 10px 0px 0px !important;
}

div[class^="row"] { 
	margin: 0px 0px 5px 5px !important;
}

.fontStyle-error {
	color: #BE81F7;
}
.fontStyle-ing {
	color: #0000FF;
}
.fontStyle-cncl {
	color: #FF0000;
}

#tip {
	position:absolute;
  	color:#FFFFFF;
	padding:5px;
	display:none;
	background:#450e4c;
  	border-radius: 5px;
}

.loding-div{
    width:100%;
    height:100%;
    background-color: white;
    position: absolute;
    z-index:1;
}

.loding-img{
    position: absolute;
	z-index: 1;
    left: calc(50% - 436px/2);
    top:25%;
}

</style>
<section>
<div class="container-fluid">
	<div class="border-style-black">
		<div class="form-inline">
			<div class="row">
				<div class="col-sm-3">
					<div class="col-sm-2">
						<label  id="lbDept" name="lbDept" >신청부서</label>
					</div>
					<div class="form-group col-sm-10 no-padding">
						 <div data-ax5select="cboDept" data-ax5select-config="{size:'sm',theme:'primary'}" style="width:100%;"></div>
					</div>
				</div>
				
				<div class="col-sm-3">
					<div class="col-sm-2">
						<label  id="lbSysCd" name="lbSysCd">시스템</label>
					</div>
					<div class="form-group col-sm-10 no-padding">
						 <div data-ax5select="cboSysCd" data-ax5select-config="{size:'sm',theme:'primary'}" style="width:100%;"></div>
					</div>
				</div>
				<div class="col-sm-2">
					<div class="col-sm-4">
						<label  id="lbgbn" name="lbgbn">처리구분</label>
					</div>
					<div class="form-group col-sm-8 no-padding">
						<div data-ax5select="cboGbn" data-ax5select-config="{size:'sm',theme:'primary'}" style="width:100%;"></div>
					</div>
				</div>
				<div class="col-sm-2">
					<div class="col-sm-3">
						<label  id="lbEditor" name="lbEditor">신청인</label>
					</div>
					<div class="form-group col-sm-9 no-padding">
						<input class="input-sm" id="txtUser" name="txtUser" type="text" class="form-control" placeholder="신청인을 입력하세요." onkeypress="if(event.keyCode==13) {cmdQry_Proc();}"/>
					</div>
				</div>
				<div class="col-sm-1 col-sm-offset-1">
					<div class="form-group">
						<button class="btn btn-default" data-grid-control="excel-export">엑셀저장</button>
					</div>
				</div>
			</div>
			 
			<div class="row">
				<div class="col-sm-3">
					<div class="col-sm-2">
						<label  id="lbBlank" name="lbBlank">신청종류</label>
					</div>
					<div class="form-group col-sm-10 no-padding">
						<div data-ax5select="cboSin" data-ax5select-config="{multiple: true, size:'sm', theme:'primary'}" style="width:100%;"></div> 	
					</div>
				</div>
				
				<div class="col-sm-3">
					<div class="col-sm-2">
						<label  id="lbSta" name="lbSta">진행상태</label>
					</div>
					<div class="form-group col-sm-10 no-padding">
						<div data-ax5select="cboSta" data-ax5select-config="{size:'sm',theme:'primary'}" style="width:100%;"></div>
					</div>
				</div>
				<div class="col-sm-4">
					<div class="col-sm-2">
						<label  id="lbSpms" name="lbSpms">SR-ID/SR명</label>
					</div>
					<div class="form-group col-sm-10 no-padding">
						<input class="input-sm" id="txtSpms" name="txtSpms" type="text" class="form-control" placeholder="SR-ID/SR명을 입력하세요." onkeypress="if(event.keyCode==13) {cmdQry_Proc();}"/>
					</div>
				</div>
				<div class="col-sm-1 col-sm-offset-1">
					<button class="btn btn-default" onclick="cmdQry_Proc();" javascript:;>조&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;회</button>
				</div>
			</div>
			
			<div class="row">
				<div class="col-sm-3">
					<div class="form-group">
						<label  id="lbCnl" class="no-padding" name="lbCnl" style="color:white;  white-space: pre; background-color: #FF0000;">반려 또는 취소</label>
						<label  id="lbCnl" class="no-padding" name="lbCnl" style="color:white;  white-space: pre; background-color: #BE81F7;">처리 중 에러발생</label>
						<label  id="lbCnl" class="no-padding" name="lbCnl" style="color:white;  white-space: pre; background-color: #000000;">처리완료</label>
						<label  id="lbCnl" class="no-padding" name="lbCnl" style="color:white;  white-space: pre; background-color: #0000FF;">진행중</label>
					</div>
				</div>
				<div class="col-sm-3">
					<div class="col-sm-4 form-group">
						<label><input style="vertical-align: middle;" id="rdoStrDate" name="rdoDate" type="radio" value="0" checked/>&nbsp;&nbsp;신청일기준</label>
					</div>
					<div class="col-sm-8 form-group">
						<label><input style="vertical-align: middle;" id="rdoEndDate" name="rdoDate"  type="radio" value="1"/>&nbsp;&nbsp;완료일기준</label>
					</div>
				</div>
				<div class="col-sm-4">
					<div class="input-group" data-ax5picker="basic" style="width:100%;">
			            <input id="datStD" name="datStD" type="text" class="form-control" placeholder="yyyy/mm/dd">
			            <span class="input-group-addon">~</span>
			            <input id="datEdD" name="datEdD" type="text" class="form-control" placeholder="yyyy/mm/dd">
			            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
			        </div>
				</div>
				<div class="col-sm-1">
					<label  id="lbTotalCnt" name="lbTotalCnt" style="width: 100%; text-align: right;">총0건</label>
				</div>
			</div>		
		</div>
	</div>
</div>
</section>

<section>
	<div class="container-fluid">
		<div data-ax5grid="first-grid" data-ax5grid-config="{showLineNumber: true, lineNumberColumnWidth: 40}" style="height: 550px;"></div>
	</div>
</section>

<script type="text/javascript" src="<c:url value="/js/ecams/dev/HJK_loading.js"/>"></script>

