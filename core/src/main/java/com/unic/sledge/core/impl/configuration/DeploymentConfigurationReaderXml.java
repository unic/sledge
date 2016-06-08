package com.unic.sledge.core.impl.configuration;

import com.unic.sledge.core.api.configuration.DeploymentConfiguration;
import com.unic.sledge.core.api.configuration.DeploymentConfigurationReader;
import com.unic.sledge.core.api.configuration.DeploymentDef;
import com.unic.sledge.core.api.configuration.DuplicateEnvironmentException;
import com.unic.sledge.core.api.configuration.PackageElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Parses the sledgefile.xml with the following format:
 * <pre>
 *     &lt;sledgefile>
 *         &lt;deployment-def environments="test-author,test-publish">
 *             &lt;packages>
 *             &lt;!-- If empty or null then by default it installs nothing -->
 *             &lt;/packages>
 *         &lt;/deployment-def>
 *         &lt;deployment-def environments="prod-author">
 *             &lt;packages>
 *                 &lt;package>com.my.package.common-1.0.0-crx.zip&lt;/package>
 *                 &lt;package>com.my.package.templating-1.0.0-crx.zip&lt;/package>
 *                 &lt;package>com.my.package.ws-1.0.0-crx.zip&lt;/package>
 *                 &lt;package>com.my.package.migration-1.0.0-crx.zip&lt;/package>
 *                 &lt;package configure="true">com.my.package.configuration-1.0.0-crx.zip&lt;/package>
 *             &lt;/packages>
 *         &lt;/deployment-def>
 *         &lt;deployment-def environments="prod-publish">
 *             &lt;packages>
 *                 &lt;package>com.my.package.common-1.0.0-crx.zip&lt;/package>
 *                 &lt;package>com.my.package.templating-1.0.0-crx.zip&lt;/package>
 *                 &lt;package>com.my.package.ws-1.0.0-crx.zip&lt;/package>
 *                 &lt;package configure="true">com.my.package.configuration-1.0.0-crx.zip&lt;/package>
 *             &lt;/packages>
 *         &lt;/deployment-def>
 * &lt;/sledgefile>
 * </pre>
 * The {@link com.unic.sledge.core.impl.extractor.SledgeApplicationPackageExtractor} uses this reader to get the {@link DeploymentConfiguration}.
 *
 * @author oliver.burkhalter
 */
public class DeploymentConfigurationReaderXml implements DeploymentConfigurationReader {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private boolean isStartPackageElement = false;
	private boolean isConfigureAttribute = false;

	@Override
	public DeploymentConfiguration parseDeploymentConfiguration(InputStream deployConfigFile) {
		List<DeploymentDef> deploymentDefList = new ArrayList<>();

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = null;

		try {
			eventReader = inputFactory.createXMLEventReader(new StreamSource(deployConfigFile));

			log.debug("START parsing sledgefile.xml --------------------");

			DeploymentDef deploymentDef = null;
			PackageElement packageElement = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					String elementName = event.asStartElement().getName().getLocalPart();

					log.debug("Start element: " + elementName);

					if (isDeploymentDefElement(elementName)) {
						deploymentDef = new DeploymentDef();
						handleEnvironmentsAttribute(deploymentDef, event);
					} else if (isPackageElement(elementName)) {
						isStartPackageElement = true;
						isConfigureAttribute = getConfigureAttribute(event);
					}
				}

				if (event.isCharacters()) {
					String elementData = event.asCharacters().getData().trim();

					if (!elementData.isEmpty() && isStartPackageElement) {
						packageElement = new PackageElement(isConfigureAttribute, elementData);
						deploymentDef.addPackage(packageElement);
					}

					if (!elementData.isEmpty()) {
						log.debug("=\"" + elementData + "\"");
					}
				}

				if (event.isEndElement()) {
					String elementName = event.asEndElement().getName().getLocalPart();

					if (isDeploymentDefElement(elementName)) {
						addDeploymentDefObject(deploymentDefList, deploymentDef);
					} else if (isPackageElement(elementName)) {
						isStartPackageElement = false;
					}

					log.debug("End element: " + elementName);
				}
			}

			log.debug("-------------------- END parsing sledgefile.xml");
		} catch (XMLStreamException e) {
			log.error("Could not parse deployment configuration file. ", e);
		}

		return new DeploymentConfiguration(deploymentDefList);
	}

	private boolean getConfigureAttribute(XMLEvent event) {
		boolean configure = false;

		Iterator<Attribute> iter = event.asStartElement().getAttributes();
		while (iter.hasNext()) {
			Attribute attr = iter.next();
			String attributeName = attr.getName().getLocalPart();

			if ("configure".equalsIgnoreCase(attributeName)) {
				configure = Boolean.parseBoolean(attr.getValue());
				break;
			}

			log.debug("/@" + attributeName + "=\"" + attr.getValue() + "\"");
		}

		return configure;
	}

	private void handleEnvironmentsAttribute(DeploymentDef deploymentDef, XMLEvent event) {
		Iterator<Attribute> iter = event.asStartElement().getAttributes();

		while (iter.hasNext()) {
			Attribute attr = iter.next();
			String attributeName = attr.getName().getLocalPart();

			if ("environments".equalsIgnoreCase(attributeName)) {
				List<String> environments = Arrays.asList(attr.getValue().split(","));
				deploymentDef.setEnvironments(environments);
			}

			log.debug("/@" + attributeName + "=\"" + attr.getValue() + "\"");
		}
	}

	private void addDeploymentDefObject(List<DeploymentDef> deploymentDefList, DeploymentDef deploymentDef) {
		// Prepare predicate to check that there is no deploymentDef element with a same environment name
		Predicate<DeploymentDef> duplicateEnvironmentPredicate = e -> e.getEnvironments().stream()
				.anyMatch(p -> deploymentDef.getEnvironments().contains(p));

		if (deploymentDefList.stream().anyMatch(duplicateEnvironmentPredicate)) {
			throw new DuplicateEnvironmentException(
					"The deployment configuration file contains already a deploymentDef element with the same environment name!");
		} else {
			deploymentDefList.add(deploymentDef);
		}
	}

	private boolean isPackageElement(String elementName) {
		return "package".equalsIgnoreCase(elementName);
	}

	private boolean isDeploymentDefElement(String elementName) {
		return "deployment-def".equalsIgnoreCase(elementName);
	}
}