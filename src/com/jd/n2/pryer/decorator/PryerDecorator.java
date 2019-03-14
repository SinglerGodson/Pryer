package com.jd.n2.pryer.decorator;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.ide.projectView.impl.nodes.PackageElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.ui.ColoredTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

/**
 * 类注释追加 装饰器
 *
 * @author maenfang1
 * @version 1.0
 * @date 2019/3/4 22:26
 */
public class PryerDecorator implements ProjectViewNodeDecorator, VirtualFileListener, PsiTreeChangeListener {

    /** 显示注释内容的最大长度 **/
    public static final int COMMENT_MAX_LENGTH = 20;

    private PresentationData data;

     /**
      * 构造函数
      * @Author maenfang1
      * @Date 2019/3/14 21:46
      * @Param
      * @return
      */
    public PryerDecorator(){
        Project project = ProjectManager.getInstance().getDefaultProject();
        PsiManager.getInstance(project).addPsiTreeChangeListener(this);
    }

    @Override
    public void decorate(ProjectViewNode node, PresentationData data) {
        final Object value = node.getValue();
        PsiElement element = null;
        if (value instanceof PsiElement) {
            element = (PsiElement)value;
        } else if (value instanceof SmartPsiElementPointer) {
            element = ((SmartPsiElementPointer)value).getElement();
        } else if (value instanceof PackageElement) {
            PackageElement packageElement = (PackageElement)value;
            final String coverageString = packageElement.getPackage().getName();
            data.setLocationString(coverageString);
        }

        if (element instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element;
            PsiDocComment docComment = psiClass.getDocComment();
            if (docComment != null) {
                PsiDocTag tag = findTag(docComment);
                if (tag != null) {
                    PsiDocTagValue tagValue = tag.getValueElement();
                    if (tagValue != null) {
                        setLocationString(data, tagValue.getText());
                    }
                } else {
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
        return docComment.findTagByName("description");
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        System.out.println("propertyChanged: " + event.getPropertyName());
        System.out.println("propertyChanged: " + event.getFileName());
        System.out.println("propertyChanged: " + event.getOldValue());
        System.out.println("propertyChanged: " + event.getNewValue());
    }


    @Override
    public void beforeContentsChange(@NotNull VirtualFileEvent event) {
        System.out.println("beforeContentsChange: " + event.getFileName());
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        System.out.println("contentsChanged: " + event.getFileName());
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        System.out.println("fileCreated: " + event.getFileName());
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        System.out.println("fileDeleted: " + event.getFileName());
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        System.out.println("fileMoved: " + event.getFileName());
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        System.out.println("fileCopied: " + event.getFileName());

    }

    @Override
    public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {
        System.out.println("beforePropertyChange: " + event.getPropertyName());
        System.out.println("beforePropertyChange: " + event.getFileName());
        System.out.println("beforePropertyChange: " + event.getOldValue());
        System.out.println("beforePropertyChange: " + event.getNewValue());
    }

    @Override
    public void beforeFileDeletion(@NotNull VirtualFileEvent event) {
        System.out.println("beforeFileDeletion: " + event.getFileName());
    }

    @Override
    public void beforeFileMovement(@NotNull VirtualFileMoveEvent event) {
        System.out.println("beforeFileMovement: " + event.getFileName());
    }



    @Override
    public void beforeChildAddition(@NotNull PsiTreeChangeEvent event) {
        System.out.println("beforeChildAddition: " + event.getPropertyName());
        PsiElement element = event.getElement();
        System.out.println("element.getText() in beforeChildAddition: " + element.getText());
        System.out.println("element.toString() in beforeChildAddition: " + element.toString());
    }

    @Override
    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildMovement(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforePropertyChange(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {

    }
}
