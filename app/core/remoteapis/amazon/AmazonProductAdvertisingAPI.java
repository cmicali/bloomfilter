package core.remoteapis.amazon;

import core.util.XmlFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import play.libs.WS;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hshu
 * Date: 4/29/11
 * Time: 8:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class AmazonProductAdvertisingAPI {
    
    final static Logger log = LoggerFactory.getLogger(AmazonProductAdvertisingAPI.class);
    
    private static final String END_OF_INPUT = "\\Z";

    private static String requestParams2SignedURL(Map<String, String> params) {
        String url = null;
        try {
            SignedRequestsHelper signedRequestsHelper = new SignedRequestsHelper();
            url = signedRequestsHelper.sign(params);
        } catch (Exception e) {
            //todo
            // setErrorMessage("signedRequestHelper throw: " + e.toString());
        }

        return url;
    }



    public static List<AmazonProduct> searchItems(String query) throws IOException, SAXException, XPathExpressionException, ParserConfigurationException {

        List<AmazonProduct> resultProductList = new ArrayList<AmazonProduct>();

        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Operation", "searchItems");
        params.put("SearchIndex", "All");
        //params.put("SearchIndex", "Blended");
        //params.put("SearchIndex", "Books");
        //params.put("Keywords", "Harry Potter");
        params.put("Keywords", query);
        //params.put("Style", "http://ecs.amazonaws.com/xsl/aws4/item-search.xsl");

        String url = requestParams2SignedURL(params);
        log.debug("signed amazon url:\n" + url);

        WS.HttpResponse resp = WS.url(url).get();

        Document doc = resp.getXml();

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr
                = xpath.compile("//RequestProcessingTime/text()");
        //= xpath.compile("//Item/ASIN/text()");

        String requestProcessingTime = xpath.evaluate("//RequestProcessingTime/text()", doc);
        log.debug("Processing Time:" + requestProcessingTime);

        log.debug("TotalResults:" + xpath.evaluate("//Items/TotalResults", doc));
        int numItems = Integer.parseInt(xpath.evaluate("//Items/TotalResults", doc));

        String xpathExpr;
        if (numItems > 10) {
            xpathExpr = "//Items/Item[position()<11]";
        } else {
            //xpathExpr = "//Items/item[position()<" + String.valueOf(numItems + 1) + "]";
            xpathExpr = "//Items/Item";
        }

        // List<Product> list;
        //Product oneProduct = ProductManager.createNewProduct();
        //list.add(list);

        XPathExpression itemsExpr = xpath.compile(xpathExpr);
        Object xpathResult = itemsExpr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) xpathResult;
        for (int i = 0; i < nodes.getLength(); i++) {
            AmazonProduct oneProduct = new AmazonProduct();

            Element itemElement = (Element) nodes.item(i);
            String ASIN = itemElement.getElementsByTagName("ASIN").item(0).getTextContent();
            log.debug("");
            log.debug(ASIN);
            oneProduct.asin = ASIN;

            Element itemAttributesElement = (Element) itemElement.getElementsByTagName("ItemAttributes").item(0);
            oneProduct.name = itemAttributesElement.getElementsByTagName("Title").item(0).getTextContent();

            oneProduct.productGroup = itemAttributesElement.getElementsByTagName("ProductGroup").item(0).getTextContent();

            //image from amazon has the following rule: http://aaugh.com/imageabuse.html
            //String imageURL = "http://ecw.images-amazon.com/images/P/" + ASIN + ".01." + "_PE20_SCMZZZZZZZ_.jpg";
            String imageURL = "http://images.amazon.com/images/P/" + ASIN + ".01." + "THUMBZZZ.jpg";
            //THUMBZZZ
            log.debug(imageURL);
            oneProduct.pictureUrl = imageURL;

            resultProductList.add(oneProduct);
        }

        return resultProductList;
    }

    /*
    // lookup an item based on ASIN
    public static List<Product> itemLookup(String ASINin) throws IOException, SAXException, XPathExpressionException, ParserConfigurationException {

        List<Product> resultProductList = new ArrayList<Product>();

        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Operation", "itemLookup");
        //params.put("SearchIndex", "All");
        //params.put("SearchIndex", "Blended");
        //params.put("SearchIndex", "Books");
        //params.put("Keywords", "Harry Potter");
        params.put("ItemId", ASINin);
        params.put("Version", "2009-11-01");
        params.put("ResponseGroup", "ItemAttributes,Reviews,EditorialReview");
        //params.put("Style", "http://ecs.amazonaws.com/xsl/aws4/item-search.xsl");

        String url = requestParams2SignedURL(params);
        log.debug("signed amazon url:\n" + url);

        String xmlResult = retrieveURL(url);
        log.debug("xmlResult:\n" + new XmlFormatter().format(xmlResult));

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        //domFactory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder = domFactory.newDocumentBuilder();

        org.xml.sax.InputSource inStream = new org.xml.sax.InputSource();
        inStream.setCharacterStream(new java.io.StringReader(xmlResult));
        Document doc = builder.parse(inStream);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr
                = xpath.compile("//RequestProcessingTime/text()");
        //= xpath.compile("//Item/ASIN/text()");

        String requestProcessingTime = xpath.evaluate("//RequestProcessingTime/text()", doc);
        log.debug("Processing Time:" + requestProcessingTime);

        boolean isValid = Boolean.parseBoolean(xpath.evaluate("//Items/Request/IsValid", doc));
        log.debug("IsValid:" + isValid);

        if (isValid) {

//            // todo: doing google product api first, will parse result later
//            String xpathExpr;
//            if (numItems > 10) {
//                xpathExpr = "//Items/Item[position()<11]";
//            } else {
//                //xpathExpr = "//Items/item[position()<" + String.valueOf(numItems + 1) + "]";
//                xpathExpr = "//Items/Item";
//            }
//
//            XPathExpression itemsExpr = xpath.compile(xpathExpr);
//            Object xpathResult = itemsExpr.evaluate(doc, XPathConstants.NODESET);
//            NodeList nodes = (NodeList) xpathResult;
//            for (int i = 0; i < nodes.getLength(); i++) {
//                Product oneProduct = new Product();
//
//                Element itemElement = (Element) nodes.item(i);
//                String ASIN = itemElement.getElementsByTagName("ASIN").item(0).getTextContent();
//                log.debug();
//                log.debug(ASIN);
//                oneProduct.setAmazonAsin(ASIN);
//
//                Element itemAttributesElement = (Element) itemElement.getElementsByTagName("ItemAttributes").item(0);
//                oneProduct.setName(itemAttributesElement.getElementsByTagName("Title").item(0).getTextContent());
//                log.debug(oneProduct.getName());
//
//                oneProduct.setAmazonProductGroup(itemAttributesElement.getElementsByTagName("ProductGroup").item(0).getTextContent());
//                log.debug(oneProduct.getAmazonProductGroup());
//
//                //image from amazon has the following rule: http://aaugh.com/imageabuse.html
//                //String imageURL = "http://ecw.images-amazon.com/images/P/" + ASIN + ".01." + "_PE20_SCMZZZZZZZ_.jpg";
//                String imageURL = "http://images.amazon.com/images/P/" + ASIN + ".01." + "THUMBZZZ.jpg";
//                //THUMBZZZ
//                log.debug(imageURL);
//                oneProduct.setPictureUrl(imageURL);
//
//                resultProductList.add(oneProduct);
//            }

        }
        return resultProductList;
    }
    */
    
    /**
     * Test method for {@link }.
     *
     * @throws Exception
    @Test
    public void testItemSearch
    () throws Exception {

        List<Product> amazonProductList = AmazonProductAdvertisingAPI.searchItems("harry potter");

        for (int i = 0; i < amazonProductList.size(); i++) {
            log.debug(amazonProductList.get(i).getName());
            log.debug(amazonProductList.get(i).getAmazonAsin());
            log.debug(amazonProductList.get(i).getAmazonProductGroup());
        }
    }

    @Test
    public void testItemLookup
            () throws Exception {
        List<Product> amazonProductList = AmazonProductAdvertisingAPI.itemLookup("B004MKM8L8");


    }
     */
}