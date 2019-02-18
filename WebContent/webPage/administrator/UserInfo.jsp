<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="/webPage/common/common.jsp" />

<style>
#listDuty li {
	height: 40px
}

#listJob li {
	height: 40px
}

#divUserList {
	height: 200px;
	width: 300px
}

#divJobCharged {
	height: 500px;
	width: 600px
}
</style>

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
							uitype="text" datastore-id="idxData1" onkeyenter="fnKeyEnter(txtId,'')"> </sbux-input>
<!-- 						Txt_UserId -->
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
							jsondata-text="cm_codename" jsondata-ref="selPosData">
						</sbux-select>
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
							jsondata-text="cm_codename" jsondata-value="cm_syscd"
							jsondata-ref="selDutyData"> </sbux-select>
						<!-- 							Cbo_Duty -->
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

<section>
	<div class="container-fluid">
		<div class="row-fluid margin-15-top">
			<div class="col-md-3">
				<sbux-label id="lbCharged" class="width-100" text="담당직무"
					uitype="normal"> </sbux-label>
			</div>
			<div class="col-md-3">
				<sbux-label id="lbAdd" class="width-100" text="담당업무추가"
					uitype="normal"> </sbux-label>
			</div>
			<div class="col-md-3">
				<sbux-label id="lbAbsence" class="width-100" text="부재등록정보"
					uitype="normal"> </sbux-label>
			</div>
			<div class="col-md-3">
				<sbux-label id="lbResult" class="width-100" text="사용자조회결과"
					uitype="normal"> </sbux-label>
			</div>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid margin-15-top">
		<div class="col-md-3">
			<sbux-select id="listDuty" name="listDuty" uitype="checkbox"
				jsondata-ref="listDutyData" jsondata-text="cm_codename"
				is-list-only="true" style="width:350px;"
				scroll-style="min-height:400px;"> </sbux-select>
		</div>
<!-- 		Lst_Duty -->
		<div class="col-md-3">
			<div class="width-100">
				<sbux-label id="lbSystem" text="시스템" uitype="normal"> </sbux-label>
				<sbux-select id="selSystem" name="selSystem" uitype="single"
					jsondata-ref="selSystemData" jsondata-text="cm_sysmsg"
					jsondata-value="cm_syscd" onchange="getJobInfo()"></sbux-select>
				<!-- 			Cbo_SysCd -->
			</div>
			<div class="width-100">
				<sbux-label id="lbJob" text="업무" uitype="normal" style="width:50%"> </sbux-label>
				<sbux-checkbox id="chkAll" name="chkAll" uitype="normal" text="전체선택" style="width:50%"></sbux-checkbox>
			</div>
			<sbux-select id="listJob" name="listJob" uitype="checkbox"
				jsondata-ref="listJobData" jsondata-text="cm_jobname"
				is-list-only="true" style="width:350px; height:400px"
				scroll-style="min-height:400px;"> </sbux-select>
			<!-- 			Lst_Job -->
		</div>
		<div class="col-md-6">
			<div class="row-fluid">
				<div class="col-md-6">
					<sbux-label id="lbDaeGyul" class="width-100" text="대결지정"
						uitype="normal"> </sbux-label>
					<sbux-input id="txtDaeGyul" name="txtDaeGyul" class="width-100"
						uitype="text" datastore-id="idxData1"
						onkeyenter="clickSearchBtn()"> </sbux-input>
					<sbux-label id="lbTerm" class="width-100" text="부재기간"
						uitype="normal"> </sbux-label>
					<sbux-input id="txtTerm" name="txtTerm" class="width-100"
						uitype="text" datastore-id="idxData1"
						onkeyenter="clickSearchBtn()"> </sbux-input>
					<sbux-label id="lbSayu" class="width-100" text="부재사유"
						uitype="normal"> </sbux-label>
					<sbux-input id="txtSayu" name="txtSayu" class="width-100"
						uitype="text" datastore-id="idxData1"
						onkeyenter="clickSearchBtn()"> </sbux-input>
					<sbux-label id="lbJobCharged" text="등록된 담당업무" uitype="normal">
					</sbux-label>
					<sbux-button id="btnJobCharged" name="btnJobCharged"
						uitype="normal" text="담당업무삭제" onclick="new_Click()"></sbux-button>
					<!-- 					Cmd_Ip3 -->
				</div>
				<div class="col-md-6">
					<div id="divUserList"></div>
				</div>
<!-- 				userList -->
			</div>
			<div id="divJobCharged"></div>
		</div>
	</div>
</section>
<section>
	<div class="row-fluid margin-20-top margin-15-right">
		<div id="divBtn" style="float: right">
			<sbux-button id="btnDutyInfo" name="btnDutyInfo" uitype="normal"
				text="사용자직무조회" onclick="new_Click()"></sbux-button>
			<sbux-button id="btnUserBat" name="btnUserBat" uitype="normal"
				text="사용자일괄등록" onclick="new_Click()"></sbux-button>
			<sbux-button id="btnGroupReg" name="btnGroupReg" uitype="normal"
				text="조직정보등록" onclick="new_Click()"></sbux-button>
			<sbux-button id="btnCopy" name="btnCopy" uitype="normal" text="권한복사"
				onclick="new_Click()"></sbux-button>
			<sbux-button id="btnResetPass" name="btnResetPass" uitype="normal"
				text="비밀번호초기화" onclick="setUserPwd()"></sbux-button>
<!-- 				Cmd_Ip4 -->
			<sbux-button id="btnAtrBat" name="btnAtrBat" uitype="normal"
				text="업무권한일괄등록" onclick="new_Click()"></sbux-button>
			<sbux-button id="btnUserInfo" name="btnUserInfo" uitype="normal"
				text="전체사용자조회" onclick="new_Click()"></sbux-button>
			<sbux-button id="btnSave" name="btnSave" uitype="normal" text="저장"
				onclick="new_Click()"></sbux-button>
			<sbux-button id="btnDisUse" name="btnDisUse" uitype="normal"
				text="폐기" onclick="new_Click()"></sbux-button>
		</div>
	</div>
</section>
<sbux-modal id="modalPwd" name="modalPwd" uitype="small" header-title="[비밀번호초기화]" body-html-id="pwdBody">
</sbux-modal>
<div id="pwdBody">
	<IFRAME id="popPwd" src="<c:url value="/webPage/modal/PopUserInfoPwd.jsp"/>" width="100%" height="250px"></IFRAME>
</div>

<script type="text/javascript"
	src="<c:url value="/js/ecams/administrator/UserInfo.js"/>"></script>