/**
 * 공지사항 화면의 기능 정의
 * 
 * <pre>
 * &lt;b&gt;History&lt;/b&gt;
 * 	작성자: 홍소미
 * 	버전 : 1.0
 *  수정일 : 2019-02-07
 * 
 */
var UserId = null;
var strAdmin = "";
var myGrid1;
var combo_dp1;
var strStD = "";
var strEdD = "";
var dataObj = {
	memo_id : "",
	user_name : "",
	user_id : "",
	memo_date : ""
};

$(document).ready(function() {
	createGrid();
	UserId = "MASTER";
	SBUxMethod.hide('start_date');
	SBUxMethod.hide('end_date');
	date_init();
	isAdmin_resultHandler();
	combo1_resultHandler();

	console.log($("#Txt_Find"));
	myGrid1.bind('dblclick', 'myGrid_doubleClick');
})

function createGrid() {
	var myGrid1Properties = {};
	myGrid1Properties.parentid = "myGrid1Area";
	myGrid1Properties.id = "myGrid1";
	myGrid1Properties.jsonref = "grid_dp1";
	myGrid1Properties.rowheader = [ 'seq' ];
	myGrid1Properties.rowheadercaption = {
		seq : 'No'
	};
	myGrid1Properties.columns = [ {
		caption : [ '제목' ],
		ref : 'CM_TITLE',
		width : '16%',
		style : 'text-align:left',
		type : 'output'
	}, {
		caption : [ '등록자' ],
		ref : 'CM_USERNAME',
		width : '14%',
		style : 'text-align:center',
		type : 'output'
	}, {
		caption : [ '등록일' ],
		ref : 'CM_ACPTDATE',
		width : '14%',
		style : 'text-align:center',
		type : 'output'
	}, {
		caption : [ '팝업시작일' ],
		ref : 'CM_STDATE',
		width : '14%',
		style : 'text-align:center',
		type : 'output'
	}, {
		caption : [ '팝업마감일' ],
		ref : 'CM_EDDATE',
		width : '14%',
		style : 'text-align:center',
		type : 'output'
	}, {
		caption : [ '팝업' ],
		ref : 'CM_NOTIYN',
		width : '14%',
		style : 'text-align:center',
		type : 'output'
	}, {
		caption : [ '첨부파일' ],
		ref : 'fileCnt',
		width : '14%',
		style : 'text-align:center',
		type : 'output'
	} ];
	myGrid1Properties.allowuserresize = true;
	myGrid1Properties.width = "100%";
	myGrid1Properties.height = "100%";
	myGrid1 = _SBGrid.create(myGrid1Properties);
	myGrid1.rebuild();
}

function date_init() {
	var now = new Date();
	var year = now.getFullYear();
	var month = (now.getMonth() + 1).toString();
	if (month.length < 2) {
		month = "0" + month;
	}
	var date = now.getDate();
	if (date.length < 2) {
		date = "0" + date;
	}
	SBUxMethod.set('start_date', "" + year + month + date);
	SBUxMethod.set('end_date', "" + year + month + date);
}

function isAdmin_resultHandler() {
	strAdmin = "0";
	$.ajax({
		type : "POST",
		url : "/webPage/mypage/Notice",
		data : "CLASSNAME=UserInfo&UserId=MASTER",
		dataType : 'json',
		async : true,
		success : function(data) {
			if (data) {
				strAdmin = "1";
				SBUxMethod.attr('Cmd_Ip3', 'disabled', 'false');
			} else {
				SBUxMethod.attr('Cmd_Ip3', 'disabled', 'true');
			}

		},
		error : function(req, stat, error) {
			console.log(error);
			alert(error);
		}
	})

}

function combo1_resultHandler() {
	$.ajax({
		type : "POST",
		url : "/webPage/mypage/Notice",
		data : "CLASSNAME=CodeInfo&UserId=MASTER",
		dataType : 'json',
		async : true,
		success : function(data) {
			combo_dp1 = data;
			SBUxMethod.refresh('Cbo_Find');
			cbo();
			grid_resultHandler();
		},
		error : function(req, stat, error) {
			console.log(error);
			alert(error);
		}
	})
}

function cbo() {
	SBUxMethod.hide('start_date');
	SBUxMethod.hide('end_date');
	SBUxMethod.hide('lbl_c');
	SBUxMethod.show('Txt_Find');
	SBUxMethod.attr('Txt_Find', 'disabled', 'true');
	if (SBUxMethod.get('Cbo_Find') == "01"
			|| SBUxMethod.get('Cbo_Find') == "02") {
		SBUxMethod.show('Txt_Find');
		SBUxMethod.attr('Txt_Find', 'disabled', 'false');
	} else if (SBUxMethod.get('Cbo_Find') == "03") {
		SBUxMethod.show('start_date');
		SBUxMethod.show('end_date');
		SBUxMethod.show('lbl_c');
		SBUxMethod.hide('Txt_Find');
	}
}

function grid_resultHandler() {
	var CboFind_micode = SBUxMethod.get("Cbo_Find");
	var TxtFind_text = document.getElementById("Txt_Find").value;

	$.ajax({
		type : "POST",
		url : "/webPage/mypage/Notice",
		data : "CLASSNAME=Cmm2100&UserId=MASTER&CboFind_micode="
				+ CboFind_micode + "&TxtFind_text=" + TxtFind_text + "&strStD="
				+ $("#strStD").val() + "&strEdD=" + $("#strEdD").val(),
		dataType : 'json',
		async : true,
		success : function(data) {
			grid_dp1 = data;
			myGrid1.rebuild();
		},
		error : function(req, stat, error) {
			console.log(error);
			alert(error);
		}
	})
}

function Search_click1() {
	Search_click();
}

function Search_click() {
	if ($("#Cbo_Find option").index($("#Cbo_Find option:selected")) == 1
			|| $("#Cbo_Find option").index($("#Cbo_Find option:selected")) == 2) {
		$('#Txt_Find').val($.trim(document.getElementById("Txt_Find").value));
		if (document.getElementById("Txt_Find").value == "") {
			alert("검색단어를 입력한 후 조회하십시오");
			$("#Txt_Find").focus();
		}
	}

	if ($("#start_date").val() > $("#end_date").val()) {
		alert("조회기간을 정확하게 선택하여 주십시오.");
		return;
	}

	strStD = $("#start_date").val().substr(0, 4)
			+ $("#start_date").val().substr(5, 2)
			+ $("#start_date").val().substr(8, 2);
	strEdD = $("#end_date").val().substr(0, 4)
			+ $("#end_date").val().substr(5, 2)
			+ $("#end_date").val().substr(8, 2);

	grid_resultHandler();
}

function myGrid_doubleClick() {
	SBUxMethod.openModal("popWin_Modal");
	if ($('#Cmd_Ip3').prop("disabled") == false) {
		dataObj.memo_date = "1"
	} else {
		dataObj.memo_date = "0"
	}
	var row = myGrid1.getRows();
	for (var i = 0; i < row - 1; i++) {
		dataObj.memo_id = dataObj.memo_id + grid_dp1.CM_ACPTNO;
		dataObj.cm_stdate = myGrid1.getCellData(i, 4);// 팝업시작일
		dataObj.cm_eddate = myGrid1.getCellData(i, 5);// 팝업마감일
	}
	dataObj.memo_id = grid_dp1.CM_ACPTNO;
	dataObj.user_id = UserId;
	$("#popWin").parentfun = eCmm2101Close;
	$("#popWin").dataObj = this.dataObj;
}

function eCmm2101Close() {
	SBUxMethod.closeModal("popWin_Modal");
	Search_click();
}

function sysPathButton_Click() {
	DataToExcel_Handler();
}

function DataToExcel_Handler() {
	$.ajax({
		type : "POST",
		url : "/webPage/mypage/Notice",
		data : "CLASSNAME=SystemPath&UserId=MASTER",
		dataType : 'json',
		async : true,
		success : function(data) {
			if (date == null) {
				alert("Excel 저장 실패");
			} else {
				var headerDef = new Array();
				var excelData;
				var i = 0;
				var j = 0;
				var k = 0;
				var colCnt = 0;
				var grdList_dp_Len = 0;
				var col = null;
				var arrCol = new Array();

				arrCol = myGrid1.getGridDataAll();
				colCnt = myGrid1.getCols();
				grdList_dp_Len = myGrid1.getCols();
			}
		},
		error : function(req, stat, error) {
			console.log(error);
			alert(error);
		}
	})
}