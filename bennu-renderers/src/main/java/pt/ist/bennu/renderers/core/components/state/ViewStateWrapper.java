package pt.ist.fenixWebFramework.renderers.components.state;

import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.UserIdentity;

public class ViewStateWrapper implements IViewState {

    private IViewState viewState;

    private String attributesPrefix;

    public ViewStateWrapper(IViewState viewState, String attributesPrefix) {
        this.viewState = viewState;
        this.attributesPrefix = attributesPrefix;
    }

    public String getPrefix() {
        return this.attributesPrefix;
    }

    public IViewState getWrappedViewState() {
        return this.viewState;
    }

    @Override
    public Object getAttribute(String name) {
        return this.viewState.getAttribute(name);
    }

    @Override
    public void removeAttribute(String name) {
        viewState.removeAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.viewState.setAttribute(name, value);
    }

    @Override
    public void setLocalAttribute(String name, Object value) {
        viewState.setAttribute(attributesPrefix + "/" + name, value);
    }

    @Override
    public Object getLocalAttribute(String name) {
        return viewState.getAttribute(attributesPrefix + "/" + name);
    }

    @Override
    public void removeLocalAttribute(String name) {
        viewState.removeAttribute(attributesPrefix + "/" + name);
    }

    @Override
    public String getId() {
        return viewState.getId();
    }

    @Override
    public boolean isPostBack() {
        return this.viewState.isPostBack();
    }

    @Override
    public void setPostBack(boolean isPostBack) {
        viewState.setPostBack(isPostBack);
    }

    @Override
    public boolean isCanceled() {
        return this.viewState.isCanceled();
    }

    @Override
    public void cancel() {
        this.viewState.cancel();
    }

    @Override
    public HtmlComponent getComponent() {
        return viewState.getComponent();
    }

    @Override
    public void setComponent(HtmlComponent component) {
        viewState.setComponent(component);
    }

    @Override
    public boolean isVisible() {
        return this.viewState.isVisible();
    }

    @Override
    public void setVisible(boolean isVisible) {
        this.viewState.setVisible(isVisible);
    }

    @Override
    public void setValid(boolean isValid) {
        viewState.setValid(isValid);
    }

    @Override
    public boolean skipUpdate() {
        return viewState.skipUpdate();
    }

    @Override
    public void setSkipUpdate(boolean skipUpdate) {
        viewState.setSkipUpdate(skipUpdate);
    }

    @Override
    public void setSkipValidation(boolean skipValidation) {
        this.viewState.setSkipValidation(skipValidation);
    }

    @Override
    public boolean skipValidation() {
        return this.viewState.skipValidation();
    }

    @Override
    public boolean isValid() {
        return viewState.isValid();
    }

    @Override
    public void setUpdateComponentTree(boolean updateTree) {
        viewState.setUpdateComponentTree(updateTree);
    }

    @Override
    public boolean getUpdateComponentTree() {
        return viewState.getUpdateComponentTree();
    }

    @Override
    public void addDestination(String name, ViewDestination destination) {
        viewState.addDestination(name, destination);
    }

    @Override
    public ViewDestination getDestination(String name) {
        return viewState.getDestination(name);
    }

    @Override
    public void setInputDestination(ViewDestination destination) {
        viewState.setInputDestination(destination);
    }

    @Override
    public ViewDestination getInputDestination() {
        return viewState.getInputDestination();
    }

    @Override
    public void setCurrentDestination(String name) {
        viewState.setCurrentDestination(name);
    }

    @Override
    public void setCurrentDestination(ViewDestination destination) {
        viewState.setCurrentDestination(destination);
    }

    @Override
    public ViewDestination getCurrentDestination() {
        return viewState.getCurrentDestination();
    }

    @Override
    public HttpServletRequest getRequest() {
        return viewState.getRequest();
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        viewState.setRequest(request);
    }

    @Override
    public UserIdentity getUser() {
        return viewState.getUser();
    }

    @Override
    public void setUser(UserIdentity user) {
        viewState.setUser(user);
    }

    @Override
    public String getLayout() {
        return viewState.getLayout();
    }

    @Override
    public void setLayout(String layout) {
        viewState.setLayout(layout);
    }

    @Override
    public Properties getProperties() {
        return viewState.getProperties();
    }

    @Override
    public void setProperties(Properties properties) {
        viewState.setProperties(properties);
    }

    @Override
    public Class getContextClass() {
        return viewState.getContextClass();
    }

    @Override
    public void setContextClass(Class contextClass) {
        viewState.setContextClass(contextClass);
    }

    @Override
    public void setMetaObject(MetaObject object) {
        viewState.setMetaObject(object);
    }

    @Override
    public MetaObject getMetaObject() {
        return viewState.getMetaObject();
    }

    @Override
    public void setContext(PresentationContext context) {
        this.viewState.setContext(context);
    }

    @Override
    public PresentationContext getContext() {
        return this.viewState.getContext();
    }

    @Override
    public void addHiddenSlot(HiddenSlot slot) {
        this.viewState.addHiddenSlot(slot);
    }

    @Override
    public List<HiddenSlot> getHiddenSlots() {
        return this.viewState.getHiddenSlots();
    }

    @Override
    public void addMessage(Message message) {
        this.viewState.addMessage(message);
    }

    @Override
    public List<Message> getMessages() {
        return this.viewState.getMessages();
    }

    @Override
    public List<Message> setMessages(List<Message> messages) {
        return this.viewState.setMessages(messages);
    }
}
