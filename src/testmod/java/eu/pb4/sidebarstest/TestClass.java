package eu.pb4.sidebarstest;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class TestClass {
    public String testString = "";
    public Vec3d position = Vec3d.ZERO;
    public ItemStack itemStack = ItemStack.EMPTY;
    public Item item = Items.AIR;
    public Text text = Text.empty();
    public Identifier id = new Identifier("default");
}
