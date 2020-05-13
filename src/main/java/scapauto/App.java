package scapauto;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.ArrayList;
import java.util.List;

public class App
{
    // Const Field

    private static final WebClient client = new WebClient();

    // Field

    private final List<Auto> autos = new ArrayList<>();
    private final List<String> tradeMarks = new ArrayList<>();

    // Construct

    App()
    {
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        fillTradeMarks();
        showTradeMarks();

        try {
            HtmlPage page = client.getPage("https://www.autoevolution.com/acura/");
            HtmlElement pageWrapper = page.getHtmlElementById("newscol2");
            List<HtmlElement> modelsInProduction = pageWrapper.getByXPath("//div[@class='carmod clearfix ']");
            List<HtmlElement> modelsDiscontinue = pageWrapper.getByXPath("//div[@class='carmod clearfix disc']");

            fillListAutos(modelsInProduction);
            fillListAutos(modelsDiscontinue);

            showAutos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methods

    private void fillTradeMarks()
    {
        try {
            HtmlPage page = client.getPage("https://www.autoevolution.com/cars/");
            HtmlElement pageWrapper = page.getHtmlElementById("pagewrapper");
            List<HtmlElement> tradeMarksElement = pageWrapper.getByXPath(
                    "//div[@class='col2width fl bcol-white carman'] " +
                            "[@itemscope] [@itemtype='https://schema.org/Brand']");

            for (var element : tradeMarksElement) {
                HtmlElement tradeMarkH5 = element.getFirstByXPath("./h5/a[@*]/span[@itemprop='name']");
                tradeMarks.add(tradeMarkH5.asText().toLowerCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillListAutos(List<HtmlElement> elements)
    {
        for (var element : elements) {
            HtmlElement nameElement = element.getFirstByXPath(".//h4");
            String nameModel = nameElement.asText();

            HtmlElement typeElement = element.getFirstByXPath(".//p[@class='body']");
            String typeModel = typeElement.asText();

            List<HtmlElement> enginesElement = element.getByXPath(".//p[@class='eng']/span");
            StringBuilder typeEngines = new StringBuilder(40);

            if (enginesElement.size() > 1) {
                for (var engine : enginesElement) {
                    typeEngines.append(engine.asText()).append(" & ");
                }

                // Delete the " & " extra that was added.
                typeEngines.delete(typeEngines.length() - 3, typeEngines.length());
            } else {
                typeEngines.append(enginesElement.get(0).asText());
            }

            autos.add(new Auto(nameModel, typeModel, typeEngines.toString()));
        }
    }

    private void showAutos()
    {
        for (var auto : autos) {
            System.out.println(auto.toString());
        }
    }

    private void showTradeMarks()
    {
        for (var tradeMark : tradeMarks) {
            System.out.println(tradeMark);
        }
    }

    // Main

    public static void main(String[] args)
    {
        new App();
    }
}