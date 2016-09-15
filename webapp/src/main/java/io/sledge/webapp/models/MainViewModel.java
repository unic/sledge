/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
