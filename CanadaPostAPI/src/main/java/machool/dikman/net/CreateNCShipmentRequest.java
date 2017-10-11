package machool.dikman.net;

import ca.canadapost.ws.ncshipment_v4.*;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import java.math.BigDecimal;

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


    public ClientResponse send() throws UniformInterfaceException {
        WebResource aWebResource = apiClient.resource(formUrl());
        return aWebResource.accept("application/vnd.cpc.ncshipment-v4+xml")
                .header("Content-Type", "application/vnd.cpc.ncshipment-v4+xml")
                .acceptLanguage("en-CA").post(ClientResponse.class, (Object)params.shipment);
    }

    public static void main(String [] args){

        CreateNCShipmentRequest.Params params = new Params();
        params.apiUsername = "3fef045f175f8fa3";
        params.apiPassword = "01457d1244a5498ccb4e6f";
        params.customerNo = "0008627679";
        params.shipment = new NonContractShipment();
        DomesticAddressDetailsType senderAddress = new DomesticAddressDetailsType();
        senderAddress.setAddressLine1("3838 Albert St");
        senderAddress.setCity("Burnaby");
        senderAddress.setProvState("BC");
        senderAddress.setPostalZipCode("V5C2C9");

        SenderType sender = new SenderType();
        sender.setName("Igor");
        sender.setCompany("private");
        sender.setContactPhone("1 (234) 567 8910");
        sender.setAddressDetails(senderAddress);

        DestinationAddressDetailsType destinationAddress = new DestinationAddressDetailsType();
        destinationAddress.setAddressLine1("110 - 328 W Hastings St.");
        destinationAddress.setCity("Vancouver");
        destinationAddress.setProvState("BC");
        destinationAddress.setCountryCode("CA");
        destinationAddress.setPostalZipCode("V6B 1K6");

        DestinationType destination = new DestinationType();
        destination.setName("Vincent Tellier");
        destination.setCompany("Machool.");
        destination.setAddressDetails(destinationAddress);

        OptionType option1 = new OptionType();
        option1.setOptionCode("DC");
        DeliverySpecType.Options options = new DeliverySpecType.Options();
        options.getOptions().add(option1);

        ParcelCharacteristicsType.Dimensions parcelDimension = new ParcelCharacteristicsType.Dimensions();
        parcelDimension.setHeight(new BigDecimal(6));
        parcelDimension.setLength(new BigDecimal(12));
        parcelDimension.setWidth(new BigDecimal(9));

        ParcelCharacteristicsType characteristics = new ParcelCharacteristicsType();
        characteristics.setWeight(new BigDecimal(5));
        characteristics.setDimensions(parcelDimension);

        NotificationType notification = new NotificationType();;
        notification.setEmail("machool@dikman.net");
        notification.setOnDelivery(true);
        notification.setOnException(false);
        notification.setOnShipment(true);

        PreferencesType preferences = new PreferencesType();
        preferences.setShowInsuredValue(true);
        preferences.setShowPackingInstructions(false);
        preferences.setShowPostageRate(true);

        ReferencesType references = new ReferencesType();
        references.setCostCentre("costCentre");
        references.setCustomerRef1("custRef1");
        references.setCustomerRef2("custRef2");

        DeliverySpecType deliverySpec = new DeliverySpecType();
        deliverySpec.setServiceCode("DOM.EP");
        deliverySpec.setSender(sender);
        deliverySpec.setDestination(destination);
        deliverySpec.setOptions(options);
        deliverySpec.setParcelCharacteristics(characteristics);
        deliverySpec.setNotification(notification);
        deliverySpec.setPreferences(preferences);
        deliverySpec.setReferences(references);

        params.shipment.setRequestedShippingPoint("K1K1K1");
        params.shipment.setDeliverySpec(deliverySpec);

        CreateNCShipmentRequest request = new CreateNCShipmentRequest(params);
        ClientResponse response = request.send();

        System.out.println("test");
    }
}
