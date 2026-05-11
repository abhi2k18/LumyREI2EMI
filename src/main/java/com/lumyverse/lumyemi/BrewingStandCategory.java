package com.lumyverse.lumyemi;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BrewingStandCategory implements DisplayCategory<BrewingStandDisplay> {

    public static final CategoryIdentifier<BrewingStandDisplay> BREWING =
            CategoryIdentifier.of("cobblemon", "brewing_stand");

    private static final Identifier BACKGROUND_TEXTURE = Identifier.of("lumyrei", "textures/gui/rei/brewing_stand.png");

    private static final Identifier PROGRESS_ARROW_TEXTURE = Identifier.of("lumyrei", "textures/gui/rei/progress_arrow.png");

    private static final int WIDTH = 146;
    private static final int HEIGHT = 68;

    private static final int INPUT_INGREDIENT_X = 65;
    private static final int INPUT_BLAZE_X = 3;
    private static final int INPUT_Y = 4;

    private static final int[] BOTTLE_X_OFFSETS = { 42, 65, 88 };
    private static final int[] BOTTLE_Y_OFFSETS = { 38, 45, 38 };

    private static final int BREWING_BUBBLE_X = 47;
    private static final int BREWING_BUBBLE_Y = 1;
    private static final int BREWING_BUBBLE_UV_U = 183;
    private static final int BREWING_BUBBLE_UV_V = 0;
    private static final int BREWING_BUBBLE_WIDTH = 12;
    private static final int BREWING_BUBBLE_HEIGHT = 29;

    private static final int ARROW_DOWN_X = 83;
    private static final int ARROW_DOWN_Y = 3;
    private static final int ARROW_DOWN_UV_U = 176;
    private static final int ARROW_DOWN_UV_V = 0;
    private static final int ARROW_DOWN_WIDTH = 9;
    private static final int ARROW_DOWN_HEIGHT = 29;

    private static final int ARROW_OUTPUT_X = 87;
    private static final int ARROW_OUTPUT_Y = 22;
    private static final int ARROW_OUTPUT_WIDTH = 36;
    private static final int ARROW_OUTPUT_HEIGHT = 12;
    private static final int ARROW_OUTPUT_UV_U = 0;
    private static final int ARROW_OUTPUT_UV_V = 0;

    private static final int OUTPUT_SLOT_X = 128;
    private static final int OUTPUT_SLOT_Y = 21;

    private static final int OUTPUT_COUNT_LABEL_X = OUTPUT_SLOT_X + 17;
    private static final int OUTPUT_COUNT_LABEL_Y = OUTPUT_SLOT_Y + 9;

    private static final double ARROW_DURATION_TICKS = 400.0;
    private static final double ARROW_DURATION_MS = ARROW_DURATION_TICKS * 50.0; // 20000 ms

    private static final double BUBBLE_DURATION_TICKS = 20.0;
    private static final double BUBBLE_DURATION_MS = BUBBLE_DURATION_TICKS * 50.0;

    private static final double VERTICAL_FILL_DURATION_MS = 10000.0;

    // Overlap: quanto prima la freccia orizzontale deve iniziare (1 secondo)
    private static final double OVERLAP_MS = 1000.0;

    // Tempo di partenza della freccia orizzontale (10s - 1s = 9s)
    private static final double HORIZONTAL_START_TIME_MS = VERTICAL_FILL_DURATION_MS - OVERLAP_MS; // 9000 ms

    // Tempo totale di riempimento della freccia orizzontale (20s - 9s = 11s)
    private static final double HORIZONTAL_FILL_DURATION_MS = ARROW_DURATION_MS - HORIZONTAL_START_TIME_MS; // 11000 ms

    @Override
    public CategoryIdentifier<? extends BrewingStandDisplay> getCategoryIdentifier() {
        return BREWING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.brewing");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(new ItemStack(Items.BREWING_STAND));
    }

    @Override
    public int getDisplayWidth(BrewingStandDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(BrewingStandDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - WIDTH / 2, bounds.getCenterY() - HEIGHT / 2);
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        // Disegna Sfondo
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) ->
                graphics.drawTexture(BACKGROUND_TEXTURE, startPoint.x, startPoint.y, 14, 13, WIDTH, HEIGHT, 256, 256)));

        // Slot Ingrediente Principale
        widgets.add(Widgets.createSlot(new Point(startPoint.x + INPUT_INGREDIENT_X, startPoint.y + INPUT_Y))
                .entries(display.getInputEntries().get(0))
                .markInput()
        );

        // Slot Combustibile (Blaze Powder)
        widgets.add(Widgets.createSlot(new Point(startPoint.x + INPUT_BLAZE_X, startPoint.y + INPUT_Y))
                .entries(display.getInputEntries().get(1))
                .markInput()
        );

        // Slot Pozioni
        for (int i = 0; i < 3; i++) {
            Point bottlePos = new Point(startPoint.x + BOTTLE_X_OFFSETS[i], startPoint.y + BOTTLE_Y_OFFSETS[i]);

            widgets.add(Widgets.createSlot(bottlePos)
                    .entries(display.getInputEntries().get(2))
                    .markInput()
            );
        }

        // Animazione Freccia e Bolle
        widgets.add(createBrewingAnimation(startPoint));

        // Slot Output
        widgets.add(Widgets.createSlot(new Point(startPoint.x + OUTPUT_SLOT_X, startPoint.y + OUTPUT_SLOT_Y))
                .entries(display.getOutputEntries().getFirst())
                .markOutput()
        );

        // Quantità Output
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            int x = startPoint.x + OUTPUT_COUNT_LABEL_X;
            int y = startPoint.y + OUTPUT_COUNT_LABEL_Y;

            Text text = Text.literal("3");

            int width = textRenderer.getWidth(text);

            graphics.getMatrices().push();
            graphics.getMatrices().translate(0.0F, 0.0F, 200.0F);
            graphics.drawTextWithShadow(textRenderer, text, x - width, y, 0xFFFFFF);
            graphics.getMatrices().pop();
        }));

        return widgets;
    }

    private Widget createBrewingAnimation(Point startPoint) {

        return Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {

            long currentTime = System.currentTimeMillis();

            double timeInCycle = (currentTime % ARROW_DURATION_MS); // Tempo corrente all'interno del ciclo di 20 secondi

            double verticalProgress;
            double horizontalProgress;

            // Logica Progresso Verticale
            verticalProgress = Math.min(1.0, timeInCycle / VERTICAL_FILL_DURATION_MS);

            // Logica Progresso Orizzontale
            double timeSinceHorizontalStart = Math.max(0.0, timeInCycle - HORIZONTAL_START_TIME_MS);

            horizontalProgress = Math.min(1.0, timeSinceHorizontalStart / HORIZONTAL_FILL_DURATION_MS);

            { // Freccia Verticale
                int arrowX = startPoint.x + ARROW_DOWN_X;
                int arrowY = startPoint.y + ARROW_DOWN_Y;

                int filledHeight = (int) Math.round(ARROW_DOWN_HEIGHT * verticalProgress);

                graphics.drawTexture(
                        BACKGROUND_TEXTURE,
                        arrowX, arrowY,
                        ARROW_DOWN_UV_U, ARROW_DOWN_UV_V,
                        ARROW_DOWN_WIDTH, filledHeight,
                        256, 256
                );
            }

            { // Freccia Orizzontale
                int arrowX = startPoint.x + ARROW_OUTPUT_X;
                int arrowY = startPoint.y + ARROW_OUTPUT_Y;

                int filledWidth = (int) Math.round(ARROW_OUTPUT_WIDTH * horizontalProgress);

                if (filledWidth > 0) {
                    graphics.drawTexture(
                            PROGRESS_ARROW_TEXTURE,
                            arrowX, arrowY,
                            ARROW_OUTPUT_UV_U, ARROW_OUTPUT_UV_V,
                            filledWidth, ARROW_OUTPUT_HEIGHT,
                            ARROW_OUTPUT_WIDTH, ARROW_OUTPUT_HEIGHT
                    );
                }
            }

            { // Animazione Bolle
                double bubbleProgress = (currentTime % BUBBLE_DURATION_MS) / BUBBLE_DURATION_MS;

                int x = startPoint.x + BREWING_BUBBLE_X;
                int y = startPoint.y + BREWING_BUBBLE_Y;
                int totalProgressHeight = BREWING_BUBBLE_HEIGHT;

                int sliceHeight = (int) Math.round(totalProgressHeight * bubbleProgress);
                int vOffset = totalProgressHeight - sliceHeight;

                graphics.drawTexture(
                        BACKGROUND_TEXTURE,
                        x, y + vOffset,
                        BREWING_BUBBLE_UV_U, BREWING_BUBBLE_UV_V + vOffset,
                        BREWING_BUBBLE_WIDTH, sliceHeight,
                        256, 256
                );
            }
        });
    }
}