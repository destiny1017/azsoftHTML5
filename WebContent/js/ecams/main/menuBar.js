/**
 * 메뉴바 기능 정의
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-01-29
 */
var menuJson = null;
var parentMenuText = null;
var childMenuText = null;
function menu_set(){   // 메뉴리스트
	var ajaxMenuData = null;
	var userInfo = {
		requestType : 'MenuList',
		UserId 		: userId
	}   
	ajaxMenuData = ajaxCallWithJson('/webPage/main/eCAMSBaseServlet', userInfo, 'json');
	menuJson = ajaxMenuData;
	SBUxMethod.refresh('menu_json');
}

function menuBarClick(event) { 
	console.dir(event.target);
	if(event.target.className.indexOf('top-item') >= 0 ){
		if(event.target.innerText === '') parentMenuText = event.target.parentNode.innerText;
		else parentMenuText = event.target.innerText;
	}
	
	if(event.target.className.indexOf('sub-item') >= 0 ){
		childMenuText = event.target.innerText;
		$('#titleText').html('[' + parentMenuText + ']' + childMenuText);
	}
}