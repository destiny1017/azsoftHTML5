// userId 및 reqcd 가져오기
var userid = window.parent.userId;
var request =  new Request();
strReqCD = request.getParameter('reqcd');

// 변수
var SecuYn;
var L_SysCd = "";

//그리드 변수
var grid_data;

// 콤보박스 변수
var cbo_Option;
var cboSysCd;
var cbo_Cond10;
var cbo_Cond2;
var cbo_Cond11;



$(document).ready(function(){
	cboOption_set();
	isAdmin();
	getJogun(2);
	createGrid();
});

function cboOption_set(){
	cbo_Option = [
		{cm_codename : "전체", cm_micode : "0", cm_macode : "OPTION"},
		{cm_codename : "신규중 제외", cm_micode : "1", cm_macode : "OPTION"},
		{cm_codename : "신규중", cm_micode : "2", cm_macode : "OPTION"},
		{cm_codename : "폐기분 제외", cm_micode : "3", cm_macode : "OPTION"},
		{cm_codename : "폐기분", cm_micode : "4", cm_macode : "OPTION"}
	]
	SBUxMethod.refresh('cbo_Option');
}

function isAdmin(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'UserInfo',
			UserId : userid
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/report/PrgListReport', tmpData, 'json');
	
	if(ajaxResultData === true){
		SecuYn = "Y";
	} else {
		SecuYn = "N";
	}
	
	ajaxResultData = null;
	tmpData = null;
	
	if(SecuYn == "Y"){
		tmpData = {
				requestType : 'getSysInfo',
				UserId : userid,
				tmp	   : 'N'
		}
		ajaxResultData = ajaxCallWithJson('/webPage/report/PrgListReport', tmpData, 'json');
	} else {
		tmpData = {
				requestType : 'getSysInfo',
				UserId : userid,
				tmp    : 'Y'
		}
		ajaxResultData = ajaxCallWithJson('/webPage/report/PrgListReport', tmpData, 'json');
	}
	getSysInfo_resultHandler(ajaxResultData);
}

function getSysInfo_resultHandler(resultData){
	var count = 0;
	var ajaxResultData = null;
	var selectedIndex;
	cboSysCd = resultData;
	
	cboSysCd = cboSysCd.filter(function(data) {
		return data.cm_sysinfo.substring(0,1) !== "1";
	});
	
	SBUxMethod.refresh('cboSysCd');
	
	getJogun(1);
}

function getJogun(vari){
	var ajaxResultData = null;
	var selectedIndex;
	var tmpData = {
			requestType : 'getJogun',
			cnt : vari
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/report/PrgListReport', tmpData, 'json');
	
	if(ajaxResultData[0].Index === "1"){
		console.log("1");
		cbo_Cond10 = ajaxResultData;
		SBUxMethod.refresh('cbo_Cond10');
		if(cbo_Cond10.length > 1){
			$("#cbo_Cond10 option:eq(2)").prop("selected", true);
			setJogunOne(0);
		}
	} else {
		cbo_Cond11 = ajaxResultData;
		SBUxMethod.refresh('cbo_Cond11');
		selectedIndex = document.getElementById("cbo_Cond11");
		SBUxMethod.set('lbl_Cond1',  ajaxResultData[selectedIndex.selectedIndex].cm_codename);
	}
}

function setJogunOne(index){
	var selectedIndex;
	if(cboSysCd.length > 0) L_SysCd = SBUxMethod.get("cboSysCd");
	else L_SysCd = "";
	
	setJogunTwo(index);
	
	cbo_Cond2 = null;
	selectedIndex = document.getElementById("cbo_Cond10");
	if(selectedIndex.selectedIndex < 1){
		SBUxMethod.hide('lbl_Cond0');
		SBUxMethod.attr('cbo_Cond2', 'readonly', 'true');
	} else {
		if (L_SysCd == ""){
		}else{
			SBUxMethod.show('lbl_Cond0');
			SBUxMethod.attr('cbo_Cond2', 'readonly', 'false');
			var cnt = selectedIndex.selectedIndex;
			SBUxMethod.set('lbl_Cond0', cbo_Cond10[cnt].cm_codename);
			getCode(cnt);
		}
	}
}

function setJogunTwo(index){
	var selectedIndex;
	SBUxMethod.set('txt_Cond', "");
	selectedIndex = document.getElementById("cbo_Cond11");	
	if(selectedIndex.selectedIndex < 1){
		SBUxMethod.hide('lbl_Cond1');
		SBUxMethod.attr('txt_Cond', 'readonly', 'true');
		SBUxMethod.set('txt_Cond', "");
	} else {
		SBUxMethod.show('lbl_Cond1');
		SBUxMethod.set('lbl_Cond1', cbo_Cond11[selectedIndex.selectedIndex].cm_codename);			
		SBUxMethod.attr('txt_Cond', 'readonly', 'false');
	}
}

function getCode(cnt){
	var ajaxResultData = null;
	var selectedIndex;
	var tmpData = {
			requestType : 'getCode',
			L_SysCd : L_SysCd,
			cnt		: cnt
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/report/PrgListReport', tmpData, 'json');
	
	cbo_Cond2 = ajaxResultData;
	SBUxMethod.refresh('cbo_Cond2');
	selectedIndex = document.getElementById("cbo_Cond10");
	if(selectedIndex.selectedIndex == "2" && cbo_Cond2.length > 2){
		$("#cbo_Cond2 option:eq(3)").prop("selected", true);
	}
	
	Sql_Qry();
}

function Sql_Qry(){
	var tmpObj = {};
	var selectedIndex;
	var L_JobCd = ""; // 개발당시 nice데모에서는 값을 공백으로 보내줌
	
	tmpObj.UserId = userid;
	tmpObj.SecuYn = SecuYn;
	tmpObj.L_SysCd = L_SysCd;
	tmpObj.L_JobCd = L_JobCd;
	
	selectedIndex = document.getElementById("cbo_Cond10");	
	tmpObj.Cbo_Cond10_code = selectedIndex.selectedIndex;
	selectedIndex = null;
	
	selectedIndex = document.getElementById("cbo_Cond11");	
	tmpObj.Cbo_Cond11_code = selectedIndex.selectedIndex;
	selectedIndex = null;
	
	selectedIndex = document.getElementById("cbo_Cond2");
	if(cbo_Cond2.length > 0 && selectedIndex.selectedIndex > 0) tmpObj.Cbo_Cond2_code = SBUxMethod.get("cbo_Cond2");
	else tmpObj.Cbo_Cond2_code = "";
	
	if(SBUxMethod.get("txt_Cond") !== undefined){
		tmpObj.Txt_Cond = SBUxMethod.get("txt_Cond");
	} else {
		tmpObj.Txt_Cond = "";
	}
	
	tmpObj.Cbo_Option = SBUxMethod.get("cbo_Option");
	tmpObj.Chk_Aply = SBUxMethod.get('chkDetail').chkDetail;
	
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'getSql_Qry',
			prjData: JSON.stringify(tmpObj)
	}	
	
	
	ajaxResultData = ajaxCallWithJson('/webPage/report/PrgListReport', tmpData, 'json');
	
	console.log(ajaxResultData);
	
	if(Object.keys(ajaxResultData).length > 0){
		var cnt = Object.keys(ajaxResultData).length;				// json 객체 길이 구하기			
		SBUxMethod.set('lbTotalCnt', '총'+cnt+'건');	// 총 개수 표현		
	}

	grid_data = ajaxResultData;
	datagrid.refresh();
}

// 그리드 생성 함수
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
		{caption : ['시스템'],	ref : 'job',width : '100px', style : 'text-align:center',  type : 'output'},
		{caption : ['프로그램명'],	ref : 'cr_rsrcname', width : '150px', style : 'text-align:center',	type : 'output'},
		{caption : ['프로그램경로'],	ref : 'cm_dirpath',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['상태'],	ref : 'sta',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['버전'],	ref : 'cr_lstver',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['프로그램종류'],	ref : 'rsrccd',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['프로그램설명'],	ref : 'cr_story',width : '200px',  style : 'text-align:center', type : 'output'},
		{caption : ['최종변경자'],	ref : 'cm_username',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['최종변경일'],	ref : 'cr_lastdate',width : '150px',  style : 'text-align:center', type : 'output'}
	];
	datagrid = _SBGrid.create(SBGridProperties); // 만들어진 SBGridProperties 객체를 파라메터로 전달합니다.
}
