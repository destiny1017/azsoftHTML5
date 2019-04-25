/**
 * 차트 종류별 모음
 * 
 * 	작성자: 이성현
 * 	버전 : 1.0
 *  수정일 : 2019-04-18
 * 
 */

$(document).ready(function() {
	bar_chart();	// line 29
	negative_chart();	// line 80
	stack_chart();		// line 134
	diverging_chart();	// line 192
	range_chart();		// line 241
	column_chart();		// line 288
	negative_column_chart();	// line 345
	percent_column_chart();		// line 404
	group_column_chart();		// line 467
	line_chart();				// line 535
	spline_chart();				// line 618
	area_chart();				// line 676
	stack_area_chart();			// line 731
	bubble_chart();				// line 794
	scatter_chart();			// line 968
	pie_chart();				// line 1538
})

function bar_chart(){
	var container = document.getElementById('bar-chart');	// 차트를 그리고 싶은 div 태그의 ID를 할당합니다.
	var data = {
	    categories: ['June', 'July', 'Aug', 'Sep', 'Oct', 'Nov'],
	    series: [
	        {
	            name: 'Budget',
	            data: [5000, 3000, 5000, 7000, 6000, 4000]
	        },
	        {
	            name: 'Income',
	            data: [8000, 1000, 7000, 2000, 5000, 3000]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: 'Monthly Revenue',
	        format: '1,000'
	    },
	    yAxis: {
	        title: 'Month'
	    },
	    xAxis: {
	        title: 'Amount',
	        min: 0,
	        max: 9000,
	        suffix: '$'
	    },
	     series: {
	         showLabel: true
	     }
	};
	
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};

	// theme에 설정한 순서대로 색상을 결정할 수 있습니다.
	//tui.chart.registerTheme('myTheme', theme);
	//options.theme = 'myTheme';

	tui.chart.barChart(container, data, options);
}

function negative_chart(){
	var container = document.getElementById('negative-chart'); // 차트를 그리고 싶은 div 태그의 ID를 할당합니다.
	var data = {
	    categories: ['May', 'June', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
	    series: [
	        {
	            name: 'Budget',
	            data: [4000, 5000, 3000, 5000, 7000, 6000, 4000, 1000]
	        },
	        {
	            name: 'Income',
	            data: [7000, 8000, 1000, 7000, 2000, 7000, 3000, 5000]
	        },
	        {
	            name: 'Expenses',
	            data: [-5000, -4000, -4000, -6000, -3000, -4000, -5000, -7000]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: 'Monthly Revenue',
	        format: '1,000'
	    },
	    yAxis: {
	        title: 'Month'
	    },
	    xAxis: {
	        title: 'Amount',
	        min: -10000,
	        max: 10000
	    },
	    series: {
	        showLabel: true
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};

	// 원하는 색상으로 변경
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';

	tui.chart.barChart(container, data, options);
}

function stack_chart(){
	var container = document.getElementById('stack-chart');
	var data = {
	    categories: ['June', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
	    series: [
	        {
	            name: 'Budget',
	            data: [5000, 3000, 5000, 7000, 6000, 4000, 1000]
	        },
	        {
	            name: 'Income',
	            data: [8000, 1000, 7000, 2000, 6000, 3000, 5000]
	        },
	        {
	            name: 'Expenses',
	            data: [4000, 4000, 6000, 3000, 4000, 5000, 7000]
	        },
	        {
	            name: 'Debt',
	            data: [6000, 3000, 3000, 1000, 2000, 4000, 3000]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: 'Monthly Revenue',
	        'format': '1,000'
	    },
	    yAxis: {
	        title: 'Month'
	    },
	    xAxis: {
	        title: 'Amount',
	        max: 24000
	    },
	    series: {
	    	//showLabel : true,  // showLabel이 들어가면 차트에 수치를 같이 그려줍니다.
	        stackType: 'normal'
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};

	// 원하는 색상으로 변경
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';

	tui.chart.barChart(container, data, options);
}

function diverging_chart(){
	var container = document.getElementById('diverging-chart');
	var data = {
	    categories: ['100 ~', '90 ~ 99', '80 ~ 89', '70 ~ 79', '60 ~ 69', '50 ~ 59', '40 ~ 49', '30 ~ 39', '20 ~ 29', '10 ~ 19', '0 ~ 9'],
	    series: [
	        {
	            name: 'Male',
	            data: [3832, 38696, 395906, 1366738, 2482657, 4198869, 4510524, 3911135, 3526321, 2966126, 2362433]
	        },
	        {
	            name: 'Female',
	            data: [121160, 128464, 839761, 1807901, 2630336, 4128479, 4359815, 3743214, 3170926, 2724383, 2232516]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: 'Population Distribution',
	        format: '1,000'
	    },
	    yAxis: {
	        title: 'Age Group',
	        align: 'center'
	    },
	    xAxis: {
	        labelMargin: 10
	    },
	    series: {
	        diverging: true
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};

	// 원하는 색상으로 변경
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';

	tui.chart.barChart(container, data, options);
}

function range_chart(){
	var container = document.getElementById('range-chart');
	var data = {
	    categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'June', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
	    series: [
	        {
	            name: 'Seoul',
	            data: [[-8.3, 0.3], [-5.8, 3.1], [-0.6, 9.1], [5.8, 16.9], [11.5, 22.6], [16.6, 26.6], [21.2, 28.8], [21.8, 30.0], [15.8, 25.6], [8.3, 19.6], [1.4, 11.1], [-5.2, 3.2]]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: 'Temperature Range'
	    },
	    yAxis: {
	        title: 'Month'
	    },
	    xAxis: {
	        title: 'Temperature (Celsius)'
	    },
	    series: {
	        showLabel: false,
	        barWidth: 25
	    },
	    tooltip: {
	        suffix: '°C'
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};

	// 원하는 색상으로 변경
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';

	tui.chart.barChart(container, data, options);
}

function column_chart(){
	var container = document.getElementById('column-chart');
	var data = {
	    categories: ['June, 2015', 'July, 2015', 'August, 2015', 'September, 2015', 'October, 2015', 'November, 2015', 'December, 2015'],
	    series: [
	        {
	            name: 'Budget',
	            data: [5000, 3000, 5000, 7000, 6000, 4000, 1000]
	        },
	        {
	            name: 'Income',
	            data: [8000, 1000, 7000, 2000, 6000, 3000, 5000]
	        },
	        {
	            name: 'Expenses',
	            data: [4000, 4000, 6000, 3000, 4000, 5000, 7000]
	        },
	        {
	            name: 'Debt',
	            data: [6000, 3000, 3000, 1000, 2000, 4000, 3000]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: 'Monthly Revenue',
	        format: '1,000'
	    },
	    yAxis: {
	        title: 'Amount',
	        min: 0,
	        max: 9000
	    },
	    xAxis: {
	        title: 'Month'
	    },
	    legend: {
	        align: 'top'
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};
	
	// 원하는 색상으로 변경
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';
	tui.chart.columnChart(container, data, options);
}

function negative_column_chart(){
	var container = document.getElementById('negative-column-chart');
	var data = {
	    categories: ['May', 'June', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
	    series: [
	        {
	            name: 'Budget',
	            data: [4000, 5000, 3000, 5000, 7000, 6000, 4000, 1000]
	        },
	        {
	            name: 'Income',
	            data: [7000, 8000, 1000, 7000, 2000, 7000, 3000, 5000]
	        },
	        {
	            name: 'Expenses',
	            data: [-5000, -4000, -4000, -6000, -3000, -4000, -5000, -7000]
	        },
	        {
	            name: 'Debt',
	            data: [-3000, -6000, -3000, -3000, -1000, -2000, -4000, -3000]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: 'Monthly Revenue',
	        format: "1,000"
	    },
	    yAxis: {
	        title: 'Amount'
	        //,
	       // min: -10000,
	       // max: 10000
	    },
	    xAxis: {
	        title: 'Month'
	    },
	    legend: {
	        visible: false
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};

	// 원하는 색상을 입힐 수 있습니다.
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';

	tui.chart.columnChart(container, data, options);
}

function percent_column_chart(){
	var container = document.getElementById('percent-column-chart');
	var data = {
	    categories: ['May', 'June', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
	    series: [
	        {
	            name: 'Budget',
	            data: [4000, 5000, 3000, 5000, 7000, 6000, 4000, 1000]
	        },
	        {
	            name: 'Income',
	            data: [7000, 8000, 1000, 7000, 2000, 7000, 3000, 5000]
	        },
	        {
	            name: 'Expenses',
	            data: [-5000, -4000, -4000, -6000, -3000, -4000, -5000, -7000]
	        },
	        {
	            name: 'Debt',
	            data: [-3000, -6000, -3000, -3000, -1000, -2000, -4000, -3000]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: 'Monthly Revenue',
	        format: '1,000'
	    },
	    yAxis: {
	        title: 'Amount'
	    },
	    xAxis: {
	        title: 'Month'
	    },
	    series: {
	        stackType: 'percent',
	        barWidth: 60
	    },
	    tooltip: {
	        grouped: true
	    },
	    legend: {
	        align: 'bottom'
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};

	// 원하는 색깔을 입힐수 있습니다.
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';

	tui.chart.columnChart(container, data, options);
}

function group_column_chart(){
	var container = document.getElementById('group-column-chart');
	var data = {
	    categories: ["0 ~ 9", "10 ~ 19", "20 ~ 29", "30 ~ 39", "40 ~ 49", "50 ~ 59", "60 ~ 69", "70 ~ 79", "80 ~ 89", "90 ~ 99", "100 ~"],
	    series: [
	        {
	            name: 'Male - Seoul',
	            data: [400718, 506749, 722122, 835851, 850007, 773094, 496232, 267037, 67004, 7769, 1314],
	            stack: 'Male'
	        },
	        {
	            name: 'Female - Seoul',
	            data: [380595, 472893, 724408, 829149, 853032, 812687, 548381, 316142, 127406, 22177, 3770],
	            stack: 'Female'
	        },
	        {
	            name: 'Male - Incheon',
	            data: [139283, 167132, 209256, 233977, 261195, 251151, 127721, 61452, 17138, 1974, 194],
	            stack: 'Male'
	        },
	        {
	            name: 'Female - Incheon',
	            data: [132088, 155895, 192760, 221250, 255601, 243374, 130406, 80763, 38005, 6057, 523],
	            stack: 'Female'
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: 'Population Distribution',
	        format: '1,000'
	    },
	    xAxis: {
	        title: 'Age Group'
	    },
	    yAxis: {
	        title: 'Amount'
	    },
	    series: {
	        stackType: 'normal'
	        //,
	        //diverging: true		        
	    },
	    legend: {
	        align: 'bottom'
	    },
	    tooltip: {
	        grouped: true
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};

	// 원하는 색상으로 가능
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';

	tui.chart.columnChart(container, data, options);
}

function line_chart(){
	var container = document.getElementById('line-chart');
	var data = {
	    categories: ['01/01/2016', '02/01/2016', '03/01/2016', '04/01/2016', '05/01/2016', '06/01/2016', '07/01/2016', '08/01/2016', '09/01/2016', '10/01/2016', '11/01/2016', '12/01/2016'],
	    series: [
	        {
	            name: 'Seoul',
	            data: [-3.5, -1.1, 4.0, 11.3, 17.5, 21.5, 24.9, 25.2, 20.4, 13.9, 6.6, -0.6]
	        },
	        {
	            name: 'Seattle',
	            data: [3.8, 5.6, 7.0, 9.1, 12.4, 15.3, 17.5, 17.8, 15.0, 10.6, 6.4, 3.7]
	        },
	        {
	            name: 'Sydney',
	            data: [22.1, 22.0, 20.9, 18.3, 15.2, 12.8, 11.8, 13.0, 15.2, 17.6, 19.4, 21.2]
	        },
	        {
	            name: 'Moskva',
	            data: [-10.3, -9.1, -4.1, 4.4, 12.2, 16.3, 18.5, 16.7, 10.9, 4.2, -2.0, -7.5]
	        },
	        {
	            name: 'Jungfrau',
	            data: [-13.2, -13.7, -13.1, -10.3, -6.1, -3.2, 0.0, -0.1, -1.8, -4.5, -9.0, -10.9]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: '24-hr Average Temperature'
	    },
	    yAxis: {
	        title: 'Temperature (Celsius)'
	    },
	    xAxis: {
	        title: 'Month',
	        pointOnColumn: true,
	        dateFormat: 'MMM',
	        tickInterval: 'auto'
	    },
	    series: {
	        showDot: false,
	        zoomable: true
	    },
	    tooltip: {
	        suffix: '°C'
	    },
	    plot: {
	        bands: [
	            {
	                range: ['03/01/2016', '05/01/2016'],
	                color: 'gray',
	                opacity: 0.2
	            }
	        ],
	        lines: [
	            {
	                value: '03/01/2016',
	                color: '#fa2828'
	            },{
	            	value: '05/01/2016',
	                color: '#fa2828'
	            }
	        ]
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};
	
	// 원하는 색상으로 가능
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';
	var chart = tui.chart.lineChart(container, data, options);
}

function spline_chart(){
	var container = document.getElementById('spline-line-chart');
	var data = {
	    categories: ['June', 'July', 'Aug', 'Sep', 'Oct', 'Nov'],
	    series: [
	        {
	            name: 'Budget',
	            data: [5000, 3000, 6000, 3000, 6000, 4000]
	        },
	        {
	            name: 'Income',
	            data: [8000, 1000, 7000, null, 5000, 3000]
	        },
	        {
	            name: 'Outgo',
	            data: [900, 6000, 1000, 0, 3000, 1000]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: '24-hr Average Temperature'
	    },
	    yAxis: {
	        title: 'Amount',
	        pointOnColumn: true
	    },
	    xAxis: {
	        title: 'Month'
	    },
	    series: {
	        spline: true,
	        showDot: false
	    },
	    tooltip: {
	        suffix: '°C',
	        grouped: true
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};

	// 원하는 색상으로 가능
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';

	tui.chart.lineChart(container, data, options);
}


function area_chart(){
	var container = document.getElementById('area-chart');
	var data = {
	    categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'June', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
	    series: [
	        {
	            name: 'Seoul',
	            data: [20, 40, 25, 50, 15, 45, 33, 34, 20, 30, 22, 13]
	        },
	        {
	            name: 'Sydney',
	            data: [5, 30, 21, 18, 59, 50, 28, 33, 7, 20, 10, 30]
	        },
	        {
	            name: 'Moskva',
	            data: [30, 5, 18, 21, 33, 41, 29, 15, 30, 10, 33, 5]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: '24-hr Average Temperature'
	    },
	    series: {
	        zoomable: true,
	        showDot: false,
	        areaOpacity: 1
	    },
	    yAxis: {
	        title: 'Temperature (Celsius)',
	        pointOnColumn: true
	    },
	    xAxis: {
	        title: 'Month'
	    },
	    tooltip: {
	        suffix: '°C'
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};
	// 원하는 색상으로 적용
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';
	tui.chart.areaChart(container, data, options);
}

function stack_area_chart(){
	var container = document.getElementById('stack-area-chart');
	var data = {
	    categories: ['June', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
	    series: [
	        {
	            name: 'Budget',
	            data: [5000, 3000, 5000, 7000, 6000, 4000, 1000]
	        },
	        {
	            name: 'Income',
	            data: [8000, 4000, 7000, 2000, 6000, 3000, 5000]
	        },
	        {
	            name: 'Expenses',
	            data: [4000, 4000, 6000, 3000, 4000, 5000, 7000]
	        },
	        {
	            name: 'Debt',
	            data: [3000, 4000, 3000, 1000, 2000, 4000, 3000]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: 'Monthly Revenue',
	        format: '1,000'
	    },
	    yAxis: {
	        title: 'Amount',
	        max: 24000,
	        pointOnColumn: true
	    },
	    xAxis: {
	        title: 'Month'
	    },
	    series: {
	        stackType: 'normal',
	        showDot: false,
	        areaOpacity: 1
	    },
	    tooltip: {
	        grouped: true
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};

	// 원하는 색상으로 꾸미기
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';

	tui.chart.areaChart(container, data, options);
}

function bubble_chart(){
	var container = document.getElementById('bubble-chart');
	var data = {
	    series: [
	        {
	            name: 'Africa',
	            data: [
	                { x: 4200, y: 70.35, r: 32209101, label: 'Morocco' },
	                { x: 4200, y: 70.71, r: 76117421, label: 'Egypt' },
	                { x: 5900, y: 56.46, r: 1355246, label: 'Gabon' },
	                { x: 6600, y: 72.74, r: 32129324, label: 'Algeria' },
	                { x: 6700, y: 76.28, r: 5631585, label: 'Libya' },
	                { x: 7100, y: 74.66, r: 9974722, label: 'Tunisia' },
	                { x: 10500, y: 69.28, r: 1096585, label: 'Trinidad and Tobago' },
	                { x: 12800, y: 72.09, r: 1220481, label: 'Mauritius' },
	                { x: 18200, y: 78.68, r: 396851, label: 'Malta' }
	            ]
	        },
	        {
	            name: 'America',
	            data: [
	                { x: 4800, y: 74.64, r: 6191368, label: 'Paraguay' },
	                { x: 4900, y: 70.92, r: 6587541, label: 'El Salvador' },
	                { x: 5600, y: 69.22, r: 2754430, label: 'Peru' },
	                { x: 5800, y: 74.06, r: 2501738, label: 'Venezuela' },
	                { x: 6300, y: 67.63, r: 8833634, label: 'Dominican Republic' },
	                { x: 6500, y: 67.43, r: 272945, label: 'Belize' },
	                { x: 6600, y: 71.43, r: 4231077, label: 'Colombia' },
	                { x: 6900, y: 72.14, r: 3000463, label: 'Panama' },
	                { x: 8100, y: 71.41, r: 78410118, label: 'Brazil' },
	                { x: 9600, y: 76.63, r: 3956507, label: 'Costa Rica' },
	                { x: 9600, y: 74.94, r: 4495959, label: 'Mexico' },
	                { x: 12400, y: 75.7, r: 6914475, label: 'Argentina' },
	                { x: 14500, y: 75.92, r: 3399237, label: 'Uruguay' },
	                { x: 16400, y: 71.64, r: 278289, label: 'Barbados' },
	                { x: 17700, y: 65.63, r: 299697, label: 'Bahamas, The' },
	                { x: 17700, y: 77.49, r: 3897960, label: 'Puerto Rico' },
	                { x: 31500, y: 79.96, r: 32507874, label: 'Canada' },
	                { x: 32100, y: 77.43, r: 89302754, label: 'United States' }
	            ]
	        },
	        {
	            name: 'Asia',
	            data: [
	                { x: 5600, y: 71.96, r: 92988000, label: 'China' },
	                { x: 5700, y: 61.29, r: 4863169, label: 'Turkmenistan' },
	                { x: 7700, y: 69.66, r: 19018924, label: 'Iran' },
	                { x: 7800, y: 66.07, r: 1514370, label: 'Kazakhstan' },
	                { x: 8100, y: 71.41, r: 14865523, label: 'Thailand' },
	                { x: 9700, y: 71.95, r: 23522482, label: 'Malaysia' },
	                { x: 12000, y: 75.23, r: 25795938, label: 'Saudi Arabia' },
	                { x: 13100, y: 72.85, r: 2903165, label: 'Oman' },
	                { x: 19200, y: 75.58, r: 48598170, label: 'Korea, South' },
	                { x: 19200, y: 73.98, r: 677886, label: 'Bahrain' },
	                { x: 20800, y: 79.17, r: 6199008, label: 'Israel' },
	                { x: 21300, y: 76.84, r: 2257549, label: 'Kuwait' },
	                { x: 23200, y: 73.4, r: 840290, label: 'Qatar' },
	                { x: 25200, y: 74.99, r: 2523915, label: 'United Arab Emirates' },
	                { x: 25300, y: 77.06, r: 22749838, label: 'Taiwan' },
	                { x: 27800, y: 81.53, r: 4353893, label: 'Singapore' },
	                { x: 29400, y: 81.04, r: 52733300, label: 'Japan' },
	                { x: 34200, y: 81.39, r: 6855125, label: 'Hong Kong' }
	            ]
	        },
	        {
	            name: 'Europe',
	            data: [
	                { x: 7700, y: 71.12, r: 2235555, label: 'Romania' },
	                { x: 8200, y: 71.75, r: 7517973, label: 'Bulgaria' },
	                { x: 9800, y: 66.39, r: 54378233, label: 'Russia' },
	                { x: 10700, y: 76.38, r: 1582395, label: 'Chile' },
	                { x: 11200, y: 74.14, r: 4496869, label: 'Croatia' },
	                { x: 11500, y: 70.86, r: 2306306, label: 'Latvia' },
	                { x: 12000, y: 74.16, r: 38626349, label: 'Poland' },
	                { x: 12500, y: 73.46, r: 3607899, label: 'Lithuania' },
	                { x: 14300, y: 71.38, r: 1341664, label: 'Estonia' },
	                { x: 14500, y: 74.19, r: 5423567, label: 'Slovakia' },
	                { x: 14900, y: 72.25, r: 1003237, label: 'Hungary' },
	                { x: 16800, y: 75.78, r: 1024617, label: 'Czech Republic' },
	                { x: 17900, y: 77.35, r: 1052414, label: 'Portugal' },
	                { x: 19600, y: 75.93, r: 2011473, label: 'Slovenia' },
	                { x: 21300, y: 78.94, r: 10647529, label: 'Greece' },
	                { x: 23300, y: 79.37, r: 40280780, label: 'Spain' },
	                { x: 27700, y: 79.54, r: 58057477, label: 'Italy' },
	                { x: 28400, y: 80.3, r: 898640, label: 'Sweden' },
	                { x: 28700, y: 78.54, r: 22424609, label: 'Germany' },
	                { x: 28700, y: 79.44, r: 30424213, label: 'France' },
	                { x: 29000, y: 78.24, r: 5214512, label: 'Finland' },
	                { x: 29500, y: 78.68, r: 16318199, label: 'Netherlands' },
	                { x: 29600, y: 78.27, r: 60270708, label: 'United Kingdom' },
	                { x: 30600, y: 78.44, r: 10348276, label: 'Belgium' },
	                { x: 31300, y: 78.87, r: 8174762, label: 'Austria' },
	                { x: 31900, y: 77.36, r: 3969558, label: 'Ireland' },
	                { x: 31900, y: 80.18, r: 293966, label: 'Iceland' },
	                { x: 32200, y: 77.44, r: 5413392, label: 'Denmark' },
	                { x: 33800, y: 80.31, r: 7450867, label: 'Switzerland' }
	            ]
	        },
	        {
	            name: 'Oceania',
	            data: [
	                { x: 2200, y: 64.56, r: 5420280, label: 'Papua New Guinea' },
	                { x: 2700, y: 61.32, r: 100798, label: 'Kiribati' },
	                { x: 5900, y: 69.2, r: 880874, label: 'Fiji' },
	                { x: 14500, y: 78.75, r: 108775, label: 'Virgin Islands' },
	                { x: 23200, y: 78.49, r: 1993817, label: 'New Zealand' },
	                { x: 30700, y: 80.26, r: 5991314, label: 'Australia' }
	            ]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: 'Life Expectancy per GDP',
	        format: function (value, chartType, areaType, valueType) {
	            if (valueType === 'r' || valueType === 'x') {
	                value = tui.chart.renderUtil.formatToComma(value);
	                if (valueType === 'x') {
	                    value = '$' + value;
	                }
	            }
	            return value;
	        }
	    },
	    yAxis: {
	        title: 'Life Expectancy (years)'
	    },
	    xAxis: {
	        title: 'GDP'
	    },
	    tooltip: {
	        template: function (category, items) {
	            return '<div class="tui-chart-default-tooltip">' +
	                '<div class="tui-chart-tooltip-head">' +
	                    '<span class="tui-chart-legend-rect" style="' + items.cssText + '; width: 10px; height: 10px"></span>' +
	                    '<span>' + items.legend + '</span>' +
	                    '<span>' + items.label + '</span>' +
	                '</div>' +
	                '<table class="tui-chart-tooltip-body">' +
	                '<tr>' +
	                    '<td>GDP</td>' +
	                    '<td class="tui-chart-tooltip-value">' + items.x + '</td>' +
	                '</tr>' +
	                '<tr>' +
	                    '<td>Life Expectancy</td>' +
	                    '<td class="tui-chart-tooltip-value">' + items.y + '</td>' +
	                '</tr>' +
	                '<tr>' +
	                    '<td>Population</td>' +
	                    '<td class="tui-chart-tooltip-value">' + items.r + '</td>' +
	                '</tr>' +
	                '</table>' + 
	            '</div>';
	        }
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};

	// 원하는 색상으로 변경 가능
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';

	tui.chart.bubbleChart(container, data, options);
}

function scatter_chart(){
	var container = document.getElementById('scatter-chart');
	var data = {
	    series: [
	        {
	            name: 'male',
	            data: [
	                { x: 174, y: 65.6 },
	                { x: 175.3, y: 71.8 },
	                { x: 193.5, y: 80.7 },
	                { x: 186.5, y: 72.6 },
	                { x: 187.2, y: 78.8 },
	                { x: 181.5, y: 74.8 },
	                { x: 184, y: 86.4 },
	                { x: 184.5, y: 78.4 },
	                { x: 175, y: 62 },
	                { x: 184, y: 81.6 },
	                { x: 180, y: 76.6 },
	                { x: 177.8, y: 83.6 },
	                { x: 192, y: 90 },
	                { x: 176, y: 74.6 },
	                { x: 174, y: 71 },
	                { x: 184, y: 79.6 },
	                { x: 192.7, y: 93.8 },
	                { x: 171.5, y: 70 },
	                { x: 173, y: 72.4 },
	                { x: 176, y: 85.9 },
	                { x: 176, y: 78.8 },
	                { x: 180.5, y: 77.8 },
	                { x: 172.7, y: 66.2 },
	                { x: 176, y: 86.4 },
	                { x: 173.5, y: 81.8 },
	                { x: 178, y: 89.6 },
	                { x: 180.3, y: 82.8 },
	                { x: 180.3, y: 76.4 },
	                { x: 164.5, y: 63.2 },
	                { x: 173, y: 60.9 },
	                { x: 183.5, y: 74.8 },
	                { x: 175.5, y: 70 },
	                { x: 188, y: 72.4 },
	                { x: 189.2, y: 84.1 },
	                { x: 172.8, y: 69.1 },
	                { x: 170, y: 59.5 },
	                { x: 182, y: 67.2 },
	                { x: 170, y: 61.3 },
	                { x: 177.8, y: 68.6 },
	                { x: 184.2, y: 80.1 },
	                { x: 186.7, y: 87.8 },
	                { x: 171.4, y: 84.7 },
	                { x: 172.7, y: 73.4 },
	                { x: 175.3, y: 72.1 },
	                { x: 180.3, y: 82.6 },
	                { x: 182.9, y: 88.7 },
	                { x: 188, y: 84.1 },
	                { x: 177.2, y: 94.1 },
	                { x: 172.1, y: 74.9 },
	                { x: 167, y: 59.1 },
	                { x: 169.5, y: 75.6 },
	                { x: 174, y: 86.2 },
	                { x: 172.7, y: 75.3 },
	                { x: 182.2, y: 87.1 },
	                { x: 164.1, y: 55.2 },
	                { x: 163, y: 57 },
	                { x: 171.5, y: 61.4 },
	                { x: 184.2, y: 76.8 },
	                { x: 174, y: 86.8 },
	                { x: 174, y: 72.2 },
	                { x: 177, y: 71.6 },
	                { x: 186, y: 84.8 },
	                { x: 167, y: 68.2 },
	                { x: 171.8, y: 66.1 },
	                { x: 182, y: 72 },
	                { x: 167, y: 64.6 },
	                { x: 177.8, y: 74.8 },
	                { x: 164.5, y: 70 },
	                { x: 192, y: 101.6 },
	                { x: 175.5, y: 63.2 },
	                { x: 171.2, y: 79.1 },
	                { x: 181.6, y: 78.9 },
	                { x: 167.4, y: 67.7 },
	                { x: 181.1, y: 66 },
	                { x: 177, y: 68.2 },
	                { x: 174.5, y: 63.9 },
	                { x: 177.5, y: 72 },
	                { x: 170.5, y: 56.8 },
	                { x: 182.4, y: 74.5 },
	                { x: 197.1, y: 90.9 },
	                { x: 180.1, y: 93 },
	                { x: 175.5, y: 80.9 },
	                { x: 180.6, y: 72.7 },
	                { x: 184.4, y: 68 },
	                { x: 175.5, y: 70.9 },
	                { x: 180.6, y: 72.5 },
	                { x: 177, y: 72.5 },
	                { x: 177.1, y: 83.4 },
	                { x: 181.6, y: 75.5 },
	                { x: 176.5, y: 73 },
	                { x: 175, y: 70.2 },
	                { x: 174, y: 73.4 },
	                { x: 165.1, y: 70.5 },
	                { x: 177, y: 68.9 },
	                { x: 192, y: 102.3 },
	                { x: 176.5, y: 68.4 },
	                { x: 169.4, y: 65.9 },
	                { x: 182.1, y: 75.7 },
	                { x: 179.8, y: 84.5 },
	                { x: 175.3, y: 87.7 },
	                { x: 184.9, y: 86.4 },
	                { x: 177.3, y: 73.2 },
	                { x: 167.4, y: 53.9 },
	                { x: 178.1, y: 72 },
	                { x: 168.9, y: 55.5 },
	                { x: 157.2, y: 58.4 },
	                { x: 180.3, y: 83.2 },
	                { x: 170.2, y: 72.7 },
	                { x: 177.8, y: 64.1 },
	                { x: 172.7, y: 72.3 },
	                { x: 165.1, y: 65 },
	                { x: 186.7, y: 86.4 },
	                { x: 165.1, y: 65 },
	                { x: 174, y: 88.6 },
	                { x: 175.3, y: 84.1 },
	                { x: 185.4, y: 66.8 },
	                { x: 177.8, y: 75.5 },
	                { x: 180.3, y: 93.2 },
	                { x: 180.3, y: 82.7 },
	                { x: 177.8, y: 58 },
	                { x: 177.8, y: 79.5 },
	                { x: 177.8, y: 78.6 },
	                { x: 177.8, y: 71.8 },
	                { x: 177.8, y: 116.4 },
	                { x: 163.8, y: 72.2 },
	                { x: 188, y: 83.6 },
	                { x: 198.1, y: 85.5 },
	                { x: 175.3, y: 90.9 },
	                { x: 166.4, y: 85.9 },
	                { x: 190.5, y: 89.1 },
	                { x: 166.4, y: 75 },
	                { x: 177.8, y: 77.7 },
	                { x: 179.7, y: 86.4 },
	                { x: 172.7, y: 90.9 },
	                { x: 190.5, y: 73.6 },
	                { x: 185.4, y: 76.4 },
	                { x: 168.9, y: 69.1 },
	                { x: 167.6, y: 84.5 },
	                { x: 175.3, y: 64.5 },
	                { x: 170.2, y: 69.1 },
	                { x: 190.5, y: 108.6 },
	                { x: 177.8, y: 86.4 },
	                { x: 190.5, y: 80.9 },
	                { x: 177.8, y: 87.7 },
	                { x: 184.2, y: 94.5 },
	                { x: 176.5, y: 80.2 },
	                { x: 177.8, y: 72 },
	                { x: 180.3, y: 71.4 },
	                { x: 171.4, y: 72.7 },
	                { x: 172.7, y: 84.1 },
	                { x: 172.7, y: 76.8 },
	                { x: 177.8, y: 63.6 },
	                { x: 177.8, y: 80.9 },
	                { x: 182.9, y: 80.9 },
	                { x: 170.2, y: 85.5 },
	                { x: 167.6, y: 68.6 },
	                { x: 175.3, y: 67.7 },
	                { x: 165.1, y: 66.4 },
	                { x: 185.4, y: 102.3 },
	                { x: 181.6, y: 70.5 },
	                { x: 172.7, y: 95.9 },
	                { x: 190.5, y: 84.1 },
	                { x: 179.1, y: 87.3 },
	                { x: 175.3, y: 71.8 },
	                { x: 170.2, y: 65.9 },
	                { x: 193, y: 95.9 },
	                { x: 171.4, y: 91.4 },
	                { x: 177.8, y: 81.8 },
	                { x: 177.8, y: 96.8 },
	                { x: 167.6, y: 69.1 },
	                { x: 167.6, y: 82.7 },
	                { x: 180.3, y: 75.5 },
	                { x: 182.9, y: 79.5 },
	                { x: 176.5, y: 73.6 },
	                { x: 186.7, y: 91.8 },
	                { x: 188, y: 84.1 },
	                { x: 188, y: 85.9 },
	                { x: 177.8, y: 81.8 },
	                { x: 174, y: 82.5 },
	                { x: 177.8, y: 80.5 },
	                { x: 171.4, y: 70 },
	                { x: 185.4, y: 81.8 },
	                { x: 185.4, y: 84.1 },
	                { x: 188, y: 90.5 },
	                { x: 188, y: 91.4 },
	                { x: 182.9, y: 89.1 },
	                { x: 176.5, y: 85 },
	                { x: 175.3, y: 69.1 },
	                { x: 175.3, y: 73.6 },
	                { x: 188, y: 80.5 },
	                { x: 188, y: 82.7 },
	                { x: 175.3, y: 86.4 },
	                { x: 170.5, y: 67.7 },
	                { x: 179.1, y: 92.7 },
	                { x: 177.8, y: 93.6 },
	                { x: 175.3, y: 70.9 },
	                { x: 182.9, y: 75 },
	                { x: 170.8, y: 93.2 },
	                { x: 188, y: 93.2 },
	                { x: 180.3, y: 77.7 },
	                { x: 177.8, y: 61.4 },
	                { x: 185.4, y: 94.1 },
	                { x: 168.9, y: 75 },
	                { x: 185.4, y: 83.6 },
	                { x: 180.3, y: 85.5 },
	                { x: 174, y: 73.9 },
	                { x: 167.6, y: 66.8 },
	                { x: 182.9, y: 87.3 },
	                { x: 160, y: 72.3 },
	                { x: 180.3, y: 88.6 },
	                { x: 167.6, y: 75.5 },
	                { x: 186.7, y: 101.4 },
	                { x: 175.3, y: 91.1 },
	                { x: 175.3, y: 67.3 },
	                { x: 175.9, y: 77.7 },
	                { x: 175.3, y: 81.8 },
	                { x: 179.1, y: 75.5 },
	                { x: 181.6, y: 84.5 },
	                { x: 177.8, y: 76.6 },
	                { x: 182.9, y: 85 },
	                { x: 177.8, y: 102.5 },
	                { x: 184.2, y: 77.3 },
	                { x: 179.1, y: 71.8 },
	                { x: 176.5, y: 87.9 },
	                { x: 188, y: 94.3 },
	                { x: 174, y: 70.9 },
	                { x: 167.6, y: 64.5 },
	                { x: 170.2, y: 77.3 },
	                { x: 167.6, y: 72.3 },
	                { x: 188, y: 87.3 },
	                { x: 174, y: 80 },
	                { x: 176.5, y: 82.3 },
	                { x: 180.3, y: 73.6 },
	                { x: 167.6, y: 74.1 },
	                { x: 188, y: 85.9 },
	                { x: 180.3, y: 73.2 },
	                { x: 167.6, y: 76.3 },
	                { x: 183, y: 65.9 },
	                { x: 183, y: 90.9 },
	                { x: 179.1, y: 89.1 },
	                { x: 170.2, y: 62.3 },
	                { x: 177.8, y: 82.7 },
	                { x: 179.1, y: 79.1 },
	                { x: 190.5, y: 98.2 },
	                { x: 177.8, y: 84.1 },
	                { x: 180.3, y: 83.2 },
	                { x: 180.3, y: 83.2 }
	            ]
	        },
	        {
	            name: 'female',
	            data: [
	                { x: 161.2, y: 51.6 },
	                { x: 167.5, y: 59 },
	                { x: 159.5, y: 49.2 },
	                { x: 157, y: 63 },
	                { x: 155.8, y: 53.6 },
	                { x: 170, y: 59 },
	                { x: 159.1, y: 47.6 },
	                { x: 166, y: 69.8 },
	                { x: 176.2, y: 66.8 },
	                { x: 160.2, y: 75.2 },
	                { x: 172.5, y: 55.2 },
	                { x: 170.9, y: 54.2 },
	                { x: 172.9, y: 62.5 },
	                { x: 153.4, y: 42 },
	                { x: 160, y: 50 },
	                { x: 147.2, y: 49.8 },
	                { x: 168.2, y: 49.2 },
	                { x: 175, y: 73.2 },
	                { x: 157, y: 47.8 },
	                { x: 167.6, y: 68.8 },
	                { x: 159.5, y: 50.6 },
	                { x: 175, y: 82.5 },
	                { x: 166.8, y: 57.2 },
	                { x: 176.5, y: 87.8 },
	                { x: 170.2, y: 72.8 },
	                { x: 174, y: 54.5 },
	                { x: 173, y: 59.8 },
	                { x: 179.9, y: 67.3 },
	                { x: 170.5, y: 67.8 },
	                { x: 160, y: 47 },
	                { x: 154.4, y: 46.2 },
	                { x: 162, y: 55 },
	                { x: 176.5, y: 83 },
	                { x: 160, y: 54.4 },
	                { x: 152, y: 45.8 },
	                { x: 162.1, y: 53.6 },
	                { x: 170, y: 73.2 },
	                { x: 160.2, y: 52.1 },
	                { x: 161.3, y: 67.9 },
	                { x: 166.4, y: 56.6 },
	                { x: 168.9, y: 62.3 },
	                { x: 163.8, y: 58.5 },
	                { x: 167.6, y: 54.5 },
	                { x: 160, y: 50.2 },
	                { x: 161.3, y: 60.3 },
	                { x: 167.6, y: 58.3 },
	                { x: 165.1, y: 56.2 },
	                { x: 160, y: 50.2 },
	                { x: 170, y: 72.9 },
	                { x: 157.5, y: 59.8 },
	                { x: 167.6, y: 61 },
	                { x: 160.7, y: 69.1 },
	                { x: 163.2, y: 55.9 },
	                { x: 152.4, y: 46.5 },
	                { x: 157.5, y: 54.3 },
	                { x: 168.3, y: 54.8 },
	                { x: 180.3, y: 60.7 },
	                { x: 165.5, y: 60 },
	                { x: 165, y: 62 },
	                { x: 164.5, y: 60.3 },
	                { x: 156, y: 52.7 },
	                { x: 160, y: 74.3 },
	                { x: 163, y: 62 },
	                { x: 165.7, y: 73.1 },
	                { x: 161, y: 80 },
	                { x: 162, y: 54.7 },
	                { x: 166, y: 53.2 },
	                { x: 174, y: 75.7 },
	                { x: 172.7, y: 61.1 },
	                { x: 167.6, y: 55.7 },
	                { x: 151.1, y: 48.7 },
	                { x: 164.5, y: 52.3 },
	                { x: 163.5, y: 50 },
	                { x: 152, y: 59.3 },
	                { x: 169, y: 62.5 },
	                { x: 164, y: 55.7 },
	                { x: 161.2, y: 54.8 },
	                { x: 155, y: 45.9 },
	                { x: 170, y: 70.6 },
	                { x: 176.2, y: 67.2 },
	                { x: 170, y: 69.4 },
	                { x: 162.5, y: 58.2 },
	                { x: 170.3, y: 64.8 },
	                { x: 164.1, y: 71.6 },
	                { x: 169.5, y: 52.8 },
	                { x: 163.2, y: 59.8 },
	                { x: 154.5, y: 49 },
	                { x: 159.8, y: 50 },
	                { x: 173.2, y: 69.2 },
	                { x: 170, y: 55.9 },
	                { x: 161.4, y: 63.4 },
	                { x: 169, y: 58.2 },
	                { x: 166.2, y: 58.6 },
	                { x: 159.4, y: 45.7 },
	                { x: 162.5, y: 52.2 },
	                { x: 159, y: 48.6 },
	                { x: 162.8, y: 57.8 },
	                { x: 159, y: 55.6 },
	                { x: 179.8, y: 66.8 },
	                { x: 162.9, y: 59.4 },
	                { x: 161, y: 53.6 },
	                { x: 151.1, y: 73.2 },
	                { x: 168.2, y: 53.4 },
	                { x: 168.9, y: 69 },
	                { x: 173.2, y: 58.4 },
	                { x: 171.8, y: 56.2 },
	                { x: 178, y: 70.6 },
	                { x: 164.3, y: 59.8 },
	                { x: 163, y: 72 },
	                { x: 168.5, y: 65.2 },
	                { x: 166.8, y: 56.6 },
	                { x: 172.7, y: 105.2 },
	                { x: 163.5, y: 51.8 },
	                { x: 169.4, y: 63.4 },
	                { x: 167.8, y: 59 },
	                { x: 159.5, y: 47.6 },
	                { x: 167.6, y: 63 },
	                { x: 161.2, y: 55.2 },
	                { x: 160, y: 45 },
	                { x: 163.2, y: 54 },
	                { x: 162.2, y: 50.2 },
	                { x: 161.3, y: 60.2 },
	                { x: 149.5, y: 44.8 },
	                { x: 157.5, y: 58.8 },
	                { x: 163.2, y: 56.4 },
	                { x: 172.7, y: 62 },
	                { x: 155, y: 49.2 },
	                { x: 156.5, y: 67.2 },
	                { x: 164, y: 53.8 },
	                { x: 160.9, y: 54.4 },
	                { x: 162.8, y: 58 },
	                { x: 167, y: 59.8 },
	                { x: 160, y: 54.8 },
	                { x: 160, y: 43.2 },
	                { x: 168.9, y: 60.5 },
	                { x: 158.2, y: 46.4 },
	                { x: 156, y: 64.4 },
	                { x: 160, y: 48.8 },
	                { x: 167.1, y: 62.2 },
	                { x: 158, y: 55.5 },
	                { x: 167.6, y: 57.8 },
	                { x: 156, y: 54.6 },
	                { x: 162.1, y: 59.2 },
	                { x: 173.4, y: 52.7 },
	                { x: 159.8, y: 53.2 },
	                { x: 170.5, y: 64.5 },
	                { x: 159.2, y: 51.8 },
	                { x: 157.5, y: 56 },
	                { x: 161.3, y: 63.6 },
	                { x: 162.6, y: 63.2 },
	                { x: 160, y: 59.5 },
	                { x: 168.9, y: 56.8 },
	                { x: 165.1, y: 64.1 },
	                { x: 162.6, y: 50 },
	                { x: 165.1, y: 72.3 },
	                { x: 166.4, y: 55 },
	                { x: 160, y: 55.9 },
	                { x: 152.4, y: 60.4 },
	                { x: 170.2, y: 69.1 },
	                { x: 162.6, y: 84.5 },
	                { x: 170.2, y: 55.9 },
	                { x: 158.8, y: 55.5 },
	                { x: 172.7, y: 69.5 },
	                { x: 167.6, y: 76.4 },
	                { x: 162.6, y: 61.4 },
	                { x: 167.6, y: 65.9 },
	                { x: 156.2, y: 58.6 },
	                { x: 175.2, y: 66.8 },
	                { x: 172.1, y: 56.6 },
	                { x: 162.6, y: 58.6 },
	                { x: 160, y: 55.9 },
	                { x: 165.1, y: 59.1 },
	                { x: 182.9, y: 81.8 },
	                { x: 166.4, y: 70.7 },
	                { x: 165.1, y: 56.8 },
	                { x: 177.8, y: 60 },
	                { x: 165.1, y: 58.2 },
	                { x: 175.3, y: 72.7 },
	                { x: 154.9, y: 54.1 },
	                { x: 158.8, y: 49.1 },
	                { x: 172.7, y: 75.9 },
	                { x: 168.9, y: 55 },
	                { x: 161.3, y: 57.3 },
	                { x: 167.6, y: 55 },
	                { x: 165.1, y: 65.5 },
	                { x: 175.3, y: 65.5 },
	                { x: 157.5, y: 48.6 },
	                { x: 163.8, y: 58.6 },
	                { x: 167.6, y: 63.6 },
	                { x: 165.1, y: 55.2 },
	                { x: 165.1, y: 62.7 },
	                { x: 168.9, y: 56.6 },
	                { x: 162.6, y: 53.9 },
	                { x: 164.5, y: 63.2 },
	                { x: 176.5, y: 73.6 },
	                { x: 168.9, y: 62 },
	                { x: 175.3, y: 63.6 },
	                { x: 159.4, y: 53.2 },
	                { x: 160, y: 53.4 },
	                { x: 170.2, y: 55 },
	                { x: 162.6, y: 70.5 },
	                { x: 167.6, y: 54.5 },
	                { x: 162.6, y: 54.5 },
	                { x: 160.7, y: 55.9 },
	                { x: 160, y: 59 },
	                { x: 157.5, y: 63.6 },
	                { x: 162.6, y: 54.5 },
	                { x: 152.4, y: 47.3 },
	                { x: 170.2, y: 67.7 },
	                { x: 165.1, y: 80.9 },
	                { x: 172.7, y: 70.5 },
	                { x: 165.1, y: 60.9 },
	                { x: 170.2, y: 63.6 },
	                { x: 170.2, y: 54.5 },
	                { x: 170.2, y: 59.1 },
	                { x: 161.3, y: 70.5 },
	                { x: 167.6, y: 52.7 },
	                { x: 167.6, y: 62.7 },
	                { x: 165.1, y: 86.3 },
	                { x: 162.6, y: 66.4 },
	                { x: 152.4, y: 67.3 },
	                { x: 168.9, y: 63 },
	                { x: 170.2, y: 73.6 },
	                { x: 175.2, y: 62.3 },
	                { x: 175.2, y: 57.7 },
	                { x: 160, y: 55.4 },
	                { x: 165.1, y: 104.1 },
	                { x: 174, y: 55.5 },
	                { x: 170.2, y: 77.3 },
	                { x: 160, y: 80.5 },
	                { x: 167.6, y: 64.5 },
	                { x: 167.6, y: 72.3 },
	                { x: 167.6, y: 61.4 },
	                { x: 154.9, y: 58.2 },
	                { x: 162.6, y: 81.8 },
	                { x: 175.3, y: 63.6 },
	                { x: 171.4, y: 53.4 },
	                { x: 157.5, y: 54.5 },
	                { x: 165.1, y: 53.6 },
	                { x: 160, y: 60 },
	                { x: 174, y: 73.6 },
	                { x: 162.6, y: 61.4 },
	                { x: 174, y: 55.5 },
	                { x: 162.6, y: 63.6 },
	                { x: 161.3, y: 60.9 },
	                { x: 156.2, y: 60 },
	                { x: 149.9, y: 46.8 },
	                { x: 169.5, y: 57.3 },
	                { x: 160, y: 64.1 },
	                { x: 175.3, y: 63.6 },
	                { x: 169.5, y: 67.3 },
	                { x: 160, y: 75.5 },
	                { x: 172.7, y: 68.2 },
	                { x: 162.6, y: 61.4 },
	                { x: 157.5, y: 76.8 },
	                { x: 176.5, y: 71.8 },
	                { x: 164.4, y: 55.5 },
	                { x: 160.7, y: 48.6 },
	                { x: 174, y: 66.4 },
	                { x: 163.8, y: 67.3 }
	            ]
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 1160,
	        height: 540,
	        title: 'Height vs Weight'
	    },
	    yAxis: {
	        title: 'Weight (kg)'
	    },
	    xAxis: {
	        title: 'Height (cm)'
	    },
	    tooltip: {
	        template: function (category, items) {
	            return '<div class="tui-chart-default-tooltip">' +
	                '<div class="tui-chart-tooltip-head">' + items.legend + '</div>' +
	                    '<table class="tui-chart-tooltip-body">' +
	                        '<tr>' +
	                            '<td>Weight</td>' +
	                        '<td class="tui-chart-tooltip-value">' + items.x + 'kg</td>' +
	                    '</tr>' +
	                    '<tr>' +
	                        '<td>Height</td>' +
	                            '<td class="tui-chart-tooltip-value">' + items.y + 'cm</td>' +
	                        '</tr>' +
	                    '</table>' +
	                '</div>';
	        }
	    }
	};
	var theme = {
	    series: {
	        colors: [
	            '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	            '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	        ]
	    }
	};

	// 원하는 색상으로 변경 가능
	// tui.chart.registerTheme('myTheme', theme);
	// options.theme = 'myTheme';

	tui.chart.scatterChart(container, data, options);
}

function pie_chart(){
	var container = document.getElementById('pie-chart');
	var data = {
	    categories: ['Browser'],
	    series: [
	        {
	            name: 'Chrome',
	            data: 300.02
	        },
	        {
	            name: 'IE',
	            data: 250.47
	        },
	        {
	            name: 'Firefox',
	            data: 120.71
	        },
	        {
	            name: 'Safari',
	            data: 100.45
	        },
	        {
	            name: 'Opera',
	            data: 100.10
	        },
	        {
	            name: 'Etc',
	            data: 110.25
	        }
	    ]
	};
	var options = {
	    chart: {
	        width: 700,
	        height: 700,
	        title: 'Usage share of web browsers'
	    },
	    series: {
	        showLegend: true,
	        showLabel: true,
	        labelAlign: 'center'
	    },
	    legend: {
	        visible: false
	    }
	};
	var theme = {
	    series: {
	        series: {
	            colors: [
	                '#83b14e', '#458a3f', '#295ba0', '#2a4175', '#289399',
	                '#289399', '#617178', '#8a9a9a', '#516f7d', '#dddddd'
	            ]
	        },
	        label: {
	            color: '#fff',
	            fontFamily: 'sans-serif'
	        }
	    }
	};

	// 원하는 색상으로 변경 가능
	tui.chart.registerTheme('myTheme', theme);
	options.theme = 'myTheme';

	tui.chart.pieChart(container, data, options);
}