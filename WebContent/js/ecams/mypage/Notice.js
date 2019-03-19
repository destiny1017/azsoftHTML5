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
var picker = new ax5.ui.picker();
var divGrid1 = new ax5.ui.grid();
var mask = new ax5.ui.mask();
var modal = new ax5.ui.modal();

$(document).ready(function() { //완료
	createGrid();
	getAdminInfo();
	date_init();
})
function createGrid() {
	
	divGrid1.setConfig({
        target: $('[data-ax5grid="divGrid1"]'),
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
            	doubleClickGrid1();
            },
        	onDataChanged: function(){
        	    this.self.repaint();
        	}
        },
        columns: [
            {key: "CM_TITLE", label: "제목",  width: '40%'},
            {key: "CM_USERNAME", label: "등록자",  width: '10%'},
            {key: "CM_STDATE", label: "팝업시작일",  width: '10%'},
            {key: "CM_EDDATE", label: "팝업마감일",  width: '10%'},
            {key: "CM_NOTIYN", label: "팝업",  width: '10%'},
            {key: "fileCnt", label: "첨부파일",  width: '20%'}
        ]
    });
}

function date_init() { //한달전 날짜로 해야
	$('#start_date').val(getDate('MON',-1));
	$('#end_date').val(getDate('DATE',0));
	
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
	
	grid_resultHandler();
}

function getAdminInfo() {//isAdmin_resultHandler 완료
	strAdmin = "0";
	var ajaxReturnData = null;
	
	var tmpData = {
		requestType : 'UserInfo',
		UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/Notice', tmpData, 'json');
	console.log('admin');
	console.log(ajaxReturnData);
	if(ajaxReturnData !== 'ERR') {
		if (ajaxReturnData) { //관리자여부
			strAdmin = "1";
			$('#btnReg').attr('disabled',false);
		}
	}
}

/*function combo1_resultHandler() { //완료
	var ajaxReturnData = null;
	
	var tmpData = {
		requestType : 'CodeInfo',
		UserId : userid
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/Notice', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		
		combo_dp1 = ajaxReturnData;
		
		var options = [];
		var setValue = [];
		$.each(combo_dp1,function(key,value) {
		    options.push({value: value.cm_micode, text: value.cm_codename});
		});
		$('[data-ax5select="Cbo_Find"]').ax5select({
	        options: options
		});
		
	}
}*/

function grid_resultHandler() { //완료(확인필요)
	strStD = $("#start_date").val().substr(0,4) + $("#start_date").val().substr(5,2) + $("#start_date").val().substr(8,2);
	strEdD = $("#end_date").val().substr(0,4) + $("#end_date").val().substr(5,2) + $("#end_date").val().substr(8,2);
	var CboFind_micode = $('#Cbo_Find option:selected').val();
	var TxtFind_text = document.getElementById("Txt_Find").value === null ? '' : document.getElementById("Txt_Find").value;
	var ajaxReturnData = null;
	
	var tmpData = {
		requestType : 'Cmm2100',
		UserId : userid,
		CboFind_micode : '02',
		TxtFind_text : TxtFind_text,
		strStD : strStD,
		strEdD : strEdD
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/Notice', tmpData, 'json');

	if(ajaxReturnData !== 'ERR') {
		grid_dp1 = ajaxReturnData;
		
		divGrid1.setData(grid_dp1);
		
		$('#lbCnt').text('총 '+grid_dp1.length+'건');

		//제목에 툴팁달기  !!
	}
}

function Search_click1() {//완료
	Search_click();
}

function Search_click() { //완료
	if ($("#Cbo_Find option").index($("#Cbo_Find option:selected")) == 1
			|| $("#Cbo_Find option").index($("#Cbo_Find option:selected")) == 2) {
		$('#Txt_Find').val($.trim(document.getElementById("Txt_Find").value));
	}
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

var modalCallBack = function(){
    modal.close();
};

function new_Click(){ //완료(확인필요)
	
    modal.open({
        width: 600,
        height: 440,
        iframe: {
            method: "get",
            url: "../modal/PopNotice.jsp",
            param: "callBack=modalCallBack"
        },
        onStateChanged: function () {
            // mask
            if (this.state === "open") {
                mask.open();
            }
            else if (this.state === "close") {
                mask.close();
            }
        }
    }, function () {
    });
	
}

function sysPathButton_Click() { //완성
	DataToExcel_Handler();
}