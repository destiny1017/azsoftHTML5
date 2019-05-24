<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="/webPage/common/common.jsp" />

<div class="row" id="divMainUp">
	<div class="col-lg-12 col-md-12">
		<div class="hpanel">
			<div class="panel-heading">
				<div class="row">
					<div class="col-lg-4 col-md-4 col-sm-6 col-6">
						<label id="applyLabel" style="padding-top: 5px;">최근 신청 목록</label>
					</div>
					<div class="col-lg-4 col-md-4 col-sm-6 col-6">
						<div style="float: right; padding-top: 5px;">
							<label class="wLabel-left"></label>
							<input id="radioAppliMy" tabindex="8" type="radio" name="radioAppli" value="myAppli" checked="checked"/>
							<label for="radioAppliMy" tooltip="본인이 신청한 목록이 보여집니다." flow="down">본인 신청건 보기</label>
							<input id="radioAppliTeam" tabindex="8" type="radio" name="radioAppli" value="teamAppli" />
							<label for="radioAppliTeam" tooltip="소속팀이 신청한 목록이 보여집니다." flow="down">팀 신청건 보기</label>
						</div>
					</div>
					<div class="col-lg-1 col-md-1 col-sm-6 col-6">
						<label 	style="float: right; padding-top: 5px;" 
								tooltip="신청기간에 따른 신청 목록이 보여집니다." flow="down">
       							신청일시
    					</label>
					</div>
					<div class="col-lg-3 col-md-3 col-sm-6 col-6">
						<div id="divPicker" class="input-group" data-ax5picker="basic">
				            <input id="start_date" name="start_date" type="text" class="form-control" placeholder="yyyy/mm/dd">
							<span class="input-group-addon">~</span>
							<input id="end_date" name="end_date" type="text" class="form-control" placeholder="yyyy/mm/dd">
				            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
						</div>
					</div>
				</div>
			</div>
			<div>
				<div data-ax5grid="applyGrid" data-ax5grid-config="{showLineNumber: true, lineNumberColumnWidth: 40}" style="height: 36%;"></div>
			</div>
		</div>
	</div>
</div>

<div class="row" id="divMainDown">
	<div class="col-lg-4 col-md-6 col-sm-12">
		<div class="hpanel">
			
			<div class="panel-heading">
                <div class="panel-tools">
					<input type="checkbox" tabindex="61" class="checkbox-pie" id="checkbox-pieA" data-label="반려 제외" checked="checked" />
                </div>
                <label id="pieTLabel_A">신청 종류</label>
            </div>
		    <div class="panel-body text-center" id="pieDiv">
		    	<div id="pieAppliKinds" tooltip="형상관리 신청 종류를 보여줍니다." flow="down"></div>
		    </div>
		</div>	
	</div>
	
	<div class="col-lg-4 col-md-6 col-sm-12">
		<div class="hpanel">
			<div class="panel-heading">
	               <div class="panel-tools">
						<input type="checkbox" tabindex="61" class="checkbox-pie" id="checkbox-pieP" data-label="반려 제외" checked="checked" />
	               </div>
	               <label id="pieTLabel_P">프로그램별 종류</label>
	           </div>
		
		    <div class="panel-body text-center" id="piePDiv">
		    	<div id="piePChart" tooltip="형상관리 신청 프로그램 종류를 보여줍니다." flow="down"></div>
		    </div>
		</div>	
	</div>
	
	<div class="col-lg-4 col-md-12 col-sm-12">
		<div class="hpanel">
			<div class="panel-heading">
            	<label id="barTLabel">관리자님 바차트</label>
            </div>
		    <div class="panel-body text-center" id="barDiv">
		    	<div id="barChart" tooltip="시스템별 신청건수입니다(폐기 시스템제외)" flow="down"></div>
		    </div>
		</div>	
	</div>
	
</div>

<div class="row">
	
</div>
 
<c:import url="/js/ecams/common/commonscript.jsp" />
<script type="text/javascript" src="<c:url value="/js/ecams/main/eCAMSMain.js"/>"></script>