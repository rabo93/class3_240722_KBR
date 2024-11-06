$(document).ready(function(){
	// section01. 메인 비주얼 슬라이드
	$(".main-visual").slick({
		infinite : true, 
		autoplay: true,
		autoplaySpeed : 5000,
		dots: true,
		speed : 400,	
		arrows : true,
		prevArrow : $('.visu-prev'),		// 이전 화살표 모양 설정
		nextArrow : $('.visu-next'),		// 다음 화살표 모양 설정
		pauseOnHover : false,
		draggable : true
	});
});