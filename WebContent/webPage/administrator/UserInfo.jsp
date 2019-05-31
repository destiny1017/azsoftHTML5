<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="/webPage/common/common.jsp" />

<div class="hpanel">
    <div class="panel-body" id="usrDetailDiv">
    	<div class="col-lg-3 col-md-3 col-sm-3 col-12">
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblUserId" class="padding-5-top float-right">사원번호</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtUserId" name="txtUserId" class="form-control" type="text" onkeydown="javascript: if (event.keyCode == 13) {getUserInfo(0);}"></input>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label class="padding-5-top float-right"></label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12" style="padding-top:3px;">
					<input style="vertical-align: middle;" id="rdoMan0" name="rdoMan" type="radio" value="0" onclick="changeBtnText()" checked />&nbsp;&nbsp;직원
					<input style="vertical-align: middle;" id="rdoMan1" name="rdoMan" type="radio" value="1" onclick="changeBtnText()"/>&nbsp;&nbsp;외주
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblDept" class="padding-5-top float-right">소속조직</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtDept" name="txtDept" class="form-control" type="text"></input>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblSubDept" class="padding-5-top float-right">소속(겸직)</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtSubDept" name="txtSubDept" class="form-control" type="text"></input>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label class="padding-5-top float-right">사용자생성일시</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtCreateDt" name="txtCreateDt" class="form-control" type="text"></input>
		    	</div>
	    	</div>
    	</div>
    	
    	<div class="col-lg-3 col-md-3 col-sm-3 col-12">
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblUserName" class="padding-5-top float-right">성명</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtUserName" name="txtUserName" class="form-control" type="text" onkeydown="javascript: if (event.keyCode == 13) {getUserInfo(1);}"></input>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label class="padding-5-top float-right"></label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<div class="float-left" style="padding-top:5px;padding-bottom:3px;">
	    				<input type="checkbox" class="checkbox-pie" id="chkAdmin" data-label="시스템관리자"></input>
	    			</div>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label class="padding-5-top float-right"></label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<div class="float-left" style="padding-top:3px;padding-bottom:5px;">
	    				<input type="checkbox" class="checkbox-pie" id="chkSync" data-label="동기화제외사용자"></input>
	    			</div>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblLoginDt" class="padding-5-top float-right">최종로그인</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtLoginDt" name="txtLoginDt" class="form-control" type="text"></input>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label class="padding-5-top float-right">사용자폐기일시</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtCloseDt" name="txtCloseDt" class="form-control" type="text"></input>
		    	</div>
	    	</div>
    	</div>
    	
    	<div class="col-lg-3 col-md-3 col-sm-3 col-12">
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblPosition" class="padding-5-top float-right">직급</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
			    	<div id="cboPosition" data-ax5select="cboPosition" data-ax5select-config="{size:'sm',theme:'primary'}" style="width:100%;" ></div>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblIpAddr" class="padding-5-top float-right">IP Address</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtIpAddr" name="txtIpAddr" class="form-control" type="text"></input>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblTelNo1" class="padding-5-top float-right">전화번호1</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtTelNo1" name="txtTelNo1" class="form-control" type="text"></input>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblTelNo2" class="padding-5-top float-right">전화번호2</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtTelNo2" name="txtTelNo2" class="form-control" type="text"></input>
		    	</div>
	    	</div>
    	</div>
    	
    	<div class="col-lg-3 col-md-3 col-sm-3 col-12">
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblDuty" class="padding-5-top float-right">직위</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
			    	<div id="cboDuty" data-ax5select="cboDuty" data-ax5select-config="{size:'sm',theme:'primary'}" style="width:100%;" ></div>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblEmail" class="padding-5-top float-right">E-mail</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtEmail" name="txtEmail" class="form-control" type="text"></input>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblErrCnt" class="padding-5-top float-right">비번오류횟수</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtErrCnt" name="txtErrCnt" class="form-control" type="text"></input>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblActive" class="padding-5-top float-right">활성화여부</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12"  style="padding-top:3px;">
					<label><input style="vertical-align: middle;" id="rdoActive0" name="rdoActive" type="radio" value="0" onclick="changeBtnText()" checked />&nbsp;&nbsp;활성화</label>
					<label>&nbsp;&nbsp;<input style="vertical-align: middle;" id="rdoActive1" name="rdoActive" type="radio" value="1" onclick="changeBtnText()"/>&nbsp;&nbsp;비활성화</label>
		    	</div>
	    	</div>
    	</div>
	</div>
</div>
    	
<div class="hpanel">
    <div class="panel-body">
    	<div class="col-lg-3 col-md-3 col-sm-3 col-12">
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblRgt" class="padding-5-top float-left">담당직무</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12" style="height:10px">
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-12 col-md-12 col-sm-12 col-12">
	    			<div class="scrollBind" style="margin-top:5px;height:73%;border-radius: 4px;background-color: inherit;border: 1px solid #e4e5e7;">
	    				<ul class="list-group" id="ulRgt" style="height:100%">
		    			</ul>
	    			</div>
		    	</div>
	    	</div>
    	</div>
    	<div class="col-lg-3 col-md-3 col-sm-3 col-12">
	    	<div class="row">
		    	<div class="col-lg-12 col-md-12 col-sm-12 col-12">
		    		<label class="padding-5-top float-left">담당업무추가</label>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-12 col-md-12 col-sm-12 col-12" style="height:5px"></div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblSys" class="padding-5-top float-left">시스템</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
			    	<div id="cboSys" data-ax5select="cboSys" data-ax5select-config="{size:'sm',theme:'primary'}" style="width:100%;" onchange="cboSysChange()"></div>
		    	</div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-12 col-md-12 col-sm-12 col-12" style="height:10px"></div>
	    	</div>
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblJob" class="padding-5-top float-left">업무</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<div class="float-right" style="padding:0px;margin-right:0px">
	    				<input type="checkbox" class="checkbox-pie" id="chkJobAll" data-label="전체선택"></input>
	    			</div>
		    	</div>
		    </div>
	    	<div class="row">
		    	<div class="col-lg-12 col-md-12 col-sm-12 col-12">
	    			<div class="scrollBind" style="margin-top:5px;height:65%;border-radius: 4px;background-color: inherit;border: 1px solid #e4e5e7;">
	    				<ul class="list-group" id="ulJob" style="height:100%">
		    				<!-- <li class="list-group-item"><input type="checkbox" class="checkbox-job" id="chkJobName" data-label=""  /></li> -->
		    			</ul>
	    			</div>
		    	</div>
	    	</div>
    	</div>
    	<div class="col-lg-6 col-md-6 col-sm-6 col-12" style="padding: 0px">
	    	<div class="col-lg-6 col-md-6 col-sm-6 col-12">
		    	<div class="row">
			    	<div class="col-lg-12 col-md-12 col-sm-12 col-12">
			    		<label class="padding-5-top float-left">부재등록정보</label>
			    	</div>
		    	</div>
		    	<div class="row">
			    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
			    		<label id="lblDaegyul" class="padding-5-top float-left">대결지정</label>
			    	</div>
			    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
			    		<input id="txtDaegyul" name="txtDaegyul" class="form-control" type="text"/>
			    	</div>
		    	</div>
		    	<div class="row">
			    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
			    		<label id="lblDaegyulDt" class="padding-5-top float-left">부재기간</label>
			    	</div>
			    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
			    		<input id="txtDaegyulDt" name="txtDaegyulDt" class="form-control" type="text"/>
			    	</div>
		    	</div>
		    	<div class="row">
			    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
			    		<label id="lblDaegyulSayu" class="padding-5-top float-left">부재사유</label>
			    	</div>
			    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
						<textarea id="txtDaegyulSayu" name="txtDaegyulSayu" class="form-control margin-1-top" rows="3"></textarea>
			    	</div>
		    	</div>
			</div>
			
	    	<div class="col-lg-6 col-md-6 col-sm-6 col-12">
		    	<div class="row">
			    	<div class="col-lg-12 col-md-12 col-sm-12 col-12">
			    		<label class="padding-5-top float-left">사용자조회결과</label>
			    	</div>
		    	</div>
		    	<div class="row">
			    	<div class="col-lg-12 col-md-12 col-sm-12 col-12">
	    				<div data-ax5grid="userGrid" data-ax5grid-config="{lineNumberColumnWidth: 40}" style="height: 13%;"></div>
	    			</div>
		    	</div>
			</div>
			
			<div class="col-lg-12 col-md-12 col-sm-12 col-12">
		    	<div class="row">
			    	<div class="col-lg-12 col-md-12 col-sm-12 col-12" style="height:10px"></div>
		    	</div>
		    	<div class="row">
			    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
			    		<label class="padding-5-top float-left">등록된 담당업무</label>
			    	</div>
			    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
						<div class="float-right">
							<button id="btnJobDel"  class="btn btn-default">
								담당업무삭제 <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
							</button>
						</div>
			    	</div>
		    	</div>
		    	<div class="row">
			    	<div class="col-lg-12 col-md-12 col-sm-12 col-12" style="height:10px"></div>
		    	</div>
		    	<div class="row">
			    	<div class="col-lg-12 col-md-12 col-sm-12 col-12">
	    				<div data-ax5grid="jobGrid" data-ax5grid-config="{showLineNumber: true, lineNumberColumnWidth: 40}" style="height: 53%;"></div>
	    			</div>
		    	</div>
			</div>
		</div>
	</div>
</div>
		
<c:import url="/js/ecams/common/commonscript.jsp" />
<script type="text/javascript"	src="<c:url value="/js/ecams/administrator/UserInfo.js"/>"></script>