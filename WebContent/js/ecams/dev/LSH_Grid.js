// http://axisj.com/
// http://ax5.io/ax5ui-grid/demo/1-formatter.html
// userid 및 ReqCD 가져오기
var userid = window.parent.userId;   
var strReqCD = "";
var request =  new Request();
strReqCD = request.getParameter('reqcd');
//grid 생성
var firstGrid = new ax5.ui.grid();
var secondGrid = new ax5.ui.grid();

$(document).ready(function(){
	if(strReqCD != null && strReqCD != ""){
		if(strReqCD.length > 2) strReqCD.substring(0, 2);
		else strReqCD = "";
	}
	setGrid();
	setGrid2();
	getMenuAllList();
});

function getMenuAllList(){
	var ajaxResultData = null;
	var tmpData = {
			requestType : 'getMenuAllList',
	}	
	ajaxResultData = ajaxCallWithJson('/webPage/test/LSH_testPage', tmpData, 'json');
	firstGrid.setData(ajaxResultData);
}

function Cmd_Ip_Click(temp){
	console.log(firstGrid);
	if(temp == '1'){
		for(var i = 0 ; i < firstGrid.selectedDataIndexs.length ; i++){
			var selectIndex = firstGrid.selectedDataIndexs[i];			
			// 중복데이터 추가안하는 로직
			if(firstGrid.list[selectIndex].colorsw != "3"){
				secondGrid.addRow($.extend({}, firstGrid.list[selectIndex], {__index: undefined}));
				firstGrid.list[selectIndex].colorsw = "3";	// 선택 데이터 컬럼색상 변경
			}
		}
		firstGrid.repaint();	// 선택 데이터 컬럼색상 변경하기 위한 그리드 새로 그리기
		firstGrid.clearSelect();
		secondGrid.clearSelect();
	} else if(temp == '2') {
		// 추가한 데이터 삭제시 firstGrid의 데이터 색상 돌려놓기위한 로직 시작
		for(var i = 0 ; i < secondGrid.selectedDataIndexs.length ; i++){
			var selectIndex2 = secondGrid.selectedDataIndexs[i];
			for(var z = 0; z < firstGrid.list.length ; z++){
				if(firstGrid.list[z].cm_maname == secondGrid.list[selectIndex2].cm_maname){
					firstGrid.list[z].colorsw = "";
				}
			}
		}
		// 추가한 데이터 삭제시 firstGrid의 데이터 색상 돌려놓기위한 로직 끝
		
		secondGrid.removeRow("selected"); // row 삭제
		firstGrid.repaint();	// 선택 데이터 컬럼색상 변경하기 위한 그리드 새로 그리기
		firstGrid.clearSelect();
		secondGrid.clearSelect();
	}
}	

function setGrid(){
	firstGrid.setConfig({
        target: $('[data-ax5grid="first-grid"]'),
        sortable: true, 
        multiSort: true,
        multipleSelect: true,
        showRowSelector: true,
        header: {
            align: "center",
            columnHeight: 30
        },
        body: {
            columnHeight: 28,
            onClick: function () {
            	this.self.select(this.dindex);
            },
        	onDataChanged: function(){
        		//그리드 새로고침 (스타일 유지)
        	    this.self.repaint();
        	},
        	trStyleClass: function () {
        		if (this.item.colorsw === '3'){
        			return "fontStyle-cncl";
        		} else {
        		}
        	}
        },
        columns: [
            {key: "cm_maname", label: "메뉴명",  width: '20%'},
            {key: "colorsw", label: "색상",  width: '10%'},
            {key: "cm_filename", label: "링크파일명",  width: '70%'} //formatter: function(){	return "<button>" + this.value + "</button>"}, 	 
        ]
    });
}

function setGrid2(){
	secondGrid.setConfig({
        target: $('[data-ax5grid="second-grid"]'),
        sortable: true, 
        multiSort: true,
        multipleSelect: true,
        showRowSelector: true,
        header: {
            align: "center",
            columnHeight: 30
        },
        body: {
            columnHeight: 28,
            onClick: function () {
            	this.self.select(this.dindex);
            },
        	onDataChanged: function(){
        		//그리드 새로고침 (스타일 유지)
        	    this.self.repaint();
        	}
        },
        columns: [
            {key: "cm_maname", label: "메뉴명",  width: '30%'},
            {key: "cm_filename", label: "링크파일명",  width: '70%'} //formatter: function(){	return "<button>" + this.value + "</button>"}, 	 
        ]
    });
}