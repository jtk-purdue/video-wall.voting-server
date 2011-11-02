function fire(url, gpi_index)
{
	method="POST";
	//url="http://vw-creator:8009/maxidrivers/maxisoftgpi/fire?gpi=" + gpi_index;
	async = false;
	send = '<?xml version="1.0" encoding="ISO-8859-1"?><status></status>';

	try
	{
		var httpRequest = new XMLHttpRequest();
		httpRequest.open(method, url+gpi_index, async);
		httpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		httpRequest.send(send);
	}
	catch (e)
	{
		return("Error: " + url + "\n" + e);
	}
	
	return("Success");
}