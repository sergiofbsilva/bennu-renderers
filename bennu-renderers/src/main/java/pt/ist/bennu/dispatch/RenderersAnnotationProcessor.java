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
        if (!apps.containsKey(functionality.app())) {
            extractApp(apps, functionality.app());
        }
        apps.get(functionality.app()).addFunctionality(
                new FunctionalityInfo(functionality.bundle(), functionality.title(), functionality.description(), functionality
                        .path(), functionality.group()));
    }

    private void extractApp(Map<Class<?>, ApplicationInfo> apps, Class<?> app) {
        Application application = app.getAnnotation(Application.class);
        if (application != null) {
            apps.put(app, new ApplicationInfo(application.bundle(), application.title(), application.description(),
                    "render.do?f=" + application.path(), application.group()));
            actionMap
                    .put(application.path(), new Forwarder(app.getAnnotation(Mapping.class).path() + ".do", application.group()));
        } else {
            throw new Error();
        }
    }
}
