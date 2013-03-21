package pt.ist.fenixWebFramework.renderers.components;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlCheckBox extends HtmlInputComponent {

    private String text;

    private boolean checked;

    public HtmlCheckBox() {
        super("checkbox");

        this.checked = false;
    }

    public HtmlCheckBox(boolean checked) {
        this();

        this.checked = checked;
    }

    public HtmlCheckBox(String text) {
        this();

        this.text = text;
    }

    public HtmlCheckBox(String text, boolean checked) {
        this();

        this.text = text;
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserValue() {
        return super.getValue();
    }

    public void setUserValue(String userValue) {
        super.setValue(userValue);
    }

    @Override
    public String getValue() {
        if (getUserValue() != null) {
            return getUserValue();
        } else {
            return String.valueOf(isChecked());
        }
    }

    @Override
    public void setValue(String value) {
        setChecked(value != null);
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        if (isChecked()) {
            tag.setAttribute("checked", "checked");
        }

        if (getText() == null) {
            return tag;
        }

        HtmlTag span = new HtmlTag("span");

        span.addChild(tag);
        span.addChild(new HtmlTag(null, getText()));

        return span;
    }
}
