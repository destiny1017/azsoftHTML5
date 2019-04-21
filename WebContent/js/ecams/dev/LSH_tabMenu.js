// tab 메뉴 만들기 주소 : http://jsfiddle.net/3n74v/
var urlArr = [];
$(document).ready(function(){
	setTabMenu();
});

function setTabMenu(){
	$(".tab_content").hide();
	$(".tab_content:first").show();
	$("ul.tabs li").click(function () {
		$("ul.tabs li").removeClass("active").css("color", "#333");
		$(this).addClass("active").css("color", "darkred");
		$(".tab_content").hide();
		
		var activeTab = $(this).attr("rel");
		
		// 페이지를 처음 불러올때 미리 불러오면 셀 width 깨짐 현상이 있어 클릭후 처움 ifram 을 새로 불러오도록 수정
		// 수정 후 첫 페이지 load 시에 fadeIn이 매끄럽지 않아 추후 수정이 필요함
		if(urlArr[$(this).index()] == null && $(this).index() > 0){
			urlArr[$(this).index()] = $("#" + activeTab).children("iframe");
			$("#" + activeTab).children("iframe").attr("src",$("#" + activeTab).children("iframe").attr("src"));
			
			$("#" + activeTab).children("iframe").on('load',function(){
				$("#" + activeTab).fadeIn();
			});
			return;
		}
		$("#" + activeTab).fadeIn();
	});
}