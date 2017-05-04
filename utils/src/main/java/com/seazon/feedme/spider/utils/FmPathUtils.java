package com.seazon.feedme.spider.utils;

import org.htmlcleaner.TagNode;

/**
 * 类似于xpath的路径定位，基于HtmlCleaner的TagNode
 * xpath路径表达式：//*[@id="articles"][1]/ul/li[1]/div/a
 * fmpath路劲表达式：@id=articles[1]/ul/li[1]/div/a
 * 规则：
 * 1. 使用/分割每次的路径定位
 * 2. 使用[x]后缀表示取第几个节点
 * 3. 匹配定位使用@开头，后面=两边，左边为属性，右边为值
 */
public class FmPathUtils {

    public static TagNode xpathSingle(TagNode node, String xpath) {
        String[] paths = xpath.split("/");
        for (int i = 0; i < paths.length; ++i) {
            node = pathSingle(node, paths[i]);
        }
        return node;
    }

    public static TagNode[] xpathMulti(TagNode node, String xpath) {
        String[] paths = xpath.split("/");
        TagNode[] result = null;
        for (int i = 0; i < paths.length; ++i) {
            if (i == paths.length - 1) {
                result = pathMulti(node, paths[i]);
            } else {
                node = pathSingle(node, paths[i]);
            }
        }
        return result;
    }

    private static TagNode pathSingle(TagNode node, String path) {
        if (path.indexOf("[") != -1) {
            String key = path.substring(0, path.indexOf("["));
            int index = Integer.parseInt(path.substring(path.indexOf("[") + 1, path.length() - 1));
            return pathMulti(node, key)[index];
        } else {
            return pathMulti(node, path)[0];
        }
    }

    private static TagNode[] pathMulti(TagNode node, String path) {
        if (path.startsWith("@")) {
            String key = path.substring(1).split("=")[0];
            String value = path.substring(1).split("=")[1];
            return node.getElementsByAttValue(key, value, true, false);
        } else {
            return node.getElementsByName(path, true);
        }
    }

}
