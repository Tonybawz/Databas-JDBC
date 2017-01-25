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
	Ticket Booked.
	Reservation number is <?php print $resnbr ?>
	<p>
	
	<form method=post action="booking1.php">
	<input type=submit value="Book another">
	</form>

</body>
</html>
