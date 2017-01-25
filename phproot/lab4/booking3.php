<?php
	require_once('database.inc.php');
	
	session_start();
	$db = $_SESSION['db'];
	$userId = $_SESSION['userId'];
	
	$movieDate = $_POST['datum'];
	$_SESSION['movieDate'] = $movieDate;
	$db->openConnection();
	
	$theaterName = $db->getTeather($_SESSION['movieName'], $_SESSION['movieDate']);
	$_SESSION['theater'] = $theaterName;
	$movieName = $_SESSION['movieName'];
	$freeseats = $db->getfreeSeats($_SESSION['movieName'], $_SESSION['movieDate'], $_SESSION['theater']);
	
	$result = $db->bookTicket($movieName, $movieDate, $userId, $theaterName);
	
	if (!($result==0)) {
	header("Location: booking4.php");
	exit();
	}
	else {
	header("Location: cannotbook.php");
	}	
	
	$db->closeConnection();
?>

<html>
<head><title>Booking 3</title><head>
<body><h1>Booking 3</h1>
	Current user: <?php print $userId ?>
	<p>
	Movie details:
	<p>
	
	<b>Movie name:</b> <?php print $_SESSION['moviename'] ?>
	<br />
	<b>Date:</b> <?php print $_SESSION['showdate'] ?>
	<br />
	<b>Theater: </b><?php print $_SESSION['theatername'] ?>

	<br />
	<b>Available seats: </b><?php print ($freeseats) ?>
	<br />

	

	
	<form method=post action="bookticket.php">
	<input type=submit value="Book ticket">
	</form>
</body>
</html>
