<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<style>
	#divPan1 {border: 2px solid black; border-top-left-radius: 5px; border-top-right-radius: 5px; padding: 10px;}
	#divGrid1 {width: 98%; height:60%}
</style>

<section>
	<div class="container-fluid padding-40-top">
		<div id="divPan1">
			<sbux-button id="Cmd_Ip3" name="Cmd_Ip3" uitype="normal" text="공지사항등록" onclick="new_Click()" disabled></sbux-button>
      		<sbux-button id="sysPath" name="sysPath" uitype="normal" text="Excel저장" onclick="sysPathButton_Click()"></sbux-button>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid">
		<div id="divGrid1"></div>
	</div>
</section>

<section>
	<div class="container-fluid">
		<div id="divSearch">
			<sbux-select id="Cbo_Find" name="Cbo_Find" uitype="single" jsondata-ref="combo_dp1" jsondata-text="cm_codename" jsondata-value="cm_micode" onchange="cbo()"></sbux-select>
      		<sbux-picker id="start_date" name="start_date" uitype="date" mode="popup" date-format="yyyy/mm/dd"></sbux-picker>
      		<sbux-picker id="end_date" name="end_date" uitype="date" mode="popup" date-format="yyyy/mm/dd"></sbux-picker>
      		<sbux-input id="Txt_Find" name="Txt_Find" uitype="text" onkeyenter="Search_click1()" readonly></sbux-input>
      		<sbux-button id="Search_Data" name="Search_Data" uitype="normal" text="조  회" onclick="Search_click()"></sbux-button>
		</div>
	</div>
</section>

<script type="text/javascript" src="<c:url value="/js/ecams/mypage/Notice.js"/>"></script>