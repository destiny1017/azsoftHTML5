/**
 * 사용자정보 화면의 기능 정의
 * 
 * <pre>
 * &lt;b&gt;History&lt;/b&gt;
 * 	작성자: 홍소미
 * 	버전 : 1.0
 *  수정일 : 2019-02-13
 * 
 */
var userid = window.parent.userId;
var selPosData;
var selDutyData;
var listDutyData;
var listJobData;

$(document).ready(function() { //완료
	if(userid == "" || userid == null) {
		alert("로그인 후 사용하시기 바랍니다.");
		return;
	}
	getCodeInfo();
	getSysInfo();
	SBUxMethod.hide('lbGroup1');
	SBUxMethod.hide('lbOrg1');
})

//function createUserGrid() {
//	var userGridProperties = {};
//	userGridProperties.parentid = "divUserList";
//	userGridProperties.id = "userGrid";
//	userGridProperties.jsonref = "userListData";
//	userGridProperties.columns = [ {
//		caption : [ '사번' ],
//		ref : 'cm_userid',
//		width : '16%',
//		style : 'text-align:left',
//		type : 'output'
//	}, {
//		caption : [ '성명' ],
//		ref : 'cm_username',
//		width : '14%',
//		style : 'text-align:center',
//		type : 'output'
//	}, {
//		caption : [ '부서' ],
//		ref : 'deptname1',
//		width : '70%',
//		style : 'text-align:center',
//		type : 'output'
//	} ];
//	userGridProperties.allowuserresize = true;
//	userGridProperties.width = "100%";
//	userGridProperties.height = "100%";
//	userGrid = _SBGrid.create(userGridProperties);
//	userGrid.rebuild();
//}

function createJobGrid() {
	var userGridProperties = {};
	jobGridProperties.parentid = "divJobCharged";
	jobGridProperties.id = "jobGrid";
	jobGridProperties.jsonref = "jobListData";
	jobGridProperties.columns = [ {
		caption : [ '시스템' ],
		ref : 'jobgrp',
		width : '16%',
		style : 'text-align:left',
		type : 'output'
	}, {
		caption : [ '업무명(업무코드)' ],
		ref : 'job',
		width : '14%',
		style : 'text-align:center',
		type : 'output'
	} ];
	jobGridProperties.allowuserresize = true;
	jobGridProperties.width = "100%";
	jobGridProperties.height = "100%";
	jobGrid = _SBGrid.create(jobGridProperties);
	jobGrid.rebuild();
}

function getCodeInfo() {
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'CodeInfo',
			UserId : userid
	}
	ajaxReturnData = ajaxCallWithJson('/webPage/administrator/UserInfo', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		
		selPosData = ajaxReturnData;
		selPosData = selPosData.filter(function(ajaxReturnData){
			return ajaxReturnData.cm_macode === "POSITION";
		});
		SBUxMethod.refresh('selPos');
		
		selDutyData = ajaxReturnData;
		selDutyData = selDutyData.filter(function(ajaxReturnData){
			return ajaxReturnData.cm_macode === "DUTY";
		});
		SBUxMethod.refresh('selDuty');
		
		listDutyData = ajaxReturnData;
		listDutyData = listDutyData.filter(function(ajaxReturnData){
			return ajaxReturnData.cm_macode === "RGTCD" && ajaxReturnData.cm_micode !== "00";
		});
		SBUxMethod.refresh('listDuty');
		
	}
}

function getSysInfo() { //완료 getSysInfo_Handler
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'SysInfo',
			UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/administrator/UserInfo', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		selSystemData = ajaxReturnData;
		SBUxMethod.refresh('selSystem');
	}
}

function getJobInfo() { //getJobInfo_Handler
	$('#chkNotice').attr("checked",false);
	var cm_syscd = SBUxMethod.get('selSystem');
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'SysInfo_1',
			UserId : userid,
			sysCd : cm_syscd
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/administrator/UserInfo', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		selSystemData = ajaxReturnData;
		SBUxMethod.refresh('selSystem');
		selSystemData = selSystemData.filter(function(ajaxReturnData){
			return selSystemData.cm_jobcd != "";
		});
		console.log(selSystemData);
		SBUxMethod.refresh('selSystem');
	}
	listJobData;
	cm_syscd = "";
}

function clickUserList() { //userList_ITEM_CLICK
	//var selectedIndex =userGrid.getRow();
	//reset(userGrid.getRowData(selectedIndex, false).cm_userid,""); //Sql_Qry
}

function getUserInfo(userId, userName) { //getUserInfo_Handler
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'Cmm0400',
			userId : userId,
			userName : userName
	}
	ajaxReturnData = ajaxCallWithJson('/webPage/administrator/UserInfo', tmpData, 'json');
	if(ajaxReturnData !== 'ERR') {
		userListData = ajaxReturnData;
		if(userListData(0).ID == "ERROR"){
			alert("등록되지 않은 사용자입니다.");
			return;
		}
		if(userListData.length > 1){
			userGrid.bind('click', 'clickUserGrid');
		} else {
			userGrid.bind('click');
			userListData = null;
		}
		$("#txtId").val(userListData(0).cm_userid);
		$("#txtName").val(userListData(0).cm_username);
		$("#txtPhone").val(userListData(0).cm_telno1);
		$("#txtPhone2").val(userListData(0).cm_telno2);
		$("#txtLastIn").val(userListData(0).cm_logindt);
		$("#txtPass").val(userListData(0).cm_ercount);
		$("#txtEmail").val(userListData(0).cm_email);
		
		var i;
		var dataLen = selPosData.length;
		for(i=0; i < dataLen; i++){
			if(selPosData(i).cm_micode == userListData(0).cm_position){
				$("#selPos option:eq("+i+")").prop("selected", true);
				break;
			}
		}
		dataLen = selDutyData.length;
		for(i=0; i<dataLen; i++){
			if(selDutyData(i).cm_micode == userListData(0).cm_duty){
				$("#selDuty option:eq("+i+")").prop("selected", true);
				break;
			}
		}
		$("#txtGroup").val(userListData(0).deptname1);
		document.getElementById("lbGroup1").innerText = userListData(0).cm_project;
		$("#txtOrg").val(userListData(0).deptname2);
		document.getElementById("lbOrg1").innerText = userListData(0).cm_project2;
		
		if(userListData(0).cm_manid == "Y"){
			$('#rdoOpt0').attr("checked",true);
		} else {
			$('#rdoOpt1').attr("checked",true);
		}
		
		if(userListData(0).cm_admin == "1"){
			$('#chkSysAdmin').attr("checked",true);
		} else {
			$('#chkSysAdmin').attr("checked",false);
		}
		
		if(userListData(0).cm_handrun == "Y"){
			$('#chkAsynchro').attr("checked",true);
		} else {
			$('#chkAsynchro').attr("checked",false);
		}
		
		$("#txtIp").val(userListData(0).cm_ipaddress);
		$("#txtDaeGyul").val(userListData(0).Txt_DaeGyul);
		$("#txtTerm").val(userListData(0).Txt_BlankTerm);
		$("#txtSayu").val(userListData(0).Txt_BlankSayu);
		
		if(userListData(0).cm_active == "1"){
			$('#rdoAct0').attr("checked",true);
		} else {
			$('#rdoAct1').attr("checked",true);
		}
		
		
	}
	
}

function setUserPwd() { //setUserJumin
	SBUxMethod.openModal("modalPwd");
	
	var modalData = {};
	modalData.userId = document.getElementById("txtId").value;
	var modalIframe = document.getElementById("popPwd");
	console.log(modalIframe);
	modalIframe.contentWindow.pwdTabInit(modalData);//모달의함수 불러오기
}

function setModalPwdclose() {
	SBUxMethod.closeModal("modalPwd");
}

function fnKeyEnter(userId, userName) {//Sql_Qry
	if(userId == "" && userName == "") return;
	
	$("#txtIp").val("");
	$("#txtPhone").val("");
	$("#txtPhone2").val("");
	$("#txtDaeGyu").val("");
	$("#txtTerm").val("");
	$("#txtSayu").val("");
	$("#txtLastIn ").val("");
	$("#txtPass").val("");
	$('#chkAsynchro').attr("checked",false);
	
	$("#txtGroup").val("");
	$("#selPos option:eq(0)").prop("selected", true);
	$("#selDuty option:eq(0)").prop("selected", true);
	$("#rdoOpt0").attr("checked", false);
	$("#rdoOpt1").attr("checked", false);
	$('#chkSysAdmin').attr("checked",false);
	$('#chkAll').attr("checked",false);
	
	var listDutyLen = listDutyData.length;
	for(var i=0; i<listDutyLen; i++){
		listDutyData(i).selected = "";
	}
	
	SBUxMethod.refresh('listDuty');
	jobListData = null;
	listJobData = null;
	$("#selSystem option:eq(0)").prop("selected", true);
	getUserInfo(userId, userName);
}
