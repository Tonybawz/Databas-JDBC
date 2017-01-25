<?php
	require_once('database.inc.php');
	
	session_start();
	$db = $_SESSION['db'];
	$userId = $_SESSION['userId'];
	
	$movieDate = $_POST['datum'];
	$_SESSION['movieDate'] = $movieDate;
	$db->openConnection();
	
	$teatherName = $db->getTeather($_SESSION['movieName'], $_SESSION['movieDate']);
	$_SESSION['theater'] = $teatherName;
	$freeseats = $db->getfreeSeats($_SESSION['movieName'], $_SESSION['movieDate'], $_SESSION['theater']);
	
	
	
	$db->closeConnection();
?>

<html>
<head><title>Booking 3</title><head>
<body><h1>Booking 3</h1>
	Current user: <?php print $userId ?>
	<p>
	Movie details:
	<p>
	
	<b>Movie name:</b> <?php print $_SESSION['movieName'] ?>
	<br />
	<b>Date:</b> <?php print $_SESSION['movieDate'] ?>
	<br />
	<b>Theater: </b><?php print $_SESSION['theater'] ?>

	<br />
	<b>Available seats: </b><?php print ($freeseats) ?>
	<br />

	

	
	<form method=post action="bookticket.php">
	<input type=submit value="Book ticket">
	</form>
</body>
</html>
