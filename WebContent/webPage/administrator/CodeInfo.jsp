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
    
<!-- #page_title -->
<div id="page_title">
	<h1>코드조회</h1>
</div>
<!-- //#page_title -->
   
   </head>
   <body>

<form id="frm" name="frm" method="post">
  	<div style="margin:30px 5px 0;">
		<div class="container-fluid">
			<div class="row-fluid">
			
				<div class="col-xs-12">
					<div class="row" style="height:30px; clear: both; background-color:#fafafa;">
						<div class="col-xs-12 col-sm-1" style="padding-top:11px; text-align:center;">
							<sbux-label id="idx_lab_gbn" text="코드구분&nbsp;&nbsp;" uitype="normal">
							</sbux-label>
						</div>
						<div class="col-xs-12 col-sm-2" style="padding-top:11px; text-align:center;">
							<sbux-label id="idx_lab_code" text="코드값&nbsp;&nbsp;" uitype="normal">
							</sbux-label>
						</div>
						
						<div class="col-xs-12 col-sm-4" style="padding-top:11px; text-align:center;">
							<sbux-label id="idx_lab_comment" text="설명&nbsp;&nbsp;" uitype="normal">
						</div>
					</div>
					
					<div class="row" style="height:30px; clear: both; background-color:#fafafa;">
						<div class="col-xs-12 col-sm-1" style="padding-top:11px; background-color:#fafafa; text-align:center; border-bottom: 1px solid #ececec;">
							<sbux-label id="idx_lab_search_condition1" text="대구분&nbsp;&nbsp;" uitype="normal"/>
						</div>
						<div class="col-xs-12 col-sm-2" style="padding-top:2px;">
							<sbux-input id="idx_search_macode" name="search_macode" uitype="text" datastore-id="idxData1" onkeyenter="code_Enter('00')"
							 sbux-popover-title="<font color='yellow'>대구분</font>"
							 sbux-popover="<font color='blue' style='font-weight:bold;'>대구분 코드</font>를 입력<br>"
							 sbux-popover-placement="top" sbux-popover-html="true"/>
						</div>
						
						<div class="col-xs-12 col-sm-4" style="padding-top:2px;">
							<sbux-input id="idx_search_macodecmt" name="search_macodecmt" uitype="text" datastore-id="idxData1" autocomplete-ref="autoCompleteData2"
							 sbux-tooltip="<font color='yellow' style='font-weight: bold;'>대구분 코드설명</font>을 입력하세요<br>"
							sbux-tooltip-placement="top" sbux-tooltip-html="true"/>
						</div>
						
						<div class="col-xs-12 col-sm-1" style="padding-top:11px; background-color:#fafafa; text-align:center; border-bottom: 1px solid #ececec;">
							<sbux-label id="idx_lab_search_condition3" text="검색조건&nbsp;&nbsp;" uitype="normal">
							</sbux-label>
						</div>
						<div class="col-xs-12 col-sm-2" style="padding-top:2px;">
							<sbux-select id="idxSelectChk_codeInfo" name="selectchk_codeInfo" uitype="single"
			                  jsondata-ref="codeInfoData"
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
						
						<div class="col-xs-12 col-sm-2" style="padding-top:2px; padding-left:2px;">
							<sbux-button id="idx_button_jobinfo" name="button_jobinfo" uitype="modal" text="업무정보"
								style="background: #0077c1; border:none; vertical-align:middle; color: #fff; padding:5px 10px; font-size: 13px; height:auto; 
								position: relative; bottom: 2px; width:95px; height:100%" button-size="big" target-id="idxmodal_middle">
							</sbux-button>
						</div>
					</div>
					
					<div class="row" style="height:30px; clear: both; background-color:#fafafa;">
						<div class="col-xs-12 col-sm-1" style="padding-top:11px; background-color:#fafafa; text-align:center; border-bottom: 1px solid #ececec;">
							<sbux-label id="idx_lab_search_condition4" text="소구분&nbsp;&nbsp;" uitype="normal" >
							</sbux-label>
						</div>
						<div class="col-xs-12 col-sm-2" style="padding-top:2px;">
							<sbux-input id="idx_search_micode" name="search_micode" uitype="text" datastore-id="idxData1" autocomplete-ref="autoCompleteData"
							sbux-popover-title="<font color='yellow'>소구분</font>"
								sbux-popover="<font color='blue' style='font-weight:bold;'>소구분 코드</font>를 입력<br>"
								sbux-popover-placement="top" sbux-popover-html="true"
							></sbux-input>
						</div>
						
						<div class="col-xs-12 col-sm-4" style="padding-top:2px;">
							<sbux-input id="idx_search_micodecmt" name="search_micodecmt" uitype="text" datastore-id="idxData1" autocomplete-ref="autoCompleteData2"
							 sbux-tooltip="<font color='yellow' style='font-weight: bold;'>소구분 코드설명</font>을 입력하세요<br>"
								sbux-tooltip-placement="top" sbux-tooltip-html="true"></sbux-input>
						</div>
						
						<div class="col-xs-12 col-sm-1" style="padding-top:11px; background-color:#fafafa; text-align:center; border-bottom: 1px solid #ececec;">
							<sbux-label id="idx_lab_search_condition7" text="소구분순서&nbsp;&nbsp;" uitype="normal">
							</sbux-label>
						</div>
						<div class="col-xs-12 col-sm-1" style="padding-top:2px;">
							<sbux-input id="idx_search_sqeno" name="search_seqno" uitype="text" datastore-id="idxData1" autocomplete-ref="autoCompleteData2"
							 sbux-tooltip="<font color='yellow' style='font-weight: bold;'>소구분 순서</font>을 입력하세요<br>"
							 sbux-tooltip-placement="top" sbux-tooltip-html="true">
							 </sbux-input>
						</div>
						<div class="col-xs-12 col-sm-1" style="padding-top:2px;">
							<sbux-select id="idxSelectChk_useyn" name="selectchk_useyn" uitype="single"
			                  jsondata-ref="UseData"
			                  jsondata-text = "text"
			                  jsondata-value = "value"
			                  title-select-max-count = "1"
			                  is-select-all-option = "false"
			                  storage-data ="value"
			                  scroll-style="min-height: 140px;"
			                  style="width:100%;" 
			                  auto-unselected-text="false"
			                >
						</div>
						<div class="col-xs-12 col-sm-1" style="padding-top:2px; padding-left:2px;">
							<sbux-button id="idx_button_search" name="button_search" uitype="normal" text="조회"
								style="background: #0077c1; border:none; vertical-align:middle; color: #fff; padding:5px 10px; font-size: 13px; height:auto; position: relative; bottom: 2px;"
								onclick="code_Enter('')"
								button-size="big">
							</sbux-button>
							
							<sbux-button id="idx_button_set" name="button_set" uitype="normal" text="적용"
								style="background: #0077c1; border:none; vertical-align:middle; color: #fff; padding:5px 10px; font-size: 13px; height:auto; position: relative; bottom: 2px;"
								onclick="set_Click()"
								button-size="big">
							</sbux-button>
						</div>
						
					</div>
				</div>
			</div>
		</div>
	</div>
</form>

<div id="sbGridArea" style="margin:0 20px; height:700px;"></div>


<sbux-modal id="idxmodal_middle" name="modal_middle" uitype="middle" header-title="업무정보" 
			body-html-id="modalBody" footer-is-close-button="false">
</sbux-modal>
<div id="modalBody">
	<iframe id="ifrm" name="ifrm" src="<c:url value="/webPage/modal/TaskInfo.jsp"/>" 
			width="100%" height="590px" 
			frameborder="0" border="0" 
			scrolling="no" marginheight="0" marginwidth="0">
	</iframe>
</div>

<script type="text/javascript" src="<c:url value="/js/ecams/administrator/CodeInfo.js"/>"></script>
	