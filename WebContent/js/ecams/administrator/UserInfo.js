/**
 * 사용자정보 화면의 기능 정의
 * 
 * 	작성자: 정선희
 * 	버전 : 1.0
 *  수정일 : 2019-05-27
 * 
 */
var userId = window.parent.userId;

var userGrid		= new ax5.ui.grid();
var jobGrid			= new ax5.ui.grid();

var cboOptions = [];

var userGridData 	= null;	//	사용자 그리드
var jobGridData 	= null;	//	업무 그리드
var cboPositionData = null;	//	직급
var cboDutyData		= null;	// 	직위
var rgtDataData	    = null;	//	직무 UL List

$(document).ready(function(){
	$('input.checkbox-pie').wCheck({theme: 'square-inset blue', selector: 'square-dot-blue', highlightLabel: true});
	
	getUsrCodeInfo();
});

//직위, 직급, 직무
function getUsrCodeInfo() {
	var codeInfos = getCodeInfoCommon([ new CodeInfo('RGTCD','','N'),
										new CodeInfo('POSITION','','N'), 
										new CodeInfo('DUTY','','N') ]);
	cboPositionData = codeInfos.POSITION;
	cboDutyData	    = codeInfos.DUTY;
	rgtDataData 	= codeInfos.RGTCD;
	
	cboOptions = [];
	cboOptions.push({value: '00', text: '선택하세요'});
	$.each(cboPositionData,function(key,value) {
		cboOptions.push({value: value.cm_micode, text: value.cm_codename});
	});
	$('[data-ax5select="cboPosition"]').ax5select({
        options: cboOptions
	});
	
	cboOptions = [];
	cboOptions.push({value: '00', text: '선택하세요'});
	$.each(cboDutyData,function(key,value) {
		cboOptions.push({value: value.cm_micode, text: value.cm_codename});
	});
	$('[data-ax5select="cboDuty"]').ax5select({
        options: cboOptions
	});
	
	getSysInfoList();
	makeRgtUlList();
}

//직무 ul 만들어주기
function makeRgtUlList() {
	$('#ulRgt').empty();
	var liStr = null;
	var addId = null;
	rgtDataData.forEach(function(rgtItem, rgtIndex) {
		addId = Number(rgtItem.cm_micode);
		liStr  = '';
		liStr += '<li class="list-group-item">';
		liStr += '	<input type="checkbox" class="checkbox-rgt" id="chkRgtName'+addId+'" data-label="'+rgtItem.cm_codename+'" value="'+rgtItem.cm_micode+'" />';
		liStr += '</li>';
		$('#ulRgt').append(liStr);
	});
	
	//$('input.checkbox-rgt').wCheck({theme: 'square-inset blue', selector: 'square-dot-blue', highlightLabel: true});
}

//시스템 리스트
function getSysInfoList() {
	var sysListInfo;
	var sysListInfoData;
	sysListInfo 		= new Object();
	sysListInfo.clsSw 	= false;
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
	cboOptions = [];
	cboOptions.push({value: "00", text: "선택하세요"});
	$.each(data,function(key,value) {
		cboOptions.push({value: value.cm_syscd, text: value.cm_sysmsg});
	});
	$('[data-ax5select="cboSys"]').ax5select({
        options: cboOptions
	});
}
//시스템선택->업무조회
function cboSysChange() {
	$('#ulJob').empty();
	
	var selectedIndex = $('#cboSys option').index($('#cboSys option:selected'));
	if(selectedIndex < 1) return;
	
	var selectedSysCboSysInfo = $('[data-ax5select="cboSys"]').ax5select("getValue");
	selectedSysCboSysInfo = selectedSysCboSysInfo[0];
	
	getSysJobInfo(selectedSysCboSysInfo.value);
}
//선택된 시스템 JOB
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
	$('#ulJob').empty();
	var liStr = null;
	var addId = null;
	data.forEach(function(jobInfoItem, jobInfoIndex) {
		addId = Number(jobInfoItem.cm_jobcd);
		liStr  = '';
		liStr += '<li class="list-group-item">';
		liStr += '	<input type="checkbox" class="checkbox-job" id="chkJobName'+addId+'" data-label="'+jobInfoItem.cm_jobname+'" value="'+jobInfoItem.cm_jobcd+'" />';
		liStr += '</li>';
		$('#ulJob').append(liStr);
	});
	
	$('input.checkbox-job').wCheck({theme: 'square-inset blue', selector: 'square-dot-blue', highlightLabel: true});
}

//사원번호, 성명 입력 후 엔터
function getUserInfo(gbn) {
	
	var userId   = null;
	var userName = null;
	
	if (gbn == 0) { //사번으로 조회
		$('#txtUserId').val($.trim(document.getElementById("txtUserId").value));
		userId   = document.getElementById("txtUserId").value;
		userName = "";
	} else { //성명으로 조회
		$('#txtUserName').val($.trim(document.getElementById("txtUserName").value));
		userId   = "";
		userName = document.getElementById("txtUserName").value;
	}
	
	var tmpData;
	tmpData = new Object();
	tmpData = {
		requestType : 'Cmm0400',
		userId : userId,
		userName : userName
	}
	ajaxAsync('/webPage/administrator/UserInfo', tmpData, 'json',successGetUserInfo);
	
}

function successGetUserInfo(data) {
	console.log(data);
	
	var userListData = null;
	
	userListData = data;
	
	console.log('>>>>>>userid:'+userListData[0].cm_userid);
	
	if(userListData[0].ID == "ERROR"){
		alert("등록되지 않은 사용자입니다.");
		return;
	}
	
	$("#txtUserId").val(userListData[0].cm_userid);
	$("#txtUserName").val(userListData[0].cm_username);
	$("#txtTelNo1").val(userListData[0].cm_telno1);
	$("#txtTelNo2").val(userListData[0].cm_telno2);
	$("#txtLoginDt").val(userListData[0].cm_logindt);
	$("#txtErrCnt").val(userListData[0].cm_ercount);
	$("#txtEmail").val(userListData[0].cm_email);

	var i;
	for(var i=0; i<cboPositionData.length; i++) {
		if(cboPositionData[i].cm_micode == userListData[0].cm_position) {
			$('[data-ax5select="cboPosition"]').ax5select('setValue',userListData[0].cm_position,true);
			break;
		}
	}
	for(var i=0; i<cboDutyData.length; i++) {
		if(cboDutyData[i].cm_micode == userListData[0].cm_duty){
			$('[data-ax5select="cboDuty"]').ax5select('setValue',userListData[0].cm_duty,true);
			break;
		}
	}
	
	$("#txtDept").val(userListData[0].deptname1);
//		document.getElementById("lbGroup1").innerText = userListData[0].cm_project;
	$("#txtSubDept").val(userListData[0].deptname2);
//		document.getElementById("lbOrg1").innerText = userListData[0].cm_project2;
	
	if(userListData[0].cm_manid == "Y"){
		$('#rdoMan0').attr("checked",true);
	} else {
		$('#rdoMan1').attr("checked",true);
	}
	
	if(userListData[0].cm_admin == "1"){
		$('#chkAdmin').parent().removeClass('wCheck-off');
		$('#chkAdmin').parent().addClass('wCheck-on');
	} else {
		$('#chkAdmin').parent().removeClass('wCheck-on');
		$('#chkAdmin').parent().addClass('wCheck-off');
	}
	
	if(userListData[0].cm_handrun == "Y"){
		$('#chkSync').parent().removeClass('wCheck-off');
		$('#chkSync').parent().addClass('wCheck-on');
	} else {
		$('#chkSync').parent().removeClass('wCheck-on');
		$('#chkSync').parent().addClass('wCheck-off');
	}
	
	$("#txtIpAddr").val(userListData[0].cm_ipaddress);
	$("#txtDaegyul").val(userListData[0].Txt_DaeGyul);
	$("#txtDaegyulDt").val(userListData[0].Txt_BlankTerm);
	$("#txtDaegyulSayu").val(userListData[0].Txt_BlankSayu);
	
	if(userListData[0].cm_active == "1"){
		$('#rdoActive0').attr("checked",true);
	} else {
		$('#rdoActive1').attr("checked",true);
	}
	
}

//사용자결과조회
userGrid.setConfig({
    target: $('[data-ax5grid="userGrid"]'),
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
    	onDataChanged: function(){
    		this.self.repaint();
    	}
    },
    columns: [
        {key: "cm_userid", label: "사번",  width: '20%'},
        {key: "cm_username", label: "성명",  width: '40%'},
        {key: "cm_deptname", label: "부서",  width: '40%'}
    ]
});

//등록된 업무조회
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
    	onDataChanged: function(){
    		this.self.repaint();
    	}
    },
    columns: [
        {key: "cm_sysmsg", label: "시스템",  width: '20%'},
        {key: "cm_jobname", label: "업무명(업무코드)",  width: '80%'},
    ]
});

