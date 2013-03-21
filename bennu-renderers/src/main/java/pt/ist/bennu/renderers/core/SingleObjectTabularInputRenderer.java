package pt.ist.fenixWebFramework.renderers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectCollection;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;

/**
 * This renderer can be used to edit a single object in a tabular view as if
 * you were editing a collection with only the given object.
 * <p>
 * If you edit a collection with this renderer then it will behave exactly as the
 * {@link pt.ist.fenixWebFramework.renderers.TabularInputRenderer}.
 * 
 * @author cfgi
 */
public class SingleObjectTabularInputRenderer extends TabularInputRenderer {

    @Override
    public HtmlComponent render(Object object, Class type) {
        if (object instanceof Collection) {
            return super.render(object, type);
        } else {
            List list = new ArrayList();
            list.add(object);

            MetaObjectCollection multipleMetaObject = MetaObjectFactory.createObjectCollection();
            multipleMetaObject.add(getInputContext().getMetaObject());

            getInputContext().setMetaObject(multipleMetaObject);

            return super.render(list, list.getClass());
        }
    }
}
