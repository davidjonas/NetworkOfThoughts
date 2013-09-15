<?php
header("Content-Type: text/plain");
require_once("FunctionProvider.php");

$provider = new FunctionProvider();
$allowed_functions = array('getNThoughts',
			   'getRandomThought',
			   'authenticate',
			   'addTextObject',
			   'getBrainstorm',
			   'addBrainstorm',
			   'getUserTextObjects',
			   'getCurrentServerTime',
			   'getNewTextObjectsSince',
			   'search',
			   'getUsersFirstBrainstorm',
			   'addExistingObjectToBrainstorm',
			   'createUser',
			   'getBrainstormConnectionString',
			   'saveConnectionStringToBrainstorm',
			   'permanentRemoveObject',
			   'removeObjectFromBrainstorm');
$allowed = false;

$function = $_REQUEST['func'];
$attr = $_REQUEST['attr'];

foreach($allowed_functions as $key => $value)
{
	if($function == $value)
	{
		$allowed = true;
		break;
	}
}

if($allowed)
{
	echo $provider->$function($attr);
}
else
{
	echo 'function not allowed';
}
?>