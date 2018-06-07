package com.myorg.databricks.library;

import com.myorg.databricks.client.HttpException;
import com.myorg.databricks.client.LibrariesClient;
import com.myorg.databricks.client.entities.libraries.ClusterLibraryStatusesDTO;
import com.myorg.databricks.client.entities.libraries.LibraryDTO;
import com.myorg.databricks.client.entities.libraries.LibraryFullStatusDTO;
import com.myorg.databricks.client.entities.libraries.PythonPyPiLibraryDTO;
import com.myorg.databricks.cluster.InteractiveCluster;

public class PyPiLibrary extends PublishedLibrary {
    private final LibrariesClient _client;

    public final String PackageName;
    public final String RepoOverride;

    public PyPiLibrary(LibrariesClient client, String packageName) {
        super();
        _client = client;
        PackageName = packageName;
        RepoOverride = null;
    }

    public PyPiLibrary(LibrariesClient client, String packageName, String repo) {
        super();
        _client = client;
        PackageName = packageName;
        RepoOverride = repo;
    }

    public LibraryStatus getClusterStatus(InteractiveCluster cluster) throws HttpException, LibraryConfigException {
        ClusterLibraryStatusesDTO libStatuses = _client.getClusterStatus(cluster.Id);

        //find library status for this library
        for (LibraryFullStatusDTO libStat : libStatuses.LibraryStatuses) {
            if(libStat.Library.PyPi != null) {
                if(libStat.Library.PyPi.Package.equals(this.PackageName)) {
                    return new LibraryStatus(libStat);
                }
            }
        }
        throw new LibraryConfigException("PyPi Library " + this.PackageName +
                " Not Associated With Cluster Id " + cluster.Id);
    }

    public LibraryDTO createLibraryDTO() {
        LibraryDTO libraryDTO = new LibraryDTO();
        PythonPyPiLibraryDTO pyPiLibrary = new PythonPyPiLibraryDTO();
        pyPiLibrary.Package = this.PackageName;
        pyPiLibrary.Repo = this.RepoOverride;
        libraryDTO.PyPi = pyPiLibrary;
        return libraryDTO;
    }
}
