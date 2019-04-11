<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="/webPage/common/common.jsp" />
<c:import url="/js/ecams/common/commonscript.jsp" />

<style type="text/css">
/* 탭 메뉴 css */
ul.tabs {
    margin: 0;
    padding: 0;
    float: left;
    list-style: none;
    height: 32px;
    border-bottom: 1px solid #eee;
    border-left: 1px solid #eee;
    width: 100%;
    font-family:"dotum";
    font-size:12px;
}
ul.tabs li {
    float: left;
    text-align:center;
    cursor: pointer;
    width:82px;
    height: 31px;
    line-height: 31px;
    border: 1px solid #eee;
    border-left: none;
    font-weight: bold;
    background: #fafafa;
    overflow: hidden;
    position: relative;
}
ul.tabs li.active {
    background: #FFFFFF;
    border-bottom: 1px solid #FFFFFF;
}
.tab_container {
    border: 1px solid #eee;
    border-top: none;
    clear: both;
    float: left;
    background: #FFFFFF;
    width:100%;
}
.tab_content {
    padding: 5px;
    font-size: 12px;
    display: none;
}
.tab_container .tab_content ul {
    width:100%;
    margin:0px;
    padding:0px;
}
.tab_container .tab_content ul li {
    padding:5px;
    list-style:none;
}

 #container {
    
}
</style>

<section>
<div id="container">
	<ul class="tabs">
        <li class="active" rel="tab1">공지사항</li>
        <li rel="tab2">구매랭킹</li>
        <li rel="tab3">공지사항</li>
   	</ul>
   	<div class="tab_container">
       	<div id="tab1" class="tab_content">
	  		<iframe src='/webPage/test/LSH_testPage.jsp?reqcd=32' width='100%' height='100%' frameborder="0"></iframe>
       	</div>
       <!-- #tab1 -->
       <div id="tab2" class="tab_content">
	  		<iframe src='/webPage/approval/RequestStatus.jsp?reqcd=32' width='100%' height='100%' frameborder="0"></iframe>
       </div>
       <!-- #tab2 -->
       <div id="tab3" class="tab_content">
	  		<iframe src='/webPage/test/LSH_Grid.jsp' width='100%' height='100%' frameborder="0"></iframe>
       </div>
       <!-- #tab3 -->
   	</div>
</div>
</section>

<script type="text/javascript" src="<c:url value="/js/ecams/dev/LSH_tabMenu.js"/>"></script>

