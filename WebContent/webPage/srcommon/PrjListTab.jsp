<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<c:import url="/webPage/common/common.jsp" />

<section>
	<div class="container-fluid">
		<div class="border-style-black">
			<div class="row-fluid">
			
				<div class="col-sm-1 col-xs-12 margin-10-top text-center"> 
					<sbux-label 
						id="lbReqDepart"
						class="width-100" 
						text="요청부서" 
						uitype="normal">
					</sbux-label>
				</div>
				<div class="col-sm-2 col-xs-12 margin-5-top no-padding">
					<sbux-select 
						id="cboReqDepart" 
						name="cboReqDepart"
						class="width-100"  
						uitype="single" 
					  	model-name="cboReqDepart"
	                  	jsondata-text  = "cm_deptname"
	                  	jsondata-value = "cm_deptcd"
	                  	scroll-style="min-height: 200px;"
	                  	required 
	                  	jsondata-ref="cboReqDepartData"
	                >
	                </sbux-select>
				</div>
				<div class="col-sm-1 col-xs-12 margin-10-top text-center">
					<sbux-label 
						id="lbCatType" 
						class="width-100"
						text="분류유형" 
						uitype="normal">
					</sbux-label>
				</div>
				<div class="col-sm-1 col-xs-12 margin-5-top no-padding">
					<sbux-select 
						id="cboCatType" 
						name="cboCatType"
						class="width-100"  
						uitype="single" 
					  	model-name="cboCatType"
	                  	jsondata-text = "cm_codename"
	                  	jsondata-value = "cm_micode"
	                  	scroll-style="min-height: 120px;"
	                  	required 
	                  	jsondata-ref="cboCatTypeData"
	                >
	                </sbux-select>
				</div>
				
				<div class="col-sm-1 col-xs-12 margin-10-top text-center">
					<sbux-label 
						id="lbQryGbn" 
						class="width-100"
						text="대상구분" 
						uitype="normal"
						>
					</sbux-label>
				</div>
				<div class="col-sm-1 col-xs-12  margin-5-top no-padding">
					<sbux-select 
						id="cboQryGbn" 
						name="cboQryGbn"
						class="width-100"  
						uitype="single" 
					  	model-name="cboQryGbn"
	                  	jsondata-text = "cm_codename"
	                  	jsondata-value = "cm_micode"
	                  	scroll-style="min-height: 120px;"
	                  	required 
	                  	jsondata-ref="cboQryGbnData"
	                  	onchange="changeQryGbnCbo(cboQryGbn)"
	                >
	                </sbux-select>
				</div>
				
				
				<div class="col-sm-4 col-xs-12 no-padding">
					<div class="col-sm-2 col-xs-12 text-center margin-10-top">
						<sbux-label 
							id="lbAcptDate" 
							class="width-100"
							text="등록일" 
							uitype="normal">
						</sbux-label>
					</div>
					<div class="col-sm-4 col-xs-12 no-padding">
						<sbux-picker 
							id="datStD" 
							name="datStD" 
							uitype="date" 
							mode="popup"
							style="width:100%;"
						    init="2018/01/01" 
						    date-format="yyyy/mm/dd">
						</sbux-picker>
					</div>
					<div class="col-sm-1 col-xs-12 margin-10-top text-center">
						<sbux-label 
							id="lbSeparator" 
							class="width-100"
							text="~" 
							uitype="normal">
						</sbux-label>
					</div>
					<div class="col-sm-4 col-xs-12 no-padding">
						<sbux-picker 
							id="datEdD" 
							name="datEdD" 
							uitype="date" 
							mode="popup"
							style="width:100%;"
						    init="2018/01/01" 
						    date-format="yyyy/mm/dd">
						</sbux-picker>
					</div>
				</div>
				
				<div class="col-sm-1 col-xs-12">
					<div class="col-sm-6 col-xs-12 margin-5-top no-padding">
						<sbux-button 
							id="btnSearch" 
							name="btnSearch" 
							class="width-100"
							uitype="normal" 
							text="조회"
							onclick="getPrjList()">
						</sbux-button>
					</div>
					<div class="col-sm-6 col-xs-12 margin-5-top no-padding">
						<sbux-button 
							id="btnInit" 
							name="btnInit" 
							class="width-100"
							uitype="normal"
							text="초기화"
							onclick="initPrjListTab()">
						</sbux-button>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-sm-12 col-xs-12">
					<div id="prjListGrid" class="sm-grid-height">
					</div>
				</div>
			</div>
		</div>
	</div>
</section>

<script type="text/javascript" src="<c:url value="/js/ecams/srcommon/PrjListTab.js"/>"></script>
