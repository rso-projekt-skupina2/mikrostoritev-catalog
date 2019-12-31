$(document).ready(function() {
	$( "#imageID1" ).focusout(function() {
		$("#viewForm").attr("action", "http://159.65.210.43/image-catalog/v1/images/" + $("#imageID1" ).val()); //Will set it
	})
	
	$('#rateIframe').contents().find('body').html("");
	
	$("#upload").on("submit", upload_image);
	
	$("#rate").on("submit", rate_image)
	

	$( "#userId2" ).focusout(function() {
		$("#recommendForm").attr("action", "http://159.65.210.43/recomender/v1/rating/collaborative/" + $("#userId2" ).val()); 
	})
	
	function upload_image(event){

		event = event || window.event;

		// Prevent the default form action i.e. loading of a new page
		if(event.preventDefault){ // W3C Variant
			event.preventDefault();
		}
		else{ // IE < 9
			event.returnValue = false;
		}
		
		var file_data = $('#file').prop('files')[0];
		var form_data = new FormData();
		form_data.append('file', file_data);
		$.ajax({
			url: 'http://159.65.210.43/image-catalog/v1/images/upload2', // point to server-side controller method
			dataType: 'text', // what to expect back from the server
			cache: false,
			enctype: 'multipart/form-data',
			contentType: false,
			processData: false,
			data: form_data,
			type: 'post',
			success: function (response) {
				console.log("succes"); // display success response from the server
			},
			error: function (response) {
				console.log("fail"); // display error response from the server
			}
		});
	}

			
	function rate_image(event){
		var myIFrame = document.getElementById('rateIframe');
		myIFrame.src="javascript:'"+"<body> </body>"+"'";
		event = event || window.event;

		// Prevent the default form action i.e. loading of a new page
		if(event.preventDefault){ // W3C Variant
			event.preventDefault();
		}
		else{ // IE < 9
			event.returnValue = false;
		}
		
		var image = $('#imageId').val();
		var user = $('#userId').val();
		var rating = $('#rating').val();
		
		var uspesno="<body>Rating uspešno dodan</body>";
		var neuspesno="<body>Napaka pri dodajanju ratinga</body>";
		$.ajax({
			url: 'http://159.65.210.43/recomender/v1/rating/rate?imageId='+ image +'&userId='+ user +'&rating='+rating, // point to server-side controller method
			dataType: 'application/json', // what to expect back from the server
			crossDomain: true,
			headers: {"Access-Control-Allow-Origin": "*"},
			type: 'post',
			success: function (response) {
				if(response.status == 200){
					myIFrame.src="javascript:'"+uspesno+"'";
				} else {
					myIFrame.src="javascript:'"+neuspesno+"'";
				}
			},
			error: function (response) {
				if(response.status == 200){
					myIFrame.src="javascript:'"+uspesno+"'";
				} else {
					myIFrame.src="javascript:'"+neuspesno+"'";
				}
			}
		});

	}
});
