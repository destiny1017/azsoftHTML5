<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="<c:url value="/css/ecams/common/ecamsStyle.css" />">
<!-- Bootstrap core CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.3/css/bootstrap.min.css" integrity="sha384-Zug+QiDoJOrZ5t4lssLdxGhVrurbmBWopoEl+M6BdEfwnCJZtKxi1KgxUyJq13dy" crossorigin="anonymous"><!-- ax5ui script -->

<!-- Custom styles -->
<link href="<c:url value="/styles/fileupload/jquery.dm-uploader.min.css"/>" rel="stylesheet">
<link href="<c:url value="/styles/fileupload/styles.css"/>" rel="stylesheet">
<link rel="stylesheet" href="<c:url value="/styles/ax5/ax5dialog.css"/>">
<style>
	.filebox label { display: inline-block; padding: .5em .75em; color: #999; font-size: inherit; line-height: normal; vertical-align: middle; background-color: #fdfdfd; cursor: pointer; border: 1px solid #ebebeb; border-bottom-color: #e2e2e2; border-radius: .25em; } 
	.filebox input[type="file"] { /* 파일 필드 숨기기 */ position: absolute; width: 1px; height: 1px; padding: 0; margin: -1px; overflow: hidden; clip:rect(0,0,0,0); border: 0; }
	body {
		overflow: hidden;
	}
</style>



<div class="row">
		<!-- 
		<div id="drag-and-drop-zone" class="dm-uploader p-5 text-center">
			<h3 class="mb-5 mt-5 text-muted">Drag &amp; drop Files here</h3>
			<div class="btn btn-primary btn-block mb-5">
				<span>Open the file Browser</span>
				<input type="file" title='Click to add Files' />
			</div>
		</div>  -->
		<!-- /uploader -->
	<div class="no-padding col-md-6 col-sm-12" style="margin-left: 10px; margin-right: 10px;">
		<div class="card">
			<ul class="list-unstyled p-2 d-flex flex-column col" id="files" style="height: 150px; overflow: scroll;">
				<li class="text-muted text-center empty">No files uploaded.</li>
			</ul>
		</div>
	</div>
</div><!-- /file list -->

<div class="row">
	<div class="no-padding col-md-6 col-sm-12 " style="margin-left: 10px; margin-right: 10px;">
		<!-- <a href="#" class="btn btn-primary" id="btnApiStart">
			<i class="fa fa-play"></i> Start
		</a>
		<a href="#" class="btn btn-danger" id="btnApiCancel">
			<i class="fa fa-stop"></i> Stop
		</a> -->
		<button class="btn" style="float: right; background-color: #fff; border-color: #e4e5e7; color: #6a6c6f;" onclick="window.parent.fileUploadModal.minimize('bottom-right');">선택완료</button>
		<div class="filebox" id="drag-and-drop-zone" style="display: inline-block; float: right;"> 
			<label for="ex_file">업로드</label> 
			<input type="file" id="ex_file"> 
		</div>
	</div>
	
	<div class="col-sm-3">
		<button  id="btnStartUpload" class="btn btn-sm btn-primary start" role="button" style="display:none;">Start</button>
	</div>
	<div class="col-sm-3">
	</div>
</div>	


<!-- <button id="btnStartUpload" type="button" style="display: none;">start</button> -->


<!-- File item template -->
<script type="text/html" id="files-template">
<li class="media">
	<div class="media-body mb-1">
		<p>
			<strong>%%filename%%</strong> - Status: <span class="text-muted">Waiting</span>
			<button href="#" class="btn btn-sm btn-danger cancel" role="button" style="float: right;">삭제</button>
		</p>
		<div class="progress mb-2">
        	<div class="progress-bar progress-bar-striped progress-bar-animated bg-primary" role="progressbar" style="width: 0%" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">
        	</div>
      	</div>
		<hr class="mt-1 mb-1" />
	</div>
</li>
</script>

  <li class="media">
    
    <div class="media-body mb-1">
      <p class="mb-2">
        <strong>%%filename%%</strong> - Status: <span class="text-muted">Waiting</span>
      </p>
      <div class="progress mb-2">
        <div class="progress-bar progress-bar-striped progress-bar-animated bg-primary" 
          role="progressbar"
          style="width: 0%" 
          aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">
        </div>
      </div>
      <hr class="mt-1 mb-1" />
    </div>
  </li>



<!-- 프로그레스바
<div class="progress mb-2">
	<div class="progress-bar progress-bar-striped progress-bar-animated bg-primary" 
				role="progressbar"
				style="width: 0%" 
				aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">
	</div>
</div>
-->
<!--  파일리스트 각 줄의 start cancel 버튼
<p class="controls mb-2">
	<button href="#" class="btn btn-sm btn-primary start" role="button">Start</button>
	<button href="#" class="btn btn-sm btn-danger cancel" role="button" disabled="disabled">Cancel</button>
</p> 
-->

<c:import url="/js/ecams/common/commonscript.jsp" />
<script type="text/javascript" src="<c:url value="/js/ecams/modal/FileUp.js"/>"></script>