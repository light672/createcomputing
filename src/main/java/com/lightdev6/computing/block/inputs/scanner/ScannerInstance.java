package com.lightdev6.computing.block.inputs.scanner;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

public class ScannerInstance extends ShaftInstance<ScannerBlockEntity> implements DynamicInstance {
    private final OrientedData pressHead;

    public ScannerInstance(MaterialManager materialManager, ScannerBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        pressHead = materialManager.defaultSolid()
                .material(Materials.ORIENTED)
                .getModel(AllPartialModels.MECHANICAL_PRESS_HEAD, blockState)
                .createInstance();

        Quaternion q = Vector3f.YP
                .rotationDegrees(AngleHelper.horizontalAngle(blockState.getValue(MechanicalPressBlock.HORIZONTAL_FACING)));

        pressHead.setRotation(q);

        transformModels();
    }

    @Override
    public void beginFrame() {
        transformModels();
    }

    private void transformModels() {
        float renderedHeadOffset = getRenderedHeadOffset(blockEntity);

        pressHead.setPosition(getInstancePosition())
                .nudge(0, -renderedHeadOffset, 0);
    }

    private float getRenderedHeadOffset(ScannerBlockEntity press) {
        ScannerBehaviour pressingBehaviour = press.processingBehaviour;
        return pressingBehaviour.getRenderedHeadOffset(AnimationTickHolder.getPartialTicks())
                * 19f / 16f;
    }

    @Override
    public void updateLight() {
        super.updateLight();

        relight(pos, pressHead);
    }

    @Override
    public void remove() {
        super.remove();
        pressHead.delete();
    }
}
