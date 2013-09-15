<?php
require_once('Include/DAL.php');

class FunctionProvider
{	
	function getRandomThought($att)
	{
		assert (is_string($att));
		
		$allowed = false;
		
		if($att == "None")
			$allowed = true;
		
		if ($allowed)
		{
			$result = query("SELECT ID, message, user, UNIX_TIMESTAMP(timestamp) AS timestamp FROM ideas_text WHERE cool=1 ORDER BY RAND() LIMIT 1");
			$line = mysql_fetch_array($result, MYSQL_ASSOC);
			$message = array ('ID' => $line['ID'], 'message' => $line['message'], 'user' => $line['user'], 'timestamp' => $line['timestamp']);
			return json_encode($message);
		}
		else
		{
			return 'Argument check failed'; 
		}
	}
	
	function getNThoughts($att)
	{
		assert (is_int((int)$att));
		
		$final = array();
		
		$allowed = true;
		
		if ($allowed)
		{
			$result = query("SELECT ID, message, user, UNIX_TIMESTAMP(timestamp) AS timestamp FROM ideas_text WHERE cool=1 ORDER BY RAND() LIMIT ".$att);
			while ($line = mysql_fetch_array($result, MYSQL_ASSOC))
			{
				array_push($final, array('ID' => $line['ID'], 'message' => $line['message'], 'user' => $line['user'], 'timestamp' => $line['timestamp']));
			}
			return json_encode($final);
		}
		else
		{
			return 'Argument check failed\n'; 
		}
	}
	
	function addBrainstorm($attr)
	{
		assert (is_array($attr));
		
		$name = $attr[0];
		$user = $attr[1];
		
		//if ( !userExists($name) ) {0}
		if ($user != "null")
		{
			query("INSERT INTO ideas_brainstorm(name, user) VALUES('".addSlashes($name)."','".$user."')");
		}
		else
		{
			query("INSERT INTO ideas_brainstorm(name) VALUES('".addSlashes($name)."')");
		}
		
		$result = query("SELECT MAX(ID) AS newId FROM ideas_brainstorm WHERE name='".addSlashes($name)."'");
		$line = mysql_fetch_array($result, MYSQL_ASSOC);
		$newId = $line["newId"];
		
		return $newId;
	}
	
	function saveConnectionStringToBrainstorm($attr)
	{
		assert (is_array($attr));
		
		query("UPDATE ideas_brainstorm SET connectionString='".$attr[1]."' WHERE ID=".$attr[0]);
		
		return "true";
	}
	
	function getBrainstormConnectionString($att)
	{
		assert(is_int((int)$att));
		
		$result = query("SELECT connectionString FROM ideas_brainstorm WHERE ID=".$att);
		$line = mysql_fetch_array($result, MYSQL_ASSOC);
		$connectionString=$line["connectionString"];
		
		if($connectionString == "")
			return "NULL";
		else
			return $connectionString;
	}
	
	function addTextObject($attr)
	{
		assert (is_array($attr));
		
		$brainstorm = 0;
		if(isset($attr[2]) && is_int((int)$attr[2]))
		{
			$brainstorm = $attr[2];
		}
		
		if ($attr[1] == "null")
		{
			query("INSERT INTO ideas_text(message, timestamp) VALUES('".addSlashes($attr[0])."', NOW())");
		}
		else
		{
			//if ( !userExists($name) ) {0}
			query("INSERT INTO ideas_text(message, user, timestamp) VALUES('".addSlashes($attr[0])."', '".$attr[1]."', NOW())");
		}
		
		$result = query("SELECT MAX(ID) AS newId FROM ideas_text WHERE message='".addSlashes($attr[0])."'");
		$line = mysql_fetch_array($result, MYSQL_ASSOC);
		$newId = $line["newId"];
		
		if($brainstorm > 0)
		{		
			query("INSERT INTO ideas_text_brainstorm(idtext, idbrainstorm) VALUES ('".$newId."', '".$brainstorm."')");
		}
		
		return $newId;
	}
	
	function removeObjectFromBrainstorm($attr)
	{
		assert (is_array($attr) && is_int((int)$attr[0]) && is_int((int)$attr[1]));
		
		$brainstorm = $attr[1];
		
		if ($brainstorm > 0)
		{
			query("DELETE FROM ideas_text_brainstorm WHERE idtext = ".$attr[0]." AND idbrainstorm = ".$brainstorm);
			return "true";
		}
		
		return "false";
	}
	
	function permanentRemoveObject($attr)
	{
		assert (is_int((int)$attr));

		query("DELETE FROM ideas_text_brainstorm WHERE idtext=".$attr);
		query("DELETE FROM ideas_text WHERE ID = ".$attr);
		return "true";
	}
	
	function authenticate($attr)
	{
		assert (is_array($attr) && sizeof($attr) == 2);
		
		$result = query("SELECT user FROM ideas_users WHERE user = '".$attr[0]."' and pass = '".$attr[1]."'");
		if(mysql_num_rows($result) == 1)
		{
			return "true";	
		}
		else
		{
			return "false";	
		}
	}
	
	function getBrainstorm($att)
	{
		assert (is_int((int)$att));
		
		$final = array();
		
		$allowed = true;
		
		if ($allowed)
		{
			$result = query("SELECT ideas_text.ID, ideas_text.message, ideas_text.user, UNIX_TIMESTAMP(ideas_text.timestamp) AS timestamp FROM ideas_text WHERE ideas_text.ID IN (SELECT idtext FROM ideas_text_brainstorm WHERE idbrainstorm = ".$att.")");
			while ($line = mysql_fetch_array($result, MYSQL_ASSOC))
			{
				array_push($final, array('ID' => $line['ID'], 'message' => $line['message'], 'user' => $line['user'], 'timestamp' => $line['timestamp']));
			}
			return json_encode($final);
		}
		else
		{
			return 'Argument check failed\n'; 
		}
	}
	
	function addExistingObjectToBrainstorm($att)
	{
		assert (is_array($att) && sizeof($att) == 2 && is_int($att[0]) && is_int($att[1]));
		
		$brainstorm = $att[1];
		$object = $att[0];
		
		//TODO: Should be testing if the object and the braistorm exist
		if($brainstorm > 0 && $object > 0)
		{		
			query("INSERT INTO ideas_text_brainstorm(idtext, idbrainstorm) VALUES ('".$object."', '".$brainstorm."')");
			return "true";	
		}
		else
		{
			return "false";	
		}
		
	}
	
	function getUserTextObjects($attr)
	{
		assert (is_string($attr));
		
		$user = $attr;
		
		$final = array();
		
		$allowed = true;
		
		if ($allowed)
		{
			$result = query("SELECT ID, message, user, UNIX_TIMESTAMP(timestamp) AS timestamp FROM ideas_text WHERE user = '".$user."'");
			while ($line = mysql_fetch_array($result, MYSQL_ASSOC))
			{
				array_push($final, array('ID' => $line['ID'], 'message' => $line['message'], 'user' => $line['user'], 'timestamp' => $line['timestamp']));
			}
			return json_encode($final);
		}
		else
		{
			return 'Argument check failed\n'; 
		}
	}
	
	function getCurrentServerTime($att)
	{
		assert (is_string($att));
		
		$allowed = false;
		
		if($att == "None")
			$allowed = true;
		
		if ($allowed)
		{
			$current = time ();
			return $current;
		}
		else
		{
			return 'Argument check failed\n'; 
		}
	}
	
	function getNewTextObjectsSince($att)
	{	
		$date =  date("Y-n-j H:i:s", $att);
		$allowed = true;
		$final = array();
		//echo($date);
		if ($allowed)
		{
			$result = query("SELECT ID, message, user, UNIX_TIMESTAMP(timestamp) AS timestamp FROM ideas_text WHERE timestamp > '".$date."' ORDER BY timestamp");
			while ($line = mysql_fetch_array($result, MYSQL_ASSOC))
			{
				array_push($final, array('ID' => $line['ID'], 'message' => $line['message'], 'user' => $line['user'], 'timestamp' => $line['timestamp']));
			}
			return json_encode($final);
		}
		else
		{
			return 'Argument check failed\n'; 
		}
	}
	
	function search($att)
	{
		assert (is_string($att));
	
		$search = $att;
		
		$search = $this->strip_punctuation( $search );
		
		$querystr = "SELECT ID, message, user, UNIX_TIMESTAMP(timestamp) AS timestamp FROM ideas_text WHERE MATCH (message) AGAINST ('".addslashes($search)."')";
		
		$final = array(); 
		
		$allowed = true;
		
		if ($allowed)
		{
			$result = query($querystr);
			while ($line = mysql_fetch_array($result, MYSQL_ASSOC))
			{
				array_push($final, array( 'ID' => $line['ID'], 'message' => $line['message'], 'user' => $line['user'], 'timestamp' => $line['timestamp']));
			}
			return json_encode($final);
		}
		else
		{
			return 'Argument check failed\n'; 
		}
	}
	
	
	/**
	 * Get a users first brainstorm or create one and return it; (Public)
	 */
	function getUsersFirstBrainstorm($attr)
	{
		assert (is_string($attr));
		
		$user = $attr;
		
		$final = array();
		
		$allowed = true;
		
		$userBS = $this->getBrainstormsFromUser( $user );
		
		if (count($userBS) == 0)
		{
			//Add brainstorm
			$newId = $this->addBrainstorm(array($user."FirstBrainstorm", $user));
			$returnJSON = "[". '{"ID":"'.$newId.'","name":"'.$user."FirstBrainstorm".'"}'. "," .substr($this->getBrainstorm($newId), 1);
			return str_replace(",]", "]", $returnJSON);
		}
		else
		{
			//return first brainstorm
			$returnJSON = "[". json_encode($userBS[0]). "," .substr($this->getBrainstorm($userBS[0]['ID']), 1);
			return str_replace(",]", "]", $returnJSON);
		}
	}
	
	
	/**
	 * Strip punctuation from text. (Private)
	 */
	function strip_punctuation( $text )
	{
		$urlbrackets    = '\[\]\(\)';
		$urlspacebefore = ':;\'_\*%@&?!' . $urlbrackets;
		$urlspaceafter  = '\.,:;\'\-_\*@&\/\\\\\?!#' . $urlbrackets;
		$urlall         = '\.,:;\'\-_\*%@&\/\\\\\?!#' . $urlbrackets;
	 
		$specialquotes  = '\'"\*<>';
	 
		$fullstop       = '\x{002E}\x{FE52}\x{FF0E}';
		$comma          = '\x{002C}\x{FE50}\x{FF0C}';
		$arabsep        = '\x{066B}\x{066C}';
		$numseparators  = $fullstop . $comma . $arabsep;
	 
		$numbersign     = '\x{0023}\x{FE5F}\x{FF03}';
		$percent        = '\x{066A}\x{0025}\x{066A}\x{FE6A}\x{FF05}\x{2030}\x{2031}';
		$prime          = '\x{2032}\x{2033}\x{2034}\x{2057}';
		$nummodifiers   = $numbersign . $percent . $prime;
	 
		return preg_replace(
			array(
				'/[\p{Z}\p{Cc}\p{Cf}\p{Cs}\p{Pi}\p{Pf}]/u',
				'/\p{Po}(?<![' . $specialquotes .
					$numseparators . $urlall . $nummodifiers . '])/u',
				'/[\p{Ps}\p{Pe}](?<![' . $urlbrackets . '])/u',
				'/[' . $specialquotes . $numseparators . $urlspaceafter .
					'\p{Pd}\p{Pc}]+((?= )|$)/u',
				'/((?<= )|^)[' . $specialquotes . $urlspacebefore . '\p{Pc}]+/u',
				'/((?<= )|^)\p{Pd}+(?![\p{N}\p{Sc}])/u',
				'/ +/',
			),
			' ',
			$text );
	}
	
	/**
	 * Returns an array with all the brainstorms from user (Private)
	 */
	
	function getBrainstormsFromUser( $username )
	{
		assert(is_string($username) && $username != "");
		$final=array();
		
		$result = query("SELECT ID, name FROM ideas_brainstorm WHERE user = '".$username."'");
		while ($line = mysql_fetch_array($result, MYSQL_ASSOC))
		{
			array_push($final, $line);
		}
		
		return $final;
	}
	
	/**
	 * Create a new user
	 **/
	function createUser($attr)
	{
		assert (is_array($attr) && sizeof($attr) == 2 && is_string($attr[0]) && is_string($attr[1]));
		$user = addslashes($attr[0]);
		$pass = addslashes($attr[1]);
		$email = "";
		
		$result = query("SELECT * FROM ideas_users WHERE user = '".$user."'");
		
		if(mysql_num_rows($result) > 0)
		{
			$returnValue = "false";
		}
		else
		{
			query("INSERT INTO ideas_users(user, pass, email) VALUES('".$user."', '".$pass."', '".$email."')");
			$returnValue = "true";
		}
		
		return $returnValue;
	}
}
?>
