package machool.dikman.net;

import ca.canadapost.ws.messages.Messages;
import ca.canadapost.ws.ncshipment_v4.Link;
import ca.canadapost.ws.ncshipment_v4.NonContractShipment;
import ca.canadapost.ws.ncshipment_v4.NonContractShipmentInfo;
import ca.canadapost.ws.ncshipment_v4.RelType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.commons.io.FileUtils;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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




    public CreateNCShipmentRequest(Params params) {
        this.params = params;
        ClientConfig config = new DefaultClientConfig();
        apiClient = Client.create(config);
        apiClient.addFilter(
                new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(
                        params.apiUsername, params.apiPassword));
    }



    public NonContractShipmentInfo send() throws UniformInterfaceException {
        return parseResponse(
                apiClient.resource(formUrl()).accept("application/vnd.cpc.ncshipment-v4+xml")
                .header("Content-Type", "application/vnd.cpc.ncshipment-v4+xml")
                .acceptLanguage("en-CA").post(ClientResponse.class, (Object)params.shipment)
        );

    }

    private String formUrl(){
        return URL + params.customerNo + METHOD_NAME;
    }



    private NonContractShipmentInfo parseResponse(ClientResponse resp){
        Object result = null;
        if(validateResponse(resp))
        {
            try {
                JAXBContext jc = JAXBContext.newInstance(NonContractShipmentInfo.class, Messages.class);
                result = jc.createUnmarshaller().unmarshal(resp.getEntityInputStream());
                if (result instanceof NonContractShipmentInfo)
                    return (NonContractShipmentInfo) result;
                //Processing error messages
                if (result instanceof Messages) {
                    for (Messages.Message message : ((Messages) result).getMessages()) {
                        log("Error Code: " + message.getCode() + " Msg: " + message.getDescription());
                    }
                }
            } catch (JAXBException e) {
                log(e.toString());
            }
        }
        return null;
    }

    private boolean validateResponse(ClientResponse resp){
        if( resp.getStatus() != Response.Status.OK.getStatusCode()) {
            log("HTTP response is NOT OK: " + resp.getStatus());
            return false;
        }
        return true;
    }

    public void requestAndSaveShippingLabel(String fileName) {
        Link link = findFirstLabelLink(send());
        ClientResponse resp = apiClient.resource(link.getHref()).accept(link.getMediaType())
                .acceptLanguage("en-CA").get(ClientResponse.class);
        if (validateResponse(resp)) {
            InputStream is = resp.getEntityInputStream();
            //checking media type
            if (resp.getType().toString().contains("pdf")) {
                try {
                    FileUtils.copyInputStreamToFile(is, new File(fileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                log("Unknown media type: " + resp.getType().toString());
            }
        }
    }



    private Link findFirstLabelLink(NonContractShipmentInfo info){
        for(Link link : info.getLinks().getLinks()){
            if(link.getRel() == RelType.LABEL)
                return link;
        }
        return null;
    }

    private void log(String arg){
        System.out.println(arg);
    }

}
