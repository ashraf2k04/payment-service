package com.ashraf.payment.service;

import com.ashraf.payment.iso.IsoDocument;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class Iso20022Service {

    public IsoDocument parse(String xml) {
        try {
            JAXBContext context = JAXBContext.newInstance(IsoDocument.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (IsoDocument) unmarshaller.unmarshal(new StringReader(xml));
        } catch (Exception e) {
            throw new RuntimeException("Invalid ISO 20022 message");
        }
    }
}