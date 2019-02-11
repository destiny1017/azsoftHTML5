var userid = window.parent.userId;     
var cboRequest;
var cboSyscd;
var strReqCD;
var strTmpDir = "";
var cboStep;
var cboGbn;
var grid_data;

var request =  new Request();
strReqCD = request.getParameter('reqcd');

var today = new Date();
var dd = today.getDate();
var mm = today.getMonth() + 1;
var yyyy = today.getFullYear();

var SBGridProperties = {};

if(dd < 10){
	dd = '0' + dd;
}
if(mm < 10){
	mm = '0' + mm;
}

today = yyyy + '/' + mm + '/' + dd;

$(document).ready(function(){
	SBUxMethod.set('datStD', today);
	SBUxMethod.set('datEdD', today);
	createGrid();
	strReqCD_set();
	userInfo_check();
	codeInfo_set();
	systemPath_set();
	getSysInfo();	
});

function strReqCD_set(){	
	step_combo_init();
	if(strReqCD=="04" || strReqCD=="05" || strReqCD == "07" || strReqCD == "03"){
		cboGbn_set();
		document.getElementById('lbExGbn').style.visibility = "visible";
		document.getElementById('cboGbn').style.visibility = "visible";	
		datagrid.setColHidden(9, false, true);
		datagrid.setColHidden(10, false, true);
	}else{
		document.getElementById('lbExGbn').style.visibility = "hidden";
		document.getElementById('cboGbn').style.visibility = "hidden";
		datagrid.setColHidden(9, true, true);
		datagrid.setColHidden(10, true, true);
	}
}

function userInfo_check(){	// 사용자의 관리자 권한 여부 체크
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'UserInfochk',
			UserId : userid
	}
	
	ajaxResultData = ajaxCallWithJson('/webPage/dev/DistributeStatus', tmpData, 'json');
	
	if('1' == ajaxResultData[0].cm_admin){
		SBUxMethod.set('chkSelf', 'false');
		SBUxMethod.attr('txtUserName', 'disabled', 'false');
	}
}

function codeInfo_set(){ // 신청종류
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'CodeInfoSet'
	}
	
	ajaxResultData = ajaxCallWithJson('/webPage/dev/DistributeStatus', tmpData, 'json');
	
	cboRequest = ajaxResultData;
	
	cboRequest = cboRequest.filter(function(data) {
		if(strReqCD == "01"){        	   		
			if(data.cm_micode === "01" || data.cm_micode === "02" || data.cm_micode === "11" || data.cm_micode === "00"){
				return true;
			} else {
				return false;
			}        			
 	   	} else if (data.cm_micode == strReqCD || data.cm_micode === "00" || data.cm_micode === "03" || data.cm_micode === "06" || data.cm_micode === "07" || data.cm_micode === "94") {
 	   		return true;
 	   	} else {
 	   		return false;
 	   	}
 	});
	
	SBUxMethod.refresh('cboRequest');
}

function systemPath_set(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'SystemPathSet'
	}
	
	ajaxResultData = ajaxCallWithJson('/webPage/dev/DistributeStatus', tmpData, 'json');
	
	strTmpDir = ajaxResultData + "/";
}

function getSysInfo(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'getSysInfo',
			UserId : userid 
	}
	ajaxResultData = ajaxCallWithJson('/webPage/dev/DistributeStatus', tmpData, 'json');
	
	console.log(ajaxResultData);
	cboSyscd = ajaxResultData;        	        	        	
	SBUxMethod.refresh('cboSyscd');
}

function step_combo_init(){
	cboStep = [
		{ label : "전체", value : "0" },
		{ label : "미완료", value : "1" },
		{ label : "반려/회수", value : "3" },
		{ label : "완료", value : "9" }		
	];
	SBUxMethod.refresh('cboStep');
}

function step_combo_change_resultHandler(args){
	if(SBUxMethod.get("cboStep") == "1"){
		$('#datStD').attr('disabled', true);
		$('[name="_datStD_sub"]').attr('disabled', true);
		$('#datEdD').attr('disabled', true);
		$('[name="_datEdD_sub"]').attr('disabled', true);
	} else {
		$('#datStD').attr('disabled', false);
		$('[name="_datStD_sub"]').attr('disabled', false);
		$('#datEdD').attr('disabled', false);
		$('[name="_datEdD_sub"]').attr('disabled', false);
	}
}

function cboGbn_set(){
	cboGbn = [
		{cm_macode : "REQPASS", cm_micode : "ALL", cm_codename : "전체"},
		{cm_macode : "REQPASS", cm_micode : "0", cm_codename : "일반적용"},
		{cm_macode : "REQPASS", cm_micode : "4", cm_codename : "수시적용"},
		{cm_macode : "REQPASS", cm_micode : "2", cm_codename : "긴급적용"}
	]
	SBUxMethod.refresh('cboGbn');
}

function cmdQry_Proc(){
	var tmpObj = {};
	var ajaxResultData = null;
	
	tmpObj.syscd = SBUxMethod.get("cboSyscd");
	tmpObj.jobcd = "0000";
	if(SBUxMethod.get("txtSrIdInput") !== undefined){
		tmpObj.spms = SBUxMethod.get("txtSrIdInput");
	} else {
		tmpObj.spms = "";
	}
	
	tmpObj.stepcd = SBUxMethod.get("cboStep");
	tmpObj.reqcd = SBUxMethod.get("cboRequest"); 
	tmpObj.req = strReqCD;
	tmpObj.UserID = userid;
	tmpObj.dategbn = SBUxMethod.get("rdoDate");
	
	var strStD = SBUxMethod.get("datStD");
	var strEdD = SBUxMethod.get("datEdD");
	
	if(strStD > strEdD){
		alert("조회기간을 정확하게 선택하여 주십시오.");
		strStD = "";
		strEdD = "";
		return;
	}  
	
	tmpObj.stDt = strStD;
	tmpObj.edDt = strEdD;
	
	if(strReqCD == "04"){
		tmpObj.docno = "0";
		tmpObj.gbn = SBUxMethod.get("cboGbn");
	} else {
		tmpObj.docno = "0";
		tmpObj.gbn = "ALL";
	}
	
	if(SBUxMethod.get("txtProgName") !== undefined){
		tmpObj.prgname = SBUxMethod.get("txtProgName");	// 상세조회 버튼 미존재
	} else {
		tmpObj.userName = "";
	}
	
	
	if(SBUxMethod.get("chkSelf").chkSelf === true){
		tmpObj.chkSelf = "true";
		tmpObj.userName ="";
	} else {
		tmpObj.chkSelf = "false";
		
		if(SBUxMethod.get("txtUserName") !== undefined){
			tmpObj.userName = SBUxMethod.get("txtUserName");
		} else {
			tmpObj.userName = "";
		}
	}

	var tmpData = {
			prjData: JSON.stringify(tmpObj),
			requestType : 'getFileList'			
	}
		
	ajaxResultData = ajaxCallWithJson('/webPage/dev/DistributeStatus', tmpData, 'json');
	
	var cnt = Object.keys(ajaxResultData).length;			// json 객체 길이 구하기			
	SBUxMethod.set('lbCnt', '총'+cnt+'건');		// 총 개수 표현		
	
	grid_data = ajaxResultData;
	datagrid.refresh();	
	strReqCD_set();
	
	$(ajaxResultData).each(function(i){
		if(ajaxResultData[i].errflag != "0"){
			datagrid.setRowStyle(i+1, 'data', 'color', '#BE81F7');	//시스템처리 중 에러발생
			datagrid.setRowStyle(i+1, 'data', 'font-weight', 'bold');
		} else if (ajaxResultData[i].cr_status == '3'){
			datagrid.setRowStyle(i+1, 'data', 'color', '#FF0000');	//반려 또는 취소
			datagrid.setRowStyle(i+1, 'data', 'font-weight', 'bold');
		} else if (ajaxResultData[i].cr_status == '0'){
			datagrid.setRowStyle(i+1, 'data', 'color', '#0000FF');	// 진행중
			datagrid.setRowStyle(i+1, 'data', 'font-weight', 'bold');
		}
	});
	
	tmpObj = null;
}

function createGrid(){
	SBGridProperties = {};
	SBGridProperties.parentid = 'sbGridArea';  // [필수] 그리드 영역의 div id 입니다.            
	SBGridProperties.id = 'datagrid';          // [필수] 그리드를 담기위한 객체명과 동일하게 입력합니다.                
	SBGridProperties.jsonref = 'grid_data';    // [필수] 그리드의 데이터를 나타내기 위한 json data 객체명을 입력합니다.
	SBGridProperties.waitingui = true;
	// 그리드의 여러 속성들을 입력합니다.
	SBGridProperties.explorerbar = 'sort';
	SBGridProperties.extendlastcol = 'scroll';
	SBGridProperties.tooltip = true;
	SBGridProperties.ellipsis = true;
	SBGridProperties.rowheader = 'seq';
	    			
	// [필수] 그리드의 컬럼을 입력합니다.  
	SBGridProperties.columns = [
		{caption : ['SR-ID'],	ref : 'cc_srid', width : '150px', style : 'text-align:center',	type : 'output'},
		{caption : ['문서번호'],	ref : 'genieid',width : '150px',  style : 'text-align:center', type : 'output'},
		{caption : ['시스템'],	ref : 'sysgbname',width : '100px', style : 'text-align:center',  type : 'output'},
		{caption : ['신청종류'],	ref : 'sin',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청자'],	ref : 'editor',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청번호'],	ref : 'acptno',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청일시'],	ref : 'acptdate',width : '200px',  style : 'text-align:center', type : 'output'},
		{caption : ['진행상태'],	ref : 'cm_codename',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['처리구분'],	ref : 'procgbn',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['적용예정일시'],	ref : 'grdprcreq',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['완료일시'],	ref : 'prcdate',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청내용'],	ref : 'rsrcnamememo',width : '200px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청사유'],	ref : 'cr_sayu', style : 'text-align:center',	type : 'output'}
	];
	datagrid = _SBGrid.create(SBGridProperties); // 만들어진 SBGridProperties 객체를 파라메터로 전달합니다.
}