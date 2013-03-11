package pt.ist.bennu.renderers.example.domain;

import java.util.Locale;
import java.util.TreeSet;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.util.MultiLanguageString;
import pt.ist.bennu.service.Service;

public class Product extends Product_Base implements Comparable<Product> {

    public Product() {
        super();
        setHost(VirtualHost.getVirtualHostForThread());
    }

    public Product(MultiLanguageString name) {
        this();
        setName(name);
    }

    public static TreeSet<Product> getPossibleProducts() {
        if (VirtualHost.getVirtualHostForThread().getProductSet().isEmpty()) {
            initProducts();
        }
        return new TreeSet<>(VirtualHost.getVirtualHostForThread().getProductSet());
    }

    @Service
    private static void initProducts() {
        new Product(new MultiLanguageString().with(Locale.forLanguageTag("pt"), "Maçã")
                .with(Locale.forLanguageTag("en"), "Apple"));
        new Product(new MultiLanguageString().with(Locale.forLanguageTag("pt"), "Banana").with(Locale.forLanguageTag("en"),
                "Banana"));
        new Product(new MultiLanguageString().with(Locale.forLanguageTag("pt"), "Bróculos").with(Locale.forLanguageTag("en"),
                "Broccoli"));
        new Product(new MultiLanguageString().with(Locale.forLanguageTag("pt"), "Tomate").with(Locale.forLanguageTag("en"),
                "Tomato"));
    }

    @Override
    public int compareTo(Product o) {
        return getName().compareTo(o.getName());
    }
}
