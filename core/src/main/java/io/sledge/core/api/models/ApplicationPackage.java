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

package io.sledge.core.api.models;

import io.sledge.core.api.extractor.ApplicationPackageExtractor;
import io.sledge.core.impl.extractor.SledgeApplicationPackageExtractor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

/**
 * An <i>ApplicationPackage</i> provides all needed information for the whole
 * application. It contains the bundles or packages (e.g. CRX packages), configurations and the deployment descriptor file <i>sledgefile.xml</i>.
 * <p>
 * The deployment descriptor file sledgefile.xml declares the <strong>deployment-def</strong> elements which defines a deployment for a specific environment.
 * </p>
 *
 * @author oliver.burkhalter
 */
@Model(adaptables = Resource.class)
public class ApplicationPackage {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@ValueMapValue
	private String packageFilename;

	private String path;

	@ChildResource(name = "packageFile/jcr:content")
	private InputStream packageFile;

	@ValueMapValue
	private String state;

	@ValueMapValue(optional = true)
	private String usedEnvironment;

	public ApplicationPackage() {
	}

	public ApplicationPackage(String packageFileName) {
		this.packageFilename = packageFileName;
	}

	public String getPackageFilename() {
		return packageFilename;
	}

	public void setPackageFilename(String packageFilename) {
		this.packageFilename = packageFilename;
	}

	public InputStream getPackageFile() {
		return packageFile;
	}

	public void setPackageFile(InputStream packageFile) {
		this.packageFile = packageFile;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getState() {
		return state;
	}

	/**
	 * @param state A defined string from the {@link ApplicationPackageState} enum
	 */
	public void setState(String state) {
		this.state = state;
	}

	public String getUsedEnvironment() {
		return usedEnvironment;
	}

	public void setUsedEnvironment(String usedEnvironment) {
		this.usedEnvironment = usedEnvironment;
	}

	public List<String> getEnvironmentNames() {
		ApplicationPackageExtractor appPackageExtractor = new SledgeApplicationPackageExtractor();
		return appPackageExtractor.getEnvironmentNames(this);
	}
}