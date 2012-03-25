<H2>Confirmação de registo</H2>

<SCRIPT>
 $(document).ready(function() {			
		jQuery.ajax( { 
		   url:"/Saskia/user?do=confirmregister&lg=<?php echo $_GET['lg'];?>&a=<?php echo $_GET['a'];?>",
		   success: function (response) {
			    document.write(response)
			},
			error: function () {
				 document.write('Error ocurred. Sorry.')
			}
		});
  })
</SCRIPT>