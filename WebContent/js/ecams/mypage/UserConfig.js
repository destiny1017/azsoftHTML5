/**
 * 사용자환경설정 화면의 기능 정의
 * 
 * <pre>
 * &lt;b&gt;History&lt;/b&gt;
 * 	작성자: 홍소미
 * 	버전 : 1.0
 *  수정일 : 2019-02-08
 * 
 */

var sysCd = null;
var secuYn = "";
var dirPopUp = null;
var PrjInfoChange = null;
var Lv_File0_dp;
var gridDp1;
var grid_dp2;
var selSysnameDp;//Cbo_SysCd_dp
var combo_dp1;
var tree_dp1;
var Register1;
var Temp_dp;
var userid = window.parent.userId;

$(document).ready(function() { //완료
	createGrid();
	if(userid == "" || userid == null){
		alert("로그인 후 사용하시기 바랍니다.");
		return;
	}
	
	SBUxMethod.hide('btnDir');
	SBUxMethod.attr('btnReg', 'disabled', 'true');
	getAdminInfo();
	getGrid1();//Sql_Qry
	myGrid1.bind('click', 'setButton');
})

function createGrid(){
	var myGrid1Properties = {};
	myGrid1Properties.parentid = "divGrid1"; //그리드 영역의 div id입니다.
	myGrid1Properties.id = "myGrid1"; //그리드를 담기 위한 객체명과 동일하게 입력합니다.
	myGrid1Properties.jsonref = "gridDp1";
	myGrid1Properties.rowheader = [ 'seq' ];
	myGrid1Properties.rowheadercaption = {
		seq : '순서'
	};
	myGrid1Properties.columns = [ {
		caption : [ '시스템' ],
		ref : 'cm_sysmsg',
		width : '30%',
		style : 'text-align:center',
		type : 'output'
	}, {
		caption : [ '개발 Home Directory' ],
		ref : 'cd_devhome',
		width : '40%',
		style : 'text-align:center',
		type : 'output'
	} ];
	myGrid1Properties.allowuserresize = true;
	myGrid1Properties.width = "100%";
	myGrid1Properties.height = "100%";
	myGrid1 = _SBGrid.create(myGrid1Properties);
	myGrid1.rebuild();
}

function getAdminInfo() { //isAdmin_Handler
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'UserInfo',
			UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/UserConfig', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		if(ajaxReturnData) secuYn="N";
		else secuYn="Y";
		getSelSysname(secuYn);
	}
}

function getSelSysname(secuYn) { //Cbo_Syscd_resultHandler
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'SysInfo',
			UserId : userid,
			SecuYn : secuYn
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/UserConfig', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		selSysnameDp = ajaxReturnData;
		SBUxMethod.refresh('selSysname');
		if(sysCd != null && sysCd != ""){
			for(var i=0; selSysnameDp.length>i; i++){
				if(selSysnameDp[i].cm_syscd == sysCd) {
					$("#selSysname option:eq("+i+")").prop("selected", true);
					changeSelSysname();//Cbo_SysCd_Click
					break;
				}
			}
		}
		var selectedIndex = $("#selSysname option").index($("#selSysname option:selected"));
		if(selectedIndex < 1 && selSysnameDp.length == 2){
			$("#selSysname option:eq(1)").prop("selected", true);
			changeSelSysname();
		}
	}
}

function changeSelSysname() { //Cbo_SysCd_Click
	SBUxMethod.hide('btnDir');
	SBUxMethod.attr('btnReg', 'disabled', 'true');
	$("#txtDir").val("");
	var selectedIndex = $("#selSysname option").index($("#selSysname option:selected"));
	if(selectedIndex < 1) return;
	SBUxMethod.show('btnDir');
	SBUxMethod.attr('btnReg', 'disabled', 'false');
	
	if(typeof gridDp1 != "undefined"){
		for(var i=0; gridDp1.length > i; i++){
			if(gridDp1(i).cd_syscd == SBUxMethod.get('selSysname')){
				$("#txtDir").val(gridDp1(i).cd_devhome);
				break;
			}
		}
	}
}

function getGrid1() { //Sql_Qry
	getGrid1Data();
}

function getGrid1Data() { //getSql_Qry_Handler
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'Cmd1300',
			UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/UserConfig', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		console.log('Hong');
		console.log(ajaxReturnData);
		gridDp1 = ajaxReturnData;
		myGrid1.bind('click', 'changeGrid1');
	}
}

function changeGrid1() { //Lv_File0_click
	if(myGrid1.getSelectedRows() < 0) return;
	$("#txtDir").val("");
	SBUxMethod.hide('btnDir');
	for(var i=0; selSysnameDp.length > i; i++){
		if(selSysnameDp(i).cm_syscd == myGrid1.getRowData(selectedRow, false).cd_syscd){
			$("#selSysname option:eq("+i+")").prop("selected", true);
			$("#txtDir").val(myGrid1.getRowData(selectedRow, false).cd_devhome);
			SBUxMethod.show('btnDir');
			break;
		}
	}
}

function setButton() { //button_enabled 완료
	SBUxMethod.show('btnDir');
	SBUxMethod.attr('btnReg', 'disabled', 'false');
}

function Cmd_Ip_Click(index){
	switch (index){
		case 0 :
			getGrid1();
			break;
		case 1 :
			if($("#selSysname option").index($("#selSysname option:selected") < 1)){
				alert("시스템을 선택하여 주십시오.");
				return;
			} else if(document.getElementById("txtDir").value == ""){
				alert("폴더를 입력하여 주십시오.");
				return;
			} else{
				updateHomeDir();
			}
			break;
		case 2 :
			var i;
			if(gridDp1.length == 0){
				alert("삭제할 항목이 없습니다.");
				return;
			}else{
				if(myGrid1.getSelectedRows() < 1){
					alert("삭제할 항목을 선택한 후 처리하십시오.");
					return;
				} else {
					deleteHomeDir();
				}
			}
			break;
	}
}

function updateHomeDir() { //TblUpdate
	getUpdateHomeDir();
}

function getUpdateHomeDir() {
var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'Cmd1300_1',
			UserId : userid,
			selSysCd : SBUxMethod.get('selSysname'),
			txtDir : document.getElementById("txtDir").value
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/UserConfig', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		if(ajaxReturnData > 0 ) alert("Home Directory가 등록되었습니다.");
		getGrid1();
	}
}

function deleteHomeDir(){ //TblDelete
	var selectedIndex = myGrid1.getRow();
	var selectedItem = myGrid1.getRowData(selectedIndex, false);
	for(var i=0; i<selectedItem.length; i++){
		//removeat
	}
}