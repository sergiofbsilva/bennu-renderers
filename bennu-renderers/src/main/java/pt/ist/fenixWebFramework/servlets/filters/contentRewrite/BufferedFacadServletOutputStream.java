package pt.ist.fenixWebFramework.servlets.filters.contentRewrite;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

public class BufferedFacadServletOutputStream extends ServletOutputStream {

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    final OutputStream realOutputStream;

    public BufferedFacadServletOutputStream(final OutputStream realOutputStream) {
        this.realOutputStream = realOutputStream;
    }

    @Override
    public void write(final int value) throws IOException {
        byteArrayOutputStream.write(value);
    }

    @Override
    public void write(final byte[] value) throws IOException {
        byteArrayOutputStream.write(value);
    }

    @Override
    public void write(final byte[] value, final int off, final int len) throws IOException {
        byteArrayOutputStream.write(value, off, len);
    }

    public void writeRealResponse(final RequestRewriter... requestRewriters) throws IOException {
        realOutputStream.write(byteArrayOutputStream.toByteArray());
        realOutputStream.flush();
        realOutputStream.close();
    }

    public String getContent() {
        return byteArrayOutputStream.toString();
    }

    public void resetBuffer() {
        byteArrayOutputStream.reset();
    }

}
