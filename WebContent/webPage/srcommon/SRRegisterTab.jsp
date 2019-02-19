<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<c:import url="/webPage/common/common.jsp" />

<section>
	<div class="container-fluid">
		<div class="border-style-black">
			<div class="row">
				<div class="col-sm-6 col-xs-12">
					<div class="col-sm-1 col-xs-12 text-right no-padding margin-5-top"> 
						<sbux-label 
							id="lbSRID"
							text="SR번호" 
							class="sm-font"
							uitype="normal">
						</sbux-label>
					</div>
					<div class="col-sm-3 col-xs-12 no-padding margin-3-top">
						<sbux-input 
							id="txtSRID" 
							model-name="txtSRID"
							name="cc_srid"
							class="width-100" 
							uitype="text"
							grid-id="prjListGrid" 
							sbux-popover="<font color='blue' style='font-weight:bold;'>SR번호입력</font>를 입력"
							sbux-popover-placement="bottom" 
							sbux-popover-html="true"
							onclick="test()">
						</sbux-input>
					</div>
					<div class="col-sm-2 col-xs-12 no-padding margin-5-top">
						<sbux-checkbox 
							id="chkNew" 
							name="chkNew"
							uitype="normal" 
							text="신규등록"
							class="sm-font"
							style="color:blue;"
							onclick="clickChkNew()">
						</sbux-checkbox>
					</div>
					
					<div class="col-sm-1 col-xs-12 text-right no-padding margin-5-top"> 
						<sbux-label 
							id="lbRegUser"
							text="등록인" 
							class="sm-font"
							uitype="normal">
						</sbux-label>
					</div>
					<div class="col-sm-5 col-xs-12 no-padding margin-3-top">
						<sbux-input 
							id="txtRegUser" 
							name="txtRegUser"
							uitype="text" 
							sbux-popover="<font color='blue' style='font-weight:bold;'>등록인</font>을 입력"
							sbux-popover-placement="bottom" 
							sbux-popover-html="true">
						</sbux-input>
					</div>
				</div> 
				
				<div class="col-sm-6 col-xs-12">
					<div class="col-sm-1 col-xs-12 text-right no-padding margin-5-top"> 
						<sbux-label 
							id="lbRegDate"
							text="등록일시" 
							class="sm-font"
							uitype="normal">
						</sbux-label>
					</div>
					<div class="col-sm-11 col-xs-12 no-padding margin-3-top">
						<sbux-input 
							id="txtRegDate" 
							model-name="txtRegDate"
							name="createdate"
							uitype="text"
							grid-id="prjListGrid"
							sbux-popover-placement="bottom" 
							sbux-popover-html="true">
						</sbux-input>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-sm-6 col-xs-12">
					<div class="col-sm-1 col-xs-12 text-right no-padding margin-10-top"> 
						<sbux-label 
							id="lbDocuNum"
							text="문서번호" 
							class="sm-font"
							uitype="normal">
						</sbux-label>
					</div>
					<div class="col-sm-5 col-xs-12 no-padding margin-3-top">
						<sbux-input 
							id="txtDocuNum" 
							name="txtDocuNum"
							class="width-100" 
							uitype="text"
							sbux-popover="<font color='blue' style='font-weight:bold;'>SR번호입력</font>를 입력"
							sbux-popover-placement="bottom" 
							sbux-popover-html="true">
						</sbux-input>
					</div>
					<div class="col-sm-1 col-xs-12 text-right no-padding margin-10-top">
						<sbux-label 
							id="lbReqDept"
							text="*요청부서" 
							class="sm-font"
							uitype="normal">
						</sbux-label>
					</div>
					
					<div class="col-sm-5 col-xs-12 text-right no-padding margin-5-top"> 
						<sbux-input 
							id="txtDept" 
							model-name="txtDept"
							name="reqdept"
							uitype="text"
							grid-id="prjListGrid"
							sbux-popover="<font color='blue' style='font-weight:bold;'>더블클릭</font> 부서선택"
							sbux-popover-placement="bottom" 
							sbux-popover-html="true">
						</sbux-input>
					</div>
				</div> 
				
				<div class="col-sm-6 col-xs-12">
					<div class="col-sm-1 col-xs-12 text-right no-padding margin-10-top"> 
						<sbux-label 
							id="lbRegComDate"
							text="*완료요청일" 
							class="sm-font"
							uitype="normal">
						</sbux-label>
					</div>
					<div class="col-sm-11 col-xs-12 no-padding">
						<sbux-picker 
							id="datReqComDate" 
							model-name="datReqComDate"
							name="reqcompdat"
							grid-id="prjListGrid" 
							uitype="date" 
							mode="popup"
							style="width: 100%;"
						    init="2018/01/01" 
						    date-format="yyyy/mm/dd">
						</sbux-picker>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-sm-6 col-xs-12">
					<div class="col-sm-1 col-xs-12 text-right no-padding margin-5-top"> 
						<sbux-label 
							id="lbReqSubject"
							text="*요청제목" 
							class="sm-font"
							uitype="normal">
						</sbux-label>
					</div>
					<div class="col-sm-11 col-xs-12 no-padding">
						<sbux-input 
							id="txtReqSubject" 
							model-name="txtReqSubject"
							name="cc_reqtitle"
							grid-id="prjListGrid" 
							uitype="text"
							sbux-popover="<font color='blue' style='font-weight:bold;'>요청제목</font>을 입력"
							sbux-popover-placement="bottom" 
							sbux-popover-html="true">
						</sbux-input>
					</div>
				</div> 
				<div class="col-sm-6 col-xs-12 no-padding height-30px">
				</div>
			</div>
			
			
			<div class="row">
				<div class="col-sm-6 col-xs-12">
					<div class="col-sm-1 col-xs-12 text-right no-padding margin-5-top"> 
						<sbux-label 
							id="lbReqContent"
							text="*상세내용" 
							class="sm-font"
							uitype="normal">
						</sbux-label>
					</div>
					<div class="col-sm-11 col-xs-12 no-padding margin-5-top">
						<sbux-textarea 
							id="texReqContent" 
							name="texReqContent"
							class="width-100"
							rows="7"
							uitype="normal"
							>
						</sbux-textarea>
					</div>
				</div>
				
				<div class="col-sm-6 col-xs-12">
					<div class="col-sm-1 col-xs-12 text-right no-padding margin-5-top"> 
						<sbux-button 
							id="btnFileAdd" 
							name="btnFileAdd" 
							class="width-100"
							uitype="normal" 
							text="파일첨부"
							onclick="openSrModal()">
						</sbux-button>
					</div>
					<div class="col-sm-11 col-xs-12 no-padding margin-3-top">
						<div id="fileAddGrid" class="xs-grid-height">
						</div>
					</div>
				</div>
				
				<div class="col-sm-6 offset-sm-6 col-xs-12">
					<div class="col-sm-1 col-xs-12 text-right no-padding margin-5-top"> 
						<sbux-label 
							id="lbDevUser"
							text="*담당개발자" 
							class="sm-font"
							uitype="normal">
						</sbux-label>
					</div>
					<div class="col-sm-1 col-xs-12 no-padding margin-3-top">
						<sbux-input 
							id="txtDevUser" 
							name="txtDevUser"
							class="width-100" 
							uitype="text"
							sbux-popover="<font color='blue' style='font-weight:bold;'>더블클릭</font>하여 사용자 검색"
							sbux-popover-placement="bottom" 
							sbux-popover-html="true"
							ondblclick="findPesonOrDepart('1')">
						</sbux-input>
					</div>
					
					<div class="col-sm-8 col-xs-12 no-padding margin-3-top">
						<sbux-select 
							id="cboDevUser" 
							name="cboDevUser"
							class="combo-height width-100"  
							uitype="single" 
						  	model-name="cboDevUser"
		                  	jsondata-text = "cm_idname"
		                  	jsondata-value = "cm_userid"
		                  	scroll-style="min-height: 120px;"
		                  	required 
		                  	jsondata-ref="cboDevUserData"
		                >
		                </sbux-select>
					</div>
					
					<div class="col-sm-1 col-xs-12 no-padding margin-3-top">
						<sbux-button 
							id="btnAddDevUser" 
							name="btnAddDevUser" 
							class="width-100"
							uitype="normal" 
							text="추가"
							onclick="openSrModal()">
						</sbux-button>
					</div>
					
					<div class="col-sm-1 col-xs-12 no-padding margin-3-top">
						<sbux-button 
							id="btnDelDevUser" 
							name="btnDelDevUser" 
							class="width-100"
							uitype="normal" 
							text="삭제"
							onclick="openSrModal()">
						</sbux-button>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-sm-6 col-xs-12">
					<div class="col-sm-1 col-xs-12 no-padding text-right margin-10-top">
						<sbux-label 
							id="lbCatTypeSR"
							text="*분류유형" 
							class="sm-font"
							uitype="normal">
						</sbux-label>
					</div>
				
					<div class="col-sm-11 col-xs-12 no-padding margin-5-top"> 
						<sbux-select 
							id="cboCatTypeSR" 
							name="cboCatTypeSR"
							class="combo-height width-100"  
							uitype="single" 
		                  	jsondata-text = "cm_codename"
		                  	jsondata-value = "cm_micode"
		                  	scroll-style="min-height: 120px;"
		                  	required 
		                  	jsondata-ref="cboCatTypeSRData"
		                >
		                </sbux-select>
					</div>
				</div>
				
				<div class="col-sm-6 offset-col-sm-6 col-xs-12"  style="float:right;">
					<div class="col-sm-1 col-xs-12 no-padding height-30px">
					</div>
				
					<div class="col-sm-11 col-xs-12 no-padding margin-5-top"> 
						<div id="devUserGrid" class="xs-grid-height">
						</div>
					</div>
				</div>
				
				<div class="col-sm-6 col-xs-12">
					<div class="col-sm-1 col-xs-12 no-padding text-right margin-10-top">
						<sbux-label 
							id="lbChgType"
							text="*변경종류" 
							class="sm-font"
							uitype="normal">
						</sbux-label>
					</div>
				
					<div class="col-sm-11 col-xs-12 no-padding margin-5-top"> 
						<sbux-select 
							id="cboChgType" 
							name="cboChgType"
							class="combo-height width-100"  
							uitype="single" 
		                  	jsondata-text = "cm_codename"
		                  	jsondata-value = "cm_micode"
		                  	scroll-style="min-height: 120px;"
		                  	required 
		                  	jsondata-ref="cboChgTypeData"
		                >
		                </sbux-select>
					</div>
				</div>
				
				<div class="col-sm-6 col-xs-12">
					<div class="col-sm-1 col-xs-12 no-padding text-right margin-10-top">
						<sbux-label 
							id="lbWorkRank"
							text="*작업순위" 
							class="sm-font"
							uitype="normal">
						</sbux-label>
					</div>
				
					<div class="col-sm-11 col-xs-12 no-padding margin-5-top"> 
						<sbux-select 
							id="cboWorkRank" 
							name="cboWorkRank"
							class="combo-height width-100"  
							uitype="single" 
		                  	jsondata-text = "cm_codename"
		                  	jsondata-value = "cm_micode"
		                  	scroll-style="min-height: 120px;"
		                  	required 
		                  	jsondata-ref="cboWorkRankData"
		                >
		                </sbux-select>
					</div>
				</div>
				
				<div class="col-sm-6 col-xs-12">
					<div class="col-sm-1 col-xs-12 no-padding text-right margin-10-top">
						<sbux-label 
							id="lbReqSecu"
							text="보안요구사항" 
							class="sm-font"
							uitype="normal">
						</sbux-label>
					</div>
				
					<div class="col-sm-11 col-xs-12 no-padding margin-5-top"> 
						<sbux-select 
							id="cboReqSecu" 
							name="cboReqSecu"
							class="combo-height width-100"  
							uitype="single" 
		                  	jsondata-text = "cm_codename"
		                  	jsondata-value = "cm_micode"
		                  	scroll-style="min-height: 120px;"
		                  	required 
		                  	jsondata-ref="cboReqSecuData"
		                  	onchange = "changeCboReqSecu(cboReqSecu)"
		                >
		                </sbux-select>
		                
		                <sbux-input 
							id="txtReqSecu" 
							model-name="txtReqSecu"
							name="txtReqSecu"
							class="width-100"
							uitype="text"
							sbux-popover="<font color='blue' style='font-weight:bold;'>보안요구사항</font>을 직접 입력"
							sbux-popover-placement="top" 
							sbux-popover-html="true">
						</sbux-input>
					</div>
				</div>
			</div>
			
			<div class="row-fluid">
				<div class="col-sm-9 col-xs-12 height-30px">
				</div>
				<div class="col-sm-1 col-xs-12 no-padding margin-5-top">
					<sbux-button 
						id="btnRegister" 
						name="btnRegister" 
						class="width-100"
						uitype="normal" 
						text="등록"
						onclick="openSrModal()">
					</sbux-button>
				</div>
				<div class="col-sm-1 col-xs-12 no-padding margin-5-top"> 
					<sbux-button 
						id="btnUpdate" 
						name="btnUpdate" 
						class="width-100"
						uitype="normal" 
						text="수정"
						onclick="openSrModal()">
					</sbux-button>
				</div>
				
				<div class="col-sm-1 col-xs-12 no-padding margin-5-top"> 
					<sbux-button 
						id="btnDelete" 
						name="btnDelete" 
						class="width-100"
						uitype="normal" 
						text="반려"
						onclick="openSrModal()">
					</sbux-button>
				</div>
			</div>
			
		</div>
	</div>
</section>
<c:import url="/js/ecams/common/commonscript.jsp" />
<script type="text/javascript" src="<c:url value="/js/ecams/srcommon/SRRegisterTab.js"/>"></script>

<sbux-modal id="modalOrganization" 
			name="modalOrganization" 
			uitype="middle" 
			header-title="조직도" 
			body-html-id="modalBody" 
			footer-is-close-button="false">
</sbux-modal>

<div id="modalBody">
	<iframe id="modalOrganizationBody"
			name="modalOrganizationBody" 
			src="<c:url value="/webPage/modal/TreeOrganization.jsp"/>" 
			width="100%" 
			height="380px" 
			frameborder="0" 
			border="0" 
			scrolling="no" 
			marginheight="0" 
			marginwidth="0">
	</iframe>
</div>


