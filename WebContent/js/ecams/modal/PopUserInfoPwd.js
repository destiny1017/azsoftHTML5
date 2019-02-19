/**
 * 사용자정보-비밀번호초기화 팝업의 기능 정의
 * 
 * <pre>
 * &lt;b&gt;History&lt;/b&gt;
 * 	작성자: 홍소미
 * 	버전 : 1.0
 *  수정일 : 2019-02-15
 * 
 */
var userid 	= null;
var name 	= null;

$(document).ready(function() {

})

function pwdTabInit(modalData) {
	userid 	= modalData.userId;
	$("#txtId").val(userid);
	
	if(userid != null && userid != ""){
		$("#txtPwd").focus();
	} else {
		$("#txtId").focus();
	}
}

function Cmd_Click() {
	var i;
	if(document.getElementById("txtPwd").value.length != 4){
		alert("초기화비밀번호는 4자리로 입력해주세요.");
		$("#txtPwd").val("");
	} else {
		var chkInvaildChar = new RefExp();
		chkInvaildChar = /[^0-9]+/g;
		if(chkInvaildChar.test(document.getElementById("txtPwd").value)){
			alert("초기화비밀번호는 숫자로만 입력하셔야 합니다.");
			$("#txtPwd").val("");
		} else {
			setPwdReset();
		}
	}
}

function setPwdReset() {
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'Cmm1700',
			userId : document.getElementById("txtId").value,
			userPwd : document.getElementById("txtPwd").value
	}
	ajaxReturnData = ajaxCallWithJson('/webPage/modal/PopUserInfoPwd', tmpData, 'json');
	if(ajaxReturnData !== 'ERR') {
		if(ajaxReturnData > 0){
			alert("비밀번호 초기화가 완료되었습니다.");
		} else {
			alert("비밀번호 초기화에 실패하였습니다.\r\n초기화시 비밀번호는 1234 입니다.");
			$("#txtPwd").val("");
			return;
		}
	}
}

function closeAlert(){
	parent.setModalPwdclose();
}