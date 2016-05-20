package com.unic.sledge.core.impl.extractor;

import com.unic.sledge.core.api.extractor.ApplicationPackageExtractor;
import com.unic.sledge.core.api.models.ApplicationPackage;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
	public List<String> getEnvironmentFilenames(ApplicationPackage appPackage) {
		List<String> envFiles = new ArrayList<>();
		ZipInputStream zipStream = getNewUtf8ZipInputStream(appPackage);

		try {
			ZipEntry zipEntry = null;

			while ((zipEntry = zipStream.getNextEntry()) != null) {

				log.info("Reading zip entry: " + zipEntry.getName());

				if (zipEntry.isDirectory()) {
					zipStream.closeEntry();
					continue;
				}

				if (zipEntry.getName().startsWith("environments/")) {
					String configFileName = zipEntry.getName().substring(13);
					envFiles.add(configFileName);
					zipStream.closeEntry();
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			try {
				zipStream.close();
				appPackage.getPackageFile().reset();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}

		return envFiles;
	}

	@Override
	public String getEnvironmentFile(String environmentFileName, ApplicationPackage appPackage) {
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

				if (zipEntry.getName().endsWith(environmentFileName)) {

					int length;
					while ((length = zipStream.read(buffer, 0,buffer.length)) >= 0) {
						output.write(buffer, 0, length);
					}

					zipStream.closeEntry();

					// Stop here because the file has been already read
					break;
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			try {
				zipStream.close();
				appPackage.getPackageFile().reset();
			} catch (IOException e) {
				log.error(e.getMessage());
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
					while ((length = zipStream.read(buffer, 0,buffer.length)) >= 0) {
						output.write(buffer, 0, length);
					}

					String packageFileName = zipEntry.getName().replace("packages/", "");
					packages.put(packageFileName, new ByteArrayInputStream(output.toByteArray()));

					zipStream.closeEntry();
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			try {
				zipStream.close();
				appPackage.getPackageFile().reset();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}

		return packages;
	}

	private ZipInputStream getNewUtf8ZipInputStream(ApplicationPackage appPackage) {
		return new ZipInputStream(new BufferedInputStream(appPackage.getPackageFile()), Charset.forName("UTF-8"));
	}
}
