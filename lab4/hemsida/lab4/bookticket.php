<?php
	require_once('database.inc.php');
	
	session_start();
	$db = $_SESSION['db'];
	$userId = $_SESSION['userId'];
	$movieName = $_SESSION['movieName'];
	$movieDate = $_SESSION['movieDate'];
	$theater = $_SESSION['theater'];
	
	$db->openConnection();
	
	
	$result = $db->bookTicket($movieName, $movieDate, $userId, $theater);
	
	$db->closeConnection();
	
	
	
	
	if (!($result==0)) {
	header("Location: booking4.php");
	exit();
	}
	else {
	header("Location: cannotBook.php");
	}	
?>
