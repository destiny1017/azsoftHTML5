/**
 * eCAMS MAIN Frame 화면 기능 정의
 * 
 * <pre>
 * 
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-05-03
 * 
 */

var userId 		= null;
var userName	= null;
var adminYN 	= null;
var userDeptCd  = null;
var userDeptName= null;
var title_ 		= null;
var class_ 		= null;
var dateStr 	= null;
var titleStr 	= null;
var applyData	= null;
var pieChart	= null;
var barChart	= null;
var piePChart	= null;
var getApplyprogressSw 	= false;
var getPieprogressSw 	= false;
var getPiePprogressSw 	= false;
var getBarprogressSw 	= false;

var applyGrid 		= new ax5.ui.grid();
var picker 			= new ax5.ui.picker();

var defaultColor =[
    '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
    '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
]; 
var theme = {
    series: {
        series: {
            colors: [
            ]
        },
        label: {
            color: '#fff',
            fontFamily: 'sans-serif'
        }
    }
};

function getRandomColorHex(colorIndex) {
	var hex = '0123456789ABCDEF';
	var color = null;
	var colors = [];
	
	for(var j = 1; j <= colorIndex; j++) {
		color = '#';
		for(var i = 1; i <= 6; i++) { 
			color += hex[Math.floor(Math.random() * 16)];
		}
		
		colors.push(color);
		
		if(colors.length > 2 && (colors[j] == colors[j-1]))
			j--;
	}
	
	return colors;
}

$('#start_date').val(getDate('MON',-20));
$('#end_date').val(getDate('DATE',0));

picker.bind({
    target: $('[data-ax5picker="basic"]'),
    direction: "top",
    content: {
        width: 220,
        margin: 10,
        type: 'date',
        config: {
            control: {
                left: '<i class="fa fa-chevron-left"></i>',
                yearTmpl: '%s',
                monthTmpl: '%s',
                right: '<i class="fa fa-chevron-right"></i>'
            },
            dateFormat: 'yyyy/MM/dd',
            lang: {
                yearTmpl: "%s년",
                months: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
                dayTmpl: "%s"
            },dimensions: {
                height: 140,
                width : 75,
                colHeadHeight: 11,
                controlHeight: 25,
            }
        },
        formatter: {
            pattern: 'date'
        }
    },
    btns: {
        today: {
            label: "Today", onClick: function () {
                var today = new Date();
                this.self
                        .setContentValue(this.item.id, 0, ax5.util.date(today, {"return": "yyyy/MM/dd"}))
                        .setContentValue(this.item.id, 1, ax5.util.date(today, {"return": "yyyy/MM/dd"}))
                        .close();
            }
        },
        thisMonth: {
            label: "This Month", onClick: function () {
                var today = new Date();
                this.self
                        .setContentValue(this.item.id, 0, ax5.util.date(today, {"return": "yyyy/MM/01"}))
                        .setContentValue(this.item.id, 1, ax5.util.date(today, {"return": "yyyy/MM"})
                                + '/'
                                + ax5.util.daysOfMonth(today.getFullYear(), today.getMonth()))
                        .close();
            }
        },
        ok: {label: "Close", theme: "default"}
    }
    
});

picker.onStateChanged = function(){
	if(this.state === 'changeValue') {
		changeApplyLabel();
    	getApplyList();
    } 
}

function doubleClickGrid() {
	dialog.alert('선택하신 신청번호는 : '+applyGrid.list[applyGrid.selectedDataIndexs].CR_ACPTNO,function(){});
}

document.body.onresize = function() {
	
	var pieWidth = $('#pieDiv').width();
	var barWidth = $('#barDiv').width();
	var piePWidth = $('#piePDiv').width();
	
	var divMainUpHeight = parseInt($('#divMainUp').height());
	var minusHeight = 115;
	if(parseInt(window.innerHeight) < 690) { 
		minusHeight += 690 - parseInt(window.innerHeight);
	}
	
	var chartHeight = parseInt(window.innerHeight) - divMainUpHeight - minusHeight;
	
	if(chartHeight < 300) chartHeight = 300;
	
	pieChart.resize({
		width 	: pieWidth - 10,
		height	: chartHeight	
	});
	
	piePChart.resize({
		width 	: piePWidth - 10,
		height	: chartHeight	
	});
	
	barChart.resize({
		width 	: barWidth - 10,
		height	: chartHeight	
	});
	
};

function changeApplyLabel() {
	dateStr = $("#start_date").val() + " ~ " + $("#end_date").val();
	
	if($('input:radio[name=radioAppli]:checked').val() === 'myAppli')
		titleStr = '['+ userName + ']님의 '+dateStr+' 신청 목록';
		
	if($('input:radio[name=radioAppli]:checked').val() === 'teamAppli')
		titleStr = '['+ userDeptName + ']팀의 '+dateStr+' 신청 목록';
	
	$('#applyLabel').text(titleStr);
}


$('input:radio[name=radioAppli]').wRadio({theme: 'circle-classic blue', selector: 'circle-dot-blue'});
$('input.checkbox-pie').wCheck({theme: 'square-inset blue', selector: 'square-dot-blue', highlightLabel: false});

$('input:radio[name=radioAppli]').change(function(){
	changeApplyLabel();
	getApplyList();
});

$('#checkbox-pieA').bind('click', function(){
	getPieAppliKinds();
});

$('#checkbox-pieP').bind('click', function(){
	getPrgPieChart();
});

$(document).ready(function(){
	userId 		= window.top.userId;
	userName 	= window.top.userName;
	adminYN 	= window.top.adminYN;
	userDeptCd 	= window.top.userDeptCd;
	userDeptName= window.top.userDeptName;
	
	if(userName != null) changeApplyLabel();
	
	applyGrid.setConfig({
	    target: $('[data-ax5grid="applyGrid"]'),
	    sortable: true, 
	    multiSort: true,
	    showRowSelector: false,
	    header: {
	        align: "center",
	        columnHeight: 30
	    },
	    body: {
	        columnHeight: 20,
	        onClick: function () {
	        	this.self.clearSelect();
	            this.self.select(this.dindex);
	        },
	        onDBLClick: function () {
	        	doubleClickGrid();
	        },
	    	trStyleClass: function () {
	    		if(this.item.COLORSW === '5'){
	    			return "fontStyle-error";
	    		} else if (this.item.COLORSW === '3'){
	    			return "text-danger";
	    		} else if (this.item.COLORSW === '0'){
	    			return "text-info";
	    		} else {
	    			return "text-secondary"
	    		}
	    	},
	    	onDataChanged: function(){
	    		this.self.repaint();
	    	}
	    },
	    columns: [
	        {key: "CM_SYSMSG", label: "시스템",  width: '24%'},
	        {key: "CR_ACPTNO", label: "신청번호",  width: '8%'},
	        {key: "CM_USERNAME", label: "신청자",  width: '6%'},
	        {key: "REQUESTNAME", label: "신청종류",  width: '8%'},
	        {key: "PASSOK", label: "처리구분",  width: '8%'},
	        {key: "CR_ACPTDATE", label: "신청일시",  width: '8%'},
	        {key: "CONFNAME", label: "진행상태",  width: '15%'},
	        {key: "PRGNAME", label: "프로그램명",  width: '15%'},
	        {key: "CR_PRCDATE", label: "완료일시",  width: '8%'},
	    ]
	});
	
	getApplyList();
});

function makeMsg(afterStr) {
	var message = '';
	if($('input:radio[name=radioAppli]:checked').val() === 'myAppli')
		message = '['+ userName+']님의 ';
	if($('input:radio[name=radioAppli]:checked').val() === 'teamAppli')
		message = '[' + userDeptName+']팀의 ';
	
	message += afterStr;
	return message;
}

function replaceAll(str, searchStr, replaceStr) {
	return str.split(searchStr).join(replaceStr);
}

function  makeFakeData(chartKin) {
	var chartData = null;
	if(chartKin === 'PIE') chartData = [{name: '데이터가 없습니다.', data: 1}];
	return chartData;
}

function getApplyList() {
	var messageStr 		= null;
	var applyInfo 		= null;
	
	
	if(getApplyprogressSw) {
		dialog.alert('현재 신청목록을 가져오는 중입니다...', function () {});
		return;
	}
		
	if(!getApplyprogressSw)
		getApplyprogressSw = true;
	
	beForAndAfterDataLoading('BEFORE', makeMsg('신청 목록을 가져오는 중입니다.'));
	
	applyInfo 			= new Object();
	applyInfo.userId 	= userId;
	applyInfo.startDate = $("#start_date").val().substr(0,4)+ $("#start_date").val().substr(5,2)+ $("#start_date").val().substr(8,2);
	applyInfo.endDate 	= $("#end_date").val().substr(0,4) 	+ $("#end_date").val().substr(5,2) 	+ $("#end_date").val().substr(8,2);
	applyInfo.appliSw 	= $('input:radio[name=radioAppli]:checked').val();
	
	applyData = new Object();
	applyData = {
		applyInfo	: 	applyInfo,
		requestType	: 	'GETAPPLYLIST'
	}
	
	ajaxAsync('/webPage/main/eCAMSMainServlet', applyData, 'json',successGetApplyData);
}

function successGetApplyData(data) {
	applyGrid.setData(data);
	
	beForAndAfterDataLoading('AFTER', makeMsg('신청 목록 가져오기를 완료 했습니다.'));
	getApplyprogressSw = false;
	
	
	getPieAppliKinds();
	getPrgPieChart();
	getBarChart();
}



function getPieAppliKinds(){
	
	if(getPieprogressSw)	return;
	if(!getPieprogressSw)	getPieprogressSw= true;
	
	beForAndAfterDataLoading('BEFORE', makeMsg('신청 별 종류를 가져오는 중입니다.'));
	
	
	$('#checkbox-pieA').is(':checked') ? 
			applyData.pieCloseSw = 'Y' : 
				applyData.pieCloseSw = 'N';
	applyData.requestType = 'GETAPPLYLIPIE';
	ajaxAsync('/webPage/main/eCAMSMainServlet', applyData, 'json',successGetPieData);
	
}

function successGetPieData(data) {
	if(data.length === 0 ){
		data = makeFakeData('PIE');
	}
	
	var width = $('#pieDiv').width();
	var divMainUpHeight = parseInt($('#divMainUp').height());
	var minusHeight = 115;
	if(parseInt(window.innerHeight) < 690) { 
		minusHeight += 690 - parseInt(window.innerHeight);
	}
	var pieChartHeight = parseInt(window.innerHeight) - divMainUpHeight - minusHeight;
	
	
	if($('#pieAppliKinds').length > 0) $('#pieAppliKinds').empty();
	
	$('#pieTLabel_A').text(titleStr.substring(0,titleStr.length-2) + '별 종류');
	
	var container = document.getElementById('pieAppliKinds');
	var data = {
	    categories: ['신청종류'],
	    series: data
	};
	
	var options = {
	    chart: {
	        width: 	 width - 10,
	        height:  pieChartHeight,
	    },
	    series: {
	        labelAlign: 'center',
	        radiusRange: ['70%', '100%']
	    },
	    legend: {
	        visible: true,
	        
	    }
	};
	theme.series.series.colors = getRandomColorHex(data.series.length);
	options.theme = 'myTheme';
	tui.chart.registerTheme('myTheme', theme);
	pieChart = tui.chart.pieChart(container, data, options);
	
	beForAndAfterDataLoading('AFTER', makeMsg(' 신청 별 종류 가져오기를 완료했습니다.'));
	getPieprogressSw = false;
}

function getPrgPieChart() {
	
	if(getPiePprogressSw)	return;
	if(!getPiePprogressSw)	getPiePprogressSw= true;
	
	beForAndAfterDataLoading('BEFORE', makeMsg('신청건 프로그램별 종류를 가져오는 중입니다.'));
	
	$('#checkbox-pieP').is(':checked') ? 
			applyData.piePCloseSw = 'Y' : 
				applyData.piePCloseSw = 'N';
	applyData.requestType = 'GETPRGPIE';
	ajaxAsync('/webPage/main/eCAMSMainServlet', applyData, 'json',successGetPRGPiePData);
}

function successGetPRGPiePData(data) {
	if(data.length === 0 ){
		data = makeFakeData('PIE');
	}
	
	var width = $('#piePDiv').width();
	var divMainUpHeight = parseInt($('#divMainUp').height());
	var minusHeight = 115;
	if(parseInt(window.innerHeight) < 690) { 
		minusHeight += 690 - parseInt(window.innerHeight);
	}
	var pieChartHeight = parseInt(window.innerHeight) - divMainUpHeight - minusHeight;
	
	
	if($('#piePChart').length > 0) $('#piePChart').empty();
	
	$('#pieTLabel_P').text(titleStr.substring(0,titleStr.length-5) + '신청건의 프로그램 종류');
	
	var container = document.getElementById('piePChart');
	var data = {
	    categories: ['신청종류'],
	    series: data
	};
	
	var options = {
	    chart: {
	        width: 	 width - 10,
	        height:  pieChartHeight,
	    },
	    series: {
	        labelAlign: 'center',
	        radiusRange: ['70%', '100%'],
	        //showLabel: true
	    },
	    legend: {
	        visible: true
	    }
	};
	theme.series.series.colors = getRandomColorHex(data.series.length);
	options.theme = 'myTheme';
	tui.chart.registerTheme('myTheme', theme);
	piePChart = tui.chart.pieChart(container, data, options);
	
	beForAndAfterDataLoading('AFTER', makeMsg(' 신청건 프로그램별 종류 가져오기를 완료했습니다.'));
	getPiePprogressSw = false;
}

function successGetBarData(bardata) {
	var width = $('#barDiv').width();
	var divMainUpHeight = parseInt($('#divMainUp').height());
	var minusHeight = 115;
	if(parseInt(window.innerHeight) < 690) { 
		minusHeight += 690 - parseInt(window.innerHeight);
	}
	var barChartHeight = parseInt(window.innerHeight) - divMainUpHeight - minusHeight;
	
	
	if($('#barChart').length > 0) $('#barChart').empty();
	
	$('#barTLabel').text(titleStr.substring(0,titleStr.length-5) + ' 시스템별 신청 건수');
	
	var container = document.getElementById('barChart');
	var options = {
		    chart: {
		        width: width - 10,
		        height: barChartHeight
		    },
		    xAxis: {
		        title: '신청종류'
		    },
		    yAxis: {
		        title: '시스템별 신청건수'
		    },
		    series: {
		        stackType: 'normal'        
		    },
		    legend : {
		    	align: 'bottom'
		    },
		    tooltip: {
		        grouped: true
		    }
		};
	
	
	barChart = tui.chart.columnChart(container, bardata, options);
	
	beForAndAfterDataLoading('AFTER', '신청 목록 가져오기를 완료 했습니다.');
	getApplyprogressSw = false;
}

function getBarChart() {
	
	applyData.requestType = 'GETBARCHART';
	ajaxAsync('/webPage/main/eCAMSMainServlet', applyData, 'json',successGetBarData);
}


//그리드에 마우스 툴팁 달기
/*
$(document).on("mouseenter","[data-ax5grid-panel='body'] span",function(event){
	if(this.innerHTML == ""){
		return;
	}
	
	//첫번째 컬럼에만 툴팁 달기
	if($(this).closest("td").index() != 7) return;
	
	//그리드 정보 가져오기
	var gridRowInfo = applyGrid.list[parseInt($(this).closest("td").closest("tr").attr("data-ax5grid-tr-data-index"))];
	var contents = gridRowInfo.PRGLIST;
	
	console.log(contents);
	
	if(contents === undefined) return;
	contents = replaceAll(contents,',','<br>');
	
	$(this).attr("title",contents);
	
	console.log($(this));
	console.log($(this).attr("title"));
	
	// title을 변수에 저장
	title_ = $(this).attr("title");
	// class를 변수에 저장
	class_ = $(this).attr("class");
	// title 속성 삭제 ( 기본 툴팁 기능 방지 )
	$(this).attr("title","");
	
	$("body").append("<div id='tip'></div>");
	if (class_ == "img") {
		$("#tip").html(imgTag);
		$("#tip").css("width","100px");
	} else {
		$("#tip").css("width","300px");
		$("#tip").html(title_);
	}
	
	var pageX = event.pageX;
	var pageY = event.pageY;
	
	$("#tip").css({left : pageX + "px", top : pageY + "px"}).fadeIn(500);
	return;
}).on('mouseleave',"[data-ax5grid-panel='body'] span",function(){
	title_ = null;
	class_ = null;
	
	$(this).attr("title", title_);
	$("#tip").remove();	
	
});*/


