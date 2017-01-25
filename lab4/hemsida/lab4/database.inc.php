<?php
/*
 * Class Database: interface to the movie database from PHP.
 *
 * You must:
 *
 * 1) Change the function userExists so the SQL query is appropriate for your tables.
 * 2) Write more functions.
 *
 */
class Database {
	private $host;
	private $userName;
	private $password;
	private $database;
	private $conn;
	
	/**
	 * Constructs a database object for the specified user.
	 */
	public function __construct($host, $userName, $password, $database) {
		$this->host = $host;
		$this->userName = $userName;
		$this->password = $password;
		$this->database = $database;
	}
	
	/** 
	 * Opens a connection to the database, using the earlier specified user
	 * name and password.
	 *
	 * @return true if the connection succeeded, false if the connection 
	 * couldn't be opened or the supplied user name and password were not 
	 * recognized.
	 */
	public function openConnection() {
		try {
			$this->conn = new PDO("mysql:host=$this->host;dbname=$this->database", 
					$this->userName,  $this->password);
			$this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		} catch (PDOException $e) {
			$error = "Connection error: " . $e->getMessage();
			print $error . "<p>";
			unset($this->conn);
			return false;
		}
		return true;
	}
	
	/**
	 * Closes the connection to the database.
	 */
	public function closeConnection() {
		$this->conn = null;
		unset($this->conn);
	}

	/**
	 * Checks if the connection to the database has been established.
	 *
	 * @return true if the connection has been established
	 */
	public function isConnected() {
		return isset($this->conn);
	}
	
	/**
	 * Execute a database query (select).
	 *
	 * @param $query The query string (SQL), with ? placeholders for parameters
	 * @param $param Array with parameters 
	 * @return The result set
	 */
	private function executeQuery($query, $param = null) {
		try {
			$stmt = $this->conn->prepare($query);
			$stmt->execute($param);
			// Here have i put in some arguments in the fetchAll()
			$result = $stmt->fetchAll();
		} catch (PDOException $e) {
			$error = "*** Internal error: " . $e->getMessage() . "<p>" . $query;
			die($error);
		}
		return $result;
	}
	
	/**
	 * Execute a database update (insert/delete/update).
	 *
	 * @param $query The query string (SQL), with ? placeholders for parameters
	 * @param $param Array with parameters 
	 * @return The number of affected rows
	 */
		
	private function executeUpdate($query, $param = null) {
		try {
			$stmt = $this->conn->prepare($query);
			$stmt->execute($param);
		} catch (PDOException $e) {
			$error = "*** Internal error: " . $e->getMessage() . "<p>" . $query;
			die($error);
		}
		return $stmt->rowCount();
	}
	
	/**
	 * Check if a user with the specified user id exists in the database.
	 * Queries the Users database table.
	 *
	 * @param userId The user id 
	 * @return true if the user exists, false otherwise.
	 */
	public function userExists($userId) {
		$sql = "select username from users where username = ?";
		$result = $this->executeQuery($sql, array($userId));
		return count($result) == 1; 
	}

	/*
	 * *** Add functions ***
	 */
	 

	public function getMovieNames() {
                $sql = "select * from Performance group by moviename";
                $result = $this->executeQuery($sql,null);
                $returnthis = array();

                foreach($result as $b) {
                                array_push($returnthis,$b['movieName']);
                }
                return $returnthis;
         }
         
         
	public function getDates($movieName) {
		$sql = "select * from Performance where moviename = ?";
		$result = $this->executeQuery($sql, array($movieName));
		                $returnthis = array();

                foreach($result as $b) {
                                array_push($returnthis,$b['datum']);
                }
                return $returnthis;
        }
		
                
                public function getTeather($movieName, $movieDate){
                $sql = "select theaterName from performance where datum = ? and movieName = ?";
                $arrayres = array($movieDate, $movieName);
               $result = $this->executeQuery($sql, $arrayres);
               
               $temptoSend = $result[0];
               $toSend = $temptoSend[0];
               return $toSend;
                }
		

          public function getFreeSeats($movieName, $movieDate, $theaterName){
			$sql1 = "select nbrofseats from theater where theaterName = ?";
			$totalSeats = $this->executeQuery($sql1, array($theaterName));
			$sql2 = "Select count(*) as antal from ticket where movieName = ? and datum = ?";
			$bookedSeats = $this->executeQuery($sql2, array($movieName, $movieDate));
			$result = $totalSeats[0][0] - $bookedSeats[0][0];
			return $result;
		}
      
		
	public function lastResNbr() {
		
		$sql = "select max(resnbr) as reservation from ticket";
               $result = $this->executeQuery($sql);
               return $result[0][0];
        		}

        public function bookTicket($movieName, $movieDate, $userId, $theater) {
        $this->conn->beginTransaction();
        
        $freeSeats = $this->getFreeSeats($movieName, $movieDate, $theater);	
        if ($freeSeats > 0) {
        	$sql = "insert into ticket values(0,?,?,?)";
        	$result = $this->executeUpdate($sql, array($userId, $movieDate, $movieName));
        	if ($result == 1) {
        		$this->conn->commit();
        		return 1;
        	}
        	$this->conn->rollback();
        	return 0;
        }
        else {
        	$this->conn->rollback();
        	return 0;
        
        }
        

	}
	

}			
?>
