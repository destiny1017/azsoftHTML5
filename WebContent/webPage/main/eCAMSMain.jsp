<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="/webPage/common/common.jsp" />




<div class="row">
	<div class="hpanel">
	    <div class="panel-body text-center h-200">
	        <div id="chart"></div>
	    </div>
	    <div class="panel-footer">
	        Line Chart 예제 입니다.
	    </div>
	</div>
</div>

<div class="row">
	<div class="col-sm-6">
		<div class="hpanel">
			<div class="panel-body text-center h-200">
				<div id="char2" style="height: 200px;"></div>
			</div>
			<div class="panel-footer">
		        Pie Chart 예제 입니다.
		    </div>
		</div>
	</div>
	
	<div class="col-sm-6">
		<div class="hpanel">
			<div class="panel-body text-center h-200">
				<div id="char3" style="height: 200px;"></div>
			</div>
			<div class="panel-footer">
		        Donut Chart 예제 입니다.
		    </div>
		</div>
	</div>
</div>

<c:import url="/js/ecams/common/commonscript.jsp" />


<script>

var toast = new ax5.ui.toast();				//토스트사용
toast.setConfig({
	containerPosition: "top-right",
	displayTime:10000
});

var chart = c3.generate({
    bindto: '#chart',
    data: {
      columns: [
        ['eCAMS', '30', '200', '100', '400', '150', '250'],
        ['Main', '50', '20', '10', '40', '15', '25'],
        ['Page', '67', '28', '39', '5', '387', '191']
      ]
    }
});

var chart2 = c3.generate({
    bindto: '#char2',
	data: {
        // iris data from R
        columns: [
            ['data1', 30],
            ['data2', 120],
        ],
        type : 'pie',
        onclick: function (d, i) { console.log("onclick", d, i); },
        onmouseover: function (d, i) { console.log("onmouseover", d, i); },
        onmouseout: function (d, i) { console.log("onmouseout", d, i); }
    }
});

var columnsKeys = ['eCMAS','Main','Page'];

setTimeout(function () {
	chart2.load({
        columns: [
            [columnsKeys[0], 40],
            [columnsKeys[1], 85],
            [columnsKeys[2], 22],
        ]
    });
}, 1500);

setTimeout(function () {
	chart2.unload({
        ids: 'data1'
    });
	chart2.unload({
        ids: 'data2'
    });
}, 2500);


var chart3 = c3.generate({
	bindto: '#char3',
	data: {
        columns: [
            ['data1', 30],
            ['data2', 120],
        ],
        type : 'donut',
        onclick: function (d, i) { console.log("onclick", d, i); },
        onmouseover: function (d, i) { console.log("onmouseover", d, i); },
        onmouseout: function (d, i) { console.log("onmouseout", d, i); }
    },
    donut: {
        title: "eCMAS Main"
    }
});

/* setInterval(function(){
	toast.push({
        theme: 'info',
        icon:  '<i class="fa fa-bell"></i>',
        msg:   'eCAMS MAIN PAGE 입니다.',
        closeIcon: '<i class="fa fa-times"></i>'
    });
},1500); */


</script>