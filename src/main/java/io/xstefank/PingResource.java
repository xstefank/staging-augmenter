package io.xstefank;

import io.xstefank.jaxb.NamespaceFilter;
import io.xstefank.model.Profile;
import io.xstefank.model.Repository;
import io.xstefank.model.Server;
import io.xstefank.model.Settings;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

@Path("/ping")
public class PingResource {


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws JAXBException, FileNotFoundException, SAXException, ParserConfigurationException {

        File file = new File("../src/main/resources/META-INF/resources/test-settings.xml");
        File outFile = new File("../src/main/resources/META-INF/resources/test-settings-out.xml");

        JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //Create an XMLReader to use with our filter
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        XMLReader reader = parser.getXMLReader();

        //Create the filter (to add namespace) and set the xmlReader as its parent.
        NamespaceFilter inFilter = new NamespaceFilter(null, false);
        inFilter.setParent(reader);

        InputSource is = new InputSource(new FileInputStream(file));

        SAXSource source = new SAXSource(inFilter, is);

        Settings readSettings = (Settings) jaxbUnmarshaller.unmarshal(source);
        System.out.println(readSettings);

        // add custom profile
        Profile mpStagingProfile = new Profile();
        mpStagingProfile.id = "mp-staging";
        mpStagingProfile.repositories = new ArrayList<>();

        Repository staging1111 = new Repository();
        staging1111.id = "id-1111";
        staging1111.url = "https://test.url";

        mpStagingProfile.repositories.add(staging1111);
        readSettings.profiles.add(mpStagingProfile);

        System.out.println("=====================");
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(readSettings, System.out);
        marshaller.marshal(readSettings, outFile);

        return "Hello RESTEasy";
    }

}
