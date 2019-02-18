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
var cboDevUserData;

var insertSrIdSw = false;
var inProgressSw = false;
var strIsrId = '';

var treeOranizationSubSw = false;

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
	changeCboReqSecu('0');
}

function elementInit(initDivision) {
	SBUxMethod.attr('btnFileAdd', 		'readonly', 'false');
	SBUxMethod.attr('btnAddDevUser', 	'readonly', 'false');
	SBUxMethod.attr('btnDelDevUser', 	'readonly', 'false');
	
	if(initDivision === 'NEW'){
    	strIsrId = '';
    	/*if( this.parentDocument.toString().indexOf("eCmc0100") > -1 ) {
    		this.parentDocument.tab0.grdPrj.selectedIndex = -1;
    	}*/
    	SBUxMethod.set('datReqComDate','');
    	SBUxMethod.set('chkNew','true');
    	SBUxMethod.attr('btnUpdate', 	'readonly', 'true');
    	SBUxMethod.attr('btnRegister', 	'readonly', 'false');
    	SBUxMethod.attr('btnDelete', 	'readonly', 'true');
    	SBUxMethod.set('txtSRID','신규등록');
    	SBUxMethod.set('txtRegUser', userName);
    	SBUxMethod.set('txtRegDate', '신규등록');
    	
    	SBUxMethod.attr('txtSRID', 		'readonly', 'true');
    	SBUxMethod.attr('txtRegUser', 	'readonly', 'true');
    	SBUxMethod.attr('txtRegDate', 	'readonly', 'true');
    	
    	insertSrIdSw = true;
    	
    	var ajaxReturnData = null;
    	var userInfo = {
			userInfoData: 	JSON.stringify(userId),
			requestType: 	'GET_USER_COMBO'
		}
		
		ajaxReturnData = ajaxCallWithJson('/webPage/srcommon/SRRegisterTab', userInfo, 'json');
		if(ajaxReturnData !== 'ERR') {
			cboDevUserData = ajaxReturnData;
			SBUxMethod.refresh('cboDevUser');
			if(cboDevUserData.length === 2) $("#cboDevUser option:eq(1)").attr("selected","selected");
		}
    } else if(strIsrId !== null && strIsrId !== '') {
    	if(initDivision === 'M'){
    		SBUxMethod.refresh('chkNew');
    		SBUxMethod.attr('btnRegister', 	'readonly', 'true');
    		SBUxMethod.attr('btnUpdate', 	'readonly', 'true');
    		SBUxMethod.attr('btnDelete', 	'readonly', 'true');
    	} else {
    		//저장 상태 - 모이라 등록건?
    		if( initDivision === '0') {
    			SBUxMethod.attr('btnRegister', 	'readonly', 'false');
        		SBUxMethod.attr('btnUpdate', 	'readonly', 'true');
        		SBUxMethod.attr('btnDelete', 	'readonly', 'true');
        	// 등록완료, 진행중, 등록승인중 반려
    		} else if( initDivision === '2' || initDivision === 'C' || initDivision === '4') {
    			//등록완료, 진행중 이면
    			if(initDivision === '2'  || initDivision === 'C' ) { 
    				SBUxMethod.attr('btnDelete', 	'readonly', 'false');
    			//등록승인중반려 면
    			} else { 
    				SBUxMethod.attr('btnDelete', 	'readonly', 'true');
	    		}
    			SBUxMethod.attr('btnRegister', 	'readonly', 'true');
        		SBUxMethod.attr('btnUpdate', 	'readonly', 'false');
    		}else { //그외 상태일 때
    			
    			SBUxMethod.attr('btnRegister', 	'readonly', 'true');
        		SBUxMethod.attr('btnUpdate', 	'readonly', 'true');
        		SBUxMethod.attr('btnDelete', 	'readonly', 'true');
        		
        		//등록승인중, 반려, 적용확인중, 완료승인중 이면
		    	if( initDivision === '1' || initDivision === '3' || 
		    			initDivision === '5' || initDivision === 'A') {
		    		SBUxMethod.attr('btnFileAdd', 		'readonly', 'true');
	        		SBUxMethod.attr('btnAddDevUser', 	'readonly', 'true');
	        		SBUxMethod.attr('btnDelDevUser', 	'readonly', 'true');
		    	}
	    	}
    		
    		SBUxMethod.set('txtSRID',	'');
    		SBUxMethod.set('txtRegUser','');
    		SBUxMethod.set('txtRegDate','');
    		
    		insertSrIdSw = false;
    		
    	}
    }else {
    	
    	SBUxMethod.set('txtSRID',	'');
		SBUxMethod.set('txtRegUser','');
		SBUxMethod.set('txtRegDate','');
		
		SBUxMethod.attr('btnFileAdd', 		'readonly', 'true');
		SBUxMethod.attr('btnAddDevUser', 	'readonly', 'true');
		SBUxMethod.attr('btnDelDevUser', 	'readonly', 'true');
		SBUxMethod.attr('btnRegister', 		'readonly', 'true');
		SBUxMethod.attr('btnUpdate', 		'readonly', 'true');
		SBUxMethod.attr('btnDelete', 		'readonly', 'true');
    }
	
	
	SBUxMethod.set('txtDocuNum', 	'');
	//SBUxMethod.set('txtRegUser', 	'');
	SBUxMethod.set('txtReqSubject', '');
	SBUxMethod.set('texReqContent', '');
	SBUxMethod.set('txtDevUser', 	'');
	
	fileAddGridData = null;
	devUserGridData = null;
	
	//fileAddGrid.refresh();
	//devUserGrid.refresh();
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

function clickChkNew() {
	var chkNew = SBUxMethod.get('chkNew').chkNew;
	if(chkNew) {
		elementInit('NEW')
	} else {
		elementInit('');
	}
}

function changeCboReqSecu(selectedValue) {
	if(selectedValue === '6') {
		$('#txtReqSecu').css('display','block');
	}else {
		$('#txtReqSecu').css('display','none');
		$('#cboReqSecu').removeClass('width-20');
		$('#cboReqSecu').addClass('width-100');
	}
}
/*$('#exampleModal').on('show.bs.modal', function (event) {
	var button = $(event.relatedTarget) // Button that triggered the modal
	var recipient = button.data('whatever') // Extract info from data-* attributes
	var modal = $(this)
	modal.find('.modal-title').text('New message to ' + recipient)
	modal.find('.modal-body input').val(recipient)
})*/

// findDivision = 1 > 사람검색
// findDivision = 0 > 부서검색
function findPesonOrDepart(findDivision) {
	if(findDivision === '1') treeOranizationSubSw = true;
	else treeOranizationSubSw = false;
	SBUxMethod.openModal('modalOrganization');
	
	console.log(treeOranizationSubSw);
	
	var modalIframe = document.getElementById("modalOrganizationBody");
	modalIframe.contentWindow.modalOrganizationInit(treeOranizationSubSw);

}