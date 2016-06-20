package io.sledge.core.impl.installer;

import io.sledge.core.api.installer.PackageConfigurer;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author oliver.burkhalter
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class SledgePackageConfigurer implements PackageConfigurer {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private Detector tikaDetector;

	private List<String> validTextfileTypes = Arrays.asList("text/plain", "application/xml", "text/x-java-properties");

	@Override
	public InputStream configure(InputStream packageStream, String packageName, Properties props) {
		InputStream resultStream = packageStream;

		try {
			Path sledgeTmpDir = createSledgeTmpDirectoryIfNotExists();
			deleteZipFile(sledgeTmpDir, packageName);
			Path configurationZipPackagePath = sledgeTmpDir.resolve(packageName);
			createNewZipfileWithReplacedPlaceholders(packageStream, configurationZipPackagePath, props);
			resultStream = new FileInputStream(configurationZipPackagePath.toFile());
		} catch (IOException e) {
			log.error("Could not configure package: " + packageName, e);
		} finally {
			try {
				packageStream.reset();
				packageStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		return resultStream;
	}

	private void createNewZipfileWithReplacedPlaceholders(InputStream packageStream, Path configurationPackagePath, Properties envProps)
			throws IOException {

		// For reading the original configuration package
		ZipInputStream configPackageZipStream = new ZipInputStream(new BufferedInputStream(packageStream), Charset.forName("UTF-8"));

		// For outputting the configured (placeholders replaced) version of the package as zip file
		File outZipFile = configurationPackagePath.toFile();
		ZipOutputStream outConfiguredZipStream = new ZipOutputStream(new FileOutputStream(outZipFile));

		ZipEntry zipEntry;
		while ((zipEntry = configPackageZipStream.getNextEntry()) != null) {

			if (zipEntry.isDirectory()) {
				configPackageZipStream.closeEntry();
				continue;
			} else {
				ByteArrayOutputStream output = new ByteArrayOutputStream();

				int length;
				byte[] buffer = new byte[2048];
				while ((length = configPackageZipStream.read(buffer, 0, buffer.length)) >= 0) {
					output.write(buffer, 0, length);
				}

				InputStream zipEntryInputStream = new BufferedInputStream(new ByteArrayInputStream(output.toByteArray()));

				if (zipEntry.getName().endsWith("instance.properties")) {
					ByteArrayOutputStream envPropsOut = new ByteArrayOutputStream();
					envProps.store(envPropsOut, "Environment configurations");
					zipEntryInputStream = new BufferedInputStream(new ByteArrayInputStream(envPropsOut.toByteArray()));

				} else if (isTextFile(zipEntry.getName(), zipEntryInputStream)) {
					String configuredContent = StrSubstitutor.replace(output, envProps);
					zipEntryInputStream = new BufferedInputStream(new ByteArrayInputStream(configuredContent.getBytes()));
				}

				// Add to output zip file
				addToZipFile(zipEntry.getName(), zipEntryInputStream, outConfiguredZipStream);

				zipEntryInputStream.close();
				configPackageZipStream.closeEntry();
			}
		}

		outConfiguredZipStream.close();
		configPackageZipStream.close();
	}

	private boolean isTextFile(String filename, InputStream inputStream) throws IOException {
		Metadata metadata = new Metadata();
		metadata.set(Metadata.RESOURCE_NAME_KEY, filename);
		final MediaType mediaType = tikaDetector.detect(inputStream, metadata);

		return validTextfileTypes.contains(mediaType.toString());
	}

	private void addToZipFile(String zipEntryName, InputStream inStream, ZipOutputStream zipStream) throws IOException {
		log.debug("Adding zip entry: " + zipEntryName);

		ZipEntry entry = new ZipEntry(zipEntryName);
		zipStream.putNextEntry(entry);

		byte[] readBuffer = new byte[2048];
		int amountRead;
		int written = 0;

		while ((amountRead = inStream.read(readBuffer)) > 0) {
			zipStream.write(readBuffer, 0, amountRead);
			written += amountRead;
		}

		zipStream.closeEntry();
		log.debug("Written " + written + " bytes for: " + zipEntryName);
	}

	private void deleteZipFile(Path directory, String packageName) throws IOException {
		Path currentDir = directory.resolve(packageName);
		Files.deleteIfExists(currentDir);
	}

	private Path createSledgeTmpDirectoryIfNotExists() throws IOException {
		Path sledgeTmpDir = Paths.get("sledge-tmp");
		if (Files.notExists(sledgeTmpDir)) {
			Files.createDirectory(sledgeTmpDir);
		}
		return sledgeTmpDir;
	}
}
