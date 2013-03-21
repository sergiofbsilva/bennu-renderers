package pt.ist.bennu.renderers.core.exceptions;

public class NoRendererException extends RuntimeException {

    public NoRendererException(Class type, String layout) {
        super("No available render for type '" + type.getName() + "' and layout '" + layout + "'");
    }

}
