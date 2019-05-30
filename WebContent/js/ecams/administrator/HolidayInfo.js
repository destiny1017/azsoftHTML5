/**
 * 휴일정보
 */

var basepicker 	  = new ax5.ui.picker();
var holidaypicker = new ax5.ui.picker();
var holidayGrid   = new ax5.ui.grid();
var applyData	  = null;

var cboOptions = [];

var holicbData		= null;	// 휴일종류 콤보
var holigbData  	= null;	// 휴일구분 콤보
var holidayGridData = null; // 휴일 그리드

$(document).ready(function() {
  
	//조회년도
	basepicker.bind({
        target: $('[data-picker-date="year"]'),
        content: {
            type: 'date',
            config: {
                mode: "year", selectMode: "year"
            },
            formatter: {
                pattern: 'date(year)'
            }
        },
		onStateChanged: function () {
			getholidayList($('#base_date').val());
		     /*if (this.state == "open") {
		     //console.log(this.item);
		     var selectedValue = this.self.getContentValue(this.item["$target"]);
		     if (!selectedValue) {
		         this.item.pickerCalendar[0].ax5uiInstance.setSelection([ax5.util.date(new Date(), {'return': 'yyyy/MM/dd', 'add': {d: 0}})]);
		         this.item.pickerCalendar[1].ax5uiInstance.setSelection([ax5.util.date(new Date(), {'return': 'yyyy/MM/dd', 'add': {d: 0}})]);
		     }
		 	}*/
		}
    });
    
    $('#base_date').val(new Date().getFullYear());
    getholidayList($('#base_date').val());
    
    //휴일
    holidaypicker.bind({
        target: $('[data-ax5picker="basic2"]'),
        direction: "top",
        content: {
            width: 270,
            margin: 10,
            type: 'date',
            config: {
                control: {
                    left: '<i class="fa fa-chevron-left"></i>',
                    yearTmpl: '%s',
                    monthTmpl: '%s',
                    right: '<i class="fa fa-chevron-right"></i>'
                },
                lang: {
                    yearTmpl: "%s년",
                    months: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
                    dayTmpl: "%s"
                },
                marker: (function () {
                    var marker = {};
                    marker[ax5.util.date(new Date(), {'return': 'yyyy-MM-dd', 'add': {d: 0}})] = true;

                    return marker;
                })()
            },
            formatter: {
                pattern: 'date'
            }
        },
        onStateChanged: function () {
            if (this.state == "open") {
                var selectedValue = this.self.getContentValue(this.item["$target"]);
                if (!selectedValue) {
                    this.item.pickerCalendar[0].ax5uiInstance.setSelection([ax5.util.date(new Date(), {'add': {d: 1}})]);
                }
            }
        }
    });
    
    var date = new Date(); 
    var year = date.getFullYear(); 
    var month = new String(date.getMonth()+1); 
    var day = new String(date.getDate()); 

    if(month.length == 1){ 
      month = "0" + month; 
    } 

    if(day.length == 1){ 
      day = "0" + day; 
    } 

    $('#holiday_date').val(year + "-" + month + "-" + day);
    
    //그리드생성
    holidayGrid.setConfig({
        target: $('[data-ax5grid="holidayGrid"]'),
        sortable: true, 
        multiSort: true,
        showRowSelector: false,
        header: {
            align: "center",
            columnHeight: 30
        },
        body: {
            columnHeight: 28,
            onClick: function () {
            	this.self.clearSelect(); //기존선택된 row deselect 처리 (multipleSelect 할땐 제외해야함)
                this.self.select(this.dindex);
            },
            onDBLClick: function () {
            	doubleClickGrid1();
            },
        	onDataChanged: function(){
        	    this.self.repaint();
        	}
        },
        columns: [
            {key: "cm_holiday1", label: "휴일",  width: '33%'},
            {key: "holigb_nm", label: "휴일구분",  width: '33%'},
            {key: "holi_nm", label: "휴일종류",  width: '33%'},
        ]
    });
    
    //휴일종류, 휴일구분
    getCodeInfo();
    
    //조회년도 변경 이벤트
//    $('#basepicker').bind('change', function() {
//		if($(this).is(':checked')) {
//			screenInit();
//			$('#chkSelfDiv').css('visibility','visible');
//			$('#txtSysCd').val('');
//			$('[data-ax5select="cboSys"]').ax5select('setValue','00000',true);
//			$('[data-ax5select="cboSys"]').ax5select("disable");
//
//		}
//		
//		if( !($(this).is(':checked')) ) {
//			screenInit();
//			$('#chkSelfDiv').css('visibility','hidden');
//			$('[data-ax5select="cboSys"]').ax5select("enable");
//		}
//	});
})

function getCodeInfo() {
	var codeInfoCbo;
	var codeInfoCboData;
	
	codeInfoData 			 = new Object();
	codeInfoData.cm_macode 	 = 'HOLIDAY,HOLICD';
	codeInfoData.selMsg  	 = 'SEL';
	codeInfoData.closeYN 	 = 'N';
	
	codeInfoCboData = new Object();
	codeInfoCboData = {
		codeInfoData	: 	codeInfoData,
		requestType		: 	'GETCODEINFO'
	}
	ajaxAsync('/webPage/administrator/CodeInfo', codeInfoCboData, 'json', successGetCodeInfo);
}

function successGetCodeInfo(data) {
	holicbData = data;
	holigbData = data;
	cboOptions = [];
	
	//휴일종류
	holicbData = holicbData.filter(function(data) {
		return data.cm_macode == "HOLIDAY";
	});
	
	$.each(holicbData,function(key,value) {
		cboOptions.push({value: value.cm_macode, text: value.cm_codename, cm_macode: value.cm_macode});
	});
	$('[data-ax5select="holi_cb"]').ax5select({
        options: cboOptions
	});
	
	
	cboOptions = [];
	
	//휴일구분
	holigbData = holigbData.filter(function(data) {
		return data.cm_macode == "HOLICD";
	});
	
	
	$.each(holigbData,function(key,value) {
		cboOptions.push({value: value.cm_macode, text: value.cm_codename, cm_macode: value.cm_macode});
	});
	$('[data-ax5select="holigb_cb"]').ax5select({
        options: cboOptions
	});
}

//휴일 리스트
function getholidayList(year) {
	var sysClsSw = $('#chkCls').is(':checked');
	
	var holiday;
	var holiInfoData;
	holiday 		= new Object();
	holiday.year 	= year.length > 0 ? year : null;
	
	holidayData = new Object();
	holidayData = {
		holiday	: 	holiday,
		requestType	: 	'GETHOLIDAY'
	}
	
	ajaxAsync('/webPage/administrator/HolidayInfoServlet', holidayData, 'json', successGetholidayList);
}

//휴일 리스트
function successGetholidayList(data) {
	holidayGridData = data;
	holidayGrid.setData(holidayGridData);
}