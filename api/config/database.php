<?php
class Database{
 
    // specify your own database credentials
    private $host = "localhost";
    private $db_name = "stohre";
    private $username = "root";
    private $password = "123KSDG#!@$5267FEW";
    public $conn;
 
    // get the database connection
    public function getConnection(){
 
        $this->conn = null;
 
        try{
            $this->conn = new PDO("mysql:host=" . $this->host . ";dbname=" . $this->db_name, $this->username, $this->password);
            $this->conn->exec("set names utf8");
        }catch(PDOException $exception){
            echo "Connection error: " . $exception->getMessage();
        }
 
        return $this->conn;
    }
}
/*
class Database { 
    public $serverName = 'localhost'; 
    public $username = 'root';
    public $password = '123KSDG#!@$5267FEW'; 
    public $dbName = 'stohre';
    public $conn = '';
    
    function connectToDb() {
        $conn = new mysqli($this->serverName,$this->username,$this->password,$this->dbName);
		if ($conn->connect_error) {
			//echo 'failure';
		    die("Connection failed: " . $conn->connect_error);
		}
		else {
			//echo 'success';
		}
		return $conn;
    }
} 
$databaseConnection = new Database();
$conn = $databaseConnection->connectToDb();

$sql = "SELECT
U.USER_NAME,
U.USER_ID,
UG.GROUP_ID,
S.STORY_NAME,
S.STORY_TEXT
FROM STORIES S
JOIN USER_GROUPS UG ON S.GROUP_ID = UG.GROUP_ID
JOIN USERS U ON UG.USER_ID = U.USER_ID
WHERE 1=1
AND S.GROUP_ID =2";

$result = $conn->query($sql);
$rows = array();
while($r = mysqli_fetch_assoc($result)) {
    $rows[] = $r;
}
echo json_encode($rows);
$conn->close();
*/
?>
