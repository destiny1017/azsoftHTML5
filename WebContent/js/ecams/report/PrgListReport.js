// userId 및 reqcd 가져오기
var userid = window.parent.userId;
var request =  new Request();
strReqCD = request.getParameter('reqcd');

// 변수
var SecuYn;

//그리드 변수
var grid_data;

// 콤보박스 변수
var cbo_Option;
var cboSysCd;


$(document).ready(function(){
	createGrid();
	cboOption_set();
	isAdmin();
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
	var L_SysCd = "";
	var ajaxResultData = null;
	
	cboSysCd = resultData;
	
	cboSysCd = cboSysCd.filter(function(data) {
		return data.cm_sysinfo.substring(0,1) !== "1";
	});
	
	SBUxMethod.refresh('cboSysCd');
	
	// eCmd3100 Cmd3100.getJogun(1); line 154
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