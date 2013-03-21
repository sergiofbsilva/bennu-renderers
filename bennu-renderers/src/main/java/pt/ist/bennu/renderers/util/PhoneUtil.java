package pt.ist.bennu.renderers.util;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class PhoneUtil {
    private static final Collection<PhoneNumberType> FIXED_NUMBERS;
    private static final Collection<PhoneNumberType> MOBILE_NUMBERS;

    private static final int ALAMEDA_PHONE = 218416000;
    private static final int TAGUS_PHONE_000 = 214233200;
    private static final int TAGUS_PHONE_100 = 214233500;

    static {
        FIXED_NUMBERS = new ArrayList<PhoneNumberType>();
        FIXED_NUMBERS.add(PhoneNumberType.VOIP);
        FIXED_NUMBERS.add(PhoneNumberType.FIXED_LINE);

        MOBILE_NUMBERS = new ArrayList<PhoneNumberType>();
        MOBILE_NUMBERS.add(PhoneNumberType.MOBILE);
    }

    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();
    private static final String COUNTRY_CODE = "PT";

    public static PhoneNumber getPhoneNumber(String numberText) {
        if (!StringUtils.isEmpty(numberText)) {

            if (numberText.startsWith("00")) {
                numberText = numberText.replaceFirst("00", "+");
            }

            if (isExtension(numberText)) {
                numberText = getExternalNumberForExtension(numberText);
            }

            try {
                final PhoneNumber phoneNumber = PHONE_UTIL.parse(numberText, COUNTRY_CODE);
                if (PHONE_UTIL.isValidNumber(phoneNumber)) {
                    return phoneNumber;
                }
            } catch (NumberParseException e) {
                System.out.println("O número não é válido:" + e);
                return null;
            }
        }
        return null;
    }

    private static PhoneNumberType getPhoneNumberType(PhoneNumber phoneNumber) {
        return phoneNumber != null ? PHONE_UTIL.getNumberType(phoneNumber) : null;
    }

    public static boolean isValidNumber(String numberText) {
        final PhoneNumber phoneNumber = getPhoneNumber(numberText);
        return phoneNumber != null;
    }

    private static boolean isType(PhoneNumberType type, Collection<PhoneNumberType> types) {
        return types.contains(type);
    }

    public static boolean isMobileNumber(String numberText) {
        return isType(getPhoneNumberType(getPhoneNumber(numberText)), MOBILE_NUMBERS);
    }

    public static boolean isPortugueseNumber(String numberText) {
        final PhoneNumber phoneNumber = getPhoneNumber(numberText);
        if (phoneNumber != null) {
            return phoneNumber.getCountryCode() == 351;
        }
        return false;
    }

    public static boolean isFixedNumber(String numberText) {
        return isType(getPhoneNumberType(getPhoneNumber(numberText)), FIXED_NUMBERS);
    }

    public static String getExternalNumberForExtension(String numberText) {
        int extension;
        try {
            extension = Integer.parseInt(numberText);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if (extension >= 1000 && extension <= 3999) {
            return new Integer(ALAMEDA_PHONE + extension).toString();
        } else {
            if (extension >= 5000 && extension <= 5099) {
                extension -= 5000;
                return new Integer(TAGUS_PHONE_000 + extension).toString();
            } else if (extension >= 5100 && extension <= 5199) {
                extension -= 5100;
                return new Integer(TAGUS_PHONE_100 + extension).toString();
            }
        }
        return null;
    }

    public static String getInternacionalFormatNumber(String numberText) {

        if (isExtension(numberText)) {
            numberText = getExternalNumberForExtension(numberText);
        }

        final PhoneNumber phoneNumber = getPhoneNumber(numberText);
        if (phoneNumber != null) {
            return PHONE_UTIL.format(phoneNumber, PhoneNumberFormat.E164);
        }

        return null;
    }

    private static boolean isExtension(String numberText) {
        return getExternalNumberForExtension(numberText) != null;
    }

    public static int getCountry(String numberText) {
        final PhoneNumber phoneNumber = getPhoneNumber(numberText);
        if (phoneNumber != null) {
            return phoneNumber.getCountryCode();
        }
        return -1;
    }
}
