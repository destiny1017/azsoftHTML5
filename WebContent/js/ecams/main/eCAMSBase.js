/**
 * 메인화면 기능 정의
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-01-29
 */
var userId 	= null;
var userName= null;
var adminYN = null;
var request = new Request();
var sessionID = null;

$(document).ready(function() {
	screenInit();
	$('#side-menu').click(clickSideMenu);
});

function screenInit() {
	if( sessionID === null ) sessionID =$('#txtSessionID').val();
	getSession();
	
}

function getSession() {
	var ajaxUserData = null;
	var sessionInfo = {
		requestType : 'GETSESSIONUSERDATA',
		sessionID 	: sessionID
	}   
	ajaxUserData = ajaxCallWithJson('/webPage/main/eCAMSBaseServlet', sessionInfo, 'json');
	userName= ajaxUserData.userName;
	userId 	= ajaxUserData.userId;
	adminYN = ajaxUserData.adminYN;
	console.log('userid:'+userId);
	console.log('userName:'+userName);
	console.log('adminYN:'+adminYN);
	//menu_set();
	meneSet();
}

function meneSet() {
	var ajaxUserData = null;
	
	var userInfo = {
		requestType : 'MenuList',
		UserId 	: userId
	}   
	ajaxUserData = ajaxCallWithJson('/webPage/main/eCAMSBaseServlet', userInfo, 'json');
	
	var menuHtmlStr = '';
	ajaxUserData.forEach(function(menuItem, menuItemIndex) {
		if(menuItem.link === undefined || menuItem.link === null) {
			if(menuHtmlStr.length > 1) {
				menuHtmlStr += '</ul>\n';
				menuHtmlStr += '</li>\n';
			}
			menuHtmlStr += '<li>\n';
			menuHtmlStr += '<a href="doneMove"><span class="nav-label">'+menuItem.text+'</span><span class="fa arrow"></span> </a>\n';
			menuHtmlStr += '<ul class="nav nav-second-level">\n';
		}else if(menuItem.link !== null) {
			menuHtmlStr += '<li><a href="'+menuItem.link+'">'+menuItem.text+'</a></li>\n';
		}
		
		if((menuItemIndex+1) === ajaxUserData.length) {
			menuHtmlStr += '</ul>\n';
			menuHtmlStr += '</li>\n';
		}
	});
	
	$('#side-menu').html(menuHtmlStr);
	$('#side-menu').metisMenu();
}

function clickSideMenu(event) {
	event.preventDefault();
	
	// 사이드메뉴 선택된 영역 a태크 아닐시 반환
	if( event.target.pathname === undefined) return;
	
	var $iFrm = '';
	var pathName = event.target.pathname;
	var parentMenuName = '';
	
	// 하위 메뉴일시만 이동
	if( pathName.indexOf('doneMove') < 0) {
		//IFRAME 지워준후 다시그리기
		$('#eCAMSFrame').empty();
		$iFrm = $('<IFRAME id="iFrm" frameBorder="0" name="iFrm" scrolling="no" src="'+event.target.href+'" style=" width:100%; height: 92vh"></IFRAME>');
		$iFrm.appendTo('#eCAMSFrame');
		
		//상위 TITLE TEXT SET
		parentMenuName = $(event.target).closest('ul').closest('li').children('a')[0].innerText;
		$('#ecamsTitleText').html('['+parentMenuName+'] '+event.target.innerText);
	}
	
}