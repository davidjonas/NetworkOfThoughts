<?php
	function ERROR($text){
		echo('<form id="error_form" action="http://'.$_SERVER["HTTP_HOST"].'/default_error.php" method="POST" />');
		if($text != 'default')
			echo('<input type="hidden" id="text" name="text" value="'.$text.'" />');
		else
			echo('<input type="hidden" id="text" name="text" value="Ocorreu um erro ao processar a sua página, o site poderá estar a ser actualizado. <BR> Por favor tente mais tarde.<BR> Se o problema persistir, por favor contacte o administrador" />');
		echo('</form>');
		echo(' <script language="javascript">obj = document.getElementById("error_form"); obj.submit();</script>');		
	}
?>

