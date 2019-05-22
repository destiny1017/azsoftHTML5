/**
 * eCAMS 공통 기능 정의
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문 +++
 * 	버전 : 1.1
 *  수정일 : 2019-02-07
 */


/*
 * SBUxAlert에 필요한 Option객체를 미리 정의해놨음.
 * js 파일에서 사용시  
 * 
 * 1-1.var alertObj = new Alert('this is title', 'this is text', 'info Or light' ,  ...... );
 * 1-2.SBUxMethod.openAlert(alertObj);
 * 
 * 2. SBUxMethod.openAlert(new Alert('this is title', 'this is text', 'info Or light'));
 * 
 * 위 두가지 방법중 편한방법 사용.
 * 앞에 파라미터 3개는 필수입니다. 
 */
/*
 * 객체 대입시 참조하는 주소가 같기 때문에 객체안의 값이 같이 변경 됩니다.
 * 주소참조 없이 복사할 때 사용하세요.
 * ex)  copyArr = arr; 
 * 		 ===>  copyArr = copyReferenceNone(arr);
 * 
 */


var toast 			= new ax5.ui.toast();
var dialog 			= new ax5.ui.dialog({title: "확인"});
var confirmDialog = new ax5.ui.dialog();	//알럿,확인창

toast.setConfig({
	containerPosition: "top-right",
	displayTime:10000
});

confirmDialog.setConfig({
    title: "선택창",
    theme: "info"
});

function copyReferenceNone(copyArray){
	return JSON.parse(JSON.stringify(copyArray));  // 
}


function ajaxCallWithJson(url, requestData, dataType) {
	var successData = null;
	$.ajax({
		type 	: 'POST',
		url 	: url,
		data 	: requestData,
		dataType: dataType,
		async 	: false,
		/**
		 * async 안쓰는게 좋다고 함
		 * [Deprecation] Synchronous XMLHttpRequest on the main thread is deprecated because of its detrimental effects to the end user's experience. 
		 * For more help, check https://xhr.spec.whatwg.org/.
		 * 메인 쓰레드에서의 동기화된 XMLHttpRequest는 사용자 경험에 안좋은 영향을 미치기 때문에 더이상 사용하지 않습니다. 더 자세한 사항은 http://xhr.spec.whatwg.org/  를 참고해 주십시오.
		 */
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



function ajaxAsync(url, requestData, dataType,successFunction) {
	var ajax = $.ajax({
		type 	: 'POST',
		url 	: url,
		data 	: requestData,
		dataType: dataType,
		async 	: true
	}).then(successFunction,defaultErrorFunction);
}

function defaultErrorFunction(err) {
	console.log('============================ajax 통신중 error 발생============================');
	console.log('============================Error message START============================');
	console.log(err);
	console.log('============================Error message END============================');
}

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


function CodeInfo(MACODE, SelMsg, closeYn) {
	this.MACODE 	= MACODE;
	this.SelMsg 	= SelMsg;
	this.closeYn 	= closeYn;
};


/* 코드정보 가져오기 공통 함수 정의.
 * 한번에 여러개 혹은 하나의 코드정보를 가져옵니다.
 * 
 * EX)
 * var codeInfos = getCodeInfoCommon( [new CodeInfo('CATTYPE','ALL','N'),		>> CodeInfo 객체를    배열 형태로 파라미터 전달
										new CodeInfo('QRYGBN','ALL','N')] );
	
	cboCatTypeData 	= codeInfos.CATTYPE;	>> 리턴받은 DATA에서 MACODE값으로 해당 값을 불러서 사용하면 됩니다.
	cboQryGbnData 	= codeInfos.QRYGBN;
	SBUxMethod.refresh('cboCatType');
	SBUxMethod.refresh('cboQryGbn');
 */
function getCodeInfoCommon(codeInfoArr) {
	var returnCodeInfo = {};
	var ajaxReturnData = null;
	var codeInfo = {};
	var divisionMacode = '';
	codeInfo = {
		codeInfoData: 	JSON.stringify(codeInfoArr),
		requestType: 	'CODE_INFO'
	};
	ajaxReturnData = ajaxCallWithJson('/webPage/common/CommonCodeInfo', codeInfo, 'json');
	if(ajaxReturnData !== 'ERR') {
		return ajaxReturnData;
	} else {
		return null;
	}
}


/* 현재 날짜를 기준으로 
 *  dateSeparator = ['MON' , 'DATE', 'LASTDATE' , 'FIRSTDATE'] 중 하나 선택사용 
 *  increaseDecreaseNumber = 양수 혹은 음수
 *  
 *  EX)
 *  SBUxMethod.set('datStD',getDate('DATE',-1));  	>> 현재 날짜에서 하루전날짜
 *  SBUxMethod.set('datStD',getDate('DATE',0));		>> 현재 날짜
 *  SBUxMethod.set('datStD',getDate('MON',-1));		>> 한달 전
 *  SBUxMethod.set('datStD',getDate('MON',1)); 		>> 한달 후
 *  SBUxMethod.set('datStD',getDate('LASTDATE',0));	>> 현재달의 마지막 날짜
 *  SBUxMethod.set('datStD',getDate('FIRSTDATE',0));>> 현재달의 첫 날짜		 		
 * 
 */
function getDate(dateSeparator, increaseDecreaseNumber) {
	
	var calcuDate = new Date( new Date().getFullYear() 
						, new Date().getMonth().length === 1 ?  '0'+ new Date().getMonth() : new Date().getMonth()
						, new Date().getDate().length === 1 ? '0'+new Date().getDate() : new Date().getDate() );
	
	if(dateSeparator === 'MON'){
		if(calcuDate.getMonth() === '0' && increaseDecreaseNumber === -1){
			calcuDate.setFullYear(calcuDate.getFullYear() - 1);
			calcuDate.setMonth(11);
		}else if(calcuDate.getMonth() === '11' && increaseDecreaseNumber === 1){
			calcuDate.setFullYear(calcuDate.getFullYear() + 1);
            calcuDate.setMonth(0);
		}else {
        	calcuDate.setMonth(calcuDate.getMonth() + increaseDecreaseNumber);
        }
	}
	if(dateSeparator === 'DATE') calcuDate.setDate(calcuDate.getDate() + increaseDecreaseNumber);
	if(dateSeparator === 'LASTDATE') calcuDate.setDate(0);
	if(dateSeparator === 'FIRSTDATE') calcuDate.setDate(1);
	
	return changeDateToYYYYMMDD(calcuDate);
}

/*
 * Javascript Date형식을 YYYYMMDD의 String으로 형 변환
 */
function changeDateToYYYYMMDD(date){
	
	var year 	= date.getFullYear();
	var month 	= (date.getMonth() + 1) <  10 ? '0' + (date.getMonth() + 1) : (date.getMonth() + 1);
	var date 	= date.getDate() <  10 ? '0' + date.getDate() : date.getDate();
	
	return year+month+date;
}

// StringReplaceAll
// ex) replaceAllString($("#dateStD").val(), "/", "");
function replaceAllString(source, find, replacement){
	return source.split( find ).join( replacement );
}



// window load 되기 전까지 마우스 커서 wait 로 나타나게 처리
$('html').css({'cursor':'wait'});
$('body').css({'cursor':'wait'});
$(window).on('load',function(){
	$('html').css({'cursor':'auto'});
	$('body').css({'cursor':'auto'});
	
});


/*
 * promise 입니다.
 * 데이터의 처리 순서를 비동기로 처리해야 할시 사용하세요.
 */
var _promise = function(ms,action){
	return new Promise(function(resolve,reject){
		setTimeout(function(){
			resolve(action);
		},ms)
	});
}

function beForAndAfterDataLoading(beForAndAfter,msg){
	if(beForAndAfter === 'BEFORE'){
		$('html').css({'cursor':'wait'});
		$('body').css({'cursor':'wait'});
		showToast(msg);
	}
	
	if(beForAndAfter === 'AFTER'){
		$('html').css({'cursor':'auto'});
		$('body').css({'cursor':'auto'});
		showToast(msg);
	}
	
}

function showToast(msg) {
	toast.push({
        theme: 'info',
        icon:  '<i class="fa fa-bell"></i>',
        msg:   msg,
        closeIcon: '<i class="fa fa-times"></i>',
        displayTime : 100
    });
}

function defaultPickerInfo(dataAx5picker) {
	return {
		target: $('[data-ax5picker="'+dataAx5picker+'"]'),
		direction: "bottom",
		content: {
			width: 220,
			margin: 10,
			type: 'date',
			config: {
				control: {
					left: '<i class="fa fa-chevron-left"></i>',
					yearTmpl: '%s',
					monthTmpl: '%s',
					right: '<i class="fa fa-chevron-right"></i>'
				},
				dateFormat: 'yyyy/MM/dd',
				lang: {
					yearTmpl: "%s년",
					months: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
					dayTmpl: "%s"
				},dimensions: {
					height: 140,
					width : 75,
					colHeadHeight: 11,
					controlHeight: 25,
				}
			},
			formatter: {
				pattern: 'date'
			}
		},
		btns: {
			today: {
				label: "Today", onClick: function () {
					var today = new Date();
					this.self
					.setContentValue(this.item.id, 0, ax5.util.date(today, {"return": "yyyy/MM/dd"}))
					.setContentValue(this.item.id, 1, ax5.util.date(today, {"return": "yyyy/MM/dd"}))
					.close();
				}
			},
			thisMonth: {
				label: "This Month", onClick: function () {
					var today = new Date();
					this.self
					.setContentValue(this.item.id, 0, ax5.util.date(today, {"return": "yyyy/MM/01"}))
					.setContentValue(this.item.id, 1, ax5.util.date(today, {"return": "yyyy/MM"})
							+ '/'
							+ ax5.util.daysOfMonth(today.getFullYear(), today.getMonth()))
							.close();
				}
			},
			ok: {label: "Close", theme: "default"}
		}
		
	};
}

