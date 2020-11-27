package io.xstefank.model;

import javax.xml.bind.Element;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

public class Profile {

    public String id;

    @XmlElementWrapper(name = "repositories")
    @XmlElement(name = "repository")
    public List<Repository> repositories;

    @XmlAnyElement(lax = true)
    public List<Element> others;

    @Override
    public String toString() {
        return "Profile{" +
            "id='" + id + '\'' +
            ", repositories=" + repositories +
            '}';
    }
}
