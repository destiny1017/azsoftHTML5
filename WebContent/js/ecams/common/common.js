/**
 * eCAMS 공통 기능 정의
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-01-29
 */
var SBUxConfig = {
	License : "SBUX-G12XD-131XD",				
	Path : "/lib/sbux",
	Locale : "ko",
	SupportVersion : "2.5",
	DeveloperTipType : "console",
	Theme : "default",
	DefaultSetFile : "SBUxDefault.js",
	SBGrid : {
		Theme : 'default',
		DefaultSetFile : "SBGridDefault.js",
		Version2_5 : true
	}
};
var SBpath = "/";

/*
 * 객체 대입시 참조하는 주소가 같기 때문에 객체안의 값이 같이 변경 됩니다.
 * 주소참조 없이 복사할 때 사용하세요.
 * ex)  copyArr = arr; 
 * 		 ===>  copyArr = copyReferenceNone(arr);
 * 
 */
function copyReferenceNone(copyArray){
	return JSON.parse(JSON.stringify(copyArray));  // 
}


function ajaxCallWithJson(url, requestData, dataType) {
	console.log('common ajax call');
	console.log(requestData);
	
	var successData = null;
	$.ajax({
		type 	: 'POST',
		url 	: url,
		data 	: requestData,
		dataType: dataType,
		async 	: false, 
		success : function(data) {
			successData =  copyReferenceNone(data);
		},
		error 	: function(req, stat, error) {
			console.log(error);
		}
	});
	
	if(successData != null) return successData;
	else return 'ERR';
};

function Request(){
	var requestParam ="";
    this.getParameter = function(param){
    	var url = unescape(location.href); //현재 주소를 decoding
        var paramArr = (url.substring(url.indexOf("?")+1,url.length)).split("&"); //파라미터만 자르고, 다시 &그분자를 잘라서 배열에 넣는다. 

        

        for(var i = 0 ; i < paramArr.length ; i++){
            var temp = paramArr[i].split("="); //파라미터 변수명을 담음

            if(temp[0].toUpperCase() == param.toUpperCase()){
            	requestParam = paramArr[i].split("=")[1]; // 변수명과 일치할 경우 데이터 삽입
                break;
            }

        }
        return requestParam;
    };
}






