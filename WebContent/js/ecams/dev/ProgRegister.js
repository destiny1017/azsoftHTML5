/** 프로그램등록 화면 기능
 * 
 * <pre>
 * <b>History</b>
 * 	작성자: 허정규
 * 	버전 : 1.0
 *  수정일 : 2019-02-19
 */
var request =  new Request();
var userId = window.parent.userId;

var GridData;

var selectedSysData;
var selectedJobData;
var selectedRsrcData;
var selectedDirData;
var selectedSrIdData;

$(document).ready(function() {
	screenInit();
	
	$(window).resize(function(){ //창이 바뀔때 버튼 밑으로 몰아넣음
		if($(this).width()<768){
			$("#btnGroup1").appendTo($("#btnArea"));
			$("#btnGroup2").appendTo($("#btnArea"));
		}
		else{
			$("#btnGroup1").appendTo($(".row").eq(2));
			$("#btnGroup2").appendTo($(".row").eq(3));
		}
	});
	

	$('#select_system').dropdown({
	    allowAdditions: true,
	    maxSelections: 3
	});
});

function screenInit() {
	reqcd =  request.getParameter('reqcd');
	//createElements(); //그리드 생성
	//getCodeInfoCommon();
	setSysCbo();
}

function setSysCbo(){ //시스템
	var tmpObj = new Object();
	tmpObj.UserId = userId;
	tmpObj.SelMsg = 'SEL';
	tmpObj.SecuYn = 'N';
	tmpObj.CloseYn = 'N';
	tmpObj.ReqCd = '06';
	
	var tmpArr = {
		Data: 		JSON.stringify(tmpObj),
		requestType: 	'SYS_INFO'
	}
	
	selectedSysData = ajaxCallWithJson('/webPage/common/CommonSysInfo', tmpArr, 'json');
	selectedSysData = $.grep(selectedSysData,function(n,i){
		return n.cm_sysinfo.substring(0,1) !== "1" && n.cm_sysinfo.length > 1;
	});
	
	
	$("#select_system").empty();
	
	$.each(selectedSysData,function(key,value) {
		var option = $("<option value="+value.cm_syscd+">"+value.cm_sysmsg+"</option>");
	    $('#select_system').append(option);
	});
	
	//SBUxMethod.refresh('select_system');

	setRsrcCbo(); //프로그램 종류 가져오기
	setJobCbo(); //업무 가져오기
	setSrId(); //SR 가져오기
}

function setRsrcCbo(){ //프로그램종류
	var tmpObj = new Object();
	tmpObj.cm_syscd = SBUxMethod.get('select_system');
	tmpObj.SelMsg = 'SEL';
	var tmpArr = {
		Data: 		JSON.stringify(tmpObj),
		requestType: 	'getRsrc'
	}
	
	selectedRsrcData = ajaxCallWithJson('/webPage/dev/ProgRegister', tmpArr, 'json');
	
	SBUxMethod.refresh('select_rsrc');
	SBUxMethod.set('idx_exeName_text', '확장자표시');
	
}

function setJobCbo(){ //업무
	var tmpObj = new Object();
	
	tmpObj.UserId = userId;
	tmpObj.SysCd = SBUxMethod.get('select_system');
	tmpObj.SecuYn = 'Y';
	tmpObj.CloseYn = 'N';
	tmpObj.SelMsg = 'SEL';
	tmpObj.ReqCd = 'NAME';
	/*
	 * 첫번째 아이템 선택시 return
	if($("#select_system option:selected").index() == 0){
		selectedJobData = null;
		SBUxMethod.refresh('select_job');
		return;
	}
	*/
	
	var tmpArr = {
		Data: 		JSON.stringify(tmpObj),
		requestType: 	'JOB_INFO'
	}
	
	selectedJobData = ajaxCallWithJson('/webPage/common/CommonSysInfo', tmpArr, 'json');
	
	
	SBUxMethod.refresh('select_job');
}

function setDir(){ //프로그램경로
	var tmpObj = new Object();
	tmpObj.UserId = userId;
	tmpObj.SysCd = SBUxMethod.get('select_system');
	tmpObj.SecuYn = 'Y';
	tmpObj.RsrcCd = SBUxMethod.get('select_rsrc');
	tmpObj.JobCd = SBUxMethod.get('select_job');
	tmpObj.SelMsg = '';
	
	var tmpArr = {
		Data: 		JSON.stringify(tmpObj),
		requestType: 	'getDir'
	}
	
	selectedDirData = ajaxCallWithJson('/webPage/dev/ProgRegister', tmpArr, 'json');
	console.log(selectedDirData);
	SBUxMethod.refresh('select_dir');
}

function setSrId(){ // SR-ID
	var tmpObj = new Object();
	tmpObj.userid = userId;
	tmpObj.reqcd = "01";
	tmpObj.secuyn = 'Y';
	tmpObj.qrygbn = "01";
	
	var tmpArr = {
		Data: 		JSON.stringify(tmpObj),
		requestType: 	'getSrId'
	}
	selectedSrIdData = ajaxCallWithJson('/webPage/dev/ProgRegister', tmpArr, 'json');
	tmpObj = {"srid":"SR정보 선택 또는 해당없음","reqtitle":"SR정보 선택 또는 해당없음"};
	selectedSrIdData.splice(0,0,tmpObj);
	SBUxMethod.refresh('select_srid');
}

function changeRsrcdCombo(){
	var rsrc_index = $("#select_rsrc option").index($("#select_rsrc option:selected"));
	if(rsrc_index == 0){
		SBUxMethod.set('idx_exeName_text', '확장자표시');
		selectedDirData = null;
		SBUxMethod.refresh('select_dir');
		return;
	}
	else
	{
		var exeName = selectedRsrcData[rsrc_index].cm_exename;
		if(exeName.length>1){
			exeName = exeName.substring(0,exeName.length-1);
		}
		SBUxMethod.set('idx_exeName_text', exeName);
		setDir();
	}
}

function changeSysCombo(){
	setJobCbo();
	setRsrcCbo();
	setSrId();
}

function clickResetBtn(){
	$('#select_rsrc option:eq(0)').prop('selected', true);
	$('#select_job option:eq(0)').prop('selected', true);
	$('#select_srid option:eq(0)').prop('selected', true);
	SBUxMethod.refresh('select_rsrc');
	SBUxMethod.refresh('select_job');
	SBUxMethod.refresh('select_srid');
	changeRsrcdCombo();
}

function clickSearchBtn(){
	if($("#select_srid option").index($("#select_srid option:selected")) < 0){
		alert("시스템을 선택하여 주십시오.");
		return;
	}
	
	var tmpObj = new Object();
	tmpObj.SRid = SBUxMethod.get('select_srid');
	tmpObj.UserId = userId;
	tmpObj.SysCd = SBUxMethod.get('select_system');
	tmpObj.RsrcCd = SBUxMethod.get('select_rsrc');
	tmpObj.RsrcName = $.trim(SBUxMethod.get('idx_prgName_text'));
	tmpObj.SecuSw = "true";
	var tmpArr = {
		Data: 		JSON.stringify(tmpObj),
		requestType: 	'getOpenList'
	}
	GridData = ajaxCallWithJson('/webPage/dev/ProgRegister', tmpArr, 'json');
	console.log(GridData);
	SUBxMethod.set('idx_lbl_total1_text',GridData.length);
	//SBUxMethod.refresh('select_srid');
	
}

