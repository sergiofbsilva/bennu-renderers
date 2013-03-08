package pt.ist.bennu.renderers.annotation;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.renderers.struts.StrutsAnnotationsPlugIn;

@HandlesTypes({ Mapping.class })
public class RenderersAnnotationProcessor implements ServletContainerInitializer {
    private static final Logger logger = LoggerFactory.getLogger(RenderersAnnotationProcessor.class);

    @Override
    @SuppressWarnings("unchecked")
    public void onStartup(Set<Class<?>> classes, ServletContext context) throws ServletException {
        if (classes != null) {
            for (Class<?> type : classes) {
                Mapping mapping = type.getAnnotation(Mapping.class);
                if (mapping != null) {
                    StrutsAnnotationsPlugIn.registerMapping(type);
                }
            }
        }
    }
}
