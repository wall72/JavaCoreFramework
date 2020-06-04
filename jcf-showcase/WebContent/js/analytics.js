/**
 * Google Analytics script. Local 로 돌릴때는 주석처리 할것.
 */
var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-1390032-4']);
_gaq.push(['_trackPageview']);

(function() {
	var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();