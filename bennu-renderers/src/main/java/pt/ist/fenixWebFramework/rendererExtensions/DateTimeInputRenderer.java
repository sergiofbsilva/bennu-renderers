package pt.ist.fenixWebFramework.rendererExtensions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.util.RequestUtils;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.renderers.InputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlHiddenField;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlController;
import pt.ist.fenixWebFramework.renderers.components.converters.ConversionException;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.converters.DateConverter;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * This renderers provides a standard input presentation for a <code>DateTime</code> from the Joda framework. The user is aked for
 * a date an
 * hour and the minutes in three separate input fields.
 * 
 * <p>
 * Example: <input type="text"/> <input type="text" size="2"/>hh:<input type="text" size="2"/>mm
 * 
 * @author cfgi
 */
public class DateTimeInputRenderer extends InputRenderer {

    private String dateFormat;

    private String dateFormatText;
    private String dateBundle;
    private boolean dateKey;

    private String dateSize;
    private Integer dateMaxLength;

    public String getDateFormat() {
        return this.dateFormat == null ? DateConverter.DEFAULT_FORMAT : dateFormat;
    }

    /**
     * The format in which the date should be displayed. The format can have the
     * form accepted by <a href=
     * "http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html"
     * >SimpleDateFormat</a>
     * 
     * <p>
     * The default format is {@value DateConverter#DEFAULT_FORMAT}.
     * 
     * @property
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDateFormatText() {
        return this.dateFormatText;
    }

    /**
     * By default the value of <code>dateFormat</code> is used to show to the
     * user how to write the date. This property allows you to override that
     * default. This means that the value of the property will be shown instead.
     * 
     * @property
     */
    public void setDateFormatText(String dateFormatText) {
        this.dateFormatText = dateFormatText;
    }

    public String getDateBundle() {
        return this.dateBundle;
    }

    /**
     * When the value of the <code>dateFormatText</code> is a key this property
     * indicates the name of the bundle where the key will be looked for.
     * 
     * @property
     */
    public void setDateBundle(String dateBundle) {
        this.dateBundle = dateBundle;
    }

    public boolean isDateKey() {
        return this.dateKey;
    }

    /**
     * Indicates the the value of the <code>dateFormatText</code> property is a
     * key and not the text itself.
     * 
     * @property
     */
    public void setDateKey(boolean dateKey) {
        this.dateKey = dateKey;
    }

    public Integer getDateMaxLength() {
        return this.dateMaxLength;
    }

    /**
     * Sets the max length of the date field.
     * 
     * @property
     */
    public void setDateMaxLength(Integer dateMaxLength) {
        this.dateMaxLength = dateMaxLength;
    }

    public String getDateSize() {
        return this.dateSize;
    }

    /**
     * Chooses the size of the date field.
     * 
     * @property
     */
    public void setDateSize(String dateSize) {
        this.dateSize = dateSize;
    }

    protected Locale getLocale() {
        HttpServletRequest request = getInputContext().getViewState().getRequest();
        return RequestUtils.getUserLocale(request, null);
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new DateTimeLayout();
    }

    public class DateTimeLayout extends Layout {
        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            DateTime dateTime = (DateTime) object;

            Date date = null;
            Calendar calendar = null;

            if (dateTime != null) {
                date = dateTime.toGregorianCalendar().getTime();
                calendar = Calendar.getInstance();
                calendar.setTime(date);
            }

            MetaSlotKey key = (MetaSlotKey) getInputContext().getMetaObject().getKey();

            HtmlInlineContainer container = new HtmlInlineContainer();

            HtmlHiddenField hiddenField = new HtmlHiddenField();
            hiddenField.setTargetSlot(key);
            container.addChild(hiddenField);

            HtmlTextInput dateField = new HtmlTextInput();
            dateField.setName(key.toString() + "_date");
            dateField.setSize(getDateSize());
            dateField.setMaxLength(getDateMaxLength());
            container.addChild(dateField);

            container.addChild(new HtmlText(getFormatLabel()));

            Locale locale = getLocale();
            SimpleDateFormat dateFormat = new SimpleDateFormat(getDateFormat(), locale);

            if (date != null) {
                dateField.setValue(dateFormat.format(date));
            }

            HtmlTextInput hoursField = new HtmlTextInput();
            hoursField.setName(key.toString() + "_hours");
            hoursField.setSize("2");
            hoursField.setMaxLength(2);
            container.addChild(hoursField);

            if (calendar != null) {
                hoursField.setValue(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)));
            }

            container.addChild(new HtmlText("hh"));

            HtmlTextInput minutesField = new HtmlTextInput();
            minutesField.setName(key.toString() + "_minutes");
            minutesField.setSize("2");
            minutesField.setMaxLength(2);
            container.addChild(minutesField);

            if (calendar != null) {
                minutesField.setValue(String.format("%02d", calendar.get(Calendar.MINUTE)));
            }

            container.addChild(new HtmlText("mm"));

            hiddenField.setConverter(new DateTimeConverter(dateField, hoursField, minutesField));
            minutesField.setController(new DateTimeController(hiddenField, dateFormat, dateField, hoursField, minutesField));

            return container;
        }

        protected String getFormatLabel() {
            if (isDateKey()) {
                return RenderUtils.getResourceString(getDateBundle(), getDateFormatText());
            } else {
                if (getDateFormatText() != null) {
                    return getDateFormatText();
                } else {
                    return getDateFormat();
                }
            }
        }
    }

    private class DateTimeController extends HtmlController {

        private HtmlHiddenField valueField;
        private DateFormat dateFormat;
        private HtmlTextInput dateField;
        private HtmlTextInput hoursField;
        private HtmlTextInput minutesField;

        public DateTimeController(HtmlHiddenField hiddenField, DateFormat dateFormat, HtmlTextInput dateField,
                HtmlTextInput hoursField, HtmlTextInput minutesField) {
            this.valueField = hiddenField;
            this.dateFormat = dateFormat;
            this.dateField = dateField;
            this.hoursField = hoursField;
            this.minutesField = minutesField;
        }

        @Override
        public void execute(IViewState viewState) {
            DateConverter converter = new DateConverter(this.dateFormat);

            try {
                Date date = (Date) converter.convert(Date.class, this.dateField.getValue());

                if (date == null) {
                    if ((this.hoursField.getValue() != null && this.hoursField.getValue().length() > 0)
                            || (this.minutesField.getValue() != null && this.minutesField.getValue().length() > 0)) {
                        this.valueField.setValue(DateTimeConverter.INCOMPLETE);
                    } else {
                        this.valueField.setValue("");
                    }

                    return;
                }

                String hours = DateTimeConverter.zeroPad(this.hoursField.getValue());
                String minutes = DateTimeConverter.zeroPad(this.minutesField.getValue());

                String value = String.format("%tFT%s:%s", date, hours, minutes);
                this.valueField.setValue(value);
            } catch (ConversionException e) {
                this.valueField.setValue(DateTimeConverter.INVALID);
            }
        }

    }

    public static class DateTimeConverter extends Converter {

        public static final String INVALID = "invalid";
        public static final String INCOMPLETE = "incomplete";

        private HtmlTextInput dateField;
        private HtmlTextInput hoursField;
        private HtmlTextInput minutesField;

        public DateTimeConverter(HtmlTextInput dateField, HtmlTextInput hoursField, HtmlTextInput minutesField) {
            this.dateField = dateField;
            this.hoursField = hoursField;
            this.minutesField = minutesField;
        }

        @Override
        public Object convert(Class type, Object value) {
            if (value == null || String.valueOf(value).length() == 0) {
                return null;
            }

            if (value.equals(INVALID)) {
                throw new ConversionException("fenix.renderers.converter.dateTime.invalid", true, this.dateField.getValue());
            }

            String hours = zeroPad(this.hoursField.getValue());
            String minutes = zeroPad(this.minutesField.getValue());

            if (value.equals(INCOMPLETE)) {
                throw new ConversionException("fenix.renderers.converter.dateTime.incomplete", true, hours, minutes);
            }

            try {
                return new DateTime(value);
            } catch (IllegalArgumentException e) {
                String date = dateField.getValue();

                throw new ConversionException("fenix.renderers.converter.dateTime.convert", e, true, date, hours, minutes);
            }
        }

        public static String zeroPad(String text) {
            if (text == null) {
                return "00";
            }

            return (text.length() < 2 ? "0" : "") + (text.length() < 1 ? "0" : "") + text;
        }

    }

}
