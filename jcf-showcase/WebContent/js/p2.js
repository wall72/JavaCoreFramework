!function ($) {
	var contextPath="/jcf-showcase/";

	$(function(){
		$('#p21_1_btn1').click(function(e){
			alert("소스를 받아 Local에서 실행하시길 바랍니다.");
        	e.preventDefault();
        	/*
        	 * 위에 2줄은 주석 처리하시고, 아래 주석은 해제하신뒤 실행하시면 됩니다.
        	 */
			//document.getElementById("p21_1_Form").submit();
		});

		$('#p21_2_btn1').click(function(e){
			alert("소스를 받아 Local에서 실행하시길 바랍니다.");
        	e.preventDefault();
        	/*
        	 * 위에 2줄은 주석 처리하시고, 아래 주석은 해제하신뒤 실행하시면 됩니다.
        	 */
        	/*
			e.preventDefault();

            var content = $('#p21_2_param1').val();

            var sendData = JSON.stringify({DS_IN : {param : content}});

            $.ajax({
            	dataType : "json"
              , contentType : "application/json+sua; charset=UTF-8"
              , type : "POST"
              , url : contextPath +"sample/file/afterUploadAction.do"
              , data : sendData
              , success : function(reponse, status, request){
            	  $('#p21_2_result').html(reponse.success);
              	}
              , error : function(request, status, error){
            	  console.log("통신실패");
              }
           });
           */
		});
	});

}(window.jQuery);

function getIframeData() {
	var successData="<p>파일이 업로드 되었습니다.<br/>자세한 내용은 로그을 확인하세요.</p>";
	$('#p21_iframe').html(successData);
}