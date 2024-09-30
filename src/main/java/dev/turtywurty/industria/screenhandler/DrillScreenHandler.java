package dev.turtywurty.industria.screenhandler;

import dev.turtywurty.industria.blockentity.DrillBlockEntity;
import dev.turtywurty.industria.init.BlockInit;
import dev.turtywurty.industria.init.ScreenHandlerTypeInit;
import dev.turtywurty.industria.network.BlockPosPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class DrillScreenHandler extends ScreenHandler {
    private final DrillBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    public DrillScreenHandler(int syncId, PlayerInventory playerInv, BlockPosPayload payload) {
        this(syncId, playerInv, (DrillBlockEntity) playerInv.player.getWorld().getBlockEntity(payload.pos()));
    }

    public DrillScreenHandler(int syncId, PlayerInventory playerInv, DrillBlockEntity blockEntity) {
        super(ScreenHandlerTypeInit.DRILL, syncId);

        this.blockEntity = blockEntity;
        this.context = ScreenHandlerContext.create(blockEntity.getWorld(), blockEntity.getPos());

        SimpleInventory inventory = blockEntity.getEntityInventory();
        checkSize(inventory, 1);
        inventory.onOpen(playerInv.player);

        addPlayerInventory(playerInv);
        addPlayerHotbar(playerInv);
        addEntityInventory(inventory);
    }

    private void addPlayerInventory(PlayerInventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory inventory) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
    }

    private void addEntityInventory(SimpleInventory inventory) {
        addSlot(new Slot(inventory, 0, 80, 35) {
            @Override
            public boolean isEnabled() {
                return DrillScreenHandler.this.blockEntity.isDrilling();
            }
        });
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = this.slots.get(slot);

        if (slotObject.hasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();

            if (slot < 1) {
                if (!insertItem(stackInSlot, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!insertItem(stackInSlot, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slotObject.setStack(ItemStack.EMPTY);
            } else {
                slotObject.markDirty();
            }
        }

        return stack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, BlockInit.DRILL);
    }
}