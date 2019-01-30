var m_datagrid;
var m_SBGridProperties = {};
var m_grid_Data;

parent.job_set = job_set;
parent.modal_createGrid = modal_createGrid;

$(document).ready(function(){
	modal_createGrid();
	job_set();
});

//업무리스트
function job_set(){
	var ajaxReturnData = null;
	var jobData = new Object();
	jobData.UserId	= "MASTER";
	
	var jobInfo = {
		requestType: 'GETJOBLIST',
		jobInfoData: JSON.stringify(jobData)
	}
	ajaxReturnData = ajaxCallWithJson('/webPage/modal/TaskInfo', jobInfo, 'json');
	if( ajaxReturnData !== 'ERR'){
    	m_grid_Data = ajaxReturnData;
    	m_datagrid.rebuild();
    	m_datagrid.refresh();
	}
}

//그리드 선언
function modal_createGrid() {
	m_SBGridProperties.parentid = 'm_sbGridArea'; //그리드를 삽입할 영역의 div id (*)
	m_SBGridProperties.id = 'm_datagrid'; //그리드 객체의 ID (*)
	m_SBGridProperties.jsonref = 'm_grid_Data'; //그리드에 표시될 데이터의 JSON 객체 (*)
	m_SBGridProperties.rowheader = 'seq'
	m_SBGridProperties.columns = [
		{caption : ['업무코드'], 	ref : 'cm_jobcd', 		width : '50%', style : 'text-align: center', type : 'output'},
		{caption : ['업무명'], 	ref : 'cm_jobname',		width : '50%', style : 'text-align: center', type : 'output'}
	];
	m_datagrid = _SBGrid.create(m_SBGridProperties);
}

//닫기
function btnClose_Click() {
	parent.fnCloseModal();
}

//삭제
function btnDel_Click() {
	var nRow = m_datagrid.getRow();
	var res;
	
    if(m_datagrid.getRowData(nRow,false).cm_jobcd != "") {
    	res = confirm("선택하신 업무를 삭제하시겠습니까?");	
    }
	
	if(res) {
		var delData = new Object();
		delData.cm_jobcd = m_datagrid.getRowData(nRow,false).cm_jobcd;
    	
    	var delInfo = {
			requestType		: 'DELJOBINFO',
			delInfoDataArr	: [JSON.stringify(delData)]
		}
		ajaxReturnData = ajaxCallWithJson('/webPage/modal/TaskInfo', delInfo, 'json');
		if( ajaxReturnData !== 'ERR'){
			job_set();
		}
	}
}

//편집
function btnEdit_Click() {
	
	var nRow = m_datagrid.getRow();
	var jobcd = m_datagrid.getRowData(nRow,false).cm_jobcd;
	var jobname = m_datagrid.getRowData(nRow,false).cm_jobname;
	
	if(nRow == "") return;
	var tmpObj = new Object(); 
	tmpObj.jobcd = jobcd; 
	tmpObj.jobname = jobname; 
	window.showModalDialog("./TaskInfoSub.jsp", tmpObj, "dialogWidth:400px; dialogHeight:150px; center:1; scroll:0; help:0; status:0");
	tmpObj = null;
}

//새로만들기
function btnNew_Click() {
	//window.showModelessDialog("./eCmm104_Sub.html", null, "dialogWidth:400px; dialogHeight:150px; center:1; scroll:0; help:0; status:0");
	window.showModalDialog("./TaskInfoSub.jsp", null, "dialogWidth:400px; dialogHeight:150px; center:1; scroll:0; help:0; status:0");
}