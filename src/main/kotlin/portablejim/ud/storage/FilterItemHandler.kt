package portablejim.ud.storage

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.items.IItemHandler
import portablejim.ud.UselessDeleterMod
import java.util.*

/**
 * Created by james on 26/06/16.
 */
class FilterItemHandler(val currentItem: ItemStack): IItemHandler {

    var filterList: MutableList<ItemStack?> = ArrayList(9)

    init {
        val filterNbt = currentItem.tagCompound ?: NBTTagCompound()

        for(i in 0..9) {
            filterList.add(null)
        }

        loadFromNBT(filterNbt)
    }

    private fun loadFromNBT(tagCompound: NBTTagCompound) {
        val NBT_TAGLIST = 10;
        val tagList = tagCompound.getTagList("UD_Filter", NBT_TAGLIST);

        for(i in 0..tagList.tagCount()) {
            val itemTag = tagList.getCompoundTagAt(i);
            val slot = if (itemTag.hasKey("UD_FilterItem")) itemTag.getInteger("UD_FilterItem") else -1;

            if(slot >= 0 && slot < 9 && itemTag != null) {
                filterList[slot] = ItemStack.loadItemStackFromNBT(itemTag)
            }
        }

    }

    override fun getStackInSlot(slot: Int): ItemStack? {
        return if (slot < filterList.size) filterList[slot]?.copy() else null
    }

    override fun insertItem(slot: Int, stack: ItemStack?, simulate: Boolean): ItemStack? {
        if(!simulate && stack != null && slot < filterList.size) {
            val ourStack = stack.copy()
            ourStack.stackSize = 1
            filterList[slot] = ourStack
        }
        return stack
    }

    override fun getSlots(): Int {
        return 9
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
        if(!simulate) {
            filterList.set(slot, null)
        }
        return null
    }

    fun saveSlots() {
        val tagList = NBTTagList()
        for(slot in 0..(getSlots()-1)) {
            val stack = filterList[slot]
            if(stack != null) {
                val itemTag = NBTTagCompound()
                itemTag.setInteger("UD_FilterItem", slot)
                UselessDeleterMod.log.info("Saving ${stack.toString()}")
                stack.writeToNBT(itemTag)
                tagList.appendTag(itemTag)
            }
        }

        if(currentItem.tagCompound == null) {
            currentItem.tagCompound = NBTTagCompound()
        }

        currentItem.tagCompound?.setTag("UD_Filter", tagList)
    }
}