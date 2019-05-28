/**
 * 시스템정보 기능 정의
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-05-16
 */

var userName 	= window.top.userName;
var userId 		= window.top.userId;
var adminYN 	= window.top.adminYN;
var userDeptName= window.top.userDeptName;
var userDeptCd 	= window.top.userDeptCd;

var sysInfoGrid		= new ax5.ui.grid();
var jobGrid			= new ax5.ui.grid();
var datStDate 		= new ax5.ui.picker();
var datEdDate 		= new ax5.ui.picker();
var datSysOpen 		= new ax5.ui.picker();
var datScmOpen 		= new ax5.ui.picker();

var cboOptions = [];

var cboSysGbData 	= null;	//	시스템유형콤보
var sysInfodata		= null;	// 	시스템 속성 UL list
var sysInfoCboData	= null;	//	시스템종류 콤보
var cboSvrCdData	= null;	//	기준서버구분콤보
var sysInfoGridData = null; // 	시스템 그리드
var jobGridData 	= null;	//	업무 그리드

sysInfoGrid.setConfig({
    target: $('[data-ax5grid="sysInfoGrid"]'),
    sortable: true, 
    multiSort: true,
    showRowSelector: false,
    header: {
        align: "center",
        columnHeight: 30
    },
    body: {
        columnHeight: 20,
        onClick: function () {
        	this.self.clearSelect();
            this.self.select(this.dindex);
            for(var j=0;j<sysInfoCboData.length; j++) {
            	if(this.item.cm_syscd == sysInfoCboData[j].cm_syscd) {
            		$('[data-ax5select="cboSys"]').ax5select('setValue',sysInfoCboData[j].cm_syscd,true);
            		cboSysClick();
            		break;
            	}
            }
        },
        onDBLClick: function () {
        	doubleClickGrid();
        },
    	trStyleClass: function () {
    		if(this.item.closeSw === 'Y'){
    			return "text-danger";
    		}
    	},
    	onDataChanged: function(){
    		this.self.repaint();
    	}
    },
    columns: [
        {key: "cm_syscd", label: "시스템코드",  width: '10%'},
        {key: "cm_sysmsg", label: "시스템명",  width: '37%'},
        {key: "sysgb", label: "시스템유형",  width: '14%'},
        {key: "servername", label: "기준서버구분",  width: '13%'},
        {key: "scmopen", label: "형상관리오픈",  width: '13%'},
        {key: "sysopen", label: "시스템오픈",  width: '13%'}
    ]
});

jobGrid.setConfig({
    target: $('[data-ax5grid="jobGrid"]'),
    sortable: true, 
    multiSort: true,
    showRowSelector: true,
    header: {
        align: "center",
        columnHeight: 30
    },
    body: {
        columnHeight: 25,
        onClick: function () {
        	this.self.clearSelect();
            this.self.select(this.dindex);
        },
        onDBLClick: function () {
        	doubleClickGrid();
        },
    	trStyleClass: function () {
    		if(this.item.COLORSW === '5'){
    			return "fontStyle-error";
    		} else if (this.item.closeSw === 'Y'){
    			return "text-danger";
    		} else if (this.item.closeSw === 'Y'){
    			return "text-info";
    		} else {
    			return "text-secondary"
    		}
    	},
    	onDataChanged: function(){
    		this.self.repaint();
    	}
    },
    columns: [
        {key: "cm_jobcd", label: "업무코드",  width: '20%'},
        {key: "cm_jobname", label: "업무명",  width: '80%'},
    ]
});

function dateInit() {
	$('#datStDate').val(getDate('DATE',0));
	$('#datEdDate').val(getDate('DATE',0));
	$('#datSysOpen').val(getDate('DATE',0));
	$('#datScmOpen').val(getDate('DATE',0));
	
	datStDate.bind(defaultPickerInfo('datStDate'));
	datEdDate.bind(defaultPickerInfo('datEdDate'));
	datSysOpen.bind(defaultPickerInfo('datSysOpen'));
	datScmOpen.bind(defaultPickerInfo('datScmOpen'));
}



$(document).ready(function(){
	$('input.checkbox-pie').wCheck({theme: 'square-inset blue', selector: 'square-dot-blue', highlightLabel: true});
	
	dateInit();
	getSysCodeInfo();
	getSysInfoCbo();
	getSysInfoList();
	screenInit();
	
	
	$('#btnQry').bind('click', function() {
		var strDay = getDate('DATE',0);
		strDay = strDay.substr(0,4) + '/' + strDay.substr(4,2) + '/' + strDay.substr(6,2);
		
		$('#txtTime').val('');
		$('#txtSysCd').val('');
		$('#txtSysMsg').val('');
		$('#txtPrcCnt').val('');
		$('#datStDate').val(strDay);
		$('#datEdDate').val(strDay);
		$('#datSysOpen').val(strDay);
		$('#datScmOpen').val(strDay);
		$('#txtPrjName').val('');
		$('#txtTime').val('');
		
		$('[data-ax5select="cboSys"]').ax5select('setValue',sysInfoCboData[0].cm_syscd,true);
		$('[data-ax5select="cboSysGb"]').ax5select('setValue',cboSysGbData[0].cm_micode,true);
		$('[data-ax5select="cboSvrCd"]').ax5select('setValue',cboSvrCdData[0].cm_micode,true);
		
		for(var i=0; i<sysInfoData.length; i++) {
			$('#chkJobName'+ (i+1) ).parent().removeClass('wCheck-on');
			$('#chkJobName'+ (i+1) ).parent().addClass('wCheck-off');
		}
		
		jobGrid.clearSelect();
		
		$('#datStDate').prop( "disabled", 	true );
		$('#timeDeploy').prop( "disabled", 	true );
		$('#datEdDate').prop( "disabled", 	true );
		$('#timeDeployE').prop( "disabled", true );
		$('#txtPrjName').prop( "disabled", true );
		$('#txtTime').prop( "disabled", true );
		
		getSysInfoList();
	});
	
	$('#chkOpen').bind('click', function() {
		if($(this).is(':checked')) {
			screenInit();
			$('#chkSelfDiv').css('visibility','visible');
		}
		
		if( !($(this).is(':checked')) ) {
			screenInit();
			$('#chkSelfDiv').css('visibility','hidden');
		}
	});
	
	$('input.checkbox-sysInfo').bind('click', function(e) {
		var selectedSysInfo = Number(this.value); 
		var selectedIndexs = sysInfoGrid.selectedDataIndexs;
		if(selectedIndexs.length == 0 && !($('#chkOpen').is(':checked')) ) {
			e.preventDefault();
		    e.stopPropagation();
			showToast('그리드를 선택후 속성을 선택 하실 수 있습니다.');
			return;
		}
		
		if( !(selectedSysInfo === 4 
				|| selectedSysInfo === 6
				|| selectedSysInfo === 13) ) {
			return;
		}
		
		if(selectedSysInfo === 4 && $(this).is(':checked')) {
			$('#datStDate').prop( "disabled",   false );
			$('#timeDeploy').prop( "disabled", 	false );
			$('#datEdDate').prop( "disabled", 	false );
			$('#timeDeployE').prop( "disabled", false );
		}
		
		if(selectedSysInfo === 4 && !($(this).is(':checked')) ) {
			$('#datStDate').prop( "disabled", 	true );
			$('#timeDeploy').prop( "disabled", 	true );
			$('#datEdDate').prop( "disabled", 	true );
			$('#timeDeployE').prop( "disabled", true );
		}
		
		if(selectedSysInfo === 6 && $(this).is(':checked') ) {
			$('#txtTime').prop( "disabled", false );
		}
		
		if(selectedSysInfo === 6 && !($(this).is(':checked')) ) {
			$('#txtTime').prop( "disabled", true );
		}
		
		if(selectedSysInfo === 13 && $(this).is(':checked') ) {
			$('#txtPrjName').prop( "disabled", false );
		}
		
		if(selectedSysInfo === 13 && !($(this).is(':checked')) ) {
			$('#txtPrjName').prop( "disabled", true );
		}
	});
	
	$('#btnFact').bind('click',function() {
		var factUpInfoData;
		factUpInfoData = new Object();
		factUpInfoData = {
			requestType	: 	'FACTUP'
		}
		ajaxAsync('/webPage/administrator/SysInfoServlet', factUpInfoData, 'json',successFactUpdt);
	});
	
	$('#btnDel').bind('click',function() {
		var gridSelectedIndex 	= sysInfoGrid.selectedDataIndexs;
		var selectedGridItem 	= null;
		
		if(gridSelectedIndex.length === 0 ) {
			dialog.alert('폐기 할 시스템을 그리드에서 선택 후 눌러주세요.',function(){});
			return;
		}
		
		selectedGridItem = sysInfoGrid.list[gridSelectedIndex];
		
		confirmDialog.confirm({
			msg: '시스템정보를 폐기처리하시겠습니까',
		}, function(){
			if(this.key === 'ok') {
				var sysInfo = new Object();
				sysInfo.SysCd = selectedGridItem.cm_syscd;
				sysInfo.UserId = userId;
				
				var sysInfoData;
				sysInfoData = new Object();
				sysInfoData = {
					sysInfo		: sysInfo,
					requestType	: 'CLOSESYS'
				}
				ajaxAsync('/webPage/administrator/SysInfoServlet', sysInfoData, 'json',successSysClose);
			}
		});
	});
	
	$('#btnAdd').bind('click',function() {
		sysValidationCheck();
	});

});

function sysValidationCheck() {
	var gridSelectedIndex 	= sysInfoGrid.selectedDataIndexs;
	var stDate = null;
	var edDate = null;
	var nowDate = null;
	var stTime = null;
	var edTime = null;
	var nowTime = null;
	var stFullDate = null;
	var edFullDate = null;
	var nowFullDate = null;
	
	if( $('#chkOpen').is(':checked') && $('#chkSelf').is(':checked') && $('#txtSysCd').val().length === 0) {
		dialog.alert('시스템코드를 입력하여 주시기 바랍니다.',function(){});
		return;
	}
	
	if($('#chkOpen').is(':checked') && !($('#chkSelf').is(':checked'))) {
		$('#txtSysCd').val('');
	}
	
	if( $('#txtSysMsg').val().length === 0 ) {
		dialog.alert('시스템명을 입력하여 주시기 바랍니다.',function(){});
		return;
	}
	
	
	if( !($('#chkOpen').is(':checked')) && $('#txtSysCd').val().length === 0 ) {
		dialog.alert('수정할 시스템을 선택하여 주시기 바랍니다.',function(){});
		return;
	}
	
	if( $('#txtPrcCnt').val().length === 0 ) {
		dialog.alert('프로세스제한갯수를 입력하여 주시기 바랍니다.',function(){});
		return;
	}
	
	if( $('#datSysOpen').val().length === 0 ) {
		dialog.alert('시스템오픈일를 선택하여 주시기 바랍니다.',function(){});
		return;
	}
	
	if( $('#datScmOpen').val().length === 0 ) {
		dialog.alert('형상관리오픈일를 선택하여 주시기 바랍니다.',function(){});
		return;
	}
	
	for(var i=0; i<sysInfoData.length; i++) {
		if(i === 3 && $('#chkJobName'+ (i+1) ).is(':checked') ) {
			
			stDate = $('#datStDate').val().substr(0,4) + $('#datStDate').val().substr(5,2) + $('#datStDate').val().substr(8,2);
			edDate = $('#datEdDate').val().substr(0,4) + $('#datEdDate').val().substr(5,2) + $('#datEdDate').val().substr(8,2);
			nowDate = getDate('DATE',0);
			stTime = $('#timeDeploy').val().substr(0,2) + $('#timeDeploy').val().substr(3,2);
			edTime = $('#timeDeployE').val().substr(0,2) + $('#timeDeployE').val().substr(3,2);
			console.log($('#timeDeploy').val());
			console.log($('#timeDeployE').val());
			nowTime = getTime();
			
			stFullDate = stDate + stTime;
			edFullDate = edDate + edTime;
			nowFullDate = nowDate + getTime();
			
			if(stDate.length === 0 || stTime.length === 0 
					|| edDate.length === 0 || edTime.length === 0) {
				dialog.alert('중단일시 및 시간을 입력해 주시기 바랍니다.',function(){});
				return;
			}
			
			console.log('stDate : '+stDate);
			console.log('edDate : '+edDate);
			console.log('nowDate : '+nowDate);
			
			console.log('stTime : ' + stTime );
			console.log('edTime : ' + edTime );
			console.log('nowTime : ' + nowTime);
			
			console.log('stFullDate : '+stFullDate);
			console.log('edFullDate : '+edFullDate);
			console.log('nowFullDate : '+nowFullDate);
			
			
			if( nowFullDate > stFullDate) {
				dialog.alert('중단시작일시가 현재일 이전입니다. 정확히 선택하여 주십시오.',function(){});
				return;
			}
			
			if( stFullDate > edFullDate ) {
				dialog.alert('중단종료시작일시가 중단시작일시 이전입니다. 정확히 선택하여 주십시오',function(){});
				return;
			}
			
			if( nowFullDate > edFullDate  ) {
				dialog.alert('중단종료시작일시가 현재일 이전입니다. 정확히 선택하여 주십시오',function(){});
				return;
			}
		}
		
		if(i === 5 && $('#chkJobName'+ (i+1) ).is(':checked') && $('#txtTime').val().length === 0 ) {
			dialog.alert('정기적용시간을 입력하여 주십시오.'
					,function(){
						$('#txtTime').focus();
					});
			return;
		}
		
		if(i === 12 && $('#chkJobName'+ (i+1) ).is(':checked') && $('#txtPrjName').val().length === 0 ) {
			dialog.alert('프로젝트 명을 입력하시기 바랍니다.'
							,function(){
								$('#txtPrjName').focus();
							});
			return;
		}
	}
	
}

function successSysClose(data) {
	if(data === 'OK')	dialog.alert('시스템정보 폐기처리가 완료되었습니다.'
										,function(){
											var gridSelectedIndex 	= sysInfoGrid.selectedDataIndexs;
											var selectedGridItem 	= sysInfoGrid.list[gridSelectedIndex];;
											var tmpGridObj = new Object();
											tmpGridObj = selectedGridItem;
											tmpGridObj.closeSw = 'Y';
											sysInfoGridData.splice(gridSelectedIndex[0],1,tmpGridObj);
											sysInfoGrid.setData(sysInfoGridData);
											
									});
	if(data !== 'OK')	dialog.alert('시스템정보 폐기중 오류가 발생했습니다. 관리자에게 문의하세요.',function(){});
}

//	화면초기화
function screenInit() {
	$('#chkSelfDiv').css('visibility','hidden');
	
	$('#datStDate').prop( "disabled", 	true );
	$('#timeDeploy').prop( "disabled", 	true );
	$('#datEdDate').prop( "disabled", 	true );
	$('#timeDeployE').prop( "disabled", true );
	$('#txtPrjName').prop( "disabled", true );
	$('#txtTime').prop( "disabled", true );
	
}

//	처리팩터 추가
function successFactUpdt(data) {
	if(data === 'OK') dialog.alert('시스템속성/서버속성/프로그램종류속성 자릿수를 일치시켰습니다.',function(){});
	if(data !== 'OK') dialog.alert('처리팩터추가중 오류가 발생했습니다. 관리자에게 문의하세요.',function(){});
}



// 시스템 리스트
function getSysInfoList() {
	var sysClsSw = $('#chkCls').is(':checked');
	
	var sysListInfo;
	var sysListInfoData;
	sysListInfo 		= new Object();
	sysListInfo.clsSw 	= sysClsSw;
	sysListInfo.SysCd 	= null;
	
	sysListInfoData = new Object();
	sysListInfoData = {
		sysInfo	: 	sysListInfo,
		requestType	: 	'GETSYSINFOLIST'
	}
	console.log(sysListInfo);
	
	ajaxAsync('/webPage/administrator/SysInfoServlet', sysListInfoData, 'json',successGetSysInfoList);
}

//시스템 리스트
function successGetSysInfoList(data) {
	sysInfoGridData = data;
	sysInfoGrid.setData(sysInfoGridData);
	
	getJobList();
}

// 업무 그리드
function getJobList() {
	var jobInfoCbo;
	var jobInfoCboData;
	jobInfoCbo 			= new Object();
	jobInfoCbo.SelMsg 	= null;
	jobInfoCbo.closeYn 	= 'N';
	
	jobInfoCboData = new Object();
	jobInfoCboData = {
		jobInfoCbo	: 	jobInfoCbo,
		requestType	: 	'GETJOBLIST'
	}
	ajaxAsync('/webPage/administrator/SysInfoServlet', jobInfoCboData, 'json',successGetJobList);
}

// 업무 그리드
function successGetJobList(data) {
	jobGridData = data;
	jobGrid.setData(jobGridData);
}

// 시스템 CBO GET
function getSysInfoCbo() {
	var sysInfoCbo;
	var sysInfoCboData;
	sysInfoCbo 			= new Object();
	sysInfoCbo.UserId 	= userId;
	sysInfoCbo.SelMsg 	= 'SEL';
	sysInfoCbo.CloseYn 	= 'Y';
	sysInfoCbo.SysCd 	= null;
	
	sysInfoCboData = new Object();
	sysInfoCboData = {
		sysInfoCbo	: 	sysInfoCbo,
		requestType	: 	'GETSYSINFOCBO'
	}
	ajaxAsync('/webPage/administrator/SysInfoServlet', sysInfoCboData, 'json',successGetSysInfoCbo);
}

// 시스템 CBO
function successGetSysInfoCbo(data) {
	sysInfoCboData = data;
	cboOptions = [];

	$.each(sysInfoCboData,function(key,value) {
		cboOptions.push({value: value.cm_syscd, text: value.cm_sysmsg, cm_sysgb: value.cm_sysgb, cm_sysinfo: value.cm_sysinfo, cm_prjname: value.cm_prjname});
	});
	$('[data-ax5select="cboSys"]').ax5select({
        options: cboOptions
	});
}

//	하단 시스템 콤보 선택
function cboSysClick() {
	var selectedIndex = $('#cboSys option').index($('#cboSys option:selected'));
	var findSw = false;
	var gridSelectedIndex = sysInfoGrid.selectedDataIndexs;
	var selectedSysCboSysInfo = $('[data-ax5select="cboSys"]').ax5select("getValue");
	selectedSysCboSysInfo = selectedSysCboSysInfo[0];
	var selectedGridItem = sysInfoGrid.list[gridSelectedIndex];
	var sysInfoStr		 = null;
	if(selectedIndex < 1) return;
	
	if(gridSelectedIndex[0] < 0 || gridSelectedIndex[0] == undefined) findSw = true;
	else if( selectedGridItem != undefined &&  (selectedSysCboSysInfo.value != selectedGridItem.cm_syscd) ) findSw = true;
	
	if(findSw) {
		for(var i=0; i<sysInfoGridData.length; i++) {
			if(sysInfoGridData[i].cm_syscd == selectedSysCboSysInfo.value) {
				sysInfoGrid.clearSelect();
				sysInfoGrid.select(i);
				selectedGridItem = sysInfoGrid.list[i];
				break;
			}
		}
	}
	
	
	$('#txtTime').val('');
	$('#txtSysCd').val(selectedSysCboSysInfo.value);
	$('#txtSysMsg').val(selectedSysCboSysInfo.text);
	
	for(var i=0; i<cboSysGbData.length; i++) {
		if(cboSysGbData[i].cm_micode == selectedSysCboSysInfo.cm_sysgb) {
			$('[data-ax5select="cboSysGb"]').ax5select('setValue',selectedSysCboSysInfo.cm_sysgb,true);
			break;
		}
	}
	
	for(var i=0; i<sysInfoData.length; i++) {
		$('#chkJobName'+ (i+1) ).parent().removeClass('wCheck-on');
		$('#chkJobName'+ (i+1) ).parent().addClass('wCheck-off');
	}
	
	if(selectedSysCboSysInfo.cm_sysinfo !== undefined && selectedSysCboSysInfo.cm_sysinfo !== null) {
		sysInfoStr = selectedSysCboSysInfo.cm_sysinfo;
		for(var i=0; i<sysInfoStr.length; i++) {
			if(sysInfoStr.substr(i,1) === '1') {
				$('#chkJobName'+ (i+1) ).parent().removeClass('wCheck-off');
				$('#chkJobName'+ (i+1) ).parent().addClass('wCheck-on');
			}
		}
	}
	
	jobGrid.clearSelect();
	
	if(selectedGridItem !== undefined) {
		$('#txtPrcCnt').val(selectedGridItem.cm_prccnt);
		$('#datSysOpen').val(selectedGridItem.sysopen);
		$('#datScmOpen').val(selectedGridItem.scmopen);
		
		for(var i=0; i<cboSvrCdData.length; i++) {
			if(cboSvrCdData[i].cm_micode == selectedGridItem.cm_dirbase) {
				$('[data-ax5select="cboSvrCd"]').ax5select('setValue',selectedGridItem.cm_dirbase,true);
				break;
			}
		}
		
		if(sysInfoStr.substr(3,1) === '1' &&  selectedGridItem.hasOwnProperty('cm_stdate') && selectedGridItem.hasOwnProperty('cm_eddate')) {
			$('#datStDate').prop( "disabled", 	false );
			$('#timeDeploy').prop( "disabled", 	false );
			$('#datEdDate').prop( "disabled", 	false );
			$('#timeDeployE').prop( "disabled", false );
			
			var stDate = selectedGridItem.cm_stdate;
			var edDate = selectedGridItem.cm_eddate;
			
			var strTime = stDate.substr(0,4) + '/' + stDate.substr(4,2) + '/' + stDate.substr(6,2);
			$('#datStDate').val(strTime);
			
			strTime = stDate.substr(8,2) + ':' + stDate.substr(10,2);
			$('#timeDeploy').val(strTime);
			
			strTime = edDate.substr(0,4) + '/' + edDate.substr(4,2) + '/' + edDate.substr(6,2);
			$('#datEdDate').val(strTime);
			
			strTime = edDate.substr(8,2) + ':' + edDate.substr(10,2);
			$('#timeDeployE').val(strTime);
			
		} else {
			$('#datStDate').prop( "disabled", true );
			$('#timeDeploy').prop( "disabled", true );
			$('#datEdDate').prop( "disabled", true );
			$('#timeDeployE').prop( "disabled", true );
		}
		
		if(sysInfoStr.substr(5,1) === '1' && selectedGridItem.hasOwnProperty('cm_systime') ) {
			$('#txtTime').val(selectedGridItem.cm_systime);
			$('#txtTime').prop( "disabled", true );
		}
		
		if(sysInfoStr.substr(12,1) === '1' && selectedGridItem.hasOwnProperty('cm_prjname')) {
			$('#txtPrjName').val(selectedGridItem.cm_prjname);
			$('#txtPrjName').prop( "disabled", false );
		} else {
			$('#txtPrjName').val('');
			$('#txtPrjName').prop( "disabled", true );
		}
		
	}
	
	getSysJobInfo(selectedSysCboSysInfo.value);
}

//	선택된 시스템 JOB
function getSysJobInfo(sysCd) {
	var sysJobInfo;
	var sysJobInfoData;
	sysJobInfo 			= new Object();
	sysJobInfo.UserID 	= userId;
	sysJobInfo.SysCd 	= sysCd;
	sysJobInfo.SecuYn 	= 'N';
	sysJobInfo.CloseYn 	= 'N';
	sysJobInfo.SelMsg 	= '';
	sysJobInfo.sortCd 	= 'CD';
	
	sysJobInfoData = new Object();
	sysJobInfoData = {
		sysJobInfo	: sysJobInfo,
		requestType	: 'GETJOBINFO'
	}
	ajaxAsync('/webPage/administrator/SysInfoServlet', sysJobInfoData, 'json',successGetSysJobInfo);
}

//	선택된 시스템 JOB
function successGetSysJobInfo(data) {
	var checkedIndex = -1;
	for(var i=0; i<data.length; i++) {
		for(var j=0; j<jobGridData.length; j++) {
			if(data[i].cm_jobcd == jobGridData[j].cm_jobcd) {
				var jobObj = {};
				jobObj = data[i];
				jobGridData.splice(j,1);
				jobGridData.splice(0,0,jobObj);
				checkedIndex++;
				break;
			}
		}
	}
	jobGrid.setData(jobGridData);
	if(checkedIndex > -1) {
		for(var i=0; i<=checkedIndex; i++) {
			jobGrid.select(i);
		}
	}
}

// 시스템 유형 CBO
// 기준 서버구분 CBO
function getSysCodeInfo() {
	var codeInfos = getCodeInfoCommon([
										new CodeInfo('SYSGB','','N'),
										new CodeInfo('SYSINFO','','N'), 
										new CodeInfo('SERVERCD','','N')
										]);
	cboSysGbData 	= codeInfos.SYSGB;
	cboSvrCdData	= codeInfos.SERVERCD;
	sysInfoData 	= codeInfos.SYSINFO;
	
	cboOptions = [];
	$.each(cboSysGbData,function(key,value) {
		cboOptions.push({value: value.cm_micode, text: value.cm_codename});
	});
	$('[data-ax5select="cboSysGb"]').ax5select({
        options: cboOptions
	});
	
	cboOptions = [];
	$.each(cboSvrCdData,function(key,value) {
		cboOptions.push({value: value.cm_micode, text: value.cm_codename});
	});
	$('[data-ax5select="cboSvrCd"]').ax5select({
        options: cboOptions
	});
	
	makeSysInfoUlList();
}

// 시스템 속성 ul 만들어주기
function makeSysInfoUlList() {
	$('#ulSysInfo').empty();
	var liStr = null;
	var addId = null;
	sysInfoData.forEach(function(sysInfoItem, sysInfoIndex) {
		addId = Number(sysInfoItem.cm_micode);
		liStr  = '';
		liStr += '<li class="list-group-item">';
		liStr += '	<input type="checkbox" class="checkbox-sysInfo" id="chkJobName'+addId+'" data-label="'+sysInfoItem.cm_codename+'"  value="'+sysInfoItem.cm_micode+'" />';
		liStr += '</li>';
		$('#ulSysInfo').append(liStr);
	});
	
	$('input.checkbox-sysInfo').wCheck({theme: 'square-inset blue', selector: 'square-dot-blue', highlightLabel: true});
}




