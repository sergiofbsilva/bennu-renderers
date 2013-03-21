package pt.ist.bennu.renderers.example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.dispatch.Application;
import pt.ist.bennu.dispatch.Functionality;
import pt.ist.bennu.renderers.actions.ContextBaseAction;
import pt.ist.bennu.renderers.annotation.Mapping;
import pt.ist.bennu.renderers.core.utils.RenderUtils;
import pt.ist.bennu.renderers.example.domain.ShoppingList;
import pt.ist.bennu.renderers.example.domain.ShoppingListItem;

@Mapping(path = "/shopping")
@Application(path = "shopping", bundle = "resources.ExampleResources", description = "title.example.shoppinglist.description",
        title = "title.example.shoppinglist")
public class ShoppingListApp extends ContextBaseAction {

    @Functionality(app = ShoppingListApp.class, path = "list", bundle = "resources.ExampleResources",
            description = "title.example.shoppinglist.list.description", title = "title.example.shoppinglist.list")
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("list", VirtualHost.getVirtualHostForThread().getShoppingListSet());
        return forward(request, "/example/shoppinglist.jsp");
    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return forward(request, "/example/createShoppinglist.jsp");
    }

    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        ShoppingList list = getDomainObject(request, "listId");
        request.setAttribute("list", list);
        return forward(request, "/example/viewShoppinglist.jsp");
    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        ShoppingList list = getDomainObject(request, "listId");
        return list(mapping, form, request, response);
    }

    public ActionForward addItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        ShoppingListItem item = getRenderedObject("create");
        RenderUtils.invalidateViewState();
        request.setAttribute("list", item.getShoppingList());
        return forward(request, "/example/viewShoppinglist.jsp");
    }

    public ActionForward app(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return list(mapping, form, request, response);
    }
}
