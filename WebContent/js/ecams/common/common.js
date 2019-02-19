/**
 * eCAMS 공통 기능 정의
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문 + 정선희
 * 	버전 : 1.1
 *  수정일 : 2019-02-07
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
function Alert(title, text, mode, time, image, is_modal, 
				is_fixed, placement, after_open, before_open, 
				before_close, show_only_one, is_fixed_close_button)  {
	this.title = title;		// 알럿의 타이틀
	this.text  = text;		// 알럿 내 문구
	this.mode  = mode;		// 알럿의 색상 모드 [info or light]
	
	this.time  = (time === undefined ? '10000000' : time);		//alert 창이 지속되는 시간( '1000' = 1초)
	this.image = (image === undefined ? null: image);			// 알럿좌측의 이미지 주소
	this.is_modal= (is_modal === undefined ? false: is_modal);	// 모달창 효과 [true or false]
	this.is_fixed= (is_fixed === undefined ? false: is_fixed); 	// 고정 여부 [true or false]
	this.placement 	=	 (placement === undefined ? 'top-right': placement); 	//위치 [top-right, top-left] 
	
	this.after_open =  defaultCallback;				//alert 창이 나타난 후에 callback 함수 호출
	this.before_open = defaultCallback;				//alert 창이 나타나기 전에 callback 함수 호출
	this.after_close = defaultCallback;				//alert 창이 닫힌 후에 callbakc 함수 호출
	this.before_close= defaultCallback;				//alert 창이 닫히기 전에 callback 함수 호출
	this.show_only_one 			= (show_only_one === undefined ? false: show_only_one);							//alert 창을 1개만 띄울지 여부[true,false (default) ]
	this.is_fixed_close_button 	= (is_fixed_close_button === undefined ? false: timeis_fixed_close_button);		//닫힘 버튼 고정여부 [true or false]
	
};

/*
 * alert 기본 콜백함수 아무것도 정의되어있지않습니다.
 */
function defaultCallback() {};


/*
 * 그리드 사용시 기본 스타일의 그리드 이용시 컬럼 객체 정의
 * SBGridProperties.columns = [
		new GridDefaultColumn('프로그램경로', 	'cm_dirpath', 	'500px', 'output'),
		new GridDefaultColumn('프로그램명', 	'cr_rsrcname', 	'150px', 'output'),
		new GridDefaultColumn('프로그램종류', 	'jawon', 		'100px', 'output','text-align:center'),
		new GridDefaultColumn('프로그램설명', 	'cr_story', 	'200px', 'output'),
		new GridDefaultColumn('상태', 		'codename', 	'80px',  'output','text-align:center'),
		new GridDefaultColumn('버전', 		'cr_lstver', 	'80px',  'output','text-align:center'),
		new GridDefaultColumn('수정자', 		'cm_username', 	'90px',  'output'),
		new GridDefaultColumn('수정일', 		'lastdt', 		'120px', 'output','text-align:center')
	];
 */
function GridDefaultColumn(caption, ref, width, type, style) {
	this.caption = [caption];
	this.ref = ref;
	this.width = width;
	this.type = type;
	this.style = (style === undefined ? null: style);	
};



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
	codeInfoArr.forEach(function( codeInfoItem, codeInfoItemIndex) {
		codeInfo = {
			codeInfoData: 	JSON.stringify(codeInfoItem),
			requestType: 	'CODE_INFO'
		};
		ajaxReturnData = ajaxCallWithJson('/webPage/common/CommonCodeInfo', codeInfo, 'json');
		
		if(ajaxReturnData !== 'ERR') {
			returnCodeInfo[codeInfoItem.MACODE] = ajaxReturnData;
		}
	});
	return returnCodeInfo;
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


