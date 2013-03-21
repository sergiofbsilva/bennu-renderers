package pt.ist.bennu.renderers.extensions;

import pt.ist.bennu.renderers.core.EnumRadioInputRenderer;
import pt.ist.bennu.renderers.core.components.HtmlComponent;
import pt.ist.bennu.renderers.core.components.HtmlHiddenField;
import pt.ist.bennu.renderers.core.components.HtmlInlineContainer;
import pt.ist.bennu.renderers.core.components.HtmlRadioButton;
import pt.ist.bennu.renderers.core.components.HtmlRadioButtonList;
import pt.ist.bennu.renderers.core.components.controllers.HtmlController;
import pt.ist.bennu.renderers.core.components.state.IViewState;
import pt.ist.bennu.renderers.core.components.state.ViewDestination;
import pt.ist.bennu.renderers.core.layouts.Layout;
import pt.ist.bennu.renderers.core.model.MetaSlot;

public class EnumRadioInputRendererWithPostBack extends EnumRadioInputRenderer {

    private String destination;
    private final String HIDDEN_NAME = "postback";

    public String getDestination() {
        return destination;
    }

    /**
     * Allows to choose the postback destination. If this property is not
     * specified the default "postback" destination is used.
     * 
     * @property
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        final Layout layout = super.getLayout(object, type);

        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                HtmlInlineContainer container = new HtmlInlineContainer();

                String prefix =
                        HtmlComponent.getValidIdOrName(((MetaSlot) getInputContext().getMetaObject()).getKey().toString()
                                .replaceAll("\\.", "_").replaceAll("\\:", "_"));

                HtmlHiddenField hidden = new HtmlHiddenField(prefix + HIDDEN_NAME, "");
                hidden.setController(new PostBackController(hidden, getDestination()));

                HtmlRadioButtonList radioButtonList = (HtmlRadioButtonList) layout.createComponent(object, type);

                for (HtmlRadioButton button : radioButtonList.getRadioButtons()) {
                    button.setOnClick("this.form." + prefix + HIDDEN_NAME + ".value='true';this.form.submit();");
                    button.setOnDblClick("this.form." + prefix + HIDDEN_NAME + ".value='true';this.form.submit();");

                }

                if (getClasses() != null) {
                    applyStyle(radioButtonList);
                }

                container.addChild(hidden);
                container.addChild(radioButtonList);

                return container;
            }

        };
    }

    private static class PostBackController extends HtmlController {

        private final HtmlHiddenField hidden;

        private final String destination;

        public PostBackController(HtmlHiddenField hidden, String destination) {
            this.hidden = hidden;
            this.destination = destination;
        }

        @Override
        public void execute(IViewState viewState) {
            if (hidden.getValue() != null && hidden.getValue().length() != 0) {
                hidden.setValue(null);

                String destinationName = this.destination == null ? "postBack" : this.destination;
                ViewDestination destination = viewState.getDestination(destinationName);

                if (destination != null) {
                    viewState.setCurrentDestination(destination);
                } else {
                    viewState.setCurrentDestination("postBack");
                }

                viewState.setSkipValidation(true);
            }

        }

    }
}
