var mask = new ax5.ui.mask();				//백그라운드 어둡게하는것
var modal = new ax5.ui.modal();				//모달창뛰우기위한것
var toast = new ax5.ui.toast();				//토스트사용
var confirmDialog = new ax5.ui.dialog();	//알럿,확인창
var confirmDialog2 = new ax5.ui.dialog();
var dialog = new ax5.ui.dialog({title: "알림창입니다."});	//알럿창
$(document).ready(function() {
	//config 설정은 꼭 ready함수에서 해주세요.
	toast.setConfig({
		containerPosition: "top-right",
		displayTime:10000
	});
	
	confirmDialog.setConfig({
        title: "확인창(버튼 커스텀)",
        theme: "info"
    });
	
	
    confirmDialog2.setConfig({
        title: "확인창(일반버전)",
        theme: "info"
    });
    
    
    var setting = {
        data: {
            simpleData: {
                enable: true,
            }
        }
    }
    
    
    var ajaxReturnData = null;
	var treeInfo = {
		treeInfoData: 	JSON.stringify(true),
		requestType: 	'GET_ZTREE_INFO'
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/modal/TreeOrganization', treeInfo, 'json');
	console.log(ajaxReturnData);
	if(ajaxReturnData !== 'ERR') {
		$.fn.zTree.init($("#treeDemo"), setting, ajaxReturnData);
	}
	
	
	
	
/*	_promise(false)
	.then(function(text){
		console.log(text);
	}, function(err) {
		console.error(err);
	});*/
	
	
	_promise(false)
		.then(function(text){
			window.alert(text);
		},function(err){
			window.alert(err);
		});
    
	
	
});


var _promise = function(flag){
	return new Promise(function(resoleve,reject){
		window.setTimeout(function(){
			if(flag){
				resoleve('해결됨!!');
			}else {
				reject(Error('실패!!'));
			}
		},1000);
	});
};


var modal2 = new ax5.ui.modal({
    /*테마
     * default 	: 회색
     * danger 	: 빨간색
     * primary 	: 청록색?
     * info 	: 연한 녹색?
     * success 	: 산뜻한 녹색
     * warning 	: 진달래색
     */
	theme: "warning",
    //모달창의 헤더부분입니다.
    header: {
        title: '<i class="fa fa-arrows" aria-hidden="true"></i> 이동가능모달',
        btns: {
            minimize: {
                label: '<i class="fa fa-minus-circle" aria-hidden="true"></i>', onClick: function(){
                    //modal2.minimize('top-right');
                	modal2.minimize('bottom-right');
                }
            },
            restore: {
                label: '<i class="fa fa-plus-circle" aria-hidden="true"></i>', onClick: function(){
                    modal2.restore();
                }
            },
            close: {
                label: '<i class="fa fa-times-circle" aria-hidden="true"></i>', onClick: function(){
                    modal2.close();
                }
            }
        }
    }
});

function openModal(){
    modal.open({
        width: 600,
        height: 440,
        iframe: {
            method: "get",
            url: "../modal/PopNotice.jsp",
            param: "callBack=modalCallBack"
        },
        onStateChanged: function () {
            // mask
            if (this.state === "open") {
                mask.open();
            }
            else if (this.state === "close") {
                mask.close();
            }
        }
    }, function () {
    });
    
}

function openModal2(){
	modal2.open({
        width: 600,
        height: 440,
        iframe: {
            method: "get",
            url: "../modal/PopNotice.jsp",
            param: "callBack=modalCallBack"
        },
        onStateChanged: function () {}
    }, function () {
    });
	
}

function openToast() {
	toast.push({
        theme: 'info',
        icon:  '<i class="fa fa-bell"></i>',
        msg:   '토스트창입니다.',
        closeIcon: '<i class="fa fa-times"></i>'
    });
}

function openAlert() {
	dialog.alert('결재완료되었습니다.', function () {
        // 알림 창 닫히면 이곳에서 기능 작성
		console.log(this);
    });
}

function openConfirm() {
	var confirmKey = null;
	var confrimStr = null;
	confirmDialog.confirm({
		msg: '확인메세지 ex)결재 하시겠습니까?',
		//버튼 여러개 생성가능
		btns: {
			//del이 key값이 됩니다.
			del: {
				label:'결재라인삭제', theme:'warning', onClick: function(key){
					confirmKey = key;
					console.log(key, this);
					confirmDialog.close();
				}
			},
			cancel: {
				label:'취소', onClick: function(key){
					confirmKey = key;
					console.log(key, this);
					confirmDialog.close();
				}
			},
			other: {
				label:'닫기', onClick: function(key){
					confirmKey = key;
					console.log(key, this);
					confirmDialog.close();
				}
			}
		}
	}, function(){
		// 이곳에서 확인 창 닫힌후의 기능 작성
		if(confirmKey === 'del') 	confrimStr = '결재라인 삭제를 클릭';
		if(confirmKey === 'cancel') confrimStr = '취소를 클릭';
		if(confirmKey === 'other') 	confrimStr = '닫기를 클릭';
		$('#confirmReturn').text(confrimStr);
	});
}

function openConfirm2() {
	confirmDialog2.confirm({
		msg: '확인메세지 ex)결재 하시겠습니까?',
	}, function(){
		// 이곳에서 확인 창 닫힌후의 기능 작성
		if(this.key === 'ok') {
			alert('ok');
		} else if ( this.key === 'cancel') {
			alert('cancel');
		}
	});
}

function loadingTest() {
	/*changeMouseCursor(true)
	.then(showToast('대용량 데이터 처리중입니다.'))
	.then(getBigData())
	.then(showToast('대용량 데이터 처리 완료 되었습니다.'))
	.then(changeMouseCursor)
	.then(console.log);*/
	
	/*.then(getBigData(true))
	.then(showToastMsg(false))
	.then(changeMouseCursor(false))
	.then(console.log);*/
	
	loading.then(getBigData);
};

var loading = new Promise(function(resolve,reject){
	
	//1. 토스트 메세지 띄울거야
	//2. 마우스 커서 변환 시킬거야
	
	showToast('대용량 데이터 처리 시작합니다.');
	$('html').css({'cursor':'wait'});
	$('body').css({'cursor':'wait'});
	
	resolve();
	
});


function showToast(msg){
	toast.push({
        theme: 'info',
        icon:  '<i class="fa fa-bell"></i>',
        msg:   msg,
        closeIcon: '<i class="fa fa-times"></i>'
    });
}

function getBigData() {
	var ajaxReturnData = null;
	var info = {
		requestType: 	'BIG_DATA_LOADING_TEST'
	}
	
	ajaxReturnData = ajaxCallWithJson('/webPage/mypage/Notice', info, 'json');
	if(ajaxReturnData !== 'ERR') {
		console.log('get success Big Data...');
	} 
	
	if(ajaxReturnData === 'ERR' ) console.log(Error('ajaxCall Error : ' + ajaxReturnData));
}

var changeMouseCursor = function(param) {
	return new Promise(function(changeCursor,removeCursor){
		if(param) {
			$('html').css({'cursor':'wait'});
			$('body').css({'cursor':'wait'});
			changeCursor('change Cursor wait....');
		} else {
			$('html').css({'cursor':'auto'});
			$('body').css({'cursor':'auto'});
			removeCursor('remove Cursor event...');
		}
	});
};

var _showToastMsg = function(param) {
	return new Promise(function(loadMsg,endMsg){
		if(param) {
			/*toast.push({
		        theme: 'info',
		        icon:  '<i class="fa fa-bell"></i>',
		        msg:   '대용량 데이터 처리중입니다.',
		        closeIcon: '<i class="fa fa-times"></i>'
		    });*/
			loadMsg('show Toast loading message...');
		} else {
			/*toast.push({
		        theme: 'info',
		        icon:  '<i class="fa fa-bell"></i>',
		        msg:   '대용량 데이터 처리완료.',
		        closeIcon: '<i class="fa fa-times"></i>'
		    });*/
			endMsg('show Toast end message...');
		}
		
	});
}

/*var _getBigData = function() {
	return new Promise(function(resolve,reject){
		var ajaxReturnData = null;
		var info = {
			requestType: 	'BIG_DATA_LOADING_TEST'
		}
		
		ajaxReturnData = ajaxCallWithJson('/webPage/mypage/Notice', info, 'json');
		if(ajaxReturnData !== 'ERR') {
			resolve('get success Big Data...');
		} 
		
		if(ajaxReturnData === 'ERR' ) reject(Error('ajaxCall Error : ' + ajaxReturnData));
	});
}
*/
	


var getLoadingTest = function() {
	return new Promise(function(resolve,reject){
		
		toast.push({
	        theme: 'info',
	        icon:  '<i class="fa fa-bell"></i>',
	        msg:   '대용량 데이터 처리중입니다.',
	        closeIcon: '<i class="fa fa-times"></i>'
	    });
		
		var ajaxReturnData = null;
		var info = {
			requestType: 	'BIG_DATA_LOADING_TEST'
		}
		
		ajaxReturnData = ajaxCallWithJson('/webPage/mypage/Notice', info, 'json');
		if(ajaxReturnData !== 'ERR') {
			resolve(function(){
				toast.push({
			        theme: 'info',
			        icon:  '<i class="fa fa-bell"></i>',
			        msg:   '대용량 데이터 처리완료.',
			        closeIcon: '<i class="fa fa-times"></i>'
			    });
				
				$('html').css({'cursor':'auto'});
				$('body').css({'cursor':'auto'});
			});
		} 
		
		if(ajaxReturnData === 'ERR' ) reject(Error('ajaxCall Error : ' + ajaxReturnData));
		
	});
}



