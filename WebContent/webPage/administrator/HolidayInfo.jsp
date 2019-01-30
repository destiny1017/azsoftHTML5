<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />



<style type="text/css">
	.row{margin:0px;}
				
	.col-xs-1, .col-sm-1, .col-md-1, .col-lg-1, .col-xs-2, .col-sm-2, .col-md-2, .col-lg-2, 
	.col-xs-3, .col-sm-3, .col-md-3, .col-lg-3, .col-xs-4, .col-sm-4, .col-md-4, .col-lg-4, 
	.col-xs-5, .col-sm-5, .col-md-5, .col-lg-5, .col-xs-6, .col-sm-6, .col-md-6, .col-lg-6, 
	.col-xs-7, .col-sm-7, .col-md-7, .col-lg-7, .col-xs-8, .col-sm-8, .col-md-8, .col-lg-8, 
	.col-xs-9, .col-sm-9, .col-md-9, .col-lg-9, .col-xs-10, .col-sm-10, .col-md-10, .col-lg-10, 
	.col-xs-11, .col-sm-11, .col-md-11, .col-lg-11, .col-xs-12, .col-sm-12, .col-md-12, .col-lg-12 {
		padding:0px;
	}
	
	.ui-menu-item{ height: auto !important;}
	
	.sb-selectbox{padding:5px 8px 4px 10px !important;}
	
	@media (min-width: 768px){
		.col-dt{
			width: 40%;
		}
		.col-wave{
			width: 20%;
		}
	}
</style>

<sbux-picker id="picker_year" name="picker_year" uitype="date" mode="popup"
   	date-format="yyyy" datepicker-mode="year" style="padding-left:30px;" onchange="year_change()">
</sbux-picker>

<div id="sbGridArea" style="width:30%; height:450px; padding-left:30px;"></div>

<div class="row" style="height:30px; clear: both; background-color:#fafafa;">
	<div class="col-xs-4 col-sm-1" style="padding-top:18px; text-align:center;">
		<sbux-label id="idx_lab_date" text="휴일&nbsp;&nbsp;" uitype="normal"></sbux-label>
	</div>
	<div class="col-xs-4 col-sm-1" style="padding-top:11px; text-align:center;">
		<sbux-picker id="picker_date" name="picker_date" uitype="date" mode="popup" date-format="yyyy/mm/dd"></sbux-picker>
	</div>
	
	<div class="col-xs-4 col-sm-1" style="padding-top:18px; text-align:center;">
		<sbux-label id="idx_lab_case" text="휴일종류&nbsp;&nbsp;" uitype="normal"></sbux-label>
	</div>
	
	<div class="col-xs-4 col-sm-1" style="padding-top:18px; text-align:center;">
		<sbux-select id="idxSelectChk_case" name="selectchk_case" uitype="single"
              jsondata-ref="holidayCaseData"
              jsondata-text = "text"
              jsondata-value = "value"
              title-select-max-count = "1"
              is-select-all-option = "false"
              storage-data ="value"
              scroll-style="min-height: 140px;"
              style="width:100%;" 
              auto-unselected-text="false"
            >
		</sbux-select>
	</div>
</div>

<div class="row" style="height:30px; clear: both; background-color:#fafafa;">
	<div class="col-xs-4 col-sm-1" style="padding-top:18px; text-align:center;">
		<sbux-label id="idx_lab_gbn" text="휴일구분&nbsp;&nbsp;" uitype="normal"></sbux-label>
	</div>
	
	<div class="col-xs-4 col-sm-1" style="padding-top:18px; text-align:center;">
		<sbux-select id="idxSelectChk_gbn" name="selectchk_gbn" uitype="single"
              jsondata-ref="holidaygbnData"
              jsondata-text = "text"
              jsondata-value = "value"
              title-select-max-count = "1"
              is-select-all-option = "false"
              storage-data ="value"
              scroll-style="min-height: 140px;"
              style="width:100%;" 
              auto-unselected-text="false"
            >
		</sbux-select>
	</div>
	
	<div class="col-xs-4 col-sm-1" style="padding-top:18px; text-align:center;">
		<div class="row">
			<span> 
				<sbux-button id="idx_button_Regist" name="button_Regist" uitype="modal" text="등록"
					style="background: #0077c1; border:none; vertical-align:middle; color: #fff; padding:5px 10px; font-size: 13px; height:auto; 
					position: relative; bottom: 2px; width:50%; height:100%" button-size="big" onclick="Regist_Click()">
				</sbux-button>
			</span>
			
			<span>
				<sbux-button id="idx_button_Delete" name="button_Delete" uitype="modal" text="삭제"
					style="background: #0077c1; border:none; vertical-align:middle; color: #fff; padding:5px 10px; font-size: 13px; height:auto; 
					position: relative; bottom: 2px; width:50%; height:100%" button-size="big" onclick="Delete_Click()">
				</sbux-button>
			</span>
		</div>
	</div>
</div>

<sbux-modal id="idxmodal_dupchk" name="modal_dupchk" uitype="small" footer-html-id="modalFooter" footer-close-text="NO" 
	body-html="<p>이미 휴일로 지정되었습니다. 수정하시겠습니까?</p>">
</sbux-modal>

<div id="modalFooter">
    <sbux-button id="btnUpdt" name="btnUpdt" uitype="normal" text="YES" button-size="small" button-color="default" onclick="addHoliday('1')"></sbux-button>
</div>

<sbux-modal id="idxmodal_delconfirm" name="modal_delconfirm" uitype="small" footer-html-id="modalFooter2" footer-close-text="NO" 
	body-html="<p>휴일에서 폐기하시겠습니까?</p>">
</sbux-modal>

<div id="modalFooter2">
    <sbux-button id="btnDel" name="btnDel" uitype="normal" text="YES" button-size="small" button-color="default" onclick="deleteHoliday()"></sbux-button>
</div>

<script type="text/javascript" src="<c:url value="/js/ecams/administrator/HolidayInfo.js"/>"></script>