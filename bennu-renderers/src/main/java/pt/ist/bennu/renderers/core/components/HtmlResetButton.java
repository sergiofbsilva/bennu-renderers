package pt.ist.bennu.renderers.core.components;

public class HtmlResetButton extends HtmlInputButton {

    public HtmlResetButton() {
        super("reset");
    }

    public HtmlResetButton(String text) {
        this();

        setText(text);
    }

}
