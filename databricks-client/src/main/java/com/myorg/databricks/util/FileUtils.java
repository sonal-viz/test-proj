package com.myorg.databricks.util;

import com.myorg.databricks.client.DatabricksSession;
import com.myorg.databricks.client.HttpException;
import com.myorg.databricks.library.LibraryConfigException;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class FileUtils {

    public static void uploadFile(DatabricksSession session, File file, URI destination) throws HttpException, IOException, LibraryConfigException {
        //TODO add support for s3, s3a, s3n
        if(destination.getScheme() == null) {
            throw new LibraryConfigException("Library must be stored in dbfs or s3. Make sure the URI begins with 'dbfs:' or 's3:'");
        } else if(destination.getScheme().equals("dbfs")) {
            session.putDbfsFile(file, destination.toString());
        } else {
            throw new LibraryConfigException(destination.getScheme() + " is not a supported scheme for upload");
        }

    }
}
