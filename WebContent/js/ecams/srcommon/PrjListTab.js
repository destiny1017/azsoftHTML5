/** PrjList 화면 정의 (공통화면)
 * 
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-02-07
 */

var prjListGrid;
var prjListGridData;
var cboReqDepartData;
var cboCatTypeData;
var cboQryGbnData;
var userId = window.parent.userId;
var reqCd;
var adminYN = window.parent.adminYN;
var request =  new Request();

$(document).ready(function() {
	reqCd =  request.getParameter('reqcd');
	screenInitPrj();
});


//초기 화면 셋팅
function screenInitPrj() {
	createElementsPrj();
	setReqDepartInfo();
	setDateInit();
	setCboElementPrj();
}

function setReqDepartInfo() {
	var ajaxReturnData = null;
	
	var teamInfoData 		= new Object();
	teamInfoData.SelMsg 	= 'ALL';
	teamInfoData.cm_useyn 	= 'Y';
	teamInfoData.gubun 		= 'req';
	teamInfoData.itYn 		= 'N';
	
	var teamInfo = {
		teamInfoData: 	JSON.stringify(teamInfoData),
		requestType: 	'SET_TEAM_INFO'
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/srcommon/PrjListTab', teamInfo, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		console.dir(ajaxReturnData);
		cboReqDepartData = ajaxReturnData;
		SBUxMethod.refresh('cboReqDepart');
	}
}

function setCboElementPrj() {
	var codeInfos = getCodeInfoCommon( [new CodeInfo('CATTYPE','ALL','N'),
										new CodeInfo('QRYGBN','ALL','N')] );
	cboCatTypeData 	= codeInfos.CATTYPE;
	cboQryGbnData 	= codeInfos.QRYGBN;
	SBUxMethod.refresh('cboCatType');
	SBUxMethod.refresh('cboQryGbn');
	
	SBUxMethod.set('cboQryGbn', '01');
	changeQryGbnCbo('01');
}

function setDateInit() {
	SBUxMethod.set('datStD',getDate('DATE',-1));
	SBUxMethod.set('datEdD',getDate('DATE',0));
}

function changeQryGbnCbo(selectedQryGbn) {
	SBUxMethod.attr('datStD', 'readonly', 'true');
	SBUxMethod.attr('datEdD', 'readonly', 'true');
	
	if(selectedQryGbn === '00') {
		SBUxMethod.attr('datStD', 'readonly', 'flase');
		SBUxMethod.attr('datEdD', 'readonly', 'false');
	}
	
	getPrjList();
}

function getPrjList() {
	//하위탭 만든후 풀기..
	//this.parentDocument.subScreenInit(); 하위 탭 초기화
	
	if($("#cboQryGbn option").index($("#cboQryGbn option:selected")) < 0 ) return;
	
	if(SBUxMethod.get('cboQryGbn') === '00' && (SBUxMethod.get('datStD')  > SBUxMethod.get('datEdD')) ) {
		SBUxMethod.openAlert(new Alert('확인', '조회기간을 정확하게 선택하여 주십시오.', 'info'));
		return;
	}
	
	if( SBUxMethod.get('datStD') > getDate('DATE',0) ) {
		SBUxMethod.openAlert(new Alert('확인', '신청일자 시작일이 오늘보다 이후일 수 없습니다.', 'info'));
		return;
	}
	
	var ajaxReturnData = null;
	
	var prjInfoData 		= new Object();
	prjInfoData.userid 	= userId;
	prjInfoData.reqcd 	= reqCd;
	prjInfoData.qrygbn 	= SBUxMethod.get('cboQryGbn');
	prjInfoData.secuyn 	= 'Y';
	prjInfoData.admin = adminYN;
	
	console.log(SBUxMethod.get('cboQryGbn'));
	if(SBUxMethod.get('cboQryGbn') === '00') {
		prjInfoData.stday = SBUxMethod.get('datStD');
		prjInfoData.edday = SBUxMethod.get('datEdD');
	}
	
	if($("#cboReqDepart option").index($("#cboReqDepart option:selected")) > 0 )
		prjInfoData.reqdept = SBUxMethod.get('cboReqDepart');
	
	if($("#cboCatType option").index($("#cboCatType option:selected")) > 0 )
		prjInfoData.cattype = SBUxMethod.get('cboCatType');
	
	//사용안하는 reqCd들인듯..
	if( reqCd === '99' || reqCd === 'LINK' || reqCd === 'CP43' || reqCd === 'CP44') {
		//prjInfoData.isrid = strIsrId;
		//prjInfoData.secuyn = "N";
	}
	
	var prjInfo = {
		prjInfoData: 	JSON.stringify(prjInfoData),
		requestType: 	'GET_PRJ_INFO'
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/srcommon/PrjListTab', prjInfo, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		prjListGridData = ajaxReturnData;
		
		// grid row header 달아주기.
		prjListGridData.forEach(function(prjItem, prjItemIndex) {
			prjItem.seq = prjItemIndex+1;
		});
		
		prjListGrid.refresh();
	}
}

// prjListTab 화면 초기화 버튼 클릭
function initPrjListTab() {
	SBUxMethod.set('cboReqDepart', '0');
	SBUxMethod.set('cboCatType', '00');
	SBUxMethod.set('cboQryGbn', '01');
	
	SBUxMethod.attr('datStD', 'readonly', 'true');
	SBUxMethod.attr('datEdD', 'readonly', 'true');
	
	getPrjList();
}

function prjGridClick() {
	var nRow = prjListGrid.getRow();
	clickedPrjInfo =  JSON.stringify(prjListGrid.getRowData(nRow));
}

function createElementsPrj() {
	var SBGridProperties 		= {};
	SBGridProperties.parentid 	= 'prjListGrid';  			// [필수] 그리드 영역의 div id 입니다.            
	SBGridProperties.id 		= 'prjListGrid';          	// [필수] 그리드를 담기위한 객체명과 동일하게 입력합니다.                
	SBGridProperties.jsonref 	= 'prjListGridData';    	// [필수] 그리드의 데이터를 나타내기 위한 json data 객체명을 입력합니다.
	SBGridProperties.rowheader 	= 'seq';
	// 그리드의 여러 속성들을 입력합니다.
	SBGridProperties.extendlastcol	= 'scroll';
	SBGridProperties.tooltip 		= true;
	SBGridProperties.ellipsis 		= true;
	SBGridProperties.rowdragmove 	= true;
	
	// [필수] 그리드의 컬럼을 입력합니다.  
	SBGridProperties.columns = [
		new GridDefaultColumn('SR-ID', 		'cc_srid', 		'10%', 'output','text-align:center'),
		new GridDefaultColumn('요청제목', 		'cc_reqtitle', 	'20%', 'output'),
		new GridDefaultColumn('등록일', 		'createdate', 	'10%', 'output','text-align:center'),
		new GridDefaultColumn('완료요청일', 	'reqcompdat', 	'10%', 'output','text-align:center'),
		new GridDefaultColumn('요청부서', 		'reqdept', 		'10%', 'output'),
		new GridDefaultColumn('분류유형', 		'cattype', 		'10%', 'output'),
		new GridDefaultColumn('변경종류', 		'chgtype', 		'10%', 'output'),
		new GridDefaultColumn('진행현황', 		'status', 		'10%', 'output'),
		new GridDefaultColumn('작업순위', 		'workrank', 	'10%', 'output','text-align:center')
		];
	prjListGrid = _SBGrid.create(SBGridProperties); 	// 만들어진 SBGridProperties 객체를 파라메터로 전달합니다.
	
	prjListGrid.bind('click','prjGridClick');
}