package com.jd.n2.pryer.decorator;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.ui.ColoredTreeCellRenderer;

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

    @Override
    public void decorate(ProjectViewNode node, PresentationData data) {
        Object value = node.getValue();
        if (value instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) value;
            PsiDocComment docComment = psiClass.getDocComment();
            if (docComment != null) {
                PsiDocTag tag = findTag(docComment);
                if (tag != null) { // 读取tag标签的注释内容
                    PsiDocTagValue tagValue = tag.getValueElement();
                    if (tagValue != null) {
                        setLocationString(data, tagValue.getText());
                    }
                } else { // 如果没有找到tag，直接读取第一行注释内容
                    PsiElement[] elements = docComment.getDescriptionElements();
                    for(PsiElement e : elements){
                        String comment = e.getText().trim().replace("\n", "");
                        if(!StringUtil.isEmpty(comment)){
                            setLocationString(data, e.getText());
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer renderer) {
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
}
