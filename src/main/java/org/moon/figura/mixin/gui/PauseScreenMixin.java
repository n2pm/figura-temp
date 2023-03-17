package org.moon.figura.mixin.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.moon.figura.avatar.AvatarManager;
import org.moon.figura.config.ConfigManager;
import org.moon.figura.config.Configs;
import org.moon.figura.gui.screens.WardrobeScreen;
import org.moon.figura.gui.widgets.TexturedButton;
import org.moon.figura.utils.FiguraIdentifier;
import org.moon.figura.utils.FiguraText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public class PauseScreenMixin extends Screen {

    @Unique
    private static final ResourceLocation FIGURA_ICON = new FiguraIdentifier("textures/gui/icon.png");

    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "createPauseMenu")
    void createPauseMenu(CallbackInfo ci) {
        int x, y;

        int config = Configs.BUTTON_LOCATION.value;
        switch (config) {
            case 1 -> { //top left
                x = 5;
                y = 5;
            }
            case 2 -> {//top right
                x = this.width - 69;
                y = 5;
            }
            case 3 -> { //bottom left
                x = 5;
                y = this.height - 25;
            }
            case 4 -> { //bottom right
                x = this.width - 69;
                y = this.height - 25;
            }
            default -> { //icon
                x = this.width / 2 + 106;
                y = this.height / 4 + 80;
            }
        }

        if (config > 0) { //button
            if (ConfigManager.modmenuShift())
                y -= 12;

            addRenderableWidget(new Button(x, y, 64, 20, FiguraText.of(),
                    btn -> this.minecraft.setScreen(new WardrobeScreen(this))));
        } else { //icon
            addRenderableWidget(new TexturedButton(x, y, 20, 20, 0, 0, 20, FIGURA_ICON, 60, 20, null, btn -> this.minecraft.setScreen(new WardrobeScreen(this))) {
                @Override
                public void renderButton(PoseStack stack, int mouseX, int mouseY, float delta) {
                    renderVanillaBackground(stack, mouseX, mouseY, delta);
                    super.renderButton(stack, mouseX, mouseY, delta);
                }

                @Override
                protected int getUVStatus() {
                    int uv = super.getUVStatus();;
                    if (uv == 1 && AvatarManager.panic)
                        return 0;
                    return uv;
                }
            });
        }
    }
}
