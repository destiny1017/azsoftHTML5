/**
 * 부재등록 화면의 기능 정의
 * <pre>
 * <b>History</b>
 * 	작성자: 홍소미
 * 	버전 : 1.0
 *  수정일 : 2019-02-01
 */
var myGrid1;
var comboDp3;
var comboDp1;
var gridDp1;
var cm_username;
var selectedIndex;
var Sql_tmp_dp1;
var userid = window.parent.userId;

$(document).ready(function() {
	console.log('AbsenceRegister.js load123');
	console.log("userid:"+userid);
	createGrid();
	$('#btnReg').children('span').text($('#rdoOpt0').attr('text'));
	isAdmin_Handler();
	Cbo_Sayu_resultHandler();
	date_init();
	SBUxMethod.attr('txtUser', 'readonly', 'true');
})

function createGrid() {
	var myGrid1Properties = {};
	myGrid1Properties.parentid = "myGrid1Area"; //그리드 영역의 div id입니다.
	myGrid1Properties.id = "myGrid1"; //그리드를 담기 위한 객체명과 동일하게 입력합니다.
	myGrid1Properties.jsonref = "gridDp1";
	myGrid1Properties.columns = [ {
		caption : [ '사원번호' ],
		ref : 'cm_userid',
		width : '30%',
		style : 'text-align:center',
		type : 'output'
	}, {
		caption : [ '대결자' ],
		ref : 'cm_username',
		width : '30%',
		style : 'text-align:center',
		type : 'output'
	}, {
		caption : [ '대결기간' ],
		ref : 'sedate',
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

function isAdmin_Handler() {
	SBUxMethod.attr('txtUser', 'readonly', 'false');
	Cbo_User_resultHandler();
}

function Cbo_User_resultHandler() {
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'Cmm1100',
			UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/AbsenceRegister', tmpData, 'json');
	
	
	if(ajaxReturnData !== 'ERR') {
		comboDp1 = ajaxReturnData;
		if (comboDp1.length > 0) {
			if (comboDp1.length > 1) {
				for (var i = 0; comboDp1.length > i; i++) {
					if (comboDp1[i].cm_userid == userid) {
						//$("#Cbo_User option:eq("+i+")").attr("selected","selected");
						comboDp1[i].selected = "selected";
						SBUxMethod.refresh('cboUser'); //부재자
						Cbo_User_Click();
						break;
					}
				}
			}
			Cbo_User_Click();
		}
	}
	
}

function Cbo_User_Click() {
	SBUxMethod.hide('lbTit');
	$("#txtName").val("");
	$("#txtUser").val("");

	selectedIndex = $("#cboUser option").index($("#cboUser option:selected"));
	if (selectedIndex < 0)
		return;

	$('#txtUser').val(comboDp1[selectedIndex].cm_username);
	gridDp1 = null;
	Cbo_User_Click_resultHandler();
	select_resultHandler();
	getDaegyulState_resultHandler();
}

function Cbo_User_Click_resultHandler() {
	var cm_userid;
	cm_userid = SBUxMethod.get('cboUser');
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'Cmm1100_1',
			UserId : userid ,
			cm_userid : cm_userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/AbsenceRegister', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		Sql_tmp_dp1 = ajaxReturnData;
		SBUxMethod.refresh('cboDaeSign');
		Sql_tmp_dp1[0].selected = "selected";
		SBUxMethod.refresh('cboDaeSign');
		selectedIndex = $("#cboDaeSign option").index(
				$("#cboDaeSign option:selected"));
		Sql_tmp_dp1[selectedIndex] = Sql_tmp_dp1[0];
		DaeSign_username_Set();
	}
	
}

function DaeSign_username_Set() {
	$("#txtName").val("");
	selectedIndex = $("#cboDaeSign option").index($("#cboDaeSign option:selected"));
	if (SBUxMethod.get('cboDaeSign') != "0") {
		$("#txtName").val(Sql_tmp_dp1[selectedIndex].cm_username);
	}
}

function Cbo_Sayu_resultHandler() {
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'CodeInfo',
			UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/AbsenceRegister', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		comboDp3 = ajaxReturnData;
		SBUxMethod.refresh('cboSayu');//부재사유
	}
	
}

function date_init() {
	var sdate = new Date();
	var year = sdate.getFullYear();
	var month = (sdate.getMonth() + 1).toString();
	if (month.length < 2) {
		month = "0" + month;
	}
	var date = sdate.getDate();
	if (date.length < 2) {
		date = "0" + date;
	}
	SBUxMethod.set('dateStD', "" + year + month + date);
	SBUxMethod.set('dateEdD', "" + year + month + date);
}

function Search_click1() {
	var i = 0;

	for (i = 0; comboDp1.length > i; i++) {
		if (comboDp1[i].cm_username == document.getElementById("txtUser").value) {
			comboDp1[i].selected = "selected";
			SBUxMethod.refresh('cboUser');
			Cbo_User_Click();
			break;
		}
	}
}

function Search_click2() {
	var i = 0;
	for (i = 0; Sql_tmp_dp1.length > i; i++) {
		if (Sql_tmp_dp1[i].cm_username == document.getElementById("txtName").value) {
			Sql_tmp_dp1[i].selected = "selected";
			SBUxMethod.refresh('cboDaeSign');
			DaeSign_username_Set();
			break;
		}
	}
	selectedIndex = $("#cboDaeSign option").index(
			$("#cboDaeSign option:selected"));
	if (Sql_tmp_dp1[selectedIndex].cm_username != document.getElementById("txtName").value)
		alert("이름이 존재하지 않습니다.");
}

function select_resultHandler() {
	var cm_userid;
	cm_userid = SBUxMethod.get('cboUser');
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'Cmm1100_2',
			UserId : userid,
			cm_userid : cm_userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/AbsenceRegister', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		gridDp1 = ajaxReturnData;
		myGrid1.rebuild();

		if (gridDp1.length > 0) {
			var daegyulObj = null;
			var tmpObj = null;
			daegyulObj = gridDp1[0];

			for (var i = 0; i < Sql_tmp_dp1.length; i++) {
				tmpObj = Sql_tmp_dp1[i];

				if (tmpObj.cm_userid == daegyulObj.cm_daegyul) {
					Sql_tmp_dp1[i].selected = "selected";
					SBUxMethod.refresh('cboDaeSign');
					var selectedIndex = $("#cboDaeSign option").index(
							$("#cboDaeSign option:selected"));
					Sql_tmp_dp1[selectedIndex] = tmpObj;
					break;
				}
			}

			for (var j = 0; j < comboDp3.length; j++) {
				tmpObj = comboDp3[j];

				if (tmpObj.cm_codename == daegyulObj.cm_daegmsg) {
					comboDp3[i].selected = "selected";
					SBUxMethod.refresh('cboSayu');
					var selectedIndex = $("#cboSayu option").index(
							$("#cboSayu option:selected"));
					comboDp3[selectedIndex] = tmpObj;
					break;
				}
			}
			daegyulObj = null;
			tmpObj = null;
		} else {
			$("#txtSayu").val("");
			$("#cboDaeSign option:eq(0)").prop("selected", true);
			SBUxMethod.refresh('cboDaeSign');
			$("#cboSayu option:eq(0)").prop("selected", true);
			SBUxMethod.refresh('cboSayu');
		}
	}
	
}

function Cbo_Sayu_Click() {
	$("#txtSayu").val("");
	selectedIndex = $("#cboSayu option").index($("#cboSayu option:selected"));
	if (selectedIndex < 1)
		return;
	$("#txtSayu").val(SBUxMethod.get('cboSayu'));
}

function getDaegyulState_resultHandler() {
	var daegyulState;
	var cm_userid;
	cm_userid = SBUxMethod.get('cboUser');
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'Cmm1100_3',
			UserId : userid,
			cm_userid : cm_userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/AbsenceRegister', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		daegyulState = ajaxReturnData;
		if (daegyulState.length != 0) {
			$('#btnReg').children('span').text(
					$('#rdoOpt0').attr('text'));
			if (daegyulState[0].cm_status == '0') {
				$("#rdoOpt0").attr("checked", true);
			} else if (daegyulState[0].cm_status == "9") {
				$("#rdoOpt1").attr("checked", true);
				$('#btnReg').children('span').text(
						$('#rdoOpt1').attr('text'));
				SBUxMethod.show('lbTit');
				$("#lbTit").val(daegyulState[0].Lbl_Tit);
				$("#txtSayu").val("");
				$("#txtName").val("");
				if (gridDp1.length > 0) {
					$("#txtSayu").val(gridDp1[0].cm_daesayu);
					$("#txtName").val(gridDp1[0].cm_username);
				}
			} else {
				$("#rdoOpt0").attr("checked", true);
				SBUxMethod.hide('Lbl_Con');
				SBUxMethod.hide('lbTit');
			}
		}
		daegyulState = null;
	}
	
}

function cmd_click() {
	var selectedObj = {};

	if ($("#rdoOpt0").is(":checked")) {
		if (gridDp1.length > 0) {
			alert("다른사용자가 해당기간에 대결재자로 지정한 상태입니다.확인하여 대결해제한 후 처리하십시오.");
			selectedObj = null;
			return;
		} else {
			selectedIndex = $("#cboSayu option").index(
					$("#cboSayu option:selected"));
			if (selectedIndex < 1) {
				alert("부재사유를 선택하여 주십시오.");
			} else {
				$("#txtSayu").val(
						$.trim(document.getElementById("txtSayu").value));
				var TxtSayu_text = document.getElementById("txtSayu").value;
				if (TxtSayu_text == "") {
					alert("부재사유를 입력하여 주십시오.");
				} else {
					if ($("#dateStD").val() > $("#dateEdD").val()) {
						alert("부재기간을 정확하게 선택하십시오.");
					} else if ($("#dateEdD").val() < $("#dateStD").val()) {
						alert("부재기간을 정확하게 선택하십시오.");
					} else {
						selectedObj.Frm_User = SBUxMethod.get('cboUser');

						if (document.getElementById("txtName").value == "") {
							selectedObj.DaeSign = "";
						} else {
							selectedObj.DaeSign = SBUxMethod.get('cboDaeSign');
						}
						selectedObj.Cbo_Sayu = SBUxMethod.get('cboSayu');
						selectedObj.Txt_Sayu = document
								.getElementById("txtSayu").value;
						selectedObj.sdate = $("#dateStD").val();
						selectedObj.edate = $("#dateEdD").val();
						selectedObj.Opt_Cd0 = $("#rdoOpt0").is(":checked")
								.toString();

						update_resultHandler(selectedObj);
					}
				}
			}
		}
	} else {
		if (gridDp1.length < 1) {
			alert("대결해제 할 대상이 없습니다.");
			selectedObj = null;
			return;
		} else {
			var TxtSayu_text = document.getElementById("txtSayu").value;
			$("#txtSayu").val($.trim(TxtSayu_text));

			selectedObj.Frm_User = SBUxMethod.get('cboUser');
			selectedObj.DaeSign = SBUxMethod.get('cboDaeSign');
			selectedObj.Cbo_Sayu = SBUxMethod.get('cboSayu');
			selectedObj.Txt_Sayu = document.getElementById("txtSayu").value;
			selectedObj.sdate = $("#dateStD").val();
			selectedObj.edate = $("#dateEdD").val();
			selectedObj.Opt_Cd0 = $("#rdoOpt0").is(":checked").toString();

			update_resultHandler(selectedObj);
		}
	}
	selectedObj = null;
}

function update_resultHandler(selectedObj) {
	var tmp_dp = 0;
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'Cmm1100_4',
			UserId : userid,
			dataObj : JSON.stringify(selectedObj)
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/AbsenceRegister', tmpData, 'json');
	
	
	if(ajaxReturnData !== 'ERR') {
		tmp_dp = Number(ajaxReturnData);
		if (tmp_dp == 1) {/*등록선택시*/
			alert("부재 및 대결재자 등록이 완료되었습니다");
			Cbo_User_Click();
		} else if (tmp_dp == 2) {/* 해지선택시 */
			alert("부재 및 대결재자 등록이 해제되었습니다");
			Cbo_User_Click();
			SBUxMethod.hide('Lbl_Con');
			SBUxMethod.hide('lbTit');
			$("#txtName").val("");
			$("#txtSayu").val("");
		} else {
			alert("작업 실패.관리자에게 문의해주세요.");
		}
	}
	
}