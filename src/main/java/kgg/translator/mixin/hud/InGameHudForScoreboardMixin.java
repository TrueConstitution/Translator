package kgg.translator.mixin.hud;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import kgg.translator.option.ScreenOption;
import kgg.translator.handler.TranslateHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(InGameHud.class)
public abstract class InGameHudForScoreboardMixin {
    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow @Final private MinecraftClient client;

    @Shadow private int scaledHeight;

    @Shadow private int scaledWidth;

    /**
     * @author KGG_Xing_Kong
     * @reason 翻译计分板
     * AI重写的
     */
    @Overwrite
    private void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective) {
        // 获取目标得分板和所有玩家的分数
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardPlayerScore> allScores = scoreboard.getAllPlayerScores(objective);

        // 过滤出玩家名称不为空且不以 "#" 开头的分数
        List<ScoreboardPlayerScore> filteredScores = allScores.stream()
                .filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#"))
                .collect(Collectors.toList());

        // 如果列表大小大于 15，获取从倒数第 15 个开始的分数
        Collection<ScoreboardPlayerScore> scoresToRender;
        if (filteredScores.size() > 15) {
            scoresToRender = Lists.newArrayList(Iterables.skip(filteredScores, filteredScores.size() - 15));
        } else {
            scoresToRender = filteredScores;
        }

        // 创建用于存储玩家和分数的对
        List<Pair<ScoreboardPlayerScore, Text>> scoreTextList = Lists.newArrayListWithCapacity(scoresToRender.size());

        // 获取目标的显示名称
        Text objectiveName = objective.getDisplayName();

        // 翻译
        if (ScreenOption.autoScoreboard.isEnable()) {
            objectiveName = TranslateHelper.translateNoWait(objectiveName);
        }
        int objectiveNameWidth = this.getTextRenderer().getWidth(objectiveName);
        int maxWidth = objectiveNameWidth;

        // ":" 的宽度
        int colonWidth = this.getTextRenderer().getWidth(": ");

        // 为每个玩家的分数计算最大宽度
        for (ScoreboardPlayerScore score : scoresToRender) {
            Team playerTeam = scoreboard.getPlayerTeam(score.getPlayerName());
            Text playerNameText = Team.decorateName(playerTeam, Text.literal(score.getPlayerName()));

            // 翻译
            if (ScreenOption.autoScoreboard.isEnable()) {
                playerNameText = TranslateHelper.translateNoWait(playerNameText);
            }

            scoreTextList.add(Pair.of(score, playerNameText));

            maxWidth = Math.max(maxWidth, this.getTextRenderer().getWidth(playerNameText) + colonWidth + this.getTextRenderer().getWidth(Integer.toString(score.getScore())));
        }

        // 计算得分板的起始位置和高度
        int totalHeight = scoresToRender.size() * 9;
        int startY = this.scaledHeight / 2 + totalHeight / 3;
        int startX = this.scaledWidth - maxWidth - 3;

        // 背景色
        int backgroundColor1 = this.client.options.getTextBackgroundColor(0.3F);
        int backgroundColor2 = this.client.options.getTextBackgroundColor(0.4F);

        // 渲染分数板
        int currentIndex = 0;
        for (Pair<ScoreboardPlayerScore, Text> pair : scoreTextList) {
            currentIndex++;

            ScoreboardPlayerScore score = pair.getFirst();
            Text playerNameText = pair.getSecond();

            // 翻译
            if (ScreenOption.autoScoreboard.isEnable()) {
                playerNameText = TranslateHelper.translateNoWait(playerNameText);
            }

            // 使用红色格式化分数
            Formatting scoreColor = Formatting.RED;
            String scoreString = "" + scoreColor + score.getScore();

            int posX = startX;
            int posY = startY - currentIndex * 9;

            // 绘制背景
            context.fill(posX - 2, posY, startX + maxWidth - 2, posY + 9, backgroundColor1);

            // 绘制玩家名字
            context.drawText(this.getTextRenderer(), playerNameText, posX, posY, -1, false);

            // 绘制分数
            context.drawText(this.getTextRenderer(), scoreString, startX + maxWidth - 2 - this.getTextRenderer().getWidth(scoreString), posY, -1, false);

            // 如果是最后一行，绘制顶部背景
            if (currentIndex == scoreTextList.size()) {
                context.fill(posX - 2, posY - 9 - 1, startX + maxWidth - 2, posY - 1, backgroundColor2);
                context.fill(posX - 2, posY - 1, startX + maxWidth - 2, posY, backgroundColor1);
                context.drawText(this.getTextRenderer(), objectiveName, posX + maxWidth / 2 - objectiveNameWidth / 2, posY - 9, -1, false);
            }
        }
    }
}
