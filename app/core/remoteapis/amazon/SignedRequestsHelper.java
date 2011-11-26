package core.remoteapis.amazon;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SignedRequestsHelper {
    private static final String UTF8_CHARSET = "UTF-8";
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final String REQUEST_URI = "/onca/xml";
    private static final String REQUEST_METHOD = "GET";

    private String endpoint = "ecs.amazonaws.com"; // must be lowercase
    private String awsAccessKeyId = "0ND4ESTM52SFYWAQGMG2";
    private String awsSecretKey = "Z2JzenFUDW/eOihJnuOjqfNenXRINf8kUrrm2VmU";
    private String awsAssociateTag = "underdev03-20";

    private SecretKeySpec secretKeySpec = null;
    private Mac mac = null;

    public SignedRequestsHelper() throws Exception {
        byte[] secretKeyBytes = awsSecretKey.getBytes(UTF8_CHARSET);
        secretKeySpec =
                new SecretKeySpec(secretKeyBytes, HMAC_SHA256_ALGORITHM);
        mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(secretKeySpec);
    }

    public String sign(Map<String, String> params) {
        params.put("AWSAccessKeyId", awsAccessKeyId);
        params.put("AssociateTag", awsAssociateTag);
        if (!(params.containsKey("Version"))) {
            params.put("Version", "2010-11-01");
        }
        //params.put("Version","2009-01-01");
        params.put("Timestamp", timestamp());

        SortedMap<String, String> sortedParamMap =
                new TreeMap<String, String>(params);
        String canonicalQS = canonicalize(sortedParamMap);
        String toSign =
                REQUEST_METHOD + "\n"
                        + endpoint + "\n"
                        + REQUEST_URI + "\n"
                        + canonicalQS;

        String hmac = hmac(toSign);
        String sig = percentEncodeRfc3986(hmac);
        String url = "http://" + endpoint + REQUEST_URI + "?" +
                canonicalQS + "&Signature=" + sig;

        return url;
    }

    private String hmac(String stringToSign) {
        String signature = null;
        byte[] data;
        byte[] rawHmac;
        try {
            data = stringToSign.getBytes(UTF8_CHARSET);
            rawHmac = mac.doFinal(data);
            Base64 encoder = new Base64();
            signature = new String(encoder.encode(rawHmac));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
        }
        return signature;
    }

    private String timestamp() {
        String timestamp = null;
        Calendar cal = Calendar.getInstance();
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
        timestamp = dfm.format(cal.getTime());
        return timestamp;
    }

    private String canonicalize(SortedMap<String, String> sortedParamMap) {
        if (sortedParamMap.isEmpty()) {
            return "";
        }

        StringBuffer buffer = new StringBuffer();
        Iterator<Map.Entry<String, String>> iter =
                sortedParamMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, String> kvpair = iter.next();
            buffer.append(percentEncodeRfc3986(kvpair.getKey()));
            buffer.append("=");
            buffer.append(percentEncodeRfc3986(kvpair.getValue()));
            if (iter.hasNext()) {
                buffer.append("&");
            }
        }
        String cannoical = buffer.toString();
        return cannoical;
    }

    private String percentEncodeRfc3986(String s) {
        String out;
        try {
            out = URLEncoder.encode(s, UTF8_CHARSET)
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            out = s;
        }
        return out;
    }

    private static final String END_OF_INPUT = "\\Z";

    public static String prettyFormat(String input, int indent) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", new Integer(indent));
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e); // simple exception handling, please review it
        }
    }

    public static String prettyFormat(String input) {
        return prettyFormat(input, 2);
    }

//    /**
//     * Test method for {@link com.amazon.rest#getAmazonInfo(java.lang.String)}.
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testGetAmazonInfo() throws Exception {
//
//        SignedRequestsHelper signedRequestsHelper = new SignedRequestsHelper();
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("Service", "AWSECommerceService");
//        params.put("Operation", "searchItems");
//        //params.put("SearchIndex", "Books");
//        params.put("SearchIndex", "All");
//        params.put("Keywords", "Harry Potter");
//        //params.put("Style", "http://ecs.amazonaws.com/xsl/aws4/item-search.xsl");
//
//        String url = signedRequestsHelper.sign(params);
//        System.out.println("new url:\n" + url);
//
//        String xmlResult = null;
//        URLConnection connection = null;
//        URL fURL = new URL(url);
//        try {
//            connection = fURL.openConnection();
//            Scanner scanner = new Scanner(connection.getInputStream());
//            scanner.useDelimiter(END_OF_INPUT);
//            xmlResult = scanner.next();
//        } catch (IOException ex) {
//            System.out.println("Cannot open connection to " + fURL.toString());
//        }
//
//        System.out.println("xmlResult:\n" + xmlResult);
//
//        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
//        //domFactory.setNamespaceAware(true); // never forget this!
//        DocumentBuilder builder = domFactory.newDocumentBuilder();
//
//        org.xml.sax.InputSource inStream = new org.xml.sax.InputSource();
//        inStream.setCharacterStream(new java.io.StringReader(xmlResult));
//        Document doc = builder.parse(inStream);
//
//        XPathFactory factory = XPathFactory.newInstance();
//        XPath xpath = factory.newXPath();
//        XPathExpression expr
//                = xpath.compile("//RequestProcessingTime/text()");
//        //= xpath.compile("//Item/ASIN/text()");
//
//        String requestProcessingTime = xpath.evaluate("//RequestProcessingTime/text()", doc);
//        System.out.println("Processing Time:" + requestProcessingTime);
//
//        System.out.println("TotalResults:" + xpath.evaluate("//Items/TotalResults", doc));
//        int numItems = Integer.parseInt(xpath.evaluate("//Items/TotalResults", doc));
//
//        String xpathExpr;
//        if (numItems > 10) {
//            xpathExpr = "//Items/Item[position()<11]";
//        } else {
//            //xpathExpr = "//Items/item[position()<" + String.valueOf(numItems + 1) + "]";
//            xpathExpr = "//Items/Item";
//        }
//
//        // List<Product> list;
//        //Product oneProduct = ProductManager.createNewProduct();
//        //list.add(list);
//
//
//        XPathExpression itemsExpr = xpath.compile(xpathExpr);
//        Object xpathResult = itemsExpr.evaluate(doc, XPathConstants.NODESET);
//        NodeList nodes = (NodeList) xpathResult;
//        for (int i = 0; i < nodes.getLength(); i++) {
//            Product oneProduct = new Product();
//
//            Element itemElement = (Element) nodes.item(i);
//            String ASIN = itemElement.getElementsByTagName("ASIN").item(0).getTextContent();
//            System.out.println();
//            System.out.println(ASIN);
//            oneProduct.setAmazonAsin(ASIN);
//
//            Element itemAttributesElement = (Element) itemElement.getElementsByTagName("ItemAttributes").item(0);
//            oneProduct.setName(itemAttributesElement.getElementsByTagName("Title").item(0).getTextContent());
//            System.out.println(oneProduct.getName());
//
//            oneProduct.setAmazonProductGroup(itemAttributesElement.getElementsByTagName("ProductGroup").item(0).getTextContent());
//            System.out.println(oneProduct.getAmazonProductGroup());
//
//            //image from amazon has the following rule: http://aaugh.com/imageabuse.html
//            //String imageURL = "http://ecw.images-amazon.com/images/P/" + ASIN + ".01." + "_PE20_SCMZZZZZZZ_.jpg";
//            String imageURL = "http://images.amazon.com/images/P/" + ASIN + ".01." + "THUMBZZZ.jpg";
//            //THUMBZZZ
//            System.out.println(imageURL);
//            oneProduct.setPictureUrl(imageURL);
//        }
//
//    }
//
//
}
