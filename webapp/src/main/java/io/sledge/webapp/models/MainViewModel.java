package io.sledge.webapp.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import java.util.Iterator;

/**
 * Model for the /etc/sledge/packages.html resource.
 *
 * @author oliver.burkhalter
 */
@Model(adaptables = Resource.class)
public class MainViewModel {

	@Self
	private Resource resource;

	public Iterator<Resource> getConnectorResources() {
		return resource.getParent().getChild("connectors").listChildren();
	}

	public boolean isSightlyGraniteClientlibTemplateAvailable() {
		Resource sightlyGraniteClientlibTemplateResource = resource.getResourceResolver()
				.getResource("/libs/granite/sightly/templates/clientlib.html");
		return sightlyGraniteClientlibTemplateResource != null;
	}
}
