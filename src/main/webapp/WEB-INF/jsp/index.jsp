<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="baseURL"
	   value="${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, pageContext.request.contextPath)}"/>
<html>
<head>
	<title></title>

	<link href="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.7.3/fullcalendar.min.css" rel="stylesheet">

	<script src="//code.jquery.com/jquery-2.2.4.min.js"></script>
	<script src="//cdn.jsdelivr.net/momentjs/2.13.0/moment.min.js"></script>
	<script src="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.7.3/fullcalendar.min.js"></script>

	<style>

		.container {
			width: 680px;
			margin: 0 auto;
		}

	</style>
</head>
<body>

<div class="container">

	<select id="team">
		<c:forEach items="${team}" var="emp">
			<option value="${emp.id}">${emp.name}</option>
		</c:forEach>
	</select>

	<div id="calendar"></div>

</div>


<script>
	$(function () {
		var team = $('#team');
		var events = undefined;
		team.change(function () {
			console.log('Requesting events for user: ' + team.val());
		});
		$('#calendar').fullCalendar({
			events: '${baseURL}/roster/cal/'+team.val()
		});

	});


</script>


</body>
</html>
