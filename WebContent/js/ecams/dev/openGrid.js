// http://axisj.com/
// http://ax5.io/ax5ui-grid/demo/1-formatter.html
// userid 및 ReqCD 가져오기
var userid = window.parent.userId;   
var strReqCD = "";
var request =  new Request();
strReqCD = request.getParameter('reqcd');

// grid 생성
var firstGrid = new ax5.ui.grid();

// 달력 생성
var picker = new ax5.ui.picker();

// 이 부분 지우면 영어명칭으로 바뀜
// ex) 월 -> MON
ax5.info.weekNames = [
    {label: "일"},
    {label: "월"},
    {label: "화"},
    {label: "수"},
    {label: "목"},
    {label: "금"},
    {label: "토"}
];

$(document).ready(function(){
	if(strReqCD != null && strReqCD != ""){
		if(strReqCD.length > 2) strReqCD.substring(0, 2);
		else strReqCD = "";
	}
	
	setGrid();
	 $('[data-grid-control]').click(function () {
         switch (this.getAttribute("data-grid-control")) {
             case "excel-export":
                 firstGrid.exportExcel("grid-to-excel.xls");
                 break;
         }
     });

	setPicker();
	getUserInfo();
	cboGbn_set();
});

function setPicker(){
	//default 오늘날짜 setting
	$('#datStD').val(getDate('DATE',0));
	$('#datEdD').val(getDate('DATE',0));
	
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
                 }
             },
             formatter: {
            	 //ax5formatter.min.js
                 pattern: 'date'
             }
         },
         onStateChanged: function () {
             /*if (this.state == "open") {
                 //console.log(this.item);
                 var selectedValue = this.self.getContentValue(this.item["$target"]);
                 if (!selectedValue) {
                     this.item.pickerCalendar[0].ax5uiInstance.setSelection([ax5.util.date(new Date(), {'return': 'yyyy/MM/dd', 'add': {d: 0}})]);
                     this.item.pickerCalendar[1].ax5uiInstance.setSelection([ax5.util.date(new Date(), {'return': 'yyyy/MM/dd', 'add': {d: 0}})]);
                 }
             }*/
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
		
}

function getUserInfo(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'UserInfochk',
			UserId : userid
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/RequestStatus', tmpData, 'json');
	
	if(strReqCD != null && strReqCD != ""){
		$("#txtUser").text(ajaxResultData[0].cm_username);
	}
	
	getPMOInfo();
}

function getPMOInfo(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'UserPMOInfo',
			UserId : userid
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/RequestStatus', tmpData, 'json');
	
	if(ajaxResultData.length > 1){
		strDeptCd = ajaxResultData.substring(1);
		strRgtCd = ajaxResultData.substring(0,1);
	}
	
	getSysInfo();
	getCodeInfo();
	getTeamInfoGrid2();
}


function getSysInfo(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'SysInfo',
			UserId : userid
	}	
	var options = [];
	ajaxResultData = ajaxCallWithJson('/webPage/approval/RequestStatus', tmpData, 'json');
	
	
	$.each(ajaxResultData,function(key,value) {
		//var option = $("<option value="+value.cm_syscd+">"+value.cm_sysmsg+"</option>");
	    //$('#cboSysCd').append(option);  
		options.push({value: value.cm_syscd, text: value.cm_sysmsg});
	});
	
	$('[data-ax5select="cboSysCd"]').ax5select({
        options: options
	});
}

function getCodeInfo(){
	var ajaxResultData = null;
	var cboSin;
	var cboSta;
	var options = [];
	var tmpData = {
			requestType : 'CodeInfo_1',
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/RequestStatus', tmpData, 'json');
	
	cboSin = ajaxResultData;
	cboSta = ajaxResultData;
	
	cboSin.push({
		cm_macode : "REQUEST",
		cm_micode : "94",
		cm_codename : "테스트폐기"
	});
	
	cboSin = cboSin.filter(function(data) {
	   return data.cm_macode === "REQUEST";
	});
	
	if(strReqCD != null && strReqCD != ""){
		cboSta.push({
			cm_macode : "R3200STA",
			cm_micode : "RB",
			cm_codename : "긴급롤백미정리건"
		});
	}
	
	cboSta = cboSta.filter(function(data) {
		   return data.cm_macode === "R3200STA";
	});
	
	for(var i = 0 ; i < cboSta.length ; i++){
		if(cboSta[i].cm_micode == "9"){
			cboSta[i].cm_codename = "처리완료";
		}
	}
	
	$.each(cboSin,function(key,value) {
		//var option = $("<option value="+value.cm_micode+">"+value.cm_codename+"</option>");
		//var option = $("<div class='item' data-value="+value.cm_micode+">"+value.cm_codename+"</div>");
		options.push({value: value.cm_micode, text: value.cm_codename});
	});
	
	$('[data-ax5select="cboSin"]').ax5select({
        options: options
	});
	// default 전체선택
    $('[data-ax5select="cboSin"]').ax5select("setValue", ['00',], true);
    
	options = [];
	
	$.each(cboSta,function(key,value) {
		//var option = $("<option value="+value.cm_micode+">"+value.cm_codename+"</option>");
		options.push({value: value.cm_micode, text: value.cm_codename});
	});
	
	$('[data-ax5select="cboSta"]').ax5select({
        options: options
	});
}

function getTeamInfoGrid2(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'TeamInfo',
	}	
	var options = [];
	ajaxResultData = ajaxCallWithJson('/webPage/approval/RequestStatus', tmpData, 'json');
	
	$.each(ajaxResultData,function(key,value) {
		//var option = $("<option value="+value.cm_deptcd+">"+value.cm_deptname+"</option>");
		// $('#cboDept').append(option);
	   
	    options.push({value: value.cm_deptcd, text: value.cm_deptname});
	});
	
	$('[data-ax5select="cboDept"]').ax5select({
        options: options
	});
}

function cboGbn_set(){
	var options = [];
	var cboGbn = [
			{cm_codename : "전체", cm_micode : "ALL", cm_macode : "REQPASS"},
			{cm_codename : "일반적용", cm_micode : "4", cm_macode : "REQPASS"},
			{cm_codename : "수시적용", cm_micode : "0", cm_macode : "REQPASS"},
			{cm_codename : "긴급적용", cm_micode : "2", cm_macode : "REQPASS"}
		]
	
	$.each(cboGbn,function(key,value) {
	    options.push({value: value.cm_micode, text: value.cm_codename});
	});
	
	$('[data-ax5select="cboGbn"]').ax5select({
        options: options
	});
}


function cmdQry_Proc(){
	var tmpObj = {};
	var ajaxResultData = null;
	
	tmpObj.strStD = $("#datStD").val().substr(0,4) + $("#datStD").val().substr(5,2) + $("#datStD").val().substr(8,2);
	tmpObj.strEdD = $("#datEdD").val().substr(0,4) + $("#datEdD").val().substr(5,2) + $("#datEdD").val().substr(8,2);

	if($('[data-ax5select="cboSysCd"]').ax5select("getValue")[0]['@index'] > 0){
		tmpObj.strSys = $('[data-ax5select="cboSysCd"]').ax5select("getValue")[0].value;
	}
		
	tmpObj.cboGbn = $('[data-ax5select="cboGbn"]').ax5select("getValue")[0].value;
	
	if($('[data-ax5select="cboSin"]').ax5select("getValue")[0]['@index'] > 0){
		tmpObj.strQry = $('[data-ax5select="cboSin"]').ax5select("getValue")[0].value;	// 멀티셀렉트 가능하게 수정해야됨
	} 
	
	if($('[data-ax5select="cboDept"]').ax5select("getValue")[0]['@index'] > 0){
		tmpObj.strTeam = $('[data-ax5select="cboDept"]').ax5select("getValue")[0].value;
	}
	
	if($('[data-ax5select="cboSta"]').ax5select("getValue")[0]['@index'] > 0){
		tmpObj.strSta = $('[data-ax5select="cboSta"]').ax5select("getValue")[0].value;
	}
	
	tmpObj.dategbn = $('input[name="rdoDate"]:checked').val();

	tmpObj.txtUser = document.getElementById("txtUser").value.trim();
	tmpObj.txtSpms = document.getElementById("txtSpms").value.trim();
	
	tmpObj.strUserId = userid;
	
	console.log(tmpObj);
	
	var tmpData = {
			requestType : 'get_SelectList',
			prjData: JSON.stringify(tmpObj)
	}	
	
	
	ajaxResultData = ajaxCallWithJson('/webPage/approval/RequestStatus', tmpData, 'json');
	
	//console.log("result" + ajaxResultData);
	
	var cnt = Object.keys(ajaxResultData).length;	
	
	$("#lbTotalCnt").text("총" + cnt + "건");
	
	firstGrid.setData(ajaxResultData);
	
	tmpObj = null;
}

function setGrid(){
	
    
    var menu =new ax5.ui.menu ({
        iconWidth: 20,
        acceleratorWidth: 100,
        itemClickAndClose: false,
        icons: {
            'arrow': '<i class="fa fa-caret-right"></i>'
        },
        items: [
            {type: 1, label: "변경신청상세"},
            {type: 2, label: "결재정보"},
            {type: 3, label: "전체회수"}
        ],
        popupFilter: function (item, param) {
            //console.log(item, param);
        	//param.item.qrycd2 -> 01,02,03,04,06,07,11,12,16
        	
        	/** 
        	 * return 값에 따른 context menu filter
        	 * 
        	 * return true; -> 모든 context menu 보기
        	 * return item.type == 1; --> type이 1인 context menu만 보기
        	 * return item.type == 1 | item.type == 2; --> type 이 1,2인 context menu만 보기
        	 * 
        	 * ex)
            	if(param.item.qrycd2 === '01'){
            		return item.type == 1 | item.type == 2;
            	}
        	 */
        	return true;
        },
        onClick: function (item) {
            console.log(item);
    		swal({
                title: item.label+"팝업",
                text: "신청번호 ["+gid1.getList("selected")[0].acptno2+"]"
            });
            close();//또는 return true;
        },
        onLoad: function(e){
        	var selIndex = Object.values(gid1.focusedColumn);
        	if(selIndex[0] == null || selIndex[0].dindex == null){
        		gid1.focus(null);
        		firstGrid.clearSelect();
        		menu.close();//또는 return true;
            	
        	}
        	else{
        		firstGrid.clearSelect(); //기존선택된 row deselect 처리 (multipleSelect 할땐 제외해야함)
            	firstGrid.select(selIndex[0].dindex);
        	}
        	//console.log(selIndex[0]);
        	//console.log(this.self.selected);
        }
    });
	
	var gid1 = firstGrid.setConfig({
        target: $('[data-ax5grid="first-grid"]'),
        sortable: true, 
        multiSort: true,
        //multipleSelect: true,
        //showRowSelector: true, //checkbox option
        //rowSelectorColumnWidth: 26 
        header: {
            align: "center",
            columnHeight: 30
        },
        body: {
            columnHeight: 28,
            onClick: function () {
                // console.log(this);
            	this.self.clearSelect(); //기존선택된 row deselect 처리 (multipleSelect 할땐 제외해야함)
                this.self.select(this.dindex);
            },
            onDBLClick: function () {
        		//alert('신청상세팝업');
            	//console.log(this);
            	//Sweet Alert [https://sweetalert.js.org/guides/]
        		swal({
                    title: "신청상세팝업",
                    text: "신청번호 ["+this.item.acptno2+"]"
                });

            },
        	trStyleClass: function () {
        		//console.log(this); -> string으로 변환하면 item 데이타 로그 볼수 없음
        		//console.log('++++++colorsw:'+this.item.colorsw);
        		if(this.item.colorsw === '5'){
        			return "fontStyle-error";
        		} else if (this.item.colorsw === '3'){
        			return "fontStyle-cncl";
        		} else if (this.item.colorsw === '0'){
        			return "fontStyle-ing";
        		} else {
        		}
        	},
        	onDataChanged: function(){
        		//그리드 새로고침 (스타일 유지)
        	    this.self.repaint();
        	}
        },
        columns: [
            {key: "syscd", label: "시스템",  width: '10%'},
            {key: "spms", label: "SR-ID",  width: '10%'},
            {key: "acptno", label: "신청번호",  width: '10%'},
            {key: "editor", label: "신청자",  width: '10%'},
            {key: "qrycd", label: "신청종류",  width: '10%'},
            {key: "passok", label: "처리구분",  width: '10%'},
            {key: "acptdate", label: "신청일시",  width: '10%'},
            {key: "sta", label: "진행상태",  width: '10%'},
            {key: "pgmid", label: "프로그램명",  width: '10%'},
            {key: "prcdate", label: "완료일시",  width: '10%'},
            {key: "prcreq", label: "적용예정일시",  width: '10%'},
            {key: "Sunhang", label: "선후행",  width: '10%'},
            {key: "sayu", label: "신청사유", width: '10%'}	//formatter: function(){	return "<button>" + this.value + "</button>"}, 	 
        ]
    });

	$('[data-ax5grid="first-grid"]').bind("contextmenu",function(e){
		menu.popup(e);
		ax5.util.stopEvent(e);
		if(e.target.outerHTML.match(/span/g).length != 2){
    		gid1.focus(null);
    		firstGrid.clearSelect();
			menu.close();
		}
	});
}