 var treeOrganizationAndPersonData;
 var treeOrganizationData;
 var treeSubSw = false;
 var selectedData = {};
 
$(document).ready(function() {
});

function modalOrganizationInit(treeOrganizationSubSw) {
	if(treeOrganizationSubSw) {
		$('#treeOrganization').css('display','none');
	}else{
		$('#treeOrganizationAndPerson').css('display','none');
	}
	
	treeSubSw = treeOrganizationSubSw;
	
	var ajaxReturnData = null;
	var treeInfo = {
		treeInfoData: 	JSON.stringify(treeOrganizationSubSw),
		requestType: 	'GET_TREE_INFO'
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/modal/TreeOrganization', treeInfo, 'json');
	console.log(ajaxReturnData);
	if(ajaxReturnData !== 'ERR') {
		if( treeOrganizationSubSw ) {
			treeOrganizationAndPersonData = ajaxReturnData;
			SBUxMethod.refresh('treeOrganizationAndPerson');
		}else{
			treeOrganizationData = ajaxReturnData;
			SBUxMethod.refresh('treeOrganization');
		}
	}
}

function searchFileTree() {
	var searchInputValue = $('#txtSearch').val();
	var findObj = null;
	
	if (searchInputValue) {
		if(treeSubSw){
			findObj = _.find(treeOrganizationAndPersonData, function(treeItem){ return treeItem.text.indexOf(searchInputValue)>-1 ; });
			if (findObj){
				// extValue 옵션을 주어 확장 모드로 만든다.
				SBUxMethod.set('treeOrganizationAndPerson', findObj.id, {extValue:'expand'})
			}
		}else{
			findObj = _.find(treeOrganizationData, function(treeItem){ return treeItem.text.indexOf(searchInputValue)>-1 ; });
			if (findObj){
				// extValue 옵션을 주어 확장 모드로 만든다.
				SBUxMethod.set('treeOrganization', findObj.id, {extValue:'expand'})
			}
		}
	}
}


function fnExpandAll(){
	if(treeSubSw)SBUxMethod.expandTreeNodes('treeOrganizationAndPerson','000000100',4);
	else SBUxMethod.expandTreeNodes('treeOrganization','000000100',4);
}

function apllyTreeInfo() {
	var treeData 	= treeSubSw ? SBUxMethod.getTreeStatus('treeOrganizationAndPerson') : SBUxMethod.getTreeStatus('treeOrganization');
	if(treeData === undefined) {
		SBUxMethod.openAlert(new Alert('확인', '선택후 눌러주세요.', 'info'));
		return;
	}
	var treeAttrObj = treeData[0].attrObj;
	
	
	if(treeSubSw) {
		if(treeAttrObj.division !== 'user') {
			SBUxMethod.openAlert(new Alert('확인', '사용자를 선택하여 주시기 바랍니다.', 'info'));
			return;
		}
		selectedData = treeAttrObj;
		parent.closeModal(true);
	} else {
		//하위조직만 선택가능하도록 변경 필요
		if(treeAttrObj.division !== 'depart') {
			SBUxMethod.openAlert(new Alert('확인', '조직을 선택하여 주시기 바랍니다.', 'info'));
			return;
		}
		selectedData = treeAttrObj;
		parent.closeModal(true);
	}
}

function cancleTree() {
	parent.closeModal(false);
}

