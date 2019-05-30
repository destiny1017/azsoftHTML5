<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />


<div class="row">
    <div class="col-sm-2">
	    <div class="col-sm-9">
			<div class="input-group" data-ax5picker="basic" style="padding-left: 10px; padding-top: 20px;">
	            <input id="basepicker" type="text" class="form-control" data-picker-date="year" placeholder="yyyy">
	            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
	        </div>    
	    </div>
	    <div class="col-sm-3" style="padding-top: 20px;">
	    	<label style="padding-top: 5px;">년도</label>
	    </div>
	</div>
	
</div>

<div class="row">
	<div class="container-fluid">
		<div data-ax5grid="holidayGrid" data-ax5grid-config="{showLineNumber: true, lineNumberColumnWidth: 40}" style="height: 60%; padding-top: 20px;"></div>
	</div>
</div>

<div class="row" style="padding-left: 10px; padding-top: 20px;">
	<div class="col-sm-2">
		<div class="col-sm-4">
			<label style="padding-top: 5px;">휴일</label>
		</div>
		<div class="col-sm-8">
			<div class="input-group" data-ax5picker="basic2">
	           <input id="holiday_date" type="text" class="form-control" placeholder="yyyy-mm-dd">
	           <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
	    	</div>
		</div>
	</div>
	
	
	<div class="col-sm-2">
		<div class="col-sm-4">
			<label style="padding-top: 5px;">휴일종류</label>
		</div>
		<div class="col-sm-8">
			<div class="form-group">
            	<div id="holi_cb" data-ax5select="holi_cb" data-ax5select-config="{}"></div>
        	</div>
		</div>
	</div>
</div>

<div class="row" style="padding-left: 10px; padding-top: 5px;">
    <div class="col-sm-2">
		<div class="col-sm-4">
			<label style="padding-top: 5px;">휴일구분</label>
		</div>
		<div class="col-sm-8">
			<div class="form-group">
            	<div id="holigb_cb" data-ax5select="holigb_cb" data-ax5select-config="{}"></div>
        	</div>
		</div>
	</div>
    
    
    <div class="col-sm-2">
    	<div class="col-lg-12 col-md-12 col-sm-12 col-12">
    		<button class="btn btn-default" id="reg_bt">등록</button>
	    	<button class="btn btn-default" id="del_bt">삭제</button>
	    </div>
	</div>
</div>



<c:import url="/js/ecams/common/commonscript.jsp" />
<script type="text/javascript" src="<c:url value="/js/ecams/administrator/HolidayInfo.js"/>"></script>