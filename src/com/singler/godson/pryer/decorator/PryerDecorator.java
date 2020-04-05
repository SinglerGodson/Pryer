package com.singler.godson.pryer.decorator;

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
import com.intellij.ui.ColoredTreeCellRenderer;
import com.singler.godson.pryer.config.PryerConfiguration;

/**
 * 类注释追加 装饰器
 *
 * @author maenfang1
 * @version 1.0
 * @date 2019/3/4 22:26
 */
public class PryerDecorator implements ProjectViewNodeDecorator {

    /** 换行符 **/
    private static final String NEW_LINE = "\n";

    /** 配置项飞哥符 **/
    private static final String SPLIT_REGEX = ",|;|\n";

    /** xml文件扩展类型 **/
    private static final String XML_FILE_EXTENSION = "xml";

    /** 显示注释内容的最大长度 **/
    public static final int MAX_LENGTH = PryerConfiguration.getMaxLength();

    /** 要读取的标签的名称 **/
    private static final String[] KEY_WORDS = PryerConfiguration.getKeyWords().replaceAll(" ", "").split(SPLIT_REGEX);
    /** 要读取的标签的起止符 **/
    private static final String[] TAG_NAMES = PryerConfiguration.getTagNames().replaceAll(" ", "").split(SPLIT_REGEX);

    @Override
    public void decorate(ProjectViewNode node, PresentationData data) {
        if(MAX_LENGTH > 0) {
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
    }

     /**
      * 获取java类文件注释
      * @author maenfang1
      * @date 2019/3/15 21:31
      */
    private String getClassComment(PsiClass psiClass){
        PsiDocComment docComment = psiClass.getDocComment();
        if (docComment != null) {
            PsiDocTag tag = findTag(docComment);
            if (tag != null) { // 读取tag标签的注释内容
                return trim(tag.getText(), "@" + tag.getName()).split(NEW_LINE)[0];
            } else { // 如果没有找到tag，则认为没有按照javadoc标准填写注释，配置的是注释前缀。
                String comment = null;
                PsiElement[] elements = docComment.getDescriptionElements();
                for(PsiElement e : elements){
                    comment = trim(e.getText(), NEW_LINE);
                    if(StringUtil.isNotEmpty(comment)){
                        comment = findCommentByKeyWords(comment);
                        if(StringUtil.isNotEmpty(comment)) {
                            return comment;
                        }
                    }
                }
                if (StringUtil.isEmpty(comment)) { // 如果还是没有找到对应的注释，则以第一条非空注释作为注释。
                    for(PsiElement e : elements){
                        comment = trim(e.getText(), NEW_LINE);
                        if(!StringUtil.isEmpty(comment)){
                            return comment;
                        }
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
        for(String tagName : KEY_WORDS){
            PsiDocTag tag = docComment.findTagByName(tagName);
            if(tag != null && tag.getName().equals(tagName)){
                return tag;
            }
        }
        return null;
    }

    /**
     * 方法描述: 根据注释前缀找到注释内容。
     */
    private String findCommentByKeyWords(String comment){
        for(String keyWords : KEY_WORDS){
            if (comment.startsWith(keyWords)) {
               return trim(comment, keyWords);
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
            if (XML_FILE_EXTENSION.equals(psiFile.getVirtualFile().getFileType().getName().toLowerCase())) {
                lineNum = 1;
            }
            String comment = "";
            String[] lines = psiFile.getText().trim().split(NEW_LINE);
            // 获取第一行 包含注释起始符的内容
            // while (lineNum < lines.length && !startsWithTagNames(comment)) {
            //     comment = lines[lineNum++].trim();
            // }
            while (lineNum < lines.length && StringUtil.isEmpty(comment)) {
                comment = lines[lineNum++].trim();
            }

            // if (StringUtil.isNotEmpty(comment)) { // 若该行为注释内容
            //     boolean endsWithTagName = endsWithTagNames(comment);
            //     comment = trim(comment, TAG_NAMES).trim();
            //     // 注释内容与注释标签在一行内。
            //     if(!endsWithTagName && StringUtil.isEmpty(comment)){
            //         while (lineNum < lines.length
            //             && StringUtil.isEmpty(comment)) {
            //             comment = lines[lineNum++].trim();
            //         }
            //         comment = trim(comment, TAG_NAMES).trim();
            //     }
            //     return comment;
            // }
            if (startsWithTagNames(comment)) { // 若该行为注释内容
                boolean endsWithTagName = endsWithTagNames(comment);
                comment = trim(comment, TAG_NAMES).trim();
                // 注释内容与注释标签不在一行内。
                if(!endsWithTagName && StringUtil.isEmpty(comment)){
                    while (lineNum < lines.length
                        && StringUtil.isEmpty(comment)) {
                        comment = lines[lineNum++].trim();
                    }
                    comment = trim(comment, TAG_NAMES).trim();
                }
                return comment;
            }
        }
        return null;
    }


    private boolean startsWithTagNames(String comment) {
        if(StringUtil.isNotEmpty(comment)) {
            // TAG_NAMES 初始化时，去掉了所有空格，所以偶数位置的都是开始标签。
            for (int i = 0; i < TAG_NAMES.length; i += 2) {
                String tagName = TAG_NAMES[i];
                if (comment.indexOf(tagName) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean endsWithTagNames(String comment) {
        if(StringUtil.isNotEmpty(comment)) {
            for (int i = 1; i < TAG_NAMES.length; i += 2) {
                String tagName = TAG_NAMES[i];
                // TAG_NAMES 初始化时，去掉了所有空格，所以奇数位置的都是结束标签。
                if (comment.indexOf(tagName) == comment.length() - tagName.length()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 在文件后追加注释
     * @param data
     * @param comment
     */
    private void setLocationString(PresentationData data, String comment){
        if (StringUtil.isNotEmpty(comment)) {
            comment = comment.trim();
            if(comment.length() > MAX_LENGTH) {
                comment = comment.substring(0, MAX_LENGTH) + "...";
            }
            if(!comment.equals(data.getLocationString())) {
                data.setLocationString(comment);
            }
        }
    }

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer renderer) {
    }

    private String trim(String str, String... replaces) {
        for(String replace : replaces) {
            str = str.replace(replace, "");
        }
        return str.trim();
    }
}
