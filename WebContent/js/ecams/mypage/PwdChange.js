/**
 * 비밀번호변경 화면의 기능 정의
 * <pre>
 * <b>History</b>
 * 	작성자: 홍소미
 * 	버전 : 1.0
 *  수정일 : 2019-01-31
 */
var phoneNumber;
var tmp_dp3;
var userInfoDp;
var lastPassDp;
var befEncPassWd;
var updtEncPassWd;
var userid = window.parent.userId;

$(document).ready(function() {
	console.log('PwdChange.js load');
	if(userid == "" || userid == null){
		alert("로그인 후 사용하시기 바랍니다.");
		return;
	}
	$("#txtUserId").val(userid);
	getUserInfo();
	getLastPasswdBef();
});

function getUserInfo() {
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'MemberDAO',
			UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/PwdChange', tmpData, 'json');
	
	
	if(ajaxReturnData !== 'ERR') {
		userInfoDp = ajaxReturnData;
		console.log(userInfoDp);
		$("#txtUserName").val(userInfoDp[0].user_name);
		phoneNumber = userInfoDp[0].phone_number.substr(7, 4);
		
		getPasswdBef();
	}
}

function getPasswdBef() { //변경전 비밀번호
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'PassWdDAO',
			UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/PwdChange', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		tmp_dp3 = ajaxReturnData;
		$("#txtPw").focus();
		if (tmp_dp3.length != 64) { //기존 암호화가 안되어 있으면
			encryptPasswdBef();
		}
	}
}

function encryptPasswdBef() { //변경전 비밀번호 암호화
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'PassWdDAO_1',
			UserId : userid ,
			usr_passwd : tmp_dp3
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/PwdChange', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		tmp_dp3 = data;
	}
}

function getLastPasswdBef() { //마지막 변경전 비밀번호
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'PassWdDAO_2',
			UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/PwdChange', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		lastPassDp = ajaxReturnData;
		getPasswdBef();
	}
}

function clickBtnPw() {
	var pw = document.getElementById("txtPw");
	var userId = document.getElementById("txtUserId");
	var updatePw1 = document.getElementById("txtUpdatePw1");
	var updatePw2 = document.getElementById("txtUpdatePw2");
	if (pw.value == null || pw.value == "") {
		alert("변경전 비밀번호를 입력해주세요.");
	} else if (updatePw1.value == "") {
		alert("설정하실 비밀번호를 입력해주십시요.");
	} else if (updatePw1.value.length < 8 || $("#txtUpdatePw2").length > 12) {
		alert("비밀번호는 8-12자리로 설정하여 주십시요.");
		$("#txtUpdatePw1").val("");
		$("#txtUpdatePw2").val("");
		$("#txtUpdatePw1").focus();
	} else {
		var pattern1 = "0123456789";
		var pattern2 = "abcdefghijklmnopqrstuvwxyz";
		var pattern3 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		var pattern4 = "-_=+\|()*&^%$#@!~`?></;,.:'";
		var tmp1 = 0;
		var tmp2 = 0;
		var encnt = 0;
		var numcnt = 0;
		var spcnt = 0;
		var temp = null;
		var samenum = false;
		var connum = false;
		var i;

		for (i = 0; i < updatePw1.value.length; i++) {
			if (pattern1.indexOf(updatePw1.value.charAt(i), 0) >= 0) {
				numcnt++;
			}
			if (pattern2.indexOf(updatePw1.value.charAt(i), 0) >= 0) {
				encnt++;
			}
			if (pattern3.indexOf(updatePw1.value.charAt(i), 0) >= 0) {
				encnt++;
			}
			if (pattern4.indexOf(updatePw1.value.charAt(i), 0) >= 0) {
				spcnt++;
			}

			if (temp == updatePw1.value.charAt(i) && temp >= '0' && temp <= '9') {
				samenum = true;
			} else if (temp != updatePw1.value.charAt(i) && temp != null
					&& temp >= '0' && temp <= '9') {
				temp1 = Number(temp) + 1;
				temp2 = Number(updatePw1.value.charAt(i));

				if (temp1 == temp2) {
					connum = true;
				}
			}
			temp = updatePw1.value.charAt(i);

		}
		
		//여기부터 
		if (encnt == 0 || numcnt == 0 || spcnt == 0) {
			alert("영문/숫자/특수문자 조합으로 등록이 가능합니다.");
			updatePw1.value = "";
			updatePw2.value = "";
			$("#txtUpdatePw1").focus();
		} else if (samenum == true) {
			alert("비밀번호에 연속된 동일숫자가 있어 불가합니다.");
			updatePw1.value = "";
			updatePw2.value = "";
			$("#txtUpdatePw2").focus();
		} else if (connum == true) {
			alert("비밀번호에 연속숫자가 있어 사용불가합니다.");
			updatePw1.value = "";
			updatePw2.value = "";
			$("#txtUpdatePw2").focus();
		} else if (updatePw1.value == userId.value) {
			alert("변경비밀번호와 사번이 일치합니다.");
			updatePw1.value = "";
			updatePw2.value = "";
			$("#txtUpdatePw1").focus();
		} else if (updatePw1.value != updatePw2.value) {
			alert("변경비밀번호와 확인비밀번호가 일치하지 않습니다.");
			updatePw2.value = "";
			$("#txtUpdatePw2").focus();
		} else if (updatePw1.value.indexOf(phoneNumber, 0) > 0) {
			alert("전화번호 사용불가합니다.");
			updatePw1.value = "";
			updatePw2.value = "";
			$("#txtUpdatePw1").focus();
		} else if (updatePw1.value == updatePw2.value) {
			encryptTxtPw();
		}
	}
}

function encryptTxtPw() { //입력변경전비밀번호 암호화
	var pw = document.getElementById("txtPw");
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'PassWdDAO_3',
			UserId : userid ,
			usr_passwd : pw.value
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/PwdChange', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		befEncPassWd = ajaxReturnData;
		Cmd_Click3();
	}
}

function Cmd_Click3() {
	var i = 0;
	var updatePw1 = document.getElementById("txtUpdatePw1");
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'PassWdDAO_4',
			UserId : userid ,
			usr_passwd : updatePw1.value
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/PwdChange', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		updtEncPassWd = ajaxReturnData;
		if (tmp_dp3 != befEncPassWd) {
			alert("변경전 비밀번호가 일치하지 않습니다.");
			$("#txtPw").val("");
			$("#txtPw").focus();
		} else if (tmp_dp3 == updtEncPassWd) {
			alert("변경전 비밀번호와 일치합니다.");
		} else {
			for (i = 0; i < lastPassDp.length; i++) {
				if (lastPassDp[i].lst_passwd == updtEncPassWd) {
					alert("이전에 변경된 비밀번호와 일치 합니다");
					$("#txtUpdatePw1").val("");
					$("#txtUpdatePw2").val("");
					$("#txtUpdatePw1").focus();
					return;
				}
			}
			setPassWd();
		}
	}
}

function setPassWd() {
	var updatePw1 = document.getElementById("txtUpdatePw1");
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'PassWdDAO_5',
			UserId : userid ,
			usr_passwd : updatePw1.value
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/PwdChange', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		if (ajaxReturnData > 0) {
			alert("비밀번호변경이 완료되었습니다.");
			$("#txtPw").val("");
			$("#txtUpdatePw1").val("");
			$("#txtUpdatePw2").val("");
			befEncPassWd = "";
			updtEncPassWd = "";
			tmp_dp3 = "";
			location.reload(true);
		} else {
			alert("비밀번호 변경 실패");
		}
	}
	
}