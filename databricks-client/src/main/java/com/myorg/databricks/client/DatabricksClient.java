package com.myorg.databricks.client;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import javax.ws.rs.core.Response;

public class DatabricksClient {
    public DatabricksSession Session;

    public DatabricksClient(DatabricksSession session) {
        Session = session;
    }

    protected ClientConfig ClientConfig() {
        return new ClientConfig().register(new JacksonFeature());
    }

    protected void checkResponse(Response response) throws HttpException {
        //This will print the entire response body; useful for debugging code
        //String debugBody = response.readEntity(String.class);
        //System.out.println(debugBody);

        // check response status code
        if (response.getStatus() == 400) {
            String body = response.readEntity(String.class);
            throw new HttpException("HTTP 400 Bad Request: " + body);
        } else if (response.getStatus() == 401) {
            throw new HttpException("HTTP 401 Unauthorized: Not Authenticated");
        } else if(response.getStatus() == 403) {
            throw new HttpException("HTTP 403 Forbidden: Not Authorized");
        } else if (response.getStatus() != 200) {
            String body = response.readEntity(String.class);
            throw new HttpException("HTTP "+ response.getStatus() + ":" + body);
        }
    }

}
