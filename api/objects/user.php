<?php
class User{
    // database connection and table name
    private $conn;
    private $table_name = "USERS";
    // object properties
    public $USER_NAME;
    public $USER_ID;
    public $DATE_CREATED;
    // constructor with $db as database connection
    public function __construct($db){
        $this->conn = $db;
    }
    // read users
    function read(){
        // select all query
        $query = "SELECT USER_ID,USER_NAME,DATE_CREATED FROM USERS";
        // prepare query statement
        $stmt = $this->conn->prepare($query);
        // execute query
        $stmt->execute();
        return $stmt;
    }
    // create USER
    function create(){
        // query to insert record
        $query = "INSERT INTO
                    " . $this->table_name . "
                SET
                    USER_NAME=:USER_NAME, DATE_CREATED=:DATE_CREATED";
        // prepare query
        $stmt = $this->conn->prepare($query);
        // sanitize
        $this->USER_NAME=htmlspecialchars(strip_tags($this->USER_NAME));
        $this->DATE_CREATED=htmlspecialchars(strip_tags($this->DATE_CREATED));
        // bind values
        $stmt->bindParam(":USER_NAME", $this->USER_NAME);
        $stmt->bindParam(":DATE_CREATED", $this->DATE_CREATED);
        // execute query
        if($stmt->execute()){
            return true;
        }
        return false; 
    }
}
