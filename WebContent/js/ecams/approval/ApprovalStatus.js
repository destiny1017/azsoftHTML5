var grid_data;
var cboSyscd;
var cboSin;
var cboSta;
var cboTeam;
var cboGbn;
var cboProc;

// userId 및 reqcd 가져오기
var userid = window.parent.userId;
var request =  new Request();
strReqCD = request.getParameter('reqcd');


var SBGridProperties = {};

$(document).ready(function(){
	if(strReqCD != null && strReqCD != ""){
		getUserInfo();
	}	
	SBUxMethod.set('datStD', getDate('DATE',0));
	SBUxMethod.set('datEdD', getDate('DATE',0));
	createGrid();
	getSysInfo();
	getCodeInfo();
	getTeamInfoGrid2();
	cboGbn_set();
});

function getUserInfo(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'UserInfochk',
			UserId : userid
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/ApprovalStatus', tmpData, 'json');
	console.log(ajaxResultData);
	if(ajaxResultData.length > 0){
		SBUxMethod.set('txtUser', ajaxResultData[0].cm_username);
	}
}

function getSysInfo(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'SysInfo',
			UserId : userid
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/ApprovalStatus', tmpData, 'json');
	
	cboSyscd = ajaxResultData;        	        	        	
	SBUxMethod.refresh('cboSyscd');
}

function getCodeInfo(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'CodeInfo'
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/ApprovalStatus', tmpData, 'json');
	
	cboSin = ajaxResultData;
	cboSta = ajaxResultData;
	
	cboSin.push({
		cm_macode : "REQUEST",
		cm_micode : "94",
		cm_codename : "테스트폐기"
	});
	
	cboSin = cboSin.filter(function(data) {
	   return data.cm_macode === "REQUEST";
	});
	
	cboSta = cboSta.filter(function(data) {
	   return data.cm_macode === "APPROVAL";
	});
	
	SBUxMethod.refresh('cboSin');
	SBUxMethod.refresh('cboSta');
}

function cboSta_change_resultHandler(args){
	if(SBUxMethod.get("cboSta") == "01"){
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

function getTeamInfoGrid2(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'TeamInfo'
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/ApprovalStatus', tmpData, 'json');
	
	cboTeam = ajaxResultData;
	SBUxMethod.refresh('cboTeam');
}

function cboGbn_set(){
	cboProc = [
		{cm_codename : "전체", cm_micode : "0"},
		{cm_codename : "반려+체크인취소", cm_micode : "3"},
		{cm_codename : "미완료(에러+진행중)", cm_micode : "1"},
		{cm_codename : "시스템에러", cm_micode : "2"},
		{cm_codename : "진행중", cm_micode : "4"},
		{cm_codename : "처리완료", cm_micode : "9"}
	]
	
	cboGbn = [
		{cm_codename : "전체", cm_micode : "ALL", cm_macode : "REQPASS"},
		{cm_codename : "일반적용", cm_micode : "4", cm_macode : "REQPASS"},
		{cm_codename : "수시적용", cm_micode : "0", cm_macode : "REQPASS"},
		{cm_codename : "긴급적용", cm_micode : "2", cm_macode : "REQPASS"}
	]
	
	SBUxMethod.refresh('cboProc');
	SBUxMethod.refresh('cboGbn');
}

function cmdQry_Proc(){
	var tmpObj = {};
	var strStD = "";
	var strEdD = "";
	var selectedIndex;
		
	if(SBUxMethod.get("cboSta") != "01"){
		strStD = SBUxMethod.get("datStD");
		strEdD = SBUxMethod.get("datEdD");
		
		if(strStD > strEdD){
			alert("조회기간을 정확하게 선택하여 주십시오.");
			strStD = "";
			strEdD = "";
			return;
		}  		
	}
	
	tmpObj.strStD = strStD;
	tmpObj.strEdD = strEdD;
	
	selectedIndex = document.getElementById("cboSyscd");
	
	if(selectedIndex.selectedIndex > 0){
		tmpObj.strSys = SBUxMethod.get("cboSyscd");
		selectedIndex = null;
	} else {
		tmpObj.strSys = "0";
	} 
	
	selectedIndex = document.getElementById("cboGbn");
	
	if(selectedIndex.selectedIndex > 0){
		tmpObj.strGbn = SBUxMethod.get("cboGbn");
		selectedIndex = null;
	} else {
		tmpObj.strGbn = "0";
	}
	
	selectedIndex = document.getElementById("cboSin");
	
	if(selectedIndex.selectedIndex > 0){
		tmpObj.strQry = SBUxMethod.get("cboSin");
		selectedIndex = null;
	} else {
		tmpObj.strQry = "0";
	}
	
	selectedIndex = document.getElementById("cboSta");
	
	if(selectedIndex.selectedIndex > 0){
		tmpObj.strSta = SBUxMethod.get("cboSta");
		selectedIndex = null;
	} else {
		tmpObj.strSta = "0";
	}
	
	selectedIndex = document.getElementById("cboTeam");
	
	if(selectedIndex.selectedIndex > 0){
		tmpObj.strTeam = SBUxMethod.get("cboTeam");
		selectedIndex = null;
	} else {
		tmpObj.strTeam = "0";
	}
	
	selectedIndex = document.getElementById("cboProc");
	
	if(selectedIndex.selectedIndex > 0){
		tmpObj.strProc = SBUxMethod.get("cboProc");
		selectedIndex = null;
	} else {
		tmpObj.strProc = "0";
	}
	
	
	tmpObj.dategbn = SBUxMethod.get("rdoDate");
	
	if(SBUxMethod.get("txtUser") !== undefined){
		tmpObj.txtUser = SBUxMethod.get("txtUser").trim();
	} else {
		tmpObj.txtUser = "";
	}
	
	if(SBUxMethod.get("txtSpms") !== undefined){
		tmpObj.txtSpms = SBUxMethod.get("txtSpms").trim();
	} else {
		tmpObj.txtSpms = "";
	}
	
	tmpObj.strUserId = userid;
	
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'get_SelectList',
			prjData: JSON.stringify(tmpObj)
	}	
	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/ApprovalStatus', tmpData, 'json');
	
	var cnt = Object.keys(ajaxResultData).length;				// json 객체 길이 구하기			
	SBUxMethod.set('lbTotalCnt', '총'+cnt+'건');	// 총 개수 표현		
	
	grid_data = ajaxResultData;
	datagrid.refresh();	
	
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
		{caption : ['시스템'],	ref : 'cm_sysmsg',width : '100px', style : 'text-align:center',  type : 'output'},
		{caption : ['SR-ID'],	ref : 'spms', width : '150px', style : 'text-align:center',	type : 'output'},
		{caption : ['신청번호'],	ref : 'acptno',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청인'],	ref : 'editor',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청종류'],	ref : 'qrycd',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['처리구분'],	ref : 'REQPASS',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청일시'],	ref : 'acptdate',width : '200px',  style : 'text-align:center', type : 'output'},
		{caption : ['진행상태'],	ref : 'acptStatus',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['결재상태'],	ref : 'sta',width : '150px',  style : 'text-align:center', type : 'output'},
		{caption : ['결재사유'],	ref : 'qrycd',width : '150px',  style : 'text-align:center', type : 'output'},
		{caption : ['결재일시'],	ref : 'confdate',width : '150px',  style : 'text-align:center', type : 'output'},		
		{caption : ['적용예정일시'],	ref : 'prcreq',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['선후행작업'],	ref : 'Sunhang',width : '200px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청사유'],	ref : 'sayu', style : 'text-align:center',	type : 'output'}
	];
	datagrid = _SBGrid.create(SBGridProperties); // 만들어진 SBGridProperties 객체를 파라메터로 전달합니다.
}