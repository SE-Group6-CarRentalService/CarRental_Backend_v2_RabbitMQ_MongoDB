package at.fhcampuswien.carrental.carrentalservice.services;

import com.squareup.okhttp.*;
import org.springframework.web.client.RestTemplate;


import java.io.IOException;


public class  CurrencyConverter {

    public static double convertCurrency(String currency, double value){

        int newvalue = (int)value;

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("text/xml; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType,
                "<s11:Envelope xmlns:s11='http://schemas.xmlsoap.org/soap/envelope/'>\n" +
                        "  <s11:Body>\n" +
                        "    <ns1:convertCurrency xmlns:ns1='django.soap.currencyconverter'>\n" +
                        "<!-- optional -->\n" +
                        "<!-- This element may be left empty if xsi:nil='true' is set. -->\n" +
                        "      <ns1:startcurr>USD</ns1:startcurr>\n" +
                        "<!-- optional -->\n" +
                        "      <ns1:quantity>"+newvalue+"</ns1:quantity>\n" +
                        "<!-- optional -->\n" +
                        "<!-- This element may be left empty if xsi:nil='true' is set. -->\n" +
                        "      <ns1:endcurr>"+currency+"</ns1:endcurr>\n" +
                        "    </ns1:convertCurrency>\n" +
                        "  </s11:Body>\n" +
                        "</s11:Envelope>"
        );
        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/SOAPservice/soap_service/")
                .method("POST", body)
                .addHeader("Content-Type", "text/xml; charset=utf-8")
                .addHeader("SOAPAction", "convertCurrency")
                .build();

        Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String stringResponse = null;

        try {
            stringResponse = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        stringResponse = stringResponse.substring(stringResponse.indexOf("<tns:convertCurrencyResult>"),stringResponse.indexOf("</tns:convertCurrencyResult>")).replaceFirst("<tns:convertCurrencyResult>", "");

        double result = Double.valueOf(stringResponse);

        return result;
    }
}
