package pt.ist.fenixWebFramework.renderers.components;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlTableHeader extends HtmlComponent {

    private List<HtmlTableRow> rows;

    public HtmlTableHeader() {
        rows = new ArrayList<HtmlTableRow>();
    }

    public HtmlTableRow createRow() {
        HtmlTableRow row = new HtmlTableHeaderRow();

        this.rows.add(row);
        return row;
    }

    @Override
    public List<HtmlComponent> getChildren() {
        return new ArrayList<HtmlComponent>(rows);
    }

    public List<HtmlTableRow> getRows() {
        return this.rows;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("thead");

        for (HtmlTableRow row : this.rows) {
            tag.addChild(row.getOwnTag(context));
        }

        return tag;
    }
}
