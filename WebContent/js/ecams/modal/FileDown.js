/** File Download Javascript Page 
 * 
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-01-29
 */
var dialog 			= new ax5.ui.dialog({title: "확인"});
var confirmDialog 	= new ax5.ui.dialog();	//알럿,확인창
var downAcptno 		= null;
var downFileCnt 	= 0;
var noticeFolderPath= null;
var downFilelist 	= [];
var fileArr 		= null;
var fileInfo 		= null;
var uploadFileCnt 	= 0;
var uploadSucessCnt = 0;
var uploadSelectedFileLength = 0;

$(document).ready(function() {
	
	downAcptno  = window.parent.downAcptno;
	downFileCnt = window.parent.downFileCnt; 
	if(downFileCnt > 0 ) {
		getFileList();
	}
	
	confirmDialog.setConfig({
        title: "파일 업로드 알림",
        theme: "info"
    });
});

function getNoticeFolderPath() {
	var ajaxReturnData = null;
	var tmpData = {
		requestType : 'getNoticeFolderPath'
	}
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/Notice', tmpData, 'json');
	if(ajaxReturnData !== 'ERR') {
		noticeFolderPath = ajaxReturnData;
		getFileList();
	}
}

function getFileList() {
	var ajaxReturnData = null;
	var tmpData = {
		requestType : 'getFileList',
		acptno : downAcptno
	}
	
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/Notice', tmpData, 'json');
	if(ajaxReturnData !== 'ERR') {
		downFilelist = ajaxReturnData;
		
		downFilelist.forEach(function(item,itemIndex){
			/* 
			 * ecma script 6 버전 이게 더 깔끔하고 사용하기 편합니다.
			 * 다만 IE에서 사용불가..
			var appendStr = ``;
			appendStr += `<tr>`;
			appendStr += `	<td>`;
			appendStr += `		<a href="/webPage/fileupload/upload?f=${item.orgname}&noticeAcptno=${downAcptno}">${item.orgname}</a>`;
			appendStr += `		<button onclick="test('${item.orgname}')" class="btn btn-sm btn-danger cancel" role="button" style="float: right;">삭제</button>`;
			appendStr += `	</td>`;
			appendStr +=`</tr>`;*/
			
			var appendStr = '';
			appendStr += '<tr id="file_'+item.orgname+'">';
			appendStr += '	<td>';
			appendStr += '		<a href="/webPage/fileupload/upload?f='+item.orgname+'&noticeAcptno='+downAcptno+'">'+item.orgname+'</a>';
			appendStr += '		<button onclick="delFile(\''+item.orgname+'\')" class="btn btn-sm btn-danger cancel" role="button" style="float: right;">삭제</button>';
			appendStr += '	</td>';
			appendStr +='</tr>';
			$('#fileDownBody').append(appendStr);
		});
	}
}

function delFile(item) {
	confirmDialog.confirm({
		msg: '해당 파일을 삭제 하시겠습니까?',
	}, function(){
		if(this.key === 'ok') {
			var ajaxReturnData = null;
			var fileInfo = {};
			fileInfo.acptNo = downAcptno;
			fileInfo.fileName = item;
			var tmpData = {
				requestType : 'deleteNoticeFile',
				fileData : JSON.stringify(fileInfo)
			}
			
			ajaxReturnData = ajaxCallWithJson('/webPage/mypage/Notice', tmpData, 'json');
			
			if(ajaxReturnData !== 'ERR') {
				dialog.alert('파일이 삭제 되었습니다.', function () {});
				$('#fileDownBody').empty();
				getFileList();
			}
		}
	});
	
	
	
	
}




/*
 * For the sake keeping the code clean and the examples simple this file
 * contains only the plugin configuration & callbacks.
 * 
 * UI functions ui_* can be located in:
 *   - assets/demo/uploader/js/ui-main.js
 *   - assets/demo/uploader/js/ui-multiple.js
 *   - assets/demo/uploader/js/ui-single.js
 */
$('#drag-and-drop-zone').dmUploader({
	url: '/webPage/fileupload/upload',	// 	서블릿 주소
	maxFileSize: 1024*1024*1024*1, 		// 	최대 1gb 파일까지
	auto: false,						// 	파일 올렸을시 바로 업로드여부
	queue: false,						//	위에서부터 순서대로 파일 업로드 여부
	extraData: function() {				//	서블릿에 보낼 데이터
			return {
				"noticeAcptno": downAcptno
			};
	},
	onDragEnter: function(){
	    // Happens when dragging something over the DnD area
	    this.addClass('active');
	},
	onDragLeave: function(){
	    // Happens when dragging something OUT of the DnD area
	    this.removeClass('active');
	},
	onInit: function(){
		// Plugin is ready to use
	    ui_add_log('Penguin initialized :)', 'info');
	},
	onComplete: function(){
		// All files in the queue are processed (success or error)
		ui_add_log('All pending tranfers finished');
	},
	onNewFile: function(id, file){
		
		if(uploadFileCnt === 0) {
			fileArr = null;
			fileArr = [];
			
			var x = document.getElementById("ex_file");
			if ('files' in x) {
				if (x.files.length > 0) {
					uploadSelectedFileLength = x.files.length;
				}
			}
		}
		
		fileInfo = null;
		fileInfo = {"noticeAcptno":downAcptno,"fileName":file.name};
		fileArr.push(fileInfo);
		
		ui_multi_add_file(id, file);
		uploadFileCnt++;
		
		if(uploadFileCnt === uploadSelectedFileLength) {
			confirmDialog.confirm({
				msg: '해당 파일을 업로드 하시겠습니까?',
			}, function(){
				if(this.key === 'ok') {
					
					var id = $(this).closest('li.media').data('file-id');
					$('#drag-and-drop-zone').dmUploader('start', id);
					
					//DB에 업로드 파일 정보 저장.
					window.parent.fileInfoInsert(fileArr);
				}else{
					uploadSucessCnt = 0;
				}
				
				uploadFileCnt = 0;
				fileArr = null;
				fileInfo = null;
			});
		}
		
		
	},
	onBeforeUpload: function(id){	//	업로드 되기전
	},
	onUploadProgress: function(id, percent){
		ui_multi_update_file_progress(id, percent);
	},
	onUploadSuccess: function(id, data){	// 업로드 성공
		uploadSucessCnt++;
		
		if(uploadSucessCnt === uploadSelectedFileLength) {
			dialog.alert('파일이 업로드 되었습니다.', function () {
				$('#fileDownBody').empty();
				getFileList();
				uploadSucessCnt = 0;
				uploadSucessCnt = 0;
			});
		}
	},
	onUploadCanceled: function(id) {	// 업로드 취소시
	},
	onUploadError: function(id, xhr, status, message){	// 업로드시 에러
	},
	onFallbackMode: function(){
	},
	onFileSizeError: function(file){	// 파일사이즈 초과시
		ui_add_log('File \'' + file.name + '\' cannot be added: size excess limit', 'danger');
	}
});

//Creates a new file and add it to our list
function ui_multi_add_file(id, file)
{
	var template = $('#files-template').text();
	template = template.replace('%%filename%%', file.name);

	template = $(template);
	template.prop('id', 'uploaderFile' + id);
	template.data('file-id', id);

	$('#files').find('li.empty').fadeOut(); // remove the 'no files yet'
	$('#files').prepend(template);
}


