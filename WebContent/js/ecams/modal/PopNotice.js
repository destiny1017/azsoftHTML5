/**
 * 공지사항-더블클릭 팝업 화면의 기능 정의
 * 
 * <pre>
 * &lt;b&gt;History&lt;/b&gt;
 * 	작성자: 홍소미
 * 	버전 : 1.0
 *  수정일 : 2019-02-08
 * 
 */

var update_dp2;
var result_dp;
var delete_dp3;
var upFiles;
var editUser = false;
var dataObj = {memo_id:"", user_name:"", user_id:"", memo_date:"", cm_eddate:"" , cm_stdate:""};
var now = new Date();
var picker = new ax5.ui.picker();
var request =  new Request();
var memo_date = null;
$(document).ready(function() {
	dataObj.memo_date =  request.getParameter('memo_date');
	dataObj.memo_id =  request.getParameter('memo_id');
	dataObj.user_id =  request.getParameter('user_id');
	
	
	$('#dateStD').attr('disabled',true);
	$('#lbFrom').attr('disabled',true);
	$('#dateEdD').attr('disabled',true);
	$('#lbTo').attr('disabled',true);
	
	popNoticeInit();
	
	/*SBUxMethod.hide('dateStD');
	SBUxMethod.hide('lbFrom');
	SBUxMethod.hide('dateEdD');
	SBUxMethod.hide('lbTo');*/
	
	
	/*var today = new Date();
	var year = today.getFullYear();
	var month = (today.getMonth() + 1).toString();
	if (month.length < 2) {
		month = "0" + month;
	}
	var date = today.getDate();
	if (date.length < 2){
		date = "0" + date;
	}
	
	if (dataObj.cm_stdate != null && dataObj.cm_stdate != ""){
		SBUxMethod.set('dateStD', "" + dataObj.cm_stdate);
	} else {
		SBUxMethod.set('dateStD', "" + year + month + date);
	}
	
	if (dataObj.cm_eddate != null && dataObj.cm_eddate != ""){
		SBUxMethod.set('dateEdD', "" + dataObj.cm_stdate);
	} else {
		SBUxMethod.set('dateEdD', "" + year + month + date);
	}
	
	if (dataObj.memo_id != null){
		if(dataObj.memo_id != ""){
			getNoticeInfo();
			SBUxMethod.hide('btnFile');
		}
		else{
			SBUxMethod.hide('btnFile1');
			SBUxMethod.show('btnFile');
			SBUxMethod.hide('btnRem');
		}
	} else{
		SBUxMethod.hide('btnFile1');
		SBUxMethod.show('btnFile');
		SBUxMethod.hide('btnRem');
	}
	*/
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
	
});

function popNoticeInit() {
	
}

function confirm() {
	parent.listData();
	parent.setUserName(dataObj);
	parent.popNoticeClose();
}

function getNoticeInfo(){ //selectHandler
	var ajaxReturnData = null;
	
	var tmpData = {
			requestType : 'Cmm2101',
			UserId : userid,
			dataObj : JSON.stringify(dataObj)
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/modal/PopNotice', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		result_dp = ajaxReturnData;
		
		if(result_dp(0).CM_EDITOR == dataObj.user_id){
			SBUxMethod.show('btnReg');
			SBUxMethod.show('btnRem');
			SBUxMethod.show('btnFile1');
			editUser = true;
			editUser = true;
			SBUxMethod.attr('txtTitle', 'disabled', 'false');
			SBUxMethod.attr('textareaContents', 'disabled', 'false');
		} else {
			if(Number(result_dp(0).filecnt) < 1){
				SBUxMethod.hide('btnFile1');
			} else {
				SBUxMethod.show('btnFile1');
			}
			SBUxMethod.hide('btnReg');
			SBUxMethod.hide('btnRem');
			
			SBUxMethod.attr('txtTitle', 'disabled', 'true');
			SBUxMethod.attr('textareaContents', 'disabled', 'true');
		}
		SBUxMethod.set('chkNotice', 'false');
		if(result_dp(0).CM_NOTIYN == "Y"){
			SBUxMethod.set('chkNotice', 'true');
		}
		changeChkNotice();
		
		$("#textareaContents").val(result_dp(0).CM_CONTENTS);
		$("#txtTitle").val(result_dp(0).CM_TITLE);
	}
}

function replaceAllString(source, find, replacement){ //StringReplaceAll
	return source.split( find ).join( replacement );
}

function update(){ //공지사항 등록
	var TODATE = "";
	var monthStr = "";
	var dayStr = "";
	var stDate = "";
	var edDate = "";
	if($('#chkNotice').prop("checked")){
		monthStr = (now.month + 1).toString();
		if((now.month + 1).toString().length <2) monthStr = "0" + (now.month+1).toString();
		datStr = now.date.toString();
		if(now.date.toString().length <2) dayStr = "0" + now.date.toString();
		TODATE = now.fullYear.toString()+monthStr+dayStr;
		stDate = replaceAllString($("#dateStD").val(), "/", "");
		stDate = replaceAllString($("#dateStD").val(), "-", "");
		edDate = replaceAllString($("#dateEdD").val(), "/", "");
		edDate = replaceAllString($("#dateEdD").val(), "-", "");
	}
	
	if(document.getElementById("txtTitle").value == ""){
		alert("제목을 입력하십시오.");
	} else {
		if($("#textareaContents").val() == ""){
			alert("내용을 입력하십시오.");
		} else {
			if($('#chkNotice').prop("checked") && (edDate<stDate)){
				alert("날짜 등록이 잘못되었습니다.");
			} else {
				updateHandler();
			}
		}
	}
}

function updateHandler(){
	var ajaxReturnData = null;
	var updateData = {};
	updateData.memo_id = dataObj.memo_id;
	updateData.user_id = dataObj.user_id;
	updateData.txtTitle = document.getElementById("txtTitle").value;
	updateData.textareaContents = $("#textareaContents").val();
	updateData.chkNotice = $('#chkNotice').prop("checked").toString();
	updateData.stDate = stDate;
	updateData.edDate = edDate;
	
	var tmpData = {
			requestType : 'Cmm2101_1',
			UserId : userid,
			dataObj : JSON.stringify(updateData)
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/modal/PopNotice', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		if(upFiles.length > 0){
			
		}
	}
}