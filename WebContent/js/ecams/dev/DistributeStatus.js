var menuJson; 
var userid = window.parent.userId;     
var reqcd_combo;
var syscd_combo;
var strReqCD;
var strTmpDir = "";
var stepcbo;
var cboGbn;
var grid_data;

var request =  new Request();
strReqCD = request.getParameter('reqcd');

var today = new Date();
var dd = today.getDate();
var mm = today.getMonth() + 1;
var yyyy = today.getFullYear();

var SBGridProperties = {};

if(dd < 10){
	dd = '0' + dd;
}
if(mm < 10){
	mm = '0' + mm;
}

today = yyyy + '/' + mm + '/' + dd;

$(document).ready(function(){
	if(strReqCD == "01"){
		$("#titleText").html("<font color='#6c6c6c'><strong>[개발]</strong> | <strong>체크아웃현황</strong></font>");
	} else if(strReqCD == "03") {
		$("#titleText").html("<font color='#6c6c6c'><strong>[테스트]</strong> | <strong>배포현황(테스트)</strong></font>");
	} else if(strReqCD == "04"){
		$("#titleText").html("<font color='#6c6c6c'><strong>[적용]</strong> | <strong>배포현황</strong></font>");
	} else if(strReqCD == "07"){
		$("#titleText").html("<font color='#6c6c6c'><strong>[개발]</strong> | <strong>체크인현황</strong></font>");
	}
	
	SBUxMethod.set('datStD', today);
	SBUxMethod.set('datEdD', today);
	createGrid();
	strReqCD_set();
	userInfo_check();
	codeInfo_set();
	systemPath_set();
	getSysInfo();	
});

function strReqCD_set(){	
	step_combo_init();
	if(strReqCD=="04" || strReqCD=="05" || strReqCD == "07" || strReqCD == "03"){
		cboGbn_set();
		document.getElementById('idxLabel_norm12').style.visibility = "visible";
		document.getElementById('cboGbn').style.visibility = "visible";	
		datagrid.setColHidden(9, false, true);
		datagrid.setColHidden(10, false, true);
	}else{
		document.getElementById('idxLabel_norm12').style.visibility = "hidden";
		document.getElementById('cboGbn').style.visibility = "hidden";
		datagrid.setColHidden(9, true, true);
		datagrid.setColHidden(10, true, true);
	}
}

function userInfo_check(){	// 사용자의 관리자 권한 여부 체크
	var tmpData = {
			requestType : 'UserInfochk',
			UserId : userid
	}
	
	$.ajax({
        type : "POST",
        url : "/webPage/dev/DistributeStatus",
        data : tmpData,
        dataType : 'json',
        async : true, 
        success : function(data) { 
        	if('1' == data[0].cm_admin){
        		SBUxMethod.set('chkbox_norm', 'false');
        		SBUxMethod.attr('input_text2', 'disabled', 'false');
        	}
        },
        error : function(req, stat, error) {
            console.log(error);
            alert(error);
        }
      });
}

function codeInfo_set(){ // 신청종류
	var tmpData = {
			requestType : 'CodeInfoSet'
	}
	$.ajax({
        type : "POST",
        url : "/webPage/dev/DistributeStatus",
        data : tmpData,
        dataType : 'json',
        async : true, 
        success : function(data) { 
        	reqcd_combo = data;
        	
        	reqcd_combo = reqcd_combo.filter(function(data) {
        		if(strReqCD == "01"){        	   		
        			if(data.cm_micode === "01" || data.cm_micode === "02" || data.cm_micode === "11" || data.cm_micode === "00"){
        				return true;
        			} else {
        				return false;
        			}        			
         	   	} else if (data.cm_micode == strReqCD || data.cm_micode === "00" || data.cm_micode === "03" || data.cm_micode === "06" || data.cm_micode === "07" || data.cm_micode === "94") {
         	   		return true;
         	   	} else {
         	   		return false;
         	   	}
         	});
        	
        	SBUxMethod.refresh('reqcd_combo');
        },
        error : function(req, stat, error) {
            console.log(error);
            alert(error);
        }
      });
}

function systemPath_set(){
	var tmpData = {
			requestType : 'SystemPathSet'
	}
	
	$.ajax({
        type : "POST",
        url : "/webPage/dev/DistributeStatus",
        data : tmpData,
        dataType : 'json',
        async : true, 
        success : function(data) { 
        	strTmpDir = data + "/";
        },
        error : function(req, stat, error) {
            console.log(error);
            alert(error);
        }
      });
}

function getSysInfo(){
	var tmpData = {
			requestType : 'getSysInfo',
			UserId : userid 
	}
	
	$.ajax({
        type : "POST",
        url : "/webPage/dev/DistributeStatus",
        data : tmpData,
        dataType : 'json',
        async : true, 
        success : function(data) {        	
        	syscd_combo = data;        	        	        	
        	SBUxMethod.refresh('syscd_combo');
        },
        error : function(req, stat, error) {
            console.log(error);
            alert(error);
        }
      });
}

function step_combo_init(){
	stepcbo = [
		{ label : "전체", value : "0" },
		{ label : "미완료", value : "1" },
		{ label : "반려/회수", value : "3" },
		{ label : "완료", value : "9" }		
	];
	SBUxMethod.refresh('stepcbo');
}

function step_combo_change_resultHandler(args){
	if(SBUxMethod.get("stepcbo") == "1"){
		$('#datStD').attr('disabled', true);
		$('[name="_datStD_sub"]').attr('disabled', true);
		$('#datEdD').attr('disabled', true);
		$('[name="_datEdD_sub"]').attr('disabled', true);
	} else {
		$('#datStD').attr('disabled', false);
		$('[name="_datStD_sub"]').attr('disabled', false);
		$('#datEdD').attr('disabled', false);
		$('[name="_datEdD_sub"]').attr('disabled', false);
	}
}

function cboGbn_set(){
	cboGbn = [
		{cm_macode : "REQPASS", cm_micode : "ALL", cm_codename : "전체"},
		{cm_macode : "REQPASS", cm_micode : "0", cm_codename : "일반적용"},
		{cm_macode : "REQPASS", cm_micode : "4", cm_codename : "수시적용"},
		{cm_macode : "REQPASS", cm_micode : "2", cm_codename : "긴급적용"}
	]
	SBUxMethod.refresh('cboGbn');
}

function cmdQry_Proc(){
	var tmpObj = {};
	
	tmpObj.syscd = SBUxMethod.get("syscd_combo");
	
}

function createGrid(){
	SBGridProperties = {};
	SBGridProperties.parentid = 'sbGridArea';  // [필수] 그리드 영역의 div id 입니다.            
	SBGridProperties.id = 'datagrid';          // [필수] 그리드를 담기위한 객체명과 동일하게 입력합니다.                
	SBGridProperties.jsonref = 'grid_data';    // [필수] 그리드의 데이터를 나타내기 위한 json data 객체명을 입력합니다.
	SBGridProperties.waitingui = true;
	// 그리드의 여러 속성들을 입력합니다.
	SBGridProperties.explorerbar = 'sort';
	SBGridProperties.extendlastcol = 'scroll';
	SBGridProperties.tooltip = true;
	SBGridProperties.ellipsis = true;
	SBGridProperties.rowheader = 'seq';
	    			
	// [필수] 그리드의 컬럼을 입력합니다.  
	SBGridProperties.columns = [
		{caption : ['SR-ID'],	ref : 'cc_srid', width : '150px', style : 'text-align:center',	type : 'output'},
		{caption : ['문서번호'],	ref : 'genieid',width : '150px',  style : 'text-align:center', type : 'output'},
		{caption : ['시스템'],	ref : 'sysgbname',width : '100px', style : 'text-align:center',  type : 'output'},
		{caption : ['신청종류'],	ref : 'sin',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청자'],	ref : 'editor',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청번호'],	ref : 'acptno',width : '150px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청일시'],	ref : 'acptdate',width : '100px',  style : 'text-align:center', type : 'output'},
		{caption : ['진행상태'],	ref : 'cm_codename',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['처리구분'],	ref : 'procgbn',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['적용예정일시'],	ref : 'grdprcreq',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['완료일시'],	ref : 'prcdate',width : '100px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청내용'],	ref : 'rsrcnamememo',width : '200px',  style : 'text-align:center',	type : 'output'},
		{caption : ['신청사유'],	ref : 'cr_sayu', style : 'text-align:center',	type : 'output'}
	];
	datagrid = _SBGrid.create(SBGridProperties); // 만들어진 SBGridProperties 객체를 파라메터로 전달합니다.
}