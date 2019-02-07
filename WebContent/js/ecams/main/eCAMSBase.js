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

$(document).ready(function() {
	screenInit();
});

function screenInit() {
	getSession();
	$('#titleText').html("형상관리 MAIN");
}

function getSession() {
	var ajaxUserData = null;
	var sessionInfo = {
		requestType : 'GETSESSIONUSERDATA',
		sessionID 	: request.getParameter('sessionID')
	}   
	ajaxUserData = ajaxCallWithJson('/webPage/main/eCAMSBaseServlet', sessionInfo, 'json');
	userName= ajaxUserData.userName;
	userId 	= ajaxUserData.userId;
	adminYN = ajaxUserData.adminYN;
	console.log('userid:'+userId);
	console.log('userName:'+userName);
	console.log('adminYN:'+adminYN);
	menu_set();
}
