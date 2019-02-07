<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<style>
	   .sbgrid-form #select_option { width: 98%; height: 100px; text-align: center;border: #757575 solid 2px;background-color: #fafafa; margin: 5px 10px 5px 15px;}
	   .sbgrid-form #select_option tr td{width: 5%;}
	   .sbgrid-form #inputData { width: 98%; height: 100%; text-align: center;border: #757575 solid 2px;background-color: #fafafa; margin: 5px 10px 5px 15px;}
	   .sbgrid-form #inputData tr td{ width: 5%; border: #757575 solid 2px;}
	   
	   label{margin-top: 5px;}
	   button{width:80px;}
	   
	  	#sbGridArea{width: 100%; height:500px;}
	  
	   .sbgrid-tem-contents{width : 100%; height:100%;}
	   #titleBar{background-color: #E0F8F7; width:100%; height: 2%;}	   
</style>

<section>
	<div class="container-fluid" style="border: #757575 solid 2px;  margin: 0px 15px 0px 15px; padding: 5px 0px 5px 0px;  overflow:hidden;">
		<div class="row-fluid">
			<div class="row">
				<div class="col-xs-12 col-sm-1">
					<sbux-label id="lbSysCd" name="lbSysCd" uitype="normal" text="시스템"></sbux-label>
				</div>
				<div class="col-xs-12 col-sm-2">
						<sbux-select id="cboSyscd" name="cboSyscd" uitype="single" jsondata-ref = "" jsondata-text="" jsondata-value=""  style="width:100%;"></sbux-select>				
				</div>
			</div>
		</div>
	</div>
</section>