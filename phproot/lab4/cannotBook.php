<?php
	require_once('database.inc.php');
	
	session_start();
	$db = $_SESSION['db'];
	$userId = $_SESSION['userId'];
	$db->openConnection();
	$resnbr = $db->lastResNbr();
	
	
	
	$db->closeConnection();
?>

<html>
<head><title>Booking 4</title><head>
<body><h1>Booking 4</h1>
	Current user: <?php print $userId ?>
	<p>
	Could not book ticket, please try again.
	<p>

	
	<form method=post action="index.html">
	<input type=submit value="Try again">
	</form>
</body>
</html>
