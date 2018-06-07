package com.myorg.databricks.job.builder;

import com.myorg.databricks.client.HttpException;
import com.myorg.databricks.client.JobsClient;
import com.myorg.databricks.client.entities.jobs.JobSettingsDTO;
import com.myorg.databricks.client.entities.libraries.LibraryDTO;
import com.myorg.databricks.client.entities.libraries.MavenLibraryDTO;
import com.myorg.databricks.client.entities.libraries.PythonPyPiLibraryDTO;
import com.myorg.databricks.client.entities.libraries.RCranLibraryDTO;
import com.myorg.databricks.cluster.InteractiveCluster;
import com.myorg.databricks.library.Library;
import com.myorg.databricks.library.LibraryConfigException;
import com.myorg.databricks.util.FileUtils;
import org.quartz.Trigger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public abstract class InteractiveJobBuilder extends JobBuilder {
    private final JobsClient _client;
    private ArrayList<LibraryDTO> _libraries = new ArrayList<LibraryDTO>();
    private Map<URI, File> _libraryFileMap = new HashMap<URI, File>();

    public final InteractiveCluster Cluster;

    public InteractiveJobBuilder(InteractiveCluster cluster, JobsClient client) {
        super();
        Cluster = cluster;
        _client = client;
    }

    @Override
    protected InteractiveJobBuilder withName(String name) {
        return (InteractiveJobBuilder)super.withName(name);
    }

    @Override
    protected InteractiveJobBuilder withEmailNotificationOnStart(String email) {
        return (InteractiveJobBuilder)super.withEmailNotificationOnStart(email);
    }

    @Override
    protected InteractiveJobBuilder withEmailNotificationOnSuccess(String email) {
        return (InteractiveJobBuilder)super.withEmailNotificationOnSuccess(email);
    }

    @Override
    protected InteractiveJobBuilder withEmailNotificationOnFailure(String email) {
        return (InteractiveJobBuilder)super.withEmailNotificationOnFailure(email);
    }

    @Override
    protected InteractiveJobBuilder withTimeout(int seconds) {
        return (InteractiveJobBuilder)super.withTimeout(seconds);
    }

    @Override
    protected InteractiveJobBuilder withMaxRetries(int retries) {
        return (InteractiveJobBuilder)super.withMaxRetries(retries);
    }

    @Override
    protected InteractiveJobBuilder withMinRetryInterval(int milliseconds) {
        return (InteractiveJobBuilder)super.withMinRetryInterval(milliseconds);
    }

    @Override
    protected InteractiveJobBuilder withRetryOnTimeout(boolean retryOnTimeout) {
        return (InteractiveJobBuilder)super.withRetryOnTimeout(retryOnTimeout);
    }

    @Override
    protected InteractiveJobBuilder withMaxConcurrentRuns(int maxConcurrentRuns) {
        return (InteractiveJobBuilder)super.withMaxConcurrentRuns(maxConcurrentRuns);
    }

    @Override
    protected InteractiveJobBuilder withSchedule(Trigger trigger, TimeZone timeZone) {
        return (InteractiveJobBuilder)super.withSchedule(trigger, timeZone);
    }

    @Override
    protected JobSettingsDTO applySettings(JobSettingsDTO jobSettingsDTO) {
        jobSettingsDTO = super.applySettings(jobSettingsDTO);
        jobSettingsDTO.ExistingClusterId = Cluster.Id;

        if(_libraries.size() > 0) {
            jobSettingsDTO.Libraries = _libraries.toArray(new LibraryDTO[_libraries.size()]);
        }
        return jobSettingsDTO;
    }

    protected InteractiveJobBuilder withJarLibrary(URI uri) {
        LibraryDTO libraryDTO = new LibraryDTO();
        libraryDTO.Jar = uri.toString();
        _libraries.add(libraryDTO);
        return this;
    }

    protected InteractiveJobBuilder withJarLibrary(URI uri, File libraryFile) {
        addLibraryToUpload(uri, libraryFile);
        return this.withJarLibrary(uri);
    }

    protected InteractiveJobBuilder withEggLibrary(URI uri) {
        LibraryDTO libraryDTO = new LibraryDTO();
        libraryDTO.Egg = uri.toString();
        _libraries.add(libraryDTO);
        return this;
    }

    protected InteractiveJobBuilder withEggLibrary(URI uri, File libraryFile) {
        addLibraryToUpload(uri, libraryFile);
        return this.withEggLibrary(uri);
    }

    protected InteractiveJobBuilder withMavenLibrary(String coordinates) {
        LibraryDTO libraryDTO = new LibraryDTO();
        MavenLibraryDTO mavenDTO = new MavenLibraryDTO();
        mavenDTO.Coordinates = coordinates;
        libraryDTO.Maven = mavenDTO;
        _libraries.add(libraryDTO);
        return this;
    }

    protected InteractiveJobBuilder withMavenLibrary(String coordinates, String repo) {
        LibraryDTO libraryDTO = new LibraryDTO();
        MavenLibraryDTO mavenDTO = new MavenLibraryDTO();
        mavenDTO.Coordinates = coordinates;
        mavenDTO.Repo = repo;
        libraryDTO.Maven = mavenDTO;
        _libraries.add(libraryDTO);
        return this;
    }

    protected InteractiveJobBuilder withMavenLibrary(String coordinates, String repo, String[] exclusions) {
        LibraryDTO libraryDTO = new LibraryDTO();
        MavenLibraryDTO mavenDTO = new MavenLibraryDTO();
        mavenDTO.Coordinates = coordinates;
        mavenDTO.Repo = repo;
        mavenDTO.Exclusions = exclusions;
        libraryDTO.Maven = mavenDTO;
        _libraries.add(libraryDTO);
        return this;
    }

    protected InteractiveJobBuilder withMavenLibrary(String coordinates, String[] exclusions) {
        LibraryDTO libraryDTO = new LibraryDTO();
        MavenLibraryDTO mavenDTO = new MavenLibraryDTO();
        mavenDTO.Coordinates = coordinates;
        mavenDTO.Exclusions = exclusions;
        libraryDTO.Maven = mavenDTO;
        _libraries.add(libraryDTO);
        return this;
    }

    protected InteractiveJobBuilder withPyPiLibrary(String packageName)  {
        LibraryDTO libraryDTO = new LibraryDTO();
        PythonPyPiLibraryDTO piPyDTO = new PythonPyPiLibraryDTO();
        piPyDTO.Package = packageName;
        libraryDTO.PyPi = piPyDTO;
        _libraries.add(libraryDTO);
        return this;
    }

    protected InteractiveJobBuilder withPyPiLibrary(String packageName, String repo) {
        LibraryDTO libraryDTO = new LibraryDTO();
        PythonPyPiLibraryDTO piPyDTO = new PythonPyPiLibraryDTO();
        piPyDTO.Package = packageName;
        piPyDTO.Repo = repo;
        libraryDTO.PyPi = piPyDTO;
        _libraries.add(libraryDTO);
        return this;
    }

    protected InteractiveJobBuilder withCranLibrary(String packageName) {
        LibraryDTO libraryDTO = new LibraryDTO();
        RCranLibraryDTO cranDTO = new RCranLibraryDTO();
        cranDTO.Package = packageName;
        libraryDTO.Cran = cranDTO;
        _libraries.add(libraryDTO);
        return this;
    }

    protected InteractiveJobBuilder withCranLibrary(String packageName, String repo) {
        LibraryDTO libraryDTO = new LibraryDTO();
        RCranLibraryDTO cranDTO = new RCranLibraryDTO();
        cranDTO.Package = packageName;
        cranDTO.Repo = repo;
        libraryDTO.Cran = cranDTO;
        _libraries.add(libraryDTO);
        return this;
    }

    private void addLibraryToUpload(URI destination, File libraryFile) {
        _libraryFileMap.put(destination, libraryFile);
    }

    protected void uploadLibraryFiles() throws HttpException, IOException, LibraryConfigException {
        for (URI uri : _libraryFileMap.keySet()) {
            FileUtils.uploadFile(_client.Session, _libraryFileMap.get(uri), uri);
        }
    }

}
