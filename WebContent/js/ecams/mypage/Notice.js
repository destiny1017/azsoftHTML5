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
var strAdmin = "";
var myGrid1;
var combo_dp1;
var strStD = "";
var strEdD = "";
var userid = window.parent.userId;
var dataObj = {
	memo_id : "",
	user_name : "",
	user_id : "",
	memo_date : ""
};

$(document).ready(function() { //완료
	createGrid();
	SBUxMethod.hide('start_date');
	SBUxMethod.hide('end_date');
	date_init();
	getAdminInfo();//isAdmin_resultHandler
	combo1_resultHandler();

	myGrid1.bind('dblclick', 'doubleClickGrid1');
})

function createGrid() {
	var myGrid1Properties = {};
	myGrid1Properties.parentid = "divGrid1";
	myGrid1Properties.id = "myGrid1";
	myGrid1Properties.jsonref = "grid_dp1";
	myGrid1Properties.tooltip = true;
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

function date_init() { //한달전 날짜로 해야
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
	var today = new Date();
	var weekDate = today.getTime() - (30*24*60*60*1000);
	today.setTime(weekData);
	
	var weekYear = today.getFullYear();
	var weekMonth = today.getMonth() + 1;
	var weekDay = today.getDate();
	
	if(weekMonth < 10) {weekMonth = "0" + weekMonth;}
	if(weekDay <10) {weekDay = "0" +weekDay;}
	
	var resultDate = weekYear+""+weekMonth+""+weekDay;
	
		
	
	
}

function getAdminInfo() {//isAdmin_resultHandler 완료
	strAdmin = "0";
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'UserInfo',
			UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/Notice', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		if (ajaxReturnData) { //관리자여부
			strAdmin = "1";
			SBUxMethod.attr('btnReg', 'disabled', 'false');
		} else {
			SBUxMethod.attr('btnReg', 'disabled', 'true');
		}
	}
}

function combo1_resultHandler() { //완료
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'CodeInfo',
			UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/Notice', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		combo_dp1 = ajaxReturnData;
		SBUxMethod.refresh('Cbo_Find');
		cbo();
		grid_resultHandler();
	}
}

function cbo() {//완료
	SBUxMethod.hide('start_date');
	SBUxMethod.hide('end_date');
	SBUxMethod.hide('lbl_c');
	SBUxMethod.show('Txt_Find');
	SBUxMethod.attr('Txt_Find', 'disabled', 'true');
	if (SBUxMethod.get('Cbo_Find') == "01" || SBUxMethod.get('Cbo_Find') == "02") {
		SBUxMethod.show('Txt_Find');
		SBUxMethod.attr('Txt_Find', 'disabled', 'false');
	} else if (SBUxMethod.get('Cbo_Find') == "03") {
		SBUxMethod.show('start_date');
		SBUxMethod.show('end_date');
		SBUxMethod.show('lbl_c');
		SBUxMethod.hide('Txt_Find');
	}
}

function grid_resultHandler() { //완료(확인필요)
	var CboFind_micode = SBUxMethod.get("Cbo_Find");
	var TxtFind_text = document.getElementById("Txt_Find").value;
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'Cmm2100',
			UserId : userid,
			CboFind_micode : CboFind_micode,
			TxtFind_text : TxtFind_text,
			strStD : $("#strStD").val(),
			strEdD : $("#strEdD").val()
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/Notice', tmpData, 'json');

	if(ajaxReturnData !== 'ERR') {
		grid_dp1 = ajaxReturnData;
		myGrid1.rebuild();
		SBUxMethod.set('lbCnt','총 '+myGrid1.getRows()+'건');
		for(var i=0;i<myGrid1.getRows();i++){
			myGrid1.setCellTooltip(i,2,myGrid1.getCellData(i,2));
		}
	}
}

function Search_click1() {//완료
	Search_click();
}

function Search_click() { //완료
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

function doubleClickGrid1() {//myGrid_doubleClick 완료(확인해야)
	SBUxMethod.openModal("modalPopWin"); //eCmm2101
	if ($('#btnReg').prop("disabled") == false) {
		dataObj.memo_date = "1"
	} else {
		dataObj.memo_date = "0"
	}
	
	var selectedRow = Number(myGrid1.getSelectedRows());
	
//	dataObj.memo_id = myGrid1.getRowData(selectedRow, false).CM_ACPTNO;
//	console.log(dataObj.memo_id);
//	dataObj.user_id = userid;
//	dataObj.cm_stdate = myGrid1.getRowData(selectedRow, false).CM_STDATE;
//	dataObj.cm_eddate = myGrid1.getRowData(selectedRow, false).CM_EDDATE;
//	$("#modalPopWin").parentfun = popNoticeClose();
//	$("#modalPopWin").dataObj = this.dataObj;
	
}

function popNoticeClose() { //eCmm2101Close	완료
	SBUxMethod.closeModal("modalPopWin");
	Search_click();
}

function sysPathButton_Click() {
	DataToExcel_Handler();
}

function DataToExcel_Handler() {
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'SystemPath',
			UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/Notice', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		if (ajaxReturnData == null) {
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
			
//			var tempObject = new Object;
//			for(i=0; i<colCnt; i++){
//				col = arrCol[i];
//				if()
//			}
		}
	}
}

function new_Click(){ //완료(확인필요)
	SBUxMethod.openModal("modalPopWin"); //eCmm2101
	dataObj.memo_id = null;
	dataObj.memo_date = "1";
	dataObj.user_id = userid;
	$("#modalPopWin").parentfun = popNoticeClose();
	$("#modalPopWin").dataObj = this.dataObj;
}

function sysPathButton_Click() { //완성
	DataToExcel_Handler();
}