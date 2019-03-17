package com.jd.n2.pryer.decorator;

/**
 * 注释标签枚举类
 * @author maenfang1
 * @date 2019/3/17 16:53
 * @version 1.0
 */
public enum CommentTagEnum {
    /** xml与html中注释 **/
    XmlHtml("<!--", "-->"),
    /** properties文件注释 **/
    Properties("#", "\n"); // 单行注释，以换行符作为注释结束标签。

    /** 开始标签 **/
    private String start;
    /** 结束标签 **/
    private String end;

     /**
      * @author maenfang1
      * @date 2019/3/17 16:57
      */
    CommentTagEnum(String s, String e){
        this.start = s;
        this.end = e;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
}
