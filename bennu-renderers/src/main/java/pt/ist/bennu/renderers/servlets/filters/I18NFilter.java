/*
 * Created on 2005/05/13
 * 
 */
package pt.ist.bennu.renderers.servlets.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;

import pt.ist.bennu.core.i18n.I18N;

/**
 * 
 * @author Luis Cruz
 */
public class I18NFilter implements Filter {
    @Override
    public void init(final FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpSession session = ((HttpServletRequest) request).getSession(true);
        session.removeAttribute(Globals.LOCALE_KEY);
        session.setAttribute(Globals.LOCALE_KEY, I18N.getLocale());
        request.removeAttribute(Globals.LOCALE_KEY);
        request.setAttribute(Globals.LOCALE_KEY, I18N.getLocale());
        chain.doFilter(request, response);
    }
}
