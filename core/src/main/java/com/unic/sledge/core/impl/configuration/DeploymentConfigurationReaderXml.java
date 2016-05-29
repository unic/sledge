package com.unic.sledge.core.impl.configuration;

import com.unic.sledge.core.api.configuration.DeploymentConfiguration;
import com.unic.sledge.core.api.configuration.DeploymentConfigurationReader;
import com.unic.sledge.core.api.configuration.DeploymentDef;
import com.unic.sledge.core.api.configuration.DuplicateEnvironmentException;
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
 * @author oliver.burkhalter
 */
public class DeploymentConfigurationReaderXml implements DeploymentConfigurationReader {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private boolean isStartInstallerElement = false;
	private boolean isStartConfigurerElement = false;
	private boolean isStartPackageElement = false;

	@Override
	public DeploymentConfiguration parseDeploymentConfiguration(InputStream deployConfigFile) {
		List<DeploymentDef> deploymentDefList = new ArrayList<>();

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = null;

		try {
			eventReader = inputFactory.createXMLEventReader(new StreamSource(deployConfigFile));

			log.debug("START parsing sledgefile.xml --------------------");

			DeploymentDef deploymentDef = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					String elementName = event.asStartElement().getName().getLocalPart();

					log.debug("Start element: " + elementName);

					if (isDeploymentDefElement(elementName)) {
						deploymentDef = new DeploymentDef();
						handleEnvironmentsAttribute(deploymentDef, event);
					} else if (isInstallerElement(elementName)) {
						isStartInstallerElement = true;
					} else if (isConfigurerElement(elementName)) {
						isStartConfigurerElement = true;
					} else if (isPackageElement(elementName)) {
						isStartPackageElement = true;
					}
				}

				if (event.isCharacters()) {
					String elementData = event.asCharacters().getData().trim();

					if (!elementData.isEmpty() && isStartInstallerElement && isStartPackageElement) {
						deploymentDef.addInstallerPackageName(elementData);
					}

					if (!elementData.isEmpty() && isStartConfigurerElement && isStartPackageElement) {
						deploymentDef.addConfigurerPackageName(elementData);
					}

					if (!elementData.isEmpty()) {
						log.debug("=\"" + elementData + "\"");
					}
				}

				if (event.isEndElement()) {
					String elementName = event.asEndElement().getName().getLocalPart();

					if (isDeploymentDefElement(elementName)) {
						addDeploymentDefObject(deploymentDefList, deploymentDef);
					} else if (isInstallerElement(elementName)) {
						isStartInstallerElement = false;
					} else if (isConfigurerElement(elementName)) {
						isStartConfigurerElement = false;
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

	private boolean isConfigurerElement(String elementName) {
		return "configurer".equalsIgnoreCase(elementName);
	}

	private boolean isInstallerElement(String elementName) {
		return "installer".equalsIgnoreCase(elementName);
	}

	private boolean isPackageElement(String elementName) {
		return "package".equalsIgnoreCase(elementName);
	}

	private boolean isDeploymentDefElement(String elementName) {
		return "deployment-def".equalsIgnoreCase(elementName);
	}
}
