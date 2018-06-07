package com.myorg.databricks.library;

import com.myorg.databricks.client.HttpException;
import com.myorg.databricks.client.LibrariesClient;
import com.myorg.databricks.client.entities.libraries.ClusterLibraryStatusesDTO;
import com.myorg.databricks.client.entities.libraries.LibraryDTO;
import com.myorg.databricks.client.entities.libraries.LibraryFullStatusDTO;
import com.myorg.databricks.cluster.InteractiveCluster;
import java.net.URI;

public class EggLibrary extends PrivateLibrary {
    private final LibrariesClient _client;

    public EggLibrary(LibrariesClient client, URI uri) throws LibraryConfigException {
        super(client, uri);
        _client = client;
    }

    public LibraryStatus getClusterStatus(InteractiveCluster cluster) throws HttpException, LibraryConfigException {
        ClusterLibraryStatusesDTO libStatuses = _client.getClusterStatus(cluster.Id);

        //find library status for this library
        for (LibraryFullStatusDTO libStat : libStatuses.LibraryStatuses) {
            if(libStat.Library.Egg != null) {
                if(libStat.Library.Egg.equals(this.Uri.toString())) {
                    return new LibraryStatus(libStat);
                }
            }
        }
        throw new LibraryConfigException("Egg Library " + this.Uri.toString() +
                " Not Associated With Cluster Id " + cluster.Id);
    }

    public LibraryDTO createLibraryDTO() {
        LibraryDTO libraryDTO = new LibraryDTO();
        libraryDTO.Egg = this.Uri.toString();
        return libraryDTO;
    }

}
