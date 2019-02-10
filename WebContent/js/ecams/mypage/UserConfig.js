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

var SysCd = null;
var secuYn = "";
var dirPopUp = null;
var PrjInfoChange = null;
var Lv_File0_dp = new ArrayCollection;
var grid_dp1 = new ArrayCollection;
var grid_dp2 = new ArrayCollection;
var Cbo_SysCd_dp = new ArrayCollection;
var combo_dp1 = new ArrayCollection;
var tree_dp1 = new ArrayCollection;
var Register1 = new ArrayCollection;
var Temp_dp = new ArrayCollection;
var userid = window.parent.userId;

$(document).ready(function() {
	if(userid == "" || userid == null){
		alert("로그인 후 사용하시기 바랍니다.");
		return;
	}
	
	SBUxMethod.attr('btnDir', 'disabled', 'true');
	SBUxMethod.attr('btnReg', 'disabled', 'true');
	getAdminInfo();
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
}

function getAdminInfo() {
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'UserInfo',
			UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/UserConfig', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		if(ajaxReturnData) secuYn="N";
		else secuYn="Y";
		
	}
}