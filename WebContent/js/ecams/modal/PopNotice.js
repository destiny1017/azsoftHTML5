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
var stDate = "";
var edDate = "";
var now = new Date();
var picker = new ax5.ui.picker();
var request =  new Request();
var memo_date = null;
var noticeInfo = null;
var dialog = new ax5.ui.dialog({title: "확인"});

$(document).ready(function() {
	popNoticeInit();
});

function popNoticeInit() {
	$('#divPicker').css('display','none');
	$('#btnRem').css('display','none');
	$('#btnReg').css('display','none');
	
	dateInit();
	
	noticeInfo = window.parent.noticePopData;
	if(noticeInfo !== null) {
		if(noticeInfo.CM_NOTIYN === 'Y') {
			
			var startDate 	= replaceAllString(noticeInfo.CM_STDATE, "/", "");
			var endDate 	= replaceAllString(noticeInfo.CM_EDDATE, "/", "");
			startDate 		= ax5.util.date(startDate, {'return': 'yyyy/MM/dd', 'add': {d: 0}} );
			endDate 		= ax5.util.date(endDate, {'return': 'yyyy/MM/dd', 'add': {d: 0}} );
			
			$('#divPicker').css('display','');
			$('#dateStD').val(startDate);
			$('#dateEdD').val(endDate);
			$('#exampleCheck1').prop('checked',true);
		}
		
		$('#txtTitle').val(noticeInfo.CM_TITLE);
		$('#textareaContents').val(noticeInfo.CM_CONTENTS);
		
		if(window.top.userId !== noticeInfo.CM_EDITOR) {
			$('#btnRem').css('display','none');
			$('#btnReg').css('display','none');
			
			if(noticeInfo.CM_NOTIYN === 'Y') {
				$('#divPicker').empty();
				var htmlStr = '';
				htmlStr += '<input id="dateStD" name="dateStD" type="text" class="form-control" value="'+noticeInfo.CM_STDATE+'">';
				htmlStr += '	<span class="input-group-addon">~</span>';
				htmlStr += '<input id="dateEdD" name="dateEdD" type="text" class="form-control" value="'+noticeInfo.CM_EDDATE+'">';
				$('#divPicker').html(htmlStr);
			}
			
			$('#txtTitle').attr('disabled',true);
			$('#textareaContents').attr('disabled',true);
		} else {
			$('#btnRem').css('display','');
			$('#btnReg').css('display','');
			
		}
		
		$('#btnFile').text('첨부파일');
	} else if ( noticeInfo === null ) {
		$('#btnReg').css('display','');
	}
}

function dateInit() {
	// 오늘 날짜로 초기화
	
	picker.bind({
        target: $('[data-ax5picker="basic"]'),
        direction: "bottom",
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
                },
                marker: (function () {
                    var marker = {};
                    marker[ax5.util.date(new Date(), {'return': 'yyyy/MM/dd', 'add': {d: 0} } )] = true;

                    return marker;
                })()
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
	
	$('#dateStD').val(ax5.util.date(new Date(), {'return': 'yyyy/MM/dd', 'add': {d: 0} } ));
	$('#dateEdD').val(ax5.util.date(new Date(), {'return': 'yyyy/MM/dd', 'add': {d: 0} } ));
}

function popClose(){
	window.parent.fileLength = 0;
	window.parent.fileUploadModal.close();
	window.parent.modal.close();
}

function notiClick() {
	if($("#exampleCheck1").is(":checked")){
		$('#divPicker').css('display','');
	}else {
		$('#divPicker').css('display','none');
	}
}

//공지사항 등록 및 수정
function update(){
	var TODATE = "";
	var monthStr = "";
	var dayStr = "";
	if($("#exampleCheck1").is(":checked")){
		stDate = replaceAllString($("#dateStD").val(), "/", "");
		edDate = replaceAllString($("#dateEdD").val(), "/", "");
	}
	
	if(document.getElementById("txtTitle").value == ""){
		dialog.alert('제목을 입력하십시오.', function () {});
	} else {
		if($("#textareaContents").val() == ""){
			dialog.alert('내용을 입력하십시오.', function () {});
		} else {
			if($("#exampleCheck1").is(":checked") && (edDate<stDate)){
				dialog.alert('날짜 등록이 잘못되었습니다.', function () {});
			} else {
				confirmDialog.confirm({
					msg: '등록하시겠습니까?',
				}, function(){
					if(this.key === 'ok') {
						updateHandler();
					}
				});
				
			}
		}
	}
}

function updateHandler(){
	var ajaxReturnData = null;
	var updateData = {};
	if(noticeInfo === null) {
		updateData.memo_id = '';
		updateData.user_id = window.top.userId;
	} else {
		updateData.memo_id = noticeInfo.CM_ACPTNO;
		updateData.user_id = noticeInfo.CM_EDITOR;
	}
	updateData.txtTitle = document.getElementById("txtTitle").value;
	updateData.textareaContents = $("#textareaContents").val();
	updateData.chkNotice = $('#exampleCheck1').prop("checked").toString();
	updateData.stDate = stDate;
	updateData.edDate = edDate;
	
	var tmpData = {
			requestType : 'Cmm2101_1',
			UserId : window.top.userId,
			dataObj : JSON.stringify(updateData)
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/modal/PopNotice', tmpData, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		
		// 첨부파일 존재시
		if(window.parent.fileLength > 1) {
			window.parent.uploadAcptno = ajaxReturnData;
			window.parent.fileUploadModal.restore();
			$('#btnStartUpload',window.parent.document.getElementsByName('ax5-modal-15-frame')[0].contentWindow.document).click();
		}
		dialog.alert('등록 되었습니다.', function () {
			window.parent.fileLength = 0;
			window.parent.fileUploadModal.close();
			window.parent.modal.close();
			window.parent.Search_click();
		});
	}
}


//파일첨부
function fileOpen() {
	if(noticeInfo !== null) {
		window.parent.downAcptno = noticeInfo.CM_ACPTNO;
		window.parent.downFileCnt = noticeInfo.fileCnt;
		window.parent.openFileDownload('','');
	} else {
		if(window.parent.checkModalLength() > 1) window.parent.fileUploadModal.restore();
		else window.parent.openFileUpload();
	}
}

function del() {
	confirmDialog.confirm({
		msg: '공지사항을 삭제하시겠습니까?',
	}, function(){
		if(this.key === 'ok') {
			
			stDate = replaceAllString($("#dateStD").val(), "/", "");
			edDate = replaceAllString($("#dateEdD").val(), "/", "");
			
			var delData = {};
			delData.memo_id = noticeInfo.CM_ACPTNO;
			delData.user_id = noticeInfo.CM_EDITOR;
			delData.txtTitle = document.getElementById("txtTitle").value;
			delData.textareaContents = $("#textareaContents").val();
			delData.chkNotice = $('#exampleCheck1').prop("checked").toString();
			delData.stDate = stDate;
			delData.edDate = edDate;
			var tmpData = {
					requestType : 'Cmm2101_2',
					UserId : window.top.userId,
					dataObj : JSON.stringify(delData)
			}
			
			ajaxReturnData = ajaxCallWithJson('/webPage/modal/PopNotice', tmpData, 'json');
			
			if(ajaxReturnData !== 'ERR') {
				dialog.alert('공지사항이 삭제되었습니다.', function () {
					window.parent.fileLength = 0;
					window.parent.fileUploadModal.close();
					window.parent.modal.close();
					window.parent.Search_click();
				});
			}
		}
	});
}