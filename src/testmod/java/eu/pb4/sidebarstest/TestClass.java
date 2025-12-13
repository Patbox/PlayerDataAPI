package eu.pb4.sidebarstest;


import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class TestClass {
    public String testString = "";
    public Vec3 position = Vec3.ZERO;
    public ItemStack itemStack = ItemStack.EMPTY;
    public Item item = Items.AIR;
    public Component text = Component.empty();
    public Identifier id = Identifier.tryParse("default");
}
