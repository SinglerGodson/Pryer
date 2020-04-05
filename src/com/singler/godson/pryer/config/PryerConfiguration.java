package com.singler.godson.pryer.config;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 配置Action
 *
 * @author maenfang1
 * @version 1.0
 * @date 2020/1/4 18:01
 */
public class PryerConfiguration implements SearchableConfigurable, Configurable.NoScroll {

    private ConfigPanel configPanel;

    public static final String PROPS_PREFIX = "com.singler.godson.pryer.";

    public static final Integer MAX_LENGTH_DEFAULT = 10;
    public static final String MAX_LENGTH_PROPS = PROPS_PREFIX + "maxLength";

    public static final String KEY_WORDS_DEFAULT = "";
    public static final String KEY_WORDS_PROPS = PROPS_PREFIX + "keyWords";

    public static final String TAG_NAMES_DEFAULT = "<!--,-->\n#,\\n\n";
    public static final String TAG_NAMES_PROPS = PROPS_PREFIX + "tagNames";


    @NotNull
    @Override
    public String getId() {
        return getDisplayName();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Pryer Settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        configPanel = new ConfigPanel(getMaxLength(), getKeyWords(), getTagNames());
        return configPanel;
    }

    @Override
    public boolean isModified() {
        return !configPanel.getTagNames().equals(getTagNames())
            || !configPanel.getKeyWords().equals(getKeyWords())
            || !configPanel.getMaxLength().equals(getMaxLength());
    }

    @Override
    public void apply() throws ConfigurationException {
        saveTagNames(configPanel.getTagNames());
        saveKeyWords(configPanel.getKeyWords());
        saveMaxLength(configPanel.getMaxLength());
    }

    public static String getKeyWords() {
        return PropertiesComponent.getInstance().getValue(KEY_WORDS_PROPS, KEY_WORDS_DEFAULT);
    }
    public static String getTagNames() {
        return TAG_NAMES_DEFAULT + PropertiesComponent.getInstance().getValue(TAG_NAMES_PROPS, "");
    }
    public static Integer getMaxLength() {
        return PropertiesComponent.getInstance().getInt(MAX_LENGTH_PROPS, MAX_LENGTH_DEFAULT);
    }

    private void saveKeyWords(String keyWords) {
        PropertiesComponent.getInstance().setValue(KEY_WORDS_PROPS, keyWords, KEY_WORDS_DEFAULT);
    }
    private void saveTagNames(String tagNames) {
        PropertiesComponent.getInstance().setValue(TAG_NAMES_PROPS, tagNames, TAG_NAMES_DEFAULT);
    }
    /** 保存注释长度 **/
    private void saveMaxLength(int maxLength) {
        PropertiesComponent.getInstance().setValue(MAX_LENGTH_PROPS, maxLength, MAX_LENGTH_DEFAULT);
    }

}
