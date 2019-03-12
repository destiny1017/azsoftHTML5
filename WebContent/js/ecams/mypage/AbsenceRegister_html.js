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
var gridDp1 = null;
var cm_username;
var selectedIndex;
var Sql_tmp_dp1;
var userid = window.parent.userId;
var picker = new ax5.ui.picker();
var myGrid1Area = new ax5.ui.grid();

$(document).ready(function() {
	createGrid();
	date_init();
	Cbo_Sayu_resultHandler();
	isAdmin_Handler();
})

function changeBtnText() {
	var selVal = $('input[name="rdoOpt0"]:checked').val();
	if(selVal === '0') {
		$('#btnReg').text('등록');
	}else {
		$('#btnReg').text('해제');
	}
}

function createGrid() {
	myGrid1Area.setConfig({
        target: $('[data-ax5grid="myGrid1Area"]'),
        sortable: true, 
        multiSort: true,
        showRowSelector: false,
        header: {
            align: "center",
            columnHeight: 30
        },
        body: {
            columnHeight: 28,
            onClick: function () {
            	this.self.clearSelect(); //기존선택된 row deselect 처리 (multipleSelect 할땐 제외해야함)
                this.self.select(this.dindex);
            },
            onDBLClick: function () {
        		swal({
                    title: "신청상세팝업",
                    text: "신청번호 ["+this.item.acptno2+"]"
                });

            },
        	onDataChanged: function(){
        	    this.self.repaint();
        	}
        },
        columns: [
            {key: "cm_userid", label: "사원번호",  width: '30%'},
            {key: "cm_username", label: "대결자",  width: '30%'},
            {key: "sedate", label: "대결기간",  width: '40%'}
        ]
    });
	
}

function isAdmin_Handler() {
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
		
		var options = [];
		var setValue = [];
		
		$.each(comboDp1,function(key,value) {
		    options.push({value: value.cm_userid, text: value.username});
		});
		
		$('[data-ax5select="cboUser"]').ax5select({
	        options: options
		});
		
		if (comboDp1.length > 0) {
			if (comboDp1.length > 1) {
				for (var i = 0; comboDp1.length > i; i++) {
					if (comboDp1[i].cm_userid == userid) {
						$('[data-ax5select="cboUser"]').ax5select("setValue", comboDp1[i].cm_userid, true);
						Cbo_User_Click();
						break;
					}
				}
			}
		}
	}
}

function Cbo_User_Click() {
	$("#txtName").val("");
	$("#txtUser").val("");

	selectedIndex = $("#cboUser option").index($("#cboUser option:selected"));
	if (selectedIndex < 0) return;
	$('#txtUser').val(comboDp1[selectedIndex].cm_username);
	gridDp1 = null;
	Cbo_User_Click_resultHandler();
	select_resultHandler();
	getDaegyulState_resultHandler();
}

function Cbo_User_Click_resultHandler() {
	var cm_userid;
	cm_userid = $('[data-ax5select="cboUser"]').ax5select("getValue");
	cm_userid = cm_userid[0].value;
	var ajaxReturnData = null;
	
	var tmpData = {
		requestType : 'Cmm1100_1',
		UserId : userid ,
		cm_userid : cm_userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/AbsenceRegister', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		Sql_tmp_dp1 = ajaxReturnData;
		var options = [];
		var setValue = [];
		$.each(Sql_tmp_dp1,function(key,value) {
		    options.push({value: value.cm_userid, text: value.username});
		});
		$('[data-ax5select="cboDaeSign"]').ax5select({
	        options: options
		});
		
		DaeSign_username_Set();
	}
	
}

function DaeSign_username_Set() {
	$("#txtName").val("");
	selectedIndex = $("#cboDaeSign option").index($("#cboDaeSign option:selected"));
	if (selectedIndex != 0) {
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
		var options = [];
		var setValue = [];
		$.each(comboDp3,function(key,value) {
		    options.push({value: value.cm_micode, text: value.cm_codename});
		});
		$('[data-ax5select="cboSayu"]').ax5select({
	        options: options
		});
	}
	
}

function date_init() {
	
	$('#datStD').val(getDate('DATE',0));
	$('#datEdD').val(getDate('DATE',0));
	
	picker.bind({
        target: $('[data-ax5picker="basic"]'),
        direction: "top",
        content: {
            width: 220,
            margin: 10,
            type: 'date',
            config: {
                control: {
                    left: '<i class="fa fa-chevron-left"></i>',
                    yearTmpl: '%s',
                    monthTmpl: '%s',
                    right: '<i class="fa fa-chevron-right"></i>'
                },
                dateFormat: 'yyyy/MM/dd',
                lang: {
                    yearTmpl: "%s년",
                    months: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
                    dayTmpl: "%s"
                }
            },
            formatter: {
                pattern: 'date'
            }
        },
        btns: {
            today: {
                label: "Today", onClick: function () {
                    var today = new Date();
                    this.self
                            .setContentValue(this.item.id, 0, ax5.util.date(today, {"return": "yyyy/MM/dd"}))
                            .setContentValue(this.item.id, 1, ax5.util.date(today, {"return": "yyyy/MM/dd"}))
                            .close();
                }
            },
            thisMonth: {
                label: "This Month", onClick: function () {
                    var today = new Date();
                    this.self
                            .setContentValue(this.item.id, 0, ax5.util.date(today, {"return": "yyyy/MM/01"}))
                            .setContentValue(this.item.id, 1, ax5.util.date(today, {"return": "yyyy/MM"})
                                    + '/'
                                    + ax5.util.daysOfMonth(today.getFullYear(), today.getMonth()))
                            .close();
                }
            },
            ok: {label: "Close", theme: "default"}
        }
    });
	picker.bind({
        target: $('[data-ax5picker="basic2"]'),
        direction: "top",
        content: {
            width: 220,
            margin: 10,
            type: 'date',
            config: {
                control: {
                    left: '<i class="fa fa-chevron-left"></i>',
                    yearTmpl: '%s',
                    monthTmpl: '%s',
                    right: '<i class="fa fa-chevron-right"></i>'
                },
                dateFormat: 'yyyy/MM/dd',
                lang: {
                    yearTmpl: "%s년",
                    months: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
                    dayTmpl: "%s"
                }
            },
            formatter: {
                pattern: 'date'
            }
        },
        btns: {
            today: {
                label: "Today", onClick: function () {
                    var today = new Date();
                    this.self
                            .setContentValue(this.item.id, 0, ax5.util.date(today, {"return": "yyyy/MM/dd"}))
                            .setContentValue(this.item.id, 1, ax5.util.date(today, {"return": "yyyy/MM/dd"}))
                            .close();
                }
            },
            thisMonth: {
                label: "This Month", onClick: function () {
                    var today = new Date();
                    this.self
                            .setContentValue(this.item.id, 0, ax5.util.date(today, {"return": "yyyy/MM/01"}))
                            .setContentValue(this.item.id, 1, ax5.util.date(today, {"return": "yyyy/MM"})
                                    + '/'
                                    + ax5.util.daysOfMonth(today.getFullYear(), today.getMonth()))
                            .close();
                }
            },
            ok: {label: "Close", theme: "default"}
        }
    });
}

function Search_click1() {
	var i = 0;

	for (i = 0; comboDp1.length > i; i++) {
		if (comboDp1[i].cm_username == document.getElementById("txtUser").value) {
			$("#cboUser option:eq("+i+")").prop("selected", true);
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
			$("#cboDaeSign option:eq("+i+")").prop("selected", true);
			SBUxMethod.refresh('cboDaeSign');
			DaeSign_username_Set();
			break;
		}
	}
	selectedIndex = $("#cboDaeSign option").index($("#cboDaeSign option:selected"));
	if (Sql_tmp_dp1[selectedIndex].cm_username != document.getElementById("txtName").value)
		alert("이름이 존재하지 않습니다.");
}

function select_resultHandler() {
	var cm_userid;
	cm_userid = $('#cboUser option:selected').val();
	var ajaxReturnData = null;
	
	var tmpData = {
		requestType : 'Cmm1100_2',
		UserId : userid,
		cm_userid : cm_userid
	}
	
	gridDp1 = null;
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/AbsenceRegister', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		gridDp1 = ajaxReturnData;
		if(gridDp1 !== null) myGrid1Area.setData(gridDp1);
		
		if (gridDp1 !== null && gridDp1.length > 0) {
			var daegyulObj = null;
			var tmpObj = null;
			daegyulObj = gridDp1[0];
			
			if( Sql_tmp_dp1 !== null && Sql_tmp_dp1.length > 0){
				for (var i = 0; i < Sql_tmp_dp1.length; i++) {
					tmpObj = Sql_tmp_dp1[i];

					if (tmpObj.cm_userid == daegyulObj.cm_daegyul) {
						$('[data-ax5select="cboDaeSign"]').ax5select("setValue", daegyulObj.cm_daegyul);
						break;
					}
				}
			}
			daegyulObj = null;
			tmpObj = null;
		} else {
			$("#txtSayu").val("");
			$("#cboDaeSign option:eq(0)").prop("selected", true);
			$("#cboSayu option:eq(0)").prop("selected", true);
		}
	}
	
}

function Cbo_Sayu_Click() {
	$("#txtSayu").val("");
	selectedIndex = $("#cboSayu option").index($("#cboSayu option:selected"));
	if (selectedIndex < 1)
		return;
	
	$("#txtSayu").val(comboDp3[selectedIndex].cm_codename);
}

function getDaegyulState_resultHandler() {
	var daegyulState = null;
	var cm_userid = $('#cboUser option:selected').val();
	var ajaxReturnData = null;
	
	var tmpData = {
		requestType : 'Cmm1100_3',
		UserId : userid,
		cm_userid : cm_userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/AbsenceRegister', tmpData, 'json');
	if(ajaxReturnData !== 'ERR') {
		daegyulState = ajaxReturnData;
		if (daegyulState !== null && daegyulState.length != 0) {
			if (daegyulState[0].cm_status == '0') {
				$("#rdoOpt0").attr("checked", true);
			} else if (daegyulState[0].cm_status == "9") {
				$("#rdoOpt1").attr("checked", true);
				//$('#btnReg').children('span').text($('#rdoOpt1').attr('text'));
				//SBUxMethod.show('lbTit');
				$("#lbTit").val(daegyulState[0].Lbl_Tit);
				$("#txtSayu").val("");
				$("#txtName").val("");
				if (gridDp1.length > 0) {
					$("#txtSayu").val(gridDp1[0].cm_daesayu);
					$("#txtName").val(gridDp1[0].cm_username);
				}
			} else {
				$("#rdoOpt0").attr("checked", true);
				//SBUxMethod.hide('Lbl_Con');
				//SBUxMethod.hide('lbTit');
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
			selectedIndex = $("#cboDaeSign option").index($("#cboDaeSign option:selected"));
			if (selectedIndex < 1) {
				alert("대결재자를 선택하여 주십시오.");
				return;
			} 
			
			
			selectedIndex = $("#cboSayu option").index($("#cboSayu option:selected"));
			if (selectedIndex < 1) {
				alert("부재사유를 선택하여 주십시오.");
				return;
			} 
			
			$("#txtSayu").val( $.trim(document.getElementById("txtSayu").value) );
			var TxtSayu_text = document.getElementById("txtSayu").value;
			
			if (TxtSayu_text == "") {
				alert("부재사유를 입력하여 주십시오.");
				return;
			} 
			if ($("#dateStD").val() > $("#dateEdD").val()) {
				alert("부재기간을 정확하게 선택하십시오.");
				return;
			} 
			if ($("#dateEdD").val() < $("#dateStD").val()) {
				alert("부재기간을 정확하게 선택하십시오.");
				return;
			} 
			
			selectedObj.Frm_User = $('#cboUser option:selected').val();

			if (document.getElementById("txtName").value == "") {
				selectedObj.DaeSign = "";
			} else {
				selectedObj.DaeSign = $('#cboDaeSign option:selected').val();
			}
			selectedObj.Cbo_Sayu = $('#cboSayu option:selected').val();
			selectedObj.Txt_Sayu = document.getElementById("txtSayu").value;
			selectedObj.sdate = $("#datStD").val().substr(0,4) + $("#datStD").val().substr(5,2) + $("#datStD").val().substr(8,2);
			selectedObj.edate = $("#datEdD").val().substr(0,4) + $("#datEdD").val().substr(5,2) + $("#datEdD").val().substr(8,2);
			selectedObj.Opt_Cd0 = $("#rdoOpt0").is(":checked").toString();

			update_resultHandler(selectedObj);
		}
	} else {
		if (gridDp1.length < 1) {
			alert("대결해제 할 대상이 없습니다.");
			selectedObj = null;
			return;
		} else {
			var TxtSayu_text = document.getElementById("txtSayu").value;
			$("#txtSayu").val($.trim(TxtSayu_text));

			selectedObj.Frm_User =$('#cboUser option:selected').val();
			selectedObj.DaeSign = $('#cboDaeSign option:selected').val();
			selectedObj.Cbo_Sayu =  $('#cboSayu option:selected').val();
			selectedObj.Txt_Sayu = document.getElementById("txtSayu").value;
			selectedObj.sdate = $("#datStD").val().substr(0,4) + $("#datStD").val().substr(5,2) + $("#datStD").val().substr(8,2);
			selectedObj.edate = $("#datEdD").val().substr(0,4) + $("#datEdD").val().substr(5,2) + $("#datEdD").val().substr(8,2);
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
			$("#txtName").val("");
			$("#txtSayu").val("");
		} else {
			alert("작업 실패.관리자에게 문의해주세요.");
		}
	}
}