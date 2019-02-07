var userid = window.parent.userId;     
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
});


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