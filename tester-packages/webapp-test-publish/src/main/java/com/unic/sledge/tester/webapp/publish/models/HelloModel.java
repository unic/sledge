package com.unic.sledge.tester.webapp.publish.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * @author oliver.burkhalter
 */
@Model(adaptables = Resource.class)
public class HelloModel {

    public String hello() {
        return "Hello Tester! (for Publish)";
    }
}
