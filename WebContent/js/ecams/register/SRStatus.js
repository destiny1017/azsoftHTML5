/* 그리드 생성 시작 */
var menuJson; 		
var datagrid;	// 그리드를 담기위한 객체를 선언합니다.
var grid_data;
var userid = window.parent.userId;        	
var cboDept1;
var cboDept2;

var cboSta1;
var cboSta2;


var SBGridProperties = {};

//reqcd param 불러오기
var request =  new Request();
var strReqCD = "";
strReqCD = request.getParameter('reqcd');


$(document).ready(function(){
	if(strReqCD == "" || strReqCD == null){
		strReqCd = "MY";
	}
	
	if(strReqCD == "MY" || strReqCD =="1" || strReqCD == "A"){
		
	}
	
	SBUxMethod.set('datStD', getDate('DATE',0));
	SBUxMethod.set('datEdD', getDate('DATE',0));
	dept_set();
	dept_set2();
	codeinfo_set();
	createGrid();
});

function dept_set(){	// 요청부서
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'TeamInfo'			
	}	
	
	ajaxResultData = ajaxCallWithJson('/webPage/regist/SRStatus', tmpData, 'json');
	
	cboDept1 = ajaxResultData;
	SBUxMethod.refresh('cboDept1');
} 

function dept_set2(){	// 등록부서
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'TeamInfo2'			
	}
	
	ajaxResultData = ajaxCallWithJson('/webPage/regist/SRStatus', tmpData, 'json');
	cboDept2 = ajaxResultData;
	SBUxMethod.refresh('cboDept2');
}

function codeinfo_set(){	// 코드인포
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'CodeInfo'			
	}
	ajaxResultData = ajaxCallWithJson('/webPage/regist/SRStatus', tmpData, 'json');
	
	cboSta1 = ajaxResultData;
	cboSta2 = ajaxResultData;
	
	cboSta1.push({
		cm_macode : "ISRSTA",
		cm_micode : "XX",
		cm_codename : "미완료전체"
	});
	
	cboSta2.push({
		cm_macode : "ISRSTAUSR",
		cm_micode : "XX",
		cm_codename : "미완료전체"
	});
	
	cboSta1 = cboSta1.filter(function(data) {
	   return data.cm_macode === "ISRSTA";
	});
	
	cboSta2 = cboSta2.filter(function(data) {
	   return data.cm_macode === "ISRSTAUSR";
	});
	
	SBUxMethod.refresh('cboSta1');
	SBUxMethod.refresh('cboSta2');
}

function fnChange(args){ // 달력 활성화
	$('#datStD').attr('disabled', true);
	$('[name="_datStD_sub"]').attr('disabled', true);
	$('#datEdD').attr('disabled', true);
	$('[name="_datEdD_sub"]').attr('disabled', true);
	if(SBUxMethod.get("cboSta1") == "00" || SBUxMethod.get("cboSta1") == "0" || SBUxMethod.get("cboSta1") == "3" || SBUxMethod.get("cboSta1") == "8" || SBUxMethod.get("cboSta1") == "9"){
		if(SBUxMethod.get("cboSta2") == "00"){
			$('#datStD').attr('disabled', false);
			$('[name="_datStD_sub"]').attr('disabled', false);
			$('#datEdD').attr('disabled', false);
			$('[name="_datEdD_sub"]').attr('disabled', false);
		}else{
			$('#datStD').attr('disabled', true);
			$('[name="_datStD_sub"]').attr('disabled', true);
			$('#datEdD').attr('disabled', true);
			$('[name="_datEdD_sub"]').attr('disabled', true);
		}
	}
}

function cmdQry_Proc(){	// 조회
	var ajaxResultData = null;
	var errSw = false;
	if(SBUxMethod.get("cboSta2") != "00" && SBUxMethod.get("cboSta1") != "00"){
		if(SBUxMethod.get("cboSta2") != "3" && SBUxMethod.get("cboSta2") != "8" && SBUxMethod.get("cboSta2") != "9"){	// 3 : 제외 / 8 : 진행중단 / 9 : 완료
			if(SBUxMethod.get("cboSta1") == "0") errSw = true;
			else if(SBUxMethod.get("cboSta1") == "3") errSw = true;
			else if(SBUxMethod.get("cboSta1") == "8") errSw = true;
			else if(SBUxMethod.get("cboSta1") == "9") errSw = true;
    	}
	}
	
	if(errSw){
		alert("상태를 정확하게 선택하여 주시기 바랍니다.");
		return;
	}
	
	var strStD = SBUxMethod.get("datStD");
	var strEdD = SBUxMethod.get("datEdD");
	var tmpObj = {};
	
	if(strStD > strEdD){
		alert("조회기간을 정확하게 선택하여 주십시오.");
		return;
	}  
	
	if(!$('#datStD').is(':disabled')  && !$('#datEdD').is(':disabled')){
		tmpObj.stday = strStD;
    	tmpObj.edday = strEdD;	 
	}  
	
	if(SBUxMethod.get("cboDept1") != "0" ) {
		tmpObj.reqdept = SBUxMethod.get("cboDept1");
	}
	
	if(SBUxMethod.get("cboDept2") != "0" ){
		tmpObj.recvdept = SBUxMethod.get("cboDept2");
	}
	if(SBUxMethod.get("cboSta1") != "00"){
		tmpObj.reqsta1 = SBUxMethod.get("cboSta1");
	}
	if(SBUxMethod.get("cboSta2") != "00"){
		tmpObj.reqsta2 = SBUxMethod.get("cboSta2");
	}
	
	if(SBUxMethod.get("txtTit") != undefined && SBUxMethod.get("txtTit").length > 0){
		tmpObj.reqtit = SBUxMethod.get("txtTit");
	}
	
	if(JSON.stringify(SBUxMethod.get('rdo_norm')) == '"T"'){
		tmpObj.selfsw = "M";	
	} else if(JSON.stringify(SBUxMethod.get('rdo_norm')) == '"A"') {
		tmpObj.selfsw = "T";
	} else {
		tmpObj.selfsw = "N";
	}          	
					
	tmpObj.userid = userid;

	var tmpData = {
			prjData: 		JSON.stringify(tmpObj),
			requestType : 'PrjInfo'			
	}
	
	ajaxResultData = ajaxCallWithJson('/webPage/regist/SRStatus', tmpData, 'json');
	
	var cnt = Object.keys(ajaxResultData).length;				// json 객체 길이 구하기			
	SBUxMethod.set('lbTotalCnt', '총'+cnt+'건');	// 총 개수 표현		
	
	grid_data = ajaxResultData;
	//datagrid.rebuild(); 		// rebuild 없어도 나오긴 함 검색버튼 두번 누르면    	
	datagrid.refresh();
	
	$(ajaxResultData).each(function(i){
		if(ajaxResultData[i].color == '3' || ajaxResultData[i].color == 'A'){
			datagrid.setRowStyle(i+1, 'data', 'color', '#FF0000');	//반려 또는 취소
			datagrid.setRowStyle(i+1, 'data', 'font-weight', 'bold');
		} else if (ajaxResultData[i].color == 'R'){
			datagrid.setRowStyle(i+1, 'data', 'color', '#FF8000');	// 접수
			datagrid.setRowStyle(i+1, 'data', 'font-weight', 'bold');
		} else if (ajaxResultData[i].color == 'C'){
			datagrid.setRowStyle(i+1, 'data', 'color', '#145A32');	// 개발
			datagrid.setRowStyle(i+1, 'data', 'font-weight', 'bold');
		} else if (ajaxResultData[i].color == 'T'){
			datagrid.setRowStyle(i+1, 'data', 'color', '#BE81F7');	// 테스트
			datagrid.setRowStyle(i+1, 'data', 'font-weight', 'bold');
		} else if (ajaxResultData[i].color == 'P'){
			datagrid.setRowStyle(i+1, 'data', 'color', '#045FB4');	// 적용
			datagrid.setRowStyle(i+1, 'data', 'font-weight', 'bold');
		} else if (ajaxResultData[i].color == '9'){
			datagrid.setRowStyle(i+1, 'data', 'color', '#2E2E2E');	// 처리완료
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
		{caption : ['SR-ID'],	ref : 'isrid', width : '150', style : 'text-align:center',	type : 'output'},
		{caption : ['문서번호'],	ref : 'genieid',width : '150px',  style : 'text-align:center', type : 'output'},
		{caption : ['등록일'],	ref : 'recvdate',width : '100px', style : 'text-align:center',  type : 'output'},
		{caption : ['요청부서'],	ref : 'reqdept',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['SR상태'],	ref : 'reqsta1',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['요청제목'],	ref : 'reqtitle',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['완료요청일'],	ref : 'reqedday',width : '100px',  style : 'text-align:center', type : 'output'},
		{caption : ['등록부서'],	ref : 'comdept',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['등록인'],	ref : 'recvuser',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['개발부서'],	ref : 'recvdept',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['개발담장자'],	ref : 'devuser',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['개발자상태'],	ref : 'reqsta2',width : '200px',  style : 'text-align:center',	type : 'output'},
		{caption : ['개발기간'],	ref : 'chgdevterm',width : '250px',  style : 'text-align:center',	type : 'output'},
		{caption : ['개발계획공수'],	ref : 'chgdevtime',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['개발투입공수'],	ref : 'realworktime',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['검수투입공수'],	ref : 'chgworktime',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['개발진행율'],	ref : 'chgpercent',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['변경종료구분'],	ref : 'chgedgbn',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['변경종료일'],	ref : 'chgeddate',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['SR완료구분'],	ref : 'isredgbn',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['SR완료일'],	ref : 'isreddate',width : '100px',  style : 'text-align:center',	type : 'output'}
	];
	datagrid = _SBGrid.create(SBGridProperties); // 만들어진 SBGridProperties 객체를 파라메터로 전달합니다.
}