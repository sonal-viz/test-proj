package com.myorg.databricks.cluster;

import com.myorg.databricks.client.ClustersClient;
import com.myorg.databricks.client.HttpException;
import com.myorg.databricks.client.JobsClient;
import com.myorg.databricks.client.LibrariesClient;
import com.myorg.databricks.client.entities.clusters.ClusterInfoDTO;
import com.myorg.databricks.client.entities.libraries.*;
import com.myorg.databricks.job.builder.InteractiveJarJobBuilder;
import com.myorg.databricks.job.builder.InteractiveNotebookJobBuilder;
import com.myorg.databricks.library.*;
import com.myorg.databricks.library.util.LibraryHelper;
import com.myorg.databricks.workspace.Notebook;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class InteractiveCluster extends Cluster{
    private ClustersClient _client;
    private LibrariesClient _librariesClient;
    private Boolean _isAutoScaling = false;
    private JobsClient _jobsClient;
    private ArrayList<ClusterLibrary> _libraries = new ArrayList<ClusterLibrary>();

    public final String Name;
    public final Integer NumWorkers;
    public final AutoScale AutoScale;
    public final Integer AutoTerminationMinutes;


    /**
     * Represents a Databricks InteractiveCluster.
     *
     * Is instantiated by
     *   1. InteractiveClusterBuilder.create()
     *   2. DatabricksSession.getCluster() by Id
     *   3. InteractiveCluster.resize()
     *
     * @param client Databricks ClusterClient
     * @param info Databricks ClusterInfoDTO POJO
     * @throws ClusterConfigException
     */
    public InteractiveCluster(ClustersClient client, ClusterInfoDTO info) throws ClusterConfigException, HttpException {
        super(client, info);
        _client = client;

        //Validate that required fields are populated in the ClusterInfoDTO
        validateClusterInfo(info);

        Name = info.ClusterName;
        NumWorkers = info.NumWorkers;

        if(info.AutoScale != null){
            _isAutoScaling = true;
            AutoScale = new AutoScale(info.AutoScale);
        } else {
            AutoScale = null;
        }

        //Set fields that do not change throughout the lifespan of a cluster configuration
        // these fields may not have been set in the DTO if object was instantiated from InteractiveClusterBuilder.create()
        AutoTerminationMinutes = initAutoTerminationMinutes();
    }

    private void validateClusterInfo(ClusterInfoDTO info) throws ClusterConfigException {
        if(info.ClusterName == null) {
            throw new ClusterConfigException("ClusterInfoDTO Must Have Name");
        }

        if(info.NumWorkers == null && info.AutoScale == null)  {
            throw new ClusterConfigException("ClusterInfoDTO Must Have either NumWorkers OR AutoScaleDTO");
        }
    }

    private Integer initAutoTerminationMinutes() throws HttpException  {
        return getClusterInfo().AutoTerminationMinutes;
    }

    public void start() throws HttpException {
        _client.start(Id);
    }

    public void restart() throws HttpException {
        _client.reStart(Id);
    }

    public void terminate() throws HttpException {
        _client.delete(Id);
    }

    public InteractiveCluster resize(Integer numWorkers) throws ClusterConfigException, HttpException {
        if(_isAutoScaling) {
            throw new ClusterConfigException("Must Include New Min and Max Worker Values when Resizing an Autoscaling InteractiveCluster");
        }
        _client.resize(Id, numWorkers);

        ClusterInfoDTO resizedClusterConfig = getClusterInfo();
        resizedClusterConfig.NumWorkers = numWorkers;
        return new InteractiveCluster(_client, resizedClusterConfig);
    }

    public InteractiveCluster resize(Integer minWorkers, Integer maxWorkers) throws ClusterConfigException, HttpException {
        if(!_isAutoScaling) {
            throw new ClusterConfigException("Must Only Include a Single Value When Resizing a Fixed Size InteractiveCluster");
        }
        _client.resize(Id, minWorkers, maxWorkers);

        ClusterInfoDTO resizedClusterConfig = getClusterInfo();
        resizedClusterConfig.AutoScale.MinWorkers = minWorkers;
        resizedClusterConfig.AutoScale.MaxWorkers = maxWorkers;
        return new InteractiveCluster(_client, resizedClusterConfig);
    }

    private JobsClient getOrCreateJobsClient() {
        if(_jobsClient == null) {
            _jobsClient = new JobsClient(_client.Session);
        }
        return _jobsClient;
    }

    public InteractiveNotebookJobBuilder createJob(Notebook notebook) {
      return new InteractiveNotebookJobBuilder(getOrCreateJobsClient(), this, notebook);
    }

    public InteractiveNotebookJobBuilder createJob(Notebook notebook, Map<String,String> baseParameters) {
        return new InteractiveNotebookJobBuilder(getOrCreateJobsClient(), this, notebook, baseParameters);
    }

    public InteractiveJarJobBuilder createJarJob(URI jarLibrary, String mainClassName) {
        return new InteractiveJarJobBuilder(getOrCreateJobsClient(), this, jarLibrary, mainClassName);
    }

    public InteractiveJarJobBuilder createJarJob(URI jarLibrary, File jarFile, String mainClassName) {
        return new InteractiveJarJobBuilder(getOrCreateJobsClient(), this, jarLibrary, jarFile, mainClassName);
    }

    public InteractiveJarJobBuilder createJarJob(URI jarLibrary, String mainClassName,
                                              List<String> baseParameters) {
        return new InteractiveJarJobBuilder(getOrCreateJobsClient(), this, jarLibrary, mainClassName, baseParameters);
    }

    public InteractiveJarJobBuilder createJarJob(URI jarLibrary, File jarFile,
                                              String mainClassName,
                                              List<String> baseParameters) {
        return new InteractiveJarJobBuilder(getOrCreateJobsClient(), this, jarLibrary, jarFile, mainClassName, baseParameters);
    }

    private LibrariesClient getLibrariesClient() {
        if(_librariesClient == null) {
            _librariesClient = new LibrariesClient(_client.Session);
        }
        return _librariesClient;
    }

    public void installLibrary(JarLibrary library) throws HttpException {
        ClusterLibraryRequestDTO libraryRequestDTO = new ClusterLibraryRequestDTO();
        libraryRequestDTO.ClusterId = this.Id;
        libraryRequestDTO.Libraries = new LibraryDTO[1];
        libraryRequestDTO.Libraries[0] = library.createLibraryDTO();

        getLibrariesClient().installLibraries(libraryRequestDTO);
        _libraries.add(new ClusterLibrary(this, library));
    }

    public void installLibrary(EggLibrary library) throws HttpException {
        ClusterLibraryRequestDTO libraryRequestDTO = new ClusterLibraryRequestDTO();
        libraryRequestDTO.ClusterId = this.Id;
        libraryRequestDTO.Libraries = new LibraryDTO[1];
        libraryRequestDTO.Libraries[0] = library.createLibraryDTO();

        getLibrariesClient().installLibraries(libraryRequestDTO);
        _libraries.add(new ClusterLibrary(this, library));
    }

    public void installLibrary(MavenLibrary library) throws HttpException {
        ClusterLibraryRequestDTO libraryRequestDTO = new ClusterLibraryRequestDTO();
        libraryRequestDTO.ClusterId = this.Id;
        libraryRequestDTO.Libraries = new LibraryDTO[1];
        libraryRequestDTO.Libraries[0] = library.createLibraryDTO();

        getLibrariesClient().installLibraries(libraryRequestDTO);
        _libraries.add(new ClusterLibrary(this, library));
    }

    public void installLibrary(PyPiLibrary library) throws HttpException {
        ClusterLibraryRequestDTO libraryRequestDTO = new ClusterLibraryRequestDTO();
        libraryRequestDTO.ClusterId = this.Id;
        libraryRequestDTO.Libraries = new LibraryDTO[1];
        libraryRequestDTO.Libraries[0] = library.createLibraryDTO();

        getLibrariesClient().installLibraries(libraryRequestDTO);
        _libraries.add(new ClusterLibrary(this, library));
    }

    public void installLibrary(CranLibrary library) throws HttpException {
        ClusterLibraryRequestDTO libraryRequestDTO = new ClusterLibraryRequestDTO();
        libraryRequestDTO.ClusterId = this.Id;
        libraryRequestDTO.Libraries = new LibraryDTO[1];
        libraryRequestDTO.Libraries[0] = library.createLibraryDTO();

        getLibrariesClient().installLibraries(libraryRequestDTO);
        _libraries.add(new ClusterLibrary(this, library));
    }

    public void uninstallLibrary(JarLibrary library) throws HttpException {
        LibraryDTO libraryDTO = new LibraryDTO();
        libraryDTO.Jar = library.Uri.toString();

        ClusterLibraryRequestDTO libraryRequestDTO = new ClusterLibraryRequestDTO();
        libraryRequestDTO.ClusterId = this.Id;
        libraryRequestDTO.Libraries = new LibraryDTO[1];
        libraryRequestDTO.Libraries[0] = libraryDTO;

        getLibrariesClient().uninstallLibraries(libraryRequestDTO);
    }

    public void uninstallLibrary(EggLibrary library) throws HttpException {
        LibraryDTO libraryDTO = new LibraryDTO();
        libraryDTO.Egg = library.Uri.toString();

        ClusterLibraryRequestDTO libraryRequestDTO = new ClusterLibraryRequestDTO();
        libraryRequestDTO.ClusterId = this.Id;
        libraryRequestDTO.Libraries = new LibraryDTO[1];
        libraryRequestDTO.Libraries[0] = libraryDTO;

        getLibrariesClient().uninstallLibraries(libraryRequestDTO);
    }

    public void uninstallLibrary(PyPiLibrary library) throws HttpException {
        LibraryDTO libraryDTO = new LibraryDTO();
        PythonPyPiLibraryDTO pyPiLibrary = new PythonPyPiLibraryDTO();
        pyPiLibrary.Package = library.PackageName;
        pyPiLibrary.Repo = library.RepoOverride;
        libraryDTO.PyPi = pyPiLibrary;

        ClusterLibraryRequestDTO libraryRequestDTO = new ClusterLibraryRequestDTO();
        libraryRequestDTO.ClusterId = this.Id;
        libraryRequestDTO.Libraries = new LibraryDTO[1];
        libraryRequestDTO.Libraries[0] = libraryDTO;

        getLibrariesClient().uninstallLibraries(libraryRequestDTO);
    }

    public void uninstallLibrary(MavenLibrary library) throws HttpException {
        LibraryDTO libraryDTO = new LibraryDTO();
        MavenLibraryDTO mavenLibraryDTO = new MavenLibraryDTO();
        mavenLibraryDTO.Coordinates = library.Coordinates;
        mavenLibraryDTO.Repo = library.RepoOverride;
        mavenLibraryDTO.Exclusions = library.DependencyExclusions;
        libraryDTO.Maven = mavenLibraryDTO;

        ClusterLibraryRequestDTO libraryRequestDTO = new ClusterLibraryRequestDTO();
        libraryRequestDTO.ClusterId = this.Id;
        libraryRequestDTO.Libraries = new LibraryDTO[1];
        libraryRequestDTO.Libraries[0] = libraryDTO;

        getLibrariesClient().uninstallLibraries(libraryRequestDTO);
    }

    public void uninstallLibrary(CranLibrary library) throws HttpException {
        LibraryDTO libraryDTO = new LibraryDTO();
        RCranLibraryDTO cranLibraryDTO = new RCranLibraryDTO();
        cranLibraryDTO.Package = library.PackageName;
        cranLibraryDTO.Repo = library.RepoOverride;
        libraryDTO.Cran = cranLibraryDTO;

        ClusterLibraryRequestDTO libraryRequestDTO = new ClusterLibraryRequestDTO();
        libraryRequestDTO.ClusterId = this.Id;
        libraryRequestDTO.Libraries = new LibraryDTO[1];
        libraryRequestDTO.Libraries[0] = libraryDTO;

        getLibrariesClient().uninstallLibraries(libraryRequestDTO);
    }

    public List<ClusterLibrary> getLibraries() throws LibraryConfigException, HttpException, URISyntaxException {
        //add remote libraries to cached list (if NOT already in list)
        ClusterLibraryStatusesDTO remoteLibStatuses = getLibrariesClient().getClusterStatus(this.Id);
        for (LibraryFullStatusDTO libStat : remoteLibStatuses.LibraryStatuses) {
            Library library = LibraryHelper.createLibrary(getLibrariesClient(), libStat.Library);
            ClusterLibrary clusterLibrary = getClusterLibraryFromCache(library);
            if(clusterLibrary == null) {
                _libraries.add(new ClusterLibrary(this, library));
            }
        }
        //remove ClusterLibrary from cached list if it is NOT in the remote library status list
        //avoid ConcurrentModificationException by using the list's iterator
        Iterator<ClusterLibrary> iterClusterLibrary = _libraries.iterator();
        while(iterClusterLibrary.hasNext()) {
          ClusterLibrary clusterLib = iterClusterLibrary.next();
          if(getLibraryFromDTO(remoteLibStatuses, clusterLib.Library) == null) {
              iterClusterLibrary.remove();
          }
        }
        return Collections.unmodifiableList(_libraries);
    }

    private ClusterLibrary getClusterLibraryFromCache(Library library) throws HttpException {
        for (ClusterLibrary clusterLibrary : _libraries) {
            if(clusterLibrary.Library.equals(library)) {
                return clusterLibrary;
            }
        }
        //no ClusterLibrary matches the library
        return null;
    }

    private Library getLibraryFromDTO(ClusterLibraryStatusesDTO remoteLibStatuses,
                                      Library library) throws HttpException {
        for (LibraryFullStatusDTO remoteLibStatus : remoteLibStatuses.LibraryStatuses) {
            if(remoteLibStatus.Library.equals(library)) {
                return library;
            }
        }
        //Library could not be found in DTO
        return null;
    }
}
