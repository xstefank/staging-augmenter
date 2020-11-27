package io.xstefank.model;

import org.jboss.logging.Logger;

import javax.xml.bind.Element;
import javax.xml.bind.annotation.XmlAnyElement;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

public class Repository {

    public String id;
    public String url;

    @XmlAnyElement(lax = true)
    public List<Element> others;

    public Repository() {
    }

    public Repository(String url) {
        try {
            this.id = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.getLogger(Repository.class).error("Cannot encode repository url " + url);
            this.id = UUID.randomUUID().toString();
        }
        this.url = url;
    }
}
