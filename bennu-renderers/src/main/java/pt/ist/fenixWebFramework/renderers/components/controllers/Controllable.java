package pt.ist.fenixWebFramework.renderers.components.controllers;

public interface Controllable {
    public boolean hasController();

    public HtmlController getController();

    public void setController(HtmlController controller);
}
