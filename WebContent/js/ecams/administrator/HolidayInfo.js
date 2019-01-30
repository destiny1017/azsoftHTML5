/**
 * ajax 공통함수로 교체
 */

var datagrid;
var SBGridProperties = {};
var grid_Data;

var holidayCaseData;
var holidaygbnData;

$(document).ready(function(){
	screenInit();
});

function screenInit() {
	var date = new Date();
	SBUxMethod.set('picker_year', date.getFullYear());
	createGrid();
	getHoliday();
	code_set();
	SBUxMethod.set('picker_date', date.getFullYear() + "/" + date.getMonth()+1 + "/" + date.getDate()); 
}

//휴일목록
function getHoliday(){
	var ajaxReturnData = null;
	var holiData = new Object();
	holiData.year = SBUxMethod.get('picker_year');
	
	var holiInfo = {
		requestType: 'GETHOLIDAY',
		holiInfoData: JSON.stringify(holiData)
	}
	ajaxReturnData = ajaxCallWithJson('/webPage/administrator/HolidayInfo', holiInfo, 'json');
	if( ajaxReturnData !== 'ERR'){
    	grid_Data = ajaxReturnData;
    	datagrid.rebuild();
    	datagrid.refresh();
	}
}

function year_change(){
	getHoliday();
}
  
  	//그리드 선언
function createGrid() {
	SBGridProperties.parentid = 'sbGridArea'; //그리드를 삽입할 영역의 div id (*)
	SBGridProperties.id = 'datagrid'; //그리드 객체의 ID (*)
	SBGridProperties.jsonref = 'grid_Data'; //그리드에 표시될 데이터의 JSON 객체 (*)
	SBGridProperties.columns = [
		{caption : ['휴일'], 		ref : 'cm_holiday1', 	width : '33.3%', style : 'text-align: center', type : 'output'},
		{caption : ['휴일구분'], 	ref : 'holigb_nm',		width : '33.3%', style : 'text-align: center', type : 'output'},
		{caption : ['휴일종류'], 	ref : 'holi_nm',		width : '33.4%', style : 'text-align: center', type : 'output'}
	];
	datagrid = _SBGrid.create(SBGridProperties);
	datagrid.bind('click','gridClick'); //그리드 클릭 이벤트 
}

function gridClick(){
	var nRow = datagrid.getRow();
	
	SBUxMethod.set('picker_date', datagrid.getRowData(nRow,false).cm_holiday2.replace("-",""));
	SBUxMethod.set('selectchk_gbn', datagrid.getRowData(nRow,false).cm_gubuncd);
	SBUxMethod.set('selectchk_case', datagrid.getRowData(nRow,false).cm_msgcd);
}

function code_set(){
	var ajaxReturnData = null;
	var codeData = new Object();
	codeData.cm_macode = "HOLIDAY,HOLICD";
	codeData.selMsg = "SEL";
	codeData.closeYN = "N";
	
	var codeInfo = {
		requestType: 'GETCODEINFO',
		codeInfoData: JSON.stringify(codeData)
	}
	ajaxReturnData = ajaxCallWithJson('/webPage/administrator/CodeInfo', codeInfo, 'json');
	if( ajaxReturnData !== 'ERR'){
    	//filter
    	holidaygbnData = ajaxReturnData.filter(function(item) {
    		return item.cm_macode == 'HOLICD';
    	});
    	SBUxMethod.refresh('selectchk_gbn');
    	
    	holidayCaseData = ajaxReturnData.filter(function(item) {
    		return item.cm_macode == 'HOLIDAY';
    	});
    	SBUxMethod.refresh('selectchk_case');
	}
}

function Regist_Click(){
	var ajaxReturnData = null;
	if(SBUxMethod.get('selectchk_gbn') == '00') { //value값
		alert('휴일구분을 입력하여 주십시오.');
		return;
	}
	
	if(SBUxMethod.get('selectchk_case') == '00') { //value값
		alert('휴일종류을 입력하여 주십시오.');
		return;
	}
	var checkData = new Object();
	checkData.date = SBUxMethod.get('picker_date');

	var checkInfo = {
		requestType: 'CHKHOLIDAY',
		checkInfoData: JSON.stringify(checkData)
	}
	console.log(checkInfo);
	ajaxReturnData = ajaxCallWithJson('/webPage/administrator/HolidayInfo', checkInfo, 'json');
	console.log(ajaxReturnData);
	if( ajaxReturnData !== 'ERR'){
		console.log(ajaxReturnData);
    	if(ajaxReturnData > 0) {
    		SBUxMethod.openModal('modal_dupchk'); //모델명(name)
    	}else {
    		addHoliday('0');
    	}
	}
}

function addHoliday(gbn){
	var ajaxReturnData = null;
	var addHoliData = new Object();
	addHoliData.date = SBUxMethod.get('picker_date');
	addHoliData.holidaygbn = SBUxMethod.get('selectchk_gbn'); //휴일구분
	addHoliData.holidaycase = SBUxMethod.get('selectchk_case'); //휴일종류
	addHoliData.updtSW = gbn; //modal: 1, else : 0
	
	if(gbn == '1') SBUxMethod.closeModal("modal_dupchk");
	
	var addHoliInfo = {
		requestType: 'ADDHOLIDAY',
		addHoliInfoData: JSON.stringify(addHoliData)
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/administrator/HolidayInfo', addHoliInfo, 'json');
	if( ajaxReturnData !== 'ERR'){
		alert(ajaxReturnData.trim());
    	getHoliday();
	}
}

function Delete_Click(){
	SBUxMethod.openModal('modal_delconfirm'); //모델명(name)
}

function deleteHoliday(){
	var ajaxReturnData = null;
	SBUxMethod.closeModal("modal_delconfirm");

	var delHoliData = new Object();
	delHoliData.date = SBUxMethod.get('picker_date');
	
	var delHoliInfo = {
		requestType		: 'DELHOLIDAY',
		delHoliInfoData	: JSON.stringify(delHoliData)
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/administrator/HolidayInfo', delHoliInfo, 'json');
	if( ajaxReturnData !== 'ERR'){
		alert(ajaxReturnData.trim());
    	getHoliday();
	}
}