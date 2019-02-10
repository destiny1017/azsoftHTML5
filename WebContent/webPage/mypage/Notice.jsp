<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />

<style>
	#divPan1 {border: 2px solid black; border-top-left-radius: 5px; border-top-right-radius: 5px; padding: 10px;}
	#divGrid1 {width: 98%; height:60%}
	#divSearch {float: right; width:23%}
	#Cbo_Find {width:30%; font-size: 0.5em}
    #Txt_Find {width: 50%}
    #Search_Data {float:right; width:18%; font-size: 0.5em}
</style>

<section>
	<div class="container-fluid padding-40-top">
		<div id="divPan1">
			<sbux-button id="btnReg" name="btnReg" uitype="normal" text="공지사항등록" onclick="new_Click()" disabled></sbux-button>
<!-- 			Cmd_Ip3 -->
      		<sbux-button id="sysPath" name="sysPath" uitype="normal" text="Excel저장" onclick="sysPathButton_Click()"></sbux-button>
      		<sbux-label id="lbCnt" name="lbCnt" uitype="normal" text="총{myGrid1.getRows();}건"></sbux-label>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid">
		<div id="divGrid1"></div>
	</div>
</section>

<section>
	<div class="container-fluid margin-15-top">
		<div id="divSearch">
			<sbux-select id="Cbo_Find" name="Cbo_Find" uitype="single" jsondata-ref="combo_dp1" jsondata-text="cm_codename" jsondata-value="cm_micode" onchange="cbo()"></sbux-select>
      		<sbux-picker id="start_date" name="start_date" uitype="date" mode="popup" date-format="yyyy/mm/dd"></sbux-picker>
      		<sbux-picker id="end_date" name="end_date" uitype="date" mode="popup" date-format="yyyy/mm/dd"></sbux-picker>
      		<sbux-input id="Txt_Find" name="Txt_Find" uitype="text" onkeyenter="Search_click1()" disabled></sbux-input>
      		<sbux-button id="Search_Data" name="Search_Data" uitype="normal" text="조  회" onclick="Search_click()"></sbux-button>
      		<sbux-modal id="popWin_Modal" name="popWin_Modal" uitype="middle" header-title="공지사항 등록" body-html-id="popupBody">
      			<div id="popupBody">
					<sbux-label id="lbSub" name="lbSub" uitype="normal" text="제목"></sbux-label>
					<sbux-input id="txtTitle" name="txtTitle" uitype="text"></sbux-input>
				</div>
   				<IFRAME id="popWin" src="PopNotice.jsp" width="564" height="480"></IFRAME>
   			</sbux-modal>
		</div>
	</div>
</section>


<script type="text/javascript" src="<c:url value="/js/ecams/mypage/Notice.js"/>"></script>