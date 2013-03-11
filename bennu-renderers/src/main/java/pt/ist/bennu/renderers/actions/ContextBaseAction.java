/*
 * @(#)ContextBaseAction.java
 * 
 * Copyright 2009 Instituto Superior Tecnico Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 * https://fenix-ashes.ist.utl.pt/
 * 
 * This file is part of the Bennu Web Application Infrastructure.
 * 
 * The Bennu Web Application Infrastructure is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * Bennu is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Bennu. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.renderers.actions;

import java.io.Serializable;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.bennu.core.i18n.I18N;

/**
 * 
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
public abstract class ContextBaseAction extends BaseAction {
    public static final String CONTEXT = "_CONTEXT_";

    public static final String LOCALE_BEAN = "localeBean";

    public static class LocaleBean implements Serializable {
        private static final long serialVersionUID = -4880869280578139842L;

        private Locale language;

        public LocaleBean() {
            language = I18N.getLocale();
        }

        public Locale getLanguage() {
            return language;
        }

        public void setLanguage(Locale language) {
            this.language = language;
        }
    }

    @Override
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        request.setAttribute(LOCALE_BEAN, new LocaleBean());
        return super.execute(mapping, form, request, response);
    }

    public static ActionForward forward(final HttpServletRequest request, final String forward) {
        DefaultContext context = new DefaultContext();
        request.setAttribute(CONTEXT, context);
        return context.forward(forward);
    }
}
