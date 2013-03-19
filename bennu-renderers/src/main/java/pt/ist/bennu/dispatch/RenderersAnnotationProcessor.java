package pt.ist.bennu.dispatch;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.apache.struts.action.ActionForward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.groups.BennuGroup;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.dispatch.model.ApplicationInfo;
import pt.ist.bennu.dispatch.model.BundleDetails;
import pt.ist.bennu.dispatch.model.FunctionalityInfo;
import pt.ist.bennu.renderers.actions.RenderAction;
import pt.ist.bennu.renderers.annotation.Mapping;
import pt.ist.bennu.renderers.struts.StrutsAnnotationsPlugIn;

@HandlesTypes({ Mapping.class })
public class RenderersAnnotationProcessor implements ServletContainerInitializer {
    public static class Forwarder implements Serializable {
        private String path;

        private String groupExpression;

        private transient BennuGroup group;

        public Forwarder(String path, String groupExpression) {
            this.path = path;
            this.groupExpression = groupExpression;
        }

        private BennuGroup group() {
            if (group == null) {
                group = BennuGroup.parse(groupExpression);
            }
            return group;
        }

        public ActionForward forward() {
            if (group().isMember(Authenticate.getUser())) {
                return new ActionForward(path);
            }
            return null;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(RenderersAnnotationProcessor.class);
    private static Map<String, Forwarder> actionMap = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public void onStartup(Set<Class<?>> classes, ServletContext context) throws ServletException {
        if (classes != null) {
            Map<Class<?>, ApplicationInfo> apps = new HashMap<>();
            for (Class<?> type : classes) {
                Mapping mapping = type.getAnnotation(Mapping.class);
                final Application app = type.getAnnotation(Application.class);
                if (app != null) {
                    extractApp(apps, type);
                }
                if (mapping != null) {
                    StrutsAnnotationsPlugIn.registerMapping(type);
                    scanEntryPoints(apps, mapping.path(), type);
                }
            }
            for (ApplicationInfo application : apps.values()) {
                AppServer.registerApp(application);
            }
            RenderAction.initializeMap(actionMap);
        }
    }

    private void scanEntryPoints(Map<Class<?>, ApplicationInfo> apps, String path, Class<?> type) {
        for (Method method : type.getMethods()) {
            Functionality functionality = method.getAnnotation(Functionality.class);
            if (functionality != null) {
                extractFunctionality(apps, functionality);
                actionMap.put(functionality.app().getAnnotation(Application.class).path() + "/" + functionality.path(),
                        new Forwarder(path + ".do?method=" + method.getName(), functionality.group()));
            }
        }
    }

    public void extractFunctionality(Map<Class<?>, ApplicationInfo> apps, Functionality functionality) {
        extractApp(apps, functionality.app());
        BundleDetails details = new BundleDetails(functionality.bundle(), functionality.title(), functionality.description());
        final ApplicationInfo app = apps.get(functionality.app());
        final FunctionalityInfo functionalityInfo =
                new FunctionalityInfo(app.getPath() + "/" + functionality.path(), functionality.group(), details);
        app.addFunctionality(functionalityInfo);
    }

    private void extractApp(Map<Class<?>, ApplicationInfo> apps, Class<?> app) {
        if (apps.containsKey(app)) {
            return;
        }
        Application application = app.getAnnotation(Application.class);
        if (application != null) {
            if (!hasAppMethod(app)) {
                throw new Error(String.format("Application class %s doesn't have app method.", app.getName()));
            }
            BundleDetails details = new BundleDetails(application.bundle(), application.title(), application.description());
            apps.put(app, new ApplicationInfo("render.do?f=" + application.path(), application.group(), details));
            actionMap.put(application.path(), new Forwarder(app.getAnnotation(Mapping.class).path() + ".do" + "?method=app",
                    application.group()));
        } else {
            throw new Error();
        }
    }

    private boolean hasAppMethod(Class<?> app) {
        for (Method method : app.getMethods()) {
            if ("app".equals(method.getName())) {
                return true;
            }
        }
        return false;
    }
}
