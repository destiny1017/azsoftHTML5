/** SR 등록 탭 화면 정의 (공통화면)
 * 
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-02-08
 */

var fileAddGrid;
var fileAddGridData;
var devUserGrid;
var devUserGridData;
var userId 	 = window.parent.userId;
var userName = window.parent.userName;
var reqCd;
var adminYN = window.parent.adminYN;
var request =  new Request();

var cboCatTypeSRData;
var cboChgTypeData;
var cboWorkRankData;
var cboReqSecuData;


$(document).ready(function() {
	reqCd =  request.getParameter('reqcd');
	screenInit();
	
	/* 다른화면에서 사용시 구현...
		if (strAcptNo != null && strAcptNo != "") {
			Cmr3100.gyulChk(strAcptNo,strUserId);//결재자 인지 체크
		}
	*/
});


//초기 화면 셋팅
function screenInit() {
	createElements();
	setCboElement();
}

function setCboElement() {
	var codeInfos = getCodeInfoCommon( [new CodeInfo('CATTYPE','SEL','N'),
										new CodeInfo('CHGTYPE','SEL','N'),
										new CodeInfo('WORKRANK','SEL','N'),
										new CodeInfo('REQSECU','SEL','N')
										] );
	
	cboCatTypeSRData= codeInfos.CATTYPE;
	cboChgTypeData= codeInfos.CHGTYPE;
	cboWorkRankData= codeInfos.WORKRANK;
	cboReqSecuData= codeInfos.REQSECU;
	
	SBUxMethod.refresh('cboCatTypeSR');
	SBUxMethod.refresh('cboChgType');
	SBUxMethod.refresh('cboWorkRank');
	SBUxMethod.refresh('cboReqSecu');
	
	//SBUxMethod.set('cboQryGbn', '01');
}


function createElements() {
	var SBGridProperties 		= {};
	SBGridProperties.parentid 	= 'fileAddGrid';  			// [필수] 그리드 영역의 div id 입니다.            
	SBGridProperties.id 		= 'fileAddGrid';          	// [필수] 그리드를 담기위한 객체명과 동일하게 입력합니다.                
	SBGridProperties.jsonref 	= 'fileAddGridData';    	// [필수] 그리드의 데이터를 나타내기 위한 json data 객체명을 입력합니다.
	//SBGridProperties.rowheader 	= 'seq';
	// 그리드의 여러 속성들을 입력합니다.
	SBGridProperties.extendlastcol	= 'scroll';
	SBGridProperties.tooltip 		= true;
	SBGridProperties.ellipsis 		= true;
	SBGridProperties.rowdragmove 	= true;
	
	// [필수] 그리드의 컬럼을 입력합니다.  
	SBGridProperties.columns = [new GridDefaultColumn('파일명', 		'fileName', 	'100%', 'output')];
	fileAddGrid = _SBGrid.create(SBGridProperties); 	// 만들어진 SBGridProperties 객체를 파라메터로 전달합니다.
	
	
	var SBGridProperties2 		= {};
	SBGridProperties2.parentid 	= 'devUserGrid';  			// [필수] 그리드 영역의 div id 입니다.            
	SBGridProperties2.id 		= 'devUserGrid';          	// [필수] 그리드를 담기위한 객체명과 동일하게 입력합니다.                
	SBGridProperties2.jsonref 	= 'devUserGridData';    	// [필수] 그리드의 데이터를 나타내기 위한 json data 객체명을 입력합니다.
	//SBGridProperties.rowheader 	= 'seq';
	// 그리드의 여러 속성들을 입력합니다.
	SBGridProperties2.extendlastcol	= 'scroll';
	SBGridProperties2.tooltip 		= true;
	SBGridProperties2.ellipsis 		= true;
	SBGridProperties2.rowdragmove 	= true;
	
	// [필수] 그리드의 컬럼을 입력합니다.  
	SBGridProperties2.columns = [
		new GridDefaultColumn('소속부서', 		'fileName', 	'40%', 'output'	,'text-align:center'),
		new GridDefaultColumn('담당개발자', 	'fileName', 	'40%', 'output' ,'text-align:center'),
		new GridDefaultColumn('상태', 		'fileName', 	'20%', 'output' ,'text-align:center'),
		];
	devUserGrid = _SBGrid.create(SBGridProperties2); 	// 만들어진 SBGridProperties 객체를 파라메터로 전달합니다.
}