<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="/webPage/common/common.jsp" />

<section>
	<div class="container-fluid">
		<div class="border-style-black">
			<div class="row-fluid">
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label id="idx_lbl_system" class="width-100" text="사원번호"
							uitype="normal"> </sbux-label>
					</div>
				</div>
				<div class="col-md-2">
					<div class="margin-5-top">
						<sbux-input id="txtId" name="txtId" class="width-100"
							uitype="text" datastore-id="idxData1"> </sbux-input>

					</div>
				</div>
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label id="idx_lbl_srid" class="width-100" text="성명"
							uitype="normal"> </sbux-label>
					</div>
				</div>
				<div class="col-md-2">
					<div class="margin-5-top">
						<sbux-input id="txtName" name="txtName" class="width-100"
							uitype="text" datastore-id="idxData1"
							onkeyenter="clickSearchBtn()"> </sbux-input>
					</div>
				</div>
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label id="lbPos" class="width-100" text="직급" uitype="normal">
						</sbux-label>
					</div>
				</div>
				<div class="col-md-2">
					<div class="margin-5-top">
						<sbux-select id="selPos" name="selPos"
							class="combo-height width-100" uitype="single"
							model-name="select_system" jsondata-text="cm_sysmsg"
							jsondata-value="cm_syscd" scroll-style="min-height: 200px;"
							auto-unselected-text="true" required jsondata-ref="cboSysData"
							onchange="changeSysCombo()"> </sbux-select>
					</div>
				</div>
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label id="lbDuty" class="width-100" text="직위"
							uitype="normal"> </sbux-label>
					</div>
				</div>
				<div class="col-md-2">
					<div class="margin-5-top">
						<sbux-select id="selDuty" name="selDuty"
							class="combo-height width-100" uitype="single"
							model-name="select_system" jsondata-text="cm_sysmsg"
							jsondata-value="cm_syscd" scroll-style="min-height: 200px;"
							auto-unselected-text="true" required jsondata-ref="cboSysData"
							onchange="changeSysCombo()"> </sbux-select>
					</div>
				</div>
			</div>

			<div class="row-fluid">
				<div class="col-md-1"></div>
				<div class="col-md-2">
					<div class="margin-15-top">
						<sbux-radio id="rdoOpt0" name="radiogroup" uitype="normal"
							text="직원" value="reg"
							onclick="$('#btnReg').children('span').text($('#rdoOpt0').attr('text'));">
						</sbux-radio>
						<sbux-radio id="rdoOpt1" name="radiogroup" uitype="normal"
							text="외주직원" value="cancel"
							onclick="$('#btnReg').children('span').text($('#rdoOpt1').attr('text'));">
						</sbux-radio>
					</div>
				</div>
				<div class="col-md-1"></div>
				<div class="col-md-2">
					<div class="margin-15-top">
						<sbux-checkbox id="chkSysAdmin" name="chkSysAdmin" uitype="normal"
							text="시스템관리자" onclick="Chk_NotiYN_Click()"></sbux-checkbox>
					</div>
				</div>
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label id="lbIp" class="width-100" text="IP Address"
							uitype="normal"> </sbux-label>
					</div>
				</div>
				<div class="col-md-2">
					<div class="margin-5-top">
						<sbux-input id="txtIp" name="txtIp" class="width-100"
							uitype="text" datastore-id="idxData1"
							onkeyenter="clickSearchBtn()"> </sbux-input>
					</div>
				</div>
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label id="lbEmail" class="width-100" text="E-mail"
							uitype="normal"> </sbux-label>
					</div>
				</div>
				<div class="col-md-2">
					<div class="margin-5-top">
						<sbux-input id="txtEmail" name="txtEmail" class="width-100"
							uitype="text" datastore-id="idxData1"
							onkeyenter="clickSearchBtn()"> </sbux-input>
					</div>
				</div>
			</div>

			<div class="row-fluid">
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label id="lbGroup" class="width-100" text="소속조직"
							uitype="normal"> </sbux-label>
					</div>
				</div>
				<div class="col-md-2">
					<div class="margin-5-top">
						<sbux-input id="txtGroup" name="txtGroup" class="width-100"
							uitype="text" datastore-id="idxData1"
							onkeyenter="clickSearchBtn()"> </sbux-input>
					</div>
				</div>
				<div class="col-md-1"></div>
				<div class="col-md-2">
					<div class="margin-15-top">
						<sbux-checkbox id="chkAsynchro" name="chkAsynchro" uitype="normal"
							text="동기화제외사용자" onclick="Chk_NotiYN_Click()"></sbux-checkbox>
					</div>
				</div>
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label id="lbPhone" class="width-100" text="전화번호1"
							uitype="normal"> </sbux-label>
					</div>
				</div>
				<div class="col-md-2">
					<div class="margin-5-top">
						<sbux-input id="txtPhone" name="txtPhone" class="width-100"
							uitype="text" datastore-id="idxData1"
							onkeyenter="clickSearchBtn()"> </sbux-input>
					</div>
				</div>
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label id="lbPass" class="width-100" text="비번오류횟수"
							uitype="normal"> </sbux-label>
					</div>
				</div>
				<div class="col-md-2">
					<div class="margin-5-top">
						<sbux-input id="txtPass" name="txtPass" class="width-100"
							uitype="text" datastore-id="idxData1"
							onkeyenter="clickSearchBtn()"> </sbux-input>
					</div>
				</div>
			</div>

			<div class="row-fluid">
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label id="lbOrg" class="width-100" text="소속(겸직)"
							uitype="normal"> </sbux-label>
					</div>
				</div>
				<div class="col-md-2">
					<div class="margin-5-top">
						<sbux-input id="txtOrg" name="txtOrg" class="width-100"
							uitype="text" datastore-id="idxData1"
							onkeyenter="clickSearchBtn()"> </sbux-input>
					</div>
				</div>
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label id="lbLastIn" class="width-100" text="최종로그인"
							uitype="normal"> </sbux-label>
					</div>
				</div>
				<div class="col-md-2">
					<div class="margin-5-top">
						<sbux-input id="txtLastIn" name="txtLastIn" class="width-100"
							uitype="text" datastore-id="idxData1"
							onkeyenter="clickSearchBtn()"> </sbux-input>
					</div>
				</div>
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label id="lbPhone2" class="width-100" text="*전화번호2"
							uitype="normal"> </sbux-label>
					</div>
				</div>
				<div class="col-md-2">
					<div class="margin-5-top">
						<sbux-input id="txtPhone2" name="txtPhone2" class="width-100"
							uitype="text" datastore-id="idxData1"
							onkeyenter="clickSearchBtn()"> </sbux-input>
					</div>
				</div>
				<div class="col-md-1">
					<div class="margin-15-left margin-15-top">
						<sbux-label id="lbAct" class="width-100" text="활성화여부"
							uitype="normal"> </sbux-label>
					</div>
				</div>
				<div class="col-md-2">
					<div class="margin-15-top">
						<sbux-radio id="rdoAct0" name="radiogroup1" uitype="normal"
							text="활성화" value="reg"
							onclick="$('#btnReg').children('span').text($('#rdoOpt0').attr('text'));">
						</sbux-radio>
						<sbux-radio id="rdoAct1" name="radiogroup1" uitype="normal"
							text="비활성화" value="cancel"
							onclick="$('#btnReg').children('span').text($('#rdoOpt1').attr('text'));">
						</sbux-radio>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>

<script type="text/javascript"
	src="<c:url value="/js/ecams/administrator/UserInfo.js"/>"></script>