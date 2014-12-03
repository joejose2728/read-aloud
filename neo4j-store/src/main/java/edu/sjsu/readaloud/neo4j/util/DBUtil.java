package edu.sjsu.readaloud.neo4j.util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import edu.sjsu.readaloud.neo4j.intf.Constants;

public class DBUtil implements Constants{

	public static boolean isDatabaseRunning()
    {
        WebResource resource = Client.create().resource( SERVER_ROOT_URI );
        ClientResponse response = resource.get(ClientResponse.class );
        int status = response.getStatus();
        response.close();
        return status == 200;
    }
	
	
}
