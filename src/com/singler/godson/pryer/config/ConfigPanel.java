package com.singler.godson.pryer.config;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

/**
 * 配置面板
 *
 * @author maenfang1
 * @version 1.0
 * @date 2020/1/4 22:19
 */
public class ConfigPanel extends JPanel {

    private JSlider maxLengthSlider;
    private JTextArea keyWordsTextArea;
    private JTextArea tagNamesTextArea;

    public ConfigPanel (int maxLength, String keyWords, String tagNames) {
        super(new GridLayout(6, 2));
        registerMaxLength(maxLength);
        registerKeyWordsTextField(keyWords);
        registerTagNamesTextField(tagNames);
    }

    public Integer getMaxLength() {
        return maxLengthSlider.getValue();
    }

    public String getKeyWords() {
        return keyWordsTextArea.getText();
    }

    public String getTagNames() {
        return tagNamesTextArea.getText();
    }

    /** 注册 注释最大长度配置滑块 **/
    private void registerMaxLength(int value) {
        JLabel jLabel = jLabel("文件名后显示注释的最大字数", SwingConstants.LEFT);
        maxLengthSlider = maxLengthSlider(value);
        add(jLabel);
        add(maxLengthSlider);
    }

    private JSlider maxLengthSlider(int value) {
        JSlider jSlider = new JSlider();
        jSlider.setMinimum(0);
        jSlider.setMaximum(50);
        jSlider.setValue(value);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);
        jSlider.setMinorTickSpacing(5);
        jSlider.setMajorTickSpacing(10);
        return jSlider;
    }

    private void registerKeyWordsTextField(String keyWords) {
        keyWordsTextArea = textArea(keyWords);
        keyWordsTextArea.setToolTipText("请使用半角逗号 or 半角分号 or 半角空格 or 换行 进行分隔");
        JBScrollPane jbScrollPane = new JBScrollPane(keyWordsTextArea);
        jbScrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        jbScrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(jLabel("注释标签/标记关键字", SwingConstants.LEFT));
        add(jbScrollPane);
    }

    private void registerTagNamesTextField(String tagNames) {
        tagNamesTextArea = textArea(tagNames);
        tagNamesTextArea.setToolTipText("每行一对注释起止符，每对注释起止符可用半角逗号 or 半角分号 or 半角空格进行分隔");
        JBScrollPane jbScrollPane = new JBScrollPane(tagNamesTextArea);
        jbScrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        jbScrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(jLabel("注释标签/标记起止符", SwingConstants.LEFT));
        add(jbScrollPane);
    }

    private JTextArea textArea(String keyWords) {
        JTextArea textArea = new JTextArea(keyWords);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        return textArea;
    }


    private JLabel jLabel(String text, int pos) {
        JLabel label = new JLabel();
        label.setText(text);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        label.setHorizontalTextPosition(pos);
        return label;
    }

}
