<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<style>
</style>

<section>
	<div class="container-fluid padding-40-top">
		<div  class="border-style-black">
			<div class="row">
				<div class="col-sm-3">
					<div id="divPicker" class="input-group" data-ax5picker="basic">
			            <input id="start_date" name="start_date" type="text" class="form-control" placeholder="yyyy/mm/dd">
						<span class="input-group-addon">~</span>
						<input id="end_date" name="end_date" type="text" class="form-control" placeholder="yyyy/mm/dd">
			            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
					</div>
				</div>
				<div class="col-sm-3 no-padding">
					<input 	id="Txt_Find" name="Txt_Find" class="form-control width-100" type="text" 
							onkeypress="if(event.keyCode==13) {Search_click();}"  
							placeholder="제목/내용 입력후 조회"></input>
				</div>
				
				<div class="col-sm-1 no-padding">
					<button id="Search_Data" name="Search_Data" class="btn btn-default" onclick="Search_click()">조  회</button>
				</div>
				
				<div class="col-sm-3 col-sm-offset-2" >
					<div class="col-sm-5 no-padding">
						<button id="btnReg" name="btnReg" class="btn btn-default width-100" onclick="new_Click()" disabled >공지사항등록</button>
					</div>
					<div class="col-sm-5 no-padding">
			      		<button id="sysPath" name="sysPath"  class="btn btn-default width-100" onclick="sysPathButton_Click()">Excel저장</button>
					</div>
					<div class="col-sm-2 no-padding">
			      		<label id="lbCnt" class="margin-10-top" style="float: right;">총 0건</label>
					</div>
				</div>
	      		
			</div>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid">
		<div data-ax5grid="divGrid1" data-ax5grid-config="{showLineNumber: true, lineNumberColumnWidth: 40}" style="height: 80%"></div>
	</div>
</section>

<%-- <modal id="modalPopWin" name="modalPopWin" uitype="middle" header-title="공지사항 등록" body-html-id="popupBody">
   			</modal>
   			<div id="popupBody">
				<IFRAME id="popWin" src="<c:url value="/webPage/modal/PopNotice.jsp"/>" width="564" height="480"></IFRAME>
			</div>  --%>

<c:import url="/js/ecams/common/commonscript.jsp" />
<script type="text/javascript" src="<c:url value="/js/ecams/mypage/Notice.js"/>"></script>