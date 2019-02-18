 var treeOrganizationAndPersonData;
 var treeOrganizationData;
 var treeSubSw = false;
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

function updateTree() {
	if(treeSubSw) {
		console.log(SBUxMethod.get('treeOrganizationAndPerson'));
	} else {
		
	}
}

function cancleTree() {
	
}

