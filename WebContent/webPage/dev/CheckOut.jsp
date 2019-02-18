<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<c:import url="/webPage/common/common.jsp" />

<section>
	<div class="container-fluid">
		<div class="border-style-black">
			<div class="row-fluid">
				<div class="col-md-1"> 
					<div class="margin-15-left margin-15-top">
						<sbux-label 
							id="idx_lbl_system"
							class="width-100" 
							text="*시스템" 
							uitype="normal">
						</sbux-label>
					</div>
				</div>
				<div class="col-md-3">
					<div class="margin-5-top">
						<sbux-select 
							id="select_system" 
							name="select_system"
							class="combo-height width-100"  
							uitype="single" 
						  	model-name="select_system"
		                  	jsondata-text  = "cm_sysmsg"
		                  	jsondata-value = "cm_syscd"
		                  	scroll-style="min-height: 200px;"
		                  	auto-unselected-text="true"
		                  	required 
		                  	jsondata-ref="cboSysData"
		                  	onchange="changeSysCombo()"
		                >
		                </sbux-select>
					</div>
				</div>
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label 
							id="idx_lbl_srid" 
							class="width-100"
							text="*SR-ID" 
							uitype="normal">
						</sbux-label>
					</div>
				</div>
				<div class="col-md-6">
					<div class="margin-5-top">
						<sbux-select 
							id="select_srid" 
							name="select_srid"
							class="combo-height width-100"  
							uitype="single" 
						  	model-name="select_srid"
		                  	jsondata-text = "srid"
		                  	jsondata-value = "cc_srid"
		                  	scroll-style="min-height: 120px;"
		                  	auto-unselected-text="true"
		                  	required 
		                  	jsondata-ref="cboSrIdData"
		                >
		                </sbux-select>
					</div>
				</div>
				<div class="col-md-1">
					<div class="margin-5-top">
						<sbux-button 
							id="idx_button_srinfo" 
							name="button_search" 
							class="width-100"
							uitype="normal" 
							text="SR정보"
							onclick="openSrModal()">
						</sbux-button>
					</div>
				</div>
			</div>
			
			<div class="row-fluid">
				<div class="col-md-1"> 
					<div class="margin-10-top" >
						<sbux-label 
							id="idx_lbl_request_text" 
							class="width-100"
							text="*신청사유" 
							uitype="normal">
						</sbux-label>
					</div>
					
				</div>
				<div class="col-md-11">
					<div class="margin-5-top">
						<sbux-input 
							id="idx_request_text" 
							name="idx_request_text"
							class="width-100" 
							uitype="text" 
							datastore-id="idxData1" 
							sbux-popover="<font color='blue' style='font-weight:bold;'>신청사유</font>를 입력"
							sbux-popover-placement="bottom" 
							sbux-popover-html="true">
						</sbux-input>
					</div>
				</div>
			</div>
			
			<div class="row-fluid">
				<div class="col-md-4"> 
					<div class="margin-10-top">
						<sbux-checkbox 
							id="chkbox_subnode" 
							name="chkbox_subnode"
							uitype="normal" 
							text="하위폴더포함하여조회"
							checked="checked" 
							style="color:blue;">
						</sbux-checkbox>
					</div>
				</div>
				
				<div class="col-md-1">
					<div class="margin-15-top">
						<sbux-label 
							id="idx_lbl_prg" 
							text="프로그램유형" 
							uitype="normal">
						</sbux-label>
					</div>
				</div>
				
				<div class="col-md-2">
					<div class="margin-5-top">
						<sbux-select 
							id="select_prg" 
							name="select_prg"
							class="combo-height width-100"  
							uitype="single" 
						  	model-name="select_prg"
		                  	jsondata-text = "cm_codename"
		                  	jsondata-value = "cm_micode"
		                  	scroll-style="min-height: 120px;"
		                  	required 
		                  	jsondata-ref="cboPrgData"
		                >
		                </sbux-select>
					</div>
				</div>
				
				<div class="col-md-1">
					<div class="width-6 margin-15-top">
						<sbux-label 
							id="idx_lbl_prg_exp" 
							text="*프로그램명/설명" 
							uitype="normal">
						</sbux-label>
					</div>
				</div>
				
				<div class="col-md-3">
					<div class="margin-5-top">
						<sbux-input 
							id="idx_lbl_prg_exp_txt" 
							name="idx_lbl_prg_exp_txt"
							class="width-100" 
							uitype="text" 
							datastore-id="idxData1" 
							sbux-popover="<font color='blue' style='font-weight:bold;'>프로그램설명</font>을 입력"
							sbux-popover-placement="bottom" 
							sbux-popover-html="true"
							onkeyenter="clickSearchBtn()">
						</sbux-input>
					</div>
				</div>
				
				<div class="col-md-1">
					<div class="margin-5-top">
						<sbux-button 
							id="idx_search_btn" 
							name="idx_search_btn" 
							class="width-100"
							uitype="normal" 
							text="검색"
							onclick="clickSearchBtn()">
						</sbux-button>
					</div>
				</div>
			</div>
			
		</div>
	</div>
</section>

<section>
	<div class="container-fluid">
		<div class="row-fulid" >
			<div class="col-xs-12 col-md-3 file-tree" >
				<sbux-tree 
				  	id="idxFileTree" 
				  	name="fileTree" 
				  	uitype="normal" 
				  	jsondata-ref="treeJsonData" 
				  	empty-message="시스템에 등록된 프로그램종류가 없습니다."
				  	onclick="fileTreeClick()">
				</sbux-tree>
			</div>
			
			<div class="col-xs-12 col-md-9 no-padding">
				<div id="fileGrid" class="default-grid-height"></div>
			</div>
			
		</div>
	</div>
</section>

<section>
	<div class="container-fluid">
		<div class="row-fulid" >
			<div class="col-xs-12 col-md-3">
				<sbux-label 
					id="idx_lbl_path" 
					text="" 
					uitype="normal"
					style="padding: 5px;">	
				</sbux-label>	
			</div>
			
			<div class="col-xs-12 col-md-7"></div>
			
			<div class="col-xs-12 col-md-2">
				<div class="row">
					<div class="float-right">
						<sbux-button 
							id="idx_del_btn" 
							name="idx_del_btn"
							class="width-default-btn" 
							uitype="normal" 
							text="제거"
							onclick="deleteDataRow()">
						</sbux-button>
					</div>
					<div class="float-right">
						<sbux-button 
							id="idx_add_btn"
							name="idx_add_btn" 
							class="width-default-btn"
							uitype="normal" 
							text="추가"
							onclick="addDataRow()">
						</sbux-button>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid">
		<div class="row-fulid" >
			<div class="col-xs-12 col-md-12 no-padding">
				<div id="requestGrid" class="default-grid-height">
				</div>
			</div>
		</div>
	</div>
</section>


<section>
	<div class="container-fluid">
		<div class="row-fulid" >
			<div class="float-right">
				<sbux-button 
					id="idx_request_btn"
					name="idx_request_btn" 
					class="width-default-btn"
					uitype="normal" 
					text="체크아웃"
					onclick="clickCheckOutBtn()">
				</sbux-button>
			</div>
		</div>
	</div>
</section>

<script type="text/javascript" src="<c:url value="/js/ecams/dev/CheckOut.js"/>"></script>
