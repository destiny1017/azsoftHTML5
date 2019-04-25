<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="<c:url value="/scripts/tui-code-snippet.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/scripts/tui-chart-all.min.js"/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value="/styles/tui-chart.min.css"/>" />
<c:import url="/webPage/common/common.jsp" />

<section>
	<div class="container-fluid">
		<div class="row">
			<pre>
<b>Bar chart</b>
 			<div id="bar-chart"></div>		
우선 tui-code-snippet.min.js, tui-chart-all.min.js, tui-chart.min.css 3개의 라이브러리가 필요합니다.
데이터에 좌측에 나올 카테고리를 순서대로 입력합니다. (맨 위가 가장 처음)
그 다음 바 차트로 표현할 데이터를 이름과 함께 수치로 입력합니다.
옵션에서 타이틀명, X축과 Y축에 붙여줄 이름 등을 설정합니다.
			
> 엑셀파일 저장의 경우 차트는 저장되지 않고 입력된 데이터만 저장됩니다.
<b>tooltip 글씨가 현재 기본 테마로 적용되 있어서 색상이 검정색입니다.
theme관련된 소스부분의 주석처리를 제거하고 
label: {
	color: '#fff',
	fontFamily: 'sans-serif'
	}
      로 추가해주면 툴팁 글자 색상이 흰색으로 변경됩니다.</b>
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>Negative Bar chart</b>
			<div id="negative-chart"></div>
음수 값을 표현할 수 있는 Bar 차트 입니다.
사용법은 기본 Bar차트랑 동일하며, 데이터에 음수를 지정해서 넣어주면 됩니다.	
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>stack Bar chart</b>
				<div id="stack-chart"></div>
종류별 수치를 stack을 쌓아서 보여주는 차트입니다.
기본 Bar차트랑 동일하지만, 옵션에서 stackType : 'normal' 을 추가해야 합니다.
> 옵션(series)에서 showLabel : true 는 차트를 그릴 때 해당 종류의 수치를 같이 그려주는 옵션입니다.
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>diverging Bar chart</b>
			<div id="diverging-chart"></div>				
두 가지의 내용을 양쪽으로 비교하여 Bar 차트로 보여주는 차트입니다.
> 옵션(series)에서 diverging: true 를 입력해줘야 합니다.
			</pre>
		</div>
	</div>
</section>


<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>range Bar chart</b>		
			<div id="range-chart"></div>
range 일정 범위의 데이터를 bar 차트 표현합니다.
> 데이터의 경우 최소값과 최대값을 같이 입력해줘야 합니다.
> tooltip의 suffix를 입력해서 원하는 표기를 설정할 수 있습니다.
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>column chart</b></b>
				<div id="column-chart"></div>
bar 차트와 비슷한 column 차트입니다.
사용법은 bar 차트와 동일하지만 마지막에 tui.chart.barChart() 가 아닌
tui.chart.columnChart()로 파라미터를 넘겨줘야 합니다.
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>Negative Column chart</b>
			<div id="negative-column-chart"></div>
기본 column 차트 구조에서 음수값을 표현 해주는 차트입니다.
> 수치 입력에서 음수값을 같이 넣어주면 됩니다.
> legend 옵션에서 visible: false 를 하면 차트의 체크박스 옵션이 보이지 않습니다.
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>Percent Column chart</b>
			<div id="percent-column-chart"></div>
기본 column 차트와 다른점은 하나의 column으로 입력한 데이터를 합하여
해당 기준의 퍼센트로 stack을 쌓아서 보여주는 차트입니다.
> stack으로 보여주기 위해서 series 옵션에 stackType: 'percent' 를 추가해주면 됩니다.
> legend 옵션에서 align: 'bottom' 을 통해 체크박스의 위치를 변경할 수 있습니다.
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>Group Column chart</b>
				<div id="group-column-chart"></div>
group stack 차트로 column을 하나씩 표현하는게 아닌
하나의 그룹으로 묶어서 보여줍니다.
>tooltip 옵션에서 grouped: true 옵션을 주지 않으면 마우스 오버 시 Column당 정보를 보여주고
  grouped: true이 있으면 그룹으로 묶어서 한번에 보여줍니다.
> data를 입력할 때 stack에 입력한 키값을 기준으로 그룹핑을 하여 묶을수 있습니다.
> series 옵션에서 diverging: true 기능을 추가하면 stack에 입력된 키값을 기준으로 묶어서 위 아래로 묶어서 보여줍니다.
  (현재는 주석처리)
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>Line chart</b>
			<div id="line-chart"></div>
Line chart로 그려주기 위해 데이터 입력하고 파라미터로 전송할 때
lineChart()에 넣어서 불러주면 됩니다.
> width, height의 길이가 넉넉하면 x축의 수치가 자세하게 나옵니다.
> plot 옵션을 통해 특정 기간을 색상을 주어서 강조할 수 있습니다.(예시는 회색 상자)
   특정 기간의 시작과 끝을 line 배열에 날짜와 색상을 입력하면 됩니다.(예시는 붉은색 세로선)
> Line을 더블클릭하면 해당 일을 기준으로 전날과 다음날을 확대하여 보여줍니다. (series 옵션의  zoomable: true)
> 각 데이터의 점을 보여주고 싶을 때는 series 옵션의 showDot: true 를 입력하면 됩니다.(예시는 false)
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>spline line chart</b>
			<div id="spline-line-chart"></div>
곡선으로 데이터를 연결하는 Line 차트입니다.
기본 Line 차트와는 다르게 최고점, 최저점에서 부드러운 곡선으로 그려줍니다.
> 기본 Line 차트에서 곡선으로 보여주고 싶으면 series 옵션에서 spline: true 를 입력하면 됩니다.
> 중간에 데이터가 없는 경우 null을 입력하면 그래프가 끊어져서 그려집니다.
> 0인 경우는 선이 이어지고, null은 선이 아예 끊어집니다.
> tooltip 옵션에서 grouped: true를 넣어주면 x축을 기준으로 모든 선의 데이터 툴팁이 하나로 묶여서 보여줍니다.
  tooltip의 글자색은 CSS 조절로 변경 가능합니다.
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div calss="row">
			<pre>
<b>Area Chart</b>
			<div id="area-chart"></div>
Area chart로 그리는 방법은 data를 입력하고 파라미터로 전송할 때
areaChart()에 넣어서 불러주면 됩니다.
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>Area stack chart</b>
			<div id="stack-area-chart"></div>
stack area chart 입니다. 
기존 area chart랑 다른점은 아래 차트부터 자신의 값을 쌓아올려서 그래프를 그립니다.
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>Bubble Chart</b>
			<div id="bubble-chart"></div>
Bubble Chart 그리는 방법은 data를 입력하고 파라미터로 전송할 때 
bubbleChart()에 넣어서 불러주면 됩니다.
> 차트의 데이터를 입력할 때 이름, 수치만 입력하던 다른 차트와는 다르게 
  label에 보여줄 이름, x축의 수치, y축의 수치, 원주율을 입력해줘야 
   다른 데이터와 크기를 다르게 원을 그려줍니다.
			</pre>
		</div>
	</div>
</section>


<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>Scatter Chart</b>
			<div id="scatter-chart"></div>
Scatter Chart 그리는 방법은 data를 입력하고 파라미터로 전송할 때
scatterChart()에 넣어서 불러주면 됩니다.
> 데이터가 많으며 어디에 많이 몰려있는지를 확인할 때 사용하면 좋습니다.
> data를 입력할 때 두 개의 기준이 있어야 하고, 그 기준에 맞춰서 값을 입력하면 
  됩니다.
			</pre>
		</div>
	</div>
</section>

<section>
	<div class="container-fluid padding-40-top">
		<div class="row">
			<pre>
<b>Pie Chart</b>
			<div id="pie-chart"></div>
가장 많이 사용하는 pie chart입니다.
Pie Chart 그리는 방법은 data를 입력하고 파라미터로 전송할 때
pieChart()에 넣어서 불러주면 됩니다.
> 데이터 입력이 다른 차트에 비해 매우 간단합니다.(예 : name : 컨텐츠A, data : 14)
> series옵션에서 showLegend: true, showLabel: true, labelAlign: 'center'를 주면
 pie chart내부에 글자를 보여줘서 알아보기 쉽게 만들어 줍니다.
<b>> series옵션에서 labelAlign: 'outer'로 입력하면 해당 컨텐츠의 label이 원 밖으로 이동합니다.</b>
> data에 수치를 입력할 때는 퍼센트로 계산해서 넣지 않아도 자동으로 전체의 퍼센트를 구해서 차트를 그려줍니다.
			</pre>
		</div>
	</div>
</section>

<c:import url="/js/ecams/common/commonscript.jsp" />
<script type="text/javascript" src="<c:url value="/js/ecams/dev/chartSample.js"/>"></script>