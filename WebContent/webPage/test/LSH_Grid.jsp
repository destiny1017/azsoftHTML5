<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="/webPage/common/common.jsp" />
<c:import url="/js/ecams/common/commonscript.jsp" />

<style>
.fontStyle-cncl {
	color: #FF0000;
}
</style>

<section>
	<div class="container-fluid" >
		<div id="first-grid" data-ax5grid="first-grid" data-ax5grid-config="{}" style="height: 350px;"></div>
		<div class="row">
			<div class="form-group">
				<button class="btn btn-default" onclick="Cmd_Ip_Click('1');">추가</button>
				<button class="btn btn-default" onclick="Cmd_Ip_Click('2');">제거</button>
			</div>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid" >
		<div data-ax5grid="second-grid" data-ax5grid-config="{}" style="height: 350px;"></div>
	</div>
</section>

<script type="text/javascript" src="<c:url value="/js/ecams/dev/LSH_Grid.js"/>"></script>
