package io.xstefank.model;

import javax.xml.bind.Element;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "settings")
public class Settings {

    @XmlAttribute(required = true)
    public String xmlns;

    @XmlAttribute(name = "xmlns:xsi", required = true)
    public String xmlnsXsi;

    @XmlAttribute(name = "xsi:schemaLocation", required = true)
    public String xsiSchemaLocation;

    @XmlElementWrapper(name = "profiles")
    @XmlElement(name = "profile")
    public List<Profile> profiles;

    @XmlAnyElement(lax = true)
    public List<Element> others;

    @Override
    public String toString() {
        return "Settings{" +
//            ", profiles=" + profiles +
            ", others=" + others +
            '}';
    }
}
