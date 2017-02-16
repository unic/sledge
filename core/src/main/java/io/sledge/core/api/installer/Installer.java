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

package io.sledge.core.api.installer;

import io.sledge.core.api.ApplicationPackage;

/**
 * The Installer takes an {@link ApplicationPackage} and installs it.
 * If you would like to handle proper environment configurations then use the {@link ConfigurableInstaller}.
 */
public interface Installer {

    /**
     * Installs a given package for a given environment provided in the setEnvironment mehtod. It takes the environment configuration provided by the given Application package and
     * merges it with the given {#propsForMerge} properties object given by the setPropertiesForMerge method.
     * <p>
     * May throw an {@link InstallationException} if anything fails during installation process.
     *
     * @param appPackage The application package.
     */
    void install(ApplicationPackage appPackage) throws InstallationException;

    /**
     * @return Returns true if this Installer is handling this application package type.
     */
    boolean handles(ApplicationPackage appPackage);
}
