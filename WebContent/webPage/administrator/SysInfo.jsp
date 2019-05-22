<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="/webPage/common/common.jsp" />

<div class="hpanel">
    <div class="panel-body text-center" id="gridDiv">
    	<div data-ax5grid="sysInfoGrid" data-ax5grid-config="{showLineNumber: true, lineNumberColumnWidth: 40}" style="height: 50%;"></div>
    </div>
</div>

<div class="row">
	<div class="col-lg-7"></div>
	<div class="col-lg-5">
		<div class="margin-10-right float-right">
			<input type="checkbox" class="checkbox-pie" id="chkCls" data-label="폐기포함"/>
			
			<button id="btnQry"  class="btn btn-default">
				조회 <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
			</button>
			<button id="btnFact" class="btn btn-default">
				처리펙터추가 <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
			</button>
		</div>
	</div>
</div>


<div class="hpanel">
    <div class="panel-body" id="sysDetailDiv">
    	<div class="col-lg-4 col-md-4 col-sm-4 col-12">
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblSysCd" class="padding-5-top float-right">시스템코드</label>
		    	</div>
		    	<div class="col-lg-2 col-md-12 col-sm-12 col-12">
		    		<input id="txtSysCd" name="txtTitle" class="form-control" type="text"></input>
		    	</div>
		    	<div class="col-lg-7 col-md-12 col-sm-12 col-12">
		    		<div class="float-left">
			    		<input type="checkbox" class="checkbox-pie" id="chkOpen" data-label="신규"  />
			    		<div id="chkSelfDiv">
				    		<input type="checkbox" class="checkbox-pie" id="chkSelf" data-label="시스템코드수동부여"  />
			    		</div>
		    		</div>
		    	</div>
	    	</div>
	    	
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblSysMsg" class="padding-5-top float-right">시스템명</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtSysMsg" name="txtTitle" class="form-control" type="text"></input>
		    	</div>
	    	</div>
	    	
	    	<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12"></div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<div id="cboSys" data-ax5select="cboSys" data-ax5select-config="{size:'sm',theme:'primary'}" 
						 style="width:100%;" onchange="cboSysClick()"></div>
		    	</div>
	    	</div>
			
			<div class="row"> 
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblSysGb" class="padding-5-top float-right">시스템유형</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
			    	<div id="cboSysGb" data-ax5select="cboSysGb" data-ax5select-config="{size:'sm',theme:'primary'}" 
							 style="width:100%;" ></div>
		    	</div>
			</div>
			
			<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblPrcCnt" class="padding-5-top float-right">프로세스제한</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtPrcCnt" name="txtTitle" class="form-control" type="text"></input>
		    	</div>
			</div>
			
			<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblSvrCd" class="padding-5-top float-right">기준서버구분</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<div id="cboSvrCd" data-ax5select="cboSvrCd" data-ax5select-config="{size:'sm',theme:'primary'}" 
							 style="width:100%;"></div>
		    	</div>
			</div>
							    	
			<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblSysOpen" class="padding-5-top float-right">시스템오픈</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<div class="input-group" data-ax5picker="datSysOpen" >
			            <input id="datSysOpen" type="text" class="form-control" placeholder="yyyy/mm/dd">
			            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
			        </div>
		    	</div>
			</div>	    	
			<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblScmOpen" class="padding-5-top float-right">형상관리오픈</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<div class="input-group" data-ax5picker="datScmOpen" >
			            <input id="datScmOpen" type="text" class="form-control" placeholder="yyyy/mm/dd">
			            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
			        </div>
		    	</div>
			</div>			
			<div class="row">
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<label id="lblPrjName" class="padding-5-top float-right">프로젝트명</label>
		    	</div>
		    	<div class="col-lg-9 col-md-12 col-sm-12 col-12">
		    		<input id="txtPrjName" name="txtTitle" class="form-control" type="text"></input>
		    	</div>
			</div>	    	
    	</div>
    	
    	<div class="col-lg-4 col-md-4 col-sm-4 col-12">
    		<div class="row">
		    	<div class="col-lg-2 col-md-12 col-sm-12 col-12">
		    		<label id="lblJobName" class="padding-5-top">업무</label>
		    	</div>
		    	<div class="col-lg-7 col-md-12 col-sm-12 col-12 no-padding">
		    		<input id="txtJobname" name="txtTitle" class="form-control" type="text"></input>
		    	</div>
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<div class="float-right">
			    		<input type="checkbox" class="checkbox-pie" id="chkJobName" data-label="전체선택"  />
		    		</div>
		    	</div>
	    	</div>
	    	<div class="row">
	    		<div class="col-lg-12 col-md-12 col-sm-12 col-12">
	    			<div data-ax5grid="jobGrid" data-ax5grid-config="{showLineNumber: true, lineNumberColumnWidth: 40}" style="height: 27%;"></div>
	    		</div>
	    	</div>
    	</div>
    	
    	<div class="col-lg-4 col-md-4 col-sm-4 col-12">
    		<div class="row">
		    	<div class="col-lg-6 col-md-12 col-sm-12 col-12">
		    		<label id="lblSysInfo" class="padding-5-top">시스템속성</label>
		    	</div>
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<div class="float-right">
			    		<label id="lblTime" class="padding-5-top">적용시간</label>
		    		</div>
		    	</div>
		    	<div class="col-lg-3 col-md-12 col-sm-12 col-12">
		    		<input id="txtTime" name="txtTitle" class="form-control" type="text"></input>
		    	</div>
	    	</div>
	    	<div class="row">
	    		<div class="col-lg-12 col-md-12 col-sm-12 col-12">
	    			<div class="scrollBind">
	    				<ul class="list-group" id="ulSysInfo">
		    				<li class="list-group-item"><input type="checkbox" class="checkbox-sysInfo" id="chkJobName" data-label="프로그램없음"  /></li>
		    				<li class="list-group-item"><input type="checkbox" class="checkbox-sysInfo" id="chkJobName" data-label="개별배포허용(사용안함)"  /></li>
		    				<li class="list-group-item"><input type="checkbox" class="checkbox-sysInfo" id="chkJobName" data-label="반입허용"  /></li>
		    				<li class="list-group-item"><input type="checkbox" class="checkbox-sysInfo" id="chkJobName" data-label="사용중지"  /></li>
		    				<li class="list-group-item"><input type="checkbox" class="checkbox-sysInfo" id="chkJobName" data-label="정기배포사용"  /></li>
		    				<li class="list-group-item"><input type="checkbox" class="checkbox-sysInfo" id="chkJobName" data-label="정기배포사용"  /></li>
		    				<li class="list-group-item"><input type="checkbox" class="checkbox-sysInfo" id="chkJobName" data-label="정기배포사용"  /></li>
		    				<li class="list-group-item"><input type="checkbox" class="checkbox-sysInfo" id="chkJobName" data-label="정기배포사용"  /></li>
		    				<li class="list-group-item"><input type="checkbox" class="checkbox-sysInfo" id="chkJobName" data-label="정기배포사용"  /></li>
		    			</ul>
	    			</div>
	    		</div>
	    	</div>
	    	<div class="row">
	    		<div class="col-lg-2 col-md-12 col-sm-12 col-12">
	    			<label id="lblDeploy" class="padding-5-top">중단시작</label>
	    		</div>
	    		<div class="col-lg-5 col-md-12 col-sm-12 col-12">
	    			<div class="input-group" data-ax5picker="datStDate" >
			            <input id="datStDate" type="text" class="form-control" placeholder="yyyy/mm/dd">
			            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
			        </div>
	    		</div>
	    		<div class="col-lg-5 col-md-12 col-sm-12 col-12">
	    			<div class="input-group bootstrap-timepicker timepicker">
			            <input  id="timeDeploy"  name="realtime" type="text" class="form-control" required="required" readonly>
			            <span 	class="input-group-addon"><i class="glyphicon glyphicon-time"></i></span>
			        </div>
	    		</div>
    		</div>
    		<div class="row">
	    		<div class="col-lg-2 col-md-12 col-sm-12 col-12">
	    			<label id="lblDeployE" class="padding-5-top">중단종료</label>
	    		</div>
	    		<div class="col-lg-5 col-md-12 col-sm-12 col-12">
	    			<div class="input-group" data-ax5picker="datEdDate" >
			            <input id="datEdDate" type="text" class="form-control" placeholder="yyyy/mm/dd">
			            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
			        </div>
	    		</div>
	    		<div class="col-lg-5 col-md-12 col-sm-12 col-12">
	    			<div class="input-group bootstrap-timepicker timepicker">
			            <input  id="timeDeployE"  name="realtime" type="text" class="form-control" required="required" readonly>
			            <span 	class="input-group-addon"><i class="glyphicon glyphicon-time"></i></span>
			        </div>
	    		</div>
    		</div>
    	</div>
    </div>
</div>

<div class="hpanel m-t">
    <div class="panel-body">
		<div class="col-lg-12 col-md-12 col-sm-12 col-12">
			<div class="row">
				<div class="float-right margin-10-right">
					<button class="btn btn-default" id="btnReleaseTimeSet">
						정기배포일괄등록 <span class="glyphicon glyphicon-registration-mark" aria-hidden="true"></span>
					</button>
					<button class="btn btn-default" id="btnAdd">
						등  록 <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
					</button>
					<button class="btn btn-default" id="btnDel">
						폐  기 <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
					</button>
					<button class="btn btn-default" id="btnJob">
						업무등록 <span class="glyphicon glyphicon-registration-mark" aria-hidden="true"></span>
					</button>
					<button class="btn btn-default" id="btnSysDetail">
						시스템상세정보 <span class="glyphicon glyphicon-list" aria-hidden="true"></span>
					</button>
					<button class="btn btn-default" id="btnProg">
						프로그램종류정보 <span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
					</button>
					<button class="btn btn-default" id="btnDir">
						공통디렉토리 <span class="glyphicon glyphicon-folder-open" aria-hidden="true"></span>
					</button>
					<button class="btn btn-default" id="btnCopy">
						시스템정보복사 <span class="glyphicon glyphicon-duplicate" aria-hidden="true"></span>
					</button>
				</div>
			</div>
		</div>
    </div>
</div>


<c:import url="/js/ecams/common/commonscript.jsp" />
<script type="text/javascript" src="<c:url value="/js/ecams/administrator/SysInfo.js"/>"></script>