<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />
<style>
.white-space-none { white-space:pre-wrap !important;}

@media (min-width: 768px) {
	.padding-1-left-web {padding-left:1;}
	.padding-1-right-web {padding-right:1;}
	
	.height-35px-web {height:35px;}
}
</style>
<section>
	<div class="container-fluid">
		<div class="border-style-black">
			<div class="row-fluid">
				<div class="col-xs-12 col-sm-1"> 
					<div class="margin-15-top">
						<sbux-label id="idx_lbl_sys" class="width-100" text="*시스템" uitype="normal"></sbux-label>
					</div>
				</div>
				<div class="col-xs-12 col-sm-3">
					<div class="margin-5-top">
						<sbux-select 
							id="select_system" name="select_system" class="combo-height width-100" uitype="single" model-name="select_system" 
							jsondata-text  = "cm_sysmsg" jsondata-value = "cm_syscd"	scroll-style="min-height: 200px;"
							required jsondata-ref="selectedSysData" onchange="changeSysCombo()"></sbux-select>
					</div>
				</div>
				<div class="col-xs-12 col-sm-1">
					<div class="margin-15-top">
						<sbux-label id="idx_lbl_rsrc" class="width-100" text="*프로그램종류" uitype="normal"></sbux-label>
					</div>
				</div>
				<div class="col-xs-12 col-sm-2 padding-1-right-web">
					<div class="margin-5-top">
						<sbux-select id="select_rsrc" name="select_rsrc" class="combo-height width-100" uitype="single" model-name="select_rsrc"
		                  	jsondata-text = "cm_codename" jsondata-value = "cm_micode" scroll-style="min-height: 120px;"
		                  	required jsondata-ref="cboRsrcData" onchange="changeRsrcdCombo()"></sbux-select>
					</div>
				</div>
				<div class="col-xs-12 col-sm-2 padding-1-left-web">
					<div class="margin-5-top">
						<sbux-input  id="idx_exeName_text" name="idx_exeName_text" class="width-100" uitype="text" readonly></sbux-input>
					</div>
				</div>
				<div class="col-xs-12 col-sm-1"> 
					<div class="margin-15-top">
						<sbux-label  id="idx_lbl_job" class="width-100" text="*업무" uitype="normal">
						</sbux-label>
					</div>
				</div>
				<div class="col-xs-12 col-sm-2 padding-1-left-web">
					<div class="margin-5-top">
						<sbux-select 
							id="select_job" name="select_job" class="combo-height width-100" uitype="single" model-name="select_job" 
							jsondata-text  = "cm_jobname" jsondata-value = "cm_jobcd" scroll-style="min-height: 200px;"
		                  	required  jsondata-ref="cboJobData"></sbux-select>
					</div>
				</div>
			</div>
			
			<div class="row-fluid">
				<div class="col-xs-12 col-sm-1"> 
					<div class="margin-10-top" >
						<sbux-label id="idx_lbl_prgName_text" class="width-100" text="*프로그램명" uitype="normal"></sbux-label>
					</div>
					
				</div>
				<div class="col-xs-12 col-sm-8">
					<div class="margin-5-top">
						<sbux-input id="idx_prgName_text" name="idx_prgName_text" class="width-100" uitype="text" 
							sbux-popover="<font color='blue' style='font-weight:bold;'>프로그램명</font>을 입력" sbux-popover-placement="bottom" sbux-popover-html="true">
						</sbux-input>
					</div>
				</div>
				<div class="col-xs-12 col-sm-3 height-35px-web">
				</div>
			</div>
			
			<div class="row-fluid">
				<div class="col-xs-12 col-sm-1"> 
					<div class="margin-10-top" >
						<sbux-label id="idx_lbl_prgStory_text" class="width-100" text="*프로그램설명" uitype="normal"></sbux-label>
					</div>
				</div>
				<div class="col-xs-12 col-sm-8">
					<div class="margin-5-top">
						<sbux-input id="idx_prgStory_text" name="idx_prgStory_text" class="width-100" uitype="text"
							sbux-popover="<font color='blue' style='font-weight:bold;'>프로그램설명</font>을 입력" sbux-popover-placement="bottom" sbux-popover-html="true">
						</sbux-input>
					</div>
				</div>
				<div class="col-xs-12 col-sm-1 padding-1-right-web">
					<div class="margin-5-top">
						<sbux-button id="idx_register_btn" name="idx_register_btn" class="width-100" uitype="normal" text="등록" onclick="clickSearchBtn()">
						</sbux-button>
					</div>
				</div>
				<div class="col-xs-12 col-sm-2 padding-1-left-web">
					<div class="margin-5-top">
						<sbux-button id="idx_devRegister_btn" name="idx_devRegister_btn" class="width-100" uitype="normal" text="개발영역연결등록" onclick="clickSearchBtn()">
						</sbux-button>
					</div>
				</div>
			</div>
			
			<div class="row-fluid">
				<div class="col-xs-12 col-sm-1"> 
					<div class="margin-10-top" >
						<sbux-label id="idx_lbl_dirpath_text" class="width-100" text="*프로그램경로" uitype="normal"></sbux-label>
					</div>
				</div>
				<div class="col-xs-12 col-sm-8">
					<div class="margin-5-top">
						<sbux-input id="idx_dirpath_text" name="idx_dirpath_text" class="width-100" uitype="text" datastore-id="idxData1" 
							sbux-popover="<font color='blue' style='font-weight:bold;'>프로그램경로</font>를 입력" sbux-popover-placement="bottom" sbux-popover-html="true">
						</sbux-input>
					</div>
					</div>
				<div class="col-xs-12 col-sm-1">
				</div>
				<div class="col-xs-12 col-sm-2 padding-1-left-web">
					<div class="margin-5-top">
						<sbux-button id="idx_loaclRegister_btn" name="idx_localRegister_btn" class="width-100" uitype="normal" text="로컬영역연결등록" onclick="clickSearchBtn()">
						</sbux-button>
					</div>
				</div>
			</div>
						
			<div class="row-fluid">
				<div class="col-xs-12 col-sm-1"> 
					<div class="margin-10-top" >
						<sbux-label id="idx_lbl_srid_text" class="width-100" text="*SR-ID" uitype="normal"></sbux-label>
					</div>
				</div>
				<div class="col-xs-12 col-sm-8">
					<div class="margin-5-top">
						<sbux-input id="idx_srid_text" name="idx_srid_text" class="width-100" uitype="text" datastore-id="idxData1" 
							sbux-popover="<font color='blue' style='font-weight:bold;'>SR-ID</font>를 입력" sbux-popover-placement="bottom" sbux-popover-html="true">
						</sbux-input>
					</div>
				</div>
				
				<div class="col-xs-12 col-sm-1 padding-1-right-web">
					<div class="margin-5-top">
						<sbux-button id="idx_reset_btn" name="idx_reset_btn" class="width-100" uitype="normal" text="초기화" onclick="clickResetBtn()">
						</sbux-button>
					</div>
				</div>
				<div class="col-xs-12 col-sm-1 padding-1-left-web  padding-1-right-web">
					<div class="margin-5-top">
						<sbux-button id="idx_search_btn" name="idx_search_btn" class="width-100" uitype="normal" text="조회" onclick="clickSearchBtn()">
						</sbux-button>
					</div>
				</div>
				<div class="col-xs-12 col-sm-1 padding-1-left-web">
					<div class="margin-5-top">
						<sbux-button id="idx_delete_btn" name="idx_delete_btn" class="width-100" uitype="normal" text="삭제" onclick="clickDeletBtn()">
						</sbux-button>
					</div>
				</div>
			</div>
			
			<div class="col-xs-12 col-sm-12"> 
				<div class="margin-5-top" >
				<sbux-label id="idx_lbl_etc_text" text="주)" uitype="normal" style="vertical-align:top"></sbux-label>
					<sbux-label id="idx_lbl_etc1_text" class="width-92 white-space-none" text="1. 프로그램명은 소스파일 기준으로 확장자까지 입력하여 주시기 바랍니다." uitype="normal" style="width:92%"></sbux-label>
					<sbux-label id="idx_lbl_etc2_text" class="width-99 padding-20-left white-space-none" text="2. 시스템/프로그램종류/프로그램명을 검색조건으로 사용합니다.(조회)" uitype="normal"></sbux-label>
				</div>
			</div>
			
		</div>
	</div>
</section>

<section>
	<div class="container-fluid">
		<div class="row-fulid" >
			<div class="col-xs-12 col-sm-12 no-padding">
				<div id="fileGrid" class="default-grid-height"></div>
			</div>
		</div>
	</div>
</section>



<script type="text/javascript" src="<c:url value="/js/ecams/dev/ProgRegister.js"/>"></script>
