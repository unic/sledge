package io.sledge.core.impl.extractor;

import io.sledge.core.api.configuration.DeploymentConfiguration;
import io.sledge.core.api.configuration.DeploymentConfigurationReader;
import io.sledge.core.api.configuration.DeploymentDef;
import io.sledge.core.api.extractor.ApplicationPackageExtractor;
import io.sledge.core.api.models.ApplicationPackage;
import io.sledge.core.impl.configuration.DeploymentConfigurationReaderXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static io.sledge.core.api.SledgeConstants.SLEDGEFILE_XML;
import static org.apache.commons.io.FilenameUtils.getBaseName;

/**
 * The {@link SledgeApplicationPackageExtractor} extracts data from a given {@link ApplicationPackage}.
 * The application package format is like this:
 * <pre>
 *     root/
 *       - environments/
 *         - test-author.properties
 *         - test-publish.properties
 *         - etc.
 *       - packages/
 *         - com.my.project.app-x.y.z.zip
 *         - com.my.project.lib-x.y.z.jar
 *       - sledgefile.xml
 * </pre>
 *
 * @author oliver.burkhalter
 */
public class SledgeApplicationPackageExtractor implements ApplicationPackageExtractor {

	private final Logger log = LoggerFactory.getLogger(getClass());

	public SledgeApplicationPackageExtractor() {
	}

	@Override
	public List<String> getEnvironmentNames(ApplicationPackage appPackage) {
		List<String> envFiles = new ArrayList<>();
		ZipInputStream zipStream = getNewUtf8ZipInputStream(appPackage);

		try {
			ZipEntry zipEntry = null;

			while ((zipEntry = zipStream.getNextEntry()) != null) {

				if (zipEntry.isDirectory()) {
					zipStream.closeEntry();
					continue;
				}

				if (zipEntry.getName().startsWith("environments/")) {
					// test-publish.properties -> test-publish
					envFiles.add(getBaseName(zipEntry.getName()));
					zipStream.closeEntry();
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				zipStream.close();
				appPackage.getPackageFile().reset();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		return envFiles;
	}

	@Override
	public String getEnvironmentFile(String environmentName, ApplicationPackage appPackage) {
		ZipInputStream zipStream = getNewUtf8ZipInputStream(appPackage);
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		try {
			byte[] buffer = new byte[2048];
			ZipEntry zipEntry = null;

			while ((zipEntry = zipStream.getNextEntry()) != null) {

				if (zipEntry.isDirectory()) {
					zipStream.closeEntry();
					continue;
				}

				if (getBaseName(zipEntry.getName()).equals(environmentName)) {

					int length;
					while ((length = zipStream.read(buffer, 0, buffer.length)) >= 0) {
						output.write(buffer, 0, length);
					}

					zipStream.closeEntry();

					// Stop here because the file has been already read
					break;
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				zipStream.close();
				appPackage.getPackageFile().reset();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		return output.toString();
	}

	@Override
	public Map<String, InputStream> getPackages(ApplicationPackage appPackage) {
		Map<String, InputStream> packages = new HashMap<>();
		ZipInputStream zipStream = getNewUtf8ZipInputStream(appPackage);

		try {
			byte[] buffer = new byte[2048];
			ZipEntry zipEntry = null;

			while ((zipEntry = zipStream.getNextEntry()) != null) {

				if (zipEntry.isDirectory()) {
					zipStream.closeEntry();
					continue;
				}

				if (zipEntry.getName().startsWith("packages/")) {
					ByteArrayOutputStream output = new ByteArrayOutputStream();

					int length;
					while ((length = zipStream.read(buffer, 0, buffer.length)) >= 0) {
						output.write(buffer, 0, length);
					}

					String packageFileName = zipEntry.getName().replace("packages/", "");
					packages.put(packageFileName, new ByteArrayInputStream(output.toByteArray()));

					zipStream.closeEntry();
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				zipStream.close();
				appPackage.getPackageFile().reset();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		return packages;
	}

	@Override
	public List<Map.Entry<String, InputStream>> getPackagesByEnvironment(ApplicationPackage appPackage, String envName) {
		List<Map.Entry<String, InputStream>> packages;
		Map<String, InputStream> allPackages = getPackages(appPackage);

		DeploymentConfiguration deploymentConfiguration = getDeploymentConfiguration(appPackage.getPackageFile());
		final DeploymentDef deploymentDef = deploymentConfiguration.getDeploymentDefByEnvironment(envName);
		final List<String> packageNamesForEnv = deploymentDef.getPackageNames();

		packages = allPackages.entrySet().stream().filter(packageEntry -> packageNamesForEnv.contains(packageEntry.getKey()))
				.collect(Collectors.toList());

		return packages;
	}

	@Override
	public DeploymentConfiguration getDeploymentConfiguration(InputStream appPackageInputStream) {
		DeploymentConfiguration deploymentConfig = null;
		ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(appPackageInputStream), Charset.forName("UTF-8"));

		try {
			byte[] buffer = new byte[2048];
			ZipEntry zipEntry = null;

			while ((zipEntry = zipStream.getNextEntry()) != null) {

				if (zipEntry.isDirectory()) {
					zipStream.closeEntry();
					continue;
				}

				if (zipEntry.getName().startsWith(SLEDGEFILE_XML)) {
					ByteArrayOutputStream output = new ByteArrayOutputStream();

					int length;
					while ((length = zipStream.read(buffer, 0, buffer.length)) >= 0) {
						output.write(buffer, 0, length);
					}

					DeploymentConfigurationReader deploymentConfigReader = new DeploymentConfigurationReaderXml();
					deploymentConfig = deploymentConfigReader.parseDeploymentConfiguration(new ByteArrayInputStream(output.toByteArray()));

					zipStream.closeEntry();

					// Stop here, the file is read
					break;
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				zipStream.close();
				appPackageInputStream.reset();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		return deploymentConfig;
	}

	private ZipInputStream getNewUtf8ZipInputStream(ApplicationPackage appPackage) {
		return new ZipInputStream(new BufferedInputStream(appPackage.getPackageFile()), Charset.forName("UTF-8"));
	}
}
