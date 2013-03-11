package pt.ist.bennu.renderers.actions;

import org.apache.struts.action.ActionForward;

/**
 * 
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
public interface Context {
    public ActionForward forward(String forward);
}
