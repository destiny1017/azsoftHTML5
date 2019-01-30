/**
 * 	ajax 호출부분 /js/ecams/common/common.js 의 ajaxCallWithJson function으로 바꾸었습니다.
 */
var codeInfoData;
var UseData;
var menuJson;
var datagrid;
var SBGridProperties = {};
var grid_Data;
var userId = window.parent.userId;

$(document).ready(function(){
	console.log('CodeInfo Page load...');
	screenInit();
});

function screenInit() {
	code_set();
	createGrid();
}

//검색조건 셋팅
function code_set(){
	var ajaxReturnData = null;
	var codeData = new Object();
	codeData.CLASSNAME = "CodeInfo";
	codeData.method = "getCodeInfo";
	codeData.cm_macode = "CODEINFO";
	codeData.selMsg = "";
	codeData.closeYN = "N";
	
	var codeInfo = {
		requestType: 'GETCODEINFO',
		codeInfoData: JSON.stringify(codeData)
	}
	ajaxReturnData = ajaxCallWithJson('/webPage/administrator/CodeInfo', codeInfo, 'json');
	if( ajaxReturnData !== 'ERR'){
    	codeInfoData = ajaxReturnData;
    	SBUxMethod.refresh('selectchk_codeInfo'); //name
	}
}

var UseData = [
    {"text" : "사용",		"value" : "사용" },
    {"text" : "미사용", 	"value" : "미사용"}
];

//그리드 선언
function createGrid() {
	SBGridProperties.parentid = 'sbGridArea'; //그리드를 삽입할 영역의 div id (*)
	SBGridProperties.id = 'datagrid'; //그리드 객체의 ID (*)
	SBGridProperties.jsonref = 'grid_Data'; //그리드에 표시될 데이터의 JSON 객체 (*)
	SBGridProperties.rowheader = 'seq'
	SBGridProperties.columns = [
		{caption : ['대구분'], 	ref : 'cm_macode', 		width : '20%', style : 'text-align: center', type : 'output'},
		{caption : ['소구분'], 	ref : 'cm_micode', 		width : '20%', style : 'text-align: center', type : 'output'},
		{caption : ['코드명칭'], 	ref : 'cm_codename', 	width : '45%', style : 'text-align: center', type : 'output'},
		{caption : ['소구분순서'], ref : 'cm_seqno', 		width : '10%', style : 'text-align: center', type : 'output'},
		{caption : ['등록일'], 	ref : 'cm_creatdt', 	width : '20%', style : 'text-align: center', type : 'output'},
		{caption : ['최종등록일'], ref : 'cm_lastupdt', 	width : '20%', style : 'text-align: center', type : 'output'},
		{caption : ['사용여부'], 	ref : 'closeYN', 		width : '10%', style : 'text-align: center', type : 'output'},
		{caption : ['폐기일'], 	ref : 'cm_closedt', 	width : '25%', style : 'text-align: center', type : 'output'}
	];
	datagrid = _SBGrid.create(SBGridProperties);
	datagrid.bind('dblclick','gridDblclick');
}

//input 타입 엔터이벤트
function code_Enter(idx) {
	var ajaxReturnData = null;
	
	if(idx == '') idx = SBUxMethod.get('selectchk_codeInfo');
	SBUxMethod.set('selectchk_codeInfo', idx); //name, value
	
	var searchData = new Object();
	searchData.UserId = userId;
	searchData.CboMicode = SBUxMethod.get('selectchk_codeInfo'); //검색조건(value값)
	searchData.Txt_Code0 = SBUxMethod.get('search_macode'); //대구분 코드값
	searchData.Txt_Code1 = SBUxMethod.get('search_macodecmt'); //대구분 코드설명
	searchData.Txt_Code2 = SBUxMethod.get('search_micode'); //소구분 코드값
	searchData.Txt_Code3 = SBUxMethod.get('search_micodecmt'); //소구분 코드설명
	
	if(searchData.Txt_Code0 == '') {
		alert('코드값을 입력하여 주십시오.');
		return;
	}
	
	var searchInfo = {
		requestType: 'GETCODELIST',
		searchInfoData: JSON.stringify(searchData)
	}
	console.log(searchInfo);
	ajaxReturnData = ajaxCallWithJson('/webPage/administrator/CodeInfo', searchInfo, 'json');
	if( ajaxReturnData !== 'ERR'){
		grid_Data = ajaxReturnData;
    	datagrid.refresh();
	}
}

//그리드 더블클릭 이벤트
function gridDblclick() {
	var ajaxReturnData = null;
	var nRow = datagrid.getRow();
    
    if(datagrid.getRowData(nRow,false).cm_micode == "****") {
    	SBUxMethod.set('search_macode', datagrid.getRowData(nRow,false).cm_macode);
    	SBUxMethod.set('search_macodecmt', datagrid.getRowData(nRow,false).cm_codename);
    	SBUxMethod.set('search_micode', datagrid.getRowData(nRow,false).cm_micode);
    	SBUxMethod.set('search_micodecmt', "");
    	SBUxMethod.set('selectchk_codeInfo', '02'); //name, value
    	code_Enter("02");
    }else {
    	var codeNameData = new Object();
    	codeNameData.CLASSNAME = "Cmm0100";
    	codeNameData.method = "getCodeName";
    	codeNameData.macode	= datagrid.getRowData(nRow,false).cm_macode;
    	
    	var codeNameInfo = {
			requestType: 'GETCODENAME',
			codeNameInfoData: JSON.stringify(codeNameData)
		}
		ajaxReturnData = ajaxCallWithJson('/webPage/administrator/CodeInfo', codeNameInfo, 'json');
    	
    	var i = 0;
    	if( ajaxReturnData !== 'ERR') {
    		SBUxMethod.set('search_macode', datagrid.getRowData(nRow,false).cm_macode);
	    	SBUxMethod.set('search_macodecmt', ajaxReturnData);
	    	SBUxMethod.set('search_micode', datagrid.getRowData(nRow,false).cm_micode);
	    	SBUxMethod.set('search_micodecmt', datagrid.getRowData(nRow,false).cm_codename);
	    	SBUxMethod.set('search_seqno', datagrid.getRowData(nRow,false).cm_seqno);
	    	$(UseData).each(function(i) {
	    		if(UseData[i].text == datagrid.getRowData(nRow,false).closeYN) {
	    			SBUxMethod.set('selectchk_useyn', datagrid.getRowData(nRow,false).closeYN);
	    		}
	    	});
    	}
    }
};

//적용버튼 클릭 이벤트
function set_Click() {
	if(SBUxMethod.get('search_macode') == '' || SBUxMethod.get('search_macode') == undefined) {
		alert('대구분 코드값을 입력하여 주십시오.');
		return;
	}
	
	if(SBUxMethod.get('search_macodecmt') == '' || SBUxMethod.get('search_macodecmt') == undefined) {
		alert('대구분 코드설명을 입력하여 주십시오.');
		return;
	}
	
	if(SBUxMethod.get('search_micode') == '' || SBUxMethod.get('search_micode') == undefined) {
		alert('소구분 코드값을 입력하여 주십시오.');
		return;
	}
	
	if(SBUxMethod.get('search_micodecmt') == '' || SBUxMethod.get('search_micodecmt') == undefined) {
		alert('소구분 코드설명을 입력하여 주십시오.');
		return;
	}
	
	SBUxMethod.set('search_macode', SBUxMethod.get('search_macode').toUpperCase()); 
	
	var codeValueData = new Object();
	codeValueData.CLASSNAME = "Cmm0100";
	codeValueData.method = "setCodeValue";
	codeValueData.CboMicode = SBUxMethod.get('selectchk_codeInfo'); //검색조건(value값)
	codeValueData.Txt_Code0 = SBUxMethod.get('search_macode'); 	  //대구분 코드값
	codeValueData.Txt_Code1 = SBUxMethod.get('search_macodecmt');   //대구분 코드설명
	codeValueData.Txt_Code2 = SBUxMethod.get('search_micode');      //소구분 코드값
	codeValueData.Txt_Code3 = SBUxMethod.get('search_micodecmt');   //소구분 코드설명
	codeValueData.Txt_Code4 = SBUxMethod.get('search_seqno');   	  //순서
	codeValueData.closeYN = SBUxMethod.get('selectchk_useyn');      //사용구분
	
	
	var codeValueInfo = {
		requestType: 'SETCODEVALUE',
		codeValueInfoData: JSON.stringify(codeValueData)
	}
	ajaxReturnData = ajaxCallWithJson('/webPage/administrator/CodeInfo', codeValueInfo, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		var i = 0;
		alert("코드설명 [" + ajaxReturnData + "] 이(가) 적용 되였습니다.");
    	
    	SBUxMethod.set('search_micode', 	"****");
    	SBUxMethod.set('search_micodecmt', 	"");
    	SBUxMethod.set('selectchk_codeInfo','02'); //name, value
    	code_Enter("02");
	}
};

function fnCloseModal(){
	SBUxMethod.closeModal("modal_middle");
}