var dialog = new ax5.ui.dialog({title: "확인"});
var uploadCnt = 0;
var fileArr = [];
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
				"noticeAcptno": window.parent.uploadAcptno
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
		// When a new file is added using the file selector or the DnD area
		ui_add_log('New file added #' + id);
		ui_multi_add_file(id, file);
	},
	onBeforeUpload: function(id){	//	업로드 되기전
		// about tho start uploading a file
		ui_add_log('Starting the upload2 of #' + id);
		ui_multi_update_file_status(id, 'uploading', 'Uploading...');
		ui_multi_update_file_progress(id, 0, '', true);
		ui_multi_update_file_controls(id, false, true);  // change control buttons status
		++uploadCnt;
	},
	onUploadProgress: function(id, percent){
		// Updating file progress
		ui_multi_update_file_progress(id, percent);
	},
	onUploadSuccess: function(id, data){	// 업로드 성공
		// A file was successfully uploaded
		ui_add_log('Server Response for file #' + id + ': ' + JSON.stringify(data));
		ui_add_log('Upload of file #' + id + ' COMPLETED', 'success');
		ui_multi_update_file_status(id, 'success', 'Upload Complete');
		ui_multi_update_file_progress(id, 100, 'success', false);
		ui_multi_update_file_controls(id, false, false);  // change control buttons status
		--uploadCnt;
		console.log("data : "+JSON.stringify(data));
		//m_acptno,cm_gbncd,cm_seqno,cm_attfile,cm_svfile
		/*var fileMeta = new Object();
		fileMeta.cm_svfile = */
		
		fileArr.push(data[0]);
		
		if(uploadCnt === 0 ) {
			dialog.alert('파일 업로드 완료.', function () {
				window.parent.fileUploadModal.close();
				
				//DB에 업로드 파일 정보 저장.
				window.parent.fileInfoInsert(fileArr);
				
	    		window.parent.modal.close();
	    		window.parent.Search_click();
			});
		}
	},
	onUploadCanceled: function(id) {	// 업로드 취소시
		// Happens when a file is directly canceled by the user.
		ui_multi_update_file_status(id, 'warning', 'Canceled by User');
		ui_multi_update_file_progress(id, 0, 'warning', false);
		ui_multi_update_file_controls(id, true, false);
	},
	onUploadError: function(id, xhr, status, message){	// 업로드시 에러
		// Happens when an upload error happens
		ui_multi_update_file_status(id, 'danger', message);
		ui_multi_update_file_progress(id, 0, 'danger', false);  
		ui_multi_update_file_controls(id, true, false, true); // change control buttons status
	},
	onFallbackMode: function(){
		// When the browser doesn't support this plugin :(
		ui_add_log('Plugin cant be used here, running Fallback callback', 'danger');
	},
	onFileSizeError: function(file){	// 파일사이즈 초과시
		ui_add_log('File \'' + file.name + '\' cannot be added: size excess limit', 'danger');
	}
});

/*
	전체파일 업로드 시작 또는 취소시
*/
$('#btnApiStart').on('click', function(evt){
	evt.preventDefault();
	$('#drag-and-drop-zone').dmUploader('start');
});

$('#btnApiCancel').on('click', function(evt){
	evt.preventDefault();
	$('#drag-and-drop-zone').dmUploader('cancel');
});

/*
  	각 파일 업로드 시작 및 취소 및 삭제시
 */
$('#files').on('click', 'button.start', function(evt){
	evt.preventDefault();
	var id = $(this).closest('li.media').data('file-id');
  	$('#drag-and-drop-zone').dmUploader('start', id);
});

$('#files').on('click', 'button.cancel', function(evt){
	evt.preventDefault();
	var id = $(this).closest('li.media').data('file-id');
	$('#drag-and-drop-zone').dmUploader('cancel', id);
});

$('#files').on('click', 'button.delete', function(evt){
	evt.preventDefault();
	$(this).closest('li.media').remove();
	if(checkFileLiLength() === 1) $('#files').find('li.empty').fadeIn();
	window.parent.fileLength = checkFileLiLength();
});

$('#btnStartUpload').on('click',function(evt) {
	evt.preventDefault();
	var id = $(this).closest('li.media').data('file-id');
	$('#drag-and-drop-zone').dmUploader('start', id);
});

function checkFileLiLength() {
	return $('#files li').length;
}

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
  
	window.parent.fileLength = checkFileLiLength();
}

// Changes the status messages on our list
function ui_multi_update_file_status(id, status, message)
{
  $('#uploaderFile' + id).find('span').html(message).prop('class', 'status text-' + status);
}

// Updates a file progress, depending on the parameters it may animate it or change the color.
function ui_multi_update_file_progress(id, percent, color, active)
{
	color = (typeof color === 'undefined' ? false : color);
	active = (typeof active === 'undefined' ? true : active);

	var bar = $('#uploaderFile' + id).find('div.progress-bar');

	bar.width(percent + '%').attr('aria-valuenow', percent);
	bar.toggleClass('progress-bar-striped progress-bar-animated', active);

	if (percent === 0){
		bar.html('');
	} else {
		bar.html(percent + '%');
	}

	if (color !== false){
		bar.removeClass('bg-success bg-info bg-warning bg-danger');
    	bar.addClass('bg-' + color);
	}
}

// Toggles the disabled status of Star/Cancel buttons on one particual file
function ui_multi_update_file_controls(id, start, cancel, wasError)
{
	wasError = (typeof wasError === 'undefined' ? false : wasError);

	$('#uploaderFile' + id).find('button.start').prop('disabled', !start);
	$('#uploaderFile' + id).find('button.cancel').prop('disabled', !cancel);

	if (!start && !cancel) {
		$('#uploaderFile' + id).find('.controls').fadeOut();
	} else {
		$('#uploaderFile' + id).find('.controls').fadeIn();
	}

	if (wasError) {
		$('#uploaderFile' + id).find('button.start').html('Retry');
	}
}


/*
 * 기본 베이직 버전 
 $(function(){
  
   * For the sake keeping the code clean and the examples simple this file
   * contains only the plugin configuration & callbacks.
   * 
   * UI functions ui_* can be located in: demo-ui.js
   
  $('#drag-and-drop-zone').dmUploader({ //
    //url: 'backend/upload.php',
	url:  '/webPage/fileupload/upload',
    maxFileSize: 3000000, // 3 Megs 
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
      // When a new file is added using the file selector or the DnD area
      ui_add_log('New file added #' + id);
      ui_multi_add_file(id, file);
    },
    onBeforeUpload: function(id){
      // about tho start uploading a file
      ui_add_log('Starting the upload of #' + id);
      ui_multi_update_file_status(id, 'uploading', 'Uploading...');
      ui_multi_update_file_progress(id, 0, '', true);
    },
    onUploadCanceled: function(id) {
      // Happens when a file is directly canceled by the user.
      ui_multi_update_file_status(id, 'warning', 'Canceled by User');
      ui_multi_update_file_progress(id, 0, 'warning', false);
    },
    onUploadProgress: function(id, percent){
      // Updating file progress
      ui_multi_update_file_progress(id, percent);
    },
    onUploadSuccess: function(id, data){
      // A file was successfully uploaded
      ui_add_log('Server Response for file #' + id + ': ' + JSON.stringify(data));
      ui_add_log('Upload of file #' + id + ' COMPLETED', 'success');
      ui_multi_update_file_status(id, 'success', 'Upload Complete');
      ui_multi_update_file_progress(id, 100, 'success', false);
    },
    onUploadError: function(id, xhr, status, message){
      ui_multi_update_file_status(id, 'danger', message);
      ui_multi_update_file_progress(id, 0, 'danger', false);  
    },
    onFallbackMode: function(){
      // When the browser doesn't support this plugin :(
      ui_add_log('Plugin cant be used here, running Fallback callback', 'danger');
    },
    onFileSizeError: function(file){
      ui_add_log('File \'' + file.name + '\' cannot be added: size excess limit', 'danger');
    }
  });
});*/

