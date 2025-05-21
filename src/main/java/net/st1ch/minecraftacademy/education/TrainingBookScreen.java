package net.st1ch.minecraftacademy.education;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.core.Insets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.st1ch.minecraftacademy.MinecraftAcademy;
import net.st1ch.minecraftacademy.network.packet.SelectEducationLevelPacket;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class TrainingBookScreen extends BaseOwoScreen<FlowLayout> {
    private Map<String, Map<String, Map<String, Object>>> menu;
    private TextAreaComponent contentLabel = new TextAreaComponent(Sizing.fill(80), Sizing.fill(100)) {
        @Override
        public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
            return false; // блокирует ввод с клавиатуры
        }

        @Override
        public boolean onCharTyped(char chr, int modifiers) {
            return false; // блокирует ввод символов
        }

    };
    private final Map<String, Boolean> expandedSections = new HashMap<>();
    private final Map<String, List<ButtonComponent>> sectionChildren = new HashMap<>();
    private ButtonComponent levelButton = null;

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        this.loadJson();
        rootComponent.surface(Surface.DARK_PANEL)
                .padding(Insets.of(10));

        rootComponent.horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        FlowLayout mainLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.fill(100));
        rootComponent.child(mainLayout);

        FlowLayout rightLayout = Containers.verticalFlow(Sizing.content(), Sizing.content());

        FlowLayout tocLayout = Containers.verticalFlow(Sizing.content(), Sizing.content());
        tocLayout.surface(Surface.DARK_PANEL).padding(Insets.of(5));

        for (Map.Entry<String, Map<String, Map<String, Object>>> entry : menu.entrySet()) {
            String section = entry.getKey();
            Map<String, Map<String, Object>> topics = entry.getValue();
            expandedSections.put(section, false);

            // Обработчик нажатия кнопки секции
            ButtonComponent sectionButton = Components.button(Text.literal("▶ " + section), btn -> {
                boolean isExpanded = expandedSections.get(section);
                expandedSections.put(section, !isExpanded);
                btn.setMessage(Text.literal((!isExpanded ? "▼ " : "▶ ") + section));

                List<ButtonComponent> existingChildren = sectionChildren.getOrDefault(section, new ArrayList<>());

                if (!isExpanded) {
                    // Добавляем дочерние кнопки
                    List<ButtonComponent> newButtons = new ArrayList<>();
                    for (Map.Entry<String, Map<String, Object>> topicEntry : topics.entrySet()) {
                        String topic = topicEntry.getKey();
                        Map<String, Object> topicData = (Map<String, Object>) topicEntry.getValue();
                        String content = (String) topicData.get("content");
                        Map<String, String> buttonData = (Map<String, String>) topicData.get("button");
                        ButtonComponent topicButton = Components.button(Text.literal("  " + topic), subBtn -> {
                            // заполняем контентом
                            contentLabel.text(content);

                            if (buttonData != null) {
                                if (levelButton != null) levelButton.remove();
                                levelButton = Components.button(Text.literal(buttonData.get("button_text")), levelBtn -> {
                                    SelectEducationLevelPacket payload = new SelectEducationLevelPacket(buttonData.get("level"));
                                    ClientPlayNetworking.send(payload);
                                    MinecraftClient.getInstance().setScreen(null);
                                });
                                rightLayout.child(levelButton);
                            } else {
                                if (levelButton != null) levelButton.remove();
                            }
                        });
                        newButtons.add(topicButton);
                    }

                    sectionChildren.put(section, newButtons);

                    // Скопировать текущие элементы, чтобы избежать ConcurrentModificationException
                    List<Component> currentChildren = new ArrayList<>(tocLayout.children());
                    int insertIndex = currentChildren.indexOf(btn);

                    tocLayout.clearChildren(); // удалим и пересоберём UI

                    for (int i = 0; i < currentChildren.size(); i++) {
                        Component child = currentChildren.get(i);
                        tocLayout.child(child);
                        if (i == insertIndex) {
                            newButtons.forEach(tocLayout::child);
                        }
                    }

                } else {
                    // Скрываем дочерние кнопки, пересобирая интерфейс
                    List<Component> currentChildren = new ArrayList<>(tocLayout.children());
                    List<Component> filtered = currentChildren.stream()
                            .filter(c -> !existingChildren.contains(c))
                            .toList();

                    tocLayout.clearChildren();
                    filtered.forEach(tocLayout::child);

                    sectionChildren.remove(section);
                }
            });
            tocLayout.child(sectionButton);
        }

        ScrollContainer<FlowLayout> tocScroll = Containers.verticalScroll(Sizing.content(), Sizing.fill(100), tocLayout);
        mainLayout.child(tocScroll);


        FlowLayout contentLayout = Containers.verticalFlow(Sizing.content(), Sizing.fill(80));
        contentLayout.surface(Surface.DARK_PANEL);
        contentLabel.text("Выберите тему слева");


        contentLayout.child(contentLabel);
        ScrollContainer<FlowLayout> contentScroll = Containers.verticalScroll(Sizing.fill(100), Sizing.fill(100), contentLayout);
//        contentScroll.margins(Insets.of(10));
        rightLayout.child(contentLayout);
        mainLayout.child(rightLayout);
    }

    private void loadJson() {
        try (InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(
                        MinecraftClient.getInstance()
                                .getResourceManager()
                                .getResource(Identifier.of(MinecraftAcademy.MOD_ID, "menu.json"))
                                .get().getInputStream()

                )
        )) {
            Type type = new TypeToken<LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>>() {}.getType();
            this.menu = new Gson().fromJson(reader, type);
        } catch (Exception e) {
            System.err.println("Ошибка загрузки JSON структуры обучающей книги: " + e.getMessage());
        }
    }
}
