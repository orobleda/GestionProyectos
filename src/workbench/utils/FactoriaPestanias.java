package workbench.utils;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import ui.GestionBotones;

public class FactoriaPestanias extends SkinBase<Pestania> {

  private HBox controlBox;
  private StackPane closeIconShape;
  private Button closeBtn;

  private Label nameLbl;

  private final ReadOnlyStringProperty name;
  private final ReadOnlyObjectProperty<Node> icon;

  public FactoriaPestanias(Pestania tab) {
    super(tab);
    name = tab.nameProperty();
    icon = tab.iconProperty();

    initializeParts();
    layoutParts();
    setupBindings();
    setupEventHandlers();
    setupValueChangedListeners();

    updateIcon();

    getChildren().add(controlBox);
  }

  private void initializeParts() {
    closeIconShape = new StackPane();
    closeIconShape.getStyleClass().add("shape");
    closeBtn = new Button("", closeIconShape);
    closeBtn.getStyleClass().addAll("icon", "close-icon");

    nameLbl = new Label();
    nameLbl.getStyleClass().add("tab-name-lbl");

    controlBox = new HBox();
    controlBox.getStyleClass().add("tab-box");

  }

  private void layoutParts() {
    Label iconPlaceholder = new Label(); // Will be replaced in the listener
    controlBox.getChildren().addAll(iconPlaceholder, nameLbl, closeBtn);
  }

  private void setupBindings() {
    nameLbl.textProperty().bind(name);
  }

  private void setupEventHandlers() {
    closeBtn.setOnAction(e -> getSkinnable().close());
  }

  private void setupValueChangedListeners() {
    // handle icon changes
    icon.addListener((observable, oldIcon, newIcon) -> {
      if (oldIcon != newIcon) {
        updateIcon();
      }
    });
  }

  /**
   * Replaces the Icon when calling setModule().
   */
  private void updateIcon() {
    Node iconNode = icon.get();
    ((ImageView) iconNode).setFitWidth(30);
    ((ImageView) iconNode).setFitHeight(30);
    ObservableList<Node> children = controlBox.getChildren();
    children.remove(0);
    children.add(0, iconNode);
    iconNode.getStyleClass().add("tab-icon");
  }
}
