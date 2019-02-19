// userId 및 reqcd 가져오기
var userid = window.parent.userId;
var request =  new Request();
strReqCD = request.getParameter('reqcd');

// 변수 선언
var strDeptCd;
var strRgtCd;

// 콤보박스 변수
var cboSysCd;
var cboSin;
var cboSta;
var cboDept;
var cboGbn;

// 그리드 변수
var grid_data;

$(document).ready(function(){
	if(strReqCD != null && strReqCD != ""){
		if(strReqCD.length > 2) strReqCD.substring(0, 2);
		else strReqCD = "";
	}
	SBUxMethod.set('datStD', getDate('DATE',0));
	SBUxMethod.set('datEdD', getDate('DATE',0));
	
	getUserInfo();
	cboGbn_set();
	createGrid();
});

function getUserInfo(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'UserInfochk',
			UserId : userid
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/RequestStatus', tmpData, 'json');
	
	if(strReqCD != null && strReqCD != ""){
		SBUxMethod.set('txtUser', ajaxResultData[0].cm_username);
	}
	
	getPMOInfo();
}

function getPMOInfo(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'UserPMOInfo',
			UserId : userid
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/RequestStatus', tmpData, 'json');
	
	if(ajaxResultData.length > 1){
		strDeptCd = ajaxResultData.substring(1);
		strRgtCd = ajaxResultData.substring(0,1);
	}
	
	getSysInfo();
	getCodeInfo();
	getTeamInfoGrid2();
}

function getSysInfo(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'SysInfo',
			UserId : userid
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/RequestStatus', tmpData, 'json');
	
	cboSysCd = ajaxResultData;        	        	        	
	SBUxMethod.refresh('cboSysCd');
}

function getCodeInfo(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'CodeInfo_1',
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/RequestStatus', tmpData, 'json');
	
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
	
	if(strReqCD != null && strReqCD != ""){
		cboSta.push({
			cm_macode : "R3200STA",
			cm_micode : "RB",
			cm_codename : "긴급롤백미정리건"
		});
	}
	
	
	cboSta = cboSta.filter(function(data) {
		   return data.cm_macode === "R3200STA";
	});
	
	for(var i = 0 ; i < cboSta.length ; i++){
		if(cboSta[i].cm_micode == "9"){
			cboSta[i].cm_codename = "처리완료";
		}
	}
	
	SBUxMethod.refresh('cboSin');
	SBUxMethod.refresh('cboSta');
}

function cboSta_change_resultHandler(args){
	if(SBUxMethod.get("cboSta") == "00" || SBUxMethod.get("cboSta") == "3" || SBUxMethod.get("cboSta") == "9"){
		$('#datStD').attr('disabled', false);
		$('[name="_datStD_sub"]').attr('disabled', false);
		$('#datEdD').attr('disabled', false);
		$('[name="_datEdD_sub"]').attr('disabled', false);
	} else {
		$('#datStD').attr('disabled', true);
		$('[name="_datStD_sub"]').attr('disabled', true);
		$('#datEdD').attr('disabled', true);
		$('[name="_datEdD_sub"]').attr('disabled', true);
	}
}
	
function getTeamInfoGrid2(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'TeamInfo',
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/RequestStatus', tmpData, 'json');
	
	cboDept = ajaxResultData;
	SBUxMethod.refresh('cboDept');
}

function cboGbn_set(){
	cboGbn = [
		{cm_codename : "전체", cm_micode : "ALL", cm_macode : "REQPASS"},
		{cm_codename : "일반적용", cm_micode : "4", cm_macode : "REQPASS"},
		{cm_codename : "수시적용", cm_micode : "0", cm_macode : "REQPASS"},
		{cm_codename : "긴급적용", cm_micode : "2", cm_macode : "REQPASS"}
	]
	SBUxMethod.refresh('cboGbn');
}

function cmdQry_Proc(){
	var tmpObj = {};
	var selectedIndex;
	var strStD = "";
	var strEdD = "";
	
	strStD = SBUxMethod.get("datStD");
	strEdD = SBUxMethod.get("datEdD");
	
	if(strStD > strEdD){
		alert("조회기간을 정확하게 선택하여 주십시오.");
		strStD = "";
		strEdD = "";
		return;
	}  
	
	tmpObj.strStD = strStD;
	tmpObj.strEdD = strEdD;
	
	selectedIndex = document.getElementById("cboSysCd");
	if(selectedIndex.selectedIndex > 0){
		tmpObj.strSys = SBUxMethod.get("cboSysCd");
		selectedIndex = null;
	}
	
	selectedIndex = document.getElementById("cboSin");
	if(selectedIndex.selectedIndex > 0){
		tmpObj.strQry = SBUxMethod.get("cboSin");
		selectedIndex = null;
	}
	
	selectedIndex = document.getElementById("cboSta");
	if(selectedIndex.selectedIndex > 0){
		tmpObj.strSta = SBUxMethod.get("cboSta");
		selectedIndex = null;
	}
	
	selectedIndex = document.getElementById("cboDept");
	if(selectedIndex.selectedIndex > 0){
		tmpObj.strTeam = SBUxMethod.get("cboDept");
		selectedIndex = null;
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
	tmpObj.cboGbn = SBUxMethod.get("cboGbn");
	
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'get_SelectList',
			prjData: JSON.stringify(tmpObj)
	}	
	
	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/RequestStatus', tmpData, 'json');
	
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
		{caption : ['시스템'],	ref : 'syscd',width : '100px', style : 'text-align:center',  type : 'output'},
		{caption : ['SR-ID'],	ref : 'spms', width : '150px', style : 'text-align:center',	type : 'output'},
		{caption : ['신청번호'],	ref : 'acptno',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청자'],	ref : 'editor',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청종류'],	ref : 'qrycd',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['처리구분'],	ref : 'passok',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청일시'],	ref : 'acptdate',width : '200px',  style : 'text-align:center', type : 'output'},
		{caption : ['진행상태'],	ref : 'sta',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['프로그램명'],	ref : 'pgmid',width : '150px',  style : 'text-align:center', type : 'output'},
		{caption : ['완료일시'],	ref : 'prcdate',width : '150px',  style : 'text-align:center', type : 'output'},
		{caption : ['적용예정일시'],	ref : 'prcreq',width : '150px',  style : 'text-align:center', type : 'output'},		
		{caption : ['선후행'],	ref : 'Sunhang',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청사유'],	ref : 'sayu', style : 'text-align:center',	type : 'output'}
	];
	datagrid = _SBGrid.create(SBGridProperties); // 만들어진 SBGridProperties 객체를 파라메터로 전달합니다.
}