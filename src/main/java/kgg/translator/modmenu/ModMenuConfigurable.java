package kgg.translator.modmenu;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;

public interface ModMenuConfigurable {
    /**
     * 在配置界面注册配置项
     * @return 关闭界面时操作
     */
    Runnable registerEntry(ConfigEntryBuilder entryBuilder, SubCategoryBuilder category);
}
