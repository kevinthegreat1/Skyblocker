package de.hysky.skyblocker.utils.render.gui;

import de.hysky.skyblocker.SkyblockerMod;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Abstract class for gui solvers. Extend this class to add a new gui solver, like terminal solvers or experiment solvers.
 */
public abstract class ContainerSolver extends AbstractContainerMatcher {
    /**
     * @deprecated Only for {@link ContainerSolverManager}. Use {@link #ContainerSolver(String)} instead.
     */
    @Deprecated
    protected ContainerSolver() {
        super();
    }

    protected ContainerSolver(@NotNull String titlePattern) {
        super(titlePattern);
    }

    protected abstract boolean isEnabled();

    public final Pattern getName() {
        return titlePattern;
    }

    protected void start(GenericContainerScreen screen) {
    }

    protected void reset() {
    }

    protected void markHighlightsDirty() {
        SkyblockerMod.getInstance().containerSolverManager.markDirty();
    }

    protected boolean onClickSlot(int slot, ItemStack stack, int screenId, String[] groups) {
        return false;
    }

    protected abstract List<ColorHighlight> getColors(String[] groups, Int2ObjectMap<ItemStack> slots);

    protected final void trimEdges(Int2ObjectMap<ItemStack> slots, int rows) {
        for (int i = 0; i < rows; i++) {
            slots.remove(9 * i);
            slots.remove(9 * i + 8);
        }
        for (int i = 1; i < 8; i++) {
            slots.remove(i);
            slots.remove((rows - 1) * 9 + i);
        }
    }
}
