package io.xstefank.model;

import javax.xml.bind.Element;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "settings")
public class Settings {

    @XmlAttribute
    public String xmlns = "http://maven.apache.org/SETTINGS/1.0.0";

    @XmlAttribute(name = "xmlns:xsi")
    public String xmlnsXsi = "http://www.w3.org/2001/XMLSchema-instance";

    @XmlAttribute(name = "xsi:schemaLocation", required = true)
    public String xsiSchemaLocation = "http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd";

    @XmlElementWrapper(name = "profiles")
    @XmlElement(name = "profile")
    public List<Profile> profiles = new ArrayList<>();

    @XmlAnyElement(lax = true)
    public List<Element> others;
}
