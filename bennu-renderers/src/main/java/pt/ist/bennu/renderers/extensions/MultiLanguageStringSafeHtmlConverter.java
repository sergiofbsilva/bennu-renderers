package pt.ist.bennu.renderers.extensions;

import pt.ist.bennu.renderers.core.components.converters.Converter;
import pt.ist.bennu.renderers.extensions.MultiLanguageStringInputRenderer.MultiLanguageStringConverter;
import pt.ist.bennu.renderers.extensions.htmlEditor.JsoupSafeHtmlConverter;
import pt.ist.bennu.renderers.util.Language;
import pt.ist.bennu.renderers.util.MultiLanguageString;

public class MultiLanguageStringSafeHtmlConverter extends Converter {

    private final boolean mathJaxEnabled;

    public MultiLanguageStringSafeHtmlConverter(final boolean mathJaxEnabled) {
        this.mathJaxEnabled = mathJaxEnabled;
    }

    @Override
    public Object convert(Class type, Object value) {
        // SafeHtmlConverter safeConverter = new SafeHtmlConverter();
        Converter safeConverter = new JsoupSafeHtmlConverter(mathJaxEnabled);
        MultiLanguageStringConverter mlsConverter = new MultiLanguageStringConverter();

        MultiLanguageString mls = (MultiLanguageString) mlsConverter.convert(type, value);

        if (mls == null) {
            return null;
        }

        if (mls.getAllLanguages().isEmpty()) {
            return null;
        }

        for (Language language : mls.getAllLanguages()) {
            String text = (String) safeConverter.convert(String.class, mls.getContent(language));

            if (text == null) {
                mls.removeContent(language);
            } else {
                mls.setContent(language, text);
            }
        }

        return mls;
    }

}
