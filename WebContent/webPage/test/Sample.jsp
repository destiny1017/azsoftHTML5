<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<c:import url="/webPage/common/common.jsp" />

<style>
	.filebox label { 
		display: inline-block; 
		padding: .5em .75em; 
		color: #999; 
		font-size: inherit; 
		line-height: normal; 
		background-color: #fdfdfd; 
		cursor: pointer; 
		border: 1px solid #ebebeb; 
		border-bottom-color: #e2e2e2; 
		border-radius: .25em; 
		/*
		vertical-align: middle; 
		*/
	} 
	.filebox input[type="file"] { 
		/* 파일 필드 숨기기 */ 
		position: absolute; 
		width: 1px; 
		height: 1px; 
		padding: 0; 
		margin: -1px; 
		overflow: hidden; 
		clip:rect(0,0,0,0); /* 특정 부분만 나오게 할때  */ 
		border: 0; 
	}
</style>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
			ax5ui modal 입니다.
			
			기본 스타일의 모달입니다.(drag 불가)
			<button id="btnReg" name="btnReg" class="btn btn-default" onclick="openModal()">modal 클릭</button> > modal close시 modal html에서 window.parent.modal.close(); 사용하세요
			부모창에서 > modal로 파라미터 전달시 
			url: "../modal/PopNotice.jsp?memoid=1&memodate=20190101"와 같은 파라미터 형태로 전달하는 방식
			
			modal > 부모창으로 값 전달시
			window.parent로 전체 변수 접근하시면됩니다.
			ex) window.parent.testStr = '';
			
			축소 확대 가능한 modal입니다.(drag가능)
			최소화가 가능하기때문에 화면이 열렸을때 뒷배경 사용가능하게 합니다.
			<button id="btnReg" name="btnReg" class="btn btn-default" onclick="openModal2()">축소확대 modal 클릭</button>
			모달 축소시 위치 지정방법 > modal2.minimize('위치');
			위치 종류   
			"top-left"
			"top-right"
			"bottom-left"
			"bottom-right"
			
			bottom으로 위치 지정시 하단의 footer에 조금 가립니다.
			ax5modal.js의 align function의 1104번줄 수정하여 위치 수정했습니다.
			문제시 끝에  '- 20' 을 빼주세요.
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
			ax5ui 토스트 입니다. (알럿처럼 사용해도 될듯합니다.)
			
			<button id="btnAlert" name="btnAlert" class="btn btn-default" onclick="openToast()">ax5ui토스트</button>
			
			토스트의 설정 종류입니다.
			1."clickEventName":"click"
			2."theme":"default"	>	테마를 설정할수있습니다.
			3."width":300		>	토스트의 가로길이
			4."icon":""		>	토스트의 왼쪽에 아이콘표시 됩니다. 아이콘 사용시 부트스트랩 glyphicons 사용
			5."closeIcon":""	>	토스트 닫기버튼모양. 아이콘 사용시 부트스트랩 glyphicons 사용
			6."msg":""	>	토스트의 띄어줄 메세지
			7."displayTime":3000	>	화면에 표시될 시간 default:3000밀리세컨드					
			8."animateTime":300		>	애니메이션시간	default:300밀리세컨드
			9."containerPosition":"bottom-left"	
			>토스트 위치
			"top-left"
			"top-right"
			"bottom-left"
			"bottom-right"
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
			ax5ui 알럿창입니다.
			
			<button id="btnAlert" name="btnAlert" class="btn btn-default" onclick="openAlert()">ax5ui 확인창</button>
			
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
			ax5ui 확인창 입니다.
			
			일반버전
			<button id="btnAlert" name="btnAlert" class="btn btn-default" onclick="openConfirm2()">ax5ui 확인창</button>
			
			버튼커스텀버전
			<button id="btnAlert" name="btnAlert" class="btn btn-default" onclick="openConfirm()">ax5ui 확인창 버튼커스텀</button> <label id="confirmReturn"></label>
			
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
			파일 트리입니다.
			<ul id="treeDemo" class="ztree"></ul>				
			</pre>
			
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
			로딩중 마우스 커서 변경
			<button id="btnAlert" name="btnAlert" class="btn btn-default" onclick="loadingTest()">클릭</button>				
			</pre>
			
		</div>
	</div>
</section>


<c:import url="/js/ecams/common/commonscript.jsp" />
<script type="text/javascript" src="<c:url value="/js/ecams/dev/Sample.js"/>"></script>