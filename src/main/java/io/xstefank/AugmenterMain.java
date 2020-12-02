package io.xstefank;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.xstefank.cli.Entry;
import io.xstefank.jaxb.NamespaceFilter;
import io.xstefank.model.Profile;
import io.xstefank.model.Repository;
import io.xstefank.model.Settings;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

@QuarkusMain
public class AugmenterMain implements QuarkusApplication {

    private static final Logger LOG = Logger.getLogger(AugmenterMain.class);

    private boolean skip = false;

    @ConfigProperty(name = "profile.name", defaultValue = "mp-staging")
    String profileName;

    @Override
    public int run(String... args) throws Exception {
        if (args.length < 2) {
            outputHelp();
            return 0;
        }

        String repositories = null;
        String output = null;
        String input = args[args.length - 1];

        for (int i = 0; i < args.length - 1; i++) {
            if (skip) {
                skip = false;
                continue;
            }

            Entry entry = createEntry(args[i]);

            switch (entry.key) {
                case "-r":
                case "--repositories":
                    repositories = processValue(entry.value, args[i + 1]);
                    break;
                case "-o":
                case "--output":
                    output = processValue(entry.value, args[i + 1]);
            }
        }

        if (input == null) {
            LOG.error("Input file not provided.");
        }

        if (LOG.isDebugEnabled()) {
            LOG.trace("Repositories: " + repositories);
            LOG.trace("Output: " + output);
            LOG.trace("Input: " + input);
        }

        File inFile = new File(input);
        File outFile = new File(output != null ? output : input);
        if (!outFile.exists()) {
            if (!outFile.createNewFile()) {
                LOG.error("Cannot create output file.");
                return 1;
            }
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        SAXSource source = createSaxSource(inFile);

        Settings readSettings = (Settings) jaxbUnmarshaller.unmarshal(source);

        // add custom profile
        Profile profile = new Profile();
        profile.id = profileName;
        profile.repositories = new ArrayList<>();

        if (repositories != null) {
            for (String url : repositories.split(",")) {
                Repository repository = new Repository(url);
                profile.repositories.add(repository);
            }
        } else {
            LOG.warn("No repositories provided.");
        }

        readSettings.profiles.add(profile);

        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(readSettings, stringWriter);
        String xml = stringWriter.toString().replaceAll(">\\s+<", "><");

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(outFile));

        System.out.println("Modified settings.xml saved to " + outFile.getPath());

        return 0;
    }

    private SAXSource createSaxSource(File inFile) throws ParserConfigurationException, SAXException, FileNotFoundException {
        //Create an XMLReader to use with our filter
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        XMLReader reader = parser.getXMLReader();

        //Create the filter (to add namespace) and set the xmlReader as its parent.
        NamespaceFilter inFilter = new NamespaceFilter(null, false);
        inFilter.setParent(reader);

        InputSource is = new InputSource(new FileInputStream(inFile));

        return new SAXSource(inFilter, is);
    }

    private String processValue(String value, String nextValue) {
        if (value != null) {
            return value;
        } else {
            skip = true;
            return nextValue;
        }
    }

    private Entry createEntry(String arg) {
        if (arg.contains("=")) {
            String[] split = arg.split("=");
            return new Entry(split[0], split[1]);
        } else {
            return new Entry(arg, null);
        }
    }

    private void outputHelp() {
        System.out.println();
        System.out.println("usage: staging-augmenter [options] <path-to-pom>");
        System.out.println();
        System.out.println("Options:");
        System.out.println(" -r,--repositories          The comma separated list of repositories to include " +
            "in the new profile");
        System.out.println(" -o,--output                The optional path to the output file, otherwise the input " +
            "file is modified in place");
    }
}
