package com.lightdev6.computing.block.computer;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FrequencyDisplaySource extends SingleLineDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.level() instanceof ServerLevel level))
            return EMPTY_LINE;
        if (!(context.getSourceBlockEntity() instanceof ComputerBlockEntity computer))
            return EMPTY_LINE;
        if (!computer.isSpeedRequirementFulfilled())
            return EMPTY_LINE;
        return Component.literal(computer.getDisplayFreq(context.sourceConfig().getInt("DisplayFrequency")));
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine)
            return;

        builder.addSelectionScrollInput(0,80, (si,l) -> {
            si.forOptions(Lang.translatedOptions("display_source.computer_display_frequency", "display_zero", "display_one", "display_two", "display_three", "display_four", "display_five"))
                    .titled(Lang.translateDirect("display_source.computer_display_frequency"));
        }, "DisplayFrequency");

    }

    @Override
    public int getPassiveRefreshTicks() {
        return 20;
    }

    @Override
    protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
        return "Instant";
    }

    @Override
    protected FlapDisplaySection createSectionForValue(DisplayLinkContext context, int size) {
        return new FlapDisplaySection(size * FlapDisplaySection.MONOSPACE, "instant", false, false);
    }
}
