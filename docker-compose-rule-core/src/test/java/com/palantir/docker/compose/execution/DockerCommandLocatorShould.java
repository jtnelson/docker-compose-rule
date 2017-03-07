/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.palantir.docker.compose.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class DockerCommandLocatorShould {
    private static final String command = "not-a-real-command!";
    private static final String windowsCommand = command + ".exe";

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Rule public ExpectedException exception = ExpectedException.none();

    private Path firstPathFolder;

    private String commandFileLocation;
    private String windowsCommandFileLocation;

    @Before
    public void setup() throws IOException {
        firstPathFolder = folder.newFolder("first").toPath();

        commandFileLocation = Files.createFile(firstPathFolder.resolve(command)).toString();
        windowsCommandFileLocation = Files.createFile(firstPathFolder.resolve(windowsCommand)).toString();
    }

    @Test public void
    provide_the_first_command_location() {
        DockerCommandLocator locator = DockerCommandLocator.forCommand(command)
                .locationOverride(firstPathFolder.toString())
                .isWindows(false)
                .build();
        assertThat(locator.getLocation(), is(commandFileLocation));
    }

    @Test public void
    provide_the_first_command_location_on_windows() {
        DockerCommandLocator locator = DockerCommandLocator.forCommand(command)
                .locationOverride(firstPathFolder.toString())
                .isWindows(true)
                .build();
        assertThat(locator.getLocation(), is(windowsCommandFileLocation));
    }

    @Test public void
    fail_when_no_paths_contain_command() {
        DockerCommandLocator locator = DockerCommandLocator.forCommand(command)
                .locationOverride(null)
                .isWindows(false)
                .build();

        exception.expect(IllegalStateException.class);
        exception.expectMessage("Could not find " + command + " in path");
        locator.getLocation();
    }

}
