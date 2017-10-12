package machool.dikman.net;

import ca.canadapost.ws.ncshipment_v4.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;


public class CreateNCShipmentRequestTest {
    @BeforeMethod
    public void setUp() throws Exception {
    }

    @AfterMethod
    public void tearDown() throws Exception {
    }

    @Test
    public void testSaveLabel() throws Exception {
        CreateNCShipmentRequest.Params params = new CreateNCShipmentRequest.Params();
        params.apiUsername = "3fef045f175f8fa3";
        params.apiPassword = "01457d1244a5498ccb4e6f";
        params.customerNo = "0008627679";
        params.shipment = createTestNCShipment();

        CreateNCShipmentRequest request = new CreateNCShipmentRequest(params);
        NonContractShipmentInfo info = request.send();

        request.requestAndSaveShippingLabel("test.pdf");

    }

    private static NonContractShipment createTestNCShipment(){
        DomesticAddressDetailsType senderAddress = new DomesticAddressDetailsType();
        senderAddress.setAddressLine1("3838 Albert St");
        senderAddress.setCity("Burnaby");
        senderAddress.setProvState("BC");
        senderAddress.setPostalZipCode("V5C2C9");

        SenderType sender = new SenderType();
        sender.setName("Igor");
        sender.setCompany("Igor Dykman");
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
        destination.setCompany("Machool");
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
        NonContractShipment result = new NonContractShipment();
        result.setRequestedShippingPoint("K1K1K1");
        result.setDeliverySpec(deliverySpec);
        return result;
    }

}