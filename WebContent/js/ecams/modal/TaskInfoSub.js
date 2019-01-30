$(document).ready(function(){
	var tmpObj = window.dialogArguments;
	var jobcd = "";
	var jobname = "";
	
	if(tmpObj != null) {
		jobcd = tmpObj.jobcd;
		jobname= tmpObj.jobname;
	}
	
	SBUxMethod.set('input_jobcd', jobcd);
	SBUxMethod.set('input_jobname', jobname);
});

function btnUpdt_Click() {
	if(SBUxMethod.get('input_jobcd') == '' || SBUxMethod.get('input_jobcd') == undefined) {
		alert('업무코드를 입력해주세요.');
		return;
	}
	
	if(SBUxMethod.get('input_jobname') == '' || SBUxMethod.get('input_jobname') == undefined) {
		alert('업무명을 입력해주세요.');
		return;
	}
	
	var jobData = new Object();
	jobData.jobcd	= SBUxMethod.get('input_jobcd');
	jobData.jobname	= SBUxMethod.get('input_jobname')
	
	var jobInfo = {
		requestType		: 'SETJOBINFOINDIVIDUAL',
		jobInfoData		: JSON.stringify(jobData)
	}
	ajaxReturnData = ajaxCallWithJson('/webPage/modal/TaskInfo', jobInfo, 'json');
	
	
	if( ajaxReturnData !== 'ERR'){}
}

function btnCncl_Click() {
	self.close();
}