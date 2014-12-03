package edu.sjsu.readaloud.neo4j.util;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import edu.sjsu.readaloud.neo4j.intf.Constants;

public class Neo4JUtil implements Constants{

	public static URI createNode() throws URISyntaxException
	{
		String nodeEntryPointUri = SERVER_ROOT_URI + "node";
		return execute(0,nodeEntryPointUri, "{}");
	}

	public static URI createRelationship(URI startNode, URI endNode,
			String relationshipType, String jsonAttributes )
	{
		String fromUri = startNode.toString() + "/relationships";
		String relationshipJson = generateJsonRelationship( endNode,
				relationshipType, jsonAttributes );

		return execute(0,fromUri, relationshipJson);
	}

	public static void addLabel(URI node, String label) throws URISyntaxException
	{
		String labelUri = node.toString() + "/labels";
		execute(0, labelUri, "\"" + label + "\"");
	}
	
	public static void addProperty( URI nodeUri, String propertyName,
			String propertyValue )
	{
		String propertyUri = nodeUri.toString() + "/properties/" + propertyName;
		execute(1, propertyUri, "\"" + propertyValue + "\"");
	}

	public static void sendTransactionalCypherQuery(String query) {
		final String txUri = SERVER_ROOT_URI + "transaction/commit";
		String payload = "{\"statements\" : [ {\"statement\" : \"" +query + "\"} ]}";
		execute(0,txUri,payload);
	}

	private static URI execute(int method, String nodeUri, String payload){
		WebResource resource = Client.create()
				.resource( nodeUri );
		// POST JSON to the relationships URI
		ClientResponse response = null;
		Builder req = resource.accept( MediaType.APPLICATION_JSON )
				.type( MediaType.APPLICATION_JSON )
				.entity( payload );

		switch (method){
			case 0: response = req.post(ClientResponse.class);//POST
				break;
			case 1: response = req.put(ClientResponse.class);//PUT
				break;
		}
		final URI location = response.getLocation();
		log(method, nodeUri, response, 
				method == 1 && response.getStatus() != 204 ? response.getEntity(String.class) : 
					(response.getStatus() == 201 ? location.toString() : ""));
		return location;
	}

	private static String generateJsonRelationship( URI endNode,
			String relationshipType, String... jsonAttributes )
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "{ \"to\" : \"" );
		sb.append( endNode.toString() );
		sb.append( "\", " );

		sb.append( "\"type\" : \"" );
		sb.append( relationshipType );
		if ( jsonAttributes == null || jsonAttributes.length < 1 )
		{
			sb.append( "\"" );
		}
		else
		{
			sb.append( "\", \"data\" : " );
			for ( int i = 0; i < jsonAttributes.length; i++ )
			{
				sb.append( jsonAttributes[i] );
				if ( i < jsonAttributes.length - 1 )
				{ // Miss off the final comma
					sb.append( ", " );
				}
			}
		}

		sb.append( " }" );
		return sb.toString();
	}

	private static void log(int method, String nodeUri, ClientResponse response, String location){
		String methodStr = method == 0? "POST" : "PUT";
		System.out.println( String.format(
				"[%s] to [%s], status code [%d], location header [%s]",
				methodStr, nodeUri, response.getStatus(), location));
	}
}
