/**
 * 체크아웃 화면의 기능 정의
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-01-29
 */
var request =  new Request();
var userId = window.parent.userId;

var fileGrid;
var requestGrid;
var fileGridData;
var befFileGridData;

var requestGridData;
var befRuestGridData;

var requestConfirmInfoData;

var cboSysData;
var cboSrIdData;
var cboPrgData;
var treeJsonData = [];

var selectedSrId	= null;
var selectedPrg 	= null;
var reqcd 			= null;
var localFileDownYN = false;
var outpos 			= '';
var reqSw			= false;


$(document).ready(function() {
	console.log('CheckOut.js load');
	screenInit();
});

function screenInit() {
	reqcd =  request.getParameter('reqcd');
	createElements();
	setSysCbo();
	SBUxMethod.attr('idx_request_btn', 'readonly', 'true');
}

function setSysCbo(){
	var ajaxReturnData = null;
	var sysInfoData = new Object();
	sysInfoData.SelMsg = 'SEL';
	sysInfoData.UserId = userId;
	sysInfoData.SecuYn = 'N';
	sysInfoData.ReqCd = reqcd;
	sysInfoData.CloseYn = 'N';
	
	var sysInfo = {
		sysData: 		JSON.stringify(sysInfoData),
		requestType: 	'SYSTEMCOMBO'
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/dev/CheckOut', sysInfo, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		cboSysData = ajaxReturnData;
		
		$(ajaxReturnData).each(function(i){
			if(ajaxReturnData[i].setyn === 'Y'){
				selectedSysData = ajaxReturnData[i];
			}
        });
		SBUxMethod.refresh('select_system');
		SBUxMethod.set('select_system', selectedSysData.cm_syscd);
		setSrIdCbo();
	}
	
}

function changeSysCombo(){
	var selectedSys = SBUxMethod.get('select_system');
	selectedSysData = cboSysData.find(function(data) {
		return data.cm_syscd === selectedSys;
	});
	
	if( selectedSysData.cm_sysinfo.substring(4,5) === '1' && selectedSysData.cm_stopsw === '1' ) {
		alert('이관통제을 위하여 일시적으로 형상관리 사용을 중지합니다.');
		SBUxMethod.attr('idx_search_btn', 'readonly', 'true');
		return;
	}
	
	if( selectedSysData.cm_sysinfo.substring(9,10) === '1') {
		SBUxMethod.attr('select_srid', 'readonly', 'true');
		SBUxMethod.attr('idx_search_btn', 'readonly', 'false');
	}else{
		SBUxMethod.attr('select_srid', 'readonly', 'false');
		SBUxMethod.attr('idx_search_btn', 'readonly', 'false');
	}
	
	if( selectedSysData.localyn !== 'S' && ( selectedSysData.localyn === 'A' || selectedSysData.localyn === 'L' ) ) {
		//로컴 홈경로 가져오기.
		getLocalHomeDir();
	}
	
	setPrgCbo();
}

function getLocalHomeDir() {
	var ajaxReturnData = null;
	var devHomeData = new Object();
	devHomeData.userId = userId;
	devHomeData.sysCd = selectedSysData.cm_syscd;
	
	var devHomeInfo = {
		devHomeData: JSON.stringify(devHomeData),
		requestType: 'GETLOCALHOME'
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/dev/CheckOut', devHomeInfo, 'json');
	if(ajaxReturnData !== 'ERR') {
		if( ajaxReturnData.length === 0)
			alert('로컬로 체크아웃을 받고자 하는 경우 \n [기본관리-사용자환경설정]에서 \n 로컬 홈디렉토리를 지정한 후 진행하시기 바랍니다.');
	}
}

function setSrIdCbo(){
	var ajaxReturnData = null;
	var srInfoData = new Object();
	srInfoData.userid = userId;
	srInfoData.secuyn = 'Y';
	srInfoData.reqcd = reqcd;
	srInfoData.qrygbn = '01';
	
	var srInfo = {
		srData: 		JSON.stringify(srInfoData),
		requestType: 	'SRIDCOMBO'
	}
	
	
	ajaxReturnData = ajaxCallWithJson('/webPage/dev/CheckOut', srInfo, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		$(ajaxReturnData).each(function(i){
			if(ajaxReturnData[i].setyn === 'Y') selectedSrId = ajaxReturnData[i].cc_srid;
        });
		cboSrIdData = ajaxReturnData;
		SBUxMethod.refresh('select_srid');
		SBUxMethod.set('select_srid', selectedSrId);
		setPrgCbo();
	}
	
}

function setPrgCbo(){
	var ajaxReturnData = null;
	var progInfoData = new Object();
	progInfoData.SysCd = selectedSysData.cm_syscd;
	progInfoData.SelMsg = 'ALL';
	
	var progInfo = {
		progData: 		JSON.stringify(progInfoData),
		requestType: 	'PRGCOMBO'
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/dev/CheckOut', progInfo, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		$(ajaxReturnData).each(function(i){
			if(ajaxReturnData[i].setyn === 'Y') 	selectedPrg=ajaxReturnData[i].cm_micode;
        });
		cboPrgData = ajaxReturnData;
		SBUxMethod.refresh('select_prg');
		SBUxMethod.set('select_prg', selectedPrg);
		getFileTree();
	}

}

function getFileTree(){
	var ajaxReturnData = null;
	var fileTreeInfoData = new Object();
	fileTreeInfoData.UserId = userId;
	fileTreeInfoData.SysCd = selectedSysData.cm_syscd;
	fileTreeInfoData.SecuYn = 'Y';
	fileTreeInfoData.SinCd = reqcd;
	fileTreeInfoData.ReqCd = '';
	
	
	var progInfo = {
		fileTreeData: 	JSON.stringify(fileTreeInfoData),
		requestType: 	'FILETREE'
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/dev/CheckOut', progInfo, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		treeJsonData = ajaxReturnData;
		SBUxMethod.refresh('fileTree');
		fileGridData = null;
		fileGrid.refresh();
	}

}

function getChildFileTree(fileInfo, rsrccd, fileId){
    var ajaxReturnData = null;
	var childFileTreeInfoData = new Object();
	childFileTreeInfoData.UserId = userId;
	childFileTreeInfoData.SysCd = selectedSysData.cm_syscd;
	childFileTreeInfoData.FileInfo = fileInfo;
	childFileTreeInfoData.Rsrccd = rsrccd;
	childFileTreeInfoData.FileId = fileId;
	
	var childFileTreeInfo = {
		childFileTreeData: 	JSON.stringify(childFileTreeInfoData),
		requestType: 	'CHILDFILETREE'
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/dev/CheckOut', childFileTreeInfo, 'json');
	
	if(ajaxReturnData !== 'ERR') {
		SBUxMethod.changeChildTreeNode('fileTree', ajaxReturnData, fileId)
	}

}

function fileTreeClick(){
	var treeData 	= SBUxMethod.getTreeStatus('fileTree');
	var sysCd  		= selectedSysData.cm_syscd;
	var sysgb  		= selectedSysData.cm_sysgb;
	var hasParent 	= treeData[0].hasParent;
	var hasChild 	= treeData[0].hasChild;
	var treeAttrObj = treeData[0].attrObj;
	var root 		= treeAttrObj.root;
	var fileId 		= treeAttrObj.id;
	var fileInfo 	= treeAttrObj.cm_info;
	var dsncd  		= treeAttrObj.dsncd;
	var fileinfo 	= treeAttrObj.cm_info;
	var rsrccd 		= treeAttrObj.cm_rsrccd;
	var subrsrccd	= treeAttrObj.cr_rsrccd
	var fullpath = null;
	
	if(treeData[0].attrObj.cm_volpath == null){
		fullpath = treeData[0].attrObj.fullpath;
	}else{
		fullpath = treeData[0].attrObj.cm_volpath;
	}
	
	if(hasParent && fileId !== "-1" ) {
		if( rsrccd ===  undefined) rsrccd = subrsrccd;
		if(root === "true" && !hasChild ) 					getChildFileTree(fileInfo, rsrccd, fileId);
		if( dsncd !== undefined || fullpath !== undefined) 	makeFileDir(fullpath, dsncd, fileinfo, hasChild, sysgb, rsrccd);
	}

}

function makeFileDir(fullpath, dsncd, fileinfo, hasChild, sysgb, rsrccd){
	var lb6split 	= fullpath.split("/");;
	var lb6String1 	= '';
	var lb6		 	= '';
	var toDsnCd 	= null;
	var strDsn 		= null;
	var devToolCon 	= false;
	var getFileData = {};
	var rsrcname 	= null;
	var selectedSubnode = SBUxMethod.get('chkbox_subnode').chkbox_subnode;
	
	if(!devToolCon && fileinfo != undefined && fileinfo.substr(26,1) == "1") {
		devToolCon = true;
	}
	
	if (lb6split.length < 2){
		SBUxMethod.set('idx_lbl_path', '');
	}
	
	for (var i=0 ; i < lb6split.length ; i++){
		if (lb6split[i].length>0) {
			if (i==0 && lb6split[i].indexOf(":")>0) {
				lb6String1 = lb6split[i];	
			} else {
				lb6String1 = lb6String1+ "/"+ lb6split[i];
			}
		}
	}
	
	if (lb6String1.length > 0){
		SBUxMethod.set('idx_lbl_path', lb6String1);
	}
	
	if (selectedSubnode  && lb6String1.length > 0 ){//하위디렉토리 포함 일때
		toDsnCd = "F" + lb6String1 + "/";
	}else if (!selectedSubnode && lb6String1.length > 0 && devToolCon && !hasChild){
		toDsnCd = "F" + lb6String1;
	}else if (!selectedSubnode && lb6String1.length > 0){
		toDsnCd = "G" + lb6String1 + "/";
	}else if (strDsn != "" && strDsn != null) toDsnCd = strDsn;
	else toDsnCd = "";
	
	rsrcname = SBUxMethod.get('idx_lbl_prg_exp_txt');

	getFileData.userid = userId;
	getFileData.syscd = selectedSysData.cm_syscd;
	getFileData.sysgb = sysgb;
	if (toDsnCd.length > 0){
		getFileData.dsncd = toDsnCd;
	} 
	getFileData.rsrccd = rsrccd;
	getFileData.reqcd = reqcd;
	if(rsrcname === undefined) getFileData.rsrcname = '';
	else getFileData.rsrcname = rsrcname;
	
	
	var getFileDataInfo = {
			requestType: 'GETFILEGRID',
			getFileData: JSON.stringify(getFileData)
	}
	
	getFileGridData(getFileDataInfo );
}

function getFileGridData(getFileData) {
	var ajaxReturnData = null;
	fileGrid.lockGrid();
	fileGrid.refresh();
	fileGrid.clearStatus();
	
	ajaxReturnData = ajaxCallWithJson('/webPage/dev/CheckOut', getFileData, 'json');

	if(ajaxReturnData !== 'ERR') {
		fileGridData = copyReferenceNone(ajaxReturnData);
		
		if(requestGridData.length > 0 ){
			fileGridData.forEach( function(fileGridItem, fileGridItemIndex) {
				requestGridData.forEach( function(requestGridItem, requestGridItemIndex) {
					if(fileGridItem.cr_itemid == requestGridItem.cr_itemid){
						fileGridItem.selected_flag = '1';
						return false;
					}
				});
			});
		}
		
		fileGrid.refresh();
		changeFileGridStyle(fileGridData);
	}

	fileGrid.lockGrid(false);
};



function changeFileGridStyle(data) {
	$(data).each(function(i) {
		if(data[i].selected_flag == '1' || data[i].cr_status != '0'  || data[i].cr_nomodify != '0' ){
			fileGrid.setRowStyle(i+1, 'data', 'color', '#FF0000');
		}else{
			fileGrid.setRowStyle(i+1, 'data', 'color', 'black');
		}
	});
}

function deleteDataRow() {
	var fileSelectedRow = [];
	fileSelectedRow = requestGrid.getSelectedRows();
	var upFileList = [];
	
	
	fileSelectedRow.forEach( function(selectedItem, index) {
		upFileList.push(requestGrid.getRowData(selectedItem,false))
	});
	
	if(upFileList.length > 0 ) upFileCheck(upFileList);
	else console.log('checkUpFile');
}

function upFileCheck(upFileList) {
	
	$( upFileList ).each(function(i) {
		$( requestGridData ).each(function(j) {
			if( upFileList[i].baseitemid == requestGridData[j].baseitemid ) {
				requestGridData.splice(j,1);
				
				$( fileGridData ).each(function(k) {
					if (upFileList[i].baseitemid == fileGridData[k].cr_itemid) {
						fileGridData[k].selected_flag = '0';
						return false;
					}
				});
				
				return false;
			}
		});
	});
	
	requestGrid.rebuild();
	fileGrid.rebuild();
	changeFileGridStyle(fileGridData);
	
	if( requestGridData.length == 0) {
		SBUxMethod.attr('select_system', 'readonly', 'false');
		SBUxMethod.attr('idx_request_btn', 'readonly', 'true');
		outpos = '';
	}
	
	/*//위에 폴스일경우 해줘야하는일
	 if (cmdDiff.visible) {
		findSw = false;
		for (i=0;list2_grid_dp1.length>i;i++) {
			if (list2_grid_dp1.getItemAt(i).cr_lstusr != strUserId 
			  && list2_grid_dp1.getItemAt(i).cm_info.substr(1,1) == "1" 
			  && list2_grid_dp1.getItemAt(i).cm_info.substr(2,1) == "0" 
			  && list2_grid_dp1.getItemAt(i).cm_info.substr(9,1)=="0" 
			  && list2_grid_dp1.getItemAt(i).cm_info.substr(44,1)=="1") {
				findSw = true;
			}
			if ( list2_grid_dp1.getItemAt(i).cm_info.substr(44,1) == "1" ) {//45:로컬에서개발
				if ( outpos == "R" ) {
					outpos = "A";
				} else if ( outpos != "A" ) {
					outpos = "L";
				}
			} else {
				if ( outpos == "L" ){
					outpos = "A";
				} else if ( outpos != "A" ) {
					outpos = "R";
				}
			}
		}
		if ( !findSw ) {
			cmdDiff.visible = false;
			regist_button.enabled = true;
		}
	}
	*/
}

function addDataRow() {
	var fileSelectedRow = [];
	fileSelectedRow = fileGrid.getSelectedRows();
	var downFileList = [];
	
	fileSelectedRow.forEach(function(selectedItem,index){
		downFileList.push(fileGrid.getRowData(selectedItem,false));
	});
	
	if(downFileList.length > 0) downFileCheck(downFileList);
	else console.log('checkDownFile');
	
}

function downFileCheck(downFileList) {
	
	var removedFileList = [];
	downFileList.forEach(function(downFileItem,index){
		if(downFileItem.selected_flag == '0' && downFileItem.cr_status == '0'){
			removedFileList.push(downFileItem);
		}
	})
	
	var calcnt = 0;
	var vercnt = 0;
	localFileDownYN = false;
	var downFileArray = new Array();
	var downFileData = new Object();
	var ajaxRequestData = {};
	
	$(removedFileList).each(function(i){
		var removedFile = removedFileList[i];
		if( removedFile.cm_info.substring(26,27) == '1' || removedFile.cm_info.substring(3,4) == '1' ||
			removedFile.cm_info.substring(46,47) == '1' || removedFile.cm_info.substring(8,9) == '1' ){
			calcnt++;
		}
		if( removedFile.cm_info.substring(44,45) == '1') {
			localFileDownYN = true;
			if(removedFile.pcdir1 == null || removedFile.pcdir1 == "") {
				alert("로컬 홈디렉토리를 지정하지 않았습니다. 기본관리>사용자환경설정에서 홈디렉토리지정 후 처리하시기 바랍니다.");
				return;
			}
		}
		if (reqcd == '02') {
			if (removedFile.cr_lstver == "sel"){
				vercnt++;
				calcnt++;
			}
		}
	});
	
	if (vercnt>0){				
		alert("버전을 선택하여 주십시요.");
		return;
	}
	
	if( (removedFileList.length + requestGridData.length) > 300 ){
		alert('300건까지 신청 가능합니다.');
		return;
	}
	
	if( removedFileList.length > 0 ){
		if( calcnt > 0 ) {
			downFileData.strUserId = userId;
			downFileData.strReqCD = reqcd;
			downFileData.syscd = selectedSysData.cm_syscd;
			downFileData.sayu = '';
			downFileData.localFileDownYN = localFileDownYN;
			
			ajaxRequestData = {
				downFileData : JSON.stringify(downFileData),
				removedFileList : JSON.stringify(removedFileList),
				requestType : 'GRIDDOWNFILE'
			}
			getDownFileList(ajaxRequestData);
		}else{
			checkDuplication(removedFileList);
		}
	}
}

function getDownFileList(downFileDatas) {
	var ajaxReturnData = null;
	
	requestGrid.lockGrid();
	ajaxReturnData = ajaxCallWithJson('/webPage/dev/CheckOut', downFileDatas, 'json');
	if(ajaxReturnData !== 'ERR') checkDuplication(ajaxReturnData);
	requestGrid.lockGrid(false);
};

function checkDuplication(downFileList) {
	
	var findSw = true;
	var addedFileList = [];
	
	if(requestGridData.length > 0 ){
		$(requestGridData).each(function(i){
			$(downFileList).each(function(j){
				if( requestGridData[i].cr_itemid == downFileList[j].cr_itemid ){
					downFileList.splice(j,1);
					return false;
				}
			});
		});
	}
	
	if(downFileList.length > 0) {
		$(downFileList).each(function(i){
			var currentItem = downFileList[i];
			//하단리스트에 추가한 프로그램중, 로컬 프로그램이 존재하는지 체크 시작  && 바이너리가 아닌거 && 체크아웃 대상인거 && 체크아웃무가 아닌거
			if( !findSw
				&&	currentItem.cm_info.substring(44,45)== '1'
				&& 	currentItem.cm_info.substring(1,2) 	== '1'
				&& 	currentItem.cm_info.substring(2,3) 	== '1'
				&& 	currentItem.cm_info.substring(9,10) == '1'
				&& 	currentItem.cr_lstusr != userId){
				findSw = true;
			}
			
			if(currentItem.baseitemid == currentItem.cr_itemid){
				$(fileGridData).each(function(j){
					if(fileGridData[j].cr_itemid == currentItem.cr_itemid) {
						fileGridData[j].selected_flag = '1';
						return false;
					}
					
				});
			}
		});
	}
	
	
	if(requestGridData.length > 0 ) {
		if((requestGridData.length + downFileList.length > 300) && localFileDownYN){
			alert('300건 이하로 선택 가능합니다.[추가가능건수:'+(300-requestGridData.length)+']');
			return;
		}
	}else if (downFileList.length > 300 && localFileDownYN ) {
		alert('300건 이하로 선택 가능합니다.[추가가능건수:'+(downFileList.length)+']');
		return;
	}
	
	addedFileList 		= requestGridData.concat(downFileList);
	requestGridData	= copyReferenceNone(addedFileList);
	
	
	if(requestGridData.length > 0 ) {
		SBUxMethod.attr('select_system', 'readonly', 'true');
		//파일비교 버튼 보이게 수정
		SBUxMethod.attr('idx_request_btn', 'readonly', 'false');
		$(requestGridData).each(function(i) {
			var currentItem = requestGridData[i];
			if( currentItem.cm_info.substring(44,45) == '1') {
				if( outpos == 'R'){
					outpos = 'A';
				} else if ( outpos != 'A'){
					outpos = 'L';
				}
			} else {
				if( outpos == 'L' ) {
					outpos = 'A';
				} else if ( outpos != 'A' ) {
					outpos = 'R';
				}
			}
		});
	}
	
	fileGrid.refresh();
	requestGrid.refresh(); 
	changeFileGridStyle(fileGridData);
}

function createElements() {
	var SBGridProperties = {};
	SBGridProperties.parentid = 'fileGrid';  // [필수] 그리드 영역의 div id 입니다.            
	SBGridProperties.id = 'fileGrid';          // [필수] 그리드를 담기위한 객체명과 동일하게 입력합니다.                
	SBGridProperties.jsonref = 'fileGridData';    // [필수] 그리드의 데이터를 나타내기 위한 json data 객체명을 입력합니다.
	
	// 그리드의 여러 속성들을 입력합니다.
	SBGridProperties.extendlastcol = 'scroll';
	SBGridProperties.tooltip = true;
	SBGridProperties.ellipsis = true;
	SBGridProperties.rowdragmove = true;
	
	// [필수] 그리드의 컬럼을 입력합니다.  
	SBGridProperties.columns = [
		{caption: ['프로그램경로'],	ref: 'cm_dirpath', 	width : '500px',  type : 'output'},
		{caption: ['프로그램명'],	ref: 'cr_rsrcname',	width : '150px',  type : 'output'},
		{caption: ['프로그램종류'],	ref: 'jawon',		width : '100px',  type : 'output',	style : 'text-align:center'},
		{caption: ['프로그램설명'],	ref: 'cr_story',	width : '200px',  type : 'output'},
		{caption: ['상태'],		ref: 'codename',	width : '80px',   type : 'output',	style : 'text-align:center'},
		{caption: ['버전'],		ref: 'cr_lstver',	width : '80px',   type : 'output',	style : 'text-align:center'},
		{caption: ['수정자'],		ref: 'cm_username',	width : '90px',   type : 'output'},
		{caption: ['수정일'],		ref: 'lastdt',		width : '120px',  type : 'output',	style : 'text-align:center'}
	];
	fileGrid = _SBGrid.create(SBGridProperties); // 만들어진 SBGridProperties 객체를 파라메터로 전달합니다.
	
	var SBGridProperties2 = {};
	SBGridProperties2.parentid = 'requestGrid';  // [필수] 그리드 영역의 div id 입니다.            
	SBGridProperties2.id = 'requestGrid';          // [필수] 그리드를 담기위한 객체명과 동일하게 입력합니다.                
	SBGridProperties2.jsonref = 'requestGridData';    // [필수] 그리드의 데이터를 나타내기 위한 json data 객체명을 입력합니다.
	
	// 그리드의 여러 속성들을 입력합니다.
	SBGridProperties2.extendlastcol = 'scroll';
	SBGridProperties2.tooltip = true;
	SBGridProperties2.ellipsis = true;
	SBGridProperties2.rowdragmove = true;
	
	SBGridProperties2.columns = [
		{caption: ['프로그램경로'],	ref: 'view_dirpath',width: '500px',  type: 'output'},
		{caption: ['프로그램명'],	ref: 'cr_rsrcname',	width: '150px',  type: 'output'},
		{caption: ['업무명'],		ref: 'jobname',		width: '150px',  type: 'output'},
		{caption: ['프로그램종류'],	ref: 'jawon',		width: '100px',  type: 'output', style : 'text-align:center'},
		{caption: ['프로그램설명'],	ref: 'cr_story',	width: '200px',  type: 'output'},
		{caption: ['신청버전'],	ref: 'cr_lstver',	width: '80px',   type: 'output', style : 'text-align:center'},
		{caption: ['로컬위치'],	ref: 'pcdir1',		width: '80px',   type: 'output', style : 'text-align:center'},
		{caption: ['수정자'],		ref: 'cm_username',	width: '90px',   type: 'output'},
		{caption: ['수정일'],		ref: 'lastdt',		width: '120px',  type: 'output', style : 'text-align:center'}
	];
	requestGrid = _SBGrid.create(SBGridProperties2); // 만들어진 SBGridProperties 객체를 파라메터로 전달합니다.
	
	fileGrid.setDnD({target : 'requestGrid', type : 'copy', position : 'insert', stylesync : false});
	requestGrid.setDnD({target : 'fileGrid', type : 'copy', position : 'insert', stylesync : false});
	
	fileGrid.bind('afterdnd','fileGridAfterDnd'); 
	requestGrid.bind('afterdnd','requestGridAfterDnd'); 
	
	fileGrid.bind('startdnd','fileGridStartDnd');
	requestGrid.bind('startdnd','requestGridStartDnd'); 
	
}


function fileGridStartDnd() {
	befRuestGridData = copyReferenceNone( requestGridData );
}

// 검색그리드에서 요청그리드로 드래그후 이벤트
function requestGridAfterDnd() {
	requestGridData = copyReferenceNone(befRuestGridData);
	requestGrid.refresh();
	addDataRow();
}

// 요청그리드 드래그 시작시 발동 이벤트
function requestGridStartDnd() {
	befFileGridData = copyReferenceNone( fileGridData );
}

//요청그리드에서 검색그리드로 드래그후 이벤트
function fileGridAfterDnd() {
	fileGridData = copyReferenceNone(  befFileGridData );
	fileGrid.refresh();
	deleteDataRow();
}

function  openSrModal() {
	console.log('srModal Click');
}

function clickSearchBtn() {
	var getFileData = {};
	var rsrcname 	= null;
	getFileData.userid 	= userId;
	getFileData.syscd 	= selectedSysData.cm_syscd;
	getFileData.sysgb 	= selectedSysData.sysgb;
	getFileData.rsrccd 	= SBUxMethod.get('select_prg');
	getFileData.reqcd 	= reqcd;
	rsrcname = SBUxMethod.get('idx_lbl_prg_exp_txt');
	if(rsrcname === '' || rsrcname === undefined) {
		alert('검색단어를 입력한 후 검색하시기 바랍니다.');
		return;
	}
	else getFileData.rsrcname = rsrcname;
	var getFileDataInfo = {
			requestType: 'GETFILEGRID',
			getFileData: JSON.stringify(getFileData)
	}
	
	
	getFileGridData(getFileDataInfo );
}

function clickCheckOutBtn(){
	var returnConfirmStr = null;
	if(checkValidation() ) {
		returnConfirmStr = checkConfirm();
		
		if( returnConfirmStr !== null){
			
			if(returnConfirmStr === 'X') {
				alert('로컬PC에서 파일을 전송하는 결재단계가 지정되지 않았습니다. 형상관리시스템담당자에게 연락하여 주시기 바랍니다.');
			}
			if(returnConfirmStr === 'C') {
				if(confirm('결재자를 지정하시겠습니까?')) confCall('Y');
				else confCall('N');
			}
			if(returnConfirmStr === 'Y') {
				confCall('N');
			}
			
			if(returnConfirmStr !== 'N' && returnConfirmStr !== 'Y' && returnConfirmStr !== 'C' && returnConfirmStr !== 'X'){
				alert('결재정보가 등록되지 않았습니다. 형상관리시스템담당자에게 연락하여 주시기 바랍니다.');
			} else {
				confCall('N');
			}
			
		}
	}
}

function confCall(gbnCd) {
	var strQry = null;
	var rsrcCd = '';
	var confirmData = {};
	var confirmInfo = {};
	var ajaxReturnData;
	requestGridData.forEach(function(requestItem, requestItemIndex) {
		if(rsrcCd.indexOf(requestItem.cr_rsrccd) < 0) rsrcCd += (rsrcCd.length === 0 ? 
																	requestItem.cr_rsrccd : 
																		',' + requestItem.cr_rsrccd);
	});
	
	confirmData.UserID = userId;
	confirmData.ReqCD = reqcd;
	confirmData.SysCd = selectedSysData.cm_syscd;
	confirmData.Rsrccd = rsrcCd;
	confirmData.QryCd = strQry;
	confirmData.EmgSw = 'N';
	confirmData.PrjNo = '';
	confirmData.OutPos = outpos;
	if(  $("#select_srid option").index($("#select_srid option:selected")) > 0 ) confirmData.PrjNo = SBUxMethod.get('select_srid');
	if(gbnCd === 'Y') {
		// 결재팝업 띄우기
		/*gyulPopUp = Confirm_select(PopUpManager.createPopUp(this, Confirm_select, true));
		gyulPopUp.parentfuc = reqQuest;
		gyulPopUp.parentvar = etcObj;
	    PopUpManager.centerPopUp(gyulPopUp);//팝업을 중앙에 위치하도록 함
	    gyulPopUp.minitApp();*/
		return;
	}
	
	if(gbnCd === 'N') {
		var confirminfo = {
			confirmInfoData: 	JSON.stringify(confirmData),
			requestType: 		'GETCONFIRMINFO'
		}
		ajaxReturnData = ajaxCallWithJson('/webPage/dev/CheckOut', confirminfo, 'json');
		requestConfirmInfoData = copyReferenceNone(ajaxReturnData);
		
		requestCheckOut();
		return;
	}
}

function requestCheckOut() {
	reqSw = true;
	var ajaxReturnStr = null;
	var requestData = {};
	requestData.UserID = userId;
	requestData.ReqCD  = reqcd;
	requestData.Sayu	 = SBUxMethod.get('idx_request_text');
	requestData.cm_syscd = selectedSysData.cm_syscd;
	requestData.cm_sysgb = selectedSysData.cm_sysgb;
	requestData.ckoutpos = outpos;
	
	if($("#select_srid option").index($("#select_srid option:selected")) > 0 ) {
		requestData.srid 		 = SBUxMethod.get('select_srid');
		cboSrIdData.forEach(function(srItem, srItemIndex) {
			if(SBUxMethod.get('select_srid') === srItem.cc_srid) requestData.cc_reqtitle = srItem.cc_reqtitle;
		});
	}else {
		requestData.srid  		 = '';
		requestData.cc_reqtitle  = '';
	}
	var requestInfo = {
		requestData: 	JSON.stringify(requestData),
		requestFiles:	JSON.stringify(requestGridData),
		requestConfirmData:	JSON.stringify(requestConfirmInfoData),
		requestType: 	'REQUESTCHECKOUT'
	}
	
	ajaxReturnStr = ajaxCallWithJson('/webPage/dev/CheckOut', requestInfo, 'json');
	
	/*reqSw = true;
	var etcObj:Object = {};
	etcObj.UserID = strUserId;
	etcObj.ReqCD  = strReqCD;
	etcObj.Sayu	  = sayu_txt.text;
	etcObj.cm_syscd = syscd_combo.selectedItem.cm_syscd;
	etcObj.cm_sysgb = syscd_combo.selectedItem.cm_sysgb;
	if(cboIsrId.enabled && cboIsrId.selectedIndex > 0){
		etcObj.srid  = cboIsrId.selectedItem.cc_srid;
		etcObj.cc_reqtitle  = cboIsrId.selectedItem.cc_reqtitle;
	}else{
		etcObj.srid = "";
		etcObj.cc_reqtitle = "";
	}
//	if (optPos1.selected) etcObj.ckoutpos = "R";
//	else etcObj.ckoutpos = "L";
	etcObj.ckoutpos = outpos;
	Cmr0100.request_Check_Out(list2_grid_dp1.toArray(),etcObj,confirm_dp.toArray());
	etcObj = null;*/
}

function checkValidation() {
	var requestText = null;
	var requestFlag = false;
	requestText = $('#idx_request_text').val().length !== 0 ? $('#idx_request_text').val().trim() : null ;
	
	if( !$('#select_srid').is('disabled') && $("#select_srid option").index($("#select_srid option:selected")) < 1) {
		alert('SR-ID를 선택하여 주십시오.');
		return;
	}
	
	if(requestText === null || requestText.length === 0) {
		alert('신청사유를 입력하여 주십시오.');
		$('#idx_request_text').focus();
		return;
	}
	
	if(requestGridData.length === 0) {
		alert('신청할 파일을 입력하여 주십시오.');
		return;
	}
	
	if (reqSw === true) {
		Alert.show("현재 처리중입니다. 잠시 기다려 주시기 바랍니다.");
		return;
	}
	
	if(confirm("체크아웃 하시겠습니까?") === true) {
		requestFlag =  true;
    }
	
	return requestFlag;
}

function checkConfirm() {
	var ajaxReturnStr = null;
	var confirmInfoData = new Object();
	var rsrcCd = null;
	confirmInfoData.sysCd = selectedSysData.cm_syscd;
	confirmInfoData.strReqCd = reqcd;
	confirmInfoData.userId = userId;
	confirmInfoData.strQry = reqcd;
	
	
	requestGridData.forEach( function(requestItem, requestItemIndex ) {
		if( rsrcCd === null) rsrcCd = requestItem.cr_rsrccd;
		else rsrcCd += ','+requestItem.cr_rsrccd;
	});
	confirmInfoData.strRsrcCd = rsrcCd;
	
	var confirminfo = {
		confirmInfoData: 	JSON.stringify(confirmInfoData),
		requestType: 		'CHECKCONFIRM'
	}
	
	ajaxReturnStr = ajaxCallWithJson('/webPage/dev/CheckOut', confirminfo, 'json');
	return ajaxReturnStr;
}



