package nl.hsac.fitnesse.fixture.slim;

import nl.hsac.fitnesse.fixture.util.LineEndingHelper;

/**
 * Fixture to generate strings using Freemarker templates. These strings can be stored in variables so the can
 * be passed as arguments to methods of other fixtures.
 * This fixture can be used using Slim's dynamic decision tables or using scripts (and scenarios).
 */
public class FreemarkerFixture extends SlimFixtureWithMap {
    private final String defaultTemplate;
    private String targetLineEndings;

    /**
     * Creates new without default template.
     */
    public FreemarkerFixture() {
        this(null);
    }

    /**
     * Creates new with a default template, this is used in dynamic decision tables to prevent having to specify
     * the template in the header row (any output column just gets the result of applying this templates).
     * @param defaultTemplate location (below /templates/) of Freemarker template.
     */
    public FreemarkerFixture(String defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    /**
     * Applies template to current values.
     * @param aTemplate name of template file (relative to 'testdata' directory on classpath)
     * @return template result.
     */
    public String applyTemplate(String aTemplate) {
        String result = getEnvironment().processTemplate(aTemplate, getCurrentValues());
        result = postProcess(result);
        result = formatResult(aTemplate, result);
        return result;
    }

    protected String postProcess(String result) {
        if (targetLineEndings != null) {
            return getEnvironment().getLineEndingHelper().convertEndingsTo(result, targetLineEndings);
        } else {
            return result;
        }
    }

    protected String formatResult(String aTemplate, String result) {
        try {
            if (aTemplate.contains(".xml")) {
                result = getEnvironment().getHtmlForXml(result);
            } else if (aTemplate.contains(".json")) {
                result = getEnvironment().getHtmlForJson(result);
            }
        } catch (Exception e) {
            // can not be formatted, return raw result
        }
        return result;
    }

    //// methods to support usage in dynamic decision tables

    public String get(String requestedValue) {
        String result;
        if (defaultTemplate == null) {
            result = applyTemplate(requestedValue);
        } else {
            result = applyTemplate(defaultTemplate);
        }
        return result;
    }

    //// end: methods to support usage in dynamic decision tables

    public void convertTemplateLineEndingsToWindows() {
        targetLineEndings = LineEndingHelper.WIN_LINE_ENDING;
    }

    public void convertTemplateLineEndingsToUnix() {
        targetLineEndings = LineEndingHelper.UNIX_LINE_ENDING;
    }

    public void doNotconvertTemplateLineEndings() {
        targetLineEndings = null;
    }
}
