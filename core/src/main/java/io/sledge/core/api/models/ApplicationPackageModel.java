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

import io.sledge.core.api.ApplicationPackage;
import io.sledge.core.api.extractor.ApplicationPackageExtractor;
import io.sledge.core.impl.extractor.SledgeApplicationPackageExtractor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.InputStream;
import java.util.List;

/**
 * This {@link ApplicationPackage} implementation uses Sling Models to map a Application package resource to a Java model.
 * <p/>
 * Use {@link UploadApplicationPackage} if you need an Application package which is not yet bound to a resource.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ApplicationPackageModel implements ApplicationPackage {

	private String path;

	@ValueMapValue
	private String groupId;

	@ValueMapValue
	private String artifactId;

	@ValueMapValue
	private String version;

	@ValueMapValue
	private String applicationPackageType;

	@ValueMapValue
	private String packageFilename;

	@ChildResource(name = "packageFile/jcr:content")
	private Resource packageFileResource;

	@ValueMapValue
	private String state;

	@ValueMapValue
	private String usedEnvironment;

	public ApplicationPackageModel() {
	}

	public ApplicationPackageModel(String packageFileName, String groupId, String artifactId, String version) {
		this.packageFilename = packageFileName;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPackageFilename() {
		return packageFilename;
	}

	public void setPackageFilename(String packageFilename) {
		this.packageFilename = packageFilename;
	}

	public InputStream getPackageFileStream() {
		return packageFileResource.adaptTo(InputStream.class);
	}

	public ApplicationPackageState getState() {
		return ApplicationPackageState.valueOf(state);
	}

	/**
	 * @param state A defined string from the {@link ApplicationPackageState} enum
	 */
	public void setState(ApplicationPackageState state) {
		this.state = state.toString();
	}

	public String getUsedEnvironment() {
		return usedEnvironment;
	}

	public void setUsedEnvironment(String usedEnvironment) {
		this.usedEnvironment = usedEnvironment;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public ApplicationPackageType getApplicationPackageType() {
		return ApplicationPackageType.valueOf(applicationPackageType);
	}

	public List<String> getEnvironmentNames() {
		ApplicationPackageExtractor appPackageExtractor = new SledgeApplicationPackageExtractor();
		return appPackageExtractor.getEnvironmentNames(this);
	}
}
