<?php
session_start();
if(!isset($_SESSION["SESSION"])) require ("session_init.php");
require_once("error_handling.php");
	
	
function connect()
{
	$connect = @mysql_connect($_SESSION['MYSQL_SERVER1'], $_SESSION['MYSQL_LOGIN1'], $_SESSION['MYSQL_PASS1'])
	or ERROR('Impossivel ligar á base de dados com '.$_SESSION['MYSQL_LOGIN1'].': ' . mysql_error()); 
	//or ERROR('default');
	$db = $_SESSION['MYSQL_DB1'];
	@mysql_select_db($db) 
	or ERROR('Impossivel selecionar a base de dados'.$db.': ' . mysql_error());
	//or ERROR('default');
}

function query($query)
{
	connect();
	
	$result = @mysql_query($query)
	or ERROR('Query failed: ' . mysql_error());
	//or ERROR('default');
	
	return $result;	
}
?>
