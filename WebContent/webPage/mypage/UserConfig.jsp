<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<style>
	#divGrid1 {width:100%; height:23%}
</style>

<section>
	<div class="container-fluid padding-40-top">
		<sbux-tabs id="idxTab_norm" name="tab_norm" uitype="normal" is-scrollable="false" title-target-id-array="tab1" title-text-array="1.개발환경설정">
		</sbux-tabs>
		<div class="tab-content">
			<div id="tab1">
				<div id="pan1">
					<sbux-label id="sysname" name="sysname" uitype="normal" text="시스템명"></sbux-label>
					<sbux-select id="Cbo_SysCd" name="Cbo_SysCd" uitype="single" jsondata-ref="combo_dp1" jsondata-text="cm_sysmsg" jsondata-value="cm_micode" onchange="Cbo_SysCd_Click()"></sbux-select>
					<sbux-button id="btnDir" name="btnDir" uitype="normal" text="디렉토리찾기" onclick="Cmd_Dir_Click()"></sbux-button>
<!-- 					Cmd_Dir -->
					<sbux-label id="lbDir" name="lbDir" uitype="normal" text="개발 Home Directory"></sbux-label>
					<sbux-input id="txtDir" name="txtDir" uitype="text"></sbux-input>
<!-- 					Lbl_Dir -->
					<sbux-button id="btnReg" name="btnReg" uitype="normal" text="등 록" onclick="Cmd_Ip_Click(1)"></sbux-button>
<!-- 					Cmd_Ip1 -->
					<sbux-label id="lbConfig" name="lbConfig" uitype="normal" text="등록된 개발환경 목록"></sbux-label>
				</div>
				<div id="divGrid1"></div>
				<div id="divBtn">
					<sbux-button id="btnRem" name="btnRem" uitype="normal" text="삭제" onclick="Cmd_Ip_Click(2)"></sbux-button>
					<sbux-button id="btnRef" name="btnRef" uitype="normal" text="조회" onclick="Cmd_Ip_Click(0)"></sbux-button>
				</div>
			</div>
		</div>
	</div>
</section>

<script type="text/javascript" src="<c:url value="/js/ecams/mypage/UserConfig.js"/>"></script>