package machool.dikman.net;

import ca.canadapost.ws.messages.Messages;
import ca.canadapost.ws.ncshipment_v4.NonContractShipment;
import ca.canadapost.ws.ncshipment_v4.NonContractShipmentInfo;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class CreateNCShipmentRequest {
    static class Params{
        public String apiUsername;
        public String apiPassword;
        public String customerNo;
        public NonContractShipment shipment;
    }

    // DEV URL
    private static final String URL = "https://ct.soa-gw.canadapost.ca/rs/";
    private static final String METHOD_NAME = "/ncshipment";
    private Client apiClient;
    private Params params;


    private String formUrl(){
        return URL + params.customerNo + METHOD_NAME;
    }

    public CreateNCShipmentRequest(Params params) {
        this.params = params;
        ClientConfig config = new DefaultClientConfig();
        apiClient = Client.create(config);
        apiClient.addFilter(
                new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(
                        params.apiUsername, params.apiPassword));
    }


    public NonContractShipmentInfo send() throws UniformInterfaceException {
        WebResource aWebResource = apiClient.resource(formUrl());
        return parseResponse(
                aWebResource.accept("application/vnd.cpc.ncshipment-v4+xml")
                .header("Content-Type", "application/vnd.cpc.ncshipment-v4+xml")
                .acceptLanguage("en-CA").post(ClientResponse.class, (Object)params.shipment)
        );

    }

    private NonContractShipmentInfo parseResponse(ClientResponse resp){
        Object result;
        try {
            JAXBContext jc = JAXBContext.newInstance(NonContractShipmentInfo.class, Messages.class);
            result = jc.createUnmarshaller().unmarshal(resp.getEntityInputStream());

        }catch (JAXBException e){
            System.out.println(e);
            return null;
        }

        if( result instanceof NonContractShipmentInfo)
            return (NonContractShipmentInfo) result;
        else{
            for(Messages.Message message : ((Messages) result).getMessages()){
                System.out.println("Error Code: " + message.getCode() + " Msg: " + message.getDescription());
            }
            return null;
        }

    }

}
