<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />
<style>
    #divContent {width: 380px; padding: 20px; border:1px solid black; margin: 0 auto}
    #myGrid1Area {width:100%; height:23%}
    #opt {width: 380px; margin: 0 auto}
    #btnReg {background: #656970; color:white; font-style:bold;}
    .row {margin-top: 10px}
</style>

	<section>
		<div class="container-fluid padding-40-top">
			<div id="opt">
				<label><input style="vertical-align: middle;" id="rdoOpt0" name="rdoOpt0" type="radio" value="0" onclick="changeBtnText()" checked />&nbsp;&nbsp;등록</label>
				<label><input style="vertical-align: middle;" id="rdoOpt1" name="rdoOpt0" type="radio" value="1" onclick="changeBtnText()"/>&nbsp;&nbsp;해제</label>
				<label id="lbTit"></label>
			</div>
		</div>
	</section>
		
	<section>
		<div class="container-fluid margin-15-top">
			<div id="divContent">
				<div class="row-fluid">
					<div class="row">
						<div class="col-xs-12 col-sm-4">
							<label id="lbUser">부 재 자</label>
						</div>
						<div class="col-xs-12 col-sm-8">
							<input id="txtUser" name="txtUser" class="input-sm width-100" onkeypress="Search_click1()"  disabled></input>
							 <div 	id="cboUser" data-ax5select="cboUser" data-ax5select-config="{size:'sm',theme:'primary'}" 
							 		style="width:100%;" onchange="Cbo_User_Click()"></div>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-12 col-sm-4">
							<label id="lbDaeSign">대결재자</label>
						</div>
						<div class="col-xs-12 col-sm-8">
							<input id="txtName" name="txtName" class="input-sm width-100" type="text" onkeypress="Search_click2()" disabled></input>
							<div 	id="cboDaeSign" data-ax5select="cboDaeSign" data-ax5select-config="{size:'sm',theme:'primary'}" 
							 		style="width:100%;" onchange="DaeSign_username_Set()"></div>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-12 col-sm-4">
							<label id="lbSayu">부재사유</label>
						</div>
						<div class="col-xs-12 col-sm-8">
							<div 	id="cboSayu" data-ax5select="cboSayu" data-ax5select-config="{size:'sm',theme:'primary'}" 
							 		style="width:100%;" onchange="Cbo_Sayu_Click()"></div>
							<input id="txtSayu" name="txtSayu" class="input-sm width-100" type="text"></input>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-12 col-sm-4">
							<label id="lbDate">부재기간</label>
						</div>
						<div class="col-xs-12 col-sm-8">
							<label id="lbFrom">FROM</label>
							<div class="input-group" data-ax5picker="basic" style="width:100%;">
								<input id="datStD" name="datStD" type="text" class="form-control" placeholder="yyyy/mm/dd">
					            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
							</div>
							<label id="lbTo">TO</label>
							<div class="input-group" data-ax5picker="basic2" style="width:100%;">
					            <input id="datEdD" name="datEdD" type="text" class="form-control" placeholder="yyyy/mm/dd">
					            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
							</div>
						</div>
					</div>
				</div>
				<div data-ax5grid="myGrid1Area" data-ax5grid-config="{showLineNumber: false, lineNumberColumnWidth: 40}" style="height: 150px;"></div>
			</div>
		</div>
	</section>
	
	<section>
		<div id="divPw" class="margin-15-top" style="text-align:center">
			<button id="btnReg" name="btnReg" onclick="cmd_click()">등록</button>
		</div>
	</section>
<c:import url="/js/ecams/common/commonscript.jsp" />
<script type="text/javascript" src="<c:url value="/js/ecams/mypage/AbsenceRegister.js"/>"></script>