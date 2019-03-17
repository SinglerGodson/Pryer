package com.jd.n2.pryer.decorator;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.ui.ColoredTreeCellRenderer;
import org.apache.commons.lang3.StringUtils;

/**
 * 类注释追加 装饰器
 *
 * @author maenfang1
 * @version 1.0
 * @date 2019/3/4 22:26
 */
public class PryerDecorator implements ProjectViewNodeDecorator {

    /** 要读取的标签的名称 **/
    private String[] tagNames;
    /** 显示注释内容的最大长度 **/
    public static final int COMMENT_MAX_LENGTH = 20;
    /** 要显示的标签名称 **/
    public static final String TAG_NAMES = "description";
    /** xml文件的扩展名 **/
    public static final String FILE_EXTENSION_XML = "xml";


    @Override
    public void decorate(ProjectViewNode node, PresentationData data) {
        String comment = null;
        Object value = node.getValue();
        if (value instanceof PsiClass) { // 若是javaClass文件
            PsiClass psiClass = (PsiClass) value;
            comment = getClassComment(psiClass);
        } else if(value instanceof PsiFile) { // 若是其他类型文件
            PsiFile psiFile = (PsiFile) value;
            comment = getFileComment(psiFile);
        }
        setLocationString(data, comment);
    }


     /**
      * 获取java类文件注释
      * @author maenfang1
      * @date 2019/3/15 21:31
      * @param
      * @return
      */
    private String getClassComment(PsiClass psiClass){
        PsiDocComment docComment = psiClass.getDocComment();
        if (docComment != null) {
            PsiDocTag tag = findTag(docComment);
            if (tag != null) { // 读取tag标签的注释内容
                PsiDocTagValue tagValue = tag.getValueElement();
                if (tagValue != null) {
                    return tagValue.getText();
                }
            } else { // 如果没有找到tag，直接读取第一行注释内容
                PsiElement[] elements = docComment.getDescriptionElements();
                for(PsiElement e : elements){
                    String comment = e.getText().trim().replace("\n", "");
                    if(!StringUtil.isEmpty(comment)){
                        return comment;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 找到可用的tag
     * @param docComment
     * @return
     */
    private PsiDocTag findTag(PsiDocComment docComment){
        if(tagNames == null){
            tagNames = TAG_NAMES.split(",");
        }
        for(String tagName : tagNames){
            PsiDocTag tag = docComment.findTagByName(tagName);
            if(tag != null){
                return tag;
            }
        }
        return null;
    }


    /**
     * 获取其他类型文件的注释内容
     * 目前只支持xml文件 与properties文件。
     * @author maenfang1
     * @date 2019/3/15 22:59
     */
    private String getFileComment(PsiFile psiFile){
        if(StringUtil.isNotEmpty(psiFile.getText())){
            int lineNum = 0;
            String comment = "";
            String[] lines = psiFile.getText().trim().split("\n");
            String fileType = psiFile.getVirtualFile().getExtension();
            CommentTagEnum commentTagEnum = CommentTagEnum.Properties;
            // 如果是xml文件，则注释要从第二行开始，即下标为1。
            if(FILE_EXTENSION_XML.equals(fileType)){
                lineNum = 1;
                commentTagEnum = CommentTagEnum.XmlHtml;
            }
            // 获取第一行不为空的内容
            while (StringUtil.isEmpty(comment)) {
                comment = lines[lineNum++].trim();
            }
            if (comment.startsWith(commentTagEnum.getStart())) { // 若该行为注释内容
                comment = comment
                        .replace(commentTagEnum.getEnd(), "")
                        .replace(commentTagEnum.getStart(), "")
                        .trim();
                // 注释内容在一行之内。
                if(StringUtil.isNotEmpty(comment)){
                    return comment;
                } else { // 注释有多行
                    // 获取第一行不为空的内容
                    do {
                        comment = lines[lineNum++].trim();
                    } while (StringUtil.isEmpty(comment));
                    return comment;
                }
            }
        }
        return null;
    }

    /**
     * 在文件后追加注释
     * @param data
     * @param comment
     */
    private void setLocationString(PresentationData data, String comment){
        if(!StringUtil.isEmpty(comment)){
            if(comment.length() > COMMENT_MAX_LENGTH) {
                comment = comment.substring(0, COMMENT_MAX_LENGTH) + " ...";
            }
            data.setLocationString(comment);
        }
    }

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer renderer) {
    }

}
