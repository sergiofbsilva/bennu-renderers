package pt.ist.fenixWebFramework.renderers.components.state;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.UserIdentity;

public class ViewState implements IViewState {

    private String id;

    private String layout;

    private Properties properties;

    private transient PresentationContext context;

    private Class contextClass;

    private Map<String, Object> attributes;

    transient private List<Message> messages;

    // Hidden slots, filled from context

    private List<HiddenSlot> hiddenSlots;

    // Destinations available 

    private ViewDestination inputDestination;

    private Map<String, ViewDestination> destinations;

    private ViewDestination currentDestination;

    // Properties set after each deserialization 

    private UserIdentity user;

    transient private HtmlComponent component;

    transient private HttpServletRequest request;

    // Viewed object 

    private MetaObject metaObject;

    // Lifecycle properties

    private boolean visible;

    private boolean valid;

    private boolean skipUpdate;

    private boolean skipValidation;

    private boolean updateComponentTree;

    private boolean postBack;

    public ViewState() {
        super();

        this.valid = true;
        this.skipUpdate = false;
        this.skipValidation = false;
        this.updateComponentTree = true;
        this.postBack = false;
        this.visible = true;

        this.messages = new ArrayList<Message>();
    }

    public ViewState(String id) {
        this();

        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isPostBack() {
        return postBack;
    }

    @Override
    public void cancel() {
        this.id = "";
        this.inputDestination = null;
    }

    @Override
    public boolean isCanceled() {
        return this.id != null && this.id.equals("") && this.inputDestination == null;
    }

    @Override
    public void setPostBack(boolean isPostBack) {
        this.postBack = isPostBack;
    }

    @Override
    public HtmlComponent getComponent() {
        return this.component;
    }

    @Override
    public void setComponent(HtmlComponent component) {
        this.component = component;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void setValid(boolean isValid) {
        this.valid = isValid;
    }

    @Override
    public boolean skipUpdate() {
        return this.skipUpdate;
    }

    @Override
    public void setSkipUpdate(boolean skipUpdate) {
        this.skipUpdate = skipUpdate;
    }

    @Override
    public boolean skipValidation() {
        return this.skipValidation;
    }

    @Override
    public void setSkipValidation(boolean skipValidation) {
        this.skipValidation = skipValidation;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Override
    public void setUpdateComponentTree(boolean updateTree) {
        this.updateComponentTree = updateTree;
    }

    @Override
    public boolean getUpdateComponentTree() {
        return this.updateComponentTree;
    }

    @Override
    public void addDestination(String name, ViewDestination destination) {
        if (this.destinations == null) {
            this.destinations = new Hashtable<String, ViewDestination>();
        }

        this.destinations.put(name, destination);
    }

    @Override
    public ViewDestination getDestination(String name) {
        if (this.destinations == null) {
            this.destinations = new Hashtable<String, ViewDestination>();
        }

        return this.destinations.get(name);
    }

    @Override
    public void setInputDestination(ViewDestination destination) {
        this.inputDestination = destination;
    }

    @Override
    public ViewDestination getInputDestination() {
        return this.inputDestination;
    }

    @Override
    public void setCurrentDestination(String name) {
        this.currentDestination = getDestination(name);
    }

    @Override
    public void setCurrentDestination(ViewDestination destination) {
        this.currentDestination = destination;
    }

    @Override
    public ViewDestination getCurrentDestination() {
        return this.currentDestination;
    }

    @Override
    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void setMetaObject(MetaObject object) {
        this.metaObject = object;
    }

    @Override
    public MetaObject getMetaObject() {
        return this.metaObject;
    }

    @Override
    public UserIdentity getUser() {
        return user;
    }

    @Override
    public void setUser(UserIdentity user) {
        this.user = user;

        if (getMetaObject() != null) {
            getMetaObject().setUser(user);
        }
    }

    @Override
    public String getLayout() {
        return layout;
    }

    @Override
    public void setLayout(String layout) {
        this.layout = layout;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Class getContextClass() {
        return contextClass;
    }

    @Override
    public void setContextClass(Class contextClass) {
        this.contextClass = contextClass;
    }

    @Override
    public void setLocalAttribute(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public Object getLocalAttribute(String name) {
        return getAttribute(name);
    }

    @Override
    public void removeLocalAttribute(String name) {
        removeAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (this.attributes == null) {
            this.attributes = new Hashtable<String, Object>();
        }

        this.attributes.put(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        if (this.attributes == null) {
            this.attributes = new Hashtable<String, Object>();
        }

        return this.attributes.get(name);
    }

    @Override
    public void removeAttribute(String name) {
        if (this.attributes == null) {
            this.attributes = new Hashtable<String, Object>();
        }

        this.attributes.remove(name);
    }

    //
    // Serialization utils
    //

    private static String encodeObjectToBase64(Object object) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream zipStream = new GZIPOutputStream(byteOutputStream);

        ObjectOutputStream stream = new ObjectOutputStream(zipStream);

        stream.writeObject(object);
        stream.close();

        return new String(Base64.encodeBase64(byteOutputStream.toByteArray()));
    }

    public static String encodeListToBase64(List<IViewState> viewStates) throws IOException {
        return encodeObjectToBase64(viewStates);
    }

    public static String encodeToBase64(IViewState state) throws IOException {
        return encodeObjectToBase64(state);
    }

    private static Object decodeObjectFromBase64(String encodedState) throws IOException, ClassNotFoundException {
        byte[] decodedForm = Base64.decodeBase64(encodedState.getBytes());

        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(decodedForm);
        GZIPInputStream zipStream = new GZIPInputStream(byteInputStream);

        ObjectInputStream stream = new ObjectInputStream(zipStream);

        return stream.readObject();
    }

    public static List<IViewState> decodeListFromBase64(String encodedState) throws IOException, ClassNotFoundException {
        return (List<IViewState>) decodeObjectFromBase64(encodedState);
    }

    public static IViewState decodeFromBase64(String encodedState) throws IOException, ClassNotFoundException {
        return (IViewState) decodeObjectFromBase64(encodedState);
    }

    @Override
    public void setContext(PresentationContext context) {
        this.context = context;

        if (this.context != null) {
            setContextClass(this.context.getClass());
        }
    }

    @Override
    public PresentationContext getContext() {
        return this.context;
    }

    @Override
    public void addHiddenSlot(HiddenSlot slot) {
        if (this.hiddenSlots == null) {
            this.hiddenSlots = new ArrayList<HiddenSlot>();
        }

        this.hiddenSlots.add(slot);
    }

    @Override
    public List<HiddenSlot> getHiddenSlots() {
        if (this.hiddenSlots == null) {
            this.hiddenSlots = new ArrayList<HiddenSlot>();
        }

        return this.hiddenSlots;
    }

    @Override
    public List<Message> setMessages(List<Message> messages) {
        ensureMessageList();
        return this.messages = messages;
    }

    @Override
    public List<Message> getMessages() {
        ensureMessageList();
        return this.messages;
    }

    @Override
    public void addMessage(Message message) {
        ensureMessageList();
        this.messages.add(message);
    }

    private void ensureMessageList() {
        if (this.messages == null) {
            this.messages = new ArrayList<Message>();
        }
    }

}
