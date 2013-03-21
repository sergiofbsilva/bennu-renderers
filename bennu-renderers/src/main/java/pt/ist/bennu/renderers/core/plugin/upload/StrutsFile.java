package pt.ist.fenixWebFramework.renderers.plugin.upload;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.struts.upload.FormFile;

import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

/**
 * This is a wrapper around a {@link org.apache.struts.upload.FormFile form file} from
 * Struts upload.
 * 
 * @author cfgi
 */
public class StrutsFile implements UploadedFile {

    private FormFile strutsFile;

    public StrutsFile(FormFile strutsFile) {
        super();

        this.strutsFile = strutsFile;
    }

    @Override
    public String getName() {
        return this.strutsFile.getFileName();
    }

    @Override
    public String getContentType() {
        return this.strutsFile.getContentType();
    }

    @Override
    public long getSize() {
        return this.strutsFile.getFileSize();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.strutsFile.getInputStream();
    }

    @Override
    public byte[] getFileData() throws FileNotFoundException, IOException {
        return strutsFile.getFileData();
    }

}
