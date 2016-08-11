package org.sitenv.vocabularies.validation.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by Brian on 10/20/2015.
 */
public class XpathUtils {
    public static String buildXpathFromNode(Node node) {
        return getXPath(node, "");
    }

    private static String getXPath(Node node, String xpath) {
        String elementName = "";
        if (node instanceof Element) {
            elementName = node.getNodeName();
            int prev_siblings = 1;
            Node prev_sibling = node.getPreviousSibling();
            while (null != prev_sibling) {
                if (prev_sibling.getNodeType() == node.getNodeType()) {
                    if (prev_sibling.getNodeName().equalsIgnoreCase(
                            node.getNodeName())) {
                        prev_siblings++;
                    }
                }
                prev_sibling = prev_sibling.getPreviousSibling();
            }
            elementName = elementName.concat("[" + prev_siblings + "]");
        }
        Node parent = node.getParentNode();
        if (parent == null) {
            return xpath;
        }
        return getXPath(parent, "/" + elementName + xpath);
    }
}
